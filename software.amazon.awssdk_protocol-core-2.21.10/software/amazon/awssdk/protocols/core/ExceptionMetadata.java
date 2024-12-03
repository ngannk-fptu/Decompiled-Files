/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SdkPojo
 */
package software.amazon.awssdk.protocols.core;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkPojo;

@SdkProtectedApi
public final class ExceptionMetadata {
    private final String errorCode;
    private final Supplier<SdkPojo> exceptionBuilderSupplier;
    private final Integer httpStatusCode;

    private ExceptionMetadata(Builder builder) {
        this.errorCode = builder.errorCode;
        this.exceptionBuilderSupplier = builder.exceptionBuilderSupplier;
        this.httpStatusCode = builder.httpStatusCode;
    }

    public String errorCode() {
        return this.errorCode;
    }

    public Supplier<SdkPojo> exceptionBuilderSupplier() {
        return this.exceptionBuilderSupplier;
    }

    public Integer httpStatusCode() {
        return this.httpStatusCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String errorCode;
        private Supplier<SdkPojo> exceptionBuilderSupplier;
        private Integer httpStatusCode;

        private Builder() {
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder exceptionBuilderSupplier(Supplier<SdkPojo> exceptionBuilderSupplier) {
            this.exceptionBuilderSupplier = exceptionBuilderSupplier;
            return this;
        }

        public Builder httpStatusCode(Integer httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }

        public ExceptionMetadata build() {
            return new ExceptionMetadata(this);
        }
    }
}

