/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.AmazonWebServiceRequest;

public interface AsyncHandler<REQUEST extends AmazonWebServiceRequest, RESULT> {
    public void onError(Exception var1);

    public void onSuccess(REQUEST var1, RESULT var2);
}

