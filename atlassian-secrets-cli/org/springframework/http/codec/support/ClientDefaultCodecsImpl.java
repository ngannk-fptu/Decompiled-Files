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

    void setPartWritersSupplier(Supplier<List<HttpMessageWriter<?>>> supplier) {
        this.partWritersSupplier = supplier;
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
    }

    @Override
    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
        objectReaders.add(new ServerSentEventHttpMessageReader(this.getSseDecoder()));
    }

    @Nullable
    private Decoder<?> getSseDecoder() {
        return this.sseDecoder != null ? this.sseDecoder : (jackson2Present ? this.getJackson2JsonDecoder() : null);
    }

    @Override
    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
        typedWriters.add(new MultipartHttpMessageWriter(this.getPartWriters(), new FormHttpMessageWriter()));
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

    private static class DefaultMultipartCodecs
    implements ClientCodecConfigurer.MultipartCodecs {
        private final List<HttpMessageWriter<?>> writers = new ArrayList();

        private DefaultMultipartCodecs() {
        }

        @Override
        public ClientCodecConfigurer.MultipartCodecs encoder(Encoder<?> encoder) {
            this.writer(new EncoderHttpMessageWriter(encoder));
            return this;
        }

        @Override
        public ClientCodecConfigurer.MultipartCodecs writer(HttpMessageWriter<?> writer) {
            this.writers.add(writer);
            return this;
        }

        List<HttpMessageWriter<?>> getWriters() {
            return this.writers;
        }
    }
}

