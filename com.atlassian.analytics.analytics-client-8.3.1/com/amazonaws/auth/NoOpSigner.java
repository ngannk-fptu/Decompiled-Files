/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.Signer;

public class NoOpSigner
implements Signer {
    @Override
    public void sign(SignableRequest<?> request, AWSCredentials credentials) {
    }
}

