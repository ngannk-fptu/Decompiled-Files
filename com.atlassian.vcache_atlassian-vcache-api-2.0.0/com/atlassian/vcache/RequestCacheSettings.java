/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.ChangeRate;
import java.util.Objects;

@PublicApi
public class RequestCacheSettings {
    private final ChangeRate changeRate;

    RequestCacheSettings(ChangeRate changeRate) {
        this.changeRate = Objects.requireNonNull(changeRate);
    }

    public ChangeRate getChangeRate() {
        return this.changeRate;
    }
}

