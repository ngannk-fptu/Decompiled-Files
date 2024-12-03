/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.util.IdGenerator;

public class SimpleIdGenerator
implements IdGenerator {
    private final AtomicLong mostSigBits = new AtomicLong(0L);
    private final AtomicLong leastSigBits = new AtomicLong(0L);

    @Override
    public UUID generateId() {
        long leastSigBits = this.leastSigBits.incrementAndGet();
        if (leastSigBits == 0L) {
            this.mostSigBits.incrementAndGet();
        }
        return new UUID(this.mostSigBits.get(), leastSigBits);
    }
}

