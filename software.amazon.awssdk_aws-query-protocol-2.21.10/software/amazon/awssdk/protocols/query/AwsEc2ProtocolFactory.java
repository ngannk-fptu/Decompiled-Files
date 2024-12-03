/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.protocols.query;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.query.AwsQueryProtocolFactory;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;

@SdkProtectedApi
public final class AwsEc2ProtocolFactory
extends AwsQueryProtocolFactory {
    private AwsEc2ProtocolFactory(Builder builder) {
        super(builder);
    }

    @Override
    boolean isEc2() {
        return true;
    }

    @Override
    Optional<XmlElement> getErrorRoot(XmlElement document) {
        return document.getOptionalElementByName("Errors").flatMap(e -> e.getOptionalElementByName("Error"));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    extends AwsQueryProtocolFactory.Builder<Builder> {
        private Builder() {
        }

        @Override
        public AwsEc2ProtocolFactory build() {
            return new AwsEc2ProtocolFactory(this);
        }
    }
}

