/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

public class StringHttpMessageConverter
extends AbstractHttpMessageConverter<String> {
    private static final MediaType APPLICATION_PLUS_JSON = new MediaType("application", "*+json");
    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
    @Nullable
    private volatile List<Charset> availableCharsets;
    private boolean writeAcceptCharset = false;

    public StringHttpMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    public StringHttpMessageConverter(Charset defaultCharset) {
        super(defaultCharset, MediaType.TEXT_PLAIN, MediaType.ALL);
    }

    public void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.writeAcceptCharset = writeAcceptCharset;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class == clazz;
    }

    @Override
    protected String readInternal(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException {
        Charset charset = this.getContentTypeCharset(inputMessage.getHeaders().getContentType());
        return StreamUtils.copyToString(inputMessage.getBody(), charset);
    }

    @Override
    protected Long getContentLength(String str, @Nullable MediaType contentType) {
        Charset charset = this.getContentTypeCharset(contentType);
        return str.getBytes(charset).length;
    }

    @Override
    protected void addDefaultHeaders(HttpHeaders headers, String s, @Nullable MediaType type) throws IOException {
        if (headers.getContentType() == null && type != null && type.isConcrete() && (type.isCompatibleWith(MediaType.APPLICATION_JSON) || type.isCompatibleWith(APPLICATION_PLUS_JSON))) {
            headers.setContentType(type);
        }
        super.addDefaultHeaders(headers, s, type);
    }

    @Override
    protected void writeInternal(String str, HttpOutputMessage outputMessage) throws IOException {
        HttpHeaders headers = outputMessage.getHeaders();
        if (this.writeAcceptCharset && headers.get("Accept-Charset") == null) {
            headers.setAcceptCharset(this.getAcceptedCharsets());
        }
        Charset charset = this.getContentTypeCharset(headers.getContentType());
        StreamUtils.copy(str, charset, outputMessage.getBody());
    }

    protected List<Charset> getAcceptedCharsets() {
        List<Charset> charsets = this.availableCharsets;
        if (charsets == null) {
            this.availableCharsets = charsets = new ArrayList<Charset>(Charset.availableCharsets().values());
        }
        return charsets;
    }

    private Charset getContentTypeCharset(@Nullable MediaType contentType) {
        Charset charset;
        if (contentType != null) {
            charset = contentType.getCharset();
            if (charset != null) {
                return charset;
            }
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON) || contentType.isCompatibleWith(APPLICATION_PLUS_JSON)) {
                return StandardCharsets.UTF_8;
            }
        }
        Assert.state((charset = this.getDefaultCharset()) != null, "No default charset");
        return charset;
    }
}

