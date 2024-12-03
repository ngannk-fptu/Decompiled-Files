/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexFormatTooNewException;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.BytesRef;

public final class CodecUtil {
    public static final int CODEC_MAGIC = 1071082519;

    private CodecUtil() {
    }

    public static void writeHeader(DataOutput out, String codec, int version) throws IOException {
        BytesRef bytes = new BytesRef(codec);
        if (bytes.length != codec.length() || bytes.length >= 128) {
            throw new IllegalArgumentException("codec must be simple ASCII, less than 128 characters in length [got " + codec + "]");
        }
        out.writeInt(1071082519);
        out.writeString(codec);
        out.writeInt(version);
    }

    public static int headerLength(String codec) {
        return 9 + codec.length();
    }

    public static int checkHeader(DataInput in, String codec, int minVersion, int maxVersion) throws IOException {
        int actualHeader = in.readInt();
        if (actualHeader != 1071082519) {
            throw new CorruptIndexException("codec header mismatch: actual header=" + actualHeader + " vs expected header=" + 1071082519 + " (resource: " + in + ")");
        }
        return CodecUtil.checkHeaderNoMagic(in, codec, minVersion, maxVersion);
    }

    public static int checkHeaderNoMagic(DataInput in, String codec, int minVersion, int maxVersion) throws IOException {
        String actualCodec = in.readString();
        if (!actualCodec.equals(codec)) {
            throw new CorruptIndexException("codec mismatch: actual codec=" + actualCodec + " vs expected codec=" + codec + " (resource: " + in + ")");
        }
        int actualVersion = in.readInt();
        if (actualVersion < minVersion) {
            throw new IndexFormatTooOldException(in, actualVersion, minVersion, maxVersion);
        }
        if (actualVersion > maxVersion) {
            throw new IndexFormatTooNewException(in, actualVersion, minVersion, maxVersion);
        }
        return actualVersion;
    }
}

