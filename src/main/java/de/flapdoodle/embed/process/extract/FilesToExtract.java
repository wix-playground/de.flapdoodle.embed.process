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
package de.flapdoodle.embed.process.extract;

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileSet.Entry;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.extract.mapper.DestinationEntry;
import de.flapdoodle.embed.process.extract.mapper.IDestinationFileMapper;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.file.FileAlreadyExistsException;
import de.flapdoodle.embed.process.io.file.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FilesToExtract {

	private final ArrayList<FileSet.Entry> _files;
    private final IDestinationFileMapper _mapper;
	private final File _dirFactoryResult;
	private final boolean _dirFactoryResultIsGenerated;

	public FilesToExtract(IDirectory dirFactory, IDestinationFileMapper mapper, FileSet fileSet) {
		if (dirFactory==null) throw new NullPointerException("dirFactory is NULL");
		if (mapper==null) throw new NullPointerException("producer is NULL");
		if (fileSet==null) throw new NullPointerException("fileSet is NULL");
		
		_files = new ArrayList<FileSet.Entry>(fileSet.entries());
		_dirFactoryResult = dirFactory.asFile();
		_dirFactoryResultIsGenerated=dirFactory.isGenerated();
		_mapper = mapper;
	}

	public File generatedBaseDir() {
		return _dirFactoryResultIsGenerated ? _dirFactoryResult : null;
	}
	
	public boolean nothingLeft() {
		return _files.isEmpty();
	}

	public IExtractionMatch find(IArchiveEntry entry) {
		Entry found = null;

		if (!entry.isDirectory()) {
			for (FileSet.Entry e : _files) {
				if (e.matchingPattern().matcher(entry.getName()).matches()) {
					found = e;
					break;
				}
			}

			if (found != null) {
				_files.remove(found);
			}
		}
		return found != null ? new Match(_dirFactoryResult, _mapper, entry, found) : null;
	}

    static class Match implements IExtractionMatch {

        private final Entry _fileSetEntry;
        private final IArchiveEntry _archiveEntry;
        private final IDestinationFileMapper _mapper;
        private final File _dirFactoryResult;

        public Match(
                final File dirFactoryResult,
                final IDestinationFileMapper mapper,
                final IArchiveEntry archiveEntry,
                final Entry fileSetEntry) {
            _dirFactoryResult = dirFactoryResult;
            _mapper = mapper;
            _fileSetEntry = fileSetEntry;
            _archiveEntry = archiveEntry;
        }

        @Override
        public FileType type() {
            return _fileSetEntry.type();
        }

        @Override
        public File write(InputStream source, long size) throws IOException {
            DestinationEntry destination = _mapper.fromSource(_archiveEntry, _fileSetEntry);
            File result;

            switch (destination.type()) {
                case Executable:
                    try {
                        result = Files.createTempFile(_dirFactoryResult, destination.destination());
                    } catch (FileAlreadyExistsException ex) {
                        throw new ExecutableFileAlreadyExistsException(ex);
                    }
                    break;
                default:
                    result = Files.createTempFile(_dirFactoryResult, destination.destination());
                    break;
            }

            Files.write(source, size, result);
            switch (destination.type()) {
                case Executable:
                case Script:
                    result.setExecutable(true);
                    break;
            }
            return result;
        }
    }

}
