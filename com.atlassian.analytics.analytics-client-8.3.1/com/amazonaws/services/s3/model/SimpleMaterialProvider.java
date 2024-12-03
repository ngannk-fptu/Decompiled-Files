/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SimpleMaterialProvider
implements EncryptionMaterialsProvider,
Serializable {
    private final Map<Map<String, String>, EncryptionMaterials> map = new HashMap<Map<String, String>, EncryptionMaterials>();
    private EncryptionMaterials latest;

    @Override
    public EncryptionMaterials getEncryptionMaterials(Map<String, String> md) {
        return this.map.get(md);
    }

    @Override
    public EncryptionMaterials getEncryptionMaterials() {
        return this.latest;
    }

    public SimpleMaterialProvider addMaterial(EncryptionMaterials m) {
        this.map.put(m.getMaterialsDescription(), m);
        return this;
    }

    public SimpleMaterialProvider withLatest(EncryptionMaterials m) {
        this.latest = m;
        return this.addMaterial(this.latest);
    }

    public SimpleMaterialProvider removeMaterial(Map<String, String> md) {
        this.map.remove(md);
        return this;
    }

    public int size() {
        return this.map.size();
    }

    @Override
    public void refresh() {
    }
}

