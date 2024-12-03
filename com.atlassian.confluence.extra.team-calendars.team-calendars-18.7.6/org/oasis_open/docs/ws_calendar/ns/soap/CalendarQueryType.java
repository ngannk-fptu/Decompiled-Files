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
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryOrMultigetBaseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FilterType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CalendarQueryType", propOrder={"depth", "filter"})
public class CalendarQueryType
extends CalendarQueryOrMultigetBaseType {
    @XmlElement(required=true)
    protected String depth;
    @XmlElement(required=true)
    protected FilterType filter;

    public String getDepth() {
        return this.depth;
    }

    public void setDepth(String value) {
        this.depth = value;
    }

    public FilterType getFilter() {
        return this.filter;
    }

    public void setFilter(FilterType value) {
        this.filter = value;
    }
}

