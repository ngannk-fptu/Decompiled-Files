/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.avro.file;

import java.util.HashMap;
import java.util.Map;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.file.BZip2Codec;
import org.apache.avro.file.Codec;
import org.apache.avro.file.DeflateCodec;
import org.apache.avro.file.NullCodec;
import org.apache.avro.file.SnappyCodec;
import org.apache.avro.file.XZCodec;
import org.apache.avro.file.ZstandardCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CodecFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CodecFactory.class);
    private static final Map<String, CodecFactory> REGISTERED = new HashMap<String, CodecFactory>();
    public static final int DEFAULT_DEFLATE_LEVEL = -1;
    public static final int DEFAULT_XZ_LEVEL = 6;
    public static final int DEFAULT_ZSTANDARD_LEVEL = 3;
    public static final boolean DEFAULT_ZSTANDARD_BUFFERPOOL = false;

    public static CodecFactory nullCodec() {
        return NullCodec.OPTION;
    }

    public static CodecFactory deflateCodec(int compressionLevel) {
        return new DeflateCodec.Option(compressionLevel);
    }

    public static CodecFactory xzCodec(int compressionLevel) {
        return new XZCodec.Option(compressionLevel);
    }

    public static CodecFactory snappyCodec() {
        try {
            return new SnappyCodec.Option();
        }
        catch (Throwable t) {
            LOG.debug("Snappy was not available", t);
            return null;
        }
    }

    public static CodecFactory bzip2Codec() {
        return new BZip2Codec.Option();
    }

    public static CodecFactory zstandardCodec(int level) {
        return new ZstandardCodec.Option(level, false, false);
    }

    public static CodecFactory zstandardCodec(int level, boolean useChecksum) {
        return new ZstandardCodec.Option(level, useChecksum, false);
    }

    public static CodecFactory zstandardCodec(int level, boolean useChecksum, boolean useBufferPool) {
        return new ZstandardCodec.Option(level, useChecksum, useBufferPool);
    }

    protected abstract Codec createInstance();

    public static CodecFactory fromString(String s) {
        CodecFactory o = REGISTERED.get(s);
        if (o == null) {
            throw new AvroRuntimeException("Unrecognized codec: " + s);
        }
        return o;
    }

    public static CodecFactory addCodec(String name, CodecFactory c) {
        if (c != null) {
            return REGISTERED.put(name, c);
        }
        return null;
    }

    public String toString() {
        Codec instance = this.createInstance();
        return instance.toString();
    }

    static {
        CodecFactory.addCodec("null", CodecFactory.nullCodec());
        CodecFactory.addCodec("deflate", CodecFactory.deflateCodec(-1));
        CodecFactory.addCodec("bzip2", CodecFactory.bzip2Codec());
        CodecFactory.addCodec("xz", CodecFactory.xzCodec(6));
        CodecFactory.addCodec("zstandard", CodecFactory.zstandardCodec(3, false));
        CodecFactory.addCodec("snappy", CodecFactory.snappyCodec());
    }
}

