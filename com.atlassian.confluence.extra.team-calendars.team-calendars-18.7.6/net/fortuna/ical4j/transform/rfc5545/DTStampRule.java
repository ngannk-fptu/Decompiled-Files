/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545PropertyRule;

public class DTStampRule
implements Rfc5545PropertyRule<DtStamp> {
    @Override
    public void applyTo(DtStamp element) {
        if (element.getValue() != null && !element.isUtc()) {
            element.setUtc(true);
        }
    }

    @Override
    public Class<DtStamp> getSupportedType() {
        return DtStamp.class;
    }
}

