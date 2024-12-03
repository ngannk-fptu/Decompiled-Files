/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.CloudPlatform;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GoogleCloudPlatform
implements CloudPlatform {
    private static final String GCLOUD_METADATA_ENDPOINT = "http://metadata.google.internal/computeMetadata/v1/";
    private static final String GCLOUD_INSTANCE_TYPE_ENDPOINT = "http://metadata.google.internal/computeMetadata/v1/instance/machine-type";
    private static final Map<String, String> GCLOUD_METADATA_HEADERS = Collections.unmodifiableMap(Stream.of(new AbstractMap.SimpleEntry<String, String>("Metadata-Flavor", "Google")).collect(Collectors.toMap(e -> (String)e.getKey(), e -> (String)e.getValue())));
    private static final Pattern GCLOUD_INSTANCE_REGEX = Pattern.compile("(?<=machineTypes/).*");

    @Override
    public String getInstanceTypeMetadataEndpoint() {
        return GCLOUD_INSTANCE_TYPE_ENDPOINT;
    }

    @Override
    public CloudPlatformType getPlatformType() {
        return CloudPlatformType.GOOGLE_CLOUD;
    }

    @Override
    public Map<String, String> getMetadataHeaders() {
        return GCLOUD_METADATA_HEADERS;
    }

    @Override
    public String parseInstanceType(String responseBody) {
        Matcher matcher = GCLOUD_INSTANCE_REGEX.matcher(responseBody);
        matcher.find();
        return matcher.group();
    }
}

