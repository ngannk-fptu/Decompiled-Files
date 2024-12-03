/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.jwt;

import javax.annotation.Nullable;

public interface Jwt {
    @Nullable
    public String getIssuer();

    @Nullable
    public String getSubject();

    @Nullable
    public String getJsonPayload();
}

