/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.WaferMapPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.general.WaferMapDataset;

public class WaferMapRenderer
extends AbstractRenderer {
    private Map paintIndex = new HashMap();
    private WaferMapPlot plot;
    private int paintLimit;
    private static final int DEFAULT_PAINT_LIMIT = 35;
    public static final int POSITION_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    private int paintIndexMethod;

    public WaferMapRenderer() {
        this(null, null);
    }

    public WaferMapRenderer(int paintLimit, int paintIndexMethod) {
        this(new Integer(paintLimit), new Integer(paintIndexMethod));
    }

    public WaferMapRenderer(Integer paintLimit, Integer paintIndexMethod) {
        this.paintLimit = paintLimit == null ? 35 : paintLimit;
        this.paintIndexMethod = 1;
        if (paintIndexMethod != null && this.isMethodValid(paintIndexMethod)) {
            this.paintIndexMethod = paintIndexMethod;
        }
    }

    private boolean isMethodValid(int method) {
        switch (method) {
            case 0: {
                return true;
            }
            case 1: {
                return true;
            }
        }
        return false;
    }

    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        WaferMapPlot p = this.getPlot();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        return result;
    }

    public WaferMapPlot getPlot() {
        return this.plot;
    }

    public void setPlot(WaferMapPlot plot) {
        this.plot = plot;
        this.makePaintIndex();
    }

    public Paint getChipColor(Number value) {
        return this.getSeriesPaint(this.getPaintIndex(value));
    }

    private int getPaintIndex(Number value) {
        return (Integer)this.paintIndex.get(value);
    }

    private void makePaintIndex() {
        if (this.plot == null) {
            return;
        }
        WaferMapDataset data = this.plot.getDataset();
        Number dataMin = data.getMinValue();
        Number dataMax = data.getMaxValue();
        Set uniqueValues = data.getUniqueValues();
        if (uniqueValues.size() <= this.paintLimit) {
            int count = 0;
            Iterator i = uniqueValues.iterator();
            while (i.hasNext()) {
                this.paintIndex.put(i.next(), new Integer(count++));
            }
        } else {
            switch (this.paintIndexMethod) {
                case 0: {
                    this.makePositionIndex(uniqueValues);
                    break;
                }
                case 1: {
                    this.makeValueIndex(dataMax, dataMin, uniqueValues);
                    break;
                }
            }
        }
    }

    private void makePositionIndex(Set uniqueValues) {
        int valuesPerColor = (int)Math.ceil((double)uniqueValues.size() / (double)this.paintLimit);
        int count = 0;
        int paint = 0;
        Iterator i = uniqueValues.iterator();
        while (i.hasNext()) {
            this.paintIndex.put(i.next(), new Integer(paint));
            if (++count % valuesPerColor == 0) {
                ++paint;
            }
            if (paint <= this.paintLimit) continue;
            paint = this.paintLimit;
        }
    }

    private void makeValueIndex(Number max, Number min, Set uniqueValues) {
        double valueRange = max.doubleValue() - min.doubleValue();
        double valueStep = valueRange / (double)this.paintLimit;
        int paint = 0;
        double cutPoint = min.doubleValue() + valueStep;
        Iterator i = uniqueValues.iterator();
        while (i.hasNext()) {
            Number value = (Number)i.next();
            while (value.doubleValue() > cutPoint) {
                cutPoint += valueStep;
                if (++paint <= this.paintLimit) continue;
                paint = this.paintLimit;
            }
            this.paintIndex.put(value, new Integer(paint));
        }
    }

    public LegendItemCollection getLegendCollection() {
        LegendItemCollection result;
        block4: {
            result = new LegendItemCollection();
            if (this.paintIndex == null || this.paintIndex.size() <= 0) break block4;
            if (this.paintIndex.size() <= this.paintLimit) {
                Iterator i = this.paintIndex.entrySet().iterator();
                while (i.hasNext()) {
                    String label;
                    Map.Entry entry = i.next();
                    String description = label = entry.getKey().toString();
                    Rectangle2D.Double shape = new Rectangle2D.Double(1.0, 1.0, 1.0, 1.0);
                    Paint paint = this.lookupSeriesPaint((Integer)entry.getValue());
                    Color outlinePaint = Color.black;
                    Stroke outlineStroke = DEFAULT_STROKE;
                    result.add(new LegendItem(label, description, null, null, (Shape)shape, paint, outlineStroke, (Paint)outlinePaint));
                }
            } else {
                HashSet unique = new HashSet();
                Iterator i = this.paintIndex.entrySet().iterator();
                while (i.hasNext()) {
                    String label;
                    Map.Entry entry = i.next();
                    if (!unique.add(entry.getValue())) continue;
                    String description = label = this.getMinPaintValue((Integer)entry.getValue()).toString() + " - " + this.getMaxPaintValue((Integer)entry.getValue()).toString();
                    Rectangle2D.Double shape = new Rectangle2D.Double(1.0, 1.0, 1.0, 1.0);
                    Paint paint = this.getSeriesPaint((Integer)entry.getValue());
                    Color outlinePaint = Color.black;
                    Stroke outlineStroke = DEFAULT_STROKE;
                    result.add(new LegendItem(label, description, null, null, (Shape)shape, paint, outlineStroke, (Paint)outlinePaint));
                }
            }
        }
        return result;
    }

    private Number getMinPaintValue(Integer index) {
        double minValue = Double.POSITIVE_INFINITY;
        Iterator i = this.paintIndex.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            if (!((Integer)entry.getValue()).equals(index) || !(((Number)entry.getKey()).doubleValue() < minValue)) continue;
            minValue = ((Number)entry.getKey()).doubleValue();
        }
        return new Double(minValue);
    }

    private Number getMaxPaintValue(Integer index) {
        double maxValue = Double.NEGATIVE_INFINITY;
        Iterator i = this.paintIndex.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            if (!((Integer)entry.getValue()).equals(index) || !(((Number)entry.getKey()).doubleValue() > maxValue)) continue;
            maxValue = ((Number)entry.getKey()).doubleValue();
        }
        return new Double(maxValue);
    }
}

