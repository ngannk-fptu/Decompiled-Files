/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.oauth2.sdk.as.ReadOnlyAuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.openid.connect.sdk.Display;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.assurance.IdentityTrustFramework;
import com.nimbusds.openid.connect.sdk.assurance.evidences.DocumentType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ElectronicRecordType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IDDocumentType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityEvidenceType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.IdentityVerificationMethod;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ValidationMethodType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.VerificationMethodType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.AttachmentType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.HashAlgorithm;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import com.nimbusds.openid.connect.sdk.claims.ClaimType;
import com.nimbusds.openid.connect.sdk.federation.registration.ClientRegistrationType;
import com.nimbusds.openid.connect.sdk.op.EndpointName;
import com.nimbusds.openid.connect.sdk.op.ReadOnlyOIDCProviderEndpointMetadata;
import java.util.List;
import java.util.Map;

public interface ReadOnlyOIDCProviderMetadata
extends ReadOnlyAuthorizationServerMetadata,
ReadOnlyOIDCProviderEndpointMetadata {
    @Override
    public ReadOnlyOIDCProviderEndpointMetadata getReadOnlyMtlsEndpointAliases();

    public List<ACR> getACRs();

    public List<SubjectType> getSubjectTypes();

    public List<JWSAlgorithm> getIDTokenJWSAlgs();

    public List<JWEAlgorithm> getIDTokenJWEAlgs();

    public List<EncryptionMethod> getIDTokenJWEEncs();

    public List<JWSAlgorithm> getUserInfoJWSAlgs();

    public List<JWEAlgorithm> getUserInfoJWEAlgs();

    public List<EncryptionMethod> getUserInfoJWEEncs();

    public List<Display> getDisplays();

    public List<ClaimType> getClaimTypes();

    public List<String> getClaims();

    public List<LangTag> getClaimsLocales();

    public boolean supportsClaimsParam();

    public boolean supportsFrontChannelLogout();

    public boolean supportsFrontChannelLogoutSession();

    public boolean supportsBackChannelLogout();

    public boolean supportsBackChannelLogoutSession();

    public boolean supportsVerifiedClaims();

    public List<IdentityTrustFramework> getIdentityTrustFrameworks();

    public List<IdentityEvidenceType> getIdentityEvidenceTypes();

    public List<DocumentType> getDocumentTypes();

    @Deprecated
    public List<IDDocumentType> getIdentityDocumentTypes();

    public List<IdentityVerificationMethod> getDocumentMethods();

    public List<ValidationMethodType> getDocumentValidationMethods();

    public List<VerificationMethodType> getDocumentVerificationMethods();

    public List<ElectronicRecordType> getElectronicRecordTypes();

    @Deprecated
    public List<IdentityVerificationMethod> getIdentityVerificationMethods();

    public List<String> getVerifiedClaims();

    public List<AttachmentType> getAttachmentTypes();

    public List<HashAlgorithm> getAttachmentDigestAlgs();

    public List<ClientRegistrationType> getClientRegistrationTypes();

    public Map<EndpointName, List<ClientAuthenticationMethod>> getClientRegistrationAuthnMethods();

    public String getOrganizationName();
}

