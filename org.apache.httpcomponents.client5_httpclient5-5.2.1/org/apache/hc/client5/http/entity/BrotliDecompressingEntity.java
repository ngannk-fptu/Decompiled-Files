/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpEntity
 */
package org.apache.hc.client5.http.entity;

import org.apache.hc.client5.http.entity.BrotliInputStreamFactory;
import org.apache.hc.client5.http.entity.DecompressingEntity;
import org.apache.hc.core5.http.HttpEntity;

public class BrotliDecompressingEntity
extends DecompressingEntity {
    public BrotliDecompressingEntity(HttpEntity entity) {
        super(entity, BrotliInputStreamFactory.getInstance());
    }

    public static boolean isAvailable() {
        try {
            Class.forName("org.brotli.dec.BrotliInputStream");
            return true;
        }
        catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }
}

