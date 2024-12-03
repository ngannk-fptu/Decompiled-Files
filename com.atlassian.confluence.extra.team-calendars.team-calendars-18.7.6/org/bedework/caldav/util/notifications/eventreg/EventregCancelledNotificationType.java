/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.eventreg;

import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.eventreg.EventregBaseNotificationType;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class EventregCancelledNotificationType
extends EventregBaseNotificationType {
    @Override
    public QName getElementName() {
        return BedeworkServerTags.eventregCancelled;
    }

    @Override
    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(this.getElementName());
        this.bodyToXml(xml);
        xml.closeTag(this.getElementName());
    }
}

