/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpRequest
 *  org.springframework.http.InvalidMediaTypeException
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.GenericHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.HttpMessageNotReadableException
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StreamUtils
 *  org.springframework.validation.Errors
 *  org.springframework.validation.annotation.ValidationAnnotationUtils
 *  org.springframework.web.HttpMediaTypeNotSupportedException
 *  org.springframework.web.bind.WebDataBinder
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.ValidationAnnotationUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyAdviceChain;

public abstract class AbstractMessageConverterMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    private static final Set<HttpMethod> SUPPORTED_METHODS = EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);
    private static final Object NO_VALUE = new Object();
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected final List<HttpMessageConverter<?>> messageConverters;
    private final RequestResponseBodyAdviceChain advice;

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters) {
        this(converters, null);
    }

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters, @Nullable List<Object> requestResponseBodyAdvice) {
        Assert.notEmpty(converters, (String)"'messageConverters' must not be empty");
        this.messageConverters = converters;
        this.advice = new RequestResponseBodyAdviceChain(requestResponseBodyAdvice);
    }

    RequestResponseBodyAdviceChain getAdvice() {
        return this.advice;
    }

    @Nullable
    protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter, Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        ServletServerHttpRequest inputMessage = this.createInputMessage(webRequest);
        return this.readWithMessageConverters((HttpInputMessage)inputMessage, parameter, paramType);
    }

    @Nullable
    protected <T> Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        Class<Object> targetClass;
        MediaType contentType;
        boolean noContentType = false;
        try {
            contentType = inputMessage.getHeaders().getContentType();
        }
        catch (InvalidMediaTypeException ex) {
            throw new HttpMediaTypeNotSupportedException(ex.getMessage());
        }
        if (contentType == null) {
            noContentType = true;
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        Class contextClass = parameter.getContainingClass();
        Class<Object> clazz = targetClass = targetType instanceof Class ? (Class<Object>)((Object)targetType) : null;
        if (targetClass == null) {
            ResolvableType resolvableType = ResolvableType.forMethodParameter((MethodParameter)parameter);
            targetClass = resolvableType.resolve();
        }
        HttpMethod httpMethod = inputMessage instanceof HttpRequest ? ((HttpRequest)inputMessage).getMethod() : null;
        Object body2 = NO_VALUE;
        EmptyBodyCheckingHttpInputMessage message = null;
        try {
            message = new EmptyBodyCheckingHttpInputMessage(inputMessage);
            for (HttpMessageConverter<?> converter : this.messageConverters) {
                GenericHttpMessageConverter genericConverter;
                Class<?> converterType = converter.getClass();
                GenericHttpMessageConverter genericHttpMessageConverter = genericConverter = converter instanceof GenericHttpMessageConverter ? (GenericHttpMessageConverter)converter : null;
                if (!(genericConverter != null ? genericConverter.canRead(targetType, contextClass, contentType) : targetClass != null && converter.canRead((Class)targetClass, contentType))) continue;
                if (message.hasBody()) {
                    HttpInputMessage msgToUse = this.getAdvice().beforeBodyRead(message, parameter, targetType, converterType);
                    body2 = genericConverter != null ? genericConverter.read(targetType, contextClass, msgToUse) : converter.read(targetClass, msgToUse);
                    body2 = this.getAdvice().afterBodyRead(body2, msgToUse, parameter, targetType, converterType);
                } else {
                    body2 = this.getAdvice().handleEmptyBody(null, message, parameter, targetType, converterType);
                }
                break;
            }
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("I/O error while reading input message", (Throwable)ex, inputMessage);
        }
        finally {
            if (message != null && message.hasBody()) {
                this.closeStreamIfNecessary(message.getBody());
            }
        }
        if (body2 == NO_VALUE) {
            if (httpMethod == null || !SUPPORTED_METHODS.contains(httpMethod) || noContentType && !message.hasBody()) {
                return null;
            }
            throw new HttpMediaTypeNotSupportedException(contentType, this.getSupportedMediaTypes(targetClass != null ? targetClass : Object.class));
        }
        MediaType selectedContentType = contentType;
        Object theBody = body2;
        LogFormatUtils.traceDebug((Log)this.logger, traceOn -> {
            String formatted = LogFormatUtils.formatValue((Object)theBody, (traceOn == false ? 1 : 0) != 0);
            return "Read \"" + selectedContentType + "\" to [" + formatted + "]";
        });
        return body2;
    }

    protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state((servletRequest != null ? 1 : 0) != 0, (String)"No HttpServletRequest");
        return new ServletServerHttpRequest(servletRequest);
    }

    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations;
        for (Annotation ann : annotations = parameter.getParameterAnnotations()) {
            Object[] validationHints = ValidationAnnotationUtils.determineValidationHints((Annotation)ann);
            if (validationHints == null) continue;
            binder.validate(validationHints);
            break;
        }
    }

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i2 = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = paramTypes.length > i2 + 1 && Errors.class.isAssignableFrom(paramTypes[i2 + 1]);
        return !hasBindingResult;
    }

    protected List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        LinkedHashSet mediaTypeSet = new LinkedHashSet();
        for (HttpMessageConverter<?> converter : this.messageConverters) {
            mediaTypeSet.addAll(converter.getSupportedMediaTypes(clazz));
        }
        ArrayList<MediaType> result = new ArrayList<MediaType>(mediaTypeSet);
        MediaType.sortBySpecificity(result);
        return result;
    }

    @Nullable
    protected Object adaptArgumentIfNecessary(@Nullable Object arg, MethodParameter parameter) {
        if (parameter.getParameterType() == Optional.class) {
            if (arg == null || arg instanceof Collection && ((Collection)arg).isEmpty() || arg instanceof Object[] && ((Object[])arg).length == 0) {
                return Optional.empty();
            }
            return Optional.of(arg);
        }
        return arg;
    }

    void closeStreamIfNecessary(InputStream body2) {
    }

    private static class EmptyBodyCheckingHttpInputMessage
    implements HttpInputMessage {
        private final HttpHeaders headers;
        @Nullable
        private final InputStream body;

        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = inputStream.read() != -1 ? inputStream : null;
                inputStream.reset();
            } else {
                PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
                int b = pushbackInputStream.read();
                if (b == -1) {
                    this.body = null;
                } else {
                    this.body = pushbackInputStream;
                    pushbackInputStream.unread(b);
                }
            }
        }

        public HttpHeaders getHeaders() {
            return this.headers;
        }

        public InputStream getBody() {
            return this.body != null ? this.body : StreamUtils.emptyInput();
        }

        public boolean hasBody() {
            return this.body != null;
        }
    }
}

