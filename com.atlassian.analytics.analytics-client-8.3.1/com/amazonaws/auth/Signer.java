/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWSCredentials;

public interface Signer {
    public void sign(SignableRequest<?> var1, AWSCredentials var2);
}

