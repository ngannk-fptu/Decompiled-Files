/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec;

import java.time.Duration;
import org.springframework.lang.Nullable;

public final class ServerSentEvent<T> {
    @Nullable
    private final String id;
    @Nullable
    private final String event;
    @Nullable
    private final Duration retry;
    @Nullable
    private final String comment;
    @Nullable
    private final T data;

    private ServerSentEvent(@Nullable String id, @Nullable String event, @Nullable Duration retry, @Nullable String comment, @Nullable T data) {
        this.id = id;
        this.event = event;
        this.retry = retry;
        this.comment = comment;
        this.data = data;
    }

    @Nullable
    public String id() {
        return this.id;
    }

    @Nullable
    public String event() {
        return this.event;
    }

    @Nullable
    public Duration retry() {
        return this.retry;
    }

    @Nullable
    public String comment() {
        return this.comment;
    }

    @Nullable
    public T data() {
        return this.data;
    }

    public String toString() {
        return "ServerSentEvent [id = '" + this.id + "', event='" + this.event + "', retry=" + this.retry + ", comment='" + this.comment + "', data=" + this.data + ']';
    }

    public static <T> Builder<T> builder() {
        return new BuilderImpl();
    }

    public static <T> Builder<T> builder(T data) {
        return new BuilderImpl<T>(data);
    }

    private static class BuilderImpl<T>
    implements Builder<T> {
        @Nullable
        private String id;
        @Nullable
        private String event;
        @Nullable
        private Duration retry;
        @Nullable
        private String comment;
        @Nullable
        private T data;

        public BuilderImpl() {
        }

        public BuilderImpl(T data) {
            this.data = data;
        }

        @Override
        public Builder<T> id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder<T> event(String event) {
            this.event = event;
            return this;
        }

        @Override
        public Builder<T> retry(Duration retry) {
            this.retry = retry;
            return this;
        }

        @Override
        public Builder<T> comment(String comment) {
            this.comment = comment;
            return this;
        }

        @Override
        public Builder<T> data(@Nullable T data) {
            this.data = data;
            return this;
        }

        @Override
        public ServerSentEvent<T> build() {
            return new ServerSentEvent(this.id, this.event, this.retry, this.comment, this.data);
        }
    }

    public static interface Builder<T> {
        public Builder<T> id(String var1);

        public Builder<T> event(String var1);

        public Builder<T> retry(Duration var1);

        public Builder<T> comment(String var1);

        public Builder<T> data(@Nullable T var1);

        public ServerSentEvent<T> build();
    }
}

