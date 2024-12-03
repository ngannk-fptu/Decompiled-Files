/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.ReadLimitInfo;
import com.amazonaws.http.HttpMethodName;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

public interface ImmutableRequest<T> {
    public Map<String, String> getHeaders();

    public String getResourcePath();

    public Map<String, List<String>> getParameters();

    public URI getEndpoint();

    public HttpMethodName getHttpMethod();

    public int getTimeOffset();

    public InputStream getContent();

    public InputStream getContentUnwrapped();

    public ReadLimitInfo getReadLimitInfo();

    public Object getOriginalRequestObject();
}

