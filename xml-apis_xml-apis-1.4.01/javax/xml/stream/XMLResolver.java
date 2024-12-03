/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import javax.xml.stream.XMLStreamException;

public interface XMLResolver {
    public Object resolveEntity(String var1, String var2, String var3, String var4) throws XMLStreamException;
}

