/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005-2008, Red Hat Middleware LLC, and individual contributors
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

package org.hornetq.tests.performance.paging;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientProducer;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.exception.MessagingException;
import org.hornetq.core.remoting.spi.MessagingBuffer;
import org.hornetq.core.server.MessagingServer;
import org.hornetq.core.settings.impl.AddressSettings;
import org.hornetq.tests.util.ServiceTestBase;
import org.hornetq.utils.SimpleString;

/**
 * A MeasurePagingMultiThreadTest
 *
 * @author <a href="mailto:clebert.suconic@jboss.org">Clebert Suconic</a>
 * 
 * Created Dec 1, 2008 1:02:39 PM
 *
 *
 */
public class MeasurePagingMultiThreadTest extends ServiceTestBase
{

   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   // Public --------------------------------------------------------

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      clearData();
   }

   public void testPagingMultipleSenders() throws Throwable
   {

      final int NUMBER_OF_THREADS = 18;
      final int NUMBER_OF_MESSAGES = 50000;
      final int SIZE_OF_MESSAGE = 1024;

      Configuration config = createDefaultConfig();

      HashMap<String, AddressSettings> settings = new HashMap<String, AddressSettings>();

      MessagingServer messagingService = createServer(true, config, 10 * 1024, 20 * 1024, settings);
      messagingService.start();
      try
      {

         final ClientSessionFactory factory = createInVMFactory();
         final SimpleString adr = new SimpleString("test-adr");

         createDestination(factory, adr);

         // Send some messages to make sure the destination is in page mode before we measure
         // And that will also help with VM optimizations
         sendInitialBatch(adr, NUMBER_OF_MESSAGES, SIZE_OF_MESSAGE, factory);

         final CountDownLatch latchAlign = new CountDownLatch(NUMBER_OF_THREADS);

         final CountDownLatch latchStart = new CountDownLatch(1);

         class Sender extends Thread
         {

            private final ClientSession session;

            private final ClientProducer producer;

            private final ClientMessage msg;

            Throwable e;

            public Sender() throws Exception
            {
               session = factory.createSession(false, true, true);
               producer = session.createProducer(adr);
               msg = session.createClientMessage(true);
               msg.getBody().writeBytes(new byte[SIZE_OF_MESSAGE]);
            }

            // run is not going to close sessions or anything, as we don't want to measure that time
            // so this will be done in a second time
            public void cleanUp() throws Exception
            {
               session.close();
            }

            @Override
            public void run()
            {
               try
               {
                  latchAlign.countDown();
                  latchStart.await();

                  long start = System.currentTimeMillis();
                  sendMessages(NUMBER_OF_MESSAGES, producer, msg);
                  long end = System.currentTimeMillis();

                  System.out.println("Thread " + Thread.currentThread().getName() +
                                     " finished sending in " +
                                     (end - start) +
                                     " milliseconds");
               }
               catch (Throwable e)
               {
                  this.e = e;
               }

            }
         }

         Sender senders[] = new Sender[NUMBER_OF_THREADS];

         for (int i = 0; i < NUMBER_OF_THREADS; i++)
         {
            senders[i] = new Sender();
            senders[i].start();
         }

         latchAlign.await();

         long timeStart = System.currentTimeMillis();

         latchStart.countDown();

         for (Thread t : senders)
         {
            t.join();
         }

         long timeEnd = System.currentTimeMillis();

         System.out.println("Total Time: " + (timeEnd - timeStart) +
                            " milliseconds what represented " +
                            (NUMBER_OF_MESSAGES * NUMBER_OF_THREADS * 1000 / (timeEnd - timeStart)) +
                            " per second");

         for (Sender s : senders)
         {
            if (s.e != null)
            {
               throw s.e;
            }
            s.cleanUp();
         }

      }
      finally
      {
         messagingService.stop();

      }

   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   /**
    * @param adr
    * @param nMessages
    * @param messageSize
    * @param factory
    * @throws MessagingException
    */
   private void sendInitialBatch(final SimpleString adr,
                                 final int nMessages,
                                 final int messageSize,
                                 final ClientSessionFactory factory) throws MessagingException
   {
      ClientSession session = factory.createSession(false, true, true);
      ClientProducer producer = session.createProducer(adr);
      ClientMessage msg = session.createClientMessage(true);

      msg.getBody().writeBytes(new byte[messageSize]);

      sendMessages(nMessages, producer, msg);
   }

   /**
    * @param nMessages
    * @param producer
    * @param msg
    * @throws MessagingException
    */
   private void sendMessages(final int nMessages, final ClientProducer producer, final ClientMessage msg) throws MessagingException
   {
      for (int i = 0; i < nMessages; i++)
      {
         producer.send(msg);
      }
   }

   /**
    * @param factory
    * @param adr
    * @throws MessagingException
    */
   private void createDestination(final ClientSessionFactory factory, final SimpleString adr) throws MessagingException
   {
      {
         ClientSession session = factory.createSession(false, false, false);
         session.createQueue(adr, adr, null, true);
         session.close();
      }
   }

   // Inner classes -------------------------------------------------

}