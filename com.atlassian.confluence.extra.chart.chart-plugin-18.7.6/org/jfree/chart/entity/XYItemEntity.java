/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.entity;

import java.awt.Shape;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.data.xy.XYDataset;

public class XYItemEntity
extends ChartEntity {
    private static final long serialVersionUID = -3870862224880283771L;
    private transient XYDataset dataset;
    private int series;
    private int item;

    public XYItemEntity(Shape area, XYDataset dataset, int series, int item, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        this.dataset = dataset;
        this.series = series;
        this.item = item;
    }

    public XYDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(XYDataset dataset) {
        this.dataset = dataset;
    }

    public int getSeriesIndex() {
        return this.series;
    }

    public void setSeriesIndex(int series) {
        this.series = series;
    }

    public int getItem() {
        return this.item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof XYItemEntity && super.equals(obj)) {
            XYItemEntity ie = (XYItemEntity)obj;
            if (this.series != ie.series) {
                return false;
            }
            return this.item == ie.item;
        }
        return false;
    }

    public String toString() {
        return "XYItemEntity: series = " + this.getSeriesIndex() + ", item = " + this.getItem() + ", dataset = " + this.getDataset();
    }
}

