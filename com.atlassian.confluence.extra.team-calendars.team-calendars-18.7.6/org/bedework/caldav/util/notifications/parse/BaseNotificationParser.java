/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.parse;

import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Element;

public interface BaseNotificationParser {
    public QName getElement();

    public BaseNotificationType parse(Element var1) throws WebdavException;
}

