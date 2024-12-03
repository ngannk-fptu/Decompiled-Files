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

class VSMAttestationParameters
extends BaseAttestationRequest {
    private static final byte[] ENCLAVE_TYPE = new byte[]{3, 0, 0, 0};

    VSMAttestationParameters() throws SQLServerException {
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

