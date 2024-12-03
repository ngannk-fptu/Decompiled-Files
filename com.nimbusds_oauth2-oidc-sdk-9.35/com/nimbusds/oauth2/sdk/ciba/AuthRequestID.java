/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import java.util.regex.Pattern;
import net.jcip.annotations.Immutable;

@Immutable
public class AuthRequestID
extends Identifier {
    public static final int MIN_BYTE_LENGTH = 16;
    public static final int RECOMMENDED_BYTE_LENGTH = 20;
    public static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("^[a-zA-Z0-9\\.\\-_]+$");
    private static final long serialVersionUID = -484823633025535607L;

    public AuthRequestID() {
        super(20);
    }

    public AuthRequestID(int byteLength) {
        super(byteLength);
        if (byteLength < 16) {
            throw new IllegalArgumentException("The CIBA request ID must be at least 16 bits long");
        }
    }

    public AuthRequestID(String value) {
        super(value);
        if (!ALLOWED_CHARS_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Illegal character(s) in the auth_req_id value");
        }
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AuthRequestID && this.toString().equals(object.toString());
    }

    public static AuthRequestID parse(String value) throws ParseException {
        try {
            return new AuthRequestID(value);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}

