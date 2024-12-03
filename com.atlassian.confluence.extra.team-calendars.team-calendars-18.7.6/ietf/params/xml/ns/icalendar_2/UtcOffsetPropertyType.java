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
import ietf.params.xml.ns.icalendar_2.TzoffsetfromPropType;
import ietf.params.xml.ns.icalendar_2.TzoffsettoPropType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="UtcOffsetPropertyType", propOrder={"utcOffset"})
@XmlSeeAlso(value={TzoffsettoPropType.class, TzoffsetfromPropType.class})
public class UtcOffsetPropertyType
extends BasePropertyType {
    @XmlElement(name="utc-offset", required=true)
    protected String utcOffset;

    public String getUtcOffset() {
        return this.utcOffset;
    }

    public void setUtcOffset(String value) {
        this.utcOffset = value;
    }
}

