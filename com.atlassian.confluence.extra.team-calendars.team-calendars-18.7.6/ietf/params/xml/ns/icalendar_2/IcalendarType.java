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

import ietf.params.xml.ns.icalendar_2.VcalendarType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="IcalendarType", propOrder={"vcalendar"})
public class IcalendarType {
    @XmlElement(required=true)
    protected List<VcalendarType> vcalendar;

    public List<VcalendarType> getVcalendar() {
        if (this.vcalendar == null) {
            this.vcalendar = new ArrayList<VcalendarType>();
        }
        return this.vcalendar;
    }
}

