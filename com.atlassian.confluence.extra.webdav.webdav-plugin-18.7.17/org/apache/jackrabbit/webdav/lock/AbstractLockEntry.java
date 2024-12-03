/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.lock;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.lock.LockEntry;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractLockEntry
implements LockEntry,
DavConstants {
    private static Logger log = LoggerFactory.getLogger(AbstractLockEntry.class);

    @Override
    public Element toXml(Document document) {
        Element entry = DomUtil.createElement(document, "lockentry", NAMESPACE);
        entry.appendChild(this.getScope().toXml(document));
        entry.appendChild(this.getType().toXml(document));
        return entry;
    }
}

