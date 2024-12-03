/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545PropertyRule;
import net.fortuna.ical4j.transform.rfc5545.TzHelper;

public class DatePropertyRule
implements Rfc5545PropertyRule<DateProperty> {
    @Override
    public void applyTo(DateProperty element) {
        TzHelper.correctTzParameterFrom(element);
        if (!element.isUtc() || element.getParameter("TZID") == null) {
            return;
        }
        element.getParameters().removeAll("TZID");
        element.setUtc(true);
    }

    @Override
    public Class<DateProperty> getSupportedType() {
        return DateProperty.class;
    }
}

