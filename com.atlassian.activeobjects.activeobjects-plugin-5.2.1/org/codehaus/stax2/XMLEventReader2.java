/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public interface XMLEventReader2
extends XMLEventReader {
    public boolean hasNextEvent() throws XMLStreamException;

    public boolean isPropertySupported(String var1);

    public boolean setProperty(String var1, Object var2);
}

