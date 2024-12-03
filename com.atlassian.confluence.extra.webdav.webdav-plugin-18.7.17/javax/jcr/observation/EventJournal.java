/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.observation;

import javax.jcr.observation.EventIterator;

public interface EventJournal
extends EventIterator {
    public void skipTo(long var1);
}

