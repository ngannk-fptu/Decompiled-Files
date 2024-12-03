/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.exception.AwsErrorDetails
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.IntelligentTieringAccessTier;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class InvalidObjectStateException
extends S3Exception
implements ToCopyableBuilder<Builder, InvalidObjectStateException> {
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(InvalidObjectStateException.getter(InvalidObjectStateException::storageClassAsString)).setter(InvalidObjectStateException.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClass").unmarshallLocationName("StorageClass").build()}).build();
    private static final SdkField<String> ACCESS_TIER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AccessTier").getter(InvalidObjectStateException.getter(InvalidObjectStateException::accessTierAsString)).setter(InvalidObjectStateException.setter(Builder::accessTier)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessTier").unmarshallLocationName("AccessTier").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(STORAGE_CLASS_FIELD, ACCESS_TIER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String storageClass;
    private final String accessTier;

    private InvalidObjectStateException(BuilderImpl builder) {
        super(builder);
        this.storageClass = builder.storageClass;
        this.accessTier = builder.accessTier;
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public StorageClass storageClass() {
        return StorageClass.fromValue(this.storageClass);
    }

    public String storageClassAsString() {
        return this.storageClass;
    }

    public IntelligentTieringAccessTier accessTier() {
        return IntelligentTieringAccessTier.fromValue(this.accessTier);
    }

    public String accessTierAsString() {
        return this.accessTier;
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<InvalidObjectStateException, T> g) {
        return obj -> g.apply((InvalidObjectStateException)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Exception.BuilderImpl
    implements Builder {
        private String storageClass;
        private String accessTier;

        private BuilderImpl() {
        }

        private BuilderImpl(InvalidObjectStateException model) {
            super(model);
            this.storageClass(model.storageClass);
            this.accessTier(model.accessTier);
        }

        public final String getStorageClass() {
            return this.storageClass;
        }

        public final void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }

        @Override
        public final Builder storageClass(String storageClass) {
            this.storageClass = storageClass;
            return this;
        }

        @Override
        public final Builder storageClass(StorageClass storageClass) {
            this.storageClass(storageClass == null ? null : storageClass.toString());
            return this;
        }

        public final String getAccessTier() {
            return this.accessTier;
        }

        public final void setAccessTier(String accessTier) {
            this.accessTier = accessTier;
        }

        @Override
        public final Builder accessTier(String accessTier) {
            this.accessTier = accessTier;
            return this;
        }

        @Override
        public final Builder accessTier(IntelligentTieringAccessTier accessTier) {
            this.accessTier(accessTier == null ? null : accessTier.toString());
            return this;
        }

        @Override
        public BuilderImpl awsErrorDetails(AwsErrorDetails awsErrorDetails) {
            this.awsErrorDetails = awsErrorDetails;
            return this;
        }

        @Override
        public BuilderImpl message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public BuilderImpl requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        @Override
        public BuilderImpl statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        @Override
        public BuilderImpl cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        @Override
        public BuilderImpl writableStackTrace(Boolean writableStackTrace) {
            this.writableStackTrace = writableStackTrace;
            return this;
        }

        @Override
        public InvalidObjectStateException build() {
            return new InvalidObjectStateException(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, InvalidObjectStateException>,
    S3Exception.Builder {
        public Builder storageClass(String var1);

        public Builder storageClass(StorageClass var1);

        public Builder accessTier(String var1);

        public Builder accessTier(IntelligentTieringAccessTier var1);

        @Override
        public Builder awsErrorDetails(AwsErrorDetails var1);

        @Override
        public Builder message(String var1);

        @Override
        public Builder requestId(String var1);

        @Override
        public Builder statusCode(int var1);

        @Override
        public Builder cause(Throwable var1);

        @Override
        public Builder writableStackTrace(Boolean var1);
    }
}

