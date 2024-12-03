/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.entities;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.client.ClientMetadata;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import com.nimbusds.openid.connect.sdk.claims.CommonClaimsSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.FederationEntityMetadata;
import com.nimbusds.openid.connect.sdk.federation.entities.FederationMetadataType;
import com.nimbusds.openid.connect.sdk.federation.policy.MetadataPolicy;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.TrustChainConstraints;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import net.minidev.json.JSONObject;

public class EntityStatementClaimsSet
extends CommonClaimsSet {
    public static final String EXP_CLAIM_NAME = "exp";
    public static final String JWKS_CLAIM_NAME = "jwks";
    public static final String AUTHORITY_HINTS_CLAIM_NAME = "authority_hints";
    public static final String METADATA_CLAIM_NAME = "metadata";
    public static final String METADATA_POLICY_CLAIM_NAME = "metadata_policy";
    public static final String TRUST_ANCHOR_ID_CLAIM_NAME = "trust_anchor_id";
    public static final String CONSTRAINTS_CLAIM_NAME = "constraints";
    public static final String CRITICAL_CLAIM_NAME = "crit";
    public static final String POLICY_LANGUAGE_CRITICAL_CLAIM_NAME = "policy_language_crit";

    public EntityStatementClaimsSet(Issuer iss, Subject sub, Date iat, Date exp, JWKSet jwks) {
        this(new EntityID(iss.getValue()), new EntityID(sub.getValue()), iat, exp, jwks);
    }

    public EntityStatementClaimsSet(EntityID iss, EntityID sub, Date iat, Date exp, JWKSet jwks) {
        this.setClaim("iss", iss.getValue());
        this.setClaim("sub", sub.getValue());
        this.setDateClaim("iat", iat);
        this.setDateClaim(EXP_CLAIM_NAME, exp);
        if (jwks != null) {
            this.setClaim(JWKS_CLAIM_NAME, new JSONObject(jwks.toJSONObject(true)));
        }
    }

    public EntityStatementClaimsSet(JWTClaimsSet jwtClaimsSet) throws ParseException {
        super(JSONObjectUtils.toJSONObject(jwtClaimsSet));
        this.validateRequiredClaimsPresence();
    }

    public void validateRequiredClaimsPresence() throws ParseException {
        if (this.getIssuer() == null) {
            throw new ParseException("Missing iss (issuer) claim");
        }
        EntityID.parse(this.getIssuer());
        if (this.getSubject() == null) {
            throw new ParseException("Missing sub (subject) claim");
        }
        EntityID.parse(this.getSubject());
        if (this.getIssueTime() == null) {
            throw new ParseException("Missing iat (issued-at) claim");
        }
        if (this.getExpirationTime() == null) {
            throw new ParseException("Missing exp (expiration) claim");
        }
        if (this.isSelfStatement() && this.getJWKSet() == null) {
            throw new ParseException("Missing jwks (JWK set) claim");
        }
        if (this.isSelfStatement() && !this.hasMetadata()) {
            throw new ParseException("Missing required metadata claim for self-statement");
        }
        List<String> crit = this.getCriticalExtensionClaims();
        if (crit != null) {
            for (String claimName : crit) {
                if (this.getClaim(claimName) != null) continue;
                throw new ParseException("Missing critical " + claimName + " claim");
            }
        }
    }

    public boolean isSelfStatement() {
        Issuer issuer = this.getIssuer();
        Subject subject = this.getSubject();
        return issuer != null && subject != null && issuer.getValue().equals(subject.getValue());
    }

    public EntityID getIssuerEntityID() {
        return new EntityID(this.getIssuer().getValue());
    }

    public EntityID getSubjectEntityID() {
        return new EntityID(this.getSubject().getValue());
    }

    public Date getExpirationTime() {
        return this.getDateClaim(EXP_CLAIM_NAME);
    }

    public JWKSet getJWKSet() {
        JSONObject jwkSetJSONObject = this.getJSONObjectClaim(JWKS_CLAIM_NAME);
        if (jwkSetJSONObject == null) {
            return null;
        }
        try {
            return JWKSet.parse(jwkSetJSONObject);
        }
        catch (java.text.ParseException e) {
            return null;
        }
    }

    public List<EntityID> getAuthorityHints() {
        List<String> strings = this.getStringListClaim(AUTHORITY_HINTS_CLAIM_NAME);
        if (strings == null) {
            return null;
        }
        LinkedList<EntityID> trustChain = new LinkedList<EntityID>();
        for (String s : strings) {
            trustChain.add(new EntityID(s));
        }
        return trustChain;
    }

    public void setAuthorityHints(List<EntityID> trustChain) {
        if (trustChain != null) {
            this.setClaim(AUTHORITY_HINTS_CLAIM_NAME, Identifier.toStringList(trustChain));
        } else {
            this.setClaim(AUTHORITY_HINTS_CLAIM_NAME, null);
        }
    }

    public boolean hasMetadata() {
        JSONObject metadataObject = this.getJSONObjectClaim(METADATA_CLAIM_NAME);
        if (MapUtils.isEmpty(metadataObject)) {
            return false;
        }
        if (metadataObject.get(FederationMetadataType.OPENID_RELYING_PARTY.getValue()) != null) {
            return true;
        }
        if (metadataObject.get(FederationMetadataType.OPENID_PROVIDER.getValue()) != null) {
            return true;
        }
        if (metadataObject.get(FederationMetadataType.OAUTH_AUTHORIZATION_SERVER.getValue()) != null) {
            return true;
        }
        if (metadataObject.get(FederationMetadataType.OAUTH_CLIENT.getValue()) != null) {
            return true;
        }
        if (metadataObject.get(FederationMetadataType.OAUTH_RESOURCE.getValue()) != null) {
            return true;
        }
        return metadataObject.get(FederationMetadataType.FEDERATION_ENTITY.getValue()) != null;
    }

    public JSONObject getMetadata(FederationMetadataType type) {
        JSONObject o = this.getJSONObjectClaim(METADATA_CLAIM_NAME);
        if (o == null) {
            return null;
        }
        try {
            return JSONObjectUtils.getJSONObject(o, type.getValue(), null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setMetadata(FederationMetadataType type, JSONObject metadata) {
        JSONObject o = this.getJSONObjectClaim(METADATA_CLAIM_NAME);
        if (o == null) {
            if (metadata == null) {
                return;
            }
            o = new JSONObject();
        }
        o.put(type.getValue(), metadata);
        this.setClaim(METADATA_CLAIM_NAME, o);
    }

    public OIDCClientMetadata getRPMetadata() {
        JSONObject o = this.getMetadata(FederationMetadataType.OPENID_RELYING_PARTY);
        if (o == null) {
            return null;
        }
        try {
            return OIDCClientMetadata.parse(o);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setRPMetadata(OIDCClientMetadata rpMetadata) {
        JSONObject o = rpMetadata != null ? rpMetadata.toJSONObject() : null;
        this.setMetadata(FederationMetadataType.OPENID_RELYING_PARTY, o);
    }

    public OIDCProviderMetadata getOPMetadata() {
        JSONObject o = this.getMetadata(FederationMetadataType.OPENID_PROVIDER);
        if (o == null) {
            return null;
        }
        try {
            return OIDCProviderMetadata.parse(o);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setOPMetadata(OIDCProviderMetadata opMetadata) {
        JSONObject o = opMetadata != null ? opMetadata.toJSONObject() : null;
        this.setMetadata(FederationMetadataType.OPENID_PROVIDER, o);
    }

    public ClientMetadata getOAuthClientMetadata() {
        JSONObject o = this.getMetadata(FederationMetadataType.OAUTH_CLIENT);
        if (o == null) {
            return null;
        }
        try {
            return ClientMetadata.parse(o);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setOAuthClientMetadata(ClientMetadata clientMetadata) {
        JSONObject o = clientMetadata != null ? clientMetadata.toJSONObject() : null;
        this.setMetadata(FederationMetadataType.OAUTH_CLIENT, o);
    }

    public AuthorizationServerMetadata getASMetadata() {
        JSONObject o = this.getMetadata(FederationMetadataType.OAUTH_AUTHORIZATION_SERVER);
        if (o == null) {
            return null;
        }
        try {
            return AuthorizationServerMetadata.parse(o);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setASMetadata(AuthorizationServerMetadata asMetadata) {
        JSONObject o = asMetadata != null ? asMetadata.toJSONObject() : null;
        this.setMetadata(FederationMetadataType.OAUTH_AUTHORIZATION_SERVER, o);
    }

    public FederationEntityMetadata getFederationEntityMetadata() {
        JSONObject o = this.getMetadata(FederationMetadataType.FEDERATION_ENTITY);
        if (o == null) {
            return null;
        }
        try {
            return FederationEntityMetadata.parse(o);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setFederationEntityMetadata(FederationEntityMetadata entityMetadata) {
        JSONObject o = entityMetadata != null ? entityMetadata.toJSONObject() : null;
        this.setMetadata(FederationMetadataType.FEDERATION_ENTITY, o);
    }

    public JSONObject getMetadataPolicyJSONObject() {
        return this.getJSONObjectClaim(METADATA_POLICY_CLAIM_NAME);
    }

    public void setMetadataPolicyJSONObject(JSONObject metadataPolicy) {
        this.setClaim(METADATA_POLICY_CLAIM_NAME, metadataPolicy);
    }

    public MetadataPolicy getMetadataPolicy(FederationMetadataType type) throws PolicyViolationException {
        JSONObject o = this.getMetadataPolicyJSONObject();
        if (o == null) {
            return null;
        }
        try {
            JSONObject policyJSONObject = JSONObjectUtils.getJSONObject(o, type.getValue(), null);
            if (policyJSONObject == null) {
                return null;
            }
            return MetadataPolicy.parse(policyJSONObject);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setMetadataPolicy(FederationMetadataType type, MetadataPolicy metadataPolicy) {
        JSONObject o = this.getMetadataPolicyJSONObject();
        if (o == null) {
            if (metadataPolicy == null) {
                return;
            }
            o = new JSONObject();
        }
        if (metadataPolicy != null) {
            o.put(type.getValue(), metadataPolicy.toJSONObject());
        } else {
            o.remove(type.getValue());
        }
        if (o.isEmpty()) {
            o = null;
        }
        this.setMetadataPolicyJSONObject(o);
    }

    public EntityID getTrustAnchorID() {
        String value = this.getStringClaim(TRUST_ANCHOR_ID_CLAIM_NAME);
        try {
            return EntityID.parse(value);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setTrustAnchorID(EntityID trustAnchorID) {
        if (trustAnchorID != null) {
            this.setClaim(TRUST_ANCHOR_ID_CLAIM_NAME, trustAnchorID.getValue());
        } else {
            this.setClaim(TRUST_ANCHOR_ID_CLAIM_NAME, null);
        }
    }

    public TrustChainConstraints getConstraints() {
        JSONObject o = this.getJSONObjectClaim(CONSTRAINTS_CLAIM_NAME);
        if (o == null) {
            return null;
        }
        try {
            return TrustChainConstraints.parse(o);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setConstraints(TrustChainConstraints constraints) {
        if (constraints != null) {
            this.setClaim(CONSTRAINTS_CLAIM_NAME, constraints.toJSONObject());
        } else {
            this.setClaim(CONSTRAINTS_CLAIM_NAME, null);
        }
    }

    public List<String> getCriticalExtensionClaims() {
        return this.getStringListClaim(CRITICAL_CLAIM_NAME);
    }

    public void setCriticalExtensionClaims(List<String> claimNames) {
        if (claimNames != null && claimNames.isEmpty()) {
            throw new IllegalArgumentException("The critical extension claim names must not be empty");
        }
        this.setClaim(CRITICAL_CLAIM_NAME, claimNames);
    }

    public List<String> getCriticalPolicyExtensions() {
        return this.getStringListClaim(POLICY_LANGUAGE_CRITICAL_CLAIM_NAME);
    }

    public void setCriticalPolicyExtensions(List<String> extNames) {
        if (extNames != null && extNames.isEmpty()) {
            throw new IllegalArgumentException("The critical policy extension names must not be empty");
        }
        this.setClaim(POLICY_LANGUAGE_CRITICAL_CLAIM_NAME, extNames);
    }
}

