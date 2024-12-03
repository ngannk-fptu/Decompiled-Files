/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.arn;

import com.amazonaws.annotation.SdkProtectedApi;

@SdkProtectedApi
public interface AwsResource {
    public String getPartition();

    public String getRegion();

    public String getAccountId();
}

