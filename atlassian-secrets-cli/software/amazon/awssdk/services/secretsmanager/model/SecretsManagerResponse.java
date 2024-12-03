/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.AwsResponseMetadata;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponseMetadata;

public abstract class SecretsManagerResponse
extends AwsResponse {
    private final SecretsManagerResponseMetadata responseMetadata;

    protected SecretsManagerResponse(Builder builder) {
        super(builder);
        this.responseMetadata = builder.responseMetadata();
    }

    @Override
    public SecretsManagerResponseMetadata responseMetadata() {
        return this.responseMetadata;
    }

    protected static abstract class BuilderImpl
    extends AwsResponse.BuilderImpl
    implements Builder {
        private SecretsManagerResponseMetadata responseMetadata;

        protected BuilderImpl() {
        }

        protected BuilderImpl(SecretsManagerResponse response) {
            super(response);
            this.responseMetadata = response.responseMetadata();
        }

        @Override
        public SecretsManagerResponseMetadata responseMetadata() {
            return this.responseMetadata;
        }

        @Override
        public Builder responseMetadata(AwsResponseMetadata responseMetadata) {
            this.responseMetadata = SecretsManagerResponseMetadata.create(responseMetadata);
            return this;
        }
    }

    public static interface Builder
    extends AwsResponse.Builder {
        @Override
        public SecretsManagerResponse build();

        @Override
        public SecretsManagerResponseMetadata responseMetadata();

        @Override
        public Builder responseMetadata(AwsResponseMetadata var1);
    }
}

