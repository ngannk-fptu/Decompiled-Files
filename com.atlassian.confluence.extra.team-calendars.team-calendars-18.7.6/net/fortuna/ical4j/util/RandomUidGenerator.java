/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.util.UUID;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.UidGenerator;

public class RandomUidGenerator
implements UidGenerator {
    @Override
    public Uid generateUid() {
        return new Uid(UUID.randomUUID().toString());
    }
}

