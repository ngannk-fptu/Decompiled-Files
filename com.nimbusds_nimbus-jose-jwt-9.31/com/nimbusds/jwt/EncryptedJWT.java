/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
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
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class EncryptedJWT
extends JWEObject
implements JWT {
    private static final long serialVersionUID = 1L;
    private JWTClaimsSet claimsSet;

    public EncryptedJWT(JWEHeader header, JWTClaimsSet claimsSet) {
        super(header, claimsSet.toPayload());
        this.claimsSet = claimsSet;
    }

    public EncryptedJWT(Base64URL firstPart, Base64URL secondPart, Base64URL thirdPart, Base64URL fourthPart, Base64URL fifthPart) throws ParseException {
        super(firstPart, secondPart, thirdPart, fourthPart, fifthPart);
    }

    @Override
    public JWTClaimsSet getJWTClaimsSet() throws ParseException {
        if (this.claimsSet != null) {
            return this.claimsSet;
        }
        Payload payload = this.getPayload();
        if (payload == null) {
            return null;
        }
        Map<String, Object> json = payload.toJSONObject();
        if (json == null) {
            throw new ParseException("Payload of JWE object is not a valid JSON object", 0);
        }
        this.claimsSet = JWTClaimsSet.parse(json);
        return this.claimsSet;
    }

    @Override
    protected void setPayload(Payload payload) {
        this.claimsSet = null;
        super.setPayload(payload);
    }

    public static EncryptedJWT parse(String s) throws ParseException {
        Base64URL[] parts = JOSEObject.split(s);
        if (parts.length != 5) {
            throw new ParseException("Unexpected number of Base64URL parts, must be five", 0);
        }
        return new EncryptedJWT(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}

