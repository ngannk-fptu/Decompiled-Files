/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.google.common.base.Function
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.model.auth.compatibility;

import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.google.common.base.Function;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RestAuthenticationProvider
extends BaseRestEntity {
    public static final Function<Object, RestAuthenticationProvider> REST_TRANSFORM = new Function<Object, RestAuthenticationProvider>(){

        @Nullable
        public RestAuthenticationProvider apply(@Nullable Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof RestAuthenticationProvider) {
                return (RestAuthenticationProvider)object;
            }
            if (object instanceof Map) {
                return new RestAuthenticationProvider((Map)object);
            }
            throw new IllegalArgumentException("Cannot instantiate RestAuthenticationProvider from " + object);
        }
    };
    public static final String MODULE = "module";
    public static final String PROVIDER = "provider";
    public static final String CONFIG = "config";

    public RestAuthenticationProvider() {
    }

    public RestAuthenticationProvider(@Nonnull Class<? extends AuthenticationProvider> clazz) {
        this.put(PROVIDER, (Object)clazz.getCanonicalName());
        this.put(CONFIG, (Object)new BaseRestEntity());
    }

    public RestAuthenticationProvider(Map<String, Object> original) {
        super(original);
    }

    @Nullable
    public String getProvider() {
        return this.getString(PROVIDER);
    }
}

