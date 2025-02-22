/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.IntegerUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class LegacyConcatKDF {
    private static final byte[] ONE_BYTES = new byte[]{0, 0, 0, 1};
    private static final byte[] ZERO_BYTES = new byte[]{0, 0, 0, 0};
    private static final byte[] ENCRYPTION_BYTES = new byte[]{69, 110, 99, 114, 121, 112, 116, 105, 111, 110};
    private static final byte[] INTEGRITY_BYTES = new byte[]{73, 110, 116, 101, 103, 114, 105, 116, 121};

    public static SecretKey generateCEK(SecretKey key, EncryptionMethod enc, byte[] epu, byte[] epv) throws JOSEException {
        MessageDigest md;
        int hashBitLength;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int cmkBitLength;
            baos.write(ONE_BYTES);
            byte[] cmkBytes = key.getEncoded();
            baos.write(cmkBytes);
            hashBitLength = cmkBitLength = cmkBytes.length * 8;
            int cekBitLength = cmkBitLength / 2;
            byte[] cekBitLengthBytes = IntegerUtils.toBytes(cekBitLength);
            baos.write(cekBitLengthBytes);
            byte[] encBytes = enc.toString().getBytes(StandardCharset.UTF_8);
            baos.write(encBytes);
            if (epu != null) {
                baos.write(IntegerUtils.toBytes(epu.length));
                baos.write(epu);
            } else {
                baos.write(ZERO_BYTES);
            }
            if (epv != null) {
                baos.write(IntegerUtils.toBytes(epv.length));
                baos.write(epv);
            } else {
                baos.write(ZERO_BYTES);
            }
            baos.write(ENCRYPTION_BYTES);
        }
        catch (IOException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        byte[] hashInput = baos.toByteArray();
        try {
            md = MessageDigest.getInstance("SHA-" + hashBitLength);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        byte[] hashOutput = md.digest(hashInput);
        byte[] cekBytes = new byte[hashOutput.length / 2];
        System.arraycopy(hashOutput, 0, cekBytes, 0, cekBytes.length);
        return new SecretKeySpec(cekBytes, "AES");
    }

    public static SecretKey generateCIK(SecretKey key, EncryptionMethod enc, byte[] epu, byte[] epv) throws JOSEException {
        MessageDigest md;
        int cikBitLength;
        int hashBitLength;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int cmkBitLength;
            baos.write(ONE_BYTES);
            byte[] cmkBytes = key.getEncoded();
            baos.write(cmkBytes);
            hashBitLength = cmkBitLength = cmkBytes.length * 8;
            cikBitLength = cmkBitLength;
            byte[] cikBitLengthBytes = IntegerUtils.toBytes(cikBitLength);
            baos.write(cikBitLengthBytes);
            byte[] encBytes = enc.toString().getBytes(StandardCharset.UTF_8);
            baos.write(encBytes);
            if (epu != null) {
                baos.write(IntegerUtils.toBytes(epu.length));
                baos.write(epu);
            } else {
                baos.write(ZERO_BYTES);
            }
            if (epv != null) {
                baos.write(IntegerUtils.toBytes(epv.length));
                baos.write(epv);
            } else {
                baos.write(ZERO_BYTES);
            }
            baos.write(INTEGRITY_BYTES);
        }
        catch (IOException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        byte[] hashInput = baos.toByteArray();
        try {
            md = MessageDigest.getInstance("SHA-" + hashBitLength);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        return new SecretKeySpec(md.digest(hashInput), "HMACSHA" + cikBitLength);
    }

    private LegacyConcatKDF() {
    }
}

