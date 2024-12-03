/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class Optional
extends ConvertNullTo {
    public Optional() {
        super((Object)null);
    }

    public Optional(CellProcessor next) {
        super(null, next);
    }
}

