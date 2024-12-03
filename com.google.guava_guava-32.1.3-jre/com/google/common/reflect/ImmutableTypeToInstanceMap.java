/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.DoNotCall
 *  javax.annotation.CheckForNull
 */
package com.google.common.reflect;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ElementTypesAreNonnullByDefault;
import com.google.common.reflect.TypeToInstanceMap;
import com.google.common.reflect.TypeToken;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import java.util.Map;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
public final class ImmutableTypeToInstanceMap<B>
extends ForwardingMap<TypeToken<? extends B>, B>
implements TypeToInstanceMap<B> {
    private final ImmutableMap<TypeToken<? extends B>, B> delegate;

    public static <B> ImmutableTypeToInstanceMap<B> of() {
        return new ImmutableTypeToInstanceMap(ImmutableMap.of());
    }

    public static <B> Builder<B> builder() {
        return new Builder();
    }

    private ImmutableTypeToInstanceMap(ImmutableMap<TypeToken<? extends B>, B> delegate) {
        this.delegate = delegate;
    }

    @Override
    @CheckForNull
    public <T extends B> T getInstance(TypeToken<T> type) {
        return this.trustedGet(type.rejectTypeVariables());
    }

    @Override
    @CheckForNull
    public <T extends B> T getInstance(Class<T> type) {
        return this.trustedGet(TypeToken.of(type));
    }

    @Override
    @Deprecated
    @CheckForNull
    @CanIgnoreReturnValue
    @DoNotCall(value="Always throws UnsupportedOperationException")
    public <T extends B> T putInstance(TypeToken<T> type, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    @CheckForNull
    @CanIgnoreReturnValue
    @DoNotCall(value="Always throws UnsupportedOperationException")
    public <T extends B> T putInstance(Class<T> type, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    @CheckForNull
    @CanIgnoreReturnValue
    @DoNotCall(value="Always throws UnsupportedOperationException")
    public B put(TypeToken<? extends B> key, B value) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    @DoNotCall(value="Always throws UnsupportedOperationException")
    public void putAll(Map<? extends TypeToken<? extends B>, ? extends B> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Map<TypeToken<? extends B>, B> delegate() {
        return this.delegate;
    }

    @CheckForNull
    private <T extends B> T trustedGet(TypeToken<T> type) {
        return (T)this.delegate.get(type);
    }

    public static final class Builder<B> {
        private final ImmutableMap.Builder<TypeToken<? extends B>, B> mapBuilder = ImmutableMap.builder();

        private Builder() {
        }

        @CanIgnoreReturnValue
        public <T extends B> Builder<B> put(Class<T> key, T value) {
            this.mapBuilder.put(TypeToken.of(key), value);
            return this;
        }

        @CanIgnoreReturnValue
        public <T extends B> Builder<B> put(TypeToken<T> key, T value) {
            this.mapBuilder.put(key.rejectTypeVariables(), value);
            return this;
        }

        public ImmutableTypeToInstanceMap<B> build() {
            return new ImmutableTypeToInstanceMap(this.mapBuilder.buildOrThrow());
        }
    }
}

