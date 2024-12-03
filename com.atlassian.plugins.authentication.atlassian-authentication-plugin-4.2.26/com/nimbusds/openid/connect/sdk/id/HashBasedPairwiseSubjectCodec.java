/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.id;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.id.PairwiseSubjectCodec;
import com.nimbusds.openid.connect.sdk.id.SectorID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class HashBasedPairwiseSubjectCodec
extends PairwiseSubjectCodec {
    public static final String HASH_ALGORITHM = "SHA-256";

    public HashBasedPairwiseSubjectCodec(byte[] salt) {
        super(salt);
        if (salt == null) {
            throw new IllegalArgumentException("The salt must not be null");
        }
    }

    public HashBasedPairwiseSubjectCodec(Base64URL salt) {
        super(salt.decode());
    }

    @Override
    public Subject encode(SectorID sectorID, Subject localSub) {
        MessageDigest sha256;
        try {
            sha256 = this.getProvider() != null ? MessageDigest.getInstance(HASH_ALGORITHM, this.getProvider()) : MessageDigest.getInstance(HASH_ALGORITHM);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        sha256.update(sectorID.getValue().getBytes(CHARSET));
        sha256.update(localSub.getValue().getBytes(CHARSET));
        byte[] hash = sha256.digest(this.getSalt());
        return new Subject(Base64URL.encode(hash).toString());
    }
}

