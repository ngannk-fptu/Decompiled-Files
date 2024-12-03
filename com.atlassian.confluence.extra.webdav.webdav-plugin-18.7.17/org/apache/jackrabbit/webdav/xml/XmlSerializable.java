/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XmlSerializable {
    public Element toXml(Document var1);
}

