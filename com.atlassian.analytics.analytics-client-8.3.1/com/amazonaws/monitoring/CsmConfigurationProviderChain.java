/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.monitoring;

import com.amazonaws.SdkClientException;
import com.amazonaws.monitoring.CsmConfiguration;
import com.amazonaws.monitoring.CsmConfigurationProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CsmConfigurationProviderChain
implements CsmConfigurationProvider {
    private static final Log log = LogFactory.getLog(CsmConfigurationProviderChain.class);
    private final List<CsmConfigurationProvider> providers = new ArrayList<CsmConfigurationProvider>();

    public CsmConfigurationProviderChain(CsmConfigurationProvider ... providers) {
        if (providers != null) {
            Collections.addAll(this.providers, providers);
        }
    }

    @Override
    public CsmConfiguration getConfiguration() {
        for (CsmConfigurationProvider p : this.providers) {
            try {
                return p.getConfiguration();
            }
            catch (SdkClientException e) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("Unable to load configuration from " + p.toString() + ": " + e.getMessage()));
            }
        }
        throw new SdkClientException("Could not resolve client side monitoring configuration from the configured providers in the chain");
    }
}

