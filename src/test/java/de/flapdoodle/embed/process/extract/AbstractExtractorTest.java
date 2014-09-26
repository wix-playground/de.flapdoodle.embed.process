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

import de.flapdoodle.embed.process.config.store.*;
import de.flapdoodle.embed.process.config.store.FileSet.Entry;
import de.flapdoodle.embed.process.example.GenericPackageResolver;
import de.flapdoodle.embed.process.extract.mapper.IDestinationFileMapper;
import de.flapdoodle.embed.process.extract.mapper.TempFileMapper;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.directories.PlatformTempDir;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class AbstractExtractorTest {

	@Test(expected=IOException.class)
	public void testForExceptionHint() throws FileNotFoundException, IOException {
		
		IPackageResolver packageResolver=new GenericPackageResolver();
		
		IDownloadConfig runtime=new DownloadConfigBuilder()
			.downloadPath("http://192.168.0.1")
			.downloadPrefix("prefix")
			.packageResolver(packageResolver)
			.artifactStorePath(new PlatformTempDir())
			.fileNaming(new UUIDTempNaming())
			.progressListener(new StandardConsoleProgressListener())
			.userAgent("foo-bar")
			.build();
		
		IDirectory factory=new PlatformTempDir();
        IDestinationFileMapper destProducer = new TempFileMapper(new UUIDTempNaming());
		List<Entry> entries=new ArrayList<Entry>();
		entries.add(new Entry(FileType.Executable, "foo-bar.exe", Pattern.compile(".")));
		FileSet fileSet=new FileSet(entries);
		FilesToExtract filesToExtract=new FilesToExtract(factory, destProducer, fileSet);
		
		new AbstractExtractor() {
			
			@Override
			protected ArchiveWrapper archiveStream(File source) throws FileNotFoundException, IOException {
				throw new IOException("foo");
			}
		}.extract(runtime, new File("bar"), filesToExtract);
	}

}
