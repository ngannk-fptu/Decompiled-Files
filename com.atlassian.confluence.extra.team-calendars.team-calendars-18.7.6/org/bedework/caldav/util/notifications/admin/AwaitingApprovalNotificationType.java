/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.admin;

import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.admin.AdminNotificationType;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class AwaitingApprovalNotificationType
extends AdminNotificationType {
    @Override
    public QName getElementName() {
        return BedeworkServerTags.awaitingApproval;
    }

    @Override
    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(this.getElementName());
        this.bodyToXml(xml);
        xml.closeTag(this.getElementName());
    }
}

