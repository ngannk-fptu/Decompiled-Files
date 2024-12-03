/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import software.amazon.awssdk.awscore.AwsRequest;

public abstract class SecretsManagerRequest
extends AwsRequest {
    protected SecretsManagerRequest(Builder builder) {
        super(builder);
    }

    @Override
    public abstract Builder toBuilder();

    protected static abstract class BuilderImpl
    extends AwsRequest.BuilderImpl
    implements Builder {
        protected BuilderImpl() {
        }

        protected BuilderImpl(SecretsManagerRequest request) {
            super(request);
        }
    }

    public static interface Builder
    extends AwsRequest.Builder {
        @Override
        public SecretsManagerRequest build();
    }
}

