/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.authentication.AuthorisationUriAware;
import com.atlassian.applinks.internal.status.error.ApplinkError;

public interface AuthorisationUriAwareApplinkError
extends ApplinkError,
AuthorisationUriAware {
}

