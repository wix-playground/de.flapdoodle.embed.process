package de.flapdoodle.embed.process.extract.produce;

import de.flapdoodle.embed.process.config.store.FileType;

/**
 * @author viliusl
 * @since 25/09/14
 */
public class DestinationEntry {

    private final FileType _type;
    private final String _destination;

    public DestinationEntry(
            FileType type,
            String destination,
            boolean relative) {
        _type = type;
        _destination = destination;
    }

    public FileType type() {
        return _type;
    }

    public String destination() {
        return _destination;
    }

    public boolean isExecutable() {
        switch (_type) {
            case Executable:
            case Script:
                return true;
            default:
                return false;
        }
    }
}
