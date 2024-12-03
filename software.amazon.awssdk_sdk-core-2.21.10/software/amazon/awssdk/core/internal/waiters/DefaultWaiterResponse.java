/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.waiters;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultWaiterResponse<T>
implements WaiterResponse<T> {
    private final T result;
    private final Throwable exception;
    private final int attemptsExecuted;
    private final ResponseOrException<T> matched;

    private DefaultWaiterResponse(Builder<T> builder) {
        Validate.mutuallyExclusive((String)"response and exception are mutually exclusive, set only one on the Builder", (Object[])new Object[]{((Builder)builder).response, ((Builder)builder).exception});
        this.result = ((Builder)builder).response;
        this.exception = ((Builder)builder).exception;
        this.attemptsExecuted = (Integer)Validate.paramNotNull((Object)((Builder)builder).attemptsExecuted, (String)"attemptsExecuted");
        Validate.isPositive((int)((Builder)builder).attemptsExecuted, (String)"attemptsExecuted");
        this.matched = this.result != null ? ResponseOrException.response(this.result) : ResponseOrException.exception(this.exception);
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    @Override
    public ResponseOrException<T> matched() {
        return this.matched;
    }

    @Override
    public int attemptsExecuted() {
        return this.attemptsExecuted;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultWaiterResponse that = (DefaultWaiterResponse)o;
        if (this.attemptsExecuted != that.attemptsExecuted) {
            return false;
        }
        if (!Objects.equals(this.result, that.result)) {
            return false;
        }
        return Objects.equals(this.exception, that.exception);
    }

    public int hashCode() {
        int result1 = this.result != null ? this.result.hashCode() : 0;
        result1 = 31 * result1 + (this.exception != null ? this.exception.hashCode() : 0);
        result1 = 31 * result1 + this.attemptsExecuted;
        return result1;
    }

    public String toString() {
        ToString toString = ToString.builder((String)"DefaultWaiterResponse").add("attemptsExecuted", (Object)this.attemptsExecuted);
        this.matched.response().ifPresent(r -> toString.add("response", this.result));
        this.matched.exception().ifPresent(r -> toString.add("exception", (Object)this.exception));
        return toString.build();
    }

    public static final class Builder<T> {
        private T response;
        private Throwable exception;
        private Integer attemptsExecuted;

        private Builder() {
        }

        public Builder<T> response(T response) {
            this.response = response;
            return this;
        }

        public Builder<T> exception(Throwable exception) {
            this.exception = exception;
            return this;
        }

        public Builder<T> attemptsExecuted(Integer attemptsExecuted) {
            this.attemptsExecuted = attemptsExecuted;
            return this;
        }

        public WaiterResponse<T> build() {
            return new DefaultWaiterResponse(this);
        }
    }
}

