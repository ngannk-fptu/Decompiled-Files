/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.util.Set;
import java.util.TreeSet;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.general.AbstractDataset;

public class WaferMapDataset
extends AbstractDataset {
    private DefaultKeyedValues2D data;
    private int maxChipX;
    private int maxChipY;
    private double chipSpace;
    private Double maxValue = new Double(Double.NEGATIVE_INFINITY);
    private Double minValue = new Double(Double.POSITIVE_INFINITY);
    private static final double DEFAULT_CHIP_SPACE = 1.0;

    public WaferMapDataset(int maxChipX, int maxChipY) {
        this(maxChipX, maxChipY, null);
    }

    public WaferMapDataset(int maxChipX, int maxChipY, Number chipSpace) {
        this.data = new DefaultKeyedValues2D();
        this.maxChipX = maxChipX;
        this.maxChipY = maxChipY;
        this.chipSpace = chipSpace == null ? 1.0 : chipSpace.doubleValue();
    }

    public void addValue(Number value, Comparable chipx, Comparable chipy) {
        this.setValue(value, chipx, chipy);
    }

    public void addValue(int v, int x, int y) {
        this.setValue(new Double(v), new Integer(x), new Integer(y));
    }

    public void setValue(Number value, Comparable chipx, Comparable chipy) {
        this.data.setValue(value, chipx, chipy);
        if (this.isMaxValue(value)) {
            this.maxValue = (Double)value;
        }
        if (this.isMinValue(value)) {
            this.minValue = (Double)value;
        }
    }

    public int getUniqueValueCount() {
        return this.getUniqueValues().size();
    }

    public Set getUniqueValues() {
        TreeSet<Number> unique = new TreeSet<Number>();
        for (int r = 0; r < this.data.getRowCount(); ++r) {
            for (int c = 0; c < this.data.getColumnCount(); ++c) {
                Number value = this.data.getValue(r, c);
                if (value == null) continue;
                unique.add(value);
            }
        }
        return unique;
    }

    public Number getChipValue(int chipx, int chipy) {
        return this.getChipValue(new Integer(chipx), new Integer(chipy));
    }

    public Number getChipValue(Comparable chipx, Comparable chipy) {
        int rowIndex = this.data.getRowIndex(chipx);
        if (rowIndex < 0) {
            return null;
        }
        int colIndex = this.data.getColumnIndex(chipy);
        if (colIndex < 0) {
            return null;
        }
        return this.data.getValue(rowIndex, colIndex);
    }

    public boolean isMaxValue(Number check) {
        return check.doubleValue() > this.maxValue;
    }

    public boolean isMinValue(Number check) {
        return check.doubleValue() < this.minValue;
    }

    public Number getMaxValue() {
        return this.maxValue;
    }

    public Number getMinValue() {
        return this.minValue;
    }

    public int getMaxChipX() {
        return this.maxChipX;
    }

    public void setMaxChipX(int maxChipX) {
        this.maxChipX = maxChipX;
    }

    public int getMaxChipY() {
        return this.maxChipY;
    }

    public void setMaxChipY(int maxChipY) {
        this.maxChipY = maxChipY;
    }

    public double getChipSpace() {
        return this.chipSpace;
    }

    public void setChipSpace(double space) {
        this.chipSpace = space;
    }
}

