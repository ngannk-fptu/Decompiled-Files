/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.AbstractS3ResponseHandler;
import com.amazonaws.util.StringUtils;
import java.io.InputStream;

public class S3StringResponseHandler
extends AbstractS3ResponseHandler<String> {
    @Override
    public AmazonWebServiceResponse<String> handle(HttpResponse response) throws Exception {
        int bytesRead;
        AmazonWebServiceResponse<String> awsResponse = this.parseResponseMetadata(response);
        byte[] buffer = new byte[1024];
        StringBuilder builder = new StringBuilder();
        InputStream content = response.getContent();
        while ((bytesRead = content.read(buffer)) > 0) {
            builder.append(new String(buffer, 0, bytesRead, StringUtils.UTF8));
        }
        awsResponse.setResult(builder.toString());
        return awsResponse;
    }
}

