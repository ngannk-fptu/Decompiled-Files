/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.CloudPlatform;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AzureCloudPlatform
implements CloudPlatform {
    private static final String AZURE_METADATA_ENDPOINT = "http://169.254.169.254/metadata/";
    private static final String AZURE_METADATA_API_VERSION = "2017-08-01";
    private static final String AZURE_METADATA_PARAMS = "?api-version=2017-08-01&format=text";
    private static final String AZURE_INSTANCE_TYPE_ENDPOINT = "http://169.254.169.254/metadata/instance/compute/vmSize?api-version=2017-08-01&format=text";
    private static final Map<String, String> AZURE_METADATA_HEADERS = Collections.unmodifiableMap(Stream.of(new AbstractMap.SimpleEntry<String, String>("Metadata", "true")).collect(Collectors.toMap(e -> (String)e.getKey(), e -> (String)e.getValue())));

    @Override
    public String getInstanceTypeMetadataEndpoint() {
        return AZURE_INSTANCE_TYPE_ENDPOINT;
    }

    @Override
    public CloudPlatformType getPlatformType() {
        return CloudPlatformType.AZURE;
    }

    @Override
    public Map<String, String> getMetadataHeaders() {
        return AZURE_METADATA_HEADERS;
    }
}

