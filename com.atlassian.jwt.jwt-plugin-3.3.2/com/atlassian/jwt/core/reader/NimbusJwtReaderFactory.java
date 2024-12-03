/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core.reader;

import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.core.Clock;
import com.atlassian.jwt.core.SimpleJwt;
import com.atlassian.jwt.core.StaticClock;
import com.atlassian.jwt.core.SystemClock;
import com.atlassian.jwt.core.reader.JwtIssuerSharedSecretService;
import com.atlassian.jwt.core.reader.JwtIssuerValidator;
import com.atlassian.jwt.core.reader.NimbusMacJwtReader;
import com.atlassian.jwt.core.reader.NimbusRsJwtReader;
import com.atlassian.jwt.exception.JwsUnsupportedAlgorithmException;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.reader.JwtReader;
import com.atlassian.jwt.reader.JwtReaderFactory;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import javax.annotation.Nonnull;

public class NimbusJwtReaderFactory
implements JwtReaderFactory {
    private final JwtIssuerValidator jwtIssuerValidator;
    private final JwtIssuerSharedSecretService jwtIssuerSharedSecretService;

    public NimbusJwtReaderFactory(JwtIssuerValidator jwtIssuerValidator, JwtIssuerSharedSecretService jwtIssuerSharedSecretService) {
        this.jwtIssuerValidator = jwtIssuerValidator;
        this.jwtIssuerSharedSecretService = jwtIssuerSharedSecretService;
    }

    @Override
    @Nonnull
    public JwtReader getReader(@Nonnull String jwt) throws JwtParseException, JwsUnsupportedAlgorithmException, JwtUnknownIssuerException, JwtIssuerLacksSharedSecretException {
        return this.getReader(jwt, SystemClock.getInstance());
    }

    @Override
    @Nonnull
    public JwtReader getReader(@Nonnull String jwt, @Nonnull Date date) throws JwsUnsupportedAlgorithmException, JwtUnknownIssuerException, JwtParseException, JwtIssuerLacksSharedSecretException {
        return this.getReader(jwt, StaticClock.at(date));
    }

    @Override
    @Nonnull
    public JwtReader getReader(@Nonnull String jwt, RSAPublicKey publicKey) throws JwsUnsupportedAlgorithmException, JwtParseException, JwtUnknownIssuerException {
        return this.getReader(jwt, publicKey, SystemClock.getInstance());
    }

    @Override
    @Nonnull
    public JwtReader getReader(@Nonnull String jwt, RSAPublicKey publicKey, @Nonnull Date date) throws JwsUnsupportedAlgorithmException, JwtParseException, JwtUnknownIssuerException {
        return this.getReader(jwt, publicKey, StaticClock.at(date));
    }

    private JwtReader getReader(String jwt, Clock clock) throws JwsUnsupportedAlgorithmException, JwtUnknownIssuerException, JwtParseException, JwtIssuerLacksSharedSecretException {
        SimpleUnverifiedJwt unverifiedJwt = new NimbusUnverifiedJwtReader().parse(jwt);
        SigningAlgorithm algorithm = this.validateAlgorithm(unverifiedJwt);
        String issuer = this.validateIssuer(unverifiedJwt);
        if (algorithm.requiresSharedSecret()) {
            return this.macVerifyingReader(issuer, this.jwtIssuerSharedSecretService.getSharedSecret(issuer), clock);
        }
        throw new JwsUnsupportedAlgorithmException(String.format("Expected a symmetric signing algorithm such as %s, and not %s. Try a symmetric algorithm.", new Object[]{SigningAlgorithm.HS256, algorithm.name()}));
    }

    private JwtReader getReader(String jwt, RSAPublicKey publicKey, Clock clock) throws JwsUnsupportedAlgorithmException, JwtParseException, JwtUnknownIssuerException {
        SimpleUnverifiedJwt unverifiedJwt = new NimbusUnverifiedJwtReader().parse(jwt);
        SigningAlgorithm algorithm = this.validateAlgorithm(unverifiedJwt);
        String issuer = this.validateIssuer(unverifiedJwt);
        if (algorithm.requiresKeyPair()) {
            return this.rsVerifyingReader(issuer, publicKey, clock);
        }
        throw new JwsUnsupportedAlgorithmException(String.format("Expected an asymmetric signing algorithm such as %s, and not %s. Try an asymmetric algorithm.", new Object[]{SigningAlgorithm.RS256, algorithm.name()}));
    }

    private JwtReader macVerifyingReader(String issuer, String sharedSecret, Clock clock) {
        return new NimbusMacJwtReader(issuer, sharedSecret, clock);
    }

    private JwtReader rsVerifyingReader(String issuer, RSAPublicKey publicKey, Clock clock) {
        return new NimbusRsJwtReader(issuer, publicKey, clock);
    }

    private String validateIssuer(SimpleUnverifiedJwt unverifiedJwt) throws JwtUnknownIssuerException {
        String issuer = unverifiedJwt.getIssuer();
        if (!this.jwtIssuerValidator.isValid(issuer)) {
            throw new JwtUnknownIssuerException(issuer);
        }
        return issuer;
    }

    private SigningAlgorithm validateAlgorithm(SimpleUnverifiedJwt unverifiedJwt) throws JwsUnsupportedAlgorithmException {
        return SigningAlgorithm.forName(unverifiedJwt.getAlgorithm());
    }

    private static class NimbusUnverifiedJwtReader {
        private NimbusUnverifiedJwtReader() {
        }

        public SimpleUnverifiedJwt parse(String jwt) throws JwtParseException {
            JWSObject jwsObject = this.parseJWSObject(jwt);
            try {
                JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
                return new SimpleUnverifiedJwt(jwsObject.getHeader().getAlgorithm().getName(), claims.getIssuer(), claims.getSubject(), jwsObject.getPayload().toString());
            }
            catch (ParseException e) {
                throw new JwtParseException(e);
            }
        }

        private JWSObject parseJWSObject(String jwt) throws JwtParseException {
            JWSObject jwsObject;
            try {
                jwsObject = JWSObject.parse(jwt);
            }
            catch (ParseException e) {
                throw new JwtParseException(e);
            }
            return jwsObject;
        }
    }

    private static class SimpleUnverifiedJwt
    extends SimpleJwt {
        private final String algorithm;

        public SimpleUnverifiedJwt(String algorithm, String iss, String sub, String payload) {
            super(iss, sub, payload);
            this.algorithm = algorithm;
        }

        public String getAlgorithm() {
            return this.algorithm;
        }
    }
}

