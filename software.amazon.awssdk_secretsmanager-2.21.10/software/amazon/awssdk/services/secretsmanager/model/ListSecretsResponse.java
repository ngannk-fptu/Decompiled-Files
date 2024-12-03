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
import software.amazon.awssdk.services.secretsmanager.model.SecretListEntry;
import software.amazon.awssdk.services.secretsmanager.model.SecretListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListSecretsResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, ListSecretsResponse> {
    private static final SdkField<List<SecretListEntry>> SECRET_LIST_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("SecretList").getter(ListSecretsResponse.getter(ListSecretsResponse::secretList)).setter(ListSecretsResponse.setter(Builder::secretList)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretList").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(SecretListEntry::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<String> NEXT_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextToken").getter(ListSecretsResponse.getter(ListSecretsResponse::nextToken)).setter(ListSecretsResponse.setter(Builder::nextToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextToken").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_LIST_FIELD, NEXT_TOKEN_FIELD));
    private final List<SecretListEntry> secretList;
    private final String nextToken;

    private ListSecretsResponse(BuilderImpl builder) {
        super(builder);
        this.secretList = builder.secretList;
        this.nextToken = builder.nextToken;
    }

    public final boolean hasSecretList() {
        return this.secretList != null && !(this.secretList instanceof SdkAutoConstructList);
    }

    public final List<SecretListEntry> secretList() {
        return this.secretList;
    }

    public final String nextToken() {
        return this.nextToken;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasSecretList() ? this.secretList() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.nextToken());
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
        if (!(obj instanceof ListSecretsResponse)) {
            return false;
        }
        ListSecretsResponse other = (ListSecretsResponse)((Object)obj);
        return this.hasSecretList() == other.hasSecretList() && Objects.equals(this.secretList(), other.secretList()) && Objects.equals(this.nextToken(), other.nextToken());
    }

    public final String toString() {
        return ToString.builder((String)"ListSecretsResponse").add("SecretList", this.hasSecretList() ? this.secretList() : null).add("NextToken", (Object)this.nextToken()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretList": {
                return Optional.ofNullable(clazz.cast(this.secretList()));
            }
            case "NextToken": {
                return Optional.ofNullable(clazz.cast(this.nextToken()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListSecretsResponse, T> g) {
        return obj -> g.apply((ListSecretsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private List<SecretListEntry> secretList = DefaultSdkAutoConstructList.getInstance();
        private String nextToken;

        private BuilderImpl() {
        }

        private BuilderImpl(ListSecretsResponse model) {
            super(model);
            this.secretList(model.secretList);
            this.nextToken(model.nextToken);
        }

        public final List<SecretListEntry.Builder> getSecretList() {
            List<SecretListEntry.Builder> result = SecretListTypeCopier.copyToBuilder(this.secretList);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setSecretList(Collection<SecretListEntry.BuilderImpl> secretList) {
            this.secretList = SecretListTypeCopier.copyFromBuilder(secretList);
        }

        @Override
        public final Builder secretList(Collection<SecretListEntry> secretList) {
            this.secretList = SecretListTypeCopier.copy(secretList);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder secretList(SecretListEntry ... secretList) {
            this.secretList(Arrays.asList(secretList));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder secretList(Consumer<SecretListEntry.Builder> ... secretList) {
            this.secretList(Stream.of(secretList).map(c -> (SecretListEntry)((SecretListEntry.Builder)SecretListEntry.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
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

        @Override
        public ListSecretsResponse build() {
            return new ListSecretsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListSecretsResponse> {
        public Builder secretList(Collection<SecretListEntry> var1);

        public Builder secretList(SecretListEntry ... var1);

        public Builder secretList(Consumer<SecretListEntry.Builder> ... var1);

        public Builder nextToken(String var1);
    }
}

