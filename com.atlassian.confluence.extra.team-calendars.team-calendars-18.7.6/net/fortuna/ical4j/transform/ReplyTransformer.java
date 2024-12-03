/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.AbstractMethodTransformer;
import net.fortuna.ical4j.util.UidGenerator;

public class ReplyTransformer
extends AbstractMethodTransformer {
    public ReplyTransformer(UidGenerator uidGenerator) {
        super(Method.REPLY, uidGenerator, true, false);
    }

    @Override
    public Calendar transform(Calendar object) {
        if (!Method.REQUEST.equals(object.getMethod())) {
            throw new IllegalArgumentException("Expecting REQUEST method in source");
        }
        return super.transform(object);
    }
}

