/*
 * JBoss, Home of Professional Open Source Copyright 2005-2008, Red Hat Middleware LLC, and individual contributors by
 * the @authors tag. See the copyright.txt in the distribution for a full listing of individual contributors. This is
 * free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this software; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.hornetq.core.remoting.impl.wireformat.replication;

import org.hornetq.core.remoting.impl.wireformat.PacketImpl;
import org.hornetq.core.remoting.spi.MessagingBuffer;
import org.hornetq.utils.DataConstants;
import org.hornetq.utils.SimpleString;

/**
 * 
 * A ReplicateRemoteBindingAddedMessage
 *
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 * 
 * Created 4 Mar 2009 18:36:30
 *
 *
 */
public class ReplicateRemoteBindingAddedMessage extends PacketImpl
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   private SimpleString clusterConnectionName;

   private SimpleString address;

   private SimpleString uniqueName;

   private SimpleString routingName;

   private int remoteQueueID;

   private SimpleString filterString;

   private SimpleString sfQueueName;

   private int distance;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   public ReplicateRemoteBindingAddedMessage(final SimpleString clusterConnectionName,
                                             final SimpleString address,
                                             final SimpleString uniqueName,
                                             final SimpleString routingName,
                                             final int remoteQueueID,
                                             final SimpleString filterString,
                                             final SimpleString sfQueueName,
                                             final int distance)
   {
      super(REPLICATE_ADD_REMOTE_QUEUE_BINDING);

      this.clusterConnectionName = clusterConnectionName;
      this.address = address;
      this.uniqueName = uniqueName;
      this.routingName = routingName;
      this.remoteQueueID = remoteQueueID;
      this.filterString = filterString;
      this.sfQueueName = sfQueueName;
      this.distance = distance;
   }

   // Public --------------------------------------------------------

   public ReplicateRemoteBindingAddedMessage()
   {
      super(REPLICATE_ADD_REMOTE_QUEUE_BINDING);
   }

   public int getRequiredBufferSize()
   {
      return BASIC_PACKET_SIZE +
             clusterConnectionName.sizeof() + // buffer.writeSimpleString(clusterConnectionName);
             address.sizeof() + // buffer.writeSimpleString(address);
             uniqueName.sizeof() + // buffer.writeSimpleString(uniqueName);
             routingName.sizeof() + //  buffer.writeSimpleString(routingName);
             DataConstants.SIZE_INT + // buffer.writeInt(remoteQueueID);
             SimpleString.sizeofNullableString(filterString) + // buffer.writeNullableSimpleString(filterString);
             sfQueueName.sizeof() + // buffer.writeSimpleString(sfQueueName);
             DataConstants.SIZE_INT; // buffer.writeInt(distance);
   }

   @Override
   public void encodeBody(final MessagingBuffer buffer)
   {
      buffer.writeSimpleString(clusterConnectionName);
      buffer.writeSimpleString(address);
      buffer.writeSimpleString(uniqueName);
      buffer.writeSimpleString(routingName);
      buffer.writeInt(remoteQueueID);
      buffer.writeNullableSimpleString(filterString);
      buffer.writeSimpleString(sfQueueName);
      buffer.writeInt(distance);
   }

   @Override
   public void decodeBody(final MessagingBuffer buffer)
   {
      clusterConnectionName = buffer.readSimpleString();
      address = buffer.readSimpleString();
      uniqueName = buffer.readSimpleString();
      routingName = buffer.readSimpleString();
      remoteQueueID = buffer.readInt();
      filterString = buffer.readNullableSimpleString();
      sfQueueName = buffer.readSimpleString();
      distance = buffer.readInt();
   }

   public SimpleString getClusterConnectionName()
   {
      return clusterConnectionName;
   }

   public SimpleString getAddress()
   {
      return address;
   }

   public SimpleString getUniqueName()
   {
      return uniqueName;
   }

   public SimpleString getRoutingName()
   {
      return routingName;
   }

   public int getRemoteQueueID()
   {
      return remoteQueueID;
   }

   public SimpleString getFilterString()
   {
      return filterString;
   }

   public SimpleString getSfQueueName()
   {
      return sfQueueName;
   }

   public int getDistance()
   {
      return distance;
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}