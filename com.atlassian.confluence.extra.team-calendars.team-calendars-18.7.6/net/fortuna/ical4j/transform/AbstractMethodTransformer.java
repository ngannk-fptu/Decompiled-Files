/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.transform.command.MethodUpdate;
import net.fortuna.ical4j.transform.command.SequenceIncrement;
import net.fortuna.ical4j.transform.command.UidUpdate;
import net.fortuna.ical4j.util.UidGenerator;

public abstract class AbstractMethodTransformer
implements Transformer<Calendar> {
    private final Method method;
    private final UidUpdate uidUpdate;
    private final SequenceIncrement sequenceIncrement;
    private final boolean incrementSequence;
    private final boolean sameUid;

    public AbstractMethodTransformer(Method method, UidGenerator uidGenerator, boolean sameUid, boolean incrementSequence) {
        this.method = method;
        this.uidUpdate = new UidUpdate(uidGenerator);
        this.sequenceIncrement = new SequenceIncrement();
        this.incrementSequence = incrementSequence;
        this.sameUid = sameUid;
    }

    @Override
    public Calendar transform(Calendar object) {
        MethodUpdate methodUpdate = new MethodUpdate(this.method);
        methodUpdate.transform(object);
        Property uid = null;
        for (CalendarComponent component : object.getComponents()) {
            this.uidUpdate.transform(component);
            if (uid == null) {
                uid = (Property)component.getProperty("UID");
            } else if (this.sameUid && !uid.equals(component.getProperty("UID"))) {
                throw new IllegalArgumentException("All components must share the same non-null UID");
            }
            if (!this.incrementSequence) continue;
            this.sequenceIncrement.transform(component);
        }
        return object;
    }
}

