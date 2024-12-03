/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.regions;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.regions.AwsRegionProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AwsRegionProviderChain
extends AwsRegionProvider {
    private static final Log LOG = LogFactory.getLog(AWSCredentialsProviderChain.class);
    private final List<AwsRegionProvider> providers;

    public AwsRegionProviderChain(AwsRegionProvider ... providers) {
        this.providers = new ArrayList<AwsRegionProvider>(providers.length);
        Collections.addAll(this.providers, providers);
    }

    @Override
    public String getRegion() throws SdkClientException {
        for (AwsRegionProvider provider : this.providers) {
            try {
                String region = provider.getRegion();
                if (region == null) continue;
                return region;
            }
            catch (Exception e) {
                LOG.debug((Object)("Unable to load region from " + provider.toString() + ": " + e.getMessage()));
            }
        }
        throw new SdkClientException("Unable to load region information from any provider in the chain");
    }
}

