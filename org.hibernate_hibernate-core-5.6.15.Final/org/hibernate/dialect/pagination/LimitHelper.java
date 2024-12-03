/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.engine.spi.RowSelection;

public class LimitHelper {
    public static boolean hasMaxRows(RowSelection selection) {
        return selection != null && selection.getMaxRows() != null && selection.getMaxRows() > 0;
    }

    public static boolean useLimit(LimitHandler limitHandler, RowSelection selection) {
        return limitHandler.supportsLimit() && LimitHelper.hasMaxRows(selection);
    }

    public static boolean hasFirstRow(RowSelection selection) {
        return LimitHelper.getFirstRow(selection) > 0;
    }

    public static int getFirstRow(RowSelection selection) {
        return selection == null || selection.getFirstRow() == null ? 0 : selection.getFirstRow();
    }

    private LimitHelper() {
    }
}

