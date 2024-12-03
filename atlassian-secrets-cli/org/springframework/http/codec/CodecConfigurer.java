/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec;

import java.util.List;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;

public interface CodecConfigurer {
    public DefaultCodecs defaultCodecs();

    public CustomCodecs customCodecs();

    public void registerDefaults(boolean var1);

    public List<HttpMessageReader<?>> getReaders();

    public List<HttpMessageWriter<?>> getWriters();

    public static interface CustomCodecs {
        public void decoder(Decoder<?> var1);

        public void encoder(Encoder<?> var1);

        public void reader(HttpMessageReader<?> var1);

        public void writer(HttpMessageWriter<?> var1);
    }

    public static interface DefaultCodecs {
        public void jackson2JsonDecoder(Decoder<?> var1);

        public void jackson2JsonEncoder(Encoder<?> var1);
    }
}

