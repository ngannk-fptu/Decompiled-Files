/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.admin;

import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.admin.AdminNotificationType;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class ApprovalResponseNotificationType
extends AdminNotificationType {
    private boolean accepted;

    public void setAccepted(boolean val) {
        this.accepted = val;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    @Override
    public QName getElementName() {
        return BedeworkServerTags.approvalResponse;
    }

    @Override
    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(this.getElementName());
        this.bodyToXml(xml);
        xml.property(BedeworkServerTags.accepted, String.valueOf(this.getAccepted()));
        xml.closeTag(this.getElementName());
    }
}

