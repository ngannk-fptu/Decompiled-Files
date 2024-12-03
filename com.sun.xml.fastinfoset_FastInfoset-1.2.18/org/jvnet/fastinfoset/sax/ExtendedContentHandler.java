/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface ExtendedContentHandler
extends ContentHandler {
    public void characters(char[] var1, int var2, int var3, boolean var4) throws SAXException;
}

