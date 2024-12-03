/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="GeoPropType", propOrder={"latitude", "longitude"})
public class GeoPropType
extends BasePropertyType {
    protected float latitude;
    protected float longitude;

    public float getLatitude() {
        return this.latitude;
    }

    public void setLatitude(float value) {
        this.latitude = value;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(float value) {
        this.longitude = value;
    }
}

