/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec;

import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.CodecConfigurerFactory;

public interface ServerCodecConfigurer
extends CodecConfigurer {
    @Override
    public ServerDefaultCodecs defaultCodecs();

    public static ServerCodecConfigurer create() {
        return CodecConfigurerFactory.create(ServerCodecConfigurer.class);
    }

    public static interface ServerDefaultCodecs
    extends CodecConfigurer.DefaultCodecs {
        public void serverSentEventEncoder(Encoder<?> var1);
    }
}

