/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 */
package com.atlassian.applinks.internal.rest.client;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.internal.authentication.AuthorisationUriAware;

public interface AuthorisationUriAwareRequest
extends ApplicationLinkRequest,
AuthorisationUriAware {
}

