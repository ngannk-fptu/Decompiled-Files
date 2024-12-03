/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.jwt.util.DateUtils
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.auth.X509CertificateConfirmation;
import com.nimbusds.oauth2.sdk.dpop.JWKThumbprintConfirmation;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class TokenIntrospectionSuccessResponse
extends TokenIntrospectionResponse
implements SuccessResponse {
    private final JSONObject params;

    public TokenIntrospectionSuccessResponse(JSONObject params) {
        if (!(params.get((Object)"active") instanceof Boolean)) {
            throw new IllegalArgumentException("Missing / invalid boolean active parameter");
        }
        this.params = params;
    }

    public boolean isActive() {
        try {
            return JSONObjectUtils.getBoolean(this.params, "active", false);
        }
        catch (ParseException e) {
            return false;
        }
    }

    public Scope getScope() {
        try {
            return Scope.parse(JSONObjectUtils.getString(this.params, "scope"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public ClientID getClientID() {
        try {
            return new ClientID(JSONObjectUtils.getString(this.params, "client_id"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public String getUsername() {
        try {
            return JSONObjectUtils.getString(this.params, "username", null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public AccessTokenType getTokenType() {
        try {
            return new AccessTokenType(JSONObjectUtils.getString(this.params, "token_type"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Date getExpirationTime() {
        try {
            return DateUtils.fromSecondsSinceEpoch((long)JSONObjectUtils.getLong(this.params, "exp"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Date getIssueTime() {
        try {
            return DateUtils.fromSecondsSinceEpoch((long)JSONObjectUtils.getLong(this.params, "iat"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Date getNotBeforeTime() {
        try {
            return DateUtils.fromSecondsSinceEpoch((long)JSONObjectUtils.getLong(this.params, "nbf"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Subject getSubject() {
        try {
            return new Subject(JSONObjectUtils.getString(this.params, "sub"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public List<Audience> getAudience() {
        try {
            return Audience.create(JSONObjectUtils.getStringList(this.params, "aud"));
        }
        catch (ParseException e) {
            try {
                return new Audience(JSONObjectUtils.getString(this.params, "aud")).toSingleAudienceList();
            }
            catch (ParseException e2) {
                return null;
            }
        }
    }

    public Issuer getIssuer() {
        try {
            return new Issuer(JSONObjectUtils.getString(this.params, "iss"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public JWTID getJWTID() {
        try {
            return new JWTID(JSONObjectUtils.getString(this.params, "jti"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    @Deprecated
    public Base64URL getX509CertificateSHA256Thumbprint() {
        try {
            JSONObject cnf = JSONObjectUtils.getJSONObject(this.params, "cnf", null);
            if (cnf == null) {
                return null;
            }
            String x5t = JSONObjectUtils.getString(cnf, "x5t#S256", null);
            if (x5t == null) {
                return null;
            }
            return new Base64URL(x5t);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public X509CertificateConfirmation getX509CertificateConfirmation() {
        return X509CertificateConfirmation.parse(this.params);
    }

    public JWKThumbprintConfirmation getJWKThumbprintConfirmation() {
        return JWKThumbprintConfirmation.parse(this.params);
    }

    public String getStringParameter(String name) {
        try {
            return JSONObjectUtils.getString(this.params, name, null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public boolean getBooleanParameter(String name) throws ParseException {
        return JSONObjectUtils.getBoolean(this.params, name);
    }

    public Number getNumberParameter(String name) {
        try {
            return JSONObjectUtils.getNumber(this.params, name, null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public List<String> getStringListParameter(String name) {
        try {
            return JSONObjectUtils.getStringList(this.params, name, null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public JSONObject getJSONObjectParameter(String name) {
        try {
            return JSONObjectUtils.getJSONObject(this.params, name, null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public JSONObject getParameters() {
        return this.params;
    }

    public JSONObject toJSONObject() {
        return new JSONObject((Map)this.params);
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setContent(this.params.toJSONString());
        return httpResponse;
    }

    public static TokenIntrospectionSuccessResponse parse(JSONObject jsonObject) throws ParseException {
        try {
            return new TokenIntrospectionSuccessResponse(jsonObject);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public static TokenIntrospectionSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200);
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        return TokenIntrospectionSuccessResponse.parse(jsonObject);
    }

    public static class Builder {
        private final JSONObject params = new JSONObject();

        public Builder(boolean active) {
            this.params.put((Object)"active", (Object)active);
        }

        public Builder(TokenIntrospectionSuccessResponse response) {
            this.params.putAll((Map)response.params);
        }

        public Builder scope(Scope scope) {
            if (scope != null) {
                this.params.put((Object)"scope", (Object)scope.toString());
            } else {
                this.params.remove((Object)"scope");
            }
            return this;
        }

        public Builder clientID(ClientID clientID) {
            if (clientID != null) {
                this.params.put((Object)"client_id", (Object)clientID.getValue());
            } else {
                this.params.remove((Object)"client_id");
            }
            return this;
        }

        public Builder username(String username) {
            if (username != null) {
                this.params.put((Object)"username", (Object)username);
            } else {
                this.params.remove((Object)"username");
            }
            return this;
        }

        public Builder tokenType(AccessTokenType tokenType) {
            if (tokenType != null) {
                this.params.put((Object)"token_type", (Object)tokenType.getValue());
            } else {
                this.params.remove((Object)"token_type");
            }
            return this;
        }

        public Builder expirationTime(Date exp) {
            if (exp != null) {
                this.params.put((Object)"exp", (Object)DateUtils.toSecondsSinceEpoch((Date)exp));
            } else {
                this.params.remove((Object)"exp");
            }
            return this;
        }

        public Builder issueTime(Date iat) {
            if (iat != null) {
                this.params.put((Object)"iat", (Object)DateUtils.toSecondsSinceEpoch((Date)iat));
            } else {
                this.params.remove((Object)"iat");
            }
            return this;
        }

        public Builder notBeforeTime(Date nbf) {
            if (nbf != null) {
                this.params.put((Object)"nbf", (Object)DateUtils.toSecondsSinceEpoch((Date)nbf));
            } else {
                this.params.remove((Object)"nbf");
            }
            return this;
        }

        public Builder subject(Subject sub) {
            if (sub != null) {
                this.params.put((Object)"sub", (Object)sub.getValue());
            } else {
                this.params.remove((Object)"sub");
            }
            return this;
        }

        public Builder audience(List<Audience> audList) {
            if (audList != null) {
                this.params.put((Object)"aud", Audience.toStringList(audList));
            } else {
                this.params.remove((Object)"aud");
            }
            return this;
        }

        public Builder issuer(Issuer iss) {
            if (iss != null) {
                this.params.put((Object)"iss", (Object)iss.getValue());
            } else {
                this.params.remove((Object)"iss");
            }
            return this;
        }

        public Builder jwtID(JWTID jti) {
            if (jti != null) {
                this.params.put((Object)"jti", (Object)jti.getValue());
            } else {
                this.params.remove((Object)"jti");
            }
            return this;
        }

        @Deprecated
        public Builder x509CertificateSHA256Thumbprint(Base64URL x5t) {
            if (x5t != null) {
                JSONObject cnf;
                if (this.params.containsKey((Object)"cnf")) {
                    cnf = (JSONObject)this.params.get((Object)"cnf");
                } else {
                    cnf = new JSONObject();
                    this.params.put((Object)"cnf", (Object)cnf);
                }
                cnf.put((Object)"x5t#S256", (Object)x5t.toString());
            } else if (this.params.containsKey((Object)"cnf")) {
                JSONObject cnf = (JSONObject)this.params.get((Object)"cnf");
                cnf.remove((Object)"x5t#S256");
                if (cnf.isEmpty()) {
                    this.params.remove((Object)"cnf");
                }
            }
            return this;
        }

        public Builder x509CertificateConfirmation(X509CertificateConfirmation cnf) {
            if (cnf != null) {
                Map.Entry<String, JSONObject> param = cnf.toJWTClaim();
                this.params.put((Object)param.getKey(), (Object)param.getValue());
            } else {
                this.params.remove((Object)"cnf");
            }
            return this;
        }

        public Builder jwkThumbprintConfirmation(JWKThumbprintConfirmation cnf) {
            if (cnf != null) {
                Map.Entry<String, JSONObject> param = cnf.toJWTClaim();
                this.params.put((Object)param.getKey(), (Object)param.getValue());
            } else {
                this.params.remove((Object)"cnf");
            }
            return this;
        }

        public Builder parameter(String name, Object value) {
            if (value != null) {
                this.params.put((Object)name, value);
            } else {
                this.params.remove((Object)name);
            }
            return this;
        }

        public TokenIntrospectionSuccessResponse build() {
            return new TokenIntrospectionSuccessResponse(this.params);
        }
    }
}

