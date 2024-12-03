/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.util.Args;

@Internal
public final class PrefixedIncrementingId {
    private final AtomicLong count = new AtomicLong(0L);
    private final String prefix0;
    private final String prefix1;
    private final String prefix2;
    private final String prefix3;
    private final String prefix4;
    private final String prefix5;
    private final String prefix6;
    private final String prefix7;
    private final String prefix8;
    private final String prefix9;

    public PrefixedIncrementingId(String prefix) {
        this.prefix0 = Args.notNull(prefix, "prefix");
        this.prefix1 = this.prefix0 + '0';
        this.prefix2 = this.prefix1 + '0';
        this.prefix3 = this.prefix2 + '0';
        this.prefix4 = this.prefix3 + '0';
        this.prefix5 = this.prefix4 + '0';
        this.prefix6 = this.prefix5 + '0';
        this.prefix7 = this.prefix6 + '0';
        this.prefix8 = this.prefix7 + '0';
        this.prefix9 = this.prefix8 + '0';
    }

    public long getNextNumber() {
        return this.count.incrementAndGet();
    }

    public String getNextId() {
        return this.createId(this.count.incrementAndGet());
    }

    String createId(long value) {
        String longString = Long.toString(value);
        switch (longString.length()) {
            case 1: {
                return this.prefix9 + longString;
            }
            case 2: {
                return this.prefix8 + longString;
            }
            case 3: {
                return this.prefix7 + longString;
            }
            case 4: {
                return this.prefix6 + longString;
            }
            case 5: {
                return this.prefix5 + longString;
            }
            case 6: {
                return this.prefix4 + longString;
            }
            case 7: {
                return this.prefix3 + longString;
            }
            case 8: {
                return this.prefix2 + longString;
            }
            case 9: {
                return this.prefix1 + longString;
            }
        }
        return this.prefix0 + longString;
    }
}

