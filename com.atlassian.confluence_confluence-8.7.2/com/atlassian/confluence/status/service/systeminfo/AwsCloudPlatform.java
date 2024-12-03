/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.CloudPlatform;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;

public class AwsCloudPlatform
implements CloudPlatform {
    private static final String AWS_METADATA_ENDPOINT = "http://169.254.169.254/latest/meta-data/";
    private static final String AWS_INSTANCE_TYPE_ENDPOINT = "http://169.254.169.254/latest/meta-data/instance-type";

    @Override
    public String getInstanceTypeMetadataEndpoint() {
        return AWS_INSTANCE_TYPE_ENDPOINT;
    }

    @Override
    public CloudPlatformType getPlatformType() {
        return CloudPlatformType.AWS;
    }
}

