/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.vcache.marshallers;

import com.atlassian.annotations.Internal;
import com.atlassian.vcache.Marshaller;
import com.atlassian.vcache.MarshallerException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Deprecated
@Internal
class OptionalMarshaller<T>
implements Marshaller<Optional<T>> {
    private final Marshaller<T> valueMarshaller;

    OptionalMarshaller(Marshaller<T> valueMarshaller) {
        this.valueMarshaller = Objects.requireNonNull(valueMarshaller);
    }

    @Override
    public byte[] marshall(Optional<T> obj) throws MarshallerException {
        if (!obj.isPresent()) {
            return new byte[]{0};
        }
        byte[] valueBytes = this.valueMarshaller.marshall(obj.get());
        byte[] resultBytes = new byte[valueBytes.length + 1];
        resultBytes[0] = 1;
        System.arraycopy(valueBytes, 0, resultBytes, 1, valueBytes.length);
        return resultBytes;
    }

    @Override
    public Optional<T> unmarshall(byte[] raw) throws MarshallerException {
        if (raw[0] == 0) {
            return Optional.empty();
        }
        byte[] valueBytes = Arrays.copyOfRange(raw, 1, raw.length);
        return Optional.of(this.valueMarshaller.unmarshall(valueBytes));
    }
}

