/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.EncryptionMaterialsAccessor;
import com.amazonaws.services.s3.model.EncryptionMaterialsFactory;

public interface EncryptionMaterialsProvider
extends EncryptionMaterialsAccessor,
EncryptionMaterialsFactory {
    public void refresh();
}

