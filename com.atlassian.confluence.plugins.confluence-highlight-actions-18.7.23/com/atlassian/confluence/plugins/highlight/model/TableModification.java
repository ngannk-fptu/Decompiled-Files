/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.model;

import com.atlassian.confluence.plugins.highlight.model.CellModification;
import java.util.List;

public class TableModification {
    private int tableColumnIndex;
    private List<CellModification> cellModifications;

    public TableModification(int tableColumnIndex, List<CellModification> cellModifications) {
        this.tableColumnIndex = tableColumnIndex;
        this.cellModifications = cellModifications;
    }

    public int getTableColumnIndex() {
        return this.tableColumnIndex;
    }

    public List<CellModification> getCellModifications() {
        return this.cellModifications;
    }
}

