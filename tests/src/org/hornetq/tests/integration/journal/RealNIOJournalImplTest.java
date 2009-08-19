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

package org.hornetq.tests.integration.journal;

import java.io.File;

import org.hornetq.core.journal.SequentialFileFactory;
import org.hornetq.core.journal.impl.NIOSequentialFileFactory;
import org.hornetq.core.logging.Logger;
import org.hornetq.tests.unit.core.journal.impl.JournalImplTestUnit;

/**
 * 
 * A RealJournalImplTest
 * 
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 *
 */
public class RealNIOJournalImplTest extends JournalImplTestUnit
{
   private static final Logger log = Logger.getLogger(RealNIOJournalImplTest.class);

   @Override
   protected SequentialFileFactory getFileFactory() throws Exception
   {
      File file = new File(getTestDir());

      log.debug("deleting directory " + getTestDir());

      deleteDirectory(file);

      file.mkdir();

      return new NIOSequentialFileFactory(getTestDir());
   }

   @Override
   protected int getAlignment()
   {
      return 1;
   }

}