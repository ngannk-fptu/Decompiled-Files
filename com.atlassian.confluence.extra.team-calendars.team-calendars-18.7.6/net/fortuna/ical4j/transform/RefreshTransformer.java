/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform;

import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.AbstractMethodTransformer;
import net.fortuna.ical4j.util.UidGenerator;

public class RefreshTransformer
extends AbstractMethodTransformer {
    public RefreshTransformer(UidGenerator uidGenerator) {
        super(Method.REFRESH, uidGenerator, true, false);
    }
}

