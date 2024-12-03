/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.data.general.HeatMapDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public abstract class HeatMapUtilities {
    public static XYDataset extractRowFromHeatMapDataset(HeatMapDataset dataset, int row, Comparable seriesName) {
        XYSeries series = new XYSeries(seriesName);
        int cols = dataset.getXSampleCount();
        for (int c = 0; c < cols; ++c) {
            series.add(dataset.getXValue(c), dataset.getZValue(c, row));
        }
        XYSeriesCollection result = new XYSeriesCollection(series);
        return result;
    }

    public static XYDataset extractColumnFromHeatMapDataset(HeatMapDataset dataset, int column, Comparable seriesName) {
        XYSeries series = new XYSeries(seriesName);
        int rows = dataset.getYSampleCount();
        for (int r = 0; r < rows; ++r) {
            series.add(dataset.getYValue(r), dataset.getZValue(column, r));
        }
        XYSeriesCollection result = new XYSeriesCollection(series);
        return result;
    }

    public static BufferedImage createHeatMapImage(HeatMapDataset dataset, PaintScale paintScale) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (paintScale == null) {
            throw new IllegalArgumentException("Null 'paintScale' argument.");
        }
        int xCount = dataset.getXSampleCount();
        int yCount = dataset.getYSampleCount();
        BufferedImage image = new BufferedImage(xCount, yCount, 2);
        Graphics2D g2 = image.createGraphics();
        for (int xIndex = 0; xIndex < xCount; ++xIndex) {
            for (int yIndex = 0; yIndex < yCount; ++yIndex) {
                double z = dataset.getZValue(xIndex, yIndex);
                Paint p = paintScale.getPaint(z);
                g2.setPaint(p);
                g2.fillRect(xIndex, yCount - yIndex - 1, 1, 1);
            }
        }
        return image;
    }
}

