/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.services.secretsmanager.model.TagKeyListTypeCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class UntagResourceRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, UntagResourceRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretId").getter(UntagResourceRequest.getter(UntagResourceRequest::secretId)).setter(UntagResourceRequest.setter(Builder::secretId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()}).build();
    private static final SdkField<List<String>> TAG_KEYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("TagKeys").getter(UntagResourceRequest.getter(UntagResourceRequest::tagKeys)).setter(UntagResourceRequest.setter(Builder::tagKeys)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TagKeys").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, TAG_KEYS_FIELD));
    private final String secretId;
    private final List<String> tagKeys;

    private UntagResourceRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.tagKeys = builder.tagKeys;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final boolean hasTagKeys() {
        return this.tagKeys != null && !(this.tagKeys instanceof SdkAutoConstructList);
    }

    public final List<String> tagKeys() {
        return this.tagKeys;
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

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.secretId());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTagKeys() ? this.tagKeys() : null);
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UntagResourceRequest)) {
            return false;
        }
        UntagResourceRequest other = (UntagResourceRequest)((Object)obj);
        return Objects.equals(this.secretId(), other.secretId()) && this.hasTagKeys() == other.hasTagKeys() && Objects.equals(this.tagKeys(), other.tagKeys());
    }

    public final String toString() {
        return ToString.builder((String)"UntagResourceRequest").add("SecretId", (Object)this.secretId()).add("TagKeys", this.hasTagKeys() ? this.tagKeys() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "TagKeys": {
                return Optional.ofNullable(clazz.cast(this.tagKeys()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<UntagResourceRequest, T> g) {
        return obj -> g.apply((UntagResourceRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private List<String> tagKeys = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(UntagResourceRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.tagKeys(model.tagKeys);
        }

        public final String getSecretId() {
            return this.secretId;
        }

        public final void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        @Override
        public final Builder secretId(String secretId) {
            this.secretId = secretId;
            return this;
        }

        public final Collection<String> getTagKeys() {
            if (this.tagKeys instanceof SdkAutoConstructList) {
                return null;
            }
            return this.tagKeys;
        }

        public final void setTagKeys(Collection<String> tagKeys) {
            this.tagKeys = TagKeyListTypeCopier.copy(tagKeys);
        }

        @Override
        public final Builder tagKeys(Collection<String> tagKeys) {
            this.tagKeys = TagKeyListTypeCopier.copy(tagKeys);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tagKeys(String ... tagKeys) {
            this.tagKeys(Arrays.asList(tagKeys));
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public UntagResourceRequest build() {
            return new UntagResourceRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, UntagResourceRequest> {
        public Builder secretId(String var1);

        public Builder tagKeys(Collection<String> var1);

        public Builder tagKeys(String ... var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

