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
import ietf.params.xml.ns.icalendar_2.ExrulePropType;
import ietf.params.xml.ns.icalendar_2.RecurType;
import ietf.params.xml.ns.icalendar_2.RrulePropType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="RecurPropertyType", propOrder={"recur"})
@XmlSeeAlso(value={ExrulePropType.class, RrulePropType.class})
public class RecurPropertyType
extends BasePropertyType {
    @XmlElement(required=true)
    protected RecurType recur;

    public RecurType getRecur() {
        return this.recur;
    }

    public void setRecur(RecurType value) {
        this.recur = value;
    }
}

