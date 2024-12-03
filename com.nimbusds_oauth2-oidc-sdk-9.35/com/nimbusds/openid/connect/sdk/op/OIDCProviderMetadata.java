/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.EncryptionMethod
 *  com.nimbusds.jose.JWEAlgorithm
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.langtag.LangTag
 *  com.nimbusds.langtag.LangTagException
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerEndpointMetadata;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.MapUtils;
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
import com.nimbusds.openid.connect.sdk.op.OIDCProviderEndpointMetadata;
import com.nimbusds.openid.connect.sdk.op.ReadOnlyOIDCProviderMetadata;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;

public class OIDCProviderMetadata
extends AuthorizationServerMetadata
implements ReadOnlyOIDCProviderMetadata {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private URI userInfoEndpoint;
    private URI checkSessionIframe;
    private URI endSessionEndpoint;
    private List<ACR> acrValues;
    private final List<SubjectType> subjectTypes;
    private List<JWSAlgorithm> idTokenJWSAlgs;
    private List<JWEAlgorithm> idTokenJWEAlgs;
    private List<EncryptionMethod> idTokenJWEEncs;
    private List<JWSAlgorithm> userInfoJWSAlgs;
    private List<JWEAlgorithm> userInfoJWEAlgs;
    private List<EncryptionMethod> userInfoJWEEncs;
    private List<Display> displays;
    private List<ClaimType> claimTypes;
    private List<String> claims;
    private List<LangTag> claimsLocales;
    private boolean claimsParamSupported = false;
    private boolean frontChannelLogoutSupported = false;
    private boolean frontChannelLogoutSessionSupported = false;
    private boolean backChannelLogoutSupported = false;
    private boolean backChannelLogoutSessionSupported = false;
    private boolean verifiedClaimsSupported = false;
    private List<IdentityTrustFramework> trustFrameworks;
    private List<IdentityEvidenceType> evidenceTypes;
    private List<DocumentType> documentTypes;
    private List<IdentityVerificationMethod> documentMethods;
    private List<ValidationMethodType> documentValidationMethods;
    private List<VerificationMethodType> documentVerificationMethods;
    @Deprecated
    private List<IDDocumentType> idDocumentTypes;
    @Deprecated
    private List<IdentityVerificationMethod> idVerificationMethods;
    private List<ElectronicRecordType> electronicRecordTypes;
    private List<String> verifiedClaims;
    private List<AttachmentType> attachmentTypes;
    private List<HashAlgorithm> attachmentDigestAlgs;
    private List<ClientRegistrationType> clientRegistrationTypes;
    private Map<EndpointName, List<ClientAuthenticationMethod>> clientRegistrationAuthMethods;
    private String organizationName;
    private URI federationRegistrationEndpoint;

    public OIDCProviderMetadata(Issuer issuer, List<SubjectType> subjectTypes, URI jwkSetURI) {
        super(issuer);
        if (subjectTypes.size() < 1) {
            throw new IllegalArgumentException("At least one supported subject type must be specified");
        }
        this.subjectTypes = subjectTypes;
        if (jwkSetURI == null) {
            throw new IllegalArgumentException("The public JWK set URI must not be null");
        }
        this.setJWKSetURI(jwkSetURI);
        this.setSupportsRequestURIParam(true);
    }

    @Override
    public void setMtlsEndpointAliases(AuthorizationServerEndpointMetadata mtlsEndpointAliases) {
        if (mtlsEndpointAliases != null && !(mtlsEndpointAliases instanceof OIDCProviderEndpointMetadata)) {
            super.setMtlsEndpointAliases(new OIDCProviderEndpointMetadata(mtlsEndpointAliases));
        } else {
            super.setMtlsEndpointAliases(mtlsEndpointAliases);
        }
    }

    @Override
    public OIDCProviderEndpointMetadata getReadOnlyMtlsEndpointAliases() {
        return this.getMtlsEndpointAliases();
    }

    @Override
    public OIDCProviderEndpointMetadata getMtlsEndpointAliases() {
        return (OIDCProviderEndpointMetadata)super.getMtlsEndpointAliases();
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    @Override
    public URI getUserInfoEndpointURI() {
        return this.userInfoEndpoint;
    }

    public void setUserInfoEndpointURI(URI userInfoEndpoint) {
        this.userInfoEndpoint = userInfoEndpoint;
    }

    @Override
    public URI getCheckSessionIframeURI() {
        return this.checkSessionIframe;
    }

    public void setCheckSessionIframeURI(URI checkSessionIframe) {
        this.checkSessionIframe = checkSessionIframe;
    }

    @Override
    public URI getEndSessionEndpointURI() {
        return this.endSessionEndpoint;
    }

    public void setEndSessionEndpointURI(URI endSessionEndpoint) {
        this.endSessionEndpoint = endSessionEndpoint;
    }

    @Override
    public List<ACR> getACRs() {
        return this.acrValues;
    }

    public void setACRs(List<ACR> acrValues) {
        this.acrValues = acrValues;
    }

    @Override
    public List<SubjectType> getSubjectTypes() {
        return this.subjectTypes;
    }

    @Override
    public List<JWSAlgorithm> getIDTokenJWSAlgs() {
        return this.idTokenJWSAlgs;
    }

    public void setIDTokenJWSAlgs(List<JWSAlgorithm> idTokenJWSAlgs) {
        this.idTokenJWSAlgs = idTokenJWSAlgs;
    }

    @Override
    public List<JWEAlgorithm> getIDTokenJWEAlgs() {
        return this.idTokenJWEAlgs;
    }

    public void setIDTokenJWEAlgs(List<JWEAlgorithm> idTokenJWEAlgs) {
        this.idTokenJWEAlgs = idTokenJWEAlgs;
    }

    @Override
    public List<EncryptionMethod> getIDTokenJWEEncs() {
        return this.idTokenJWEEncs;
    }

    public void setIDTokenJWEEncs(List<EncryptionMethod> idTokenJWEEncs) {
        this.idTokenJWEEncs = idTokenJWEEncs;
    }

    @Override
    public List<JWSAlgorithm> getUserInfoJWSAlgs() {
        return this.userInfoJWSAlgs;
    }

    public void setUserInfoJWSAlgs(List<JWSAlgorithm> userInfoJWSAlgs) {
        this.userInfoJWSAlgs = userInfoJWSAlgs;
    }

    @Override
    public List<JWEAlgorithm> getUserInfoJWEAlgs() {
        return this.userInfoJWEAlgs;
    }

    public void setUserInfoJWEAlgs(List<JWEAlgorithm> userInfoJWEAlgs) {
        this.userInfoJWEAlgs = userInfoJWEAlgs;
    }

    @Override
    public List<EncryptionMethod> getUserInfoJWEEncs() {
        return this.userInfoJWEEncs;
    }

    public void setUserInfoJWEEncs(List<EncryptionMethod> userInfoJWEEncs) {
        this.userInfoJWEEncs = userInfoJWEEncs;
    }

    @Override
    public List<Display> getDisplays() {
        return this.displays;
    }

    public void setDisplays(List<Display> displays) {
        this.displays = displays;
    }

    @Override
    public List<ClaimType> getClaimTypes() {
        return this.claimTypes;
    }

    public void setClaimTypes(List<ClaimType> claimTypes) {
        this.claimTypes = claimTypes;
    }

    @Override
    public List<String> getClaims() {
        return this.claims;
    }

    public void setClaims(List<String> claims) {
        this.claims = claims;
    }

    @Override
    public List<LangTag> getClaimsLocales() {
        return this.claimsLocales;
    }

    public void setClaimLocales(List<LangTag> claimsLocales) {
        this.claimsLocales = claimsLocales;
    }

    @Override
    public boolean supportsClaimsParam() {
        return this.claimsParamSupported;
    }

    public void setSupportsClaimsParams(boolean claimsParamSupported) {
        this.claimsParamSupported = claimsParamSupported;
    }

    @Override
    public boolean supportsFrontChannelLogout() {
        return this.frontChannelLogoutSupported;
    }

    public void setSupportsFrontChannelLogout(boolean frontChannelLogoutSupported) {
        this.frontChannelLogoutSupported = frontChannelLogoutSupported;
    }

    @Override
    public boolean supportsFrontChannelLogoutSession() {
        return this.frontChannelLogoutSessionSupported;
    }

    public void setSupportsFrontChannelLogoutSession(boolean frontChannelLogoutSessionSupported) {
        this.frontChannelLogoutSessionSupported = frontChannelLogoutSessionSupported;
    }

    @Override
    public boolean supportsBackChannelLogout() {
        return this.backChannelLogoutSupported;
    }

    public void setSupportsBackChannelLogout(boolean backChannelLogoutSupported) {
        this.backChannelLogoutSupported = backChannelLogoutSupported;
    }

    @Override
    public boolean supportsBackChannelLogoutSession() {
        return this.backChannelLogoutSessionSupported;
    }

    public void setSupportsBackChannelLogoutSession(boolean backChannelLogoutSessionSupported) {
        this.backChannelLogoutSessionSupported = backChannelLogoutSessionSupported;
    }

    @Override
    public boolean supportsVerifiedClaims() {
        return this.verifiedClaimsSupported;
    }

    public void setSupportsVerifiedClaims(boolean verifiedClaimsSupported) {
        this.verifiedClaimsSupported = verifiedClaimsSupported;
    }

    @Override
    public List<IdentityTrustFramework> getIdentityTrustFrameworks() {
        return this.trustFrameworks;
    }

    public void setIdentityTrustFrameworks(List<IdentityTrustFramework> trustFrameworks) {
        this.trustFrameworks = trustFrameworks;
    }

    @Override
    public List<IdentityEvidenceType> getIdentityEvidenceTypes() {
        return this.evidenceTypes;
    }

    public void setIdentityEvidenceTypes(List<IdentityEvidenceType> evidenceTypes) {
        this.evidenceTypes = evidenceTypes;
    }

    @Override
    public List<DocumentType> getDocumentTypes() {
        return this.documentTypes;
    }

    public void setDocumentTypes(List<DocumentType> documentTypes) {
        this.documentTypes = documentTypes;
    }

    @Override
    @Deprecated
    public List<IDDocumentType> getIdentityDocumentTypes() {
        return this.idDocumentTypes;
    }

    @Deprecated
    public void setIdentityDocumentTypes(List<IDDocumentType> idDocuments) {
        this.idDocumentTypes = idDocuments;
    }

    @Override
    public List<IdentityVerificationMethod> getDocumentMethods() {
        return this.documentMethods;
    }

    public void setDocumentMethods(List<IdentityVerificationMethod> methods) {
        this.documentMethods = methods;
    }

    @Override
    public List<ValidationMethodType> getDocumentValidationMethods() {
        return this.documentValidationMethods;
    }

    public void setDocumentValidationMethods(List<ValidationMethodType> methods) {
        this.documentValidationMethods = methods;
    }

    @Override
    public List<VerificationMethodType> getDocumentVerificationMethods() {
        return this.documentVerificationMethods;
    }

    public void setDocumentVerificationMethods(List<VerificationMethodType> methods) {
        this.documentVerificationMethods = methods;
    }

    @Override
    public List<ElectronicRecordType> getElectronicRecordTypes() {
        return this.electronicRecordTypes;
    }

    public void setElectronicRecordTypes(List<ElectronicRecordType> electronicRecordTypes) {
        this.electronicRecordTypes = electronicRecordTypes;
    }

    @Override
    @Deprecated
    public List<IdentityVerificationMethod> getIdentityVerificationMethods() {
        return this.idVerificationMethods;
    }

    @Deprecated
    public void setIdentityVerificationMethods(List<IdentityVerificationMethod> idVerificationMethods) {
        this.idVerificationMethods = idVerificationMethods;
    }

    @Override
    public List<String> getVerifiedClaims() {
        return this.verifiedClaims;
    }

    public void setVerifiedClaims(List<String> verifiedClaims) {
        this.verifiedClaims = verifiedClaims;
    }

    @Override
    public List<AttachmentType> getAttachmentTypes() {
        return this.attachmentTypes;
    }

    public void setAttachmentTypes(List<AttachmentType> attachmentTypes) {
        this.attachmentTypes = attachmentTypes;
    }

    @Override
    public List<HashAlgorithm> getAttachmentDigestAlgs() {
        return this.attachmentDigestAlgs;
    }

    public void setAttachmentDigestAlgs(List<HashAlgorithm> digestAlgs) {
        this.attachmentDigestAlgs = digestAlgs;
    }

    @Override
    public List<ClientRegistrationType> getClientRegistrationTypes() {
        return this.clientRegistrationTypes;
    }

    public void setClientRegistrationTypes(List<ClientRegistrationType> clientRegistrationTypes) {
        this.clientRegistrationTypes = clientRegistrationTypes;
    }

    @Override
    public Map<EndpointName, List<ClientAuthenticationMethod>> getClientRegistrationAuthnMethods() {
        return this.clientRegistrationAuthMethods;
    }

    public void setClientRegistrationAuthnMethods(Map<EndpointName, List<ClientAuthenticationMethod>> methods) {
        this.clientRegistrationAuthMethods = methods;
    }

    @Override
    public String getOrganizationName() {
        return this.organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    @Override
    public URI getFederationRegistrationEndpointURI() {
        return this.federationRegistrationEndpoint;
    }

    public void setFederationRegistrationEndpointURI(URI federationRegistrationEndpoint) {
        this.federationRegistrationEndpoint = federationRegistrationEndpoint;
    }

    @Override
    public void applyDefaults() {
        super.applyDefaults();
        if (this.claimTypes == null) {
            this.claimTypes = new ArrayList<ClaimType>(1);
            this.claimTypes.add(ClaimType.NORMAL);
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        ArrayList<String> stringList = new ArrayList<String>(this.subjectTypes.size());
        for (SubjectType subjectType : this.subjectTypes) {
            stringList.add(subjectType.toString());
        }
        o.put((Object)"subject_types_supported", stringList);
        if (this.userInfoEndpoint != null) {
            o.put((Object)"userinfo_endpoint", (Object)this.userInfoEndpoint.toString());
        }
        if (this.checkSessionIframe != null) {
            o.put((Object)"check_session_iframe", (Object)this.checkSessionIframe.toString());
        }
        if (this.endSessionEndpoint != null) {
            o.put((Object)"end_session_endpoint", (Object)this.endSessionEndpoint.toString());
        }
        if (this.acrValues != null) {
            o.put((Object)"acr_values_supported", Identifier.toStringList(this.acrValues));
        }
        if (this.idTokenJWSAlgs != null) {
            stringList = new ArrayList(this.idTokenJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.idTokenJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"id_token_signing_alg_values_supported", stringList);
        }
        if (this.idTokenJWEAlgs != null) {
            stringList = new ArrayList(this.idTokenJWEAlgs.size());
            for (JWEAlgorithm jWEAlgorithm : this.idTokenJWEAlgs) {
                stringList.add(jWEAlgorithm.getName());
            }
            o.put((Object)"id_token_encryption_alg_values_supported", stringList);
        }
        if (this.idTokenJWEEncs != null) {
            stringList = new ArrayList(this.idTokenJWEEncs.size());
            for (EncryptionMethod encryptionMethod : this.idTokenJWEEncs) {
                stringList.add(encryptionMethod.getName());
            }
            o.put((Object)"id_token_encryption_enc_values_supported", stringList);
        }
        if (this.userInfoJWSAlgs != null) {
            stringList = new ArrayList(this.userInfoJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.userInfoJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"userinfo_signing_alg_values_supported", stringList);
        }
        if (this.userInfoJWEAlgs != null) {
            stringList = new ArrayList(this.userInfoJWEAlgs.size());
            for (JWEAlgorithm jWEAlgorithm : this.userInfoJWEAlgs) {
                stringList.add(jWEAlgorithm.getName());
            }
            o.put((Object)"userinfo_encryption_alg_values_supported", stringList);
        }
        if (this.userInfoJWEEncs != null) {
            stringList = new ArrayList(this.userInfoJWEEncs.size());
            for (EncryptionMethod encryptionMethod : this.userInfoJWEEncs) {
                stringList.add(encryptionMethod.getName());
            }
            o.put((Object)"userinfo_encryption_enc_values_supported", stringList);
        }
        if (this.displays != null) {
            stringList = new ArrayList(this.displays.size());
            for (Display display : this.displays) {
                stringList.add(display.toString());
            }
            o.put((Object)"display_values_supported", stringList);
        }
        if (this.claimTypes != null) {
            stringList = new ArrayList(this.claimTypes.size());
            for (ClaimType claimType : this.claimTypes) {
                stringList.add(claimType.toString());
            }
            o.put((Object)"claim_types_supported", stringList);
        }
        if (this.claims != null) {
            o.put((Object)"claims_supported", this.claims);
        }
        if (this.claimsLocales != null) {
            stringList = new ArrayList(this.claimsLocales.size());
            for (LangTag langTag : this.claimsLocales) {
                stringList.add(langTag.toString());
            }
            o.put((Object)"claims_locales_supported", stringList);
        }
        if (this.claimsParamSupported) {
            o.put((Object)"claims_parameter_supported", (Object)true);
        }
        o.put((Object)"request_uri_parameter_supported", (Object)this.supportsRequestURIParam());
        if (this.frontChannelLogoutSupported) {
            o.put((Object)"frontchannel_logout_supported", (Object)true);
        }
        if (this.frontChannelLogoutSupported) {
            o.put((Object)"frontchannel_logout_session_supported", (Object)this.frontChannelLogoutSessionSupported);
        }
        if (this.backChannelLogoutSupported) {
            o.put((Object)"backchannel_logout_supported", (Object)true);
        }
        if (this.backChannelLogoutSupported) {
            o.put((Object)"backchannel_logout_session_supported", (Object)this.backChannelLogoutSessionSupported);
        }
        if (this.verifiedClaimsSupported) {
            o.put((Object)"verified_claims_supported", (Object)true);
            if (this.trustFrameworks != null) {
                o.put((Object)"trust_frameworks_supported", Identifier.toStringList(this.trustFrameworks));
            }
            if (this.evidenceTypes != null) {
                o.put((Object)"evidence_supported", Identifier.toStringList(this.evidenceTypes));
            }
            if ((CollectionUtils.contains(this.evidenceTypes, IdentityEvidenceType.DOCUMENT) || CollectionUtils.contains(this.evidenceTypes, IdentityEvidenceType.ID_DOCUMENT)) && this.documentTypes != null) {
                o.put((Object)"documents_supported", Identifier.toStringList(this.documentTypes));
                if (this.documentMethods != null) {
                    o.put((Object)"documents_methods_supported", Identifier.toStringList(this.documentMethods));
                }
                if (this.documentValidationMethods != null) {
                    o.put((Object)"documents_validation_methods_supported", Identifier.toStringList(this.documentValidationMethods));
                }
                if (this.documentVerificationMethods != null) {
                    o.put((Object)"documents_verification_methods_supported", Identifier.toStringList(this.documentVerificationMethods));
                }
            }
            if (this.idDocumentTypes != null) {
                o.put((Object)"id_documents_supported", Identifier.toStringList(this.idDocumentTypes));
            }
            if (this.idVerificationMethods != null) {
                o.put((Object)"id_documents_verification_methods_supported", Identifier.toStringList(this.idVerificationMethods));
            }
            if (this.electronicRecordTypes != null) {
                o.put((Object)"electronic_records_supported", Identifier.toStringList(this.electronicRecordTypes));
            }
            if (this.verifiedClaims != null) {
                o.put((Object)"claims_in_verified_claims_supported", this.verifiedClaims);
            }
            if (this.attachmentTypes != null) {
                LinkedList<String> strings = new LinkedList<String>();
                for (AttachmentType type : this.attachmentTypes) {
                    strings.add(type.toString());
                }
                o.put((Object)"attachments_supported", strings);
                if (this.attachmentTypes.contains((Object)AttachmentType.EXTERNAL) && this.attachmentDigestAlgs != null) {
                    o.put((Object)"digest_algorithms_supported", Identifier.toStringList(this.attachmentDigestAlgs));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(this.clientRegistrationTypes)) {
            o.put((Object)"client_registration_types_supported", Identifier.toStringList(this.clientRegistrationTypes));
        }
        if (MapUtils.isNotEmpty(this.clientRegistrationAuthMethods)) {
            JSONObject map = new JSONObject();
            for (Map.Entry<EndpointName, List<ClientAuthenticationMethod>> en : this.getClientRegistrationAuthnMethods().entrySet()) {
                LinkedList<String> methodNames = new LinkedList<String>();
                for (ClientAuthenticationMethod method : en.getValue()) {
                    methodNames.add(method.getValue());
                }
                map.put((Object)en.getKey().getValue(), methodNames);
            }
            o.put((Object)"client_registration_authn_methods_supported", (Object)map);
        }
        if (this.organizationName != null) {
            o.put((Object)"organization_name", (Object)this.organizationName);
        }
        if (this.federationRegistrationEndpoint != null) {
            o.put((Object)"federation_registration_endpoint", (Object)this.federationRegistrationEndpoint.toString());
        }
        return o;
    }

    public static OIDCProviderMetadata parse(JSONObject jsonObject) throws ParseException {
        AuthorizationServerMetadata as = AuthorizationServerMetadata.parse(jsonObject);
        ArrayList<SubjectType> subjectTypes = new ArrayList<SubjectType>();
        for (String v : JSONObjectUtils.getStringArray(jsonObject, "subject_types_supported")) {
            subjectTypes.add(SubjectType.parse(v));
        }
        OIDCProviderMetadata op = new OIDCProviderMetadata(as.getIssuer(), Collections.unmodifiableList(subjectTypes), as.getJWKSetURI());
        op.setAuthorizationEndpointURI(as.getAuthorizationEndpointURI());
        op.setTokenEndpointURI(as.getTokenEndpointURI());
        op.setRegistrationEndpointURI(as.getRegistrationEndpointURI());
        op.setIntrospectionEndpointURI(as.getIntrospectionEndpointURI());
        op.setRevocationEndpointURI(as.getRevocationEndpointURI());
        op.setRequestObjectEndpoint(as.getRequestObjectEndpoint());
        op.setPushedAuthorizationRequestEndpointURI(as.getPushedAuthorizationRequestEndpointURI());
        op.setDeviceAuthorizationEndpointURI(as.getDeviceAuthorizationEndpointURI());
        op.userInfoEndpoint = JSONObjectUtils.getURI(jsonObject, "userinfo_endpoint", null);
        op.checkSessionIframe = JSONObjectUtils.getURI(jsonObject, "check_session_iframe", null);
        op.endSessionEndpoint = JSONObjectUtils.getURI(jsonObject, "end_session_endpoint", null);
        op.setScopes(as.getScopes());
        op.setResponseTypes(as.getResponseTypes());
        op.setResponseModes(as.getResponseModes());
        op.setGrantTypes(as.getGrantTypes());
        op.setTokenEndpointAuthMethods(as.getTokenEndpointAuthMethods());
        op.setTokenEndpointJWSAlgs(as.getTokenEndpointJWSAlgs());
        op.setIntrospectionEndpointAuthMethods(as.getIntrospectionEndpointAuthMethods());
        op.setIntrospectionEndpointJWSAlgs(as.getIntrospectionEndpointJWSAlgs());
        op.setRevocationEndpointAuthMethods(as.getRevocationEndpointAuthMethods());
        op.setRevocationEndpointJWSAlgs(as.getRevocationEndpointJWSAlgs());
        op.setRequestObjectJWSAlgs(as.getRequestObjectJWSAlgs());
        op.setRequestObjectJWEAlgs(as.getRequestObjectJWEAlgs());
        op.setRequestObjectJWEEncs(as.getRequestObjectJWEEncs());
        op.setSupportsRequestParam(as.supportsRequestParam());
        op.setSupportsRequestURIParam(as.supportsRequestURIParam());
        op.setRequiresRequestURIRegistration(as.requiresRequestURIRegistration());
        op.requiresPushedAuthorizationRequests(as.requiresPushedAuthorizationRequests());
        op.setSupportsAuthorizationResponseIssuerParam(as.supportsAuthorizationResponseIssuerParam());
        op.setCodeChallengeMethods(as.getCodeChallengeMethods());
        op.setBackChannelAuthenticationEndpointURI(as.getBackChannelAuthenticationEndpointURI());
        op.setBackChannelAuthenticationRequestJWSAlgs(as.getBackChannelAuthenticationRequestJWSAlgs());
        op.setSupportsBackChannelUserCodeParam(as.supportsBackChannelUserCodeParam());
        op.setBackChannelTokenDeliveryModes(as.getBackChannelTokenDeliveryModes());
        op.setPromptTypes(as.getPromptTypes());
        if (jsonObject.get((Object)"acr_values_supported") != null) {
            op.acrValues = new ArrayList<ACR>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "acr_values_supported")) {
                if (v == null) continue;
                op.acrValues.add(new ACR(v));
            }
        }
        if (jsonObject.get((Object)"id_token_signing_alg_values_supported") != null) {
            op.idTokenJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "id_token_signing_alg_values_supported")) {
                if (v == null) continue;
                op.idTokenJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"id_token_encryption_alg_values_supported") != null) {
            op.idTokenJWEAlgs = new ArrayList<JWEAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "id_token_encryption_alg_values_supported")) {
                if (v == null) continue;
                op.idTokenJWEAlgs.add(JWEAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"id_token_encryption_enc_values_supported") != null) {
            op.idTokenJWEEncs = new ArrayList<EncryptionMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "id_token_encryption_enc_values_supported")) {
                if (v == null) continue;
                op.idTokenJWEEncs.add(EncryptionMethod.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"userinfo_signing_alg_values_supported") != null) {
            op.userInfoJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "userinfo_signing_alg_values_supported")) {
                if (v == null) continue;
                op.userInfoJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"userinfo_encryption_alg_values_supported") != null) {
            op.userInfoJWEAlgs = new ArrayList<JWEAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "userinfo_encryption_alg_values_supported")) {
                if (v == null) continue;
                op.userInfoJWEAlgs.add(JWEAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"userinfo_encryption_enc_values_supported") != null) {
            op.userInfoJWEEncs = new ArrayList<EncryptionMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "userinfo_encryption_enc_values_supported")) {
                if (v == null) continue;
                op.userInfoJWEEncs.add(EncryptionMethod.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"display_values_supported") != null) {
            op.displays = new ArrayList<Display>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "display_values_supported")) {
                if (v == null) continue;
                op.displays.add(Display.parse(v));
            }
        }
        if (jsonObject.get((Object)"claim_types_supported") != null) {
            op.claimTypes = new ArrayList<ClaimType>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "claim_types_supported")) {
                if (v == null) continue;
                op.claimTypes.add(ClaimType.parse(v));
            }
        }
        if (jsonObject.get((Object)"claims_supported") != null) {
            op.claims = new ArrayList<String>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "claims_supported")) {
                if (v == null) continue;
                op.claims.add(v);
            }
        }
        if (jsonObject.get((Object)"claims_locales_supported") != null) {
            op.claimsLocales = new ArrayList<LangTag>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "claims_locales_supported")) {
                if (v == null) continue;
                try {
                    op.claimsLocales.add(LangTag.parse((String)v));
                }
                catch (LangTagException e) {
                    throw new ParseException("Invalid claims_locales_supported field: " + e.getMessage(), e);
                }
            }
        }
        op.setUILocales(as.getUILocales());
        op.setServiceDocsURI(as.getServiceDocsURI());
        op.setPolicyURI(as.getPolicyURI());
        op.setTermsOfServiceURI(as.getTermsOfServiceURI());
        if (jsonObject.get((Object)"claims_parameter_supported") != null) {
            op.claimsParamSupported = JSONObjectUtils.getBoolean(jsonObject, "claims_parameter_supported");
        }
        if (jsonObject.get((Object)"request_uri_parameter_supported") == null) {
            op.setSupportsRequestURIParam(true);
        }
        if (jsonObject.get((Object)"frontchannel_logout_supported") != null) {
            op.frontChannelLogoutSupported = JSONObjectUtils.getBoolean(jsonObject, "frontchannel_logout_supported");
        }
        if (op.frontChannelLogoutSupported && jsonObject.get((Object)"frontchannel_logout_session_supported") != null) {
            op.frontChannelLogoutSessionSupported = JSONObjectUtils.getBoolean(jsonObject, "frontchannel_logout_session_supported");
        }
        if (jsonObject.get((Object)"backchannel_logout_supported") != null) {
            op.backChannelLogoutSupported = JSONObjectUtils.getBoolean(jsonObject, "backchannel_logout_supported");
        }
        if (op.frontChannelLogoutSupported && jsonObject.get((Object)"backchannel_logout_session_supported") != null) {
            op.backChannelLogoutSessionSupported = JSONObjectUtils.getBoolean(jsonObject, "backchannel_logout_session_supported");
        }
        if (jsonObject.get((Object)"mtls_endpoint_aliases") != null) {
            op.setMtlsEndpointAliases(OIDCProviderEndpointMetadata.parse(JSONObjectUtils.getJSONObject(jsonObject, "mtls_endpoint_aliases")));
        }
        op.setSupportsTLSClientCertificateBoundAccessTokens(as.supportsTLSClientCertificateBoundAccessTokens());
        op.setDPoPJWSAlgs(as.getDPoPJWSAlgs());
        op.setAuthorizationJWSAlgs(as.getAuthorizationJWSAlgs());
        op.setAuthorizationJWEAlgs(as.getAuthorizationJWEAlgs());
        op.setAuthorizationJWEEncs(as.getAuthorizationJWEEncs());
        op.setIncrementalAuthorizationTypes(as.getIncrementalAuthorizationTypes());
        if (jsonObject.get((Object)"verified_claims_supported") != null) {
            op.verifiedClaimsSupported = JSONObjectUtils.getBoolean(jsonObject, "verified_claims_supported");
            if (op.verifiedClaimsSupported) {
                if (jsonObject.get((Object)"trust_frameworks_supported") != null) {
                    op.trustFrameworks = new LinkedList<IdentityTrustFramework>();
                    for (String v : JSONObjectUtils.getStringList(jsonObject, "trust_frameworks_supported")) {
                        op.trustFrameworks.add(new IdentityTrustFramework(v));
                    }
                }
                if (jsonObject.get((Object)"evidence_supported") != null) {
                    op.evidenceTypes = new LinkedList<IdentityEvidenceType>();
                    for (String v : JSONObjectUtils.getStringList(jsonObject, "evidence_supported")) {
                        op.evidenceTypes.add(new IdentityEvidenceType(v));
                    }
                }
                if ((CollectionUtils.contains(op.evidenceTypes, IdentityEvidenceType.DOCUMENT) || CollectionUtils.contains(op.evidenceTypes, IdentityEvidenceType.ID_DOCUMENT)) && jsonObject.get((Object)"documents_supported") != null) {
                    op.documentTypes = new LinkedList<DocumentType>();
                    for (String v : JSONObjectUtils.getStringList(jsonObject, "documents_supported")) {
                        op.documentTypes.add(new DocumentType(v));
                    }
                    if (jsonObject.get((Object)"documents_methods_supported") != null) {
                        op.documentMethods = new LinkedList<IdentityVerificationMethod>();
                        for (String v : JSONObjectUtils.getStringList(jsonObject, "documents_methods_supported")) {
                            op.documentMethods.add(new IdentityVerificationMethod(v));
                        }
                    }
                    if (jsonObject.get((Object)"documents_validation_methods_supported") != null) {
                        op.documentValidationMethods = new LinkedList<ValidationMethodType>();
                        for (String v : JSONObjectUtils.getStringList(jsonObject, "documents_validation_methods_supported")) {
                            op.documentValidationMethods.add(new ValidationMethodType(v));
                        }
                    }
                    if (jsonObject.get((Object)"documents_verification_methods_supported") != null) {
                        op.documentVerificationMethods = new LinkedList<VerificationMethodType>();
                        for (String v : JSONObjectUtils.getStringList(jsonObject, "documents_verification_methods_supported")) {
                            op.documentVerificationMethods.add(new VerificationMethodType(v));
                        }
                    }
                }
                if (jsonObject.get((Object)"id_documents_supported") != null) {
                    op.idDocumentTypes = new LinkedList<IDDocumentType>();
                    for (String v : JSONObjectUtils.getStringList(jsonObject, "id_documents_supported")) {
                        op.idDocumentTypes.add(new IDDocumentType(v));
                    }
                }
                if (jsonObject.get((Object)"id_documents_verification_methods_supported") != null) {
                    op.idVerificationMethods = new LinkedList<IdentityVerificationMethod>();
                    for (String v : JSONObjectUtils.getStringList(jsonObject, "id_documents_verification_methods_supported")) {
                        op.idVerificationMethods.add(new IdentityVerificationMethod(v));
                    }
                }
                if (jsonObject.get((Object)"electronic_records_supported") != null) {
                    op.electronicRecordTypes = new LinkedList<ElectronicRecordType>();
                    for (String v : JSONObjectUtils.getStringList(jsonObject, "electronic_records_supported")) {
                        op.electronicRecordTypes.add(new ElectronicRecordType(v));
                    }
                }
                if (jsonObject.get((Object)"claims_in_verified_claims_supported") != null) {
                    op.verifiedClaims = JSONObjectUtils.getStringList(jsonObject, "claims_in_verified_claims_supported");
                }
                if (jsonObject.get((Object)"attachments_supported") != null) {
                    op.attachmentTypes = new LinkedList<AttachmentType>();
                    for (String v : JSONObjectUtils.getStringList(jsonObject, "attachments_supported")) {
                        op.attachmentTypes.add(AttachmentType.parse(v));
                    }
                    if (op.attachmentTypes.contains((Object)AttachmentType.EXTERNAL) && jsonObject.get((Object)"digest_algorithms_supported") != null) {
                        op.attachmentDigestAlgs = new LinkedList<HashAlgorithm>();
                        for (String v : JSONObjectUtils.getStringList(jsonObject, "digest_algorithms_supported")) {
                            op.attachmentDigestAlgs.add(new HashAlgorithm(v));
                        }
                    }
                }
            }
        }
        if (jsonObject.get((Object)"client_registration_types_supported") != null) {
            op.clientRegistrationTypes = new LinkedList<ClientRegistrationType>();
            for (String v : JSONObjectUtils.getStringList(jsonObject, "client_registration_types_supported")) {
                op.clientRegistrationTypes.add(new ClientRegistrationType(v));
            }
        }
        if (jsonObject.get((Object)"client_registration_authn_methods_supported") != null) {
            HashMap<EndpointName, List<ClientAuthenticationMethod>> fedClientAuthMethods = new HashMap<EndpointName, List<ClientAuthenticationMethod>>();
            JSONObject spec = JSONObjectUtils.getJSONObject(jsonObject, "client_registration_authn_methods_supported");
            for (String endpointName : spec.keySet()) {
                List<String> methodNames = JSONObjectUtils.getStringList(spec, endpointName, Collections.emptyList());
                LinkedList<ClientAuthenticationMethod> authMethods = new LinkedList<ClientAuthenticationMethod>();
                for (String name : methodNames) {
                    authMethods.add(ClientAuthenticationMethod.parse(name));
                }
                fedClientAuthMethods.put(new EndpointName(endpointName), authMethods);
            }
            op.setClientRegistrationAuthnMethods(fedClientAuthMethods);
        }
        op.organizationName = JSONObjectUtils.getString(jsonObject, "organization_name", null);
        op.federationRegistrationEndpoint = JSONObjectUtils.getURI(jsonObject, "federation_registration_endpoint", null);
        for (Map.Entry entry : as.getCustomParameters().entrySet()) {
            if (REGISTERED_PARAMETER_NAMES.contains(entry.getKey())) continue;
            op.setCustomParameter((String)entry.getKey(), entry.getValue());
        }
        return op;
    }

    public static OIDCProviderMetadata parse(String s) throws ParseException {
        return OIDCProviderMetadata.parse(JSONObjectUtils.parse(s));
    }

    public static URL resolveURL(Issuer issuer) throws GeneralException {
        try {
            URL issuerURL = new URL(issuer.getValue());
            if (issuerURL.getQuery() != null && !issuerURL.getQuery().trim().isEmpty()) {
                throw new GeneralException("The issuer identifier must not contain a query component");
            }
            if (issuerURL.getPath() != null && issuerURL.getPath().endsWith("/")) {
                return new URL(issuerURL + ".well-known/openid-configuration");
            }
            return new URL(issuerURL + "/.well-known/openid-configuration");
        }
        catch (MalformedURLException e) {
            throw new GeneralException("The issuer identifier doesn't represent a valid URL: " + e.getMessage(), e);
        }
    }

    public static OIDCProviderMetadata resolve(Issuer issuer) throws GeneralException, IOException {
        return OIDCProviderMetadata.resolve(issuer, 0, 0);
    }

    public static OIDCProviderMetadata resolve(Issuer issuer, int connectTimeout, int readTimeout) throws GeneralException, IOException {
        URL configURL = OIDCProviderMetadata.resolveURL(issuer);
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, configURL);
        httpRequest.setConnectTimeout(connectTimeout);
        httpRequest.setReadTimeout(readTimeout);
        HTTPResponse httpResponse = httpRequest.send();
        if (httpResponse.getStatusCode() != 200) {
            throw new IOException("Couldn't download OpenID Provider metadata from " + configURL + ": Status code " + httpResponse.getStatusCode());
        }
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        OIDCProviderMetadata op = OIDCProviderMetadata.parse(jsonObject);
        if (!issuer.equals(op.getIssuer())) {
            throw new GeneralException("The returned issuer doesn't match the expected: " + op.getIssuer());
        }
        return op;
    }

    static {
        HashSet<String> p = new HashSet<String>(AuthorizationServerMetadata.getRegisteredParameterNames());
        p.addAll(OIDCProviderEndpointMetadata.getRegisteredParameterNames());
        p.add("acr_values_supported");
        p.add("subject_types_supported");
        p.add("id_token_signing_alg_values_supported");
        p.add("id_token_encryption_alg_values_supported");
        p.add("id_token_encryption_enc_values_supported");
        p.add("userinfo_signing_alg_values_supported");
        p.add("userinfo_encryption_alg_values_supported");
        p.add("userinfo_encryption_enc_values_supported");
        p.add("display_values_supported");
        p.add("claim_types_supported");
        p.add("claims_supported");
        p.add("claims_locales_supported");
        p.add("claims_parameter_supported");
        p.add("backchannel_logout_supported");
        p.add("backchannel_logout_session_supported");
        p.add("frontchannel_logout_supported");
        p.add("frontchannel_logout_session_supported");
        p.add("verified_claims_supported");
        p.add("trust_frameworks_supported");
        p.add("evidence_supported");
        p.add("documents_supported");
        p.add("documents_methods_supported");
        p.add("documents_validation_methods_supported");
        p.add("documents_verification_methods_supported");
        p.add("id_documents_supported");
        p.add("id_documents_verification_methods_supported");
        p.add("electronic_records_supported");
        p.add("claims_in_verified_claims_supported");
        p.add("attachments_supported");
        p.add("digest_algorithms_supported");
        p.add("client_registration_types_supported");
        p.add("client_registration_authn_methods_supported");
        p.add("organization_name");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

