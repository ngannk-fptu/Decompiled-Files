/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.eventreg;

import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.eventreg.EventregBaseNotificationType;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class EventregRegisteredNotificationType
extends EventregBaseNotificationType {
    private int numTicketsRequested;
    private int numTickets;

    @Override
    public QName getElementName() {
        return BedeworkServerTags.eventregRegistered;
    }

    public void setNumTicketsRequested(int val) {
        this.numTickets = val;
    }

    public int getNumTicketsRequested() {
        return this.numTickets;
    }

    public void setNumTickets(int val) {
        this.numTickets = val;
    }

    public int getNumTickets() {
        return this.numTickets;
    }

    @Override
    protected void bodyToXml(XmlEmit xml) throws Throwable {
        super.bodyToXml(xml);
        xml.property(BedeworkServerTags.eventregNumTicketsRequested, String.valueOf(this.getNumTicketsRequested()));
        xml.property(BedeworkServerTags.eventregNumTickets, String.valueOf(this.getNumTickets()));
    }

    @Override
    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(this.getElementName());
        this.bodyToXml(xml);
        xml.closeTag(this.getElementName());
    }
}

