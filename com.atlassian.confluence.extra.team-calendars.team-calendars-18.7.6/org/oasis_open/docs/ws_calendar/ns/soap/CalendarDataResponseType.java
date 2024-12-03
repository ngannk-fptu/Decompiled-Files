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
package org.oasis_open.docs.ws_calendar.ns.soap;

import ietf.params.xml.ns.icalendar_2.IcalendarType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CalendarDataResponseType", propOrder={"text", "icalendar"})
public class CalendarDataResponseType {
    protected String text;
    @XmlElement(namespace="urn:ietf:params:xml:ns:icalendar-2.0")
    protected IcalendarType icalendar;
    @XmlAttribute(name="content-type")
    protected String contentType;
    @XmlAttribute
    protected String version;

    public String getText() {
        return this.text;
    }

    public void setText(String value) {
        this.text = value;
    }

    public IcalendarType getIcalendar() {
        return this.icalendar;
    }

    public void setIcalendar(IcalendarType value) {
        this.icalendar = value;
    }

    public String getContentType() {
        if (this.contentType == null) {
            return "application/calendar+xml";
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

