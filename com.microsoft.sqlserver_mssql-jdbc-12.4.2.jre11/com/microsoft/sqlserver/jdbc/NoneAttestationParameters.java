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

class NoneAttestationParameters
extends BaseAttestationRequest {
    private static final byte[] ENCLAVE_TYPE = new byte[]{2, 0, 0, 0};

    NoneAttestationParameters() throws SQLServerException {
        this.enclaveChallenge = new byte[]{0, 0, 0, 0};
        this.initBcryptECDH();
    }

    @Override
    byte[] getBytes() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(ENCLAVE_TYPE);
        os.write(this.enclaveChallenge);
        os.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(104).array());
        os.write(ECDH_MAGIC);
        os.write(this.x);
        os.write(this.y);
        return os.toByteArray();
    }
}

