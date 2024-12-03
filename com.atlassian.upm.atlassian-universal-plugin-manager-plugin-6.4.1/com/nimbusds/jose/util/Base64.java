/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONAware
 *  net.minidev.json.JSONValue
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.Base64Codec;
import com.nimbusds.jose.util.BigIntegerUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.io.Serializable;
import java.math.BigInteger;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONValue;

@Immutable
public class Base64
implements JSONAware,
Serializable {
    private static final long serialVersionUID = 1L;
    private final String value;

    public Base64(String base64) {
        if (base64 == null) {
            throw new IllegalArgumentException("The Base64 value must not be null");
        }
        this.value = base64;
    }

    public byte[] decode() {
        return Base64Codec.decode(this.value);
    }

    public BigInteger decodeToBigInteger() {
        return new BigInteger(1, this.decode());
    }

    public String decodeToString() {
        return new String(this.decode(), StandardCharset.UTF_8);
    }

    public String toJSONString() {
        return "\"" + JSONValue.escape((String)this.value) + "\"";
    }

    public String toString() {
        return this.value;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object object) {
        return object != null && object instanceof Base64 && this.toString().equals(object.toString());
    }

    public static Base64 from(String base64) {
        if (base64 == null) {
            return null;
        }
        return new Base64(base64);
    }

    public static Base64 encode(byte[] bytes) {
        return new Base64(Base64Codec.encodeToString(bytes, false));
    }

    public static Base64 encode(BigInteger bigInt) {
        return Base64.encode(BigIntegerUtils.toBytesUnsigned(bigInt));
    }

    public static Base64 encode(String text) {
        return Base64.encode(text.getBytes(StandardCharset.UTF_8));
    }
}

