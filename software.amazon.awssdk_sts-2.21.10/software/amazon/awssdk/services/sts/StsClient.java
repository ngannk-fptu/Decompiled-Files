/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.AwsClient
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.regions.ServiceMetadata
 */
package software.amazon.awssdk.services.sts;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.services.sts.DefaultStsClientBuilder;
import software.amazon.awssdk.services.sts.StsClientBuilder;
import software.amazon.awssdk.services.sts.StsServiceClientConfiguration;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithSamlRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithSamlResponse;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityResponse;
import software.amazon.awssdk.services.sts.model.DecodeAuthorizationMessageRequest;
import software.amazon.awssdk.services.sts.model.DecodeAuthorizationMessageResponse;
import software.amazon.awssdk.services.sts.model.ExpiredTokenException;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoRequest;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoResponse;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.sts.model.GetFederationTokenRequest;
import software.amazon.awssdk.services.sts.model.GetFederationTokenResponse;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;
import software.amazon.awssdk.services.sts.model.IdpCommunicationErrorException;
import software.amazon.awssdk.services.sts.model.IdpRejectedClaimException;
import software.amazon.awssdk.services.sts.model.InvalidAuthorizationMessageException;
import software.amazon.awssdk.services.sts.model.InvalidIdentityTokenException;
import software.amazon.awssdk.services.sts.model.MalformedPolicyDocumentException;
import software.amazon.awssdk.services.sts.model.PackedPolicyTooLargeException;
import software.amazon.awssdk.services.sts.model.RegionDisabledException;
import software.amazon.awssdk.services.sts.model.StsException;

@SdkPublicApi
@ThreadSafe
public interface StsClient
extends AwsClient {
    public static final String SERVICE_NAME = "sts";
    public static final String SERVICE_METADATA_ID = "sts";

    default public AssumeRoleResponse assumeRole(AssumeRoleRequest assumeRoleRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, RegionDisabledException, ExpiredTokenException, AwsServiceException, SdkClientException, StsException {
        throw new UnsupportedOperationException();
    }

    default public AssumeRoleResponse assumeRole(Consumer<AssumeRoleRequest.Builder> assumeRoleRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, RegionDisabledException, ExpiredTokenException, AwsServiceException, SdkClientException, StsException {
        return this.assumeRole((AssumeRoleRequest)((Object)((AssumeRoleRequest.Builder)AssumeRoleRequest.builder().applyMutation(assumeRoleRequest)).build()));
    }

    default public AssumeRoleWithSamlResponse assumeRoleWithSAML(AssumeRoleWithSamlRequest assumeRoleWithSamlRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, IdpRejectedClaimException, InvalidIdentityTokenException, ExpiredTokenException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        throw new UnsupportedOperationException();
    }

    default public AssumeRoleWithSamlResponse assumeRoleWithSAML(Consumer<AssumeRoleWithSamlRequest.Builder> assumeRoleWithSamlRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, IdpRejectedClaimException, InvalidIdentityTokenException, ExpiredTokenException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        return this.assumeRoleWithSAML((AssumeRoleWithSamlRequest)((Object)((AssumeRoleWithSamlRequest.Builder)AssumeRoleWithSamlRequest.builder().applyMutation(assumeRoleWithSamlRequest)).build()));
    }

    default public AssumeRoleWithWebIdentityResponse assumeRoleWithWebIdentity(AssumeRoleWithWebIdentityRequest assumeRoleWithWebIdentityRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, IdpRejectedClaimException, IdpCommunicationErrorException, InvalidIdentityTokenException, ExpiredTokenException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        throw new UnsupportedOperationException();
    }

    default public AssumeRoleWithWebIdentityResponse assumeRoleWithWebIdentity(Consumer<AssumeRoleWithWebIdentityRequest.Builder> assumeRoleWithWebIdentityRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, IdpRejectedClaimException, IdpCommunicationErrorException, InvalidIdentityTokenException, ExpiredTokenException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        return this.assumeRoleWithWebIdentity((AssumeRoleWithWebIdentityRequest)((Object)((AssumeRoleWithWebIdentityRequest.Builder)AssumeRoleWithWebIdentityRequest.builder().applyMutation(assumeRoleWithWebIdentityRequest)).build()));
    }

    default public DecodeAuthorizationMessageResponse decodeAuthorizationMessage(DecodeAuthorizationMessageRequest decodeAuthorizationMessageRequest) throws InvalidAuthorizationMessageException, AwsServiceException, SdkClientException, StsException {
        throw new UnsupportedOperationException();
    }

    default public DecodeAuthorizationMessageResponse decodeAuthorizationMessage(Consumer<DecodeAuthorizationMessageRequest.Builder> decodeAuthorizationMessageRequest) throws InvalidAuthorizationMessageException, AwsServiceException, SdkClientException, StsException {
        return this.decodeAuthorizationMessage((DecodeAuthorizationMessageRequest)((Object)((DecodeAuthorizationMessageRequest.Builder)DecodeAuthorizationMessageRequest.builder().applyMutation(decodeAuthorizationMessageRequest)).build()));
    }

    default public GetAccessKeyInfoResponse getAccessKeyInfo(GetAccessKeyInfoRequest getAccessKeyInfoRequest) throws AwsServiceException, SdkClientException, StsException {
        throw new UnsupportedOperationException();
    }

    default public GetAccessKeyInfoResponse getAccessKeyInfo(Consumer<GetAccessKeyInfoRequest.Builder> getAccessKeyInfoRequest) throws AwsServiceException, SdkClientException, StsException {
        return this.getAccessKeyInfo((GetAccessKeyInfoRequest)((Object)((GetAccessKeyInfoRequest.Builder)GetAccessKeyInfoRequest.builder().applyMutation(getAccessKeyInfoRequest)).build()));
    }

    default public GetCallerIdentityResponse getCallerIdentity(GetCallerIdentityRequest getCallerIdentityRequest) throws AwsServiceException, SdkClientException, StsException {
        throw new UnsupportedOperationException();
    }

    default public GetCallerIdentityResponse getCallerIdentity(Consumer<GetCallerIdentityRequest.Builder> getCallerIdentityRequest) throws AwsServiceException, SdkClientException, StsException {
        return this.getCallerIdentity((GetCallerIdentityRequest)((Object)((GetCallerIdentityRequest.Builder)GetCallerIdentityRequest.builder().applyMutation(getCallerIdentityRequest)).build()));
    }

    default public GetCallerIdentityResponse getCallerIdentity() throws AwsServiceException, SdkClientException, StsException {
        return this.getCallerIdentity((GetCallerIdentityRequest)((Object)GetCallerIdentityRequest.builder().build()));
    }

    default public GetFederationTokenResponse getFederationToken(GetFederationTokenRequest getFederationTokenRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        throw new UnsupportedOperationException();
    }

    default public GetFederationTokenResponse getFederationToken(Consumer<GetFederationTokenRequest.Builder> getFederationTokenRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        return this.getFederationToken((GetFederationTokenRequest)((Object)((GetFederationTokenRequest.Builder)GetFederationTokenRequest.builder().applyMutation(getFederationTokenRequest)).build()));
    }

    default public GetSessionTokenResponse getSessionToken(GetSessionTokenRequest getSessionTokenRequest) throws RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        throw new UnsupportedOperationException();
    }

    default public GetSessionTokenResponse getSessionToken(Consumer<GetSessionTokenRequest.Builder> getSessionTokenRequest) throws RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        return this.getSessionToken((GetSessionTokenRequest)((Object)((GetSessionTokenRequest.Builder)GetSessionTokenRequest.builder().applyMutation(getSessionTokenRequest)).build()));
    }

    default public GetSessionTokenResponse getSessionToken() throws RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        return this.getSessionToken((GetSessionTokenRequest)((Object)GetSessionTokenRequest.builder().build()));
    }

    public static StsClient create() {
        return (StsClient)StsClient.builder().build();
    }

    public static StsClientBuilder builder() {
        return new DefaultStsClientBuilder();
    }

    public static ServiceMetadata serviceMetadata() {
        return ServiceMetadata.of((String)"sts");
    }

    default public StsServiceClientConfiguration serviceClientConfiguration() {
        throw new UnsupportedOperationException();
    }
}

