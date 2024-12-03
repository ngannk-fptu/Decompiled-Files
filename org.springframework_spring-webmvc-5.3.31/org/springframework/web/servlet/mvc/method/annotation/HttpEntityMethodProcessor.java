/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.RequestEntity
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.ui.ModelMap
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 *  org.springframework.web.HttpMediaTypeNotSupportedException
 *  org.springframework.web.accept.ContentNegotiationManager
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.ServletWebRequest
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

public class HttpEntityMethodProcessor
extends AbstractMessageConverterMethodProcessor {
    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager) {
        super(converters, manager);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice) {
        super(converters, null, requestResponseBodyAdvice);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return HttpEntity.class == parameter.getParameterType() || RequestEntity.class == parameter.getParameterType();
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        return HttpEntity.class.isAssignableFrom(returnType.getParameterType()) && !RequestEntity.class.isAssignableFrom(returnType.getParameterType());
    }

    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws IOException, HttpMediaTypeNotSupportedException {
        ServletServerHttpRequest inputMessage = this.createInputMessage(webRequest);
        Type paramType = this.getHttpEntityType(parameter);
        if (paramType == null) {
            throw new IllegalArgumentException("HttpEntity parameter '" + parameter.getParameterName() + "' in method " + parameter.getMethod() + " is not parameterized");
        }
        Object body2 = this.readWithMessageConverters(webRequest, parameter, paramType);
        if (RequestEntity.class == parameter.getParameterType()) {
            return new RequestEntity(body2, (MultiValueMap)inputMessage.getHeaders(), inputMessage.getMethod(), inputMessage.getURI());
        }
        return new HttpEntity(body2, (MultiValueMap)inputMessage.getHeaders());
    }

    @Nullable
    private Type getHttpEntityType(MethodParameter parameter) {
        Assert.isAssignable(HttpEntity.class, (Class)parameter.getParameterType());
        Type parameterType = parameter.getGenericParameterType();
        if (parameterType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType)parameterType;
            if (type.getActualTypeArguments().length != 1) {
                throw new IllegalArgumentException("Expected single generic parameter on '" + parameter.getParameterName() + "' in method " + parameter.getMethod());
            }
            return type.getActualTypeArguments()[0];
        }
        if (parameterType instanceof Class) {
            return Object.class;
        }
        return null;
    }

    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        if (returnValue == null) {
            return;
        }
        ServletServerHttpRequest inputMessage = this.createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = this.createOutputMessage(webRequest);
        Assert.isInstanceOf(HttpEntity.class, (Object)returnValue);
        HttpEntity responseEntity = (HttpEntity)returnValue;
        HttpHeaders outputHeaders = outputMessage.getHeaders();
        HttpHeaders entityHeaders = responseEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            entityHeaders.forEach((key, value) -> {
                if ("Vary".equals(key) && outputHeaders.containsKey((Object)"Vary")) {
                    List<String> values = this.getVaryRequestHeadersToAdd(outputHeaders, entityHeaders);
                    if (!values.isEmpty()) {
                        outputHeaders.setVary(values);
                    }
                } else {
                    outputHeaders.put(key, value);
                }
            });
        }
        if (responseEntity instanceof ResponseEntity) {
            String location;
            int returnStatus = ((ResponseEntity)responseEntity).getStatusCodeValue();
            outputMessage.getServletResponse().setStatus(returnStatus);
            if (returnStatus == 200) {
                HttpMethod method = inputMessage.getMethod();
                if ((HttpMethod.GET.equals((Object)method) || HttpMethod.HEAD.equals((Object)method)) && this.isResourceNotModified(inputMessage, outputMessage)) {
                    outputMessage.flush();
                    return;
                }
            } else if (returnStatus / 100 == 3 && (location = outputHeaders.getFirst("location")) != null) {
                this.saveFlashAttributes(mavContainer, webRequest, location);
            }
        }
        this.writeWithMessageConverters(responseEntity.getBody(), returnType, inputMessage, outputMessage);
        outputMessage.flush();
    }

    private List<String> getVaryRequestHeadersToAdd(HttpHeaders responseHeaders, HttpHeaders entityHeaders) {
        List entityHeadersVary = entityHeaders.getVary();
        List vary = responseHeaders.get((Object)"Vary");
        if (vary != null) {
            ArrayList<String> result = new ArrayList<String>(entityHeadersVary);
            for (String header : vary) {
                for (String existing : StringUtils.tokenizeToStringArray((String)header, (String)",")) {
                    if ("*".equals(existing)) {
                        return Collections.emptyList();
                    }
                    for (String value : entityHeadersVary) {
                        if (!value.equalsIgnoreCase(existing)) continue;
                        result.remove(value);
                    }
                }
            }
            return result;
        }
        return entityHeadersVary;
    }

    private boolean isResourceNotModified(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request.getServletRequest(), response.getServletResponse());
        HttpHeaders responseHeaders = response.getHeaders();
        String etag = responseHeaders.getETag();
        long lastModifiedTimestamp = responseHeaders.getLastModified();
        if (request.getMethod() == HttpMethod.GET || request.getMethod() == HttpMethod.HEAD) {
            responseHeaders.remove((Object)"ETag");
            responseHeaders.remove((Object)"Last-Modified");
        }
        return servletWebRequest.checkNotModified(etag, lastModifiedTimestamp);
    }

    private void saveFlashAttributes(ModelAndViewContainer mav, NativeWebRequest request, String location) {
        Map<String, ?> flashAttributes;
        mav.setRedirectModelScenario(true);
        ModelMap model = mav.getModel();
        if (model instanceof RedirectAttributes && !CollectionUtils.isEmpty(flashAttributes = ((RedirectAttributes)model).getFlashAttributes())) {
            HttpServletRequest req = (HttpServletRequest)request.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse res = (HttpServletResponse)request.getNativeResponse(HttpServletResponse.class);
            if (req != null) {
                RequestContextUtils.getOutputFlashMap(req).putAll(flashAttributes);
                if (res != null) {
                    RequestContextUtils.saveOutputFlashMap(location, req, res);
                }
            }
        }
    }

    @Override
    protected Class<?> getReturnValueType(@Nullable Object returnValue, MethodParameter returnType) {
        if (returnValue != null) {
            return returnValue.getClass();
        }
        Object type = this.getHttpEntityType(returnType);
        type = type != null ? type : Object.class;
        return ResolvableType.forMethodParameter((MethodParameter)returnType, (Type)type).toClass();
    }
}

