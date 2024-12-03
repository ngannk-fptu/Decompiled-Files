/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import java.security.PublicKey;

@Deprecated
public interface Application {
    public PublicKey getPublicKey();

    public String getID();
}

