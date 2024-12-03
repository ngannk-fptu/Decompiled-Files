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
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.oasis_open.docs.ws_calendar.ns.soap.CreationDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.LastModifiedDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MinDateTimeType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="DateTimePropertyType", propOrder={"dateTime"})
@XmlSeeAlso(value={LastModifiedDateTimeType.class, MinDateTimeType.class, CreationDateTimeType.class, MaxDateTimeType.class})
public class DateTimePropertyType
extends GetPropertiesBasePropertyType {
    @XmlElement(required=true)
    @XmlSchemaType(name="dateTime")
    protected XMLGregorianCalendar dateTime;

    public XMLGregorianCalendar getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(XMLGregorianCalendar value) {
        this.dateTime = value;
    }
}

