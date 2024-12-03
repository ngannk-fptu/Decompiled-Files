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
import org.oasis_open.docs.ws_calendar.ns.soap.BaseRequestType;
import org.oasis_open.docs.ws_calendar.ns.soap.UTCTimeRangeType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="FreebusyReportType", propOrder={"timeRange"})
public class FreebusyReportType
extends BaseRequestType {
    @XmlElement(name="time-range")
    protected UTCTimeRangeType timeRange;

    public UTCTimeRangeType getTimeRange() {
        return this.timeRange;
    }

    public void setTimeRange(UTCTimeRangeType value) {
        this.timeRange = value;
    }
}

