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

package org.hornetq.core.paging;

import java.util.List;
import java.util.concurrent.Executor;

import org.hornetq.core.journal.SequentialFileFactory;
import org.hornetq.core.persistence.StorageManager;
import org.hornetq.core.postoffice.PostOffice;
import org.hornetq.core.settings.HierarchicalRepository;
import org.hornetq.core.settings.impl.AddressSettings;
import org.hornetq.utils.SimpleString;

/**
 * The integration point between the PagingManger and the File System (aka SequentialFiles)
 * 
 * @author <a href="mailto:clebert.suconic@jboss.com">Clebert Suconic</a>
 *
 */
public interface PagingStoreFactory
{
   PagingStore newStore(SimpleString destinationName, AddressSettings addressSettings) throws Exception;

   void stop() throws InterruptedException;

   void setPagingManager(PagingManager manager);

   void setStorageManager(StorageManager storageManager);

   void setPostOffice(PostOffice office);

   List<PagingStore> reloadStores(HierarchicalRepository<AddressSettings> addressSettingsRepository) throws Exception;

   /**
    * @param storeName
    * @return
    */
   SequentialFileFactory newFileFactory(SimpleString destinationName) throws Exception;

}