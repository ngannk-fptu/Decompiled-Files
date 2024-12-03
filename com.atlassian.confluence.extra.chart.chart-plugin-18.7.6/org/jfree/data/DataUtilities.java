/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data;

import java.util.Arrays;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.Values2D;

public abstract class DataUtilities {
    public static boolean equal(double[][] a, double[][] b) {
        if (a == null) {
            return b == null;
        }
        if (b == null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; ++i) {
            if (Arrays.equals(a[i], b[i])) continue;
            return false;
        }
        return true;
    }

    public static double[][] clone(double[][] source) {
        if (source == null) {
            throw new IllegalArgumentException("Null 'source' argument.");
        }
        double[][] clone = new double[source.length][];
        for (int i = 0; i < source.length; ++i) {
            if (source[i] == null) continue;
            double[] row = new double[source[i].length];
            System.arraycopy(source[i], 0, row, 0, source[i].length);
            clone[i] = row;
        }
        return clone;
    }

    public static double calculateColumnTotal(Values2D data, int column) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        double total = 0.0;
        int rowCount = data.getRowCount();
        for (int r = 0; r < rowCount; ++r) {
            Number n = data.getValue(r, column);
            if (n == null) continue;
            total += n.doubleValue();
        }
        return total;
    }

    public static double calculateColumnTotal(Values2D data, int column, int[] validRows) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        double total = 0.0;
        int rowCount = data.getRowCount();
        for (int v = 0; v < validRows.length; ++v) {
            Number n;
            int row = validRows[v];
            if (row >= rowCount || (n = data.getValue(row, column)) == null) continue;
            total += n.doubleValue();
        }
        return total;
    }

    public static double calculateRowTotal(Values2D data, int row) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        double total = 0.0;
        int columnCount = data.getColumnCount();
        for (int c = 0; c < columnCount; ++c) {
            Number n = data.getValue(row, c);
            if (n == null) continue;
            total += n.doubleValue();
        }
        return total;
    }

    public static double calculateRowTotal(Values2D data, int row, int[] validCols) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        double total = 0.0;
        int colCount = data.getColumnCount();
        for (int v = 0; v < validCols.length; ++v) {
            Number n;
            int col = validCols[v];
            if (col >= colCount || (n = data.getValue(row, col)) == null) continue;
            total += n.doubleValue();
        }
        return total;
    }

    public static Number[] createNumberArray(double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        Number[] result = new Number[data.length];
        for (int i = 0; i < data.length; ++i) {
            result[i] = new Double(data[i]);
        }
        return result;
    }

    public static Number[][] createNumberArray2D(double[][] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        int l1 = data.length;
        Number[][] result = new Number[l1][];
        for (int i = 0; i < l1; ++i) {
            result[i] = DataUtilities.createNumberArray(data[i]);
        }
        return result;
    }

    public static KeyedValues getCumulativePercentages(KeyedValues data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        DefaultKeyedValues result = new DefaultKeyedValues();
        double total = 0.0;
        for (int i = 0; i < data.getItemCount(); ++i) {
            Number v = data.getValue(i);
            if (v == null) continue;
            total += v.doubleValue();
        }
        double runningTotal = 0.0;
        for (int i = 0; i < data.getItemCount(); ++i) {
            Number v = data.getValue(i);
            if (v != null) {
                runningTotal += v.doubleValue();
            }
            result.addValue(data.getKey(i), new Double(runningTotal / total));
        }
        return result;
    }
}

