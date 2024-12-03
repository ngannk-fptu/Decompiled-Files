/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core.keys;

import com.atlassian.jwt.exception.JwtCannotRetrieveKeyException;
import java.security.interfaces.RSAPrivateKey;
import javax.annotation.Nonnull;

public interface PrivateKeyRetriever {
    @Nonnull
    public RSAPrivateKey getPrivateKey() throws JwtCannotRetrieveKeyException;

    public static enum keyLocationType {
        FILE,
        CLASSPATH_RESOURCE;

    }
}

