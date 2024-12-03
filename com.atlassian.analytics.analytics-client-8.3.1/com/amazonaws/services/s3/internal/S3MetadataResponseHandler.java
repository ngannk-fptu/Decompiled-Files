/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.AbstractS3ResponseHandler;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class S3MetadataResponseHandler
extends AbstractS3ResponseHandler<ObjectMetadata> {
    @Override
    public AmazonWebServiceResponse<ObjectMetadata> handle(HttpResponse response) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        this.populateObjectMetadata(response, metadata);
        AmazonWebServiceResponse<ObjectMetadata> awsResponse = this.parseResponseMetadata(response);
        awsResponse.setResult(metadata);
        return awsResponse;
    }
}

