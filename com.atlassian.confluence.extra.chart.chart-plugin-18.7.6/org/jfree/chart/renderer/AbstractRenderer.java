/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintList;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeList;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.StrokeList;

public abstract class AbstractRenderer
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -828267569428206075L;
    public static final Double ZERO = new Double(0.0);
    public static final Paint DEFAULT_PAINT = Color.blue;
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1.0f);
    public static final Shape DEFAULT_SHAPE = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);
    public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", 0, 10);
    public static final Paint DEFAULT_VALUE_LABEL_PAINT = Color.black;
    private BooleanList seriesVisibleList = new BooleanList();
    private boolean baseSeriesVisible = true;
    private BooleanList seriesVisibleInLegendList = new BooleanList();
    private boolean baseSeriesVisibleInLegend = true;
    private PaintList paintList = new PaintList();
    private boolean autoPopulateSeriesPaint = true;
    private transient Paint basePaint = DEFAULT_PAINT;
    private PaintList fillPaintList = new PaintList();
    private boolean autoPopulateSeriesFillPaint = false;
    private transient Paint baseFillPaint = Color.white;
    private PaintList outlinePaintList = new PaintList();
    private boolean autoPopulateSeriesOutlinePaint = false;
    private transient Paint baseOutlinePaint = DEFAULT_OUTLINE_PAINT;
    private StrokeList strokeList = new StrokeList();
    private boolean autoPopulateSeriesStroke = true;
    private transient Stroke baseStroke = DEFAULT_STROKE;
    private StrokeList outlineStrokeList = new StrokeList();
    private transient Stroke baseOutlineStroke = DEFAULT_OUTLINE_STROKE;
    private boolean autoPopulateSeriesOutlineStroke = false;
    private ShapeList shapeList = new ShapeList();
    private boolean autoPopulateSeriesShape = true;
    private transient Shape baseShape = DEFAULT_SHAPE;
    private BooleanList itemLabelsVisibleList = new BooleanList();
    private Boolean baseItemLabelsVisible = Boolean.FALSE;
    private ObjectList itemLabelFontList = new ObjectList();
    private Font baseItemLabelFont = new Font("SansSerif", 0, 10);
    private PaintList itemLabelPaintList = new PaintList();
    private transient Paint baseItemLabelPaint = Color.black;
    private ObjectList positiveItemLabelPositionList = new ObjectList();
    private ItemLabelPosition basePositiveItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
    private ObjectList negativeItemLabelPositionList = new ObjectList();
    private ItemLabelPosition baseNegativeItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
    private double itemLabelAnchorOffset = 2.0;
    private BooleanList createEntitiesList = new BooleanList();
    private boolean baseCreateEntities = true;
    private ShapeList legendShape = new ShapeList();
    private transient Shape baseLegendShape = null;
    private ObjectList legendTextFont = new ObjectList();
    private Font baseLegendTextFont = null;
    private PaintList legendTextPaint = new PaintList();
    private transient Paint baseLegendTextPaint = null;
    private boolean dataBoundsIncludesVisibleSeriesOnly = true;
    private int defaultEntityRadius = 3;
    private transient EventListenerList listenerList = new EventListenerList();
    private transient RendererChangeEvent event;
    private static final double ADJ = Math.cos(0.5235987755982988);
    private static final double OPP = Math.sin(0.5235987755982988);
    private Boolean seriesVisible = null;
    private Boolean seriesVisibleInLegend = null;
    private transient Paint paint = null;
    private transient Paint fillPaint = null;
    private transient Paint outlinePaint = null;
    private transient Stroke stroke = null;
    private transient Stroke outlineStroke = null;
    private transient Shape shape = null;
    private Boolean itemLabelsVisible = null;
    private Font itemLabelFont = null;
    private transient Paint itemLabelPaint = null;
    private ItemLabelPosition positiveItemLabelPosition = null;
    private ItemLabelPosition negativeItemLabelPosition = null;
    private Boolean createEntities = null;
    static /* synthetic */ Class class$org$jfree$chart$event$RendererChangeListener;

    public abstract DrawingSupplier getDrawingSupplier();

    public boolean getItemVisible(int series, int item) {
        return this.isSeriesVisible(series);
    }

    public boolean isSeriesVisible(int series) {
        boolean result = this.baseSeriesVisible;
        if (this.seriesVisible != null) {
            result = this.seriesVisible;
        } else {
            Boolean b = this.seriesVisibleList.getBoolean(series);
            if (b != null) {
                result = b;
            }
        }
        return result;
    }

    public Boolean getSeriesVisible(int series) {
        return this.seriesVisibleList.getBoolean(series);
    }

    public void setSeriesVisible(int series, Boolean visible) {
        this.setSeriesVisible(series, visible, true);
    }

    public void setSeriesVisible(int series, Boolean visible, boolean notify) {
        this.seriesVisibleList.setBoolean(series, visible);
        if (notify) {
            RendererChangeEvent e = new RendererChangeEvent((Object)this, true);
            this.notifyListeners(e);
        }
    }

    public boolean getBaseSeriesVisible() {
        return this.baseSeriesVisible;
    }

    public void setBaseSeriesVisible(boolean visible) {
        this.setBaseSeriesVisible(visible, true);
    }

    public void setBaseSeriesVisible(boolean visible, boolean notify) {
        this.baseSeriesVisible = visible;
        if (notify) {
            RendererChangeEvent e = new RendererChangeEvent((Object)this, true);
            this.notifyListeners(e);
        }
    }

    public boolean isSeriesVisibleInLegend(int series) {
        boolean result = this.baseSeriesVisibleInLegend;
        if (this.seriesVisibleInLegend != null) {
            result = this.seriesVisibleInLegend;
        } else {
            Boolean b = this.seriesVisibleInLegendList.getBoolean(series);
            if (b != null) {
                result = b;
            }
        }
        return result;
    }

    public Boolean getSeriesVisibleInLegend(int series) {
        return this.seriesVisibleInLegendList.getBoolean(series);
    }

    public void setSeriesVisibleInLegend(int series, Boolean visible) {
        this.setSeriesVisibleInLegend(series, visible, true);
    }

    public void setSeriesVisibleInLegend(int series, Boolean visible, boolean notify) {
        this.seriesVisibleInLegendList.setBoolean(series, visible);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean getBaseSeriesVisibleInLegend() {
        return this.baseSeriesVisibleInLegend;
    }

    public void setBaseSeriesVisibleInLegend(boolean visible) {
        this.setBaseSeriesVisibleInLegend(visible, true);
    }

    public void setBaseSeriesVisibleInLegend(boolean visible, boolean notify) {
        this.baseSeriesVisibleInLegend = visible;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Paint getItemPaint(int row, int column) {
        return this.lookupSeriesPaint(row);
    }

    public Paint lookupSeriesPaint(int series) {
        DrawingSupplier supplier;
        if (this.paint != null) {
            return this.paint;
        }
        Paint seriesPaint = this.getSeriesPaint(series);
        if (seriesPaint == null && this.autoPopulateSeriesPaint && (supplier = this.getDrawingSupplier()) != null) {
            seriesPaint = supplier.getNextPaint();
            this.setSeriesPaint(series, seriesPaint, false);
        }
        if (seriesPaint == null) {
            seriesPaint = this.basePaint;
        }
        return seriesPaint;
    }

    public Paint getSeriesPaint(int series) {
        return this.paintList.getPaint(series);
    }

    public void setSeriesPaint(int series, Paint paint) {
        this.setSeriesPaint(series, paint, true);
    }

    public void setSeriesPaint(int series, Paint paint, boolean notify) {
        this.paintList.setPaint(series, paint);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void clearSeriesPaints(boolean notify) {
        this.paintList.clear();
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Paint getBasePaint() {
        return this.basePaint;
    }

    public void setBasePaint(Paint paint) {
        this.setBasePaint(paint, true);
    }

    public void setBasePaint(Paint paint, boolean notify) {
        this.basePaint = paint;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean getAutoPopulateSeriesPaint() {
        return this.autoPopulateSeriesPaint;
    }

    public void setAutoPopulateSeriesPaint(boolean auto) {
        this.autoPopulateSeriesPaint = auto;
    }

    public Paint getItemFillPaint(int row, int column) {
        return this.lookupSeriesFillPaint(row);
    }

    public Paint lookupSeriesFillPaint(int series) {
        DrawingSupplier supplier;
        if (this.fillPaint != null) {
            return this.fillPaint;
        }
        Paint seriesFillPaint = this.getSeriesFillPaint(series);
        if (seriesFillPaint == null && this.autoPopulateSeriesFillPaint && (supplier = this.getDrawingSupplier()) != null) {
            seriesFillPaint = supplier.getNextFillPaint();
            this.setSeriesFillPaint(series, seriesFillPaint, false);
        }
        if (seriesFillPaint == null) {
            seriesFillPaint = this.baseFillPaint;
        }
        return seriesFillPaint;
    }

    public Paint getSeriesFillPaint(int series) {
        return this.fillPaintList.getPaint(series);
    }

    public void setSeriesFillPaint(int series, Paint paint) {
        this.setSeriesFillPaint(series, paint, true);
    }

    public void setSeriesFillPaint(int series, Paint paint, boolean notify) {
        this.fillPaintList.setPaint(series, paint);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Paint getBaseFillPaint() {
        return this.baseFillPaint;
    }

    public void setBaseFillPaint(Paint paint) {
        this.setBaseFillPaint(paint, true);
    }

    public void setBaseFillPaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseFillPaint = paint;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean getAutoPopulateSeriesFillPaint() {
        return this.autoPopulateSeriesFillPaint;
    }

    public void setAutoPopulateSeriesFillPaint(boolean auto) {
        this.autoPopulateSeriesFillPaint = auto;
    }

    public Paint getItemOutlinePaint(int row, int column) {
        return this.lookupSeriesOutlinePaint(row);
    }

    public Paint lookupSeriesOutlinePaint(int series) {
        DrawingSupplier supplier;
        if (this.outlinePaint != null) {
            return this.outlinePaint;
        }
        Paint seriesOutlinePaint = this.getSeriesOutlinePaint(series);
        if (seriesOutlinePaint == null && this.autoPopulateSeriesOutlinePaint && (supplier = this.getDrawingSupplier()) != null) {
            seriesOutlinePaint = supplier.getNextOutlinePaint();
            this.setSeriesOutlinePaint(series, seriesOutlinePaint, false);
        }
        if (seriesOutlinePaint == null) {
            seriesOutlinePaint = this.baseOutlinePaint;
        }
        return seriesOutlinePaint;
    }

    public Paint getSeriesOutlinePaint(int series) {
        return this.outlinePaintList.getPaint(series);
    }

    public void setSeriesOutlinePaint(int series, Paint paint) {
        this.setSeriesOutlinePaint(series, paint, true);
    }

    public void setSeriesOutlinePaint(int series, Paint paint, boolean notify) {
        this.outlinePaintList.setPaint(series, paint);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Paint getBaseOutlinePaint() {
        return this.baseOutlinePaint;
    }

    public void setBaseOutlinePaint(Paint paint) {
        this.setBaseOutlinePaint(paint, true);
    }

    public void setBaseOutlinePaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseOutlinePaint = paint;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean getAutoPopulateSeriesOutlinePaint() {
        return this.autoPopulateSeriesOutlinePaint;
    }

    public void setAutoPopulateSeriesOutlinePaint(boolean auto) {
        this.autoPopulateSeriesOutlinePaint = auto;
    }

    public Stroke getItemStroke(int row, int column) {
        return this.lookupSeriesStroke(row);
    }

    public Stroke lookupSeriesStroke(int series) {
        DrawingSupplier supplier;
        if (this.stroke != null) {
            return this.stroke;
        }
        Stroke result = this.getSeriesStroke(series);
        if (result == null && this.autoPopulateSeriesStroke && (supplier = this.getDrawingSupplier()) != null) {
            result = supplier.getNextStroke();
            this.setSeriesStroke(series, result, false);
        }
        if (result == null) {
            result = this.baseStroke;
        }
        return result;
    }

    public Stroke getSeriesStroke(int series) {
        return this.strokeList.getStroke(series);
    }

    public void setSeriesStroke(int series, Stroke stroke) {
        this.setSeriesStroke(series, stroke, true);
    }

    public void setSeriesStroke(int series, Stroke stroke, boolean notify) {
        this.strokeList.setStroke(series, stroke);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void clearSeriesStrokes(boolean notify) {
        this.strokeList.clear();
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Stroke getBaseStroke() {
        return this.baseStroke;
    }

    public void setBaseStroke(Stroke stroke) {
        this.setBaseStroke(stroke, true);
    }

    public void setBaseStroke(Stroke stroke, boolean notify) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseStroke = stroke;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean getAutoPopulateSeriesStroke() {
        return this.autoPopulateSeriesStroke;
    }

    public void setAutoPopulateSeriesStroke(boolean auto) {
        this.autoPopulateSeriesStroke = auto;
    }

    public Stroke getItemOutlineStroke(int row, int column) {
        return this.lookupSeriesOutlineStroke(row);
    }

    public Stroke lookupSeriesOutlineStroke(int series) {
        DrawingSupplier supplier;
        if (this.outlineStroke != null) {
            return this.outlineStroke;
        }
        Stroke result = this.getSeriesOutlineStroke(series);
        if (result == null && this.autoPopulateSeriesOutlineStroke && (supplier = this.getDrawingSupplier()) != null) {
            result = supplier.getNextOutlineStroke();
            this.setSeriesOutlineStroke(series, result, false);
        }
        if (result == null) {
            result = this.baseOutlineStroke;
        }
        return result;
    }

    public Stroke getSeriesOutlineStroke(int series) {
        return this.outlineStrokeList.getStroke(series);
    }

    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        this.setSeriesOutlineStroke(series, stroke, true);
    }

    public void setSeriesOutlineStroke(int series, Stroke stroke, boolean notify) {
        this.outlineStrokeList.setStroke(series, stroke);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Stroke getBaseOutlineStroke() {
        return this.baseOutlineStroke;
    }

    public void setBaseOutlineStroke(Stroke stroke) {
        this.setBaseOutlineStroke(stroke, true);
    }

    public void setBaseOutlineStroke(Stroke stroke, boolean notify) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseOutlineStroke = stroke;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean getAutoPopulateSeriesOutlineStroke() {
        return this.autoPopulateSeriesOutlineStroke;
    }

    public void setAutoPopulateSeriesOutlineStroke(boolean auto) {
        this.autoPopulateSeriesOutlineStroke = auto;
    }

    public Shape getItemShape(int row, int column) {
        return this.lookupSeriesShape(row);
    }

    public Shape lookupSeriesShape(int series) {
        DrawingSupplier supplier;
        if (this.shape != null) {
            return this.shape;
        }
        Shape result = this.getSeriesShape(series);
        if (result == null && this.autoPopulateSeriesShape && (supplier = this.getDrawingSupplier()) != null) {
            result = supplier.getNextShape();
            this.setSeriesShape(series, result, false);
        }
        if (result == null) {
            result = this.baseShape;
        }
        return result;
    }

    public Shape getSeriesShape(int series) {
        return this.shapeList.getShape(series);
    }

    public void setSeriesShape(int series, Shape shape) {
        this.setSeriesShape(series, shape, true);
    }

    public void setSeriesShape(int series, Shape shape, boolean notify) {
        this.shapeList.setShape(series, shape);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Shape getBaseShape() {
        return this.baseShape;
    }

    public void setBaseShape(Shape shape) {
        this.setBaseShape(shape, true);
    }

    public void setBaseShape(Shape shape, boolean notify) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.baseShape = shape;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean getAutoPopulateSeriesShape() {
        return this.autoPopulateSeriesShape;
    }

    public void setAutoPopulateSeriesShape(boolean auto) {
        this.autoPopulateSeriesShape = auto;
    }

    public boolean isItemLabelVisible(int row, int column) {
        return this.isSeriesItemLabelsVisible(row);
    }

    public boolean isSeriesItemLabelsVisible(int series) {
        if (this.itemLabelsVisible != null) {
            return this.itemLabelsVisible;
        }
        Boolean b = this.itemLabelsVisibleList.getBoolean(series);
        if (b == null) {
            b = this.baseItemLabelsVisible;
        }
        if (b == null) {
            b = Boolean.FALSE;
        }
        return b;
    }

    public void setSeriesItemLabelsVisible(int series, boolean visible) {
        this.setSeriesItemLabelsVisible(series, BooleanUtilities.valueOf(visible));
    }

    public void setSeriesItemLabelsVisible(int series, Boolean visible) {
        this.setSeriesItemLabelsVisible(series, visible, true);
    }

    public void setSeriesItemLabelsVisible(int series, Boolean visible, boolean notify) {
        this.itemLabelsVisibleList.setBoolean(series, visible);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Boolean getBaseItemLabelsVisible() {
        return this.baseItemLabelsVisible;
    }

    public void setBaseItemLabelsVisible(boolean visible) {
        this.setBaseItemLabelsVisible(BooleanUtilities.valueOf(visible));
    }

    public void setBaseItemLabelsVisible(Boolean visible) {
        this.setBaseItemLabelsVisible(visible, true);
    }

    public void setBaseItemLabelsVisible(Boolean visible, boolean notify) {
        this.baseItemLabelsVisible = visible;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Font getItemLabelFont(int row, int column) {
        Font result = this.itemLabelFont;
        if (result == null && (result = this.getSeriesItemLabelFont(row)) == null) {
            result = this.baseItemLabelFont;
        }
        return result;
    }

    public Font getSeriesItemLabelFont(int series) {
        return (Font)this.itemLabelFontList.get(series);
    }

    public void setSeriesItemLabelFont(int series, Font font) {
        this.setSeriesItemLabelFont(series, font, true);
    }

    public void setSeriesItemLabelFont(int series, Font font, boolean notify) {
        this.itemLabelFontList.set(series, font);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Font getBaseItemLabelFont() {
        return this.baseItemLabelFont;
    }

    public void setBaseItemLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.setBaseItemLabelFont(font, true);
    }

    public void setBaseItemLabelFont(Font font, boolean notify) {
        this.baseItemLabelFont = font;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Paint getItemLabelPaint(int row, int column) {
        Paint result = this.itemLabelPaint;
        if (result == null && (result = this.getSeriesItemLabelPaint(row)) == null) {
            result = this.baseItemLabelPaint;
        }
        return result;
    }

    public Paint getSeriesItemLabelPaint(int series) {
        return this.itemLabelPaintList.getPaint(series);
    }

    public void setSeriesItemLabelPaint(int series, Paint paint) {
        this.setSeriesItemLabelPaint(series, paint, true);
    }

    public void setSeriesItemLabelPaint(int series, Paint paint, boolean notify) {
        this.itemLabelPaintList.setPaint(series, paint);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Paint getBaseItemLabelPaint() {
        return this.baseItemLabelPaint;
    }

    public void setBaseItemLabelPaint(Paint paint) {
        this.setBaseItemLabelPaint(paint, true);
    }

    public void setBaseItemLabelPaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseItemLabelPaint = paint;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column) {
        return this.getSeriesPositiveItemLabelPosition(row);
    }

    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series) {
        if (this.positiveItemLabelPosition != null) {
            return this.positiveItemLabelPosition;
        }
        ItemLabelPosition position = (ItemLabelPosition)this.positiveItemLabelPositionList.get(series);
        if (position == null) {
            position = this.basePositiveItemLabelPosition;
        }
        return position;
    }

    public void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position) {
        this.setSeriesPositiveItemLabelPosition(series, position, true);
    }

    public void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position, boolean notify) {
        this.positiveItemLabelPositionList.set(series, position);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public ItemLabelPosition getBasePositiveItemLabelPosition() {
        return this.basePositiveItemLabelPosition;
    }

    public void setBasePositiveItemLabelPosition(ItemLabelPosition position) {
        this.setBasePositiveItemLabelPosition(position, true);
    }

    public void setBasePositiveItemLabelPosition(ItemLabelPosition position, boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.basePositiveItemLabelPosition = position;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column) {
        return this.getSeriesNegativeItemLabelPosition(row);
    }

    public ItemLabelPosition getSeriesNegativeItemLabelPosition(int series) {
        if (this.negativeItemLabelPosition != null) {
            return this.negativeItemLabelPosition;
        }
        ItemLabelPosition position = (ItemLabelPosition)this.negativeItemLabelPositionList.get(series);
        if (position == null) {
            position = this.baseNegativeItemLabelPosition;
        }
        return position;
    }

    public void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position) {
        this.setSeriesNegativeItemLabelPosition(series, position, true);
    }

    public void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position, boolean notify) {
        this.negativeItemLabelPositionList.set(series, position);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public ItemLabelPosition getBaseNegativeItemLabelPosition() {
        return this.baseNegativeItemLabelPosition;
    }

    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position) {
        this.setBaseNegativeItemLabelPosition(position, true);
    }

    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position, boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.baseNegativeItemLabelPosition = position;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public double getItemLabelAnchorOffset() {
        return this.itemLabelAnchorOffset;
    }

    public void setItemLabelAnchorOffset(double offset) {
        this.itemLabelAnchorOffset = offset;
        this.fireChangeEvent();
    }

    public boolean getItemCreateEntity(int series, int item) {
        if (this.createEntities != null) {
            return this.createEntities;
        }
        Boolean b = this.getSeriesCreateEntities(series);
        if (b != null) {
            return b;
        }
        return this.baseCreateEntities;
    }

    public Boolean getSeriesCreateEntities(int series) {
        return this.createEntitiesList.getBoolean(series);
    }

    public void setSeriesCreateEntities(int series, Boolean create) {
        this.setSeriesCreateEntities(series, create, true);
    }

    public void setSeriesCreateEntities(int series, Boolean create, boolean notify) {
        this.createEntitiesList.setBoolean(series, create);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean getBaseCreateEntities() {
        return this.baseCreateEntities;
    }

    public void setBaseCreateEntities(boolean create) {
        this.setBaseCreateEntities(create, true);
    }

    public void setBaseCreateEntities(boolean create, boolean notify) {
        this.baseCreateEntities = create;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public int getDefaultEntityRadius() {
        return this.defaultEntityRadius;
    }

    public void setDefaultEntityRadius(int radius) {
        this.defaultEntityRadius = radius;
    }

    public Shape lookupLegendShape(int series) {
        Shape result = this.getLegendShape(series);
        if (result == null) {
            result = this.baseLegendShape;
        }
        if (result == null) {
            result = this.lookupSeriesShape(series);
        }
        return result;
    }

    public Shape getLegendShape(int series) {
        return this.legendShape.getShape(series);
    }

    public void setLegendShape(int series, Shape shape) {
        this.legendShape.setShape(series, shape);
        this.fireChangeEvent();
    }

    public Shape getBaseLegendShape() {
        return this.baseLegendShape;
    }

    public void setBaseLegendShape(Shape shape) {
        this.baseLegendShape = shape;
        this.fireChangeEvent();
    }

    public Font lookupLegendTextFont(int series) {
        Font result = this.getLegendTextFont(series);
        if (result == null) {
            result = this.baseLegendTextFont;
        }
        return result;
    }

    public Font getLegendTextFont(int series) {
        return (Font)this.legendTextFont.get(series);
    }

    public void setLegendTextFont(int series, Font font) {
        this.legendTextFont.set(series, font);
        this.fireChangeEvent();
    }

    public Font getBaseLegendTextFont() {
        return this.baseLegendTextFont;
    }

    public void setBaseLegendTextFont(Font font) {
        this.baseLegendTextFont = font;
        this.fireChangeEvent();
    }

    public Paint lookupLegendTextPaint(int series) {
        Paint result = this.getLegendTextPaint(series);
        if (result == null) {
            result = this.baseLegendTextPaint;
        }
        return result;
    }

    public Paint getLegendTextPaint(int series) {
        return this.legendTextPaint.getPaint(series);
    }

    public void setLegendTextPaint(int series, Paint paint) {
        this.legendTextPaint.setPaint(series, paint);
        this.fireChangeEvent();
    }

    public Paint getBaseLegendTextPaint() {
        return this.baseLegendTextPaint;
    }

    public void setBaseLegendTextPaint(Paint paint) {
        this.baseLegendTextPaint = paint;
        this.fireChangeEvent();
    }

    public boolean getDataBoundsIncludesVisibleSeriesOnly() {
        return this.dataBoundsIncludesVisibleSeriesOnly;
    }

    public void setDataBoundsIncludesVisibleSeriesOnly(boolean visibleOnly) {
        this.dataBoundsIncludesVisibleSeriesOnly = visibleOnly;
        this.notifyListeners(new RendererChangeEvent((Object)this, true));
    }

    protected Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor, double x, double y, PlotOrientation orientation) {
        Point2D.Double result = null;
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x, y);
        } else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x + OPP * this.itemLabelAnchorOffset, y - ADJ * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x + ADJ * this.itemLabelAnchorOffset, y - OPP * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x + this.itemLabelAnchorOffset, y);
        } else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x + ADJ * this.itemLabelAnchorOffset, y + OPP * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x + OPP * this.itemLabelAnchorOffset, y + ADJ * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x, y + this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x - OPP * this.itemLabelAnchorOffset, y + ADJ * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x - ADJ * this.itemLabelAnchorOffset, y + OPP * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x - this.itemLabelAnchorOffset, y);
        } else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x - ADJ * this.itemLabelAnchorOffset, y - OPP * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x - OPP * this.itemLabelAnchorOffset, y - ADJ * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x, y - this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x + 2.0 * OPP * this.itemLabelAnchorOffset, y - 2.0 * ADJ * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x + 2.0 * ADJ * this.itemLabelAnchorOffset, y - 2.0 * OPP * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x + 2.0 * this.itemLabelAnchorOffset, y);
        } else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x + 2.0 * ADJ * this.itemLabelAnchorOffset, y + 2.0 * OPP * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x + 2.0 * OPP * this.itemLabelAnchorOffset, y + 2.0 * ADJ * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x, y + 2.0 * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x - 2.0 * OPP * this.itemLabelAnchorOffset, y + 2.0 * ADJ * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x - 2.0 * ADJ * this.itemLabelAnchorOffset, y + 2.0 * OPP * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x - 2.0 * this.itemLabelAnchorOffset, y);
        } else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x - 2.0 * ADJ * this.itemLabelAnchorOffset, y - 2.0 * OPP * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x - 2.0 * OPP * this.itemLabelAnchorOffset, y - 2.0 * ADJ * this.itemLabelAnchorOffset);
        } else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x, y - 2.0 * this.itemLabelAnchorOffset);
        }
        return result;
    }

    public void addChangeListener(RendererChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.listenerList.add(class$org$jfree$chart$event$RendererChangeListener == null ? (class$org$jfree$chart$event$RendererChangeListener = AbstractRenderer.class$("org.jfree.chart.event.RendererChangeListener")) : class$org$jfree$chart$event$RendererChangeListener, listener);
    }

    public void removeChangeListener(RendererChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.listenerList.remove(class$org$jfree$chart$event$RendererChangeListener == null ? (class$org$jfree$chart$event$RendererChangeListener = AbstractRenderer.class$("org.jfree.chart.event.RendererChangeListener")) : class$org$jfree$chart$event$RendererChangeListener, listener);
    }

    public boolean hasListener(EventListener listener) {
        List<Object> list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    protected void fireChangeEvent() {
        this.notifyListeners(new RendererChangeEvent(this));
    }

    public void notifyListeners(RendererChangeEvent event) {
        Object[] ls = this.listenerList.getListenerList();
        for (int i = ls.length - 2; i >= 0; i -= 2) {
            if (ls[i] != (class$org$jfree$chart$event$RendererChangeListener == null ? AbstractRenderer.class$("org.jfree.chart.event.RendererChangeListener") : class$org$jfree$chart$event$RendererChangeListener)) continue;
            ((RendererChangeListener)ls[i + 1]).rendererChanged(event);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractRenderer)) {
            return false;
        }
        AbstractRenderer that = (AbstractRenderer)obj;
        if (this.dataBoundsIncludesVisibleSeriesOnly != that.dataBoundsIncludesVisibleSeriesOnly) {
            return false;
        }
        if (this.defaultEntityRadius != that.defaultEntityRadius) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesVisible, that.seriesVisible)) {
            return false;
        }
        if (!this.seriesVisibleList.equals(that.seriesVisibleList)) {
            return false;
        }
        if (this.baseSeriesVisible != that.baseSeriesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesVisibleInLegend, that.seriesVisibleInLegend)) {
            return false;
        }
        if (!this.seriesVisibleInLegendList.equals(that.seriesVisibleInLegendList)) {
            return false;
        }
        if (this.baseSeriesVisibleInLegend != that.baseSeriesVisibleInLegend) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.paintList, that.paintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.basePaint, that.basePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.fillPaintList, that.fillPaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseFillPaint, that.baseFillPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlinePaintList, that.outlinePaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseOutlinePaint, that.baseOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.strokeList, that.strokeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseStroke, that.baseStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStrokeList, that.outlineStrokeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseOutlineStroke, that.baseOutlineStroke)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.shape, that.shape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapeList, that.shapeList)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.baseShape, that.baseShape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelsVisible, that.itemLabelsVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelsVisibleList, that.itemLabelsVisibleList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelsVisible, that.baseItemLabelsVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelFont, that.itemLabelFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelFontList, that.itemLabelFontList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelFont, that.baseItemLabelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.itemLabelPaint, that.itemLabelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelPaintList, that.itemLabelPaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseItemLabelPaint, that.baseItemLabelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.positiveItemLabelPosition, that.positiveItemLabelPosition)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.positiveItemLabelPositionList, that.positiveItemLabelPositionList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.basePositiveItemLabelPosition, that.basePositiveItemLabelPosition)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.negativeItemLabelPosition, that.negativeItemLabelPosition)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.negativeItemLabelPositionList, that.negativeItemLabelPositionList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseNegativeItemLabelPosition, that.baseNegativeItemLabelPosition)) {
            return false;
        }
        if (this.itemLabelAnchorOffset != that.itemLabelAnchorOffset) {
            return false;
        }
        if (!ObjectUtilities.equal(this.createEntities, that.createEntities)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.createEntitiesList, that.createEntitiesList)) {
            return false;
        }
        if (this.baseCreateEntities != that.baseCreateEntities) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendShape, that.legendShape)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.baseLegendShape, that.baseLegendShape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendTextFont, that.legendTextFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseLegendTextFont, that.baseLegendTextFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendTextPaint, that.legendTextPaint)) {
            return false;
        }
        return PaintUtilities.equal(this.baseLegendTextPaint, that.baseLegendTextPaint);
    }

    public int hashCode() {
        int result = 193;
        result = HashUtilities.hashCode(result, this.seriesVisibleList);
        result = HashUtilities.hashCode(result, this.baseSeriesVisible);
        result = HashUtilities.hashCode(result, this.seriesVisibleInLegendList);
        result = HashUtilities.hashCode(result, this.baseSeriesVisibleInLegend);
        result = HashUtilities.hashCode(result, this.paintList);
        result = HashUtilities.hashCode(result, this.basePaint);
        result = HashUtilities.hashCode(result, this.fillPaintList);
        result = HashUtilities.hashCode(result, this.baseFillPaint);
        result = HashUtilities.hashCode(result, this.outlinePaintList);
        result = HashUtilities.hashCode(result, this.baseOutlinePaint);
        result = HashUtilities.hashCode(result, this.strokeList);
        result = HashUtilities.hashCode(result, this.baseStroke);
        result = HashUtilities.hashCode(result, this.outlineStrokeList);
        result = HashUtilities.hashCode(result, this.baseOutlineStroke);
        result = HashUtilities.hashCode(result, this.itemLabelsVisibleList);
        result = HashUtilities.hashCode(result, this.baseItemLabelsVisible);
        return result;
    }

    protected Object clone() throws CloneNotSupportedException {
        AbstractRenderer clone = (AbstractRenderer)super.clone();
        if (this.seriesVisibleList != null) {
            clone.seriesVisibleList = (BooleanList)this.seriesVisibleList.clone();
        }
        if (this.seriesVisibleInLegendList != null) {
            clone.seriesVisibleInLegendList = (BooleanList)this.seriesVisibleInLegendList.clone();
        }
        if (this.paintList != null) {
            clone.paintList = (PaintList)this.paintList.clone();
        }
        if (this.fillPaintList != null) {
            clone.fillPaintList = (PaintList)this.fillPaintList.clone();
        }
        if (this.outlinePaintList != null) {
            clone.outlinePaintList = (PaintList)this.outlinePaintList.clone();
        }
        if (this.strokeList != null) {
            clone.strokeList = (StrokeList)this.strokeList.clone();
        }
        if (this.outlineStrokeList != null) {
            clone.outlineStrokeList = (StrokeList)this.outlineStrokeList.clone();
        }
        if (this.shape != null) {
            clone.shape = ShapeUtilities.clone(this.shape);
        }
        if (this.shapeList != null) {
            clone.shapeList = (ShapeList)this.shapeList.clone();
        }
        if (this.baseShape != null) {
            clone.baseShape = ShapeUtilities.clone(this.baseShape);
        }
        if (this.itemLabelsVisibleList != null) {
            clone.itemLabelsVisibleList = (BooleanList)this.itemLabelsVisibleList.clone();
        }
        if (this.itemLabelFontList != null) {
            clone.itemLabelFontList = (ObjectList)this.itemLabelFontList.clone();
        }
        if (this.itemLabelPaintList != null) {
            clone.itemLabelPaintList = (PaintList)this.itemLabelPaintList.clone();
        }
        if (this.positiveItemLabelPositionList != null) {
            clone.positiveItemLabelPositionList = (ObjectList)this.positiveItemLabelPositionList.clone();
        }
        if (this.negativeItemLabelPositionList != null) {
            clone.negativeItemLabelPositionList = (ObjectList)this.negativeItemLabelPositionList.clone();
        }
        if (this.createEntitiesList != null) {
            clone.createEntitiesList = (BooleanList)this.createEntitiesList.clone();
        }
        if (this.legendShape != null) {
            clone.legendShape = (ShapeList)this.legendShape.clone();
        }
        if (this.legendTextFont != null) {
            clone.legendTextFont = (ObjectList)this.legendTextFont.clone();
        }
        if (this.legendTextPaint != null) {
            clone.legendTextPaint = (PaintList)this.legendTextPaint.clone();
        }
        clone.listenerList = new EventListenerList();
        clone.event = null;
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writePaint(this.basePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writePaint(this.baseFillPaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.baseOutlinePaint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writeStroke(this.baseStroke, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writeStroke(this.baseOutlineStroke, stream);
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writeShape(this.baseShape, stream);
        SerialUtilities.writePaint(this.itemLabelPaint, stream);
        SerialUtilities.writePaint(this.baseItemLabelPaint, stream);
        SerialUtilities.writeShape(this.baseLegendShape, stream);
        SerialUtilities.writePaint(this.baseLegendTextPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.basePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.baseFillPaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.baseOutlinePaint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
        this.baseStroke = SerialUtilities.readStroke(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.baseOutlineStroke = SerialUtilities.readStroke(stream);
        this.shape = SerialUtilities.readShape(stream);
        this.baseShape = SerialUtilities.readShape(stream);
        this.itemLabelPaint = SerialUtilities.readPaint(stream);
        this.baseItemLabelPaint = SerialUtilities.readPaint(stream);
        this.baseLegendShape = SerialUtilities.readShape(stream);
        this.baseLegendTextPaint = SerialUtilities.readPaint(stream);
        this.listenerList = new EventListenerList();
    }

    public Boolean getSeriesVisible() {
        return this.seriesVisible;
    }

    public void setSeriesVisible(Boolean visible) {
        this.setSeriesVisible(visible, true);
    }

    public void setSeriesVisible(Boolean visible, boolean notify) {
        this.seriesVisible = visible;
        if (notify) {
            RendererChangeEvent e = new RendererChangeEvent((Object)this, true);
            this.notifyListeners(e);
        }
    }

    public Boolean getSeriesVisibleInLegend() {
        return this.seriesVisibleInLegend;
    }

    public void setSeriesVisibleInLegend(Boolean visible) {
        this.setSeriesVisibleInLegend(visible, true);
    }

    public void setSeriesVisibleInLegend(Boolean visible, boolean notify) {
        this.seriesVisibleInLegend = visible;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setPaint(Paint paint) {
        this.setPaint(paint, true);
    }

    public void setPaint(Paint paint, boolean notify) {
        this.paint = paint;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setFillPaint(Paint paint) {
        this.setFillPaint(paint, true);
    }

    public void setFillPaint(Paint paint, boolean notify) {
        this.fillPaint = paint;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setOutlinePaint(Paint paint) {
        this.setOutlinePaint(paint, true);
    }

    public void setOutlinePaint(Paint paint, boolean notify) {
        this.outlinePaint = paint;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setStroke(Stroke stroke) {
        this.setStroke(stroke, true);
    }

    public void setStroke(Stroke stroke, boolean notify) {
        this.stroke = stroke;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setOutlineStroke(Stroke stroke) {
        this.setOutlineStroke(stroke, true);
    }

    public void setOutlineStroke(Stroke stroke, boolean notify) {
        this.outlineStroke = stroke;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setShape(Shape shape) {
        this.setShape(shape, true);
    }

    public void setShape(Shape shape, boolean notify) {
        this.shape = shape;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setItemLabelsVisible(boolean visible) {
        this.setItemLabelsVisible(BooleanUtilities.valueOf(visible));
    }

    public void setItemLabelsVisible(Boolean visible) {
        this.setItemLabelsVisible(visible, true);
    }

    public void setItemLabelsVisible(Boolean visible, boolean notify) {
        this.itemLabelsVisible = visible;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Font getItemLabelFont() {
        return this.itemLabelFont;
    }

    public void setItemLabelFont(Font font) {
        this.setItemLabelFont(font, true);
    }

    public void setItemLabelFont(Font font, boolean notify) {
        this.itemLabelFont = font;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Paint getItemLabelPaint() {
        return this.itemLabelPaint;
    }

    public void setItemLabelPaint(Paint paint) {
        this.setItemLabelPaint(paint, true);
    }

    public void setItemLabelPaint(Paint paint, boolean notify) {
        this.itemLabelPaint = paint;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public ItemLabelPosition getPositiveItemLabelPosition() {
        return this.positiveItemLabelPosition;
    }

    public void setPositiveItemLabelPosition(ItemLabelPosition position) {
        this.setPositiveItemLabelPosition(position, true);
    }

    public void setPositiveItemLabelPosition(ItemLabelPosition position, boolean notify) {
        this.positiveItemLabelPosition = position;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public ItemLabelPosition getNegativeItemLabelPosition() {
        return this.negativeItemLabelPosition;
    }

    public void setNegativeItemLabelPosition(ItemLabelPosition position) {
        this.setNegativeItemLabelPosition(position, true);
    }

    public void setNegativeItemLabelPosition(ItemLabelPosition position, boolean notify) {
        this.negativeItemLabelPosition = position;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Boolean getCreateEntities() {
        return this.createEntities;
    }

    public void setCreateEntities(Boolean create) {
        this.setCreateEntities(create, true);
    }

    public void setCreateEntities(Boolean create, boolean notify) {
        this.createEntities = create;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

