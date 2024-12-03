/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryOrMultigetBaseType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultiOpType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="BaseRequestType", propOrder={"href"})
@XmlSeeAlso(value={GetPropertiesType.class, UpdateItemType.class, MultiOpType.class, AddItemType.class, FetchItemType.class, FreebusyReportType.class, DeleteItemType.class, CalendarQueryOrMultigetBaseType.class})
public class BaseRequestType {
    @XmlElement(required=true)
    protected String href;
    @XmlAttribute
    protected Integer id;

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public int getId() {
        if (this.id == null) {
            return 0;
        }
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }
}

