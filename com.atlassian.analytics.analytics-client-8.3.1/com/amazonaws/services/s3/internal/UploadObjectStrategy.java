/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.Request;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.model.ObjectMetadata;

@SdkInternalApi
public interface UploadObjectStrategy<RequestT, ResponseT> {
    public ObjectMetadata invokeServiceCall(Request<RequestT> var1);

    public ResponseT createResult(ObjectMetadata var1, String var2);

    public String md5ValidationErrorSuffix();
}

