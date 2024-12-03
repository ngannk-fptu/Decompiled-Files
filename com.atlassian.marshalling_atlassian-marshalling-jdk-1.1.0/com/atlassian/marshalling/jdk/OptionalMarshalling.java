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
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@PublicApi
public class OptionalMarshalling<T>
implements Marshaller<Optional<T>>,
Unmarshaller<Optional<T>> {
    private final MarshallingPair<T> valueMarshallingPair;

    public OptionalMarshalling(MarshallingPair<T> valueMarshallingPair) {
        this.valueMarshallingPair = Objects.requireNonNull(valueMarshallingPair);
    }

    public byte[] marshallToBytes(Optional<T> obj) throws MarshallingException {
        if (!obj.isPresent()) {
            return new byte[]{0};
        }
        byte[] valueBytes = this.valueMarshallingPair.getMarshaller().marshallToBytes(obj.get());
        byte[] resultBytes = new byte[valueBytes.length + 1];
        resultBytes[0] = 1;
        System.arraycopy(valueBytes, 0, resultBytes, 1, valueBytes.length);
        return resultBytes;
    }

    public Optional<T> unmarshallFrom(byte[] raw) throws MarshallingException {
        if (raw[0] == 0) {
            return Optional.empty();
        }
        byte[] valueBytes = Arrays.copyOfRange(raw, 1, raw.length);
        return Optional.of(this.valueMarshallingPair.getUnmarshaller().unmarshallFrom(valueBytes));
    }

    public static <T> MarshallingPair<Optional<T>> pair(MarshallingPair<T> valueMarshallingPair) {
        OptionalMarshalling<T> sm = new OptionalMarshalling<T>(valueMarshallingPair);
        return new MarshallingPair(sm, sm);
    }
}

