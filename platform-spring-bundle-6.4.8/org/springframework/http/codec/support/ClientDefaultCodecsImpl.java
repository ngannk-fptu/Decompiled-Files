/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.http.codec.support.BaseDefaultCodecs;
import org.springframework.lang.Nullable;

class ClientDefaultCodecsImpl
extends BaseDefaultCodecs
implements ClientCodecConfigurer.ClientDefaultCodecs {
    @Nullable
    private DefaultMultipartCodecs multipartCodecs;
    @Nullable
    private Decoder<?> sseDecoder;
    @Nullable
    private Supplier<List<HttpMessageWriter<?>>> partWritersSupplier;

    ClientDefaultCodecsImpl() {
    }

    ClientDefaultCodecsImpl(ClientDefaultCodecsImpl other) {
        super(other);
        this.multipartCodecs = other.multipartCodecs != null ? new DefaultMultipartCodecs(other.multipartCodecs) : null;
        this.sseDecoder = other.sseDecoder;
    }

    void setPartWritersSupplier(Supplier<List<HttpMessageWriter<?>>> supplier) {
        this.partWritersSupplier = supplier;
        this.initTypedWriters();
    }

    @Override
    public ClientCodecConfigurer.MultipartCodecs multipartCodecs() {
        if (this.multipartCodecs == null) {
            this.multipartCodecs = new DefaultMultipartCodecs();
        }
        return this.multipartCodecs;
    }

    @Override
    public void serverSentEventDecoder(Decoder<?> decoder) {
        this.sseDecoder = decoder;
        this.initObjectReaders();
    }

    @Override
    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
        Decoder<?> decoder = this.sseDecoder != null ? this.sseDecoder : (jackson2Present ? this.getJackson2JsonDecoder() : (kotlinSerializationJsonPresent ? this.getKotlinSerializationJsonDecoder() : null));
        this.addCodec(objectReaders, new ServerSentEventHttpMessageReader(decoder));
    }

    @Override
    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
        this.addCodec(typedWriters, new MultipartHttpMessageWriter(this.getPartWriters(), new FormHttpMessageWriter()));
    }

    private List<HttpMessageWriter<?>> getPartWriters() {
        if (this.multipartCodecs != null) {
            return this.multipartCodecs.getWriters();
        }
        if (this.partWritersSupplier != null) {
            return this.partWritersSupplier.get();
        }
        return Collections.emptyList();
    }

    private class DefaultMultipartCodecs
    implements ClientCodecConfigurer.MultipartCodecs {
        private final List<HttpMessageWriter<?>> writers = new ArrayList();

        DefaultMultipartCodecs() {
        }

        DefaultMultipartCodecs(DefaultMultipartCodecs other) {
            this.writers.addAll(other.writers);
        }

        @Override
        public ClientCodecConfigurer.MultipartCodecs encoder(Encoder<?> encoder) {
            this.writer(new EncoderHttpMessageWriter(encoder));
            ClientDefaultCodecsImpl.this.initTypedWriters();
            return this;
        }

        @Override
        public ClientCodecConfigurer.MultipartCodecs writer(HttpMessageWriter<?> writer) {
            this.writers.add(writer);
            ClientDefaultCodecsImpl.this.initTypedWriters();
            return this;
        }

        List<HttpMessageWriter<?>> getWriters() {
            return this.writers;
        }
    }
}

