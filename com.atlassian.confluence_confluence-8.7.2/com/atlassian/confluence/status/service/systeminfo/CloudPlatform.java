/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;
import java.util.Collections;
import java.util.Map;

public interface CloudPlatform {
    public String getInstanceTypeMetadataEndpoint();

    public CloudPlatformType getPlatformType();

    default public Map<String, String> getMetadataHeaders() {
        return Collections.emptyMap();
    }

    default public String parseInstanceType(String responseBody) {
        return responseBody;
    }
}

