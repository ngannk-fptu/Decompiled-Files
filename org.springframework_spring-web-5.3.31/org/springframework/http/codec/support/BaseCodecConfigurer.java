/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Decoder
 *  org.springframework.core.codec.Encoder
 *  org.springframework.util.Assert
 */
package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.support.BaseDefaultCodecs;
import org.springframework.util.Assert;

abstract class BaseCodecConfigurer
implements CodecConfigurer {
    protected final BaseDefaultCodecs defaultCodecs;
    protected final DefaultCustomCodecs customCodecs;

    BaseCodecConfigurer(BaseDefaultCodecs defaultCodecs) {
        Assert.notNull((Object)defaultCodecs, (String)"'defaultCodecs' is required");
        this.defaultCodecs = defaultCodecs;
        this.customCodecs = new DefaultCustomCodecs();
    }

    protected BaseCodecConfigurer(BaseCodecConfigurer other) {
        this.defaultCodecs = other.cloneDefaultCodecs();
        this.customCodecs = new DefaultCustomCodecs(other.customCodecs);
    }

    protected abstract BaseDefaultCodecs cloneDefaultCodecs();

    @Override
    public CodecConfigurer.DefaultCodecs defaultCodecs() {
        return this.defaultCodecs;
    }

    @Override
    public void registerDefaults(boolean shouldRegister) {
        this.defaultCodecs.registerDefaults(shouldRegister);
    }

    @Override
    public CodecConfigurer.CustomCodecs customCodecs() {
        return this.customCodecs;
    }

    @Override
    public List<HttpMessageReader<?>> getReaders() {
        this.defaultCodecs.applyDefaultConfig(this.customCodecs);
        ArrayList result = new ArrayList();
        result.addAll(this.customCodecs.getTypedReaders().keySet());
        result.addAll(this.defaultCodecs.getTypedReaders());
        result.addAll(this.customCodecs.getObjectReaders().keySet());
        result.addAll(this.defaultCodecs.getObjectReaders());
        result.addAll(this.defaultCodecs.getCatchAllReaders());
        return result;
    }

    @Override
    public List<HttpMessageWriter<?>> getWriters() {
        this.defaultCodecs.applyDefaultConfig(this.customCodecs);
        ArrayList result = new ArrayList();
        result.addAll(this.customCodecs.getTypedWriters().keySet());
        result.addAll(this.defaultCodecs.getTypedWriters());
        result.addAll(this.customCodecs.getObjectWriters().keySet());
        result.addAll(this.defaultCodecs.getObjectWriters());
        result.addAll(this.defaultCodecs.getCatchAllWriters());
        return result;
    }

    @Override
    public abstract CodecConfigurer clone();

    protected static final class DefaultCustomCodecs
    implements CodecConfigurer.CustomCodecs {
        private final Map<HttpMessageReader<?>, Boolean> typedReaders = new LinkedHashMap(4);
        private final Map<HttpMessageWriter<?>, Boolean> typedWriters = new LinkedHashMap(4);
        private final Map<HttpMessageReader<?>, Boolean> objectReaders = new LinkedHashMap(4);
        private final Map<HttpMessageWriter<?>, Boolean> objectWriters = new LinkedHashMap(4);
        private final List<Consumer<CodecConfigurer.DefaultCodecConfig>> defaultConfigConsumers = new ArrayList<Consumer<CodecConfigurer.DefaultCodecConfig>>(4);

        DefaultCustomCodecs() {
        }

        DefaultCustomCodecs(DefaultCustomCodecs other) {
            this.typedReaders.putAll(other.typedReaders);
            this.typedWriters.putAll(other.typedWriters);
            this.objectReaders.putAll(other.objectReaders);
            this.objectWriters.putAll(other.objectWriters);
        }

        @Override
        public void register(Object codec) {
            this.addCodec(codec, false);
        }

        @Override
        public void registerWithDefaultConfig(Object codec) {
            this.addCodec(codec, true);
        }

        @Override
        public void registerWithDefaultConfig(Object codec, Consumer<CodecConfigurer.DefaultCodecConfig> configConsumer) {
            this.addCodec(codec, false);
            this.defaultConfigConsumers.add(configConsumer);
        }

        @Override
        public void decoder(Decoder<?> decoder) {
            this.addCodec(decoder, false);
        }

        @Override
        public void encoder(Encoder<?> encoder) {
            this.addCodec(encoder, false);
        }

        @Override
        public void reader(HttpMessageReader<?> reader) {
            this.addCodec(reader, false);
        }

        @Override
        public void writer(HttpMessageWriter<?> writer) {
            this.addCodec(writer, false);
        }

        @Override
        public void withDefaultCodecConfig(Consumer<CodecConfigurer.DefaultCodecConfig> codecsConfigConsumer) {
            this.defaultConfigConsumers.add(codecsConfigConsumer);
        }

        private void addCodec(Object codec, boolean applyDefaultConfig) {
            if (codec instanceof Decoder) {
                codec = new DecoderHttpMessageReader((Decoder)codec);
            } else if (codec instanceof Encoder) {
                codec = new EncoderHttpMessageWriter((Encoder)codec);
            }
            if (codec instanceof HttpMessageReader) {
                HttpMessageReader reader = (HttpMessageReader)codec;
                boolean canReadToObject = reader.canRead(ResolvableType.forClass(Object.class), null);
                (canReadToObject ? this.objectReaders : this.typedReaders).put(reader, applyDefaultConfig);
            } else if (codec instanceof HttpMessageWriter) {
                HttpMessageWriter writer = (HttpMessageWriter)codec;
                boolean canWriteObject = writer.canWrite(ResolvableType.forClass(Object.class), null);
                (canWriteObject ? this.objectWriters : this.typedWriters).put(writer, applyDefaultConfig);
            } else {
                throw new IllegalArgumentException("Unexpected codec type: " + codec.getClass().getName());
            }
        }

        Map<HttpMessageReader<?>, Boolean> getTypedReaders() {
            return this.typedReaders;
        }

        Map<HttpMessageWriter<?>, Boolean> getTypedWriters() {
            return this.typedWriters;
        }

        Map<HttpMessageReader<?>, Boolean> getObjectReaders() {
            return this.objectReaders;
        }

        Map<HttpMessageWriter<?>, Boolean> getObjectWriters() {
            return this.objectWriters;
        }

        List<Consumer<CodecConfigurer.DefaultCodecConfig>> getDefaultConfigConsumers() {
            return this.defaultConfigConsumers;
        }
    }
}

