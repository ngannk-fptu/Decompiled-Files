/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.AwsCloudPlatform;
import com.atlassian.confluence.status.service.systeminfo.AzureCloudPlatform;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatform;
import com.atlassian.confluence.status.service.systeminfo.GoogleCloudPlatform;

public enum CloudPlatformType {
    AWS(new AwsCloudPlatform()),
    AZURE(new AzureCloudPlatform()),
    GOOGLE_CLOUD(new GoogleCloudPlatform());

    private final CloudPlatform cloudPlatform;

    private CloudPlatformType(CloudPlatform cloudPlatform) {
        this.cloudPlatform = cloudPlatform;
    }

    public CloudPlatform getCloudPlatform() {
        return this.cloudPlatform;
    }
}

