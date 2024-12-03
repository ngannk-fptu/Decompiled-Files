/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseAttestationRequest;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

class AASAttestationParameters
extends BaseAttestationRequest {
    private static final byte[] ENCLAVE_TYPE = new byte[]{1, 0, 0, 0};
    private static final byte[] NONCE_LENGTH = new byte[]{0, 1, 0, 0};
    private byte[] nonce = new byte[256];

    AASAttestationParameters(String attestationUrl) throws SQLServerException, IOException {
        byte[] attestationUrlBytes = (attestationUrl + "\u0000").getBytes(StandardCharsets.UTF_16LE);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(attestationUrlBytes.length).array());
        os.write(attestationUrlBytes);
        os.write(NONCE_LENGTH);
        new SecureRandom().nextBytes(this.nonce);
        os.write(this.nonce);
        this.enclaveChallenge = os.toByteArray();
        this.initBcryptECDH();
    }

    @Override
    byte[] getBytes() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(ENCLAVE_TYPE);
        os.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.enclaveChallenge.length).array());
        os.write(this.enclaveChallenge);
        os.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(104).array());
        os.write(ECDH_MAGIC);
        os.write(this.x);
        os.write(this.y);
        return os.toByteArray();
    }

    byte[] getNonce() {
        return this.nonce;
    }
}

