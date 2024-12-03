/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public interface XMLReporter {
    public void report(String var1, String var2, Object var3, Location var4) throws XMLStreamException;
}

