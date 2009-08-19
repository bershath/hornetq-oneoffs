/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005-2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.hornetq.tests.integration.management;

import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.core.management.BroadcastGroupControl;
import org.hornetq.core.management.ResourceNames;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;

/**
 * A BroadcastGroupControlUsingCoreTest
 *
 * @author <a href="mailto:jmesnil@redhat.com">Jeff Mesnil</a>
 *
 *
 */
public class BroadcastGroupControlUsingCoreTest extends BroadcastGroupControlTest
{

   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   private ClientSession session;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   // BroadcastGroupControlTest overrides --------------------------------

   @Override
   protected BroadcastGroupControl createManagementControl(final String name) throws Exception
   {
      ClientSessionFactory sf = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));
      session = sf.createSession(false, true, true);
      session.start();

      return new BroadcastGroupControl()
      {
         private final CoreMessagingProxy proxy = new CoreMessagingProxy(session,
                                                                         ResourceNames.CORE_BROADCAST_GROUP + name);
         
         public long getBroadcastPeriod()
         {
            return ((Integer)proxy.retrieveAttributeValue("broadcastPeriod")).longValue();
         }

         public Object[] getConnectorPairs()
         {
            return (Object[])proxy.retrieveAttributeValue("connectorPairs");
         }
         
         public String getConnectorPairsAsJSON()
         {
            return (String)proxy.retrieveAttributeValue("connectorPairsAsJSON");
         }

         public String getGroupAddress()
         {
            return (String)proxy.retrieveAttributeValue("groupAddress");
         }

         public int getGroupPort()
         {
            return (Integer)proxy.retrieveAttributeValue("groupPort");
         }

         public int getLocalBindPort()
         {
            return (Integer)proxy.retrieveAttributeValue("localBindPort");
         }

         public String getName()
         {
            return (String)proxy.retrieveAttributeValue("name");
         }

         public boolean isStarted()
         {
            return (Boolean)proxy.retrieveAttributeValue("started");
         }

         public void start() throws Exception
         {
            proxy.invokeOperation("start");
         }

         public void stop() throws Exception
         {
            proxy.invokeOperation("stop");
         }
      };
   }

   // Public --------------------------------------------------------

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   @Override
   protected void tearDown() throws Exception
   {
      if (session != null)
      {
         session.close();
      }
      
      session = null;

      super.tearDown();
   }

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------

}