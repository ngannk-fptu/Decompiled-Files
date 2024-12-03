/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.xml.stream;

import org.apache.xmlbeans.xml.stream.Attribute;

public interface AttributeIterator {
    public Attribute next();

    public boolean hasNext();

    public Attribute peek();

    public void skip();
}

