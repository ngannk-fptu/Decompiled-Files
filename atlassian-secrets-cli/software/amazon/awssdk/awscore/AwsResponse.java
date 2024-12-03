/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.AwsResponseMetadata;
import software.amazon.awssdk.core.SdkResponse;

@SdkPublicApi
public abstract class AwsResponse
extends SdkResponse {
    private AwsResponseMetadata responseMetadata;

    protected AwsResponse(Builder builder) {
        super(builder);
        this.responseMetadata = builder.responseMetadata();
    }

    public AwsResponseMetadata responseMetadata() {
        return this.responseMetadata;
    }

    @Override
    public abstract Builder toBuilder();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AwsResponse that = (AwsResponse)o;
        return Objects.equals(this.responseMetadata, that.responseMetadata);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.responseMetadata);
        return hashCode;
    }

    protected static abstract class BuilderImpl
    extends SdkResponse.BuilderImpl
    implements Builder {
        private AwsResponseMetadata responseMetadata;

        protected BuilderImpl() {
        }

        protected BuilderImpl(AwsResponse response) {
            super(response);
            this.responseMetadata = response.responseMetadata();
        }

        @Override
        public Builder responseMetadata(AwsResponseMetadata responseMetadata) {
            this.responseMetadata = responseMetadata;
            return this;
        }

        @Override
        public AwsResponseMetadata responseMetadata() {
            return this.responseMetadata;
        }
    }

    public static interface Builder
    extends SdkResponse.Builder {
        public AwsResponseMetadata responseMetadata();

        public Builder responseMetadata(AwsResponseMetadata var1);

        @Override
        public AwsResponse build();
    }
}

