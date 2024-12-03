/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec;

import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.CodecConfigurerFactory;
import org.springframework.http.codec.HttpMessageReader;

public interface ServerCodecConfigurer
extends CodecConfigurer {
    @Override
    public ServerDefaultCodecs defaultCodecs();

    @Override
    public ServerCodecConfigurer clone();

    public static ServerCodecConfigurer create() {
        return CodecConfigurerFactory.create(ServerCodecConfigurer.class);
    }

    public static interface ServerDefaultCodecs
    extends CodecConfigurer.DefaultCodecs {
        public void multipartReader(HttpMessageReader<?> var1);

        public void serverSentEventEncoder(Encoder<?> var1);
    }
}

