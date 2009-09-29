/*
 * Copyright 2009 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */


package org.hornetq.jms.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientSession;

/**
 * This class implements javax.jms.ObjectMessage
 * 
 * Don't used ObjectMessage if you want good performance!
 * 
 * Serialization is slooooow!
 * 
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 * @author <a href="mailto:ovidiu@feodorov.com">Ovidiu Feodorov</a>
 * @author <a href="mailto:ataylor@redhat.com">Andy Taylor</a>
 * 
 * @version $Revision: 3412 $
 *
 * $Id: HornetQRAObjectMessage.java 3412 2007-12-05 19:41:47Z timfox $
 */
public class HornetQObjectMessage extends HornetQMessage implements ObjectMessage
{
   // Constants -----------------------------------------------------

   public static final byte TYPE = 2;

   // Attributes ----------------------------------------------------
   
   // keep a snapshot of the Serializable Object as a byte[] to provide Object isolation
   private byte[] data;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
   
   /*
    * This constructor is used to construct messages prior to sending
    */
   public HornetQObjectMessage()
   {
      super(HornetQObjectMessage.TYPE);
   }

   public HornetQObjectMessage( final ClientSession session)
   {
      super(HornetQObjectMessage.TYPE, session);
   }
   
   public HornetQObjectMessage(final ClientMessage message, ClientSession session)
   {
      super(message, session);
   }

   /**
    * A copy constructor for foreign JMS ObjectMessages.
    */
   public HornetQObjectMessage(final ObjectMessage foreign, final ClientSession session) throws JMSException
   {
      super(foreign, HornetQObjectMessage.TYPE, session);

      setObject(foreign.getObject()); 
   }

   // Public --------------------------------------------------------

   public byte getType()
   {
      return HornetQObjectMessage.TYPE;
   }
   
   public void doBeforeSend() throws Exception
   {
      getBody().clear();
      if (data != null)
      {
         getBody().writeInt(data.length);
         getBody().writeBytes(data);
      }
      
      super.doBeforeSend();
   }
   
   
   public void doBeforeReceive() throws Exception
   {
      super.doBeforeReceive();
      try
      {
         int len = getBody().readInt();
         data = new byte[len];
         getBody().readBytes(data);
      }
      catch (Exception e)
      {
         data = null;
      }
      
   }
      
   // ObjectMessage implementation ----------------------------------

   public void setObject(Serializable object) throws JMSException
   {  
      checkWrite();

      if (object != null)
      {
         try 
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(object);

            oos.flush();

            data = baos.toByteArray();
         }
         catch (Exception e)
         {
            JMSException je = new JMSException("Failed to serialize object");
            je.setLinkedException(e);
            throw je;
         }
      }
   }

   // lazy deserialize the Object the first time the client requests it
   public Serializable getObject() throws JMSException
   {
      if (data == null || data.length == 0)
      {
         return null;
      }

      try
      {
         ByteArrayInputStream bais = new ByteArrayInputStream(data);
         ObjectInputStream ois = new org.hornetq.utils.ObjectInputStreamWithClassLoader(bais);
         Serializable object = (Serializable)ois.readObject();
         return object;
      }
      catch (Exception e)
      {
         JMSException je = new JMSException(e.getMessage());
         je.setStackTrace(e.getStackTrace());
         throw je;
      }
   }

   public void clearBody() throws JMSException
   {
      super.clearBody();
      
      data = null;
   }
   
   

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}