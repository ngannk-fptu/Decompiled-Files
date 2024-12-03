/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamLocation2;

public interface LocationInfo {
    public long getStartingByteOffset();

    public long getStartingCharOffset();

    public long getEndingByteOffset() throws XMLStreamException;

    public long getEndingCharOffset() throws XMLStreamException;

    public Location getLocation();

    public XMLStreamLocation2 getStartLocation();

    public XMLStreamLocation2 getCurrentLocation();

    public XMLStreamLocation2 getEndLocation() throws XMLStreamException;
}

