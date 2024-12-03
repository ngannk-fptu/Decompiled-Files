/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SignableRequest;

public interface RequestSigner {
    public void sign(SignableRequest<?> var1);
}

