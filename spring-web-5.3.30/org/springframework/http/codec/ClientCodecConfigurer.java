/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.codec.Decoder
 *  org.springframework.core.codec.Encoder
 */
package org.springframework.http.codec;

import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.CodecConfigurerFactory;
import org.springframework.http.codec.HttpMessageWriter;

public interface ClientCodecConfigurer
extends CodecConfigurer {
    @Override
    public ClientDefaultCodecs defaultCodecs();

    @Override
    public ClientCodecConfigurer clone();

    public static ClientCodecConfigurer create() {
        return CodecConfigurerFactory.create(ClientCodecConfigurer.class);
    }

    public static interface MultipartCodecs {
        public MultipartCodecs encoder(Encoder<?> var1);

        public MultipartCodecs writer(HttpMessageWriter<?> var1);
    }

    public static interface ClientDefaultCodecs
    extends CodecConfigurer.DefaultCodecs {
        public MultipartCodecs multipartCodecs();

        public void serverSentEventDecoder(Decoder<?> var1);
    }
}

