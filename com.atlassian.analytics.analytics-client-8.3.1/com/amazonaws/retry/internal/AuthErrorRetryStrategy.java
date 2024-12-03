/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.internal;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.retry.internal.AuthRetryParameters;

public interface AuthErrorRetryStrategy {
    public AuthRetryParameters shouldRetryWithAuthParam(Request<?> var1, HttpResponse var2, AmazonServiceException var3);
}

