/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.messaging.core.impl.postoffice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jboss.messaging.util.ConcurrentHashSet;

/**
 * 
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 * @version <tt>$Revision: $</tt>6 Jul 2007
 *
 * $Id: $
 *
 */
public class AddAllReplicatedDeliveriesMessage extends ClusterRequest
{
	private int nodeID;
	
	private Map deliveries;
	
	public AddAllReplicatedDeliveriesMessage()
	{		
	}
	
	public AddAllReplicatedDeliveriesMessage(int nodeID, Map deliveries)
	{
		this.nodeID = nodeID;
		
		this.deliveries = deliveries;		
	}
		
	Object execute(RequestTarget office) throws Throwable
	{		
		office.handleAddAllReplicatedDeliveries(deliveries, nodeID);
		
		return "ok";
	}

	byte getType()
	{
		return ClusterRequest.ADD_ALL_REPLICATED_DELIVERIES_REQUEST;
	}

	public void read(DataInputStream in) throws Exception
	{
		nodeID = in.readInt();
		
		int size = in.readInt();
		
		deliveries = new HashMap(size);
		
		for (int i = 0; i < size; i++)
		{
			String queueName = in.readUTF();
			
			int size2 = in.readInt();
			
			Set ids = new ConcurrentHashSet(size2);
			
			for (int j = 0; j < size2; j++)
			{
				long id = in.readLong();
				
				ids.add(new Long(id));
			}
			
			deliveries.put(queueName, ids);
		}
	}

	public void write(DataOutputStream out) throws Exception
	{		
		out.writeInt(nodeID);
		
		out.writeInt(deliveries.size());
		
		Iterator iter = deliveries.entrySet().iterator();
		
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry)iter.next();
			
			String queueName = (String)entry.getKey();
			
			out.writeUTF(queueName);
			
			Set ids = (Set)entry.getValue();
			
			out.writeInt(ids.size());
			
			Iterator iter2 = ids.iterator();
			
			while (iter2.hasNext())
			{
				Long id = (Long)iter2.next();
				
				out.writeLong(id.longValue());
			}
		}
	}

}
