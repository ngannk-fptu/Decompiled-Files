/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;

public interface XDDFNumericalDataSource<T extends Number>
extends XDDFDataSource<T> {
    public void setFormatCode(String var1);

    @Override
    default public boolean isLiteral() {
        return false;
    }
}

