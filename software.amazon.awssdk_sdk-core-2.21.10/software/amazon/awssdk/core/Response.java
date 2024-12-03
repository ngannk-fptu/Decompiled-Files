/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.http.SdkHttpFullResponse;

@SdkProtectedApi
public final class Response<T> {
    private final Boolean isSuccess;
    private final T response;
    private final SdkException exception;
    private final SdkHttpFullResponse httpResponse;

    private Response(Builder<T> builder) {
        this.isSuccess = ((Builder)builder).isSuccess;
        this.response = ((Builder)builder).response;
        this.exception = ((Builder)builder).exception;
        this.httpResponse = ((Builder)builder).httpResponse;
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    public Builder<T> toBuilder() {
        return new Builder().isSuccess(this.isSuccess).response(this.response).exception(this.exception).httpResponse(this.httpResponse);
    }

    public T response() {
        return this.response;
    }

    public SdkException exception() {
        return this.exception;
    }

    public SdkHttpFullResponse httpResponse() {
        return this.httpResponse;
    }

    public Boolean isSuccess() {
        return this.isSuccess;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Response response1 = (Response)o;
        if (this.isSuccess != null ? !this.isSuccess.equals(response1.isSuccess) : response1.isSuccess != null) {
            return false;
        }
        if (this.response != null ? !this.response.equals(response1.response) : response1.response != null) {
            return false;
        }
        if (this.exception != null ? !this.exception.equals(response1.exception) : response1.exception != null) {
            return false;
        }
        return this.httpResponse != null ? this.httpResponse.equals(response1.httpResponse) : response1.httpResponse == null;
    }

    public int hashCode() {
        int result = this.isSuccess != null ? this.isSuccess.hashCode() : 0;
        result = 31 * result + (this.response != null ? this.response.hashCode() : 0);
        result = 31 * result + (this.exception != null ? this.exception.hashCode() : 0);
        result = 31 * result + (this.httpResponse != null ? this.httpResponse.hashCode() : 0);
        return result;
    }

    public static final class Builder<T> {
        private Boolean isSuccess;
        private T response;
        private SdkException exception;
        private SdkHttpFullResponse httpResponse;

        private Builder() {
        }

        public Builder<T> isSuccess(Boolean success) {
            this.isSuccess = success;
            return this;
        }

        public Builder<T> response(T response) {
            this.response = response;
            return this;
        }

        public Builder<T> exception(SdkException exception) {
            this.exception = exception;
            return this;
        }

        public Builder<T> httpResponse(SdkHttpFullResponse httpResponse) {
            this.httpResponse = httpResponse;
            return this;
        }

        public Response<T> build() {
            return new Response(this);
        }
    }
}

