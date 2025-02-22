/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.Arrays;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class Comparison
extends EnumeratedAttribute {
    private static final String[] VALUES = new String[]{"equal", "greater", "less", "ne", "ge", "le", "eq", "gt", "lt", "more"};
    public static final Comparison EQUAL = new Comparison("equal");
    public static final Comparison NOT_EQUAL = new Comparison("ne");
    public static final Comparison GREATER = new Comparison("greater");
    public static final Comparison LESS = new Comparison("less");
    public static final Comparison GREATER_EQUAL = new Comparison("ge");
    public static final Comparison LESS_EQUAL = new Comparison("le");
    private static final int[] EQUAL_INDEX = new int[]{0, 4, 5, 6};
    private static final int[] LESS_INDEX = new int[]{2, 3, 5, 8};
    private static final int[] GREATER_INDEX = new int[]{1, 3, 4, 7, 9};

    public Comparison() {
    }

    public Comparison(String value) {
        this.setValue(value);
    }

    @Override
    public String[] getValues() {
        return VALUES;
    }

    public boolean evaluate(int comparisonResult) {
        if (this.getIndex() == -1) {
            throw new BuildException("Comparison value not set.");
        }
        int[] i = comparisonResult < 0 ? LESS_INDEX : (comparisonResult > 0 ? GREATER_INDEX : EQUAL_INDEX);
        return Arrays.binarySearch(i, this.getIndex()) >= 0;
    }
}

