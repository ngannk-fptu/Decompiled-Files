/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.util.cellwalk;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.cellwalk.CellWalkContext;

public interface CellHandler {
    public void onCell(Cell var1, CellWalkContext var2);
}

