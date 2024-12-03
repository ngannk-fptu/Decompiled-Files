/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.checksums;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.Algorithm;

@SdkInternalApi
public class ChecksumSpecs {
    private final Algorithm algorithm;
    private final String headerName;
    private final List<Algorithm> responseValidationAlgorithms;
    private final boolean isValidationEnabled;
    private final boolean isRequestChecksumRequired;
    private final boolean isRequestStreaming;

    private ChecksumSpecs(Builder builder) {
        this.algorithm = builder.algorithm;
        this.headerName = builder.headerName;
        this.responseValidationAlgorithms = builder.responseValidationAlgorithms;
        this.isValidationEnabled = builder.isValidationEnabled;
        this.isRequestChecksumRequired = builder.isRequestChecksumRequired;
        this.isRequestStreaming = builder.isRequestStreaming;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Algorithm algorithm() {
        return this.algorithm;
    }

    public String headerName() {
        return this.headerName;
    }

    public boolean isRequestStreaming() {
        return this.isRequestStreaming;
    }

    public boolean isValidationEnabled() {
        return this.isValidationEnabled;
    }

    public boolean isRequestChecksumRequired() {
        return this.isRequestChecksumRequired;
    }

    public List<Algorithm> responseValidationAlgorithms() {
        return this.responseValidationAlgorithms;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChecksumSpecs)) {
            return false;
        }
        ChecksumSpecs checksum = (ChecksumSpecs)o;
        return this.algorithm() == checksum.algorithm() && this.isRequestStreaming() == checksum.isRequestStreaming() && Objects.equals(this.headerName(), checksum.headerName()) && Objects.equals(this.responseValidationAlgorithms(), checksum.responseValidationAlgorithms()) && Objects.equals(this.isValidationEnabled(), checksum.isValidationEnabled()) && Objects.equals(this.isRequestChecksumRequired(), checksum.isRequestChecksumRequired());
    }

    public int hashCode() {
        int result = this.algorithm != null ? this.algorithm.hashCode() : 0;
        result = 31 * result + (this.headerName != null ? this.headerName.hashCode() : 0);
        result = 31 * result + (this.responseValidationAlgorithms != null ? this.responseValidationAlgorithms.hashCode() : 0);
        result = 31 * result + (this.isValidationEnabled ? 1 : 0);
        result = 31 * result + (this.isRequestChecksumRequired ? 1 : 0);
        result = 31 * result + (this.isRequestStreaming ? 1 : 0);
        return result;
    }

    public String toString() {
        return "ChecksumSpecs{algorithm=" + (Object)((Object)this.algorithm) + ", headerName='" + this.headerName + '\'' + ", responseValidationAlgorithms=" + this.responseValidationAlgorithms + ", isValidationEnabled=" + this.isValidationEnabled + ", isRequestChecksumRequired=" + this.isRequestChecksumRequired + ", isStreamingData=" + this.isRequestStreaming + '}';
    }

    public static final class Builder {
        private Algorithm algorithm;
        private String headerName;
        private List<Algorithm> responseValidationAlgorithms;
        private boolean isValidationEnabled;
        private boolean isRequestChecksumRequired;
        private boolean isRequestStreaming;

        private Builder() {
        }

        public Builder algorithm(Algorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder headerName(String headerName) {
            this.headerName = headerName;
            return this;
        }

        public Builder responseValidationAlgorithms(List<Algorithm> responseValidationAlgorithms) {
            this.responseValidationAlgorithms = responseValidationAlgorithms != null ? Collections.unmodifiableList(responseValidationAlgorithms) : null;
            return this;
        }

        public Builder isValidationEnabled(boolean isValidationEnabled) {
            this.isValidationEnabled = isValidationEnabled;
            return this;
        }

        public Builder isRequestChecksumRequired(boolean isRequestChecksumRequired) {
            this.isRequestChecksumRequired = isRequestChecksumRequired;
            return this;
        }

        public Builder isRequestStreaming(boolean isRequestStreaming) {
            this.isRequestStreaming = isRequestStreaming;
            return this;
        }

        public ChecksumSpecs build() {
            return new ChecksumSpecs(this);
        }
    }
}

