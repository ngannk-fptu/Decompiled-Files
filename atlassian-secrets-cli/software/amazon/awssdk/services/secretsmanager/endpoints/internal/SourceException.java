/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkException;

@SdkInternalApi
public class SourceException
extends SdkException {
    private SourceException(Builder b) {
        super(b);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static class BuilderImpl
    extends SdkException.BuilderImpl
    implements Builder {
        @Override
        public Builder cause(Throwable cause) {
            super.cause(cause);
            return this;
        }

        @Override
        public Builder message(String message) {
            super.message(message);
            return this;
        }

        @Override
        public Builder writableStackTrace(Boolean writableStackTrace) {
            super.writableStackTrace(writableStackTrace);
            return this;
        }

        @Override
        public SourceException build() {
            return new SourceException(this);
        }
    }

    static interface Builder
    extends SdkException.Builder {
        @Override
        public Builder cause(Throwable var1);

        @Override
        public Builder writableStackTrace(Boolean var1);

        @Override
        public Builder message(String var1);

        @Override
        public SourceException build();
    }
}

