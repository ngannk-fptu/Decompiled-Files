/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.adapters.types;

import com.amazonaws.adapters.types.TypeAdapter;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.util.StringUtils;
import java.nio.ByteBuffer;

@SdkProtectedApi
public class StringToByteBufferAdapter
implements TypeAdapter<String, ByteBuffer> {
    @Override
    public ByteBuffer adapt(String source) {
        if (source == null) {
            return null;
        }
        return ByteBuffer.wrap(source.getBytes(StringUtils.UTF8));
    }
}

