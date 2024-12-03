/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec;

import java.util.List;
import java.util.function.Consumer;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.lang.Nullable;

public interface CodecConfigurer {
    public DefaultCodecs defaultCodecs();

    public CustomCodecs customCodecs();

    public void registerDefaults(boolean var1);

    public List<HttpMessageReader<?>> getReaders();

    public List<HttpMessageWriter<?>> getWriters();

    public CodecConfigurer clone();

    public static interface DefaultCodecConfig {
        @Nullable
        public Integer maxInMemorySize();

        @Nullable
        public Boolean isEnableLoggingRequestDetails();
    }

    public static interface CustomCodecs {
        public void register(Object var1);

        public void registerWithDefaultConfig(Object var1);

        public void registerWithDefaultConfig(Object var1, Consumer<DefaultCodecConfig> var2);

        @Deprecated
        public void decoder(Decoder<?> var1);

        @Deprecated
        public void encoder(Encoder<?> var1);

        @Deprecated
        public void reader(HttpMessageReader<?> var1);

        @Deprecated
        public void writer(HttpMessageWriter<?> var1);

        @Deprecated
        public void withDefaultCodecConfig(Consumer<DefaultCodecConfig> var1);
    }

    public static interface DefaultCodecs {
        public void jackson2JsonDecoder(Decoder<?> var1);

        public void jackson2JsonEncoder(Encoder<?> var1);

        public void jackson2SmileDecoder(Decoder<?> var1);

        public void jackson2SmileEncoder(Encoder<?> var1);

        public void protobufDecoder(Decoder<?> var1);

        public void protobufEncoder(Encoder<?> var1);

        public void jaxb2Decoder(Decoder<?> var1);

        public void jaxb2Encoder(Encoder<?> var1);

        public void kotlinSerializationJsonDecoder(Decoder<?> var1);

        public void kotlinSerializationJsonEncoder(Encoder<?> var1);

        public void configureDefaultCodec(Consumer<Object> var1);

        public void maxInMemorySize(int var1);

        public void enableLoggingRequestDetails(boolean var1);
    }
}

