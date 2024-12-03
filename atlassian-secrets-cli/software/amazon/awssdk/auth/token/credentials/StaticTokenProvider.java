/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.token.credentials;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProvider;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class StaticTokenProvider
implements SdkTokenProvider {
    private final SdkToken token;

    private StaticTokenProvider(SdkToken token) {
        this.token = Validate.notNull(token, "Token must not be null.", new Object[0]);
    }

    public static StaticTokenProvider create(SdkToken token) {
        return new StaticTokenProvider(token);
    }

    @Override
    public SdkToken resolveToken() {
        return this.token;
    }

    public String toString() {
        return ToString.builder("StaticTokenProvider").add("token", this.token).build();
    }
}

