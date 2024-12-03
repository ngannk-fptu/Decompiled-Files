/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationContextInjectableProvider
extends SingletonTypeInjectableProvider<Context, AuthenticationContext> {
    public AuthenticationContextInjectableProvider(AuthenticationContext authenticationContext) {
        super((Type)((Object)AuthenticationContext.class), Objects.requireNonNull(authenticationContext));
    }
}

