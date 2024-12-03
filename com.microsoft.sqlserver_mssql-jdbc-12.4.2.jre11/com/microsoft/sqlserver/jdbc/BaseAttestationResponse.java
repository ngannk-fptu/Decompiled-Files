/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;

abstract class BaseAttestationResponse {
    protected int totalSize;
    protected int identitySize;
    protected int attestationTokenSize;
    protected int enclaveType;
    protected byte[] enclavePK;
    protected int sessionInfoSize;
    protected byte[] sessionID = new byte[8];
    protected int dhpkSize;
    protected int dhpkSsize;
    protected byte[] dhPublicKey;
    protected byte[] publicKeySig;

    BaseAttestationResponse() {
    }

    void validateDHPublicKey() throws SQLServerException, GeneralSecurityException {
        ByteBuffer enclavePKBuffer = ByteBuffer.wrap(this.enclavePK).order(ByteOrder.LITTLE_ENDIAN);
        byte[] rsa1 = new byte[4];
        enclavePKBuffer.get(rsa1);
        enclavePKBuffer.getInt();
        int publicExponentLength = enclavePKBuffer.getInt();
        int publicModulusLength = enclavePKBuffer.getInt();
        enclavePKBuffer.getInt();
        enclavePKBuffer.getInt();
        byte[] exponent = new byte[publicExponentLength];
        enclavePKBuffer.get(exponent);
        byte[] modulus = new byte[publicModulusLength];
        enclavePKBuffer.get(modulus);
        if (enclavePKBuffer.remaining() != 0) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclavePKLengthError"), "0", false);
        }
        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(1, modulus), new BigInteger(1, exponent));
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey pub = factory.generatePublic(spec);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(pub);
        sig.update(this.dhPublicKey);
        if (!sig.verify(this.publicKeySig)) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_InvalidDHKeySignature"), "0", false);
        }
    }

    byte[] getDHpublicKey() {
        return this.dhPublicKey;
    }

    byte[] getSessionID() {
        return this.sessionID;
    }
}

