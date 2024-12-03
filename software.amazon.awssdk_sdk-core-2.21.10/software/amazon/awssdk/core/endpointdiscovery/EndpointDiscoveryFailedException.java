/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.endpointdiscovery;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public class EndpointDiscoveryFailedException
extends SdkClientException {
    private static final long serialVersionUID = 1L;

    private EndpointDiscoveryFailedException(Builder b) {
        super(b);
        Validate.paramNotNull((Object)b.cause(), (String)"cause");
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static EndpointDiscoveryFailedException create(Throwable cause) {
        return EndpointDiscoveryFailedException.builder().message("Failed when retrieving a required endpoint from AWS.").cause(cause).build();
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    protected static final class BuilderImpl
    extends SdkClientException.BuilderImpl
    implements Builder {
        protected BuilderImpl() {
        }

        protected BuilderImpl(EndpointDiscoveryFailedException ex) {
            super(ex);
        }

        @Override
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        @Override
        public Builder writableStackTrace(Boolean writableStackTrace) {
            this.writableStackTrace = writableStackTrace;
            return this;
        }

        @Override
        public EndpointDiscoveryFailedException build() {
            return new EndpointDiscoveryFailedException(this);
        }
    }

    public static interface Builder
    extends SdkClientException.Builder {
        @Override
        public Builder message(String var1);

        @Override
        public Builder cause(Throwable var1);

        @Override
        public Builder writableStackTrace(Boolean var1);

        @Override
        public EndpointDiscoveryFailedException build();
    }
}

