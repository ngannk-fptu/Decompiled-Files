/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import ietf.params.xml.ns.icalendar_2.IcalendarType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.AllpropType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseRequestType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarMultigetType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryType;
import org.oasis_open.docs.ws_calendar.ns.soap.ExpandType;
import org.oasis_open.docs.ws_calendar.ns.soap.LimitRecurrenceSetType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CalendarQueryOrMultigetBaseType", propOrder={"allprop", "icalendar", "expand", "limitRecurrenceSet"})
@XmlSeeAlso(value={CalendarQueryType.class, CalendarMultigetType.class})
public class CalendarQueryOrMultigetBaseType
extends BaseRequestType {
    protected AllpropType allprop;
    @XmlElement(namespace="urn:ietf:params:xml:ns:icalendar-2.0")
    protected IcalendarType icalendar;
    protected ExpandType expand;
    protected LimitRecurrenceSetType limitRecurrenceSet;

    public AllpropType getAllprop() {
        return this.allprop;
    }

    public void setAllprop(AllpropType value) {
        this.allprop = value;
    }

    public IcalendarType getIcalendar() {
        return this.icalendar;
    }

    public void setIcalendar(IcalendarType value) {
        this.icalendar = value;
    }

    public ExpandType getExpand() {
        return this.expand;
    }

    public void setExpand(ExpandType value) {
        this.expand = value;
    }

    public LimitRecurrenceSetType getLimitRecurrenceSet() {
        return this.limitRecurrenceSet;
    }

    public void setLimitRecurrenceSet(LimitRecurrenceSetType value) {
        this.limitRecurrenceSet = value;
    }
}

