/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.hpsf.HPSFException;

public abstract class VariantTypeException
extends HPSFException {
    private Object value;
    private long variantType;

    public VariantTypeException(long variantType, Object value, String msg) {
        super(msg);
        this.variantType = variantType;
        this.value = value;
    }

    public long getVariantType() {
        return this.variantType;
    }

    public Object getValue() {
        return this.value;
    }
}

