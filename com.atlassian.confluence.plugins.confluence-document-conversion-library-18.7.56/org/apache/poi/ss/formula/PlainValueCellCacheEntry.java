/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.CellCacheEntry;
import org.apache.poi.ss.formula.eval.ValueEval;

final class PlainValueCellCacheEntry
extends CellCacheEntry {
    public PlainValueCellCacheEntry(ValueEval value) {
        this.updateValue(value);
    }
}

