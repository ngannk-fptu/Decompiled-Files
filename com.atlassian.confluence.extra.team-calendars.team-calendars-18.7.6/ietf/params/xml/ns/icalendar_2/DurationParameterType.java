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

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="DurationParameterType", propOrder={"duration"})
public class DurationParameterType
extends BaseParameterType {
    @XmlElement(required=true)
    protected Duration duration;

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration value) {
        this.duration = value;
    }
}

