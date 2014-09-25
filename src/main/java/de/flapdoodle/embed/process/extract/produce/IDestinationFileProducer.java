/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano (trajano@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embed.process.extract.produce;

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.extract.IArchiveEntry;

import java.io.IOException;

/**
 * @author viliusl
 * @since 25/09/14
 */
public interface IDestinationFileProducer {

    /**
     * Create target file entry.
     *
     * @param archiveEntry entry in archive
     * @param fileSetEntry fileSet entry against which archive entry was matched.
     * @return created empty file.
     * @throws java.io.IOException
     */
    DestinationEntry fromSource(IArchiveEntry archiveEntry, FileSet.Entry fileSetEntry);
}