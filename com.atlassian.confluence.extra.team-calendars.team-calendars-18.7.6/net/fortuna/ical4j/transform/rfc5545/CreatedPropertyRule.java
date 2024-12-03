/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.rfc5545;

import java.text.ParseException;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545PropertyRule;

public class CreatedPropertyRule
implements Rfc5545PropertyRule<Created> {
    private static final String UTC_MARKER = "Z";

    @Override
    public void applyTo(Created created) {
        if (created.isUtc() || created.getTimeZone() != null) {
            return;
        }
        try {
            created.setValue(created.getValue() + UTC_MARKER);
        }
        catch (ParseException parseException) {
            // empty catch block
        }
    }

    @Override
    public Class<Created> getSupportedType() {
        return Created.class;
    }
}

