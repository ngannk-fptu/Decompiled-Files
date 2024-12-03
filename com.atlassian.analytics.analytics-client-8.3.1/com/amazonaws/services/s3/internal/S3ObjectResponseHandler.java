/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.AbstractS3ResponseHandler;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3ObjectResponseHandler
extends AbstractS3ResponseHandler<S3Object> {
    @Override
    public AmazonWebServiceResponse<S3Object> handle(HttpResponse response) throws Exception {
        S3Object object = new S3Object();
        AmazonWebServiceResponse<S3Object> awsResponse = this.parseResponseMetadata(response);
        if (response.getHeaders().get("x-amz-website-redirect-location") != null) {
            object.setRedirectLocation(response.getHeaders().get("x-amz-website-redirect-location"));
        }
        if (response.getHeaders().get("x-amz-request-charged") != null) {
            object.setRequesterCharged(true);
        }
        if (response.getHeaders().get("x-amz-tagging-count") != null) {
            object.setTaggingCount(Integer.parseInt(response.getHeaders().get("x-amz-tagging-count")));
        }
        ObjectMetadata metadata = object.getObjectMetadata();
        this.populateObjectMetadata(response, metadata);
        object.setObjectContent(new S3ObjectInputStream(response.getContent(), response.getHttpRequest()));
        awsResponse.setResult(object);
        return awsResponse;
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return true;
    }

    private long getContentLength(HttpResponse response) {
        String contentLength = response.getHeader("Content-Length");
        if (contentLength == null) {
            return -1L;
        }
        return Long.parseLong(response.getHeader("Content-Length"));
    }
}

