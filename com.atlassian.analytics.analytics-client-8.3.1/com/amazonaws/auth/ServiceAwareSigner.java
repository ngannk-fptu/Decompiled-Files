/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.Signer;

public interface ServiceAwareSigner
extends Signer {
    public void setServiceName(String var1);
}

