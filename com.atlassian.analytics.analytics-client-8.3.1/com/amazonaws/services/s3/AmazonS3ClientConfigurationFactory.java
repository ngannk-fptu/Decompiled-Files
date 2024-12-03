/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
class AmazonS3ClientConfigurationFactory
extends ClientConfigurationFactory {
    AmazonS3ClientConfigurationFactory() {
    }

    @Override
    protected ClientConfiguration getInRegionOptimizedConfig() {
        return super.getInRegionOptimizedConfig().withSocketTimeout(21000);
    }
}

