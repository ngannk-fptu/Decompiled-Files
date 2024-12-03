/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.auth.signer.params;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public class TokenSignerParams {
    private final SdkToken token;

    TokenSignerParams(BuilderImpl<?> builder) {
        this.token = (SdkToken)Validate.paramNotNull((Object)((BuilderImpl)builder).token, (String)"Signing token");
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public SdkToken token() {
        return this.token;
    }

    protected static class BuilderImpl<B extends Builder>
    implements Builder<B> {
        private SdkToken token;

        protected BuilderImpl() {
        }

        @Override
        public B token(SdkToken token) {
            this.token = token;
            return (B)this;
        }

        public void setToken(SdkToken token) {
            this.token(token);
        }

        @Override
        public TokenSignerParams build() {
            return new TokenSignerParams(this);
        }
    }

    public static interface Builder<B extends Builder> {
        public B token(SdkToken var1);

        public TokenSignerParams build();
    }
}

