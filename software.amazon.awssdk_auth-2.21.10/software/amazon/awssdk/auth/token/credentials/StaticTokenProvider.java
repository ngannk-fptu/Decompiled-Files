/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
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
        this.token = (SdkToken)Validate.notNull((Object)token, (String)"Token must not be null.", (Object[])new Object[0]);
    }

    public static StaticTokenProvider create(SdkToken token) {
        return new StaticTokenProvider(token);
    }

    @Override
    public SdkToken resolveToken() {
        return this.token;
    }

    public String toString() {
        return ToString.builder((String)"StaticTokenProvider").add("token", (Object)this.token).build();
    }
}

