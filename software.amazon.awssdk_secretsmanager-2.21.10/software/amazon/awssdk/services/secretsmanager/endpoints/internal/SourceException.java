/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.core.exception.SdkException$Builder
 *  software.amazon.awssdk.core.exception.SdkException$BuilderImpl
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkException;

@SdkInternalApi
public class SourceException
extends SdkException {
    private SourceException(Builder b) {
        super((SdkException.Builder)b);
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
        public Builder cause(Throwable var1);

        public Builder writableStackTrace(Boolean var1);

        public Builder message(String var1);

        public SourceException build();
    }
}

