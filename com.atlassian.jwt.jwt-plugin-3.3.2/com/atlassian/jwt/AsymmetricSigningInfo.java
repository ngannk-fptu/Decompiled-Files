/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt;

import com.atlassian.jwt.SigningInfo;
import java.security.interfaces.RSAPrivateKey;

public interface AsymmetricSigningInfo
extends SigningInfo {
    public RSAPrivateKey getPrivateKey();
}

