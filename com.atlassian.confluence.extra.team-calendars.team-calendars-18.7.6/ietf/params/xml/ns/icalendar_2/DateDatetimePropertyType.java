/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.DtendPropType;
import ietf.params.xml.ns.icalendar_2.DtstartPropType;
import ietf.params.xml.ns.icalendar_2.DuePropType;
import ietf.params.xml.ns.icalendar_2.ExdatePropType;
import ietf.params.xml.ns.icalendar_2.RdatePropType;
import ietf.params.xml.ns.icalendar_2.RecurrenceIdPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkRegistrationEndPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkRegistrationStartPropType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="DateDatetimePropertyType", propOrder={"dateTime", "date"})
@XmlSeeAlso(value={RdatePropType.class, XBedeworkRegistrationEndPropType.class, RecurrenceIdPropType.class, DtstartPropType.class, ExdatePropType.class, XBedeworkRegistrationStartPropType.class, DuePropType.class, DtendPropType.class})
public class DateDatetimePropertyType
extends BasePropertyType {
    @XmlElement(name="date-time")
    protected XMLGregorianCalendar dateTime;
    @XmlSchemaType(name="date")
    protected XMLGregorianCalendar date;

    public XMLGregorianCalendar getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(XMLGregorianCalendar value) {
        this.dateTime = value;
    }

    public XMLGregorianCalendar getDate() {
        return this.date;
    }

    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }
}

