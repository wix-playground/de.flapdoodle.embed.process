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
