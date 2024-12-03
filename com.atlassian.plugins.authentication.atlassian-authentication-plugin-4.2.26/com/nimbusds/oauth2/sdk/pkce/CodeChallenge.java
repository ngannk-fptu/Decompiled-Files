/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.pkce;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CodeChallenge
extends Identifier {
    private CodeChallenge(String value) {
        super(value);
    }

    public static CodeChallenge compute(CodeChallengeMethod method, CodeVerifier codeVerifier) {
        if (CodeChallengeMethod.PLAIN.equals(method)) {
            return new CodeChallenge(codeVerifier.getValue());
        }
        if (CodeChallengeMethod.S256.equals(method)) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-256");
            }
            catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage());
            }
            byte[] hash = md.digest(codeVerifier.getValueBytes());
            return new CodeChallenge(Base64URL.encode(hash).toString());
        }
        throw new IllegalArgumentException("Unsupported code challenge method: " + method);
    }

    public static CodeChallenge parse(String value) throws ParseException {
        try {
            return new CodeChallenge(value);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException("Invalid code challenge: " + e.getMessage(), e);
        }
    }
}

