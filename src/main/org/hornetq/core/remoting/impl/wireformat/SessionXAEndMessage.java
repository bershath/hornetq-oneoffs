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

package org.hornetq.core.remoting.impl.wireformat;

import javax.transaction.xa.Xid;

import org.hornetq.core.remoting.spi.MessagingBuffer;
import org.hornetq.utils.DataConstants;


/**
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 * 
 * @version <tt>$Revision$</tt>
 */
public class SessionXAEndMessage extends PacketImpl
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------
   
   private Xid xid;
   
   private boolean failed;
   
   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   public SessionXAEndMessage(final Xid xid, final boolean failed)
   {
      super(SESS_XA_END);
      
      this.xid = xid;
      
      this.failed = failed;
   }
   
   public SessionXAEndMessage()
   {
      super(SESS_XA_END);
   }

   // Public --------------------------------------------------------
   
   public boolean isFailed()
   {
      return failed;
   }
   
   public Xid getXid()
   {
      return xid;
   }

   public int getRequiredBufferSize()
   {
      return BASIC_PACKET_SIZE + XidCodecSupport.getXidEncodeLength(xid) + DataConstants.SIZE_BOOLEAN;
   }

   public void encodeBody(final MessagingBuffer buffer)
   {
      XidCodecSupport.encodeXid(xid, buffer);
      buffer.writeBoolean(failed);
   }
   
   public void decodeBody(final MessagingBuffer buffer)
   {
      xid = XidCodecSupport.decodeXid(buffer);
      failed = buffer.readBoolean();
   }

   @Override
   public String toString()
   {
      return getParentString() + ", xid=" + xid + ", failed=" + failed + "]";
   }
   
   public boolean equals(Object other)
   {
      if (other instanceof SessionXAEndMessage == false)
      {
         return false;
      }
            
      SessionXAEndMessage r = (SessionXAEndMessage)other;
      
      return super.equals(other) && this.xid.equals(r.xid) &&
             this.failed == r.failed;
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
