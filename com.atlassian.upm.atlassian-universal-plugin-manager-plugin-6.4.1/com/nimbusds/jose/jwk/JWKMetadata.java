/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertChainUtils;
import java.net.URI;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import net.minidev.json.JSONObject;

final class JWKMetadata {
    JWKMetadata() {
    }

    static KeyType parseKeyType(JSONObject o) throws ParseException {
        try {
            return KeyType.parse(JSONObjectUtils.getString(o, "kty"));
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    static KeyUse parseKeyUse(JSONObject o) throws ParseException {
        return KeyUse.parse(JSONObjectUtils.getString(o, "use"));
    }

    static Set<KeyOperation> parseKeyOperations(JSONObject o) throws ParseException {
        return KeyOperation.parse(JSONObjectUtils.getStringList(o, "key_ops"));
    }

    static Algorithm parseAlgorithm(JSONObject o) throws ParseException {
        return Algorithm.parse(JSONObjectUtils.getString(o, "alg"));
    }

    static String parseKeyID(JSONObject o) throws ParseException {
        return JSONObjectUtils.getString(o, "kid");
    }

    static URI parseX509CertURL(JSONObject o) throws ParseException {
        return JSONObjectUtils.getURI(o, "x5u");
    }

    static Base64URL parseX509CertThumbprint(JSONObject o) throws ParseException {
        return JSONObjectUtils.getBase64URL(o, "x5t");
    }

    static Base64URL parseX509CertSHA256Thumbprint(JSONObject o) throws ParseException {
        return JSONObjectUtils.getBase64URL(o, "x5t#S256");
    }

    static List<Base64> parseX509CertChain(JSONObject o) throws ParseException {
        List<Base64> chain = X509CertChainUtils.toBase64List(JSONObjectUtils.getJSONArray(o, "x5c"));
        if (chain == null || !chain.isEmpty()) {
            return chain;
        }
        return null;
    }
}

