/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.AwsRegionProvider;

public class AwsEnvVarOverrideRegionProvider
extends AwsRegionProvider {
    @Override
    public String getRegion() throws SdkClientException {
        return System.getenv("AWS_REGION");
    }
}

