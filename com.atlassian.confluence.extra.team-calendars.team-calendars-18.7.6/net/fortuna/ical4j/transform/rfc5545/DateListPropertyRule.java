/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545PropertyRule;
import net.fortuna.ical4j.transform.rfc5545.TzHelper;

public class DateListPropertyRule
implements Rfc5545PropertyRule<DateListProperty> {
    @Override
    public void applyTo(DateListProperty element) {
        TzHelper.correctTzParameterFrom(element);
    }

    @Override
    public Class<DateListProperty> getSupportedType() {
        return DateListProperty.class;
    }
}

