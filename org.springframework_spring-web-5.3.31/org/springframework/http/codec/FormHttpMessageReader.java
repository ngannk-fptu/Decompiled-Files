/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Hints
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FormHttpMessageReader
extends LoggingCodecSupport
implements HttpMessageReader<MultiValueMap<String, String>> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final ResolvableType MULTIVALUE_STRINGS_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, (Class[])new Class[]{String.class, String.class});
    private Charset defaultCharset = DEFAULT_CHARSET;
    private int maxInMemorySize = 262144;

    public void setDefaultCharset(Charset charset) {
        Assert.notNull((Object)charset, (String)"Charset must not be null");
        this.defaultCharset = charset;
    }

    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        boolean multiValueUnresolved = elementType.hasUnresolvableGenerics() && MultiValueMap.class.isAssignableFrom(elementType.toClass());
        return !(!MULTIVALUE_STRINGS_TYPE.isAssignableFrom(elementType) && !multiValueUnresolved || mediaType != null && !MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType));
    }

    @Override
    public Flux<MultiValueMap<String, String>> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.from(this.readMono(elementType, message, hints));
    }

    @Override
    public Mono<MultiValueMap<String, String>> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        MediaType contentType = message.getHeaders().getContentType();
        Charset charset = this.getMediaTypeCharset(contentType);
        return DataBufferUtils.join(message.getBody(), (int)this.maxInMemorySize).map(buffer -> {
            CharBuffer charBuffer = charset.decode(buffer.asByteBuffer());
            String body = charBuffer.toString();
            DataBufferUtils.release((DataBuffer)buffer);
            MultiValueMap<String, String> formData = this.parseFormData(charset, body);
            this.logFormData(formData, hints);
            return formData;
        });
    }

    private void logFormData(MultiValueMap<String, String> formData, Map<String, Object> hints) {
        LogFormatUtils.traceDebug((Log)this.logger, traceOn -> Hints.getLogPrefix((Map)hints) + "Read " + (this.isEnableLoggingRequestDetails() ? LogFormatUtils.formatValue((Object)formData, (traceOn == false ? 1 : 0) != 0) : "form fields " + formData.keySet() + " (content masked)"));
    }

    private Charset getMediaTypeCharset(@Nullable MediaType mediaType) {
        if (mediaType != null && mediaType.getCharset() != null) {
            return mediaType.getCharset();
        }
        return this.getDefaultCharset();
    }

    private MultiValueMap<String, String> parseFormData(Charset charset, String body) {
        String[] pairs = StringUtils.tokenizeToStringArray((String)body, (String)"&");
        LinkedMultiValueMap result = new LinkedMultiValueMap(pairs.length);
        try {
            for (String pair : pairs) {
                int idx = pair.indexOf(61);
                if (idx == -1) {
                    result.add((Object)URLDecoder.decode(pair, charset.name()), null);
                    continue;
                }
                String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                result.add((Object)name, (Object)value);
            }
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
        return result;
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED);
    }
}

