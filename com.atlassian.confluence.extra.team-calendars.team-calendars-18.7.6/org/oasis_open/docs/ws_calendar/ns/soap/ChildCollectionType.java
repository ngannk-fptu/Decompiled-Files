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

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ChildCollectionType", propOrder={"href", "calendarCollectionOrCollection"})
public class ChildCollectionType
extends GetPropertiesBasePropertyType {
    @XmlElement(required=true)
    protected String href;
    @XmlElements(value={@XmlElement(name="calendarCollection", type=CalendarCollectionType.class), @XmlElement(name="collection", type=CollectionType.class)})
    protected List<Object> calendarCollectionOrCollection;

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public List<Object> getCalendarCollectionOrCollection() {
        if (this.calendarCollectionOrCollection == null) {
            this.calendarCollectionOrCollection = new ArrayList<Object>();
        }
        return this.calendarCollectionOrCollection;
    }
}

