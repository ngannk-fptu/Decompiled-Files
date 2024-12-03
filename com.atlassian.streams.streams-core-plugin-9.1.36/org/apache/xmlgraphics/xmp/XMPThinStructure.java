/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import org.apache.xmlgraphics.xmp.XMPProperty;
import org.apache.xmlgraphics.xmp.XMPStructure;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class XMPThinStructure
extends XMPStructure {
    @Override
    public void toSAX(ContentHandler handler) throws SAXException {
        for (Object o : this.properties.values()) {
            XMPProperty prop = (XMPProperty)o;
            prop.toSAX(handler);
        }
    }
}

