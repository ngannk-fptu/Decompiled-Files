/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.plugins.rest.module.json;

import com.atlassian.plugins.rest.module.json.JsonWithPaddingResponseAdapter;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

@Provider
public class JsonWithPaddingResponseFilter
implements ContainerResponseFilter {
    public static final String ATLASSIAN_ALLOW_JSONP = "atlassian.allow.jsonp";
    private final String callbackFunctionParameterName;

    public JsonWithPaddingResponseFilter() {
        this("jsonp-callback");
    }

    public JsonWithPaddingResponseFilter(String callbackFunctionParameterName) {
        Validate.notEmpty((CharSequence)callbackFunctionParameterName);
        this.callbackFunctionParameterName = callbackFunctionParameterName;
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        if (this.isJsonWithPadding(request, response)) {
            response.setContainerResponseWriter(new JsonWithPaddingResponseAdapter(this.getCallbackFunction(request), response.getContainerResponseWriter()));
        }
        return response;
    }

    private boolean isJsonWithPadding(ContainerRequest request, ContainerResponse response) {
        return this.isJsonResponse(response) && this.isCallbackRequest(request);
    }

    private boolean isCallbackRequest(ContainerRequest request) {
        return StringUtils.isNotBlank((CharSequence)this.getCallbackFunction(request));
    }

    private String getCallbackFunction(ContainerRequest request) {
        return request.getQueryParameters().getFirst(this.callbackFunctionParameterName);
    }

    private boolean isJsonResponse(ContainerResponse response) {
        MultivaluedMap<String, Object> httpHeaders = response.getHttpHeaders();
        return httpHeaders.containsKey("Content-Type") && httpHeaders.getFirst("Content-Type").equals(MediaType.APPLICATION_JSON_TYPE);
    }
}

