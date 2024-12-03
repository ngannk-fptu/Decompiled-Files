/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

public interface IStabilityClassifier {
    public static final IStabilityClassifier TOTALLY_IMMUTABLE = (sheetIndex, rowIndex, columnIndex) -> true;

    public boolean isCellFinal(int var1, int var2, int var3);
}

