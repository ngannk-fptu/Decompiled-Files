/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.io.JsonStringEncoder
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.sockjs.frame;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.sockjs.frame.AbstractSockJsMessageCodec;

public class Jackson2SockJsMessageCodec
extends AbstractSockJsMessageCodec {
    private final ObjectMapper objectMapper;

    public Jackson2SockJsMessageCodec() {
        this.objectMapper = Jackson2ObjectMapperBuilder.json().build();
    }

    public Jackson2SockJsMessageCodec(ObjectMapper objectMapper) {
        Assert.notNull((Object)objectMapper, (String)"ObjectMapper must not be null");
        this.objectMapper = objectMapper;
    }

    @Override
    @Nullable
    public String[] decode(String content) throws IOException {
        return (String[])this.objectMapper.readValue(content, String[].class);
    }

    @Override
    @Nullable
    public String[] decodeInputStream(InputStream content) throws IOException {
        return (String[])this.objectMapper.readValue(content, String[].class);
    }

    @Override
    protected char[] applyJsonQuoting(String content) {
        return JsonStringEncoder.getInstance().quoteAsString(content);
    }
}

