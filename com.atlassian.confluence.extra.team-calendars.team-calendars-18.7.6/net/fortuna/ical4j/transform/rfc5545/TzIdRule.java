/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545PropertyRule;
import net.fortuna.ical4j.transform.rfc5545.TzHelper;

public class TzIdRule
implements Rfc5545PropertyRule<TzId> {
    @Override
    public void applyTo(TzId element) {
        TzHelper.correctTzValueOf(element);
    }

    @Override
    public Class<TzId> getSupportedType() {
        return TzId.class;
    }
}

