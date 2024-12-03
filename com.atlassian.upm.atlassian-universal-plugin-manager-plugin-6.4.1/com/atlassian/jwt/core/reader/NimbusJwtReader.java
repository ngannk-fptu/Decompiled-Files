/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minidev.json.JSONObject
 */
package com.atlassian.jwt.core.reader;

import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.core.Clock;
import com.atlassian.jwt.core.SimpleJwt;
import com.atlassian.jwt.exception.JwtExpiredException;
import com.atlassian.jwt.exception.JwtInvalidClaimException;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtSignatureMismatchException;
import com.atlassian.jwt.exception.JwtTooEarlyException;
import com.atlassian.jwt.exception.JwtVerificationException;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import com.atlassian.jwt.reader.JwtReader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minidev.json.JSONObject;

public class NimbusJwtReader
implements JwtReader {
    private static final String UNEXPECTED_TYPE_MESSAGE_PREFIX = "Unexpected type of JSON object member with key ";
    private static final Set<String> NUMERIC_CLAIM_NAMES = new HashSet<String>(Arrays.asList("exp", "iat", "nbf"));
    private final String issuer;
    private final JWSVerifier verifier;
    private final Clock clock;

    public NimbusJwtReader(String issuer, JWSVerifier verifier, Clock clock) {
        this.issuer = issuer;
        this.verifier = verifier;
        this.clock = clock;
    }

    @Override
    @Nonnull
    public Jwt readUnverified(@Nonnull String jwt) throws JwtParseException, JwtVerificationException {
        return this.read(jwt, null, false);
    }

    @Override
    @Nonnull
    public Jwt readAndVerify(@Nonnull String jwt, @Nonnull Map<String, ? extends JwtClaimVerifier> requiredClaims) throws JwtParseException, JwtVerificationException {
        return this.read(jwt, requiredClaims, true);
    }

    @Override
    @Deprecated
    @Nonnull
    public Jwt read(@Nonnull String jwt, @Nonnull Map<String, ? extends JwtClaimVerifier> requiredClaims) throws JwtParseException, JwtVerificationException {
        return this.read(jwt, requiredClaims, true);
    }

    private Jwt read(@Nonnull String jwt, Map<String, ? extends JwtClaimVerifier> requiredClaims, boolean verify) throws JwtParseException, JwtVerificationException {
        JWTClaimsSet claims;
        JWSObject jwsObject;
        if (verify) {
            jwsObject = this.verify(jwt);
        } else {
            try {
                jwsObject = JWSObject.parse(jwt);
            }
            catch (ParseException e) {
                throw new JwtParseException(e);
            }
        }
        JSONObject jsonPayload = jwsObject.getPayload().toJSONObject();
        try {
            claims = JWTClaimsSet.parse(jsonPayload);
        }
        catch (ParseException e) {
            if (e.getMessage().startsWith(UNEXPECTED_TYPE_MESSAGE_PREFIX)) {
                String claimName = e.getMessage().replace(UNEXPECTED_TYPE_MESSAGE_PREFIX, "").replaceAll("\"", "");
                if (NUMERIC_CLAIM_NAMES.contains(claimName)) {
                    throw new JwtInvalidClaimException(String.format("Expecting claim '%s' to be numeric but it is a string", claimName), e);
                }
                throw new JwtParseException("Perhaps a claim is of the wrong type (e.g. expecting integer but found string): " + e.getMessage(), e);
            }
            throw new JwtParseException(e);
        }
        if (claims.getIssueTime() == null || claims.getExpirationTime() == null) {
            throw new JwtInvalidClaimException("'exp' and 'iat' are required claims. Atlassian JWT does not allow JWTs with unlimited lifetimes.");
        }
        Date now = this.clock.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(13, -30);
        Date nowMinusLeeway = calendar.getTime();
        calendar.setTime(now);
        calendar.add(13, 30);
        Date nowPlusLeeway = calendar.getTime();
        if (null != claims.getNotBeforeTime()) {
            if (!claims.getExpirationTime().after(claims.getNotBeforeTime())) {
                throw new JwtInvalidClaimException(String.format("The expiration time must be after the not-before time but exp=%s and nbf=%s", claims.getExpirationTime(), claims.getNotBeforeTime()));
            }
            if (claims.getNotBeforeTime().after(nowPlusLeeway)) {
                throw new JwtTooEarlyException(claims.getNotBeforeTime(), now, 30);
            }
        }
        if (claims.getExpirationTime().before(nowMinusLeeway)) {
            throw new JwtExpiredException(claims.getExpirationTime(), now, 30);
        }
        if (requiredClaims != null) {
            for (Map.Entry<String, ? extends JwtClaimVerifier> requiredClaim : requiredClaims.entrySet()) {
                requiredClaim.getValue().verify(claims.getClaim(requiredClaim.getKey()));
            }
        }
        return new SimpleJwt(claims.getIssuer(), claims.getSubject(), jsonPayload.toString());
    }

    private JWSObject verify(@Nonnull String jwt) throws JwtParseException, JwtVerificationException {
        try {
            JWSObject jwsObject = JWSObject.parse(jwt);
            if (!jwsObject.verify(this.verifier)) {
                throw new JwtSignatureMismatchException(jwt, this.issuer);
            }
            return jwsObject;
        }
        catch (ParseException e) {
            throw new JwtParseException(e);
        }
        catch (JOSEException e) {
            throw new JwtSignatureMismatchException(e);
        }
    }
}

