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
import ietf.params.xml.ns.icalendar_2.DelegatedFromParamType;
import ietf.params.xml.ns.icalendar_2.DelegatedToParamType;
import ietf.params.xml.ns.icalendar_2.MemberParamType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CalAddressListParamType", propOrder={"calAddress"})
@XmlSeeAlso(value={DelegatedFromParamType.class, MemberParamType.class, DelegatedToParamType.class})
public class CalAddressListParamType
extends BaseParameterType {
    @XmlElement(name="cal-address", required=true)
    protected List<String> calAddress;

    public List<String> getCalAddress() {
        if (this.calAddress == null) {
            this.calAddress = new ArrayList<String>();
        }
        return this.calAddress;
    }
}

