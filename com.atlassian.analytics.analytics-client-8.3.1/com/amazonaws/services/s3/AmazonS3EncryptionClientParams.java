/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.s3.AmazonS3ClientParams;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;

@Deprecated
abstract class AmazonS3EncryptionClientParams
extends AmazonS3ClientParams {
    AmazonS3EncryptionClientParams() {
    }

    abstract EncryptionMaterialsProvider getEncryptionMaterials();

    abstract CryptoConfiguration getCryptoConfiguration();

    abstract AWSKMS getKmsClient();
}

