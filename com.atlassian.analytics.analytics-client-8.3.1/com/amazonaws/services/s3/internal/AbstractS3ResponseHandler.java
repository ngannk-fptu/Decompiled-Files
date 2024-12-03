/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.SdkClientException;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.services.s3.S3ResponseMetadata;
import com.amazonaws.services.s3.internal.ObjectExpirationHeaderHandler;
import com.amazonaws.services.s3.internal.ObjectRestoreHeaderHandler;
import com.amazonaws.services.s3.internal.S3MetadataResponseHandler;
import com.amazonaws.services.s3.internal.S3RequesterChargedHeaderHandler;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.StringUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractS3ResponseHandler<T>
implements HttpResponseHandler<AmazonWebServiceResponse<T>> {
    private static final Log log = LogFactory.getLog(S3MetadataResponseHandler.class);
    private static final Set<String> ignoredHeaders = new HashSet<String>();

    @Override
    public boolean needsConnectionLeftOpen() {
        return false;
    }

    protected AmazonWebServiceResponse<T> parseResponseMetadata(HttpResponse response) {
        AmazonWebServiceResponse awsResponse = new AmazonWebServiceResponse();
        String awsRequestId = response.getHeaders().get("x-amz-request-id");
        String hostId = response.getHeaders().get("x-amz-id-2");
        String cloudFrontId = response.getHeaders().get("X-Amz-Cf-Id");
        HashMap<String, String> metadataMap = new HashMap<String, String>();
        metadataMap.put("AWS_REQUEST_ID", awsRequestId);
        metadataMap.put("HOST_ID", hostId);
        metadataMap.put("CLOUD_FRONT_ID", cloudFrontId);
        awsResponse.setResponseMetadata(new S3ResponseMetadata(metadataMap));
        return awsResponse;
    }

    protected void populateObjectMetadata(HttpResponse response, ObjectMetadata metadata) {
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            String key = header.getKey();
            if (StringUtils.beginsWithIgnoreCase(key, "x-amz-meta-")) {
                key = key.substring("x-amz-meta-".length());
                metadata.addUserMetadata(key, header.getValue());
                continue;
            }
            if (ignoredHeaders.contains(key)) continue;
            if (key.equalsIgnoreCase("Last-Modified")) {
                try {
                    metadata.setHeader(key, ServiceUtils.parseRfc822Date(header.getValue()));
                }
                catch (Exception pe) {
                    log.warn((Object)("Unable to parse last modified date: " + header.getValue()), (Throwable)pe);
                }
                continue;
            }
            if (key.equalsIgnoreCase("Content-Length")) {
                try {
                    metadata.setHeader(key, Long.parseLong(header.getValue()));
                    continue;
                }
                catch (NumberFormatException nfe) {
                    throw new SdkClientException("Unable to parse content length. Header 'Content-Length' has corrupted data" + nfe.getMessage(), nfe);
                }
            }
            if (key.equalsIgnoreCase("ETag")) {
                metadata.setHeader(key, ServiceUtils.removeQuotes(header.getValue()));
                continue;
            }
            if (key.equalsIgnoreCase("Expires")) {
                metadata.setHeader("Expires", header.getValue());
                try {
                    metadata.setHttpExpiresDate(DateUtils.parseRFC822Date(header.getValue()));
                }
                catch (Exception pe) {
                    log.warn((Object)("Unable to parse http expiration date: " + header.getValue()), (Throwable)pe);
                }
                continue;
            }
            if (key.equalsIgnoreCase("x-amz-expiration")) {
                new ObjectExpirationHeaderHandler<ObjectMetadata>().handle(metadata, response);
                continue;
            }
            if (key.equalsIgnoreCase("x-amz-restore")) {
                new ObjectRestoreHeaderHandler<ObjectMetadata>().handle(metadata, response);
                continue;
            }
            if (key.equalsIgnoreCase("x-amz-request-charged")) {
                new S3RequesterChargedHeaderHandler<ObjectMetadata>().handle(metadata, response);
                continue;
            }
            if (key.equalsIgnoreCase("x-amz-mp-parts-count")) {
                try {
                    metadata.setHeader(key, Integer.parseInt(header.getValue()));
                    continue;
                }
                catch (NumberFormatException nfe) {
                    throw new SdkClientException("Unable to parse part count. Header x-amz-mp-parts-count has corrupted data" + nfe.getMessage(), nfe);
                }
            }
            if (key.equalsIgnoreCase("x-amz-server-side-encryption-bucket-key-enabled")) {
                metadata.setBucketKeyEnabled("true".equals(header.getValue()));
                continue;
            }
            metadata.setHeader(key, header.getValue());
        }
    }

    static {
        ignoredHeaders.add("Date");
        ignoredHeaders.add("Server");
        ignoredHeaders.add("x-amz-request-id");
        ignoredHeaders.add("x-amz-id-2");
        ignoredHeaders.add("X-Amz-Cf-Id");
        ignoredHeaders.add("Connection");
    }
}

