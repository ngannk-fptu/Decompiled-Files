/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime;

public final class Operators {
    public static final int EQ = 0;
    public static final int NE = 1;
    public static final int GT = 2;
    public static final int LT = 3;
    public static final int GE = 4;
    public static final int LE = 5;
    private static final String[] names = new String[]{"=", "!=", ">", "<", ">=", "<="};
    private static final int[] swapOpArray = new int[]{0, 1, 3, 2, 5, 4};

    public static final String getOpNames(int operator) {
        return names[operator];
    }

    public static final int swapOp(int operator) {
        return swapOpArray[operator];
    }
}

