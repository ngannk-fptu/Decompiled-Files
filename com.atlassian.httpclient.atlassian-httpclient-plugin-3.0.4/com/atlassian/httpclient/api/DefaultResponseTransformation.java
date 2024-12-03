/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.Promise
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.HttpStatus;
import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.httpclient.api.ResponsePromiseMapFunction;
import com.atlassian.httpclient.api.ResponseTransformation;
import com.atlassian.httpclient.api.ResponseTransformationException;
import com.atlassian.httpclient.api.UnexpectedResponseException;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.Promise;
import java.util.function.Function;

public final class DefaultResponseTransformation<T>
implements ResponseTransformation<T> {
    private final ResponsePromiseMapFunction<T> mapFunctions;
    private final Function<Throwable, ? extends T> failFunction;

    private DefaultResponseTransformation(ResponsePromiseMapFunction<T> mapFunctions, Function<Throwable, ? extends T> failFunction) {
        this.mapFunctions = mapFunctions;
        this.failFunction = failFunction;
    }

    @Override
    public Function<Throwable, ? extends T> getFailFunction() {
        return this.failFunction;
    }

    @Override
    public Function<Response, T> getSuccessFunctions() {
        return this.mapFunctions;
    }

    @Override
    public Promise<T> apply(ResponsePromise responsePromise) {
        return responsePromise.transform(this);
    }

    public static <T> ResponseTransformation.Builder<T> builder() {
        return new DefaultResponseTransformationBuilder();
    }

    static final class OrStatusRange
    implements ResponsePromiseMapFunction.StatusRange {
        private final ResponsePromiseMapFunction.StatusRange one;
        private final ResponsePromiseMapFunction.StatusRange two;

        private OrStatusRange(ResponsePromiseMapFunction.StatusRange one, ResponsePromiseMapFunction.StatusRange two) {
            this.one = (ResponsePromiseMapFunction.StatusRange)Preconditions.checkNotNull((Object)one);
            this.two = (ResponsePromiseMapFunction.StatusRange)Preconditions.checkNotNull((Object)two);
        }

        @Override
        public boolean isIn(int code) {
            return this.one.isIn(code) || this.two.isIn(code);
        }
    }

    static final class NotInStatusRange
    implements ResponsePromiseMapFunction.StatusRange {
        private final ResponsePromiseMapFunction.StatusRange range;

        private NotInStatusRange(ResponsePromiseMapFunction.StatusRange range) {
            this.range = (ResponsePromiseMapFunction.StatusRange)Preconditions.checkNotNull((Object)range);
        }

        @Override
        public boolean isIn(int code) {
            return !this.range.isIn(code);
        }
    }

    static final class HundredsStatusRange
    implements ResponsePromiseMapFunction.StatusRange {
        private final HttpStatus status;

        private HundredsStatusRange(HttpStatus status) {
            this.status = (HttpStatus)((Object)Preconditions.checkNotNull((Object)((Object)status)));
        }

        @Override
        public boolean isIn(int code) {
            int diff = code - this.status.code;
            return 0 <= diff && diff < 100;
        }
    }

    static final class SingleStatusRange
    implements ResponsePromiseMapFunction.StatusRange {
        private final int statusCode;

        SingleStatusRange(int statusCode) {
            this.statusCode = (Integer)Preconditions.checkNotNull((Object)statusCode);
        }

        @Override
        public boolean isIn(int code) {
            return this.statusCode == code;
        }
    }

    private static class DefaultResponseTransformationBuilder<T>
    implements ResponseTransformation.Builder<T> {
        private final ResponsePromiseMapFunction.ResponsePromiseMapFunctionBuilder<T> builder = ResponsePromiseMapFunction.builder();
        private Function<Throwable, ? extends T> failFunction = this.defaultThrowableHandler();

        private DefaultResponseTransformationBuilder() {
        }

        @Override
        public ResponseTransformation.Builder<T> on(HttpStatus status, Function<Response, ? extends T> f) {
            return this.addSingle(status, f);
        }

        @Override
        public ResponseTransformation.Builder<T> on(int statusCode, Function<Response, ? extends T> f) {
            return this.addSingle(statusCode, f);
        }

        @Override
        public ResponseTransformation.Builder<T> informational(Function<Response, ? extends T> f) {
            return this.addRange(HttpStatus.CONTINUE, f);
        }

        @Override
        public ResponseTransformation.Builder<T> successful(Function<Response, ? extends T> f) {
            return this.addRange(HttpStatus.OK, f);
        }

        @Override
        public ResponseTransformation.Builder<T> ok(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.OK, f);
        }

        @Override
        public ResponseTransformation.Builder<T> created(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.CREATED, f);
        }

        @Override
        public ResponseTransformation.Builder<T> noContent(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.NO_CONTENT, f);
        }

        @Override
        public ResponseTransformation.Builder<T> redirection(Function<Response, ? extends T> f) {
            return this.addRange(HttpStatus.MULTIPLE_CHOICES, f);
        }

        @Override
        public ResponseTransformation.Builder<T> seeOther(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.SEE_OTHER, f);
        }

        @Override
        public ResponseTransformation.Builder<T> notModified(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.NOT_MODIFIED, f);
        }

        @Override
        public ResponseTransformation.Builder<T> clientError(Function<Response, ? extends T> f) {
            return this.addRange(HttpStatus.BAD_REQUEST, f);
        }

        @Override
        public ResponseTransformation.Builder<T> badRequest(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.BAD_REQUEST, f);
        }

        @Override
        public ResponseTransformation.Builder<T> unauthorized(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.UNAUTHORIZED, f);
        }

        @Override
        public ResponseTransformation.Builder<T> forbidden(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.FORBIDDEN, f);
        }

        @Override
        public ResponseTransformation.Builder<T> notFound(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.NOT_FOUND, f);
        }

        @Override
        public ResponseTransformation.Builder<T> conflict(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.CONFLICT, f);
        }

        @Override
        public ResponseTransformation.Builder<T> serverError(Function<Response, ? extends T> f) {
            return this.addRange(HttpStatus.INTERNAL_SERVER_ERROR, f);
        }

        @Override
        public ResponseTransformation.Builder<T> internalServerError(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.INTERNAL_SERVER_ERROR, f);
        }

        @Override
        public ResponseTransformation.Builder<T> serviceUnavailable(Function<Response, ? extends T> f) {
            return this.addSingle(HttpStatus.SERVICE_UNAVAILABLE, f);
        }

        @Override
        public ResponseTransformation.Builder<T> error(Function<Response, ? extends T> f) {
            this.builder.addStatusRangeFunction(new OrStatusRange(new HundredsStatusRange(HttpStatus.BAD_REQUEST), new HundredsStatusRange(HttpStatus.INTERNAL_SERVER_ERROR)), f);
            return this;
        }

        @Override
        public ResponseTransformation.Builder<T> notSuccessful(Function<Response, ? extends T> f) {
            this.builder.addStatusRangeFunction(new NotInStatusRange(new HundredsStatusRange(HttpStatus.OK)), f);
            return this;
        }

        @Override
        public ResponseTransformation.Builder<T> others(Function<Response, ? extends T> f) {
            this.builder.setOthersFunction(f);
            return this;
        }

        @Override
        public ResponseTransformation.Builder<T> otherwise(Function<Throwable, T> callback) {
            this.others(input -> callback.apply(new UnexpectedResponseException((Response)input)));
            this.fail(callback);
            return this;
        }

        @Override
        public ResponseTransformation.Builder<T> done(Function<Response, T> f) {
            this.others(f);
            return this;
        }

        @Override
        public ResponseTransformation.Builder<T> fail(Function<Throwable, ? extends T> f) {
            this.failFunction = f;
            return this;
        }

        private DefaultResponseTransformationBuilder<T> addSingle(HttpStatus status, Function<Response, ? extends T> f) {
            return this.addSingle(status.code, f);
        }

        private DefaultResponseTransformationBuilder<T> addSingle(int statusCode, Function<Response, ? extends T> f) {
            this.builder.addStatusRangeFunction(new SingleStatusRange(statusCode), f);
            return this;
        }

        private DefaultResponseTransformationBuilder<T> addRange(HttpStatus status, Function<Response, ? extends T> f) {
            this.builder.addStatusRangeFunction(new HundredsStatusRange(status), f);
            return this;
        }

        private Function<Throwable, ? extends T> defaultThrowableHandler() {
            return throwable -> {
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException)throwable;
                }
                throw new ResponseTransformationException((Throwable)throwable);
            };
        }

        @Override
        public ResponseTransformation<T> build() {
            return new DefaultResponseTransformation((ResponsePromiseMapFunction)this.builder.build(), this.failFunction);
        }
    }
}

