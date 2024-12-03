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
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarDataResponseType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="MultistatusPropElementType", propOrder={"calendarData"})
public class MultistatusPropElementType {
    @XmlElement(name="calendar-data")
    protected CalendarDataResponseType calendarData;

    public CalendarDataResponseType getCalendarData() {
        return this.calendarData;
    }

    public void setCalendarData(CalendarDataResponseType value) {
        this.calendarData = value;
    }
}

