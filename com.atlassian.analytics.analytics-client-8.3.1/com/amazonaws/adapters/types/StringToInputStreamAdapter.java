/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.adapters.types;

import com.amazonaws.SdkClientException;
import com.amazonaws.adapters.types.TypeAdapter;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.util.StringInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

@SdkProtectedApi
public class StringToInputStreamAdapter
implements TypeAdapter<String, InputStream> {
    @Override
    public InputStream adapt(String source) {
        if (source == null) {
            return null;
        }
        try {
            return new StringInputStream(source);
        }
        catch (UnsupportedEncodingException e) {
            throw new SdkClientException(e);
        }
    }
}

