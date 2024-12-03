/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Promise
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Buildable;
import com.atlassian.httpclient.api.HttpStatus;
import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import io.atlassian.util.concurrent.Promise;
import java.util.function.Function;

public interface ResponseTransformation<T> {
    public Function<Throwable, ? extends T> getFailFunction();

    public Function<Response, T> getSuccessFunctions();

    public Promise<T> apply(ResponsePromise var1);

    public static interface Builder<T>
    extends Buildable<ResponseTransformation<T>> {
        public Builder<T> on(HttpStatus var1, Function<Response, ? extends T> var2);

        public Builder<T> on(int var1, Function<Response, ? extends T> var2);

        public Builder<T> informational(Function<Response, ? extends T> var1);

        public Builder<T> successful(Function<Response, ? extends T> var1);

        public Builder<T> ok(Function<Response, ? extends T> var1);

        public Builder<T> created(Function<Response, ? extends T> var1);

        public Builder<T> noContent(Function<Response, ? extends T> var1);

        public Builder<T> redirection(Function<Response, ? extends T> var1);

        public Builder<T> seeOther(Function<Response, ? extends T> var1);

        public Builder<T> notModified(Function<Response, ? extends T> var1);

        public Builder<T> clientError(Function<Response, ? extends T> var1);

        public Builder<T> badRequest(Function<Response, ? extends T> var1);

        public Builder<T> unauthorized(Function<Response, ? extends T> var1);

        public Builder<T> forbidden(Function<Response, ? extends T> var1);

        public Builder<T> notFound(Function<Response, ? extends T> var1);

        public Builder<T> conflict(Function<Response, ? extends T> var1);

        public Builder<T> serverError(Function<Response, ? extends T> var1);

        public Builder<T> internalServerError(Function<Response, ? extends T> var1);

        public Builder<T> serviceUnavailable(Function<Response, ? extends T> var1);

        public Builder<T> error(Function<Response, ? extends T> var1);

        public Builder<T> notSuccessful(Function<Response, ? extends T> var1);

        public Builder<T> others(Function<Response, ? extends T> var1);

        public Builder<T> otherwise(Function<Throwable, T> var1);

        public Builder<T> done(Function<Response, T> var1);

        public Builder<T> fail(Function<Throwable, ? extends T> var1);
    }
}

