/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 */
package com.atlassian.oauth2.provider.core.security;

import com.atlassian.oauth2.provider.core.security.Hasher;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class Sha256Hasher
implements Hasher {
    @Override
    public String hash(String toHash) {
        return Hashing.sha256().hashString((CharSequence)toHash, StandardCharsets.UTF_8).toString();
    }
}

