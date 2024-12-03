/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.marshalling.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.marshalling.api.Marshaller;
import com.atlassian.marshalling.api.Unmarshaller;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public class MarshallingPair<T> {
    private final Marshaller<T> marshaller;
    private final Unmarshaller<T> unmarshaller;

    public MarshallingPair(Marshaller<T> marshaller, Unmarshaller<T> unmarshaller) {
        this.marshaller = Objects.requireNonNull(marshaller);
        this.unmarshaller = Objects.requireNonNull(unmarshaller);
    }

    public Marshaller<T> getMarshaller() {
        return this.marshaller;
    }

    public Unmarshaller<T> getUnmarshaller() {
        return this.unmarshaller;
    }
}

