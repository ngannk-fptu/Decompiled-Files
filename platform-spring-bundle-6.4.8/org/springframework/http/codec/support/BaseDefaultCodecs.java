/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.Message
 *  io.netty.buffer.ByteBuf
 */
package org.springframework.http.codec.support;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.core.SpringProperties;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.ByteBufferDecoder;
import org.springframework.core.codec.ByteBufferEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.DataBufferDecoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.NettyByteBufDecoder;
import org.springframework.core.codec.NettyByteBufEncoder;
import org.springframework.core.codec.ResourceDecoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageReader;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2SmileDecoder;
import org.springframework.http.codec.json.Jackson2SmileEncoder;
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder;
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.http.codec.protobuf.ProtobufHttpMessageWriter;
import org.springframework.http.codec.support.BaseCodecConfigurer;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

class BaseDefaultCodecs
implements CodecConfigurer.DefaultCodecs,
CodecConfigurer.DefaultCodecConfig {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
    static final boolean jackson2Present;
    private static final boolean jackson2SmilePresent;
    private static final boolean jaxb2Present;
    private static final boolean protobufPresent;
    static final boolean synchronossMultipartPresent;
    static final boolean nettyByteBufPresent;
    static final boolean kotlinSerializationJsonPresent;
    @Nullable
    private Decoder<?> jackson2JsonDecoder;
    @Nullable
    private Encoder<?> jackson2JsonEncoder;
    @Nullable
    private Encoder<?> jackson2SmileEncoder;
    @Nullable
    private Decoder<?> jackson2SmileDecoder;
    @Nullable
    private Decoder<?> protobufDecoder;
    @Nullable
    private Encoder<?> protobufEncoder;
    @Nullable
    private Decoder<?> jaxb2Decoder;
    @Nullable
    private Encoder<?> jaxb2Encoder;
    @Nullable
    private Decoder<?> kotlinSerializationJsonDecoder;
    @Nullable
    private Encoder<?> kotlinSerializationJsonEncoder;
    @Nullable
    private Consumer<Object> codecConsumer;
    @Nullable
    private Integer maxInMemorySize;
    @Nullable
    private Boolean enableLoggingRequestDetails;
    private boolean registerDefaults = true;
    private final List<HttpMessageReader<?>> typedReaders = new ArrayList();
    private final List<HttpMessageReader<?>> objectReaders = new ArrayList();
    private final List<HttpMessageWriter<?>> typedWriters = new ArrayList();
    private final List<HttpMessageWriter<?>> objectWriters = new ArrayList();

    BaseDefaultCodecs() {
        this.initReaders();
        this.initWriters();
    }

    protected void initReaders() {
        this.initTypedReaders();
        this.initObjectReaders();
    }

    protected void initWriters() {
        this.initTypedWriters();
        this.initObjectWriters();
    }

    protected BaseDefaultCodecs(BaseDefaultCodecs other) {
        this.jackson2JsonDecoder = other.jackson2JsonDecoder;
        this.jackson2JsonEncoder = other.jackson2JsonEncoder;
        this.jackson2SmileDecoder = other.jackson2SmileDecoder;
        this.jackson2SmileEncoder = other.jackson2SmileEncoder;
        this.protobufDecoder = other.protobufDecoder;
        this.protobufEncoder = other.protobufEncoder;
        this.jaxb2Decoder = other.jaxb2Decoder;
        this.jaxb2Encoder = other.jaxb2Encoder;
        this.kotlinSerializationJsonDecoder = other.kotlinSerializationJsonDecoder;
        this.kotlinSerializationJsonEncoder = other.kotlinSerializationJsonEncoder;
        this.codecConsumer = other.codecConsumer;
        this.maxInMemorySize = other.maxInMemorySize;
        this.enableLoggingRequestDetails = other.enableLoggingRequestDetails;
        this.registerDefaults = other.registerDefaults;
        this.typedReaders.addAll(other.typedReaders);
        this.objectReaders.addAll(other.objectReaders);
        this.typedWriters.addAll(other.typedWriters);
        this.objectWriters.addAll(other.objectWriters);
    }

    @Override
    public void jackson2JsonDecoder(Decoder<?> decoder) {
        this.jackson2JsonDecoder = decoder;
        this.initObjectReaders();
    }

    @Override
    public void jackson2JsonEncoder(Encoder<?> encoder) {
        this.jackson2JsonEncoder = encoder;
        this.initObjectWriters();
        this.initTypedWriters();
    }

    @Override
    public void jackson2SmileDecoder(Decoder<?> decoder) {
        this.jackson2SmileDecoder = decoder;
        this.initObjectReaders();
    }

    @Override
    public void jackson2SmileEncoder(Encoder<?> encoder) {
        this.jackson2SmileEncoder = encoder;
        this.initObjectWriters();
        this.initTypedWriters();
    }

    @Override
    public void protobufDecoder(Decoder<?> decoder) {
        this.protobufDecoder = decoder;
        this.initTypedReaders();
    }

    @Override
    public void protobufEncoder(Encoder<?> encoder) {
        this.protobufEncoder = encoder;
        this.initTypedWriters();
    }

    @Override
    public void jaxb2Decoder(Decoder<?> decoder) {
        this.jaxb2Decoder = decoder;
        this.initObjectReaders();
    }

    @Override
    public void jaxb2Encoder(Encoder<?> encoder) {
        this.jaxb2Encoder = encoder;
        this.initObjectWriters();
    }

    @Override
    public void kotlinSerializationJsonDecoder(Decoder<?> decoder) {
        this.kotlinSerializationJsonDecoder = decoder;
        this.initObjectReaders();
    }

    @Override
    public void kotlinSerializationJsonEncoder(Encoder<?> encoder) {
        this.kotlinSerializationJsonEncoder = encoder;
        this.initObjectWriters();
    }

    @Override
    public void configureDefaultCodec(Consumer<Object> codecConsumer) {
        this.codecConsumer = this.codecConsumer != null ? this.codecConsumer.andThen(codecConsumer) : codecConsumer;
        this.initReaders();
        this.initWriters();
    }

    @Override
    public void maxInMemorySize(int byteCount) {
        if (!ObjectUtils.nullSafeEquals(this.maxInMemorySize, byteCount)) {
            this.maxInMemorySize = byteCount;
            this.initReaders();
        }
    }

    @Override
    @Nullable
    public Integer maxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override
    public void enableLoggingRequestDetails(boolean enable) {
        if (!ObjectUtils.nullSafeEquals(this.enableLoggingRequestDetails, enable)) {
            this.enableLoggingRequestDetails = enable;
            this.initReaders();
            this.initWriters();
        }
    }

    @Override
    @Nullable
    public Boolean isEnableLoggingRequestDetails() {
        return this.enableLoggingRequestDetails;
    }

    void registerDefaults(boolean registerDefaults) {
        if (this.registerDefaults != registerDefaults) {
            this.registerDefaults = registerDefaults;
            this.initReaders();
            this.initWriters();
        }
    }

    final List<HttpMessageReader<?>> getTypedReaders() {
        return this.typedReaders;
    }

    protected void initTypedReaders() {
        this.typedReaders.clear();
        if (!this.registerDefaults) {
            return;
        }
        this.addCodec(this.typedReaders, new DecoderHttpMessageReader<byte[]>(new ByteArrayDecoder()));
        this.addCodec(this.typedReaders, new DecoderHttpMessageReader<ByteBuffer>(new ByteBufferDecoder()));
        this.addCodec(this.typedReaders, new DecoderHttpMessageReader<DataBuffer>(new DataBufferDecoder()));
        if (nettyByteBufPresent) {
            this.addCodec(this.typedReaders, new DecoderHttpMessageReader<ByteBuf>(new NettyByteBufDecoder()));
        }
        this.addCodec(this.typedReaders, new ResourceHttpMessageReader(new ResourceDecoder()));
        this.addCodec(this.typedReaders, new DecoderHttpMessageReader<String>(StringDecoder.textPlainOnly()));
        if (protobufPresent) {
            this.addCodec(this.typedReaders, new DecoderHttpMessageReader<Message>(this.protobufDecoder != null ? (ProtobufDecoder)this.protobufDecoder : new ProtobufDecoder()));
        }
        this.addCodec(this.typedReaders, new FormHttpMessageReader());
        this.extendTypedReaders(this.typedReaders);
    }

    protected <T> void addCodec(List<T> codecs, T codec) {
        this.initCodec(codec);
        codecs.add(codec);
    }

    private void initCodec(@Nullable Object codec) {
        Boolean enable;
        if (codec instanceof DecoderHttpMessageReader) {
            codec = ((DecoderHttpMessageReader)codec).getDecoder();
        } else if (codec instanceof EncoderHttpMessageWriter) {
            codec = ((EncoderHttpMessageWriter)codec).getEncoder();
        }
        if (codec == null) {
            return;
        }
        Integer size = this.maxInMemorySize;
        if (size != null) {
            if (codec instanceof AbstractDataBufferDecoder) {
                ((AbstractDataBufferDecoder)codec).setMaxInMemorySize(size);
            }
            if (protobufPresent && codec instanceof ProtobufDecoder) {
                ((ProtobufDecoder)codec).setMaxMessageSize(size);
            }
            if (kotlinSerializationJsonPresent && codec instanceof KotlinSerializationJsonDecoder) {
                ((KotlinSerializationJsonDecoder)codec).setMaxInMemorySize(size);
            }
            if (jackson2Present && codec instanceof AbstractJackson2Decoder) {
                ((AbstractJackson2Decoder)codec).setMaxInMemorySize(size);
            }
            if (jaxb2Present && !shouldIgnoreXml && codec instanceof Jaxb2XmlDecoder) {
                ((Jaxb2XmlDecoder)codec).setMaxInMemorySize(size);
            }
            if (codec instanceof FormHttpMessageReader) {
                ((FormHttpMessageReader)codec).setMaxInMemorySize(size);
            }
            if (codec instanceof ServerSentEventHttpMessageReader) {
                ((ServerSentEventHttpMessageReader)codec).setMaxInMemorySize(size);
            }
            if (codec instanceof DefaultPartHttpMessageReader) {
                ((DefaultPartHttpMessageReader)codec).setMaxInMemorySize(size);
            }
            if (synchronossMultipartPresent && codec instanceof SynchronossPartHttpMessageReader) {
                ((SynchronossPartHttpMessageReader)codec).setMaxInMemorySize(size);
            }
        }
        if ((enable = this.enableLoggingRequestDetails) != null) {
            if (codec instanceof FormHttpMessageReader) {
                ((FormHttpMessageReader)codec).setEnableLoggingRequestDetails(enable);
            }
            if (codec instanceof MultipartHttpMessageReader) {
                ((MultipartHttpMessageReader)codec).setEnableLoggingRequestDetails(enable);
            }
            if (codec instanceof DefaultPartHttpMessageReader) {
                ((DefaultPartHttpMessageReader)codec).setEnableLoggingRequestDetails(enable);
            }
            if (synchronossMultipartPresent && codec instanceof SynchronossPartHttpMessageReader) {
                ((SynchronossPartHttpMessageReader)codec).setEnableLoggingRequestDetails(enable);
            }
            if (codec instanceof FormHttpMessageWriter) {
                ((FormHttpMessageWriter)codec).setEnableLoggingRequestDetails(enable);
            }
            if (codec instanceof MultipartHttpMessageWriter) {
                ((MultipartHttpMessageWriter)codec).setEnableLoggingRequestDetails(enable);
            }
        }
        if (this.codecConsumer != null) {
            this.codecConsumer.accept(codec);
        }
        if (codec instanceof MultipartHttpMessageReader) {
            this.initCodec(((MultipartHttpMessageReader)codec).getPartReader());
        } else if (codec instanceof MultipartHttpMessageWriter) {
            this.initCodec(((MultipartHttpMessageWriter)codec).getFormWriter());
        } else if (codec instanceof ServerSentEventHttpMessageReader) {
            this.initCodec(((ServerSentEventHttpMessageReader)codec).getDecoder());
        } else if (codec instanceof ServerSentEventHttpMessageWriter) {
            this.initCodec(((ServerSentEventHttpMessageWriter)codec).getEncoder());
        }
    }

    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
    }

    final List<HttpMessageReader<?>> getObjectReaders() {
        return this.objectReaders;
    }

    protected void initObjectReaders() {
        this.objectReaders.clear();
        if (!this.registerDefaults) {
            return;
        }
        if (kotlinSerializationJsonPresent) {
            this.addCodec(this.objectReaders, new DecoderHttpMessageReader(this.getKotlinSerializationJsonDecoder()));
        }
        if (jackson2Present) {
            this.addCodec(this.objectReaders, new DecoderHttpMessageReader(this.getJackson2JsonDecoder()));
        }
        if (jackson2SmilePresent) {
            this.addCodec(this.objectReaders, new DecoderHttpMessageReader<Object>(this.jackson2SmileDecoder != null ? (Jackson2SmileDecoder)this.jackson2SmileDecoder : new Jackson2SmileDecoder()));
        }
        if (jaxb2Present && !shouldIgnoreXml) {
            this.addCodec(this.objectReaders, new DecoderHttpMessageReader<Object>(this.jaxb2Decoder != null ? (Jaxb2XmlDecoder)this.jaxb2Decoder : new Jaxb2XmlDecoder()));
        }
        this.extendObjectReaders(this.objectReaders);
    }

    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
    }

    final List<HttpMessageReader<?>> getCatchAllReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList readers = new ArrayList();
        this.addCodec(readers, new DecoderHttpMessageReader<String>(StringDecoder.allMimeTypes()));
        return readers;
    }

    final List<HttpMessageWriter<?>> getTypedWriters() {
        return this.typedWriters;
    }

    protected void initTypedWriters() {
        this.typedWriters.clear();
        if (!this.registerDefaults) {
            return;
        }
        this.typedWriters.addAll(this.getBaseTypedWriters());
        this.extendTypedWriters(this.typedWriters);
    }

    final List<HttpMessageWriter<?>> getBaseTypedWriters() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList writers = new ArrayList();
        this.addCodec(writers, new EncoderHttpMessageWriter<byte[]>(new ByteArrayEncoder()));
        this.addCodec(writers, new EncoderHttpMessageWriter<ByteBuffer>(new ByteBufferEncoder()));
        this.addCodec(writers, new EncoderHttpMessageWriter<DataBuffer>(new DataBufferEncoder()));
        if (nettyByteBufPresent) {
            this.addCodec(writers, new EncoderHttpMessageWriter<ByteBuf>(new NettyByteBufEncoder()));
        }
        this.addCodec(writers, new ResourceHttpMessageWriter());
        this.addCodec(writers, new EncoderHttpMessageWriter<CharSequence>(CharSequenceEncoder.textPlainOnly()));
        if (protobufPresent) {
            this.addCodec(writers, new ProtobufHttpMessageWriter(this.protobufEncoder != null ? (ProtobufEncoder)this.protobufEncoder : new ProtobufEncoder()));
        }
        return writers;
    }

    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
    }

    final List<HttpMessageWriter<?>> getObjectWriters() {
        return this.objectWriters;
    }

    protected void initObjectWriters() {
        this.objectWriters.clear();
        if (!this.registerDefaults) {
            return;
        }
        this.objectWriters.addAll(this.getBaseObjectWriters());
        this.extendObjectWriters(this.objectWriters);
    }

    final List<HttpMessageWriter<?>> getBaseObjectWriters() {
        ArrayList writers = new ArrayList();
        if (kotlinSerializationJsonPresent) {
            this.addCodec(writers, new EncoderHttpMessageWriter(this.getKotlinSerializationJsonEncoder()));
        }
        if (jackson2Present) {
            this.addCodec(writers, new EncoderHttpMessageWriter(this.getJackson2JsonEncoder()));
        }
        if (jackson2SmilePresent) {
            this.addCodec(writers, new EncoderHttpMessageWriter<Object>(this.jackson2SmileEncoder != null ? (Jackson2SmileEncoder)this.jackson2SmileEncoder : new Jackson2SmileEncoder()));
        }
        if (jaxb2Present && !shouldIgnoreXml) {
            this.addCodec(writers, new EncoderHttpMessageWriter<Object>(this.jaxb2Encoder != null ? (Jaxb2XmlEncoder)this.jaxb2Encoder : new Jaxb2XmlEncoder()));
        }
        return writers;
    }

    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
    }

    List<HttpMessageWriter<?>> getCatchAllWriters() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList result = new ArrayList();
        result.add(new EncoderHttpMessageWriter<CharSequence>(CharSequenceEncoder.allMimeTypes()));
        return result;
    }

    void applyDefaultConfig(BaseCodecConfigurer.DefaultCustomCodecs customCodecs) {
        this.applyDefaultConfig(customCodecs.getTypedReaders());
        this.applyDefaultConfig(customCodecs.getObjectReaders());
        this.applyDefaultConfig(customCodecs.getTypedWriters());
        this.applyDefaultConfig(customCodecs.getObjectWriters());
        customCodecs.getDefaultConfigConsumers().forEach(consumer -> consumer.accept(this));
    }

    private void applyDefaultConfig(Map<?, Boolean> readers) {
        readers.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).forEach(this::initCodec);
    }

    protected Decoder<?> getJackson2JsonDecoder() {
        if (this.jackson2JsonDecoder == null) {
            this.jackson2JsonDecoder = new Jackson2JsonDecoder();
        }
        return this.jackson2JsonDecoder;
    }

    protected Encoder<?> getJackson2JsonEncoder() {
        if (this.jackson2JsonEncoder == null) {
            this.jackson2JsonEncoder = new Jackson2JsonEncoder();
        }
        return this.jackson2JsonEncoder;
    }

    protected Decoder<?> getKotlinSerializationJsonDecoder() {
        if (this.kotlinSerializationJsonDecoder == null) {
            this.kotlinSerializationJsonDecoder = new KotlinSerializationJsonDecoder();
        }
        return this.kotlinSerializationJsonDecoder;
    }

    protected Encoder<?> getKotlinSerializationJsonEncoder() {
        if (this.kotlinSerializationJsonEncoder == null) {
            this.kotlinSerializationJsonEncoder = new KotlinSerializationJsonEncoder();
        }
        return this.kotlinSerializationJsonEncoder;
    }

    static {
        ClassLoader classLoader = BaseCodecConfigurer.class.getClassLoader();
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
        protobufPresent = ClassUtils.isPresent("com.google.protobuf.Message", classLoader);
        synchronossMultipartPresent = ClassUtils.isPresent("org.synchronoss.cloud.nio.multipart.NioMultipartParser", classLoader);
        nettyByteBufPresent = ClassUtils.isPresent("io.netty.buffer.ByteBuf", classLoader);
        kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
    }
}

