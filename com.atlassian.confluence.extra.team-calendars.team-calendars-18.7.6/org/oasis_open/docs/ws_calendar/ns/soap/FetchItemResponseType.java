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
@XmlType(name="FetchItemResponseType", propOrder={"changeToken", "href", "icalendar"})
public class FetchItemResponseType
extends BaseResponseType {
    protected String changeToken;
    @XmlElement(required=true)
    protected String href;
    @XmlElement(namespace="urn:ietf:params:xml:ns:icalendar-2.0")
    protected IcalendarType icalendar;

    public String getChangeToken() {
        return this.changeToken;
    }

    public void setChangeToken(String value) {
        this.changeToken = value;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public IcalendarType getIcalendar() {
        return this.icalendar;
    }

    public void setIcalendar(IcalendarType value) {
        this.icalendar = value;
    }
}

