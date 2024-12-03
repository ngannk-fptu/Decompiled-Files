/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.property;

import java.util.Collection;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class PropContainer
implements XmlSerializable,
DavConstants {
    private static Logger log = LoggerFactory.getLogger(PropContainer.class);

    public boolean addContent(Object contentEntry) {
        if (contentEntry instanceof PropEntry) {
            return this.addContent((PropEntry)contentEntry);
        }
        return false;
    }

    public abstract boolean addContent(PropEntry var1);

    public abstract boolean isEmpty();

    public abstract int getContentSize();

    public abstract Collection<? extends PropEntry> getContent();

    public abstract boolean contains(DavPropertyName var1);

    @Override
    public Element toXml(Document document) {
        Element prop = DomUtil.createElement(document, "prop", NAMESPACE);
        for (PropEntry propEntry : this.getContent()) {
            if (propEntry instanceof XmlSerializable) {
                prop.appendChild(((XmlSerializable)((Object)propEntry)).toXml(document));
                continue;
            }
            log.debug("Unexpected content in PropContainer: should be XmlSerializable.");
        }
        return prop;
    }
}

