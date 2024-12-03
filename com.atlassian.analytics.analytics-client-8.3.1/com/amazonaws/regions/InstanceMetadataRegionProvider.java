/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.regions;

import com.amazonaws.AmazonClientException;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.util.EC2MetadataUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InstanceMetadataRegionProvider
extends AwsRegionProvider {
    private static final Log LOG = LogFactory.getLog(InstanceMetadataRegionProvider.class);
    private volatile String region;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getRegion() {
        if (SDKGlobalConfiguration.isEc2MetadataDisabled()) {
            throw new AmazonClientException("AWS_EC2_METADATA_DISABLED is set to true, not loading region from EC2 Instance Metadata service");
        }
        if (this.region == null) {
            InstanceMetadataRegionProvider instanceMetadataRegionProvider = this;
            synchronized (instanceMetadataRegionProvider) {
                if (this.region == null) {
                    this.region = this.tryDetectRegion();
                }
            }
        }
        return this.region;
    }

    private String tryDetectRegion() {
        try {
            return EC2MetadataUtils.getEC2InstanceRegion();
        }
        catch (AmazonClientException sce) {
            LOG.debug((Object)("Ignoring failure to retrieve the region: " + sce.getMessage()));
            return null;
        }
    }
}

