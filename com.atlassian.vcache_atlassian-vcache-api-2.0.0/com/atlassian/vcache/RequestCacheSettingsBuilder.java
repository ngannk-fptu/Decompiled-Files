/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.ChangeRate;
import com.atlassian.vcache.RequestCacheSettings;
import java.util.Optional;

@PublicApi
public class RequestCacheSettingsBuilder {
    private Optional<ChangeRate> changeRate = Optional.empty();

    public RequestCacheSettingsBuilder dataChangeRateHint(ChangeRate hint) {
        this.changeRate = Optional.of(hint);
        return this;
    }

    public RequestCacheSettings build() {
        return new RequestCacheSettings(this.changeRate.orElse(ChangeRate.HIGH_CHANGE));
    }
}

