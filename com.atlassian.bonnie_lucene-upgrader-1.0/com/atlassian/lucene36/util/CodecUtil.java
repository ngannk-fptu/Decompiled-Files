/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexFormatTooNewException;
import com.atlassian.lucene36.index.IndexFormatTooOldException;
import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.store.DataOutput;
import com.atlassian.lucene36.util.BytesRef;
import java.io.IOException;

public final class CodecUtil {
    private static final int CODEC_MAGIC = 1071082519;

    private CodecUtil() {
    }

    public static DataOutput writeHeader(DataOutput out, String codec, int version) throws IOException {
        BytesRef bytes = new BytesRef(codec);
        if (bytes.length != codec.length() || bytes.length >= 128) {
            throw new IllegalArgumentException("codec must be simple ASCII, less than 128 characters in length [got " + codec + "]");
        }
        out.writeInt(1071082519);
        out.writeString(codec);
        out.writeInt(version);
        return out;
    }

    public static int headerLength(String codec) {
        return 9 + codec.length();
    }

    public static int checkHeader(DataInput in, String codec, int minVersion, int maxVersion) throws IOException {
        int actualHeader = in.readInt();
        if (actualHeader != 1071082519) {
            throw new CorruptIndexException("codec header mismatch: actual header=" + actualHeader + " vs expected header=" + 1071082519 + " (resource: " + in + ")");
        }
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

