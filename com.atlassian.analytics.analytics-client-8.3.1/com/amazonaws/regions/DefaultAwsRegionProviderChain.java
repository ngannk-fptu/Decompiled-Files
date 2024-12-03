/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.regions.AwsEnvVarOverrideRegionProvider;
import com.amazonaws.regions.AwsProfileRegionProvider;
import com.amazonaws.regions.AwsRegionProviderChain;
import com.amazonaws.regions.AwsSystemPropertyRegionProvider;
import com.amazonaws.regions.InstanceMetadataRegionProvider;

public class DefaultAwsRegionProviderChain
extends AwsRegionProviderChain {
    public DefaultAwsRegionProviderChain() {
        super(new AwsEnvVarOverrideRegionProvider(), new AwsSystemPropertyRegionProvider(), new AwsProfileRegionProvider(), new InstanceMetadataRegionProvider());
    }
}

