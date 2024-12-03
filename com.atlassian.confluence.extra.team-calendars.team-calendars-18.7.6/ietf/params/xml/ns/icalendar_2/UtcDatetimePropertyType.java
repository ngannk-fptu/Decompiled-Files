/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.CompletedPropType;
import ietf.params.xml.ns.icalendar_2.CreatedPropType;
import ietf.params.xml.ns.icalendar_2.DtstampPropType;
import ietf.params.xml.ns.icalendar_2.LastModifiedPropType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="UtcDatetimePropertyType", propOrder={"utcDateTime"})
@XmlSeeAlso(value={CompletedPropType.class, DtstampPropType.class, CreatedPropType.class, LastModifiedPropType.class})
public class UtcDatetimePropertyType
extends BasePropertyType {
    @XmlElement(name="utc-date-time", required=true)
    protected XMLGregorianCalendar utcDateTime;

    public XMLGregorianCalendar getUtcDateTime() {
        return this.utcDateTime;
    }

    public void setUtcDateTime(XMLGregorianCalendar value) {
        this.utcDateTime = value;
    }
}

