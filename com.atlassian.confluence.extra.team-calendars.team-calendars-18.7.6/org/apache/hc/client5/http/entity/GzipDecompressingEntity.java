/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.entity;

import org.apache.hc.client5.http.entity.DecompressingEntity;
import org.apache.hc.client5.http.entity.GZIPInputStreamFactory;
import org.apache.hc.core5.http.HttpEntity;

public class GzipDecompressingEntity
extends DecompressingEntity {
    public GzipDecompressingEntity(HttpEntity entity) {
        super(entity, GZIPInputStreamFactory.getInstance());
    }
}

