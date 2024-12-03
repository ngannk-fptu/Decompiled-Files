/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.entity;

import org.apache.hc.client5.http.entity.DecompressingEntity;
import org.apache.hc.client5.http.entity.DeflateInputStreamFactory;
import org.apache.hc.core5.http.HttpEntity;

public class DeflateDecompressingEntity
extends DecompressingEntity {
    public DeflateDecompressingEntity(HttpEntity entity) {
        super(entity, DeflateInputStreamFactory.getInstance());
    }
}

