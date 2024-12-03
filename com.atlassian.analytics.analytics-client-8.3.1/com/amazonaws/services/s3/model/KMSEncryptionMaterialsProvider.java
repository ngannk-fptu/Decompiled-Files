/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.KMSEncryptionMaterials;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;
import java.io.Serializable;

public class KMSEncryptionMaterialsProvider
extends StaticEncryptionMaterialsProvider
implements Serializable {
    public KMSEncryptionMaterialsProvider(String defaultCustomerMasterKeyId) {
        this(new KMSEncryptionMaterials(defaultCustomerMasterKeyId));
    }

    public KMSEncryptionMaterialsProvider(KMSEncryptionMaterials materials) {
        super(materials);
    }
}

