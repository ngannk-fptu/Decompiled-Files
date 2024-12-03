/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Hints
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

public class FormHttpMessageWriter
extends LoggingCodecSupport
implements HttpMessageWriter<MultiValueMap<String, String>> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final MediaType DEFAULT_FORM_DATA_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET);
    private static final List<MediaType> MEDIA_TYPES = Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED);
    private static final ResolvableType MULTIVALUE_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, (Class[])new Class[]{String.class, String.class});
    private Charset defaultCharset = DEFAULT_CHARSET;

    public void setDefaultCharset(Charset charset) {
        Assert.notNull((Object)charset, (String)"Charset must not be null");
        this.defaultCharset = charset;
    }

    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    @Override
    public List<MediaType> getWritableMediaTypes() {
        return MEDIA_TYPES;
    }

    @Override
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(elementType.toClass())) {
            return false;
        }
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            return true;
        }
        if (mediaType == null) {
            return MULTIVALUE_TYPE.isAssignableFrom(elementType);
        }
        return false;
    }

    @Override
    public Mono<Void> write(Publisher<? extends MultiValueMap<String, String>> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        mediaType = this.getMediaType(mediaType);
        message.getHeaders().setContentType(mediaType);
        Charset charset = mediaType.getCharset() != null ? mediaType.getCharset() : this.getDefaultCharset();
        return Mono.from(inputStream).flatMap(form -> {
            this.logFormData((MultiValueMap<String, String>)form, hints);
            String value = this.serializeForm((MultiValueMap<String, String>)form, charset);
            ByteBuffer byteBuffer = charset.encode(value);
            DataBuffer buffer = message.bufferFactory().wrap(byteBuffer);
            message.getHeaders().setContentLength(byteBuffer.remaining());
            return message.writeWith((Publisher<? extends DataBuffer>)Mono.just((Object)buffer));
        });
    }

    protected MediaType getMediaType(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return DEFAULT_FORM_DATA_MEDIA_TYPE;
        }
        if (mediaType.getCharset() == null) {
            return new MediaType(mediaType, this.getDefaultCharset());
        }
        return mediaType;
    }

    private void logFormData(MultiValueMap<String, String> form, Map<String, Object> hints) {
        LogFormatUtils.traceDebug((Log)this.logger, traceOn -> Hints.getLogPrefix((Map)hints) + "Writing " + (this.isEnableLoggingRequestDetails() ? LogFormatUtils.formatValue((Object)form, (traceOn == false ? 1 : 0) != 0) : "form fields " + form.keySet() + " (content masked)"));
    }

    protected String serializeForm(MultiValueMap<String, String> formData, Charset charset) {
        StringBuilder builder = new StringBuilder();
        formData.forEach((name, values) -> values.forEach(value -> {
            try {
                if (builder.length() != 0) {
                    builder.append('&');
                }
                builder.append(URLEncoder.encode(name, charset.name()));
                if (value != null) {
                    builder.append('=');
                    builder.append(URLEncoder.encode(value, charset.name()));
                }
            }
            catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }));
        return builder.toString();
    }
}

