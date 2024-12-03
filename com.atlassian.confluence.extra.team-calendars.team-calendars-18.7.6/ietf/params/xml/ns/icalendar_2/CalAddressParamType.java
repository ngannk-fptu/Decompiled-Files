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

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import ietf.params.xml.ns.icalendar_2.SentByParamType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CalAddressParamType", propOrder={"calAddress"})
@XmlSeeAlso(value={SentByParamType.class})
public class CalAddressParamType
extends BaseParameterType {
    @XmlElement(name="cal-address", required=true)
    protected String calAddress;

    public String getCalAddress() {
        return this.calAddress;
    }

    public void setCalAddress(String value) {
        this.calAddress = value;
    }
}

