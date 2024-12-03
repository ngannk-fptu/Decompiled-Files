/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.ResponseMetadata;

public class AmazonWebServiceResponse<T> {
    private T result;
    private ResponseMetadata responseMetadata;

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setResponseMetadata(ResponseMetadata responseMetadata) {
        this.responseMetadata = responseMetadata;
    }

    public ResponseMetadata getResponseMetadata() {
        return this.responseMetadata;
    }

    public String getRequestId() {
        if (this.responseMetadata == null) {
            return null;
        }
        return this.responseMetadata.getRequestId();
    }
}

