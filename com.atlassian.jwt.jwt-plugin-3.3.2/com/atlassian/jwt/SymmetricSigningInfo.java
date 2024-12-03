/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt;

import com.atlassian.jwt.SigningInfo;

public interface SymmetricSigningInfo
extends SigningInfo {
    public String getSharedSecret();
}

