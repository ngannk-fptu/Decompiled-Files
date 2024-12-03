/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.ext.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;

public interface CharacterDataReader {
    public static final String PROPERTY = CharacterDataReader.class.getName();

    public void writeTextTo(Writer var1) throws XMLStreamException, IOException;
}

