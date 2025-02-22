/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.jwt;

import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONObject;

@ThreadSafe
public class EncryptedJWT
extends JWEObject
implements JWT {
    private static final long serialVersionUID = 1L;

    public EncryptedJWT(JWEHeader header, JWTClaimsSet claimsSet) {
        super(header, new Payload(claimsSet.toJSONObject()));
    }

    public EncryptedJWT(Base64URL firstPart, Base64URL secondPart, Base64URL thirdPart, Base64URL fourthPart, Base64URL fifthPart) throws ParseException {
        super(firstPart, secondPart, thirdPart, fourthPart, fifthPart);
    }

    @Override
    public JWTClaimsSet getJWTClaimsSet() throws ParseException {
        Payload payload = this.getPayload();
        if (payload == null) {
            return null;
        }
        JSONObject json = payload.toJSONObject();
        if (json == null) {
            throw new ParseException("Payload of JWE object is not a valid JSON object", 0);
        }
        return JWTClaimsSet.parse(json);
    }

    public static EncryptedJWT parse(String s) throws ParseException {
        Base64URL[] parts = JOSEObject.split(s);
        if (parts.length != 5) {
            throw new ParseException("Unexpected number of Base64URL parts, must be five", 0);
        }
        return new EncryptedJWT(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}

