/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PeriodType", propOrder={"start", "end", "duration"})
public class PeriodType {
    @XmlElement(required=true)
    protected XMLGregorianCalendar start;
    protected XMLGregorianCalendar end;
    protected String duration;

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

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String value) {
        this.duration = value;
    }
}

