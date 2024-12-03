/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.hpsf.UnsupportedVariantTypeException;

public class ReadingNotSupportedException
extends UnsupportedVariantTypeException {
    public ReadingNotSupportedException(long variantType, Object value) {
        super(variantType, value);
    }
}

