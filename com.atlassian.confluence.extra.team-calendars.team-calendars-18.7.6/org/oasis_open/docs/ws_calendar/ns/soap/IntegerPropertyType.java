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
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxAttendeesPerInstanceType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxInstancesType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxResourceSizeType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="IntegerPropertyType", propOrder={"integer"})
@XmlSeeAlso(value={MaxInstancesType.class, MaxAttendeesPerInstanceType.class, MaxResourceSizeType.class})
public class IntegerPropertyType
extends GetPropertiesBasePropertyType {
    @XmlElement(required=true)
    protected BigInteger integer;

    public BigInteger getInteger() {
        return this.integer;
    }

    public void setInteger(BigInteger value) {
        this.integer = value;
    }
}

