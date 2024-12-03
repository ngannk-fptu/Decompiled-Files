/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.AwsClient
 */
package software.amazon.awssdk.services.sts;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.services.sts.DefaultStsAsyncClientBuilder;
import software.amazon.awssdk.services.sts.StsAsyncClientBuilder;
import software.amazon.awssdk.services.sts.StsServiceClientConfiguration;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithSamlRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithSamlResponse;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityResponse;
import software.amazon.awssdk.services.sts.model.DecodeAuthorizationMessageRequest;
import software.amazon.awssdk.services.sts.model.DecodeAuthorizationMessageResponse;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoRequest;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoResponse;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.sts.model.GetFederationTokenRequest;
import software.amazon.awssdk.services.sts.model.GetFederationTokenResponse;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;

@SdkPublicApi
@ThreadSafe
public interface StsAsyncClient
extends AwsClient {
    public static final String SERVICE_NAME = "sts";
    public static final String SERVICE_METADATA_ID = "sts";

    default public CompletableFuture<AssumeRoleResponse> assumeRole(AssumeRoleRequest assumeRoleRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<AssumeRoleResponse> assumeRole(Consumer<AssumeRoleRequest.Builder> assumeRoleRequest) {
        return this.assumeRole((AssumeRoleRequest)((Object)((AssumeRoleRequest.Builder)AssumeRoleRequest.builder().applyMutation(assumeRoleRequest)).build()));
    }

    default public CompletableFuture<AssumeRoleWithSamlResponse> assumeRoleWithSAML(AssumeRoleWithSamlRequest assumeRoleWithSamlRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<AssumeRoleWithSamlResponse> assumeRoleWithSAML(Consumer<AssumeRoleWithSamlRequest.Builder> assumeRoleWithSamlRequest) {
        return this.assumeRoleWithSAML((AssumeRoleWithSamlRequest)((Object)((AssumeRoleWithSamlRequest.Builder)AssumeRoleWithSamlRequest.builder().applyMutation(assumeRoleWithSamlRequest)).build()));
    }

    default public CompletableFuture<AssumeRoleWithWebIdentityResponse> assumeRoleWithWebIdentity(AssumeRoleWithWebIdentityRequest assumeRoleWithWebIdentityRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<AssumeRoleWithWebIdentityResponse> assumeRoleWithWebIdentity(Consumer<AssumeRoleWithWebIdentityRequest.Builder> assumeRoleWithWebIdentityRequest) {
        return this.assumeRoleWithWebIdentity((AssumeRoleWithWebIdentityRequest)((Object)((AssumeRoleWithWebIdentityRequest.Builder)AssumeRoleWithWebIdentityRequest.builder().applyMutation(assumeRoleWithWebIdentityRequest)).build()));
    }

    default public CompletableFuture<DecodeAuthorizationMessageResponse> decodeAuthorizationMessage(DecodeAuthorizationMessageRequest decodeAuthorizationMessageRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DecodeAuthorizationMessageResponse> decodeAuthorizationMessage(Consumer<DecodeAuthorizationMessageRequest.Builder> decodeAuthorizationMessageRequest) {
        return this.decodeAuthorizationMessage((DecodeAuthorizationMessageRequest)((Object)((DecodeAuthorizationMessageRequest.Builder)DecodeAuthorizationMessageRequest.builder().applyMutation(decodeAuthorizationMessageRequest)).build()));
    }

    default public CompletableFuture<GetAccessKeyInfoResponse> getAccessKeyInfo(GetAccessKeyInfoRequest getAccessKeyInfoRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetAccessKeyInfoResponse> getAccessKeyInfo(Consumer<GetAccessKeyInfoRequest.Builder> getAccessKeyInfoRequest) {
        return this.getAccessKeyInfo((GetAccessKeyInfoRequest)((Object)((GetAccessKeyInfoRequest.Builder)GetAccessKeyInfoRequest.builder().applyMutation(getAccessKeyInfoRequest)).build()));
    }

    default public CompletableFuture<GetCallerIdentityResponse> getCallerIdentity(GetCallerIdentityRequest getCallerIdentityRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetCallerIdentityResponse> getCallerIdentity(Consumer<GetCallerIdentityRequest.Builder> getCallerIdentityRequest) {
        return this.getCallerIdentity((GetCallerIdentityRequest)((Object)((GetCallerIdentityRequest.Builder)GetCallerIdentityRequest.builder().applyMutation(getCallerIdentityRequest)).build()));
    }

    default public CompletableFuture<GetCallerIdentityResponse> getCallerIdentity() {
        return this.getCallerIdentity((GetCallerIdentityRequest)((Object)GetCallerIdentityRequest.builder().build()));
    }

    default public CompletableFuture<GetFederationTokenResponse> getFederationToken(GetFederationTokenRequest getFederationTokenRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetFederationTokenResponse> getFederationToken(Consumer<GetFederationTokenRequest.Builder> getFederationTokenRequest) {
        return this.getFederationToken((GetFederationTokenRequest)((Object)((GetFederationTokenRequest.Builder)GetFederationTokenRequest.builder().applyMutation(getFederationTokenRequest)).build()));
    }

    default public CompletableFuture<GetSessionTokenResponse> getSessionToken(GetSessionTokenRequest getSessionTokenRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetSessionTokenResponse> getSessionToken(Consumer<GetSessionTokenRequest.Builder> getSessionTokenRequest) {
        return this.getSessionToken((GetSessionTokenRequest)((Object)((GetSessionTokenRequest.Builder)GetSessionTokenRequest.builder().applyMutation(getSessionTokenRequest)).build()));
    }

    default public CompletableFuture<GetSessionTokenResponse> getSessionToken() {
        return this.getSessionToken((GetSessionTokenRequest)((Object)GetSessionTokenRequest.builder().build()));
    }

    default public StsServiceClientConfiguration serviceClientConfiguration() {
        throw new UnsupportedOperationException();
    }

    public static StsAsyncClient create() {
        return (StsAsyncClient)StsAsyncClient.builder().build();
    }

    public static StsAsyncClientBuilder builder() {
        return new DefaultStsAsyncClientBuilder();
    }
}

