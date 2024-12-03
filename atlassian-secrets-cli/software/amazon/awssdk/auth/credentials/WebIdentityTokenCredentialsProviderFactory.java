/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.internal.WebIdentityTokenCredentialProperties;

@FunctionalInterface
@SdkProtectedApi
public interface WebIdentityTokenCredentialsProviderFactory {
    public AwsCredentialsProvider create(WebIdentityTokenCredentialProperties var1);
}

