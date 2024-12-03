/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.transform.Transformer;

public class SequenceIncrement
implements Transformer<CalendarComponent> {
    @Override
    public CalendarComponent transform(CalendarComponent object) {
        PropertyList<Property> compProps = object.getProperties();
        Sequence sequence = (Sequence)compProps.getProperty("SEQUENCE");
        if (sequence == null) {
            compProps.add(new Sequence(0));
        } else {
            compProps.remove(sequence);
            compProps.add(new Sequence(sequence.getSequenceNo() + 1));
        }
        return object;
    }
}

