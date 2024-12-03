/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

public interface Pxg {
    public int getExternalWorkbookNumber();

    public String getSheetName();

    public void setSheetName(String var1);

    public String toFormulaString();
}

