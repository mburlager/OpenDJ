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
 * Copyright 2007-2009 Sun Microsystems, Inc.
 * Portions Copyright 2011-2015 ForgeRock AS.
 */

package org.opends.server.replication.service;

import static org.opends.messages.ReplicationMessages.*;
import org.forgerock.i18n.slf4j.LocalizedLogger;
import org.opends.server.replication.protocol.Session;

import org.opends.server.api.DirectoryThread;

/**
 * This class implements a thread to monitor heartbeat messages from the
 * replication server.  Each broker runs one of these threads.
 */
final class HeartbeatMonitor extends DirectoryThread
{
  private static final LocalizedLogger logger = LocalizedLogger.getLoggerForThisClass();



  /**
   * The session on which heartbeats are to be monitored.
   */
  private final Session session;


  /**
   * The time in milliseconds between heartbeats from the replication
   * server.  Zero means heartbeats are off.
   */
  private final long heartbeatInterval;

  /** Info required for logging. */
  private final int serverID;
  private final int replicationServerID;
  private final String baseDN;

  /** Thread life-cycle state. */
  private volatile boolean shutdown;
  private final Object shutdownLock = new Object();



  /**
   * Create a heartbeat monitor thread.
   *
   * @param serverID
   *          The local directory server ID.
   * @param replicationServerID
   *          The remote replication server ID.
   * @param baseDN
   *          The name of the domain being replicated.
   * @param session
   *          The session on which heartbeats are to be monitored.
   * @param heartbeatInterval
   *          The expected interval between heartbeats received (in
   *          milliseconds).
   */
  HeartbeatMonitor(int serverID, int replicationServerID,
      String baseDN, Session session, long heartbeatInterval)
  {
    super("Replica DS("
      + serverID + ") heartbeat monitor for domain \""
      + baseDN + "\" from RS(" + replicationServerID
      + ") at " + session.getReadableRemoteAddress());
    this.serverID = serverID;
    this.replicationServerID = replicationServerID;
    this.baseDN = baseDN;
    this.session = session;
    this.heartbeatInterval = heartbeatInterval;
  }

  /**
   * Call this method to stop the thread.
   */
  public void shutdown()
  {
    synchronized (shutdownLock)
    {
      shutdown = true;
      shutdownLock.notifyAll();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void run()
  {
    if (logger.isTraceEnabled())
    {
      logger.trace(this + " is starting, expected interval is " +
                heartbeatInterval);
    }

    try
    {
      boolean gotOneFailure = false;
      while (!shutdown)
      {
        long now = System.currentTimeMillis();
        long lastReceiveTime = session.getLastReceiveTime();
        if (now > lastReceiveTime + heartbeatInterval)
        {
          if (gotOneFailure)
          {
            // Heartbeat is well overdue so the server is assumed to be dead.
            logger.warn(WARN_HEARTBEAT_FAILURE, serverID,
                replicationServerID, session.getReadableRemoteAddress(), baseDN);

            // Exit monitor and close session.
            shutdown = true;
            break;
          }
          else
          {
            gotOneFailure = true;
          }
        }
        else
        {
          gotOneFailure = false;
        }

        // Sleep.
        synchronized (shutdownLock)
        {
          if (!shutdown)
          {
            try
            {
              shutdownLock.wait(heartbeatInterval);
            }
            catch (InterruptedException e)
            {
              // Server shutdown monitor may interrupt slow threads.
              logger.traceException(e);
              shutdown = true;
            }
          }
        }
      }
    }
    finally
    {
      if (logger.isTraceEnabled())
      {
        logger.trace("Heartbeat monitor is exiting");
      }
      session.close();
    }
  }
}
