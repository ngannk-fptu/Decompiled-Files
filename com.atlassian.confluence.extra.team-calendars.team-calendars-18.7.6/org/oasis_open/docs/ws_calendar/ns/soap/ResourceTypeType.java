/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarCollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.CollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.InboxType;
import org.oasis_open.docs.ws_calendar.ns.soap.OutboxType;
import org.oasis_open.docs.ws_calendar.ns.soap.XresourceType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ResourceTypeType", propOrder={"calendarCollectionOrCollectionOrInbox"})
public class ResourceTypeType
extends GetPropertiesBasePropertyType {
    @XmlElements(value={@XmlElement(name="collection", type=CollectionType.class), @XmlElement(name="inbox", type=InboxType.class), @XmlElement(name="calendarCollection", type=CalendarCollectionType.class), @XmlElement(name="outbox", type=OutboxType.class), @XmlElement(name="xresource", type=XresourceType.class)})
    protected List<Object> calendarCollectionOrCollectionOrInbox;

    public List<Object> getCalendarCollectionOrCollectionOrInbox() {
        if (this.calendarCollectionOrCollectionOrInbox == null) {
            this.calendarCollectionOrCollectionOrInbox = new ArrayList<Object>();
        }
        return this.calendarCollectionOrCollectionOrInbox;
    }
}

