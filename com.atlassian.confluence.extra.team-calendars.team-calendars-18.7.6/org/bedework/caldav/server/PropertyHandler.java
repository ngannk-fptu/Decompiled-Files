/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.io.Serializable;
import java.util.Map;
import javax.xml.namespace.QName;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsNode;

public abstract class PropertyHandler
implements Serializable {
    public abstract Map<QName, WebdavNsNode.PropertyTagEntry> getPropertyNames();

    public boolean knownProperty(QName tag) throws WebdavException {
        return this.getPropertyNames().get(tag) != null;
    }

    public static enum PropertyType {
        generalProperty,
        principalProperty,
        userProperty,
        groupProperty,
        collectionProperty,
        folderProperty,
        calendarProperty;

    }
}

