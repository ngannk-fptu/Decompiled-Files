/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.MessageBodyClientHttpResponseWrapper;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.UnknownContentTypeException;

public class HttpMessageConverterExtractor<T>
implements ResponseExtractor<T> {
    private final Type responseType;
    @Nullable
    private final Class<T> responseClass;
    private final List<HttpMessageConverter<?>> messageConverters;
    private final Log logger;

    public HttpMessageConverterExtractor(Class<T> responseType, List<HttpMessageConverter<?>> messageConverters) {
        this((Type)responseType, messageConverters);
    }

    public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
        this(responseType, messageConverters, LogFactory.getLog(HttpMessageConverterExtractor.class));
    }

    HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters, Log logger) {
        Assert.notNull((Object)responseType, "'responseType' must not be null");
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        Assert.noNullElements(messageConverters, "'messageConverters' must not contain null elements");
        this.responseType = responseType;
        this.responseClass = responseType instanceof Class ? (Class)responseType : null;
        this.messageConverters = messageConverters;
        this.logger = logger;
    }

    @Override
    public T extractData(ClientHttpResponse response) throws IOException {
        MessageBodyClientHttpResponseWrapper responseWrapper = new MessageBodyClientHttpResponseWrapper(response);
        if (!responseWrapper.hasMessageBody() || responseWrapper.hasEmptyMessageBody()) {
            return null;
        }
        MediaType contentType = this.getContentType(responseWrapper);
        try {
            for (HttpMessageConverter<T> httpMessageConverter : this.messageConverters) {
                GenericHttpMessageConverter genericMessageConverter;
                if (httpMessageConverter instanceof GenericHttpMessageConverter && (genericMessageConverter = (GenericHttpMessageConverter)httpMessageConverter).canRead(this.responseType, null, contentType)) {
                    if (this.logger.isDebugEnabled()) {
                        ResolvableType resolvableType = ResolvableType.forType(this.responseType);
                        this.logger.debug((Object)("Reading to [" + resolvableType + "]"));
                    }
                    return genericMessageConverter.read(this.responseType, null, responseWrapper);
                }
                if (this.responseClass == null || !httpMessageConverter.canRead(this.responseClass, contentType)) continue;
                if (this.logger.isDebugEnabled()) {
                    String className = this.responseClass.getName();
                    this.logger.debug((Object)("Reading to [" + className + "] as \"" + contentType + "\""));
                }
                return (T)httpMessageConverter.read(this.responseClass, responseWrapper);
            }
        }
        catch (IOException | HttpMessageNotReadableException ex) {
            throw new RestClientException("Error while extracting response for type [" + this.responseType + "] and content type [" + contentType + "]", ex);
        }
        throw new UnknownContentTypeException(this.responseType, contentType, responseWrapper.getRawStatusCode(), responseWrapper.getStatusText(), responseWrapper.getHeaders(), HttpMessageConverterExtractor.getResponseBody(responseWrapper));
    }

    protected MediaType getContentType(ClientHttpResponse response) {
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)"No content-type, using 'application/octet-stream'");
            }
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return contentType;
    }

    private static byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        }
        catch (IOException iOException) {
            return new byte[0];
        }
    }
}

