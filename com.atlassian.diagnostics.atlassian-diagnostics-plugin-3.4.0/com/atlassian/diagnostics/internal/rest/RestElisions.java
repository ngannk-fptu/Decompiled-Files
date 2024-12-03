/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Elisions
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.Elisions;
import com.atlassian.diagnostics.internal.rest.RestEntity;
import com.atlassian.diagnostics.internal.rest.RestInterval;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RestElisions
extends RestEntity {
    public RestElisions(@Nonnull Elisions elisions) {
        Objects.requireNonNull(elisions, "elisions");
        this.put("count", elisions.getCount());
        this.put("interval", new RestInterval(elisions.getInterval()));
    }
}

