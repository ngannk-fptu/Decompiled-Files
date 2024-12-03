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

public class PublishTransformer
extends AbstractMethodTransformer {
    private final OrganizerUpdate organizerUpdate;

    public PublishTransformer(Organizer organizer, UidGenerator uidGenerator, boolean incrementSequence) {
        super(Method.PUBLISH, uidGenerator, false, incrementSequence);
        this.organizerUpdate = new OrganizerUpdate(organizer);
    }

    @Override
    public Calendar transform(Calendar object) {
        for (CalendarComponent component : object.getComponents()) {
            this.organizerUpdate.transform(component);
        }
        return super.transform(object);
    }
}

