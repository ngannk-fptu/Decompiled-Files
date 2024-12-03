/*
 * Decompiled with CFR 0.152.
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

