/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.cache;

import java.time.Instant;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkProtectedApi
public final class RefreshResult<T>
implements ToCopyableBuilder<Builder<T>, RefreshResult<T>> {
    private final T value;
    private final Instant staleTime;
    private final Instant prefetchTime;

    private RefreshResult(Builder<T> builder) {
        this.value = ((Builder)builder).value;
        this.staleTime = ((Builder)builder).staleTime;
        this.prefetchTime = ((Builder)builder).prefetchTime;
    }

    public static <T> Builder<T> builder(T value) {
        return new Builder(value);
    }

    public T value() {
        return this.value;
    }

    public Instant staleTime() {
        return this.staleTime;
    }

    public Instant prefetchTime() {
        return this.prefetchTime;
    }

    @Override
    public Builder<T> toBuilder() {
        return new Builder(this);
    }

    public static final class Builder<T>
    implements CopyableBuilder<Builder<T>, RefreshResult<T>> {
        private final T value;
        private Instant staleTime = Instant.MAX;
        private Instant prefetchTime = Instant.MAX;

        private Builder(T value) {
            this.value = value;
        }

        private Builder(RefreshResult<T> value) {
            this.value = ((RefreshResult)value).value;
            this.staleTime = ((RefreshResult)value).staleTime;
            this.prefetchTime = ((RefreshResult)value).prefetchTime;
        }

        public Builder<T> staleTime(Instant staleTime) {
            this.staleTime = staleTime;
            return this;
        }

        public Builder<T> prefetchTime(Instant prefetchTime) {
            this.prefetchTime = prefetchTime;
            return this;
        }

        @Override
        public RefreshResult<T> build() {
            return new RefreshResult(this);
        }
    }
}

