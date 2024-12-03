/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.codec;

import java.util.List;
import zipkin2.codec.Encoding;

public interface BytesEncoder<T> {
    public Encoding encoding();

    public int sizeInBytes(T var1);

    public byte[] encode(T var1);

    public byte[] encodeList(List<T> var1);
}

