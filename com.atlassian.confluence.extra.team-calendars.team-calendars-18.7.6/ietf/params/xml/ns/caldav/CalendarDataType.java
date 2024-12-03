/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.caldav;

import ietf.params.xml.ns.caldav.CompType;
import ietf.params.xml.ns.caldav.ExpandType;
import ietf.params.xml.ns.caldav.LimitFreebusySetType;
import ietf.params.xml.ns.caldav.LimitRecurrenceSetType;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CalendarDataType", propOrder={"comp", "expand", "limitRecurrenceSet", "limitFreebusySet", "icalendar"})
public class CalendarDataType {
    protected CompType comp;
    protected ExpandType expand;
    @XmlElement(name="limit-recurrence-set")
    protected LimitRecurrenceSetType limitRecurrenceSet;
    @XmlElement(name="limit-freebusy-set")
    protected LimitFreebusySetType limitFreebusySet;
    @XmlElement(namespace="urn:ietf:params:xml:ns:icalendar-2.0")
    protected IcalendarType icalendar;
    @XmlAttribute(name="content-type")
    protected String contentType;
    @XmlAttribute
    protected String version;

    public CompType getComp() {
        return this.comp;
    }

    public void setComp(CompType value) {
        this.comp = value;
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

    public LimitFreebusySetType getLimitFreebusySet() {
        return this.limitFreebusySet;
    }

    public void setLimitFreebusySet(LimitFreebusySetType value) {
        this.limitFreebusySet = value;
    }

    public IcalendarType getIcalendar() {
        return this.icalendar;
    }

    public void setIcalendar(IcalendarType value) {
        this.icalendar = value;
    }

    public String getContentType() {
        if (this.contentType == null) {
            return "text/calendar";
        }
        return this.contentType;
    }

    public void setContentType(String value) {
        this.contentType = value;
    }

    public String getVersion() {
        if (this.version == null) {
            return "2.0";
        }
        return this.version;
    }

    public void setVersion(String value) {
        this.version = value;
    }
}

