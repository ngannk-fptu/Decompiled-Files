/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.protocols.json.BaseAwsJsonProtocolFactory;

@ThreadSafe
@SdkProtectedApi
public final class AwsJsonProtocolFactory
extends BaseAwsJsonProtocolFactory {
    protected AwsJsonProtocolFactory(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    extends BaseAwsJsonProtocolFactory.Builder<Builder> {
        private Builder() {
        }

        public AwsJsonProtocolFactory build() {
            return new AwsJsonProtocolFactory(this);
        }
    }
}

