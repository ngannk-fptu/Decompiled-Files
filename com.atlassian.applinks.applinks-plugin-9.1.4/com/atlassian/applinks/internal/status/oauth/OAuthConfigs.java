/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.google.common.base.Function
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.oauth;

import com.atlassian.applinks.internal.common.rest.model.oauth.RestConsumer;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.oauth.Consumer;
import com.google.common.base.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class OAuthConfigs {
    public static final Function<RestConsumer, OAuthConfig> FROM_REST_CONSUMER = new Function<RestConsumer, OAuthConfig>(){

        public OAuthConfig apply(@Nullable RestConsumer restConsumer) {
            return OAuthConfigs.fromRestConsumer(restConsumer);
        }
    };

    private OAuthConfigs() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    @Nonnull
    public static OAuthConfig fromConsumer(@Nullable Consumer consumer) {
        if (consumer == null) {
            return OAuthConfig.createDisabledConfig();
        }
        return OAuthConfig.fromConfig(consumer.getThreeLOAllowed(), consumer.getTwoLOAllowed(), consumer.getTwoLOImpersonationAllowed());
    }

    @Nonnull
    public static OAuthConfig fromRestConsumer(@Nullable RestConsumer restConsumer) {
        if (restConsumer == null) {
            return OAuthConfig.createDisabledConfig();
        }
        return OAuthConfig.fromConfig(true, restConsumer.isTwoLoAllowed(), restConsumer.isTwoLoImpersonationAllowed());
    }
}

