/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;

public interface XDDFCategoryDataSource
extends XDDFDataSource<String> {
    @Override
    default public int getColIndex() {
        return 0;
    }

    @Override
    default public boolean isLiteral() {
        return false;
    }

    @Override
    default public boolean isNumeric() {
        return false;
    }

    @Override
    default public boolean isReference() {
        return true;
    }
}

