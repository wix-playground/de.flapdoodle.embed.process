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