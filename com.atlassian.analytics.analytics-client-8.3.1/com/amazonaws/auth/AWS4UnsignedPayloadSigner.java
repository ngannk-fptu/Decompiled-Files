/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SignableRequest;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.SdkClock;

public class AWS4UnsignedPayloadSigner
extends AWS4Signer {
    public AWS4UnsignedPayloadSigner() {
    }

    @SdkTestInternalApi
    public AWS4UnsignedPayloadSigner(SdkClock clock) {
        super(clock);
    }

    @Override
    public void sign(SignableRequest<?> request, AWSCredentials credentials) {
        request.getHeaders().put("x-amz-content-sha256", "required");
        super.sign(request, credentials);
    }

    @Override
    protected String calculateContentHash(SignableRequest<?> request) {
        if ("https".equals(request.getEndpoint().getScheme())) {
            return "UNSIGNED-PAYLOAD";
        }
        return super.calculateContentHash(request);
    }
}

