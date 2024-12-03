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

import ietf.params.xml.ns.icalendar_2.ArrayOfParameters;
import ietf.params.xml.ns.icalendar_2.AttachPropType;
import ietf.params.xml.ns.icalendar_2.CalAddressPropertyType;
import ietf.params.xml.ns.icalendar_2.CalscalePropType;
import ietf.params.xml.ns.icalendar_2.DateDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DurationPropType;
import ietf.params.xml.ns.icalendar_2.FreebusyPropType;
import ietf.params.xml.ns.icalendar_2.GeoPropType;
import ietf.params.xml.ns.icalendar_2.IntegerPropertyType;
import ietf.params.xml.ns.icalendar_2.LinkPropType;
import ietf.params.xml.ns.icalendar_2.RecurPropertyType;
import ietf.params.xml.ns.icalendar_2.RelatedToPropType;
import ietf.params.xml.ns.icalendar_2.RequestStatusPropType;
import ietf.params.xml.ns.icalendar_2.TextListPropertyType;
import ietf.params.xml.ns.icalendar_2.TextPropertyType;
import ietf.params.xml.ns.icalendar_2.TolerancePropType;
import ietf.params.xml.ns.icalendar_2.TriggerPropType;
import ietf.params.xml.ns.icalendar_2.UriPropertyType;
import ietf.params.xml.ns.icalendar_2.UtcDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.UtcOffsetPropertyType;
import ietf.params.xml.ns.icalendar_2.WsCalendarAttachType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="BasePropertyType", propOrder={"parameters"})
@XmlSeeAlso(value={CalscalePropType.class, FreebusyPropType.class, DurationPropType.class, GeoPropType.class, AttachPropType.class, TriggerPropType.class, RequestStatusPropType.class, TolerancePropType.class, WsCalendarAttachType.class, RelatedToPropType.class, LinkPropType.class, UtcOffsetPropertyType.class, CalAddressPropertyType.class, UtcDatetimePropertyType.class, UriPropertyType.class, RecurPropertyType.class, IntegerPropertyType.class, DatetimePropertyType.class, TextListPropertyType.class, DateDatetimePropertyType.class, TextPropertyType.class})
public abstract class BasePropertyType {
    protected ArrayOfParameters parameters;

    public ArrayOfParameters getParameters() {
        return this.parameters;
    }

    public void setParameters(ArrayOfParameters value) {
        this.parameters = value;
    }
}

