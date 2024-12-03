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
import ietf.params.xml.ns.icalendar_2.PercentCompletePropType;
import ietf.params.xml.ns.icalendar_2.PollItemIdPropType;
import ietf.params.xml.ns.icalendar_2.PriorityPropType;
import ietf.params.xml.ns.icalendar_2.RepeatPropType;
import ietf.params.xml.ns.icalendar_2.SequencePropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkMaxTicketsPerUserPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkMaxTicketsPropType;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="IntegerPropertyType", propOrder={"integer"})
@XmlSeeAlso(value={PercentCompletePropType.class, PriorityPropType.class, PollItemIdPropType.class, XBedeworkMaxTicketsPropType.class, XBedeworkMaxTicketsPerUserPropType.class, SequencePropType.class, RepeatPropType.class})
public class IntegerPropertyType
extends BasePropertyType {
    @XmlElement(required=true)
    protected BigInteger integer;

    public BigInteger getInteger() {
        return this.integer;
    }

    public void setInteger(BigInteger value) {
        this.integer = value;
    }
}

