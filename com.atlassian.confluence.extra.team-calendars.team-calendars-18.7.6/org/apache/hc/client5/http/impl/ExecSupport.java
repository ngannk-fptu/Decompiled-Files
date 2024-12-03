/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

import org.apache.hc.client5.http.impl.PrefixedIncrementingId;
import org.apache.hc.core5.annotation.Internal;

@Internal
public final class ExecSupport {
    private static final PrefixedIncrementingId INCREMENTING_ID = new PrefixedIncrementingId("ex-");

    public static long getNextExecNumber() {
        return INCREMENTING_ID.getNextNumber();
    }

    public static String getNextExchangeId() {
        return INCREMENTING_ID.getNextId();
    }
}

