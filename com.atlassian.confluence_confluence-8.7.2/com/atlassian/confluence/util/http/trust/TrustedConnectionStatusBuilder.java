/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http.trust;

import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.http.trust.TrustedConnectionStatus;

@Deprecated(forRemoval=true)
public interface TrustedConnectionStatusBuilder {
    public TrustedConnectionStatus getTrustedConnectionStatus(HttpResponse var1);
}

