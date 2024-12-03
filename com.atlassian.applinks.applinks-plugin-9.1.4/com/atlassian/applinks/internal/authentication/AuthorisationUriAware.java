/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.authentication;

import com.atlassian.applinks.api.AuthorisationURIGenerator;
import javax.annotation.Nonnull;

public interface AuthorisationUriAware {
    @Nonnull
    public AuthorisationURIGenerator getAuthorisationUriGenerator();
}

