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
import de.flapdoodle.embed.process.extract.ITempNaming;

/**
 * @author viliusl
 * @since 25/09/14
 */
public class TempFileNamingProducer implements IDestinationFileProducer {

    private final ITempNaming namingStrategy;

    public TempFileNamingProducer(final ITempNaming executableNaming) {
        namingStrategy = executableNaming;
    }

    @Override
    public DestinationEntry fromSource(IArchiveEntry archiveEntry, FileSet.Entry fileSetEntry) {
        String relPath;
        switch (fileSetEntry.type()) {
            case Executable:
                relPath = namingStrategy.nameFor("extract", fileSetEntry.destination());
                break;
            default:
                relPath = fileSetEntry.destination();
                break;
        }

        return new DestinationEntry(fileSetEntry.type(), relPath, false);
    }
}
