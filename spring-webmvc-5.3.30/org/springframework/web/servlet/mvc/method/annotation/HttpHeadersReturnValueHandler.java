/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.core.MethodParameter
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandler
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class HttpHeadersReturnValueHandler
implements HandlerMethodReturnValueHandler {
    public boolean supportsReturnType(MethodParameter returnType) {
        return HttpHeaders.class.isAssignableFrom(returnType.getParameterType());
    }

    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        Assert.state((boolean)(returnValue instanceof HttpHeaders), (String)"HttpHeaders expected");
        HttpHeaders headers = (HttpHeaders)returnValue;
        if (!headers.isEmpty()) {
            HttpServletResponse servletResponse = (HttpServletResponse)webRequest.getNativeResponse(HttpServletResponse.class);
            Assert.state((servletResponse != null ? 1 : 0) != 0, (String)"No HttpServletResponse");
            ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(servletResponse);
            outputMessage.getHeaders().putAll((Map)headers);
            outputMessage.getBody();
        }
    }
}

