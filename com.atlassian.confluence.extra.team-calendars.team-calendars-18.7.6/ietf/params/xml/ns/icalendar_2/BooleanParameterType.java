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
import ietf.params.xml.ns.icalendar_2.RsvpParamType;
import ietf.params.xml.ns.icalendar_2.StayInformedParameterType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="BooleanParameterType", propOrder={"_boolean"})
@XmlSeeAlso(value={RsvpParamType.class, StayInformedParameterType.class})
public class BooleanParameterType
extends BaseParameterType {
    @XmlElement(name="boolean")
    protected boolean _boolean;

    public boolean isBoolean() {
        return this._boolean;
    }

    public void setBoolean(boolean value) {
        this._boolean = value;
    }
}

