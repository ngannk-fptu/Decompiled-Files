/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class SdkRequestContext {
    private final boolean isFullDuplex;

    private SdkRequestContext(Builder builder) {
        this.isFullDuplex = builder.isFullDuplex;
    }

    @SdkInternalApi
    public static Builder builder() {
        return new Builder();
    }

    public boolean fullDuplex() {
        return this.isFullDuplex;
    }

    @SdkInternalApi
    public static final class Builder {
        private boolean isFullDuplex;

        private Builder() {
        }

        public boolean fullDuplex() {
            return this.isFullDuplex;
        }

        public Builder fullDuplex(boolean fullDuplex) {
            this.isFullDuplex = fullDuplex;
            return this;
        }

        public SdkRequestContext build() {
            return new SdkRequestContext(this);
        }
    }
}

