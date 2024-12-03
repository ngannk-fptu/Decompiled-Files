/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.auth.spi.signer;

import java.util.Optional;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignerProperty;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface BaseSignRequest<PayloadT, IdentityT extends Identity> {
    public SdkHttpRequest request();

    public Optional<PayloadT> payload();

    public IdentityT identity();

    public <T> T property(SignerProperty<T> var1);

    default public <T> boolean hasProperty(SignerProperty<T> property) {
        return this.property(property) != null;
    }

    default public <T> T requireProperty(SignerProperty<T> property) {
        return (T)Validate.notNull(this.property(property), (String)(property.toString() + " must not be null!"), (Object[])new Object[0]);
    }

    default public <T> T requireProperty(SignerProperty<T> property, T defaultValue) {
        return (T)Validate.getOrDefault(this.property(property), () -> defaultValue);
    }

    public static interface Builder<B extends Builder<B, PayloadT, IdentityT>, PayloadT, IdentityT extends Identity> {
        public B request(SdkHttpRequest var1);

        public B payload(PayloadT var1);

        public B identity(IdentityT var1);

        public <T> B putProperty(SignerProperty<T> var1, T var2);
    }
}

