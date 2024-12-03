/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.auth.types;

import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.api.auth.NonImpersonatingAuthenticationProvider;

public interface CorsAuthenticationProvider
extends ImpersonatingAuthenticationProvider,
NonImpersonatingAuthenticationProvider {
}

