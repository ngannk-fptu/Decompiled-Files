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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.ArrayOfHrefs;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryOrMultigetBaseType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CalendarMultigetType", propOrder={"hrefs"})
public class CalendarMultigetType
extends CalendarQueryOrMultigetBaseType {
    @XmlElement(required=true)
    protected ArrayOfHrefs hrefs;

    public ArrayOfHrefs getHrefs() {
        return this.hrefs;
    }

    public void setHrefs(ArrayOfHrefs value) {
        this.hrefs = value;
    }
}

