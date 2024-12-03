/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.SecretVersionsListEntry;
import software.amazon.awssdk.services.secretsmanager.model.SecretVersionsListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListSecretVersionIdsResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, ListSecretVersionIdsResponse> {
    private static final SdkField<List<SecretVersionsListEntry>> VERSIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Versions").getter(ListSecretVersionIdsResponse.getter(ListSecretVersionIdsResponse::versions)).setter(ListSecretVersionIdsResponse.setter(Builder::versions)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Versions").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(SecretVersionsListEntry::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<String> NEXT_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextToken").getter(ListSecretVersionIdsResponse.getter(ListSecretVersionIdsResponse::nextToken)).setter(ListSecretVersionIdsResponse.setter(Builder::nextToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextToken").build()}).build();
    private static final SdkField<String> ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ARN").getter(ListSecretVersionIdsResponse.getter(ListSecretVersionIdsResponse::arn)).setter(ListSecretVersionIdsResponse.setter(Builder::arn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ARN").build()}).build();
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(ListSecretVersionIdsResponse.getter(ListSecretVersionIdsResponse::name)).setter(ListSecretVersionIdsResponse.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(VERSIONS_FIELD, NEXT_TOKEN_FIELD, ARN_FIELD, NAME_FIELD));
    private final List<SecretVersionsListEntry> versions;
    private final String nextToken;
    private final String arn;
    private final String name;

    private ListSecretVersionIdsResponse(BuilderImpl builder) {
        super(builder);
        this.versions = builder.versions;
        this.nextToken = builder.nextToken;
        this.arn = builder.arn;
        this.name = builder.name;
    }

    public final boolean hasVersions() {
        return this.versions != null && !(this.versions instanceof SdkAutoConstructList);
    }

    public final List<SecretVersionsListEntry> versions() {
        return this.versions;
    }

    public final String nextToken() {
        return this.nextToken;
    }

    public final String arn() {
        return this.arn;
    }

    public final String name() {
        return this.name;
    }

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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasVersions() ? this.versions() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.nextToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.arn());
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
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
        if (!(obj instanceof ListSecretVersionIdsResponse)) {
            return false;
        }
        ListSecretVersionIdsResponse other = (ListSecretVersionIdsResponse)((Object)obj);
        return this.hasVersions() == other.hasVersions() && Objects.equals(this.versions(), other.versions()) && Objects.equals(this.nextToken(), other.nextToken()) && Objects.equals(this.arn(), other.arn()) && Objects.equals(this.name(), other.name());
    }

    public final String toString() {
        return ToString.builder((String)"ListSecretVersionIdsResponse").add("Versions", this.hasVersions() ? this.versions() : null).add("NextToken", (Object)this.nextToken()).add("ARN", (Object)this.arn()).add("Name", (Object)this.name()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Versions": {
                return Optional.ofNullable(clazz.cast(this.versions()));
            }
            case "NextToken": {
                return Optional.ofNullable(clazz.cast(this.nextToken()));
            }
            case "ARN": {
                return Optional.ofNullable(clazz.cast(this.arn()));
            }
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListSecretVersionIdsResponse, T> g) {
        return obj -> g.apply((ListSecretVersionIdsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private List<SecretVersionsListEntry> versions = DefaultSdkAutoConstructList.getInstance();
        private String nextToken;
        private String arn;
        private String name;

        private BuilderImpl() {
        }

        private BuilderImpl(ListSecretVersionIdsResponse model) {
            super(model);
            this.versions(model.versions);
            this.nextToken(model.nextToken);
            this.arn(model.arn);
            this.name(model.name);
        }

        public final List<SecretVersionsListEntry.Builder> getVersions() {
            List<SecretVersionsListEntry.Builder> result = SecretVersionsListTypeCopier.copyToBuilder(this.versions);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setVersions(Collection<SecretVersionsListEntry.BuilderImpl> versions) {
            this.versions = SecretVersionsListTypeCopier.copyFromBuilder(versions);
        }

        @Override
        public final Builder versions(Collection<SecretVersionsListEntry> versions) {
            this.versions = SecretVersionsListTypeCopier.copy(versions);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder versions(SecretVersionsListEntry ... versions) {
            this.versions(Arrays.asList(versions));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder versions(Consumer<SecretVersionsListEntry.Builder> ... versions) {
            this.versions(Stream.of(versions).map(c -> (SecretVersionsListEntry)((SecretVersionsListEntry.Builder)SecretVersionsListEntry.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final String getNextToken() {
            return this.nextToken;
        }

        public final void setNextToken(String nextToken) {
            this.nextToken = nextToken;
        }

        @Override
        public final Builder nextToken(String nextToken) {
            this.nextToken = nextToken;
            return this;
        }

        public final String getArn() {
            return this.arn;
        }

        public final void setArn(String arn) {
            this.arn = arn;
        }

        @Override
        public final Builder arn(String arn) {
            this.arn = arn;
            return this;
        }

        public final String getName() {
            return this.name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        @Override
        public final Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public ListSecretVersionIdsResponse build() {
            return new ListSecretVersionIdsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListSecretVersionIdsResponse> {
        public Builder versions(Collection<SecretVersionsListEntry> var1);

        public Builder versions(SecretVersionsListEntry ... var1);

        public Builder versions(Consumer<SecretVersionsListEntry.Builder> ... var1);

        public Builder nextToken(String var1);

        public Builder arn(String var1);

        public Builder name(String var1);
    }
}

