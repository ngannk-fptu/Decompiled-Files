/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.List;
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

class BaseCodecConfigurer
implements CodecConfigurer {
    private final BaseDefaultCodecs defaultCodecs;
    private final DefaultCustomCodecs customCodecs = new DefaultCustomCodecs();

    BaseCodecConfigurer(BaseDefaultCodecs defaultCodecs) {
        Assert.notNull((Object)defaultCodecs, "'defaultCodecs' is required");
        this.defaultCodecs = defaultCodecs;
    }

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
        ArrayList result = new ArrayList();
        result.addAll(this.defaultCodecs.getTypedReaders());
        result.addAll(this.customCodecs.getTypedReaders());
        result.addAll(this.defaultCodecs.getObjectReaders());
        result.addAll(this.customCodecs.getObjectReaders());
        result.addAll(this.defaultCodecs.getCatchAllReaders());
        return result;
    }

    @Override
    public List<HttpMessageWriter<?>> getWriters() {
        return this.getWritersInternal(false);
    }

    protected List<HttpMessageWriter<?>> getWritersInternal(boolean forMultipart) {
        ArrayList result = new ArrayList();
        result.addAll(this.defaultCodecs.getTypedWriters(forMultipart));
        result.addAll(this.customCodecs.getTypedWriters());
        result.addAll(this.defaultCodecs.getObjectWriters(forMultipart));
        result.addAll(this.customCodecs.getObjectWriters());
        result.addAll(this.defaultCodecs.getCatchAllWriters());
        return result;
    }

    private static final class DefaultCustomCodecs
    implements CodecConfigurer.CustomCodecs {
        private final List<HttpMessageReader<?>> typedReaders = new ArrayList();
        private final List<HttpMessageWriter<?>> typedWriters = new ArrayList();
        private final List<HttpMessageReader<?>> objectReaders = new ArrayList();
        private final List<HttpMessageWriter<?>> objectWriters = new ArrayList();

        private DefaultCustomCodecs() {
        }

        @Override
        public void decoder(Decoder<?> decoder) {
            this.reader(new DecoderHttpMessageReader(decoder));
        }

        @Override
        public void encoder(Encoder<?> encoder) {
            this.writer(new EncoderHttpMessageWriter(encoder));
        }

        @Override
        public void reader(HttpMessageReader<?> reader) {
            boolean canReadToObject = reader.canRead(ResolvableType.forClass(Object.class), null);
            (canReadToObject ? this.objectReaders : this.typedReaders).add(reader);
        }

        @Override
        public void writer(HttpMessageWriter<?> writer) {
            boolean canWriteObject = writer.canWrite(ResolvableType.forClass(Object.class), null);
            (canWriteObject ? this.objectWriters : this.typedWriters).add(writer);
        }

        List<HttpMessageReader<?>> getTypedReaders() {
            return this.typedReaders;
        }

        List<HttpMessageWriter<?>> getTypedWriters() {
            return this.typedWriters;
        }

        List<HttpMessageReader<?>> getObjectReaders() {
            return this.objectReaders;
        }

        List<HttpMessageWriter<?>> getObjectWriters() {
            return this.objectWriters;
        }
    }
}

