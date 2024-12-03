/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.Request;
import com.amazonaws.SdkClientException;
import com.amazonaws.internal.ServiceEndpointBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.s3.internal.BucketNameUtils;
import com.amazonaws.util.SdkHttpUtils;
import java.net.URI;
import java.net.URISyntaxException;

public class S3RequestEndpointResolver {
    private final ServiceEndpointBuilder endpointBuilder;
    private final boolean isPathStyleAccess;
    private final String bucketName;
    private final String key;

    public S3RequestEndpointResolver(ServiceEndpointBuilder endpointBuilder, boolean isPathStyleAccess, String bucketName, String key) {
        this.endpointBuilder = endpointBuilder;
        this.isPathStyleAccess = isPathStyleAccess;
        this.bucketName = bucketName;
        this.key = key;
    }

    static boolean isValidIpV4Address(String ipAddr) {
        if (ipAddr == null) {
            return false;
        }
        String[] tokens = ipAddr.split("\\.");
        if (tokens.length != 4) {
            return false;
        }
        for (String token : tokens) {
            try {
                int tokenInt = Integer.parseInt(token);
                if (tokenInt >= 0 && tokenInt <= 255) continue;
                return false;
            }
            catch (NumberFormatException ase) {
                return false;
            }
        }
        return true;
    }

    private static URI convertToVirtualHostEndpoint(URI endpoint, String bucketName) {
        try {
            return new URI(String.format("%s://%s.%s", endpoint.getScheme(), bucketName, endpoint.getAuthority()));
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid bucket name: " + bucketName, e);
        }
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public void resolveRequestEndpoint(Request<?> request) {
        this.resolveRequestEndpoint(request, null);
    }

    public void resolveRequestEndpoint(Request<?> request, String regionString) {
        URI endpoint;
        if (regionString != null) {
            Region r = RegionUtils.getRegion(regionString);
            if (r == null) {
                throw new SdkClientException("Not able to determine region for " + regionString + ".Please upgrade to a newer version of the SDK");
            }
            this.endpointBuilder.withRegion(r);
        }
        if ((endpoint = this.endpointBuilder.getServiceEndpoint()).getHost() == null) {
            throw new IllegalArgumentException("Endpoint does not contain a valid host name: " + request.getEndpoint());
        }
        if (this.shouldUseVirtualAddressing(endpoint)) {
            request.setEndpoint(S3RequestEndpointResolver.convertToVirtualHostEndpoint(endpoint, this.bucketName));
            request.setResourcePath(SdkHttpUtils.urlEncode(this.getHostStyleResourcePath(), true));
        } else {
            request.setEndpoint(endpoint);
            request.setResourcePath(SdkHttpUtils.urlEncode(this.getPathStyleResourcePath(), true));
        }
    }

    private boolean shouldUseVirtualAddressing(URI endpoint) {
        return !this.isPathStyleAccess && BucketNameUtils.isDNSBucketName(this.bucketName) && !S3RequestEndpointResolver.isValidIpV4Address(endpoint.getHost());
    }

    private String getHostStyleResourcePath() {
        String resourcePath = this.key;
        if (this.key != null && this.key.startsWith("/")) {
            resourcePath = "/" + this.key;
        }
        return resourcePath;
    }

    private String getPathStyleResourcePath() {
        if (this.bucketName == null) {
            return this.key;
        }
        return this.bucketName + "/" + (this.key != null ? this.key : "");
    }
}

