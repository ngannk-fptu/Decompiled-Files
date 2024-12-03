/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.ical4j.transformer;

import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.AbstractMethodTransformer;
import net.fortuna.ical4j.util.UidGenerator;

public class PublishTransformer
extends AbstractMethodTransformer {
    public PublishTransformer(UidGenerator uidGenerator, boolean incrementSequence) {
        super(Method.PUBLISH, uidGenerator, false, incrementSequence);
    }
}

