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

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.ToleranceValueType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="TolerancePropType", propOrder={"tolerate"})
public class TolerancePropType
extends BasePropertyType {
    @XmlElement(required=true)
    protected ToleranceValueType tolerate;

    public ToleranceValueType getTolerate() {
        return this.tolerate;
    }

    public void setTolerate(ToleranceValueType value) {
        this.tolerate = value;
    }
}

