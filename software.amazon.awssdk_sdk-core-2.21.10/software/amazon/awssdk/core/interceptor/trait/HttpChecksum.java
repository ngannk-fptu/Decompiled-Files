/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.interceptor.trait;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class HttpChecksum {
    private final boolean requestChecksumRequired;
    private final String requestAlgorithm;
    private final String requestValidationMode;
    private final boolean isRequestStreaming;
    private final List<String> responseAlgorithms;

    private HttpChecksum(Builder builder) {
        this.requestChecksumRequired = builder.requestChecksumRequired;
        this.requestAlgorithm = builder.requestAlgorithm;
        this.requestValidationMode = builder.requestValidationMode;
        this.responseAlgorithms = builder.responseAlgorithms;
        this.isRequestStreaming = builder.isRequestStreaming;
    }

    public boolean isRequestChecksumRequired() {
        return this.requestChecksumRequired;
    }

    public String requestAlgorithm() {
        return this.requestAlgorithm;
    }

    public List<String> responseAlgorithms() {
        return this.responseAlgorithms;
    }

    public String requestValidationMode() {
        return this.requestValidationMode;
    }

    public boolean isRequestStreaming() {
        return this.isRequestStreaming;
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
        HttpChecksum that = (HttpChecksum)o;
        return this.requestChecksumRequired == that.requestChecksumRequired && this.isRequestStreaming == that.isRequestStreaming && Objects.equals(this.requestAlgorithm, that.requestAlgorithm) && Objects.equals(this.requestValidationMode, that.requestValidationMode) && Objects.equals(this.responseAlgorithms, that.responseAlgorithms);
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.requestChecksumRequired ? 1 : 0);
        hashCode = 31 * hashCode + (this.isRequestStreaming ? 1 : 0);
        hashCode = 31 * hashCode + Objects.hashCode(this.requestAlgorithm);
        hashCode = 31 * hashCode + Objects.hashCode(this.requestValidationMode);
        hashCode = 31 * hashCode + Objects.hashCode(this.responseAlgorithms);
        return hashCode;
    }

    public static final class Builder {
        private boolean requestChecksumRequired;
        private String requestAlgorithm;
        private String requestValidationMode;
        private List<String> responseAlgorithms;
        private boolean isRequestStreaming;

        public Builder requestChecksumRequired(boolean requestChecksumRequired) {
            this.requestChecksumRequired = requestChecksumRequired;
            return this;
        }

        public Builder requestAlgorithm(String requestAlgorithm) {
            this.requestAlgorithm = requestAlgorithm;
            return this;
        }

        public Builder requestValidationMode(String requestValidationMode) {
            this.requestValidationMode = requestValidationMode;
            return this;
        }

        public Builder responseAlgorithms(List<String> responseAlgorithms) {
            this.responseAlgorithms = responseAlgorithms;
            return this;
        }

        public Builder responseAlgorithms(String ... responseAlgorithms) {
            if (responseAlgorithms != null) {
                this.responseAlgorithms = Arrays.asList(responseAlgorithms);
            }
            return this;
        }

        public Builder isRequestStreaming(boolean isRequestStreaming) {
            this.isRequestStreaming = isRequestStreaming;
            return this;
        }

        public HttpChecksum build() {
            return new HttpChecksum(this);
        }
    }
}

