/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.internal.S3XmlResponseHandler;
import com.amazonaws.transform.Unmarshaller;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ResponseHeaderHandlerChain<T>
extends S3XmlResponseHandler<T> {
    private final List<HeaderHandler<T>> headerHandlers;

    public ResponseHeaderHandlerChain(Unmarshaller<T, InputStream> responseUnmarshaller, HeaderHandler<T> ... headerHandlers) {
        super(responseUnmarshaller);
        this.headerHandlers = Arrays.asList(headerHandlers);
    }

    @Override
    public AmazonWebServiceResponse<T> handle(HttpResponse response) throws Exception {
        Object awsResponse = super.handle(response);
        Object result = ((AmazonWebServiceResponse)awsResponse).getResult();
        if (result != null) {
            for (HeaderHandler handler : this.headerHandlers) {
                handler.handle(result, response);
            }
        }
        return awsResponse;
    }
}

