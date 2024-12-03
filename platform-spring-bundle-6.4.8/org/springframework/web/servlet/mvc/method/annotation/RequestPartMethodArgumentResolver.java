/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.multipart.support.RequestPartServletServerHttpRequest;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

public class RequestPartMethodArgumentResolver
extends AbstractMessageConverterMethodArgumentResolver {
    public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters, List<Object> requestResponseBodyAdvice) {
        super(messageConverters, requestResponseBodyAdvice);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(RequestPart.class)) {
            return true;
        }
        if (parameter.hasParameterAnnotation(RequestParam.class)) {
            return false;
        }
        return MultipartResolutionDelegate.isMultipartArgument(parameter.nestedIfOptional());
    }

    @Override
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest request, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Object arg;
        String name;
        boolean isRequired;
        HttpServletRequest servletRequest;
        block10: {
            servletRequest = request.getNativeRequest(HttpServletRequest.class);
            Assert.state(servletRequest != null, "No HttpServletRequest");
            RequestPart requestPart = parameter.getParameterAnnotation(RequestPart.class);
            isRequired = (requestPart == null || requestPart.required()) && !parameter.isOptional();
            name = this.getPartName(parameter, requestPart);
            parameter = parameter.nestedIfOptional();
            arg = null;
            Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
            if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
                arg = mpArg;
            } else {
                try {
                    RequestPartServletServerHttpRequest inputMessage = new RequestPartServletServerHttpRequest(servletRequest, name);
                    arg = this.readWithMessageConverters(inputMessage, parameter, parameter.getNestedGenericParameterType());
                    if (binderFactory != null) {
                        WebDataBinder binder = binderFactory.createBinder(request, arg, name);
                        if (arg != null) {
                            this.validateIfApplicable(binder, parameter);
                            if (binder.getBindingResult().hasErrors() && this.isBindExceptionRequired(binder, parameter)) {
                                throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                            }
                        }
                        if (mavContainer != null) {
                            mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
                        }
                    }
                }
                catch (MultipartException | MissingServletRequestPartException ex) {
                    if (!isRequired) break block10;
                    throw ex;
                }
            }
        }
        if (arg == null && isRequired) {
            if (!MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            }
            throw new MissingServletRequestPartException(name);
        }
        return this.adaptArgumentIfNecessary(arg, parameter);
    }

    private String getPartName(MethodParameter methodParam, @Nullable RequestPart requestPart) {
        String partName;
        String string = partName = requestPart != null ? requestPart.name() : "";
        if (partName.isEmpty() && (partName = methodParam.getParameterName()) == null) {
            throw new IllegalArgumentException("Request part name for argument type [" + methodParam.getNestedParameterType().getName() + "] not specified, and parameter name information not found in class file either.");
        }
        return partName;
    }

    @Override
    void closeStreamIfNecessary(InputStream body2) {
        try {
            body2.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

