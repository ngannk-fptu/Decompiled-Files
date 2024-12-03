/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Interval
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.Interval;
import com.atlassian.diagnostics.internal.rest.RestEntity;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RestInterval
extends RestEntity {
    public RestInterval(@Nonnull Interval interval) {
        Objects.requireNonNull(interval, "interval");
        this.put("start", interval.getStart().toEpochMilli());
        this.put("end", interval.getEnd().toEpochMilli());
    }
}

