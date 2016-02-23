/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2009-2010 Sun Microsystems, Inc.
 * Portions Copyright 2014-2015 ForgeRock AS.
 */
package org.opends.server.tools.tasks;

import static org.forgerock.opendj.ldap.ResultCode.*;
import static org.opends.messages.ToolMessages.*;
import static org.opends.server.config.ConfigConstants.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.DecodeException;
import org.forgerock.opendj.ldap.DereferenceAliasesPolicy;
import org.forgerock.opendj.ldap.ModificationType;
import org.forgerock.opendj.ldap.SearchScope;
import org.opends.server.backends.task.FailedDependencyAction;
import org.opends.server.backends.task.TaskState;
import org.opends.server.config.ConfigConstants;
import org.opends.server.protocols.ldap.AddRequestProtocolOp;
import org.opends.server.protocols.ldap.AddResponseProtocolOp;
import org.opends.server.protocols.ldap.DeleteRequestProtocolOp;
import org.opends.server.protocols.ldap.DeleteResponseProtocolOp;
import org.opends.server.protocols.ldap.LDAPAttribute;
import org.opends.server.protocols.ldap.LDAPConstants;
import org.opends.server.protocols.ldap.LDAPFilter;
import org.opends.server.protocols.ldap.LDAPMessage;
import org.opends.server.protocols.ldap.LDAPModification;
import org.opends.server.protocols.ldap.LDAPResultCode;
import org.opends.server.protocols.ldap.ModifyRequestProtocolOp;
import org.opends.server.protocols.ldap.ModifyResponseProtocolOp;
import org.opends.server.protocols.ldap.SearchRequestProtocolOp;
import org.opends.server.protocols.ldap.SearchResultEntryProtocolOp;
import org.opends.server.tools.LDAPConnection;
import org.opends.server.tools.LDAPReader;
import org.opends.server.tools.LDAPWriter;
import org.opends.server.types.Control;
import org.opends.server.types.Entry;
import org.opends.server.types.LDAPException;
import org.opends.server.types.RawAttribute;
import org.opends.server.types.RawModification;
import org.opends.server.types.SearchResultEntry;
import org.opends.server.util.StaticUtils;

/**
 * Helper class for interacting with the task backend on behalf of utilities
 * that are capable of being scheduled.
 */
public class TaskClient {

  /**
   * Connection through which task scheduling will take place.
   */
  protected LDAPConnection connection;

  /**
   * Keeps track of message IDs.
   */
  private final AtomicInteger nextMessageID = new AtomicInteger(0);

  /**
   * Creates a new TaskClient for interacting with the task backend remotely.
   * @param conn for accessing the task backend
   */
  public TaskClient(LDAPConnection conn) {
    this.connection = conn;
  }

  /**
   * Returns the ID of the task entry for a given list of task attributes.
   * @param taskAttributes the task attributes.
   * @return the ID of the task entry for a given list of task attributes.
   */
  public static String getTaskID(List<RawAttribute> taskAttributes)
  {
    String taskID = null;

    RawAttribute recurringIDAttr = getAttribute(ATTR_RECURRING_TASK_ID,
        taskAttributes);

    if (recurringIDAttr != null) {
      taskID = recurringIDAttr.getValues().get(0).toString();
    } else {
      RawAttribute taskIDAttr = getAttribute(ATTR_TASK_ID,
          taskAttributes);
      taskID = taskIDAttr.getValues().get(0).toString();
    }

    return taskID;
  }

  private static RawAttribute getAttribute(String attrName,
      List<RawAttribute> taskAttributes)
  {
    for (RawAttribute attr : taskAttributes)
    {
      if (attr.getAttributeType().equalsIgnoreCase(attrName))
      {
        return attr;
      }
    }
    return null;
  }

  /**
   * Returns the DN of the task entry for a given list of task attributes.
   * @param taskAttributes the task attributes.
   * @return the DN of the task entry for a given list of task attributes.
   */
  public static String getTaskDN(List<RawAttribute> taskAttributes)
  {
    String entryDN = null;
    String taskID = getTaskID(taskAttributes);
    RawAttribute recurringIDAttr = getAttribute(ATTR_RECURRING_TASK_ID,
        taskAttributes);

    if (recurringIDAttr != null) {
      entryDN = ATTR_RECURRING_TASK_ID + "=" +
      taskID + "," + RECURRING_TASK_BASE_RDN + "," + DN_TASK_ROOT;
    } else {
      entryDN = ATTR_TASK_ID + "=" + taskID + "," +
      SCHEDULED_TASK_BASE_RDN + "," + DN_TASK_ROOT;
    }
    return entryDN;
  }

  private static boolean isScheduleRecurring(TaskScheduleInformation information)
  {
    return information.getRecurringDateTime() != null;
  }

  /**
   * This is a commodity method that returns the common attributes (those
   * related to scheduling) of a task entry for a given
   * {@link TaskScheduleInformation} object.
   * @param information the scheduling information.
   * @return the schedule attributes of the task entry.
   */
  public static ArrayList<RawAttribute> getTaskAttributes(
      TaskScheduleInformation information)
  {
    String taskID = null;
    boolean scheduleRecurring = isScheduleRecurring(information);

    if (scheduleRecurring) {
      taskID = information.getTaskId();
      if (taskID == null || taskID.length() == 0) {
        taskID = information.getTaskClass().getSimpleName() + "-" + UUID.randomUUID();
      }
    } else {
      // Use a formatted time/date for the ID so that is remotely useful
      SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      taskID = df.format(new Date());
    }

    ArrayList<RawAttribute> attributes = new ArrayList<>();

    ArrayList<String> ocValues = new ArrayList<>(4);
    ocValues.add("top");
    ocValues.add(ConfigConstants.OC_TASK);
    if (scheduleRecurring) {
      ocValues.add(ConfigConstants.OC_RECURRING_TASK);
    }
    ocValues.add(information.getTaskObjectclass());
    attributes.add(new LDAPAttribute(ATTR_OBJECTCLASS, ocValues));

    if (scheduleRecurring) {
      attributes.add(new LDAPAttribute(ATTR_RECURRING_TASK_ID, taskID));
    }
    attributes.add(new LDAPAttribute(ATTR_TASK_ID, taskID));

    String classValue = information.getTaskClass().getName();
    attributes.add(new LDAPAttribute(ATTR_TASK_CLASS, classValue));

    // add the start time if necessary
    Date startDate = information.getStartDateTime();
    if (startDate != null) {
      String startTimeString = StaticUtils.formatDateTimeString(startDate);
      attributes.add(new LDAPAttribute(ATTR_TASK_SCHEDULED_START_TIME, startTimeString));
    }

    if (scheduleRecurring) {
      String recurringPatternValues = information.getRecurringDateTime();
      attributes.add(new LDAPAttribute(ATTR_RECURRING_TASK_SCHEDULE, recurringPatternValues));
    }

    // add dependency IDs
    List<String> dependencyIds = information.getDependencyIds();
    if (dependencyIds != null && !dependencyIds.isEmpty()) {
      attributes.add(new LDAPAttribute(ATTR_TASK_DEPENDENCY_IDS, dependencyIds));

      // add the dependency action
      FailedDependencyAction fda = information.getFailedDependencyAction();
      if (fda == null) {
        fda = FailedDependencyAction.defaultValue();
      }
      attributes.add(new LDAPAttribute(ATTR_TASK_FAILED_DEPENDENCY_ACTION, fda.name()));
    }

    // add completion notification email addresses
    List<String> compNotifEmailAddresss = information.getNotifyUponCompletionEmailAddresses();
    if (compNotifEmailAddresss != null && !compNotifEmailAddresss.isEmpty()) {
      attributes.add(new LDAPAttribute(ATTR_TASK_NOTIFY_ON_COMPLETION, compNotifEmailAddresss));
    }

    // add error notification email addresses
    List<String> errNotifEmailAddresss = information.getNotifyUponErrorEmailAddresses();
    if (errNotifEmailAddresss != null && !errNotifEmailAddresss.isEmpty()) {
      attributes.add(new LDAPAttribute(ATTR_TASK_NOTIFY_ON_ERROR, errNotifEmailAddresss));
    }

    information.addTaskAttributes(attributes);

    return attributes;
  }

  /**
   * Schedule a task for execution by writing an entry to the task backend.
   *
   * @param information to be scheduled
   * @return String task ID assigned the new task
   * @throws IOException if there is a stream communication problem
   * @throws LDAPException if there is a problem getting information
   *         out to the directory
   * @throws DecodeException if there is a problem with the encoding
   * @throws TaskClientException if there is a problem with the task entry
   */
  public synchronized TaskEntry schedule(TaskScheduleInformation information)
          throws LDAPException, IOException, DecodeException, TaskClientException
  {
    LDAPReader reader = connection.getLDAPReader();
    LDAPWriter writer = connection.getLDAPWriter();

    ArrayList<Control> controls = new ArrayList<>();
    ArrayList<RawAttribute> attributes = getTaskAttributes(information);

    ByteString entryDN = ByteString.valueOfUtf8(getTaskDN(attributes));
    AddRequestProtocolOp addRequest = new AddRequestProtocolOp(entryDN, attributes);
    LDAPMessage requestMessage =
         new LDAPMessage(nextMessageID.getAndIncrement(), addRequest, controls);

    // Send the request to the server and read the response.
    LDAPMessage responseMessage;
    writer.writeMessage(requestMessage);

    responseMessage = reader.readMessage();
    if (responseMessage == null)
    {
      throw new LDAPException(
              LDAPResultCode.CLIENT_SIDE_SERVER_DOWN,
              ERR_TASK_CLIENT_UNEXPECTED_CONNECTION_CLOSURE.get());
    }

    if (responseMessage.getProtocolOpType() !=
        LDAPConstants.OP_TYPE_ADD_RESPONSE)
    {
      throw new LDAPException(
              LDAPResultCode.CLIENT_SIDE_LOCAL_ERROR,
              ERR_TASK_CLIENT_INVALID_RESPONSE_TYPE.get(
                responseMessage.getProtocolOpName()));
    }

    AddResponseProtocolOp addResponse =
         responseMessage.getAddResponseProtocolOp();
    if (addResponse.getResultCode() != 0) {
      throw new LDAPException(
              LDAPResultCode.CLIENT_SIDE_LOCAL_ERROR,
              addResponse.getErrorMessage());
    }
    return getTaskEntry(getTaskID(attributes));
  }

  /**
   * Gets all the ds-task entries from the task root.
   *
   * @return list of entries from the task root
   * @throws IOException if there is a stream communication problem
   * @throws LDAPException if there is a problem getting information
   *         out to the directory
   * @throws DecodeException if there is a problem with the encoding
   */
  public synchronized List<TaskEntry> getTaskEntries()
          throws LDAPException, IOException, DecodeException {
    List<Entry> entries = new ArrayList<>();

    writeSearch(new SearchRequestProtocolOp(
        ByteString.valueOfUtf8(ConfigConstants.DN_TASK_ROOT),
            SearchScope.WHOLE_SUBTREE,
            DereferenceAliasesPolicy.NEVER,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            false,
            LDAPFilter.decode("(objectclass=ds-task)"),
            new LinkedHashSet<String>()));

    LDAPReader reader = connection.getLDAPReader();
    byte opType;
    do {
      LDAPMessage responseMessage = reader.readMessage();
      if (responseMessage == null) {
        throw new LDAPException(
                LDAPResultCode.CLIENT_SIDE_SERVER_DOWN,
                ERR_TASK_CLIENT_UNEXPECTED_CONNECTION_CLOSURE.get());
      } else {
        opType = responseMessage.getProtocolOpType();
        if (opType == LDAPConstants.OP_TYPE_SEARCH_RESULT_ENTRY) {
          SearchResultEntryProtocolOp searchEntryOp =
                  responseMessage.getSearchResultEntryProtocolOp();
          SearchResultEntry entry = searchEntryOp.toSearchResultEntry();
          entries.add(entry);
        }
      }
    }
    while (opType != LDAPConstants.OP_TYPE_SEARCH_RESULT_DONE);
    List<TaskEntry> taskEntries = new ArrayList<>(entries.size());
    for (Entry entry : entries) {
      taskEntries.add(new TaskEntry(entry));
    }
    return Collections.unmodifiableList(taskEntries);
  }

  /**
   * Gets the entry of the task whose ID is <code>id</code> from the directory.
   *
   * @param id of the entry to retrieve
   * @return Entry for the task
   * @throws IOException if there is a stream communication problem
   * @throws LDAPException if there is a problem getting information
   *         out to the directory
   * @throws DecodeException if there is a problem with the encoding
   * @throws TaskClientException if there is no task with the requested id
   */
  public synchronized TaskEntry getTaskEntry(String id)
          throws LDAPException, IOException, DecodeException, TaskClientException
  {
    Entry entry = null;

    writeSearch(new SearchRequestProtocolOp(
        ByteString.valueOfUtf8(ConfigConstants.DN_TASK_ROOT),
            SearchScope.WHOLE_SUBTREE,
            DereferenceAliasesPolicy.NEVER,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            false,
            LDAPFilter.decode("(" + ATTR_TASK_ID + "=" + id + ")"),
            new LinkedHashSet<String>()));

    LDAPReader reader = connection.getLDAPReader();
    byte opType;
    do {
      LDAPMessage responseMessage = reader.readMessage();
      if (responseMessage == null) {
        LocalizableMessage message = ERR_TASK_CLIENT_UNEXPECTED_CONNECTION_CLOSURE.get();
        throw new LDAPException(UNAVAILABLE.intValue(), message);
      } else {
        opType = responseMessage.getProtocolOpType();
        if (opType == LDAPConstants.OP_TYPE_SEARCH_RESULT_ENTRY) {
          SearchResultEntryProtocolOp searchEntryOp =
                  responseMessage.getSearchResultEntryProtocolOp();
          entry = searchEntryOp.toSearchResultEntry();
        }
      }
    }
    while (opType != LDAPConstants.OP_TYPE_SEARCH_RESULT_DONE);
    if (entry == null) {
      throw new TaskClientException(ERR_TASK_CLIENT_UNKNOWN_TASK.get(id));
    }
    return new TaskEntry(entry);
  }


  /**
   * Changes that the state of the task in the backend to a canceled state.
   *
   * @param  id if the task to cancel
   * @throws IOException if there is a stream communication problem
   * @throws LDAPException if there is a problem getting information
   *         out to the directory
   * @throws DecodeException if there is a problem with the encoding
   * @throws TaskClientException if there is no task with the requested id
   */
  public synchronized void cancelTask(String id)
          throws TaskClientException, IOException, DecodeException, LDAPException
  {
    LDAPReader reader = connection.getLDAPReader();
    LDAPWriter writer = connection.getLDAPWriter();

    TaskEntry entry = getTaskEntry(id);
    TaskState state = entry.getTaskState();
    if (state != null) {
      if (!TaskState.isDone(state)) {

        ByteString dn = ByteString.valueOfUtf8(entry.getDN().toString());

        ArrayList<RawModification> mods = new ArrayList<>();

        String newState;
        if (TaskState.isPending(state)) {
          newState = TaskState.CANCELED_BEFORE_STARTING.name();
        } else {
          newState = TaskState.STOPPED_BY_ADMINISTRATOR.name();
        }
        LDAPAttribute attr = new LDAPAttribute(ATTR_TASK_STATE, newState);
        mods.add(new LDAPModification(ModificationType.REPLACE, attr));

        ModifyRequestProtocolOp modRequest =
                new ModifyRequestProtocolOp(dn, mods);
        LDAPMessage requestMessage =
             new LDAPMessage(nextMessageID.getAndIncrement(), modRequest, null);

        writer.writeMessage(requestMessage);

        LDAPMessage responseMessage = reader.readMessage();

        if (responseMessage == null) {
          LocalizableMessage message = ERR_TASK_CLIENT_UNEXPECTED_CONNECTION_CLOSURE.get();
          throw new LDAPException(UNAVAILABLE.intValue(), message);
        }

        if (responseMessage.getProtocolOpType() !=
                LDAPConstants.OP_TYPE_MODIFY_RESPONSE)
        {
          throw new LDAPException(
                  LDAPResultCode.CLIENT_SIDE_LOCAL_ERROR,
                  ERR_TASK_CLIENT_INVALID_RESPONSE_TYPE.get(
                    responseMessage.getProtocolOpName()));
        }

        ModifyResponseProtocolOp modResponse =
                responseMessage.getModifyResponseProtocolOp();
        LocalizableMessage errorMessage = modResponse.getErrorMessage();
        if (errorMessage != null) {
          throw new LDAPException(
                  LDAPResultCode.CLIENT_SIDE_LOCAL_ERROR,
                  errorMessage);
        }
      } else if (TaskState.isRecurring(state)) {

        ByteString dn = ByteString.valueOfUtf8(entry.getDN().toString());
        DeleteRequestProtocolOp deleteRequest =
          new DeleteRequestProtocolOp(dn);

        LDAPMessage requestMessage = new LDAPMessage(
          nextMessageID.getAndIncrement(), deleteRequest, null);

        writer.writeMessage(requestMessage);

        LDAPMessage responseMessage = reader.readMessage();

        if (responseMessage == null) {
          LocalizableMessage message = ERR_TASK_CLIENT_UNEXPECTED_CONNECTION_CLOSURE.get();
          throw new LDAPException(UNAVAILABLE.intValue(), message);
        }

        if (responseMessage.getProtocolOpType() !=
                LDAPConstants.OP_TYPE_DELETE_RESPONSE)
        {
          throw new LDAPException(
                  LDAPResultCode.CLIENT_SIDE_LOCAL_ERROR,
                  ERR_TASK_CLIENT_INVALID_RESPONSE_TYPE.get(
                    responseMessage.getProtocolOpName()));
        }

        DeleteResponseProtocolOp deleteResponse =
                responseMessage.getDeleteResponseProtocolOp();
        LocalizableMessage errorMessage = deleteResponse.getErrorMessage();
        if (errorMessage != null) {
          throw new LDAPException(
                  LDAPResultCode.CLIENT_SIDE_LOCAL_ERROR,
                  errorMessage);
        }
      } else {
        throw new TaskClientException(
                ERR_TASK_CLIENT_UNCANCELABLE_TASK.get(id));
      }
    } else {
      throw new TaskClientException(
              ERR_TASK_CLIENT_TASK_STATE_UNKNOWN.get(id));
    }
  }


  /**
   * Writes a search to the directory writer.
   * @param searchRequest to write
   * @throws IOException if there is a stream communication problem
   */
  private void writeSearch(SearchRequestProtocolOp searchRequest)
          throws IOException {
    LDAPWriter writer = connection.getLDAPWriter();
    LDAPMessage requestMessage = new LDAPMessage(
            nextMessageID.getAndIncrement(),
            searchRequest,
            new ArrayList<Control>());

    // Send the request to the server and read the response.
    writer.writeMessage(requestMessage);
  }

}
