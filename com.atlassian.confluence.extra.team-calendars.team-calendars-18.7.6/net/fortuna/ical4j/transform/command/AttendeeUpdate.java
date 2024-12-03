/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.transform.Transformer;

public class AttendeeUpdate
implements Transformer<Component> {
    private final Attendee attendee;

    public AttendeeUpdate(Attendee attendee) {
        this.attendee = attendee;
    }

    @Override
    public Component transform(Component object) {
        PropertyList attendees = object.getProperties().getProperties("ATTENDEE");
        if (attendees.contains(this.attendee)) {
            attendees.remove(this.attendee);
        }
        attendees.add(this.attendee);
        object.getProperties().addAll(attendees);
        return object;
    }
}

