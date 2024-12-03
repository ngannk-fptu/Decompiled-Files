/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.rest;

import com.atlassian.confluence.plugins.highlight.model.CellModification;
import com.atlassian.confluence.plugins.highlight.rest.ModificationBean;
import java.util.List;

public class TableModificationBean
extends ModificationBean {
    private int tableColumnIndex;
    private List<CellModification> cellModifications;

    public int getTableColumnIndex() {
        return this.tableColumnIndex;
    }

    public void setTableColumnIndex(int tableColumnIndex) {
        this.tableColumnIndex = tableColumnIndex;
    }

    public List<CellModification> getCellModifications() {
        return this.cellModifications;
    }

    public void setCellModifications(List<CellModification> cellModifications) {
        this.cellModifications = cellModifications;
    }
}

