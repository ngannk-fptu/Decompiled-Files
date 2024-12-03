/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.Signer;

public interface EndpointPrefixAwareSigner
extends Signer {
    public void setEndpointPrefix(String var1);
}

