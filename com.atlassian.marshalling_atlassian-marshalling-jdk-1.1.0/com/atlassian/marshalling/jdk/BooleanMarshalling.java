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
public class BooleanMarshalling
implements Marshaller<Boolean>,
Unmarshaller<Boolean> {
    public static MarshallingPair<Boolean> pair() {
        BooleanMarshalling bm = new BooleanMarshalling();
        return new MarshallingPair((Marshaller)bm, (Unmarshaller)bm);
    }

    public byte[] marshallToBytes(Boolean aBoolean) throws MarshallingException {
        return new byte[]{(byte)(aBoolean != false ? 1 : 0)};
    }

    public Boolean unmarshallFrom(byte[] bytes) throws MarshallingException {
        return bytes[0] == 1;
    }
}

