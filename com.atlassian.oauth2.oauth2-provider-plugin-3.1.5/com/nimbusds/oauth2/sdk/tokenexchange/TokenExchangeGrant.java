/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.tokenexchange;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.token.Token;
import com.nimbusds.oauth2.sdk.token.TokenTypeURI;
import com.nimbusds.oauth2.sdk.token.TypelessToken;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.jcip.annotations.Immutable;

@Immutable
public class TokenExchangeGrant
extends AuthorizationGrant {
    public static final GrantType GRANT_TYPE = GrantType.TOKEN_EXCHANGE;
    private final Token subjectToken;
    private final TokenTypeURI subjectTokenType;
    private final Token actorToken;
    private final TokenTypeURI actorTokenType;
    private final TokenTypeURI requestedTokenType;
    private final List<Audience> audience;

    public TokenExchangeGrant(Token subjectToken, TokenTypeURI subjectTokenType) {
        this(subjectToken, subjectTokenType, null, null, null, null);
    }

    public TokenExchangeGrant(Token subjectToken, TokenTypeURI subjectTokenType, Token actorToken, TokenTypeURI actorTokenType, TokenTypeURI requestedTokenType, List<Audience> audience) {
        super(GRANT_TYPE);
        if (subjectToken == null) {
            throw new IllegalArgumentException("The subject token must not be null");
        }
        this.subjectToken = subjectToken;
        if (subjectTokenType == null) {
            throw new IllegalArgumentException("The subject token type must not be null");
        }
        this.subjectTokenType = subjectTokenType;
        this.actorToken = actorToken;
        if (actorToken != null && actorTokenType == null) {
            throw new IllegalArgumentException("If an actor token is specified the actor token type must not be null");
        }
        this.actorTokenType = actorTokenType;
        this.requestedTokenType = requestedTokenType;
        this.audience = audience;
    }

    public Token getSubjectToken() {
        return this.subjectToken;
    }

    public TokenTypeURI getSubjectTokenType() {
        return this.subjectTokenType;
    }

    public Token getActorToken() {
        return this.actorToken;
    }

    public TokenTypeURI getActorTokenType() {
        return this.actorTokenType;
    }

    public TokenTypeURI getRequestedTokenType() {
        return this.requestedTokenType;
    }

    public List<Audience> getAudience() {
        return this.audience;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        if (CollectionUtils.isNotEmpty(this.audience)) {
            params.put("audience", Audience.toStringList(this.audience));
        }
        if (this.requestedTokenType != null) {
            params.put("requested_token_type", Collections.singletonList(this.requestedTokenType.getURI().toString()));
        }
        params.put("subject_token", Collections.singletonList(this.subjectToken.getValue()));
        params.put("subject_token_type", Collections.singletonList(this.subjectTokenType.getURI().toString()));
        if (this.actorToken != null) {
            params.put("actor_token", Collections.singletonList(this.actorToken.getValue()));
            params.put("actor_token_type", Collections.singletonList(this.actorTokenType.getURI().toString()));
        }
        return params;
    }

    private static List<Audience> parseAudience(Map<String, List<String>> params) {
        List<String> audienceList = params.get("audience");
        if (CollectionUtils.isEmpty(audienceList)) {
            return null;
        }
        return Audience.create(audienceList);
    }

    private static TokenTypeURI parseTokenType(Map<String, List<String>> params, String key, boolean mandatory) throws ParseException {
        String tokenTypeString = MultivaluedMapUtils.getFirstValue(params, key);
        if (StringUtils.isBlank(tokenTypeString)) {
            if (mandatory) {
                String msg = String.format("Missing or empty %s parameter", key);
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
            }
            return null;
        }
        try {
            return TokenTypeURI.parse(tokenTypeString);
        }
        catch (ParseException uriSyntaxException) {
            String msg = "Invalid " + key + " " + tokenTypeString;
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
    }

    private static TypelessToken parseToken(Map<String, List<String>> params, String key, boolean mandatory) throws ParseException {
        String tokenString = MultivaluedMapUtils.getFirstValue(params, key);
        if (StringUtils.isBlank(tokenString)) {
            if (mandatory) {
                String msg = String.format("Missing or empty %s parameter", key);
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
            }
            return null;
        }
        return new TypelessToken(tokenString);
    }

    public static TokenExchangeGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        List<Audience> audience = TokenExchangeGrant.parseAudience(params);
        TokenTypeURI requestedTokenType = TokenExchangeGrant.parseTokenType(params, "requested_token_type", false);
        TypelessToken subjectToken = TokenExchangeGrant.parseToken(params, "subject_token", true);
        TokenTypeURI subjectTokenType = TokenExchangeGrant.parseTokenType(params, "subject_token_type", true);
        TypelessToken actorToken = TokenExchangeGrant.parseToken(params, "actor_token", false);
        TokenTypeURI actorTokenType = TokenExchangeGrant.parseTokenType(params, "actor_token_type", false);
        return new TokenExchangeGrant(subjectToken, subjectTokenType, actorToken, actorTokenType, requestedTokenType, audience);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TokenExchangeGrant)) {
            return false;
        }
        TokenExchangeGrant that = (TokenExchangeGrant)o;
        return this.requestedTokenType.equals(that.requestedTokenType) && this.subjectToken.equals(that.subjectToken) && this.subjectTokenType.equals(that.subjectTokenType) && Objects.equals(this.actorToken, that.actorToken) && Objects.equals(this.actorTokenType, that.actorTokenType);
    }

    public int hashCode() {
        return Objects.hash(this.requestedTokenType, this.subjectToken, this.subjectTokenType, this.actorToken, this.actorTokenType);
    }
}

