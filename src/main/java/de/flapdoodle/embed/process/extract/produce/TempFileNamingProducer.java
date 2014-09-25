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
