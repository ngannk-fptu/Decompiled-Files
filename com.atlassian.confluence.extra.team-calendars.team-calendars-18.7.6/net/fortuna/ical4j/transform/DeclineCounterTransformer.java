/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.transform.AbstractMethodTransformer;
import net.fortuna.ical4j.transform.command.OrganizerUpdate;
import net.fortuna.ical4j.util.UidGenerator;

public class DeclineCounterTransformer
extends AbstractMethodTransformer {
    private final OrganizerUpdate organizerUpdate;

    public DeclineCounterTransformer(Organizer organizer, UidGenerator uidGenerator) {
        super(Method.DECLINE_COUNTER, uidGenerator, true, false);
        this.organizerUpdate = new OrganizerUpdate(organizer);
    }

    @Override
    public Calendar transform(Calendar object) {
        if (!Method.COUNTER.equals(object.getMethod())) {
            throw new IllegalArgumentException("Expecting COUNTER method in source");
        }
        for (CalendarComponent component : object.getComponents()) {
            this.organizerUpdate.transform(component);
        }
        return super.transform(object);
    }
}

