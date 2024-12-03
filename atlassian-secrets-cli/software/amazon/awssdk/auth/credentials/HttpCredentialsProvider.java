/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkPublicApi
public interface HttpCredentialsProvider
extends AwsCredentialsProvider,
SdkAutoCloseable {

    public static interface Builder<TypeToBuildT extends HttpCredentialsProvider, BuilderT extends Builder<?, ?>> {
        public BuilderT asyncCredentialUpdateEnabled(Boolean var1);

        public BuilderT asyncThreadName(String var1);

        public BuilderT endpoint(String var1);

        public TypeToBuildT build();
    }
}

