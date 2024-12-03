/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonView
 *  org.springframework.core.MethodParameter
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.json.MappingJacksonValue
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.mvc.method.annotation;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

public class JsonViewResponseBodyAdvice
extends AbstractMappingJacksonResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return super.supports(returnType, converterType) && returnType.hasMethodAnnotation(JsonView.class);
    }

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType, MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
        JsonView ann = (JsonView)returnType.getMethodAnnotation(JsonView.class);
        Assert.state((ann != null ? 1 : 0) != 0, (String)"No JsonView annotation");
        Class[] classes = ann.value();
        if (classes.length != 1) {
            throw new IllegalArgumentException("@JsonView only supported for response body advice with exactly 1 class argument: " + returnType);
        }
        bodyContainer.setSerializationView(classes[0]);
    }
}

