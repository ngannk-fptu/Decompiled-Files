/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseAttestationResponse;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class NoneAttestationResponse
extends BaseAttestationResponse {
    NoneAttestationResponse(byte[] b) throws SQLServerException {
        ByteBuffer response;
        ByteBuffer byteBuffer = response = null != b ? ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN) : null;
        if (null != response) {
            this.totalSize = response.getInt();
            this.sessionInfoSize = response.getInt();
            response.get(this.sessionID, 0, 8);
            this.dhpkSize = response.getInt();
            this.dhpkSsize = response.getInt();
            this.dhPublicKey = new byte[this.dhpkSize];
            this.publicKeySig = new byte[this.dhpkSsize];
            response.get(this.dhPublicKey, 0, this.dhpkSize);
            response.get(this.publicKeySig, 0, this.dhpkSsize);
        }
        if (null == response || 0 != response.remaining()) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclaveResponseLengthError"), "0", false);
        }
    }
}

