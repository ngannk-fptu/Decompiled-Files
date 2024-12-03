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

import ietf.params.xml.ns.icalendar_2.AcceptResponsePropType;
import ietf.params.xml.ns.icalendar_2.ActionPropType;
import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.BusytypePropType;
import ietf.params.xml.ns.icalendar_2.ClassPropType;
import ietf.params.xml.ns.icalendar_2.CommentPropType;
import ietf.params.xml.ns.icalendar_2.ContactPropType;
import ietf.params.xml.ns.icalendar_2.DescriptionPropType;
import ietf.params.xml.ns.icalendar_2.LocationPropType;
import ietf.params.xml.ns.icalendar_2.MethodPropType;
import ietf.params.xml.ns.icalendar_2.PollModePropType;
import ietf.params.xml.ns.icalendar_2.PollPropertiesPropType;
import ietf.params.xml.ns.icalendar_2.ProdidPropType;
import ietf.params.xml.ns.icalendar_2.StatusPropType;
import ietf.params.xml.ns.icalendar_2.SummaryPropType;
import ietf.params.xml.ns.icalendar_2.TranspPropType;
import ietf.params.xml.ns.icalendar_2.TzidPropType;
import ietf.params.xml.ns.icalendar_2.TznamePropType;
import ietf.params.xml.ns.icalendar_2.UidPropType;
import ietf.params.xml.ns.icalendar_2.VersionPropType;
import ietf.params.xml.ns.icalendar_2.WsCalendarTypeType;
import ietf.params.xml.ns.icalendar_2.XBedeworkCostPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkExsynchEndtzidPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkExsynchLastmodPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkExsynchStarttzidPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkInstanceOnlyPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkWaitListLimitPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkWrapperPropType;
import ietf.params.xml.ns.icalendar_2.XBwContactPropType;
import ietf.params.xml.ns.icalendar_2.XBwLocationPropType;
import ietf.params.xml.ns.icalendar_2.XMicrosoftCdoBusystatusPropType;
import ietf.params.xml.ns.icalendar_2.XMicrosoftCdoIntendedstatusPropType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="TextPropertyType", propOrder={"text"})
@XmlSeeAlso(value={ActionPropType.class, TzidPropType.class, MethodPropType.class, XBedeworkWrapperPropType.class, ProdidPropType.class, VersionPropType.class, StatusPropType.class, XBedeworkInstanceOnlyPropType.class, XBwLocationPropType.class, XMicrosoftCdoBusystatusPropType.class, XBwContactPropType.class, UidPropType.class, XBedeworkExsynchLastmodPropType.class, XBedeworkExsynchStarttzidPropType.class, PollModePropType.class, PollPropertiesPropType.class, XBedeworkCostPropType.class, XBedeworkExsynchEndtzidPropType.class, ContactPropType.class, XBedeworkWaitListLimitPropType.class, WsCalendarTypeType.class, TranspPropType.class, XMicrosoftCdoIntendedstatusPropType.class, DescriptionPropType.class, TznamePropType.class, AcceptResponsePropType.class, ClassPropType.class, SummaryPropType.class, CommentPropType.class, LocationPropType.class, BusytypePropType.class})
public class TextPropertyType
extends BasePropertyType {
    @XmlElement(required=true)
    protected String text;

    public String getText() {
        return this.text;
    }

    public void setText(String value) {
        this.text = value;
    }
}

