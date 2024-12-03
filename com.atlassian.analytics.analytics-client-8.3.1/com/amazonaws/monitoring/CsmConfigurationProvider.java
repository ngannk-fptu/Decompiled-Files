/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.monitoring;

import com.amazonaws.SdkClientException;
import com.amazonaws.monitoring.CsmConfiguration;

public interface CsmConfigurationProvider {
    public CsmConfiguration getConfiguration() throws SdkClientException;
}

