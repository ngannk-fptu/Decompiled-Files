/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.arn;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.arn.Arn;
import com.amazonaws.arn.AwsResource;

@SdkProtectedApi
public interface ArnConverter<T extends AwsResource> {
    public T convertArn(Arn var1);
}

