/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.validate;

import java.util.Collections;
import net.fortuna.ical4j.extensions.property.CalStart;
import net.fortuna.ical4j.extensions.property.WrCalDesc;
import net.fortuna.ical4j.extensions.property.WrCalName;
import net.fortuna.ical4j.extensions.property.WrRelCalId;
import net.fortuna.ical4j.extensions.property.WrTimezone;
import net.fortuna.ical4j.validate.CalendarValidatorImpl;
import net.fortuna.ical4j.validate.ValidationRule;

public class ExtensionsCalendarValidator
extends CalendarValidatorImpl {
    public ExtensionsCalendarValidator(ValidationRule ... rules) {
        super(rules);
        Collections.addAll(this.calendarProperties, WrTimezone.class, WrRelCalId.class, CalStart.class, WrCalDesc.class, WrCalName.class);
    }
}

