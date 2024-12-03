/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.util.AWSRequestMetrics;

@SdkInternalApi
class AwsErrorResponseHandler
implements HttpResponseHandler<AmazonServiceException> {
    private final HttpResponseHandler<AmazonServiceException> delegate;
    private final AWSRequestMetrics awsRequestMetrics;
    private final ClientConfiguration clientConfiguration;

    AwsErrorResponseHandler(HttpResponseHandler<AmazonServiceException> errorResponseHandler, AWSRequestMetrics awsRequestMetrics, ClientConfiguration clientConfiguration) {
        this.delegate = errorResponseHandler;
        this.awsRequestMetrics = awsRequestMetrics;
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    public AmazonServiceException handle(HttpResponse response) throws Exception {
        AmazonServiceException ase = this.handleAse(response);
        ase.setStatusCode(response.getStatusCode());
        ase.setServiceName(response.getRequest().getServiceName());
        ase.setProxyHost(this.clientConfiguration.getProxyHost());
        this.awsRequestMetrics.addPropertyWith(AWSRequestMetrics.Field.AWSRequestID, (Object)ase.getRequestId()).addPropertyWith(AWSRequestMetrics.Field.AWSErrorCode, (Object)ase.getErrorCode()).addPropertyWith(AWSRequestMetrics.Field.StatusCode, (Object)ase.getStatusCode());
        return ase;
    }

    private AmazonServiceException handleAse(HttpResponse response) throws Exception {
        int statusCode = response.getStatusCode();
        try {
            return this.delegate.handle(response);
        }
        catch (InterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            if (statusCode == 413) {
                AmazonServiceException exception = new AmazonServiceException("Request entity too large");
                exception.setServiceName(response.getRequest().getServiceName());
                exception.setStatusCode(statusCode);
                exception.setErrorType(AmazonServiceException.ErrorType.Client);
                exception.setErrorCode("Request entity too large");
                return exception;
            }
            if (statusCode >= 500 && statusCode < 600) {
                AmazonServiceException exception = new AmazonServiceException(response.getStatusText());
                exception.setServiceName(response.getRequest().getServiceName());
                exception.setStatusCode(statusCode);
                exception.setErrorType(AmazonServiceException.ErrorType.Service);
                exception.setErrorCode(response.getStatusText());
                return exception;
            }
            throw e;
        }
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return this.delegate.needsConnectionLeftOpen();
    }
}

