/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.marshalling.api.Marshaller
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.marshalling.api.Unmarshaller
 */
package com.atlassian.marshalling.jdk;

import com.atlassian.annotations.PublicApi;
import com.atlassian.marshalling.api.Marshaller;
import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.marshalling.api.Unmarshaller;

@PublicApi
public class ByteMarshalling
implements Marshaller<byte[]>,
Unmarshaller<byte[]> {
    public byte[] marshallToBytes(byte[] bytes) throws MarshallingException {
        return bytes;
    }

    public byte[] unmarshallFrom(byte[] rawBytes) throws MarshallingException {
        return rawBytes;
    }

    public static MarshallingPair<byte[]> pair() {
        ByteMarshalling bm = new ByteMarshalling();
        return new MarshallingPair((Marshaller)bm, (Unmarshaller)bm);
    }
}

