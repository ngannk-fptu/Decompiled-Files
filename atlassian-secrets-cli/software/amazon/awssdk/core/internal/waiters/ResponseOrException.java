/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.waiters;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public final class ResponseOrException<R> {
    private final Optional<R> response;
    private final Optional<Throwable> exception;

    private ResponseOrException(Optional<R> response, Optional<Throwable> exception) {
        this.response = response;
        this.exception = exception;
    }

    public Optional<R> response() {
        return this.response;
    }

    public Optional<Throwable> exception() {
        return this.exception;
    }

    public static <R> ResponseOrException<R> response(R value) {
        return new ResponseOrException<R>(Optional.of(value), Optional.empty());
    }

    public static <R> ResponseOrException<R> exception(Throwable value) {
        return new ResponseOrException(Optional.empty(), Optional.of(value));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponseOrException)) {
            return false;
        }
        ResponseOrException either = (ResponseOrException)o;
        return this.response.equals(either.response) && this.exception.equals(either.exception);
    }

    public int hashCode() {
        return 31 * this.response.hashCode() + this.exception.hashCode();
    }
}

