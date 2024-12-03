/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface KeyStore {
    public @Nullable PrivateKey getPrivateKey(String var1);

    public @Nullable PublicKey getPublicKey(String var1);

    public @Nullable KeyPair getKeyPair(String var1);

    public void storeKeyPair(String var1, KeyPair var2);

    public void storePublicKey(String var1, @Nullable PublicKey var2);
}

