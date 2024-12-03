/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.support;

import java.util.List;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.PartHttpMessageWriter;
import org.springframework.http.codec.support.BaseDefaultCodecs;
import org.springframework.lang.Nullable;

class ServerDefaultCodecsImpl
extends BaseDefaultCodecs
implements ServerCodecConfigurer.ServerDefaultCodecs {
    @Nullable
    private HttpMessageReader<?> multipartReader;
    @Nullable
    private Encoder<?> sseEncoder;

    ServerDefaultCodecsImpl() {
    }

    ServerDefaultCodecsImpl(ServerDefaultCodecsImpl other) {
        super(other);
        this.multipartReader = other.multipartReader;
        this.sseEncoder = other.sseEncoder;
    }

    @Override
    public void multipartReader(HttpMessageReader<?> reader) {
        this.multipartReader = reader;
        this.initTypedReaders();
    }

    @Override
    public void serverSentEventEncoder(Encoder<?> encoder) {
        this.sseEncoder = encoder;
        this.initObjectWriters();
    }

    @Override
    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
        if (this.multipartReader != null) {
            this.addCodec(typedReaders, this.multipartReader);
            return;
        }
        DefaultPartHttpMessageReader partReader = new DefaultPartHttpMessageReader();
        this.addCodec(typedReaders, partReader);
        this.addCodec(typedReaders, new MultipartHttpMessageReader(partReader));
    }

    @Override
    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
        this.addCodec(typedWriters, new PartHttpMessageWriter());
    }

    @Override
    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
        objectWriters.add(new ServerSentEventHttpMessageWriter(this.getSseEncoder()));
    }

    @Nullable
    private Encoder<?> getSseEncoder() {
        return this.sseEncoder != null ? this.sseEncoder : (jackson2Present ? this.getJackson2JsonEncoder() : (kotlinSerializationJsonPresent ? this.getKotlinSerializationJsonEncoder() : null));
    }
}

