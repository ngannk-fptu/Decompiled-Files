/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt;

import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.PlainHeader;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONObject;

@ThreadSafe
public class PlainJWT
extends PlainObject
implements JWT {
    private static final long serialVersionUID = 1L;

    public PlainJWT(JWTClaimsSet claimsSet) {
        super(new Payload(claimsSet.toJSONObject()));
    }

    public PlainJWT(PlainHeader header, JWTClaimsSet claimsSet) {
        super(header, new Payload(claimsSet.toJSONObject()));
    }

    public PlainJWT(Base64URL firstPart, Base64URL secondPart) throws ParseException {
        super(firstPart, secondPart);
    }

    @Override
    public JWTClaimsSet getJWTClaimsSet() throws ParseException {
        JSONObject json = this.getPayload().toJSONObject();
        if (json == null) {
            throw new ParseException("Payload of unsecured JOSE object is not a valid JSON object", 0);
        }
        return JWTClaimsSet.parse(json);
    }

    public static PlainJWT parse(String s) throws ParseException {
        Base64URL[] parts = JOSEObject.split(s);
        if (!parts[2].toString().isEmpty()) {
            throw new ParseException("Unexpected third Base64URL part in the unsecured JWT object", 0);
        }
        return new PlainJWT(parts[0], parts[1]);
    }
}

