/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.oauth.OAuth$Parameter
 */
package com.atlassian.applinks.oauth.auth;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.oauth.OAuth;

public final class OAuthParameters {
    private OAuthParameters() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    @Nonnull
    public static Map<String, String> asMap(@Nullable List<OAuth.Parameter> params) {
        if (params == null || params.isEmpty()) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder paramsBuilder = ImmutableMap.builder();
        for (OAuth.Parameter parameter : params) {
            paramsBuilder.put((Object)parameter.getKey(), (Object)parameter.getValue());
        }
        return paramsBuilder.build();
    }
}

