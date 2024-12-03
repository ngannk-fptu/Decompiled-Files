/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.Signer;

public interface RegionAwareSigner
extends Signer {
    public void setRegionName(String var1);
}

