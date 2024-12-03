/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.interceptor.trait;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class RequestCompression {
    private List<String> encodings;
    private boolean isStreaming;

    private RequestCompression(Builder builder) {
        this.encodings = builder.encodings;
        this.isStreaming = builder.isStreaming;
    }

    public List<String> getEncodings() {
        return this.encodings;
    }

    public boolean isStreaming() {
        return this.isStreaming;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RequestCompression that = (RequestCompression)o;
        return this.isStreaming == that.isStreaming() && Objects.equals(this.encodings, that.getEncodings());
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.isStreaming ? 1 : 0);
        hashCode = 31 * hashCode + Objects.hashCode(this.encodings);
        return hashCode;
    }

    public static final class Builder {
        private List<String> encodings;
        private boolean isStreaming;

        public Builder encodings(List<String> encodings) {
            this.encodings = encodings;
            return this;
        }

        public Builder encodings(String ... encodings) {
            if (encodings != null) {
                this.encodings = Arrays.asList(encodings);
            }
            return this;
        }

        public Builder isStreaming(boolean isStreaming) {
            this.isStreaming = isStreaming;
            return this;
        }

        public RequestCompression build() {
            return new RequestCompression(this);
        }
    }
}

