/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.rfc5545;

import java.util.Calendar;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545ComponentRule;

public class VEventRule
implements Rfc5545ComponentRule<VEvent> {
    @Override
    public void applyTo(VEvent element) {
        PropertyList dtStamps;
        Object start = element.getProperty("DTSTART");
        Object end = element.getProperty("DTEND");
        Object duration = element.getProperty("DURATION");
        if (end != null && duration != null && ((Content)end).getValue() != null && duration != null) {
            element.getProperties().remove((Property)duration);
        }
        if (start != null && end != null) {
            Object startType = ((Property)start).getParameter("VALUE");
            Object endType = ((Property)end).getParameter("VALUE");
            if (startType != null && endType != null && ((Content)startType).getValue().equals(Value.DATE.getValue()) && ((Content)endType).getValue().equals(Value.DATE.getValue()) && ((Content)start).getValue().equals(((Content)end).getValue()) && end instanceof DateProperty) {
                DateProperty endDate = (DateProperty)end;
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate.getDate());
                cal.add(5, 1);
                endDate.setDate(new Date(cal.getTime()));
            }
        }
        if ((dtStamps = element.getProperties("DTSTAMP")) == null || dtStamps.isEmpty()) {
            element.getProperties().add(new DtStamp());
        }
    }

    @Override
    public Class<VEvent> getSupportedType() {
        return VEvent.class;
    }
}

