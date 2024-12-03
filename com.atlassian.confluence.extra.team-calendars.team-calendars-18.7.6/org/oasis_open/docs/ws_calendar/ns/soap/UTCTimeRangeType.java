/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.oasis_open.docs.ws_calendar.ns.soap.ExpandType;
import org.oasis_open.docs.ws_calendar.ns.soap.LimitFreebusySetType;
import org.oasis_open.docs.ws_calendar.ns.soap.LimitRecurrenceSetType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="UTCTimeRangeType", propOrder={"start", "end"})
@XmlSeeAlso(value={LimitFreebusySetType.class, ExpandType.class, LimitRecurrenceSetType.class})
public class UTCTimeRangeType {
    protected XMLGregorianCalendar start;
    protected XMLGregorianCalendar end;

    public XMLGregorianCalendar getStart() {
        return this.start;
    }

    public void setStart(XMLGregorianCalendar value) {
        this.start = value;
    }

    public XMLGregorianCalendar getEnd() {
        return this.end;
    }

    public void setEnd(XMLGregorianCalendar value) {
        this.end = value;
    }
}

