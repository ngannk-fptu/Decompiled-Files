/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface XMLizable {
    public void toSAX(ContentHandler var1) throws SAXException;
}

