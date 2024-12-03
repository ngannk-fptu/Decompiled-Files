/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.support;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.ByteBufferDecoder;
import org.springframework.core.codec.ByteBufferEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.DataBufferDecoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.ResourceDecoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2SmileDecoder;
import org.springframework.http.codec.json.Jackson2SmileEncoder;
import org.springframework.http.codec.support.BaseCodecConfigurer;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

class BaseDefaultCodecs
implements CodecConfigurer.DefaultCodecs {
    static final boolean jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", BaseCodecConfigurer.class.getClassLoader()) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", BaseCodecConfigurer.class.getClassLoader());
    private static final boolean jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", BaseCodecConfigurer.class.getClassLoader());
    private static final boolean jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", BaseCodecConfigurer.class.getClassLoader());
    @Nullable
    private Decoder<?> jackson2JsonDecoder;
    @Nullable
    private Encoder<?> jackson2JsonEncoder;
    private boolean registerDefaults = true;

    BaseDefaultCodecs() {
    }

    @Override
    public void jackson2JsonDecoder(Decoder<?> decoder) {
        this.jackson2JsonDecoder = decoder;
    }

    @Override
    public void jackson2JsonEncoder(Encoder<?> encoder) {
        this.jackson2JsonEncoder = encoder;
    }

    void registerDefaults(boolean registerDefaults) {
        this.registerDefaults = registerDefaults;
    }

    final List<HttpMessageReader<?>> getTypedReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList readers = new ArrayList();
        readers.add(new DecoderHttpMessageReader<byte[]>(new ByteArrayDecoder()));
        readers.add(new DecoderHttpMessageReader<ByteBuffer>(new ByteBufferDecoder()));
        readers.add(new DecoderHttpMessageReader<DataBuffer>(new DataBufferDecoder()));
        readers.add(new DecoderHttpMessageReader<Resource>(new ResourceDecoder()));
        readers.add(new DecoderHttpMessageReader<String>(StringDecoder.textPlainOnly()));
        readers.add(new FormHttpMessageReader());
        this.extendTypedReaders(readers);
        return readers;
    }

    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
    }

    final List<HttpMessageReader<?>> getObjectReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList readers = new ArrayList();
        if (jackson2Present) {
            readers.add(new DecoderHttpMessageReader(this.getJackson2JsonDecoder()));
        }
        if (jackson2SmilePresent) {
            readers.add(new DecoderHttpMessageReader<Object>(new Jackson2SmileDecoder()));
        }
        if (jaxb2Present) {
            readers.add(new DecoderHttpMessageReader<Object>(new Jaxb2XmlDecoder()));
        }
        this.extendObjectReaders(readers);
        return readers;
    }

    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
    }

    final List<HttpMessageReader<?>> getCatchAllReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList result = new ArrayList();
        result.add(new DecoderHttpMessageReader<String>(StringDecoder.allMimeTypes()));
        return result;
    }

    final List<HttpMessageWriter<?>> getTypedWriters(boolean forMultipart) {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList writers = new ArrayList();
        writers.add(new EncoderHttpMessageWriter<byte[]>(new ByteArrayEncoder()));
        writers.add(new EncoderHttpMessageWriter<ByteBuffer>(new ByteBufferEncoder()));
        writers.add(new EncoderHttpMessageWriter<DataBuffer>(new DataBufferEncoder()));
        writers.add(new ResourceHttpMessageWriter());
        writers.add(new EncoderHttpMessageWriter<CharSequence>(CharSequenceEncoder.textPlainOnly()));
        if (!forMultipart) {
            this.extendTypedWriters(writers);
        }
        return writers;
    }

    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
    }

    final List<HttpMessageWriter<?>> getObjectWriters(boolean forMultipart) {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList writers = new ArrayList();
        if (jackson2Present) {
            writers.add(new EncoderHttpMessageWriter(this.getJackson2JsonEncoder()));
        }
        if (jackson2SmilePresent) {
            writers.add(new EncoderHttpMessageWriter<Object>(new Jackson2SmileEncoder()));
        }
        if (jaxb2Present) {
            writers.add(new EncoderHttpMessageWriter<Object>(new Jaxb2XmlEncoder()));
        }
        if (!forMultipart) {
            this.extendObjectWriters(writers);
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

    protected Decoder<?> getJackson2JsonDecoder() {
        return this.jackson2JsonDecoder != null ? this.jackson2JsonDecoder : new Jackson2JsonDecoder();
    }

    protected Encoder<?> getJackson2JsonEncoder() {
        return this.jackson2JsonEncoder != null ? this.jackson2JsonEncoder : new Jackson2JsonEncoder();
    }
}

