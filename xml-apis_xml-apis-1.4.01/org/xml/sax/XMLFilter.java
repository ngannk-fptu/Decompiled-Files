/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax;

import org.xml.sax.XMLReader;

public interface XMLFilter
extends XMLReader {
    public void setParent(XMLReader var1);

    public XMLReader getParent();
}

