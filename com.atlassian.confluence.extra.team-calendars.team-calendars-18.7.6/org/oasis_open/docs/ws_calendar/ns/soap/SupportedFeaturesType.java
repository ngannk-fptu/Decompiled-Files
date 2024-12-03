/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarAccessFeatureType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SupportedFeaturesType", propOrder={"calendarAccessFeature"})
public class SupportedFeaturesType
extends GetPropertiesBasePropertyType {
    protected List<CalendarAccessFeatureType> calendarAccessFeature;

    public List<CalendarAccessFeatureType> getCalendarAccessFeature() {
        if (this.calendarAccessFeature == null) {
            this.calendarAccessFeature = new ArrayList<CalendarAccessFeatureType>();
        }
        return this.calendarAccessFeature;
    }
}

