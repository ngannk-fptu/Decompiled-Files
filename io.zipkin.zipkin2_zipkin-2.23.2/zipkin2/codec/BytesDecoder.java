/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.codec;

import java.util.Collection;
import java.util.List;
import zipkin2.codec.Encoding;
import zipkin2.internal.Nullable;

public interface BytesDecoder<T> {
    public Encoding encoding();

    public boolean decode(byte[] var1, Collection<T> var2);

    @Nullable
    public T decodeOne(byte[] var1);

    public boolean decodeList(byte[] var1, Collection<T> var2);

    public List<T> decodeList(byte[] var1);
}

