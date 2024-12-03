/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.rp;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientMetadata;
import com.nimbusds.oauth2.sdk.client.RegistrationError;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import com.nimbusds.openid.connect.sdk.id.SectorID;
import com.nimbusds.openid.connect.sdk.rp.ApplicationType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class OIDCClientMetadata
extends ClientMetadata {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private ApplicationType applicationType;
    private SubjectType subjectType;
    private URI sectorIDURI;
    private JWSAlgorithm idTokenJWSAlg;
    private JWEAlgorithm idTokenJWEAlg;
    private EncryptionMethod idTokenJWEEnc;
    private JWSAlgorithm userInfoJWSAlg;
    private JWEAlgorithm userInfoJWEAlg;
    private EncryptionMethod userInfoJWEEnc;
    private int defaultMaxAge = -1;
    private boolean requiresAuthTime;
    private List<ACR> defaultACRs;
    private URI initiateLoginURI;
    private Set<URI> postLogoutRedirectURIs;
    private URI frontChannelLogoutURI;
    private boolean frontChannelLogoutSessionRequired = false;
    private URI backChannelLogoutURI;
    private boolean backChannelLogoutSessionRequired = false;

    public OIDCClientMetadata() {
    }

    public OIDCClientMetadata(ClientMetadata metadata) {
        super(metadata);
    }

    public OIDCClientMetadata(OIDCClientMetadata metadata) {
        super(metadata);
        this.applicationType = metadata.getApplicationType();
        this.subjectType = metadata.getSubjectType();
        this.sectorIDURI = metadata.getSectorIDURI();
        this.idTokenJWSAlg = metadata.getIDTokenJWSAlg();
        this.idTokenJWEAlg = metadata.getIDTokenJWEAlg();
        this.idTokenJWEEnc = metadata.getIDTokenJWEEnc();
        this.userInfoJWSAlg = metadata.getUserInfoJWSAlg();
        this.userInfoJWEAlg = metadata.getUserInfoJWEAlg();
        this.userInfoJWEEnc = metadata.getUserInfoJWEEnc();
        this.defaultMaxAge = metadata.getDefaultMaxAge();
        this.requiresAuthTime = metadata.requiresAuthTime();
        this.defaultACRs = metadata.getDefaultACRs();
        this.initiateLoginURI = metadata.getInitiateLoginURI();
        this.postLogoutRedirectURIs = metadata.getPostLogoutRedirectionURIs();
        this.frontChannelLogoutURI = metadata.getFrontChannelLogoutURI();
        this.frontChannelLogoutSessionRequired = metadata.requiresFrontChannelLogoutSession();
        this.backChannelLogoutURI = metadata.getBackChannelLogoutURI();
        this.backChannelLogoutSessionRequired = metadata.requiresBackChannelLogoutSession();
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public ApplicationType getApplicationType() {
        return this.applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public SubjectType getSubjectType() {
        return this.subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public URI getSectorIDURI() {
        return this.sectorIDURI;
    }

    public void setSectorIDURI(URI sectorIDURI) {
        if (sectorIDURI != null) {
            SectorID.ensureHTTPScheme(sectorIDURI);
            SectorID.ensureHostComponent(sectorIDURI);
        }
        this.sectorIDURI = sectorIDURI;
    }

    public SectorID resolveSectorID() {
        if (!SubjectType.PAIRWISE.equals((Object)this.getSubjectType())) {
            return null;
        }
        if (this.getSectorIDURI() != null) {
            return new SectorID(this.getSectorIDURI());
        }
        if (CollectionUtils.isEmpty(this.getRedirectionURIs())) {
            throw new IllegalStateException("Couldn't resolve sector ID: Missing redirect_uris");
        }
        if (this.getRedirectionURIs().size() > 1) {
            throw new IllegalStateException("Couldn't resolve sector ID: More than one redirect_uri, sector_identifier_uri not specified");
        }
        return new SectorID(this.getRedirectionURIs().iterator().next());
    }

    public JWSAlgorithm getIDTokenJWSAlg() {
        return this.idTokenJWSAlg;
    }

    public void setIDTokenJWSAlg(JWSAlgorithm idTokenJWSAlg) {
        this.idTokenJWSAlg = idTokenJWSAlg;
    }

    public JWEAlgorithm getIDTokenJWEAlg() {
        return this.idTokenJWEAlg;
    }

    public void setIDTokenJWEAlg(JWEAlgorithm idTokenJWEAlg) {
        this.idTokenJWEAlg = idTokenJWEAlg;
    }

    public EncryptionMethod getIDTokenJWEEnc() {
        return this.idTokenJWEEnc;
    }

    public void setIDTokenJWEEnc(EncryptionMethod idTokenJWEEnc) {
        this.idTokenJWEEnc = idTokenJWEEnc;
    }

    public JWSAlgorithm getUserInfoJWSAlg() {
        return this.userInfoJWSAlg;
    }

    public void setUserInfoJWSAlg(JWSAlgorithm userInfoJWSAlg) {
        this.userInfoJWSAlg = userInfoJWSAlg;
    }

    public JWEAlgorithm getUserInfoJWEAlg() {
        return this.userInfoJWEAlg;
    }

    public void setUserInfoJWEAlg(JWEAlgorithm userInfoJWEAlg) {
        this.userInfoJWEAlg = userInfoJWEAlg;
    }

    public EncryptionMethod getUserInfoJWEEnc() {
        return this.userInfoJWEEnc;
    }

    public void setUserInfoJWEEnc(EncryptionMethod userInfoJWEEnc) {
        this.userInfoJWEEnc = userInfoJWEEnc;
    }

    public int getDefaultMaxAge() {
        return this.defaultMaxAge;
    }

    public void setDefaultMaxAge(int defaultMaxAge) {
        this.defaultMaxAge = defaultMaxAge;
    }

    public boolean requiresAuthTime() {
        return this.requiresAuthTime;
    }

    public void requiresAuthTime(boolean requiresAuthTime) {
        this.requiresAuthTime = requiresAuthTime;
    }

    public List<ACR> getDefaultACRs() {
        return this.defaultACRs;
    }

    public void setDefaultACRs(List<ACR> defaultACRs) {
        this.defaultACRs = defaultACRs;
    }

    public URI getInitiateLoginURI() {
        return this.initiateLoginURI;
    }

    public void setInitiateLoginURI(URI loginURI) {
        URIUtils.ensureSchemeIsHTTPS(loginURI);
        this.initiateLoginURI = loginURI;
    }

    public Set<URI> getPostLogoutRedirectionURIs() {
        return this.postLogoutRedirectURIs;
    }

    public void setPostLogoutRedirectionURIs(Set<URI> logoutURIs) {
        if (logoutURIs != null) {
            for (URI uri : logoutURIs) {
                URIUtils.ensureSchemeIsNotProhibited(uri, PROHIBITED_REDIRECT_URI_SCHEMES);
            }
        }
        this.postLogoutRedirectURIs = logoutURIs;
    }

    public URI getFrontChannelLogoutURI() {
        return this.frontChannelLogoutURI;
    }

    public void setFrontChannelLogoutURI(URI frontChannelLogoutURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(frontChannelLogoutURI);
        this.frontChannelLogoutURI = frontChannelLogoutURI;
    }

    public boolean requiresFrontChannelLogoutSession() {
        return this.frontChannelLogoutSessionRequired;
    }

    public void requiresFrontChannelLogoutSession(boolean requiresSession) {
        this.frontChannelLogoutSessionRequired = requiresSession;
    }

    public URI getBackChannelLogoutURI() {
        return this.backChannelLogoutURI;
    }

    public void setBackChannelLogoutURI(URI backChannelLogoutURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(backChannelLogoutURI);
        this.backChannelLogoutURI = backChannelLogoutURI;
    }

    public boolean requiresBackChannelLogoutSession() {
        return this.backChannelLogoutSessionRequired;
    }

    public void requiresBackChannelLogoutSession(boolean requiresSession) {
        this.backChannelLogoutSessionRequired = requiresSession;
    }

    @Override
    public void applyDefaults() {
        super.applyDefaults();
        if (this.applicationType == null) {
            this.applicationType = ApplicationType.WEB;
        }
        if (this.idTokenJWSAlg == null) {
            this.idTokenJWSAlg = JWSAlgorithm.RS256;
        }
    }

    @Override
    public JSONObject toJSONObject(boolean includeCustomFields) {
        JSONObject o = super.toJSONObject(includeCustomFields);
        o.putAll(this.getCustomFields());
        if (this.applicationType != null) {
            o.put("application_type", this.applicationType.toString());
        }
        if (this.subjectType != null) {
            o.put("subject_type", this.subjectType.toString());
        }
        if (this.sectorIDURI != null) {
            o.put("sector_identifier_uri", this.sectorIDURI.toString());
        }
        if (this.idTokenJWSAlg != null) {
            o.put("id_token_signed_response_alg", this.idTokenJWSAlg.getName());
        }
        if (this.idTokenJWEAlg != null) {
            o.put("id_token_encrypted_response_alg", this.idTokenJWEAlg.getName());
        }
        if (this.idTokenJWEEnc != null) {
            o.put("id_token_encrypted_response_enc", this.idTokenJWEEnc.getName());
        }
        if (this.userInfoJWSAlg != null) {
            o.put("userinfo_signed_response_alg", this.userInfoJWSAlg.getName());
        }
        if (this.userInfoJWEAlg != null) {
            o.put("userinfo_encrypted_response_alg", this.userInfoJWEAlg.getName());
        }
        if (this.userInfoJWEEnc != null) {
            o.put("userinfo_encrypted_response_enc", this.userInfoJWEEnc.getName());
        }
        if (this.defaultMaxAge > 0) {
            o.put("default_max_age", this.defaultMaxAge);
        }
        if (this.requiresAuthTime()) {
            o.put("require_auth_time", this.requiresAuthTime);
        }
        if (this.defaultACRs != null) {
            JSONArray acrList = new JSONArray();
            acrList.addAll(this.defaultACRs);
            o.put("default_acr_values", acrList);
        }
        if (this.initiateLoginURI != null) {
            o.put("initiate_login_uri", this.initiateLoginURI.toString());
        }
        if (this.postLogoutRedirectURIs != null) {
            JSONArray uriList = new JSONArray();
            for (URI uri : this.postLogoutRedirectURIs) {
                uriList.add(uri.toString());
            }
            o.put("post_logout_redirect_uris", uriList);
        }
        if (this.frontChannelLogoutURI != null) {
            o.put("frontchannel_logout_uri", this.frontChannelLogoutURI.toString());
            o.put("frontchannel_logout_session_required", this.frontChannelLogoutSessionRequired);
        }
        if (this.backChannelLogoutURI != null) {
            o.put("backchannel_logout_uri", this.backChannelLogoutURI.toString());
            o.put("backchannel_logout_session_required", this.backChannelLogoutSessionRequired);
        }
        return o;
    }

    public static OIDCClientMetadata parse(JSONObject jsonObject) throws ParseException {
        JSONObject oidcFields;
        OIDCClientMetadata metadata;
        block31: {
            ClientMetadata baseMetadata = ClientMetadata.parse(jsonObject);
            metadata = new OIDCClientMetadata(baseMetadata);
            oidcFields = baseMetadata.getCustomFields();
            try {
                if (jsonObject.get("application_type") != null) {
                    metadata.setApplicationType(JSONObjectUtils.getEnum(jsonObject, "application_type", ApplicationType.class));
                    oidcFields.remove("application_type");
                }
                if (jsonObject.get("subject_type") != null) {
                    metadata.setSubjectType(JSONObjectUtils.getEnum(jsonObject, "subject_type", SubjectType.class));
                    oidcFields.remove("subject_type");
                }
                if (jsonObject.get("sector_identifier_uri") != null) {
                    metadata.setSectorIDURI(JSONObjectUtils.getURI(jsonObject, "sector_identifier_uri"));
                    oidcFields.remove("sector_identifier_uri");
                }
                if (jsonObject.get("id_token_signed_response_alg") != null) {
                    metadata.setIDTokenJWSAlg(JWSAlgorithm.parse(JSONObjectUtils.getString(jsonObject, "id_token_signed_response_alg")));
                    oidcFields.remove("id_token_signed_response_alg");
                }
                if (jsonObject.get("id_token_encrypted_response_alg") != null) {
                    metadata.setIDTokenJWEAlg(JWEAlgorithm.parse(JSONObjectUtils.getString(jsonObject, "id_token_encrypted_response_alg")));
                    oidcFields.remove("id_token_encrypted_response_alg");
                }
                if (jsonObject.get("id_token_encrypted_response_enc") != null) {
                    metadata.setIDTokenJWEEnc(EncryptionMethod.parse(JSONObjectUtils.getString(jsonObject, "id_token_encrypted_response_enc")));
                    oidcFields.remove("id_token_encrypted_response_enc");
                }
                if (jsonObject.get("userinfo_signed_response_alg") != null) {
                    metadata.setUserInfoJWSAlg(JWSAlgorithm.parse(JSONObjectUtils.getString(jsonObject, "userinfo_signed_response_alg")));
                    oidcFields.remove("userinfo_signed_response_alg");
                }
                if (jsonObject.get("userinfo_encrypted_response_alg") != null) {
                    metadata.setUserInfoJWEAlg(JWEAlgorithm.parse(JSONObjectUtils.getString(jsonObject, "userinfo_encrypted_response_alg")));
                    oidcFields.remove("userinfo_encrypted_response_alg");
                }
                if (jsonObject.get("userinfo_encrypted_response_enc") != null) {
                    metadata.setUserInfoJWEEnc(EncryptionMethod.parse(JSONObjectUtils.getString(jsonObject, "userinfo_encrypted_response_enc")));
                    oidcFields.remove("userinfo_encrypted_response_enc");
                }
                if (jsonObject.get("default_max_age") != null) {
                    metadata.setDefaultMaxAge(JSONObjectUtils.getInt(jsonObject, "default_max_age"));
                    oidcFields.remove("default_max_age");
                }
                if (jsonObject.get("require_auth_time") != null) {
                    metadata.requiresAuthTime(JSONObjectUtils.getBoolean(jsonObject, "require_auth_time"));
                    oidcFields.remove("require_auth_time");
                }
                if (jsonObject.get("default_acr_values") != null) {
                    LinkedList<ACR> acrValues = new LinkedList<ACR>();
                    for (String acrString : JSONObjectUtils.getStringArray(jsonObject, "default_acr_values")) {
                        acrValues.add(new ACR(acrString));
                    }
                    metadata.setDefaultACRs(acrValues);
                    oidcFields.remove("default_acr_values");
                }
                if (jsonObject.get("initiate_login_uri") != null) {
                    try {
                        metadata.setInitiateLoginURI(JSONObjectUtils.getURI(jsonObject, "initiate_login_uri"));
                    }
                    catch (IllegalArgumentException e) {
                        throw new ParseException("Invalid initiate_login_uri parameter: " + e.getMessage());
                    }
                    oidcFields.remove("initiate_login_uri");
                }
                if (jsonObject.get("post_logout_redirect_uris") != null) {
                    LinkedHashSet<URI> logoutURIs = new LinkedHashSet<URI>();
                    for (String uriString : JSONObjectUtils.getStringArray(jsonObject, "post_logout_redirect_uris")) {
                        try {
                            logoutURIs.add(new URI(uriString));
                        }
                        catch (URISyntaxException e) {
                            throw new ParseException("Invalid post_logout_redirect_uris parameter");
                        }
                    }
                    try {
                        metadata.setPostLogoutRedirectionURIs(logoutURIs);
                    }
                    catch (IllegalArgumentException e) {
                        throw new ParseException("Invalid post_logout_redirect_uris parameter: " + e.getMessage());
                    }
                    oidcFields.remove("post_logout_redirect_uris");
                }
                if (jsonObject.get("frontchannel_logout_uri") != null) {
                    try {
                        metadata.setFrontChannelLogoutURI(JSONObjectUtils.getURI(jsonObject, "frontchannel_logout_uri"));
                    }
                    catch (IllegalArgumentException e) {
                        throw new ParseException("Invalid frontchannel_logout_uri parameter: " + e.getMessage());
                    }
                    oidcFields.remove("frontchannel_logout_uri");
                    if (jsonObject.get("frontchannel_logout_session_required") != null) {
                        metadata.requiresFrontChannelLogoutSession(JSONObjectUtils.getBoolean(jsonObject, "frontchannel_logout_session_required"));
                        oidcFields.remove("frontchannel_logout_session_required");
                    }
                }
                if (jsonObject.get("backchannel_logout_uri") == null) break block31;
                try {
                    metadata.setBackChannelLogoutURI(JSONObjectUtils.getURI(jsonObject, "backchannel_logout_uri"));
                }
                catch (IllegalArgumentException e) {
                    throw new ParseException("Invalid backchannel_logout_uri parameter: " + e.getMessage());
                }
                oidcFields.remove("backchannel_logout_uri");
                if (jsonObject.get("backchannel_logout_session_required") != null) {
                    metadata.requiresBackChannelLogoutSession(JSONObjectUtils.getBoolean(jsonObject, "backchannel_logout_session_required"));
                    oidcFields.remove("backchannel_logout_session_required");
                }
            }
            catch (ParseException e) {
                throw new ParseException(e.getMessage(), RegistrationError.INVALID_CLIENT_METADATA.appendDescription(": " + e.getMessage()), e.getCause());
            }
        }
        metadata.setCustomFields(oidcFields);
        return metadata;
    }

    static {
        HashSet<String> p = new HashSet<String>(ClientMetadata.getRegisteredParameterNames());
        p.add("application_type");
        p.add("subject_type");
        p.add("sector_identifier_uri");
        p.add("id_token_signed_response_alg");
        p.add("id_token_encrypted_response_alg");
        p.add("id_token_encrypted_response_enc");
        p.add("userinfo_signed_response_alg");
        p.add("userinfo_encrypted_response_alg");
        p.add("userinfo_encrypted_response_enc");
        p.add("default_max_age");
        p.add("require_auth_time");
        p.add("default_acr_values");
        p.add("initiate_login_uri");
        p.add("post_logout_redirect_uris");
        p.add("frontchannel_logout_uri");
        p.add("frontchannel_logout_session_required");
        p.add("backchannel_logout_uri");
        p.add("backchannel_logout_session_required");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

