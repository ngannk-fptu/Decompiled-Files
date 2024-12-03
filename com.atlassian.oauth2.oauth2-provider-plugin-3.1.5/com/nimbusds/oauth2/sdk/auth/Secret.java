/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.jose.crypto.utils.ConstantTimeUtils;
import com.nimbusds.jose.util.Base64URL;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import net.jcip.annotations.Immutable;

@Immutable
public class Secret
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_BYTE_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private byte[] value;
    private final Date expDate;

    public Secret(String value) {
        this(value, null);
    }

    public Secret(String value, Date expDate) {
        this.value = value.getBytes(StandardCharsets.UTF_8);
        this.expDate = expDate;
    }

    public Secret(int byteLength) {
        this(byteLength, null);
    }

    public Secret(int byteLength, Date expDate) {
        if (byteLength < 1) {
            throw new IllegalArgumentException("The byte length must be a positive integer");
        }
        byte[] n = new byte[byteLength];
        SECURE_RANDOM.nextBytes(n);
        this.value = Base64URL.encode(n).toString().getBytes(StandardCharsets.UTF_8);
        this.expDate = expDate;
    }

    public Secret() {
        this(32);
    }

    public String getValue() {
        if (this.value == null) {
            return null;
        }
        return new String(this.value, StandardCharsets.UTF_8);
    }

    public byte[] getValueBytes() {
        return this.value;
    }

    public byte[] getSHA256() {
        if (this.value == null) {
            return null;
        }
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return sha256.digest(this.value);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void erase() {
        if (this.value == null) {
            return;
        }
        Arrays.fill(this.value, (byte)0);
        this.value = null;
    }

    public Date getExpirationDate() {
        return this.expDate;
    }

    public boolean expired() {
        if (this.expDate == null) {
            return false;
        }
        Date now = new Date();
        return this.expDate.before(now);
    }

    @Deprecated
    public boolean equalsSHA256Based(Secret other) {
        if (other == null) {
            return false;
        }
        byte[] thisHash = this.getSHA256();
        byte[] otherHash = other.getSHA256();
        if (thisHash == null || otherHash == null) {
            return false;
        }
        return ConstantTimeUtils.areEqual(thisHash, otherHash);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (this.value == null) {
            return false;
        }
        if (!(o instanceof Secret)) {
            return false;
        }
        Secret otherSecret = (Secret)o;
        return this.equalsSHA256Based(otherSecret);
    }

    public int hashCode() {
        return Arrays.hashCode(this.value);
    }
}

