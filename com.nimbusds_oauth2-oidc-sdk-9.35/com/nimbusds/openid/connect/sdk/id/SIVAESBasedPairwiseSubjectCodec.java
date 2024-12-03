/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.jose.util.ByteUtils
 *  net.jcip.annotations.ThreadSafe
 *  org.cryptomator.siv.SivMode
 */
package com.nimbusds.openid.connect.sdk.id;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.id.InvalidPairwiseSubjectException;
import com.nimbusds.openid.connect.sdk.id.PairwiseSubjectCodec;
import com.nimbusds.openid.connect.sdk.id.SectorID;
import java.util.AbstractMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.jcip.annotations.ThreadSafe;
import org.cryptomator.siv.SivMode;

@ThreadSafe
public class SIVAESBasedPairwiseSubjectCodec
extends PairwiseSubjectCodec {
    private static final SivMode AES_SIV = new SivMode();
    private final byte[] aesCtrKey;
    private final byte[] macKey;
    private final int padSubjectToLength;

    public SIVAESBasedPairwiseSubjectCodec(SecretKey secretKey) {
        this(secretKey, -1);
    }

    public SIVAESBasedPairwiseSubjectCodec(SecretKey secretKey, int padSubjectToLength) {
        super(null);
        if (secretKey == null) {
            throw new IllegalArgumentException("The SIV AES secret key must not be null");
        }
        byte[] keyBytes = secretKey.getEncoded();
        switch (keyBytes.length) {
            case 32: {
                this.aesCtrKey = ByteUtils.subArray((byte[])keyBytes, (int)0, (int)16);
                this.macKey = ByteUtils.subArray((byte[])keyBytes, (int)16, (int)16);
                break;
            }
            case 48: {
                this.aesCtrKey = ByteUtils.subArray((byte[])keyBytes, (int)0, (int)24);
                this.macKey = ByteUtils.subArray((byte[])keyBytes, (int)24, (int)24);
                break;
            }
            case 64: {
                this.aesCtrKey = ByteUtils.subArray((byte[])keyBytes, (int)0, (int)32);
                this.macKey = ByteUtils.subArray((byte[])keyBytes, (int)32, (int)32);
                break;
            }
            default: {
                throw new IllegalArgumentException("The SIV AES secret key length must be 256, 384 or 512 bits");
            }
        }
        this.padSubjectToLength = padSubjectToLength;
    }

    public SecretKey getSecretKey() {
        return new SecretKeySpec(ByteUtils.concat((byte[][])new byte[][]{this.aesCtrKey, this.macKey}), "AES");
    }

    public int getPadSubjectToLength() {
        return this.padSubjectToLength;
    }

    private static String escapeSeparator(String s) {
        return s.replace("|", "\\|");
    }

    @Override
    public Subject encode(SectorID sectorID, Subject localSub) {
        String escapedSectorIDString = SIVAESBasedPairwiseSubjectCodec.escapeSeparator(sectorID.getValue());
        String escapedLocalSub = SIVAESBasedPairwiseSubjectCodec.escapeSeparator(localSub.getValue());
        StringBuilder optionalPadding = new StringBuilder();
        if (this.padSubjectToLength > 0) {
            int paddingLength = this.padSubjectToLength - escapedLocalSub.length();
            if (paddingLength == 1) {
                optionalPadding = new StringBuilder("|");
            } else if (paddingLength > 1) {
                optionalPadding = new StringBuilder("|");
                int i = paddingLength;
                while (--i > 0) {
                    optionalPadding.append("0");
                }
            }
        }
        String plainTextString = escapedSectorIDString + '|' + escapedLocalSub + optionalPadding;
        byte[] plainText = plainTextString.getBytes(CHARSET);
        byte[] cipherText = AES_SIV.encrypt(this.aesCtrKey, this.macKey, plainText, (byte[][])new byte[0][]);
        return new Subject(Base64URL.encode((byte[])cipherText).toString());
    }

    @Override
    public Map.Entry<SectorID, Subject> decode(Subject pairwiseSubject) throws InvalidPairwiseSubjectException {
        byte[] plainText;
        byte[] cipherText = new Base64URL(pairwiseSubject.getValue()).decode();
        try {
            plainText = AES_SIV.decrypt(this.aesCtrKey, this.macKey, cipherText, (byte[][])new byte[0][]);
        }
        catch (Exception e) {
            throw new InvalidPairwiseSubjectException("Decryption failed: " + e.getMessage(), e);
        }
        String[] parts = new String(plainText, CHARSET).split("(?<!\\\\)\\|");
        for (int i = 0; i < parts.length; ++i) {
            parts[i] = parts[i].replace("\\|", "|");
        }
        if (parts.length > 3) {
            throw new InvalidPairwiseSubjectException("Invalid format: Unexpected number of tokens: " + parts.length);
        }
        return new AbstractMap.SimpleImmutableEntry<SectorID, Subject>(new SectorID(parts[0]), new Subject(parts[1]));
    }
}

