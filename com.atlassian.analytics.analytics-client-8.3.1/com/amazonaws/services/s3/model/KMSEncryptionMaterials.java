/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.EncryptionMaterials;
import java.io.Serializable;
import java.security.KeyPair;
import javax.crypto.SecretKey;

public class KMSEncryptionMaterials
extends EncryptionMaterials
implements Serializable {
    public static final String CUSTOMER_MASTER_KEY_ID = "kms_cmk_id";

    public KMSEncryptionMaterials(String defaultCustomerMasterKeyId) {
        super(null, null);
        if (defaultCustomerMasterKeyId == null || defaultCustomerMasterKeyId.length() == 0) {
            throw new IllegalArgumentException("The default customer master key id must be specified");
        }
        this.addDescription(CUSTOMER_MASTER_KEY_ID, defaultCustomerMasterKeyId);
    }

    @Override
    public final KeyPair getKeyPair() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SecretKey getSymmetricKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isKMSEnabled() {
        return true;
    }

    @Override
    public String getCustomerMasterKeyId() {
        return this.getDescription(CUSTOMER_MASTER_KEY_ID);
    }

    public String toString() {
        return String.valueOf(this.getMaterialsDescription());
    }
}

