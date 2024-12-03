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
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.http.codec.support.BaseDefaultCodecs;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

class ServerDefaultCodecsImpl
extends BaseDefaultCodecs
implements ServerCodecConfigurer.ServerDefaultCodecs {
    private static final boolean synchronossMultipartPresent = ClassUtils.isPresent("org.synchronoss.cloud.nio.multipart.NioMultipartParser", DefaultServerCodecConfigurer.class.getClassLoader());
    @Nullable
    private Encoder<?> sseEncoder;

    ServerDefaultCodecsImpl() {
    }

    @Override
    public void serverSentEventEncoder(Encoder<?> encoder) {
        this.sseEncoder = encoder;
    }

    @Override
    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
        if (synchronossMultipartPresent) {
            SynchronossPartHttpMessageReader partReader = new SynchronossPartHttpMessageReader();
            typedReaders.add(partReader);
            typedReaders.add(new MultipartHttpMessageReader(partReader));
        }
    }

    @Override
    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
        objectWriters.add(new ServerSentEventHttpMessageWriter(this.getSseEncoder()));
    }

    @Nullable
    private Encoder<?> getSseEncoder() {
        return this.sseEncoder != null ? this.sseEncoder : (jackson2Present ? this.getJackson2JsonEncoder() : null);
    }
}

