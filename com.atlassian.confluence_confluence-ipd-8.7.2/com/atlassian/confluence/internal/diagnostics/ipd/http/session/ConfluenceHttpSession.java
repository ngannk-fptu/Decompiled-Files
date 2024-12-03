/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http.session;

import java.time.Instant;

public interface ConfluenceHttpSession {
    public String getId();

    public String getUserName();

    public Instant getLastAccessTime();
}

