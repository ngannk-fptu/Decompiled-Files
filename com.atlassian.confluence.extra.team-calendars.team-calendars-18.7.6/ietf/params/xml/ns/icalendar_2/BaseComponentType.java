/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.ArrayOfComponents;
import ietf.params.xml.ns.icalendar_2.ArrayOfProperties;
import ietf.params.xml.ns.icalendar_2.AvailableType;
import ietf.params.xml.ns.icalendar_2.DaylightType;
import ietf.params.xml.ns.icalendar_2.StandardType;
import ietf.params.xml.ns.icalendar_2.ValarmType;
import ietf.params.xml.ns.icalendar_2.VavailabilityType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import ietf.params.xml.ns.icalendar_2.VeventType;
import ietf.params.xml.ns.icalendar_2.VfreebusyType;
import ietf.params.xml.ns.icalendar_2.VjournalType;
import ietf.params.xml.ns.icalendar_2.VpollType;
import ietf.params.xml.ns.icalendar_2.VtimezoneType;
import ietf.params.xml.ns.icalendar_2.VtodoType;
import ietf.params.xml.ns.icalendar_2.WsCalendarGluonType;
import ietf.params.xml.ns.icalendar_2.WsCalendarIntervalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="BaseComponentType", propOrder={"properties", "components"})
@XmlSeeAlso(value={VtodoType.class, VjournalType.class, DaylightType.class, WsCalendarIntervalType.class, VfreebusyType.class, VcalendarType.class, ValarmType.class, StandardType.class, VpollType.class, AvailableType.class, VavailabilityType.class, VtimezoneType.class, VeventType.class, WsCalendarGluonType.class})
public abstract class BaseComponentType {
    protected ArrayOfProperties properties;
    protected ArrayOfComponents components;

    public ArrayOfProperties getProperties() {
        return this.properties;
    }

    public void setProperties(ArrayOfProperties value) {
        this.properties = value;
    }

    public ArrayOfComponents getComponents() {
        return this.components;
    }

    public void setComponents(ArrayOfComponents value) {
        this.components = value;
    }
}

