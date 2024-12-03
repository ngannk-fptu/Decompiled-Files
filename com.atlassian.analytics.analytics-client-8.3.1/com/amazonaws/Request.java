/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.HandlerContextAware;
import com.amazonaws.SignableRequest;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.util.AWSRequestMetrics;
import java.net.URI;
import java.util.List;
import java.util.Map;

public interface Request<T>
extends SignableRequest<T>,
HandlerContextAware {
    public void setHeaders(Map<String, String> var1);

    public void setResourcePath(String var1);

    public Request<T> withParameter(String var1, String var2);

    public void setParameters(Map<String, List<String>> var1);

    public void addParameters(String var1, List<String> var2);

    public void setEndpoint(URI var1);

    public void setHttpMethod(HttpMethodName var1);

    public String getServiceName();

    public AmazonWebServiceRequest getOriginalRequest();

    public void setTimeOffset(int var1);

    public Request<T> withTimeOffset(int var1);

    public AWSRequestMetrics getAWSRequestMetrics();

    public void setAWSRequestMetrics(AWSRequestMetrics var1);
}

