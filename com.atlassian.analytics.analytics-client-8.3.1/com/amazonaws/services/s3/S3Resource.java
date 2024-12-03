/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.arn.AwsResource;

@SdkInternalApi
public interface S3Resource
extends AwsResource {
    public String getType();

    public S3Resource getParentS3Resource();
}

