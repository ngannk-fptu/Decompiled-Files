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
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseRequestType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarMultigetType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ArrayOfOperations", propOrder={"getPropertiesOrFreebusyReportOrCalendarQuery"})
public class ArrayOfOperations {
    @XmlElements(value={@XmlElement(name="addItem", type=AddItemType.class), @XmlElement(name="calendarMultiget", type=CalendarMultigetType.class), @XmlElement(name="freebusyReport", type=FreebusyReportType.class), @XmlElement(name="updateItem", type=UpdateItemType.class), @XmlElement(name="fetchItem", type=FetchItemType.class), @XmlElement(name="getProperties", type=GetPropertiesType.class), @XmlElement(name="deleteItem", type=DeleteItemType.class), @XmlElement(name="calendarQuery", type=CalendarQueryType.class)})
    protected List<BaseRequestType> getPropertiesOrFreebusyReportOrCalendarQuery;

    public List<BaseRequestType> getGetPropertiesOrFreebusyReportOrCalendarQuery() {
        if (this.getPropertiesOrFreebusyReportOrCalendarQuery == null) {
            this.getPropertiesOrFreebusyReportOrCalendarQuery = new ArrayList<BaseRequestType>();
        }
        return this.getPropertiesOrFreebusyReportOrCalendarQuery;
    }
}

