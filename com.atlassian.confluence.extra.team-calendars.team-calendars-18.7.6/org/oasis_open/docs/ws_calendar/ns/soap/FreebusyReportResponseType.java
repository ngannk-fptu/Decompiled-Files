/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import ietf.params.xml.ns.icalendar_2.IcalendarType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="FreebusyReportResponseType", propOrder={"icalendar"})
public class FreebusyReportResponseType
extends BaseResponseType {
    @XmlElement(namespace="urn:ietf:params:xml:ns:icalendar-2.0")
    protected IcalendarType icalendar;

    public IcalendarType getIcalendar() {
        return this.icalendar;
    }

    public void setIcalendar(IcalendarType value) {
        this.icalendar = value;
    }
}

