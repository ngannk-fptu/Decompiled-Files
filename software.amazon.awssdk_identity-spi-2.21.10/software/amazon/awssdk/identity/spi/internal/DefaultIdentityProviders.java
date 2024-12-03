/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Lazy
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.identity.spi.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@Immutable
@SdkInternalApi
public final class DefaultIdentityProviders
implements IdentityProviders {
    private final Lazy<Map<Class<?>, IdentityProvider<?>>> identityProviders;
    private final List<IdentityProvider<?>> identityProvidersList;

    private DefaultIdentityProviders(BuilderImpl builder) {
        this.identityProvidersList = new ArrayList(builder.identityProviders);
        this.identityProviders = new Lazy(() -> {
            HashMap result = new HashMap();
            for (IdentityProvider<?> identityProvider : this.identityProvidersList) {
                result.put(identityProvider.identityType(), identityProvider);
            }
            return result;
        });
    }

    public static IdentityProviders.Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public <T extends Identity> IdentityProvider<T> identityProvider(Class<T> identityType) {
        return (IdentityProvider)((Map)this.identityProviders.getValue()).get(identityType);
    }

    public IdentityProviders.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public String toString() {
        return ToString.builder((String)"IdentityProviders").add("identityProviders", this.identityProvidersList).build();
    }

    private static final class BuilderImpl
    implements IdentityProviders.Builder {
        private final List<IdentityProvider<?>> identityProviders = new ArrayList();

        private BuilderImpl() {
        }

        private BuilderImpl(DefaultIdentityProviders identityProviders) {
            this.identityProviders.addAll(identityProviders.identityProvidersList);
        }

        @Override
        public <T extends Identity> IdentityProviders.Builder putIdentityProvider(IdentityProvider<T> identityProvider) {
            Validate.paramNotNull(identityProvider, (String)"identityProvider");
            this.identityProviders.add(identityProvider);
            return this;
        }

        public IdentityProviders build() {
            return new DefaultIdentityProviders(this);
        }
    }
}

