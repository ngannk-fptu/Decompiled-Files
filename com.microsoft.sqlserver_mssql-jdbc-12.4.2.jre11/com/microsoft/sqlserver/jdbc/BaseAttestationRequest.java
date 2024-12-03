/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;

abstract class BaseAttestationRequest {
    protected static final byte[] ECDH_MAGIC = new byte[]{69, 67, 75, 51, 48, 0, 0, 0};
    protected static final int ENCLAVE_LENGTH = 104;
    protected static final int BIG_INTEGER_SIZE = 48;
    protected PrivateKey privateKey;
    protected byte[] enclaveChallenge;
    protected byte[] x;
    protected byte[] y;

    BaseAttestationRequest() {
    }

    byte[] getBytes() throws IOException {
        return null;
    }

    byte[] createSessionSecret(byte[] serverResponse) throws GeneralSecurityException, SQLServerException {
        if (serverResponse == null || serverResponse.length != 104) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_MalformedECDHPublicKey"), "0", false);
        }
        ByteBuffer sr = ByteBuffer.wrap(serverResponse);
        byte[] magic = new byte[8];
        sr.get(magic);
        if (!Arrays.equals(magic, ECDH_MAGIC)) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_MalformedECDHHeader"), "0", false);
        }
        byte[] xx = new byte[48];
        byte[] yy = new byte[48];
        sr.get(xx);
        sr.get(yy);
        ECPublicKeySpec keySpec = new ECPublicKeySpec(new ECPoint(new BigInteger(1, xx), new BigInteger(1, yy)), ((ECPrivateKey)this.privateKey).getParams());
        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(this.privateKey);
        ka.doPhase(KeyFactory.getInstance("EC").generatePublic(keySpec), true);
        return MessageDigest.getInstance("SHA-256").digest(ka.generateSecret());
    }

    void initBcryptECDH() throws SQLServerException {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(new ECGenParameterSpec("secp384r1"));
            KeyPair kp = kpg.generateKeyPair();
            ECPublicKey publicKey = (ECPublicKey)kp.getPublic();
            this.privateKey = kp.getPrivate();
            ECPoint w = publicKey.getW();
            this.x = this.adjustBigInt(w.getAffineX().toByteArray());
            this.y = this.adjustBigInt(w.getAffineY().toByteArray());
        }
        catch (IOException | GeneralSecurityException e) {
            SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
        }
    }

    private byte[] adjustBigInt(byte[] b) throws IOException {
        if (0 == b[0] && 48 < b.length) {
            b = Arrays.copyOfRange(b, 1, b.length);
        }
        if (b.length < 48) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (int i = 0; i < 48 - b.length; ++i) {
                output.write(0);
            }
            output.write(b);
            b = output.toByteArray();
        }
        return b;
    }
}

