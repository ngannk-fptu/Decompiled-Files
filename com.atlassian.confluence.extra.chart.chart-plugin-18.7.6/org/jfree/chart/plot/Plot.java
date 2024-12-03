/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.event.EventListenerList;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.event.MarkerChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.io.SerialUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.Align;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public abstract class Plot
implements AxisChangeListener,
DatasetChangeListener,
MarkerChangeListener,
LegendItemSource,
PublicCloneable,
Cloneable,
Serializable {
    private static final long serialVersionUID = -8831571430103671324L;
    public static final Number ZERO = new Integer(0);
    public static final RectangleInsets DEFAULT_INSETS = new RectangleInsets(4.0, 8.0, 4.0, 8.0);
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(0.5f);
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;
    public static final float DEFAULT_FOREGROUND_ALPHA = 1.0f;
    public static final float DEFAULT_BACKGROUND_ALPHA = 1.0f;
    public static final Paint DEFAULT_BACKGROUND_PAINT = Color.white;
    public static final int MINIMUM_WIDTH_TO_DRAW = 10;
    public static final int MINIMUM_HEIGHT_TO_DRAW = 10;
    public static final Shape DEFAULT_LEGEND_ITEM_BOX = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);
    public static final Shape DEFAULT_LEGEND_ITEM_CIRCLE = new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0);
    private Plot parent = null;
    private DatasetGroup datasetGroup;
    private String noDataMessage = null;
    private Font noDataMessageFont;
    private transient Paint noDataMessagePaint;
    private RectangleInsets insets = DEFAULT_INSETS;
    private boolean outlineVisible = true;
    private transient Stroke outlineStroke;
    private transient Paint outlinePaint;
    private transient Paint backgroundPaint = DEFAULT_BACKGROUND_PAINT;
    private transient Image backgroundImage = null;
    private int backgroundImageAlignment = 15;
    private float backgroundImageAlpha = 0.5f;
    private float foregroundAlpha = 1.0f;
    private float backgroundAlpha = 1.0f;
    private DrawingSupplier drawingSupplier;
    private transient EventListenerList listenerList;
    private boolean notify = true;
    static /* synthetic */ Class class$org$jfree$chart$event$PlotChangeListener;

    protected Plot() {
        this.outlineStroke = DEFAULT_OUTLINE_STROKE;
        this.outlinePaint = DEFAULT_OUTLINE_PAINT;
        this.noDataMessageFont = new Font("SansSerif", 0, 12);
        this.noDataMessagePaint = Color.black;
        this.drawingSupplier = new DefaultDrawingSupplier();
        this.listenerList = new EventListenerList();
    }

    public DatasetGroup getDatasetGroup() {
        return this.datasetGroup;
    }

    protected void setDatasetGroup(DatasetGroup group) {
        this.datasetGroup = group;
    }

    public String getNoDataMessage() {
        return this.noDataMessage;
    }

    public void setNoDataMessage(String message) {
        this.noDataMessage = message;
        this.fireChangeEvent();
    }

    public Font getNoDataMessageFont() {
        return this.noDataMessageFont;
    }

    public void setNoDataMessageFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.noDataMessageFont = font;
        this.fireChangeEvent();
    }

    public Paint getNoDataMessagePaint() {
        return this.noDataMessagePaint;
    }

    public void setNoDataMessagePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.noDataMessagePaint = paint;
        this.fireChangeEvent();
    }

    public abstract String getPlotType();

    public Plot getParent() {
        return this.parent;
    }

    public void setParent(Plot parent) {
        this.parent = parent;
    }

    public Plot getRootPlot() {
        Plot p = this.getParent();
        if (p == null) {
            return this;
        }
        return p.getRootPlot();
    }

    public boolean isSubplot() {
        return this.getParent() != null;
    }

    public RectangleInsets getInsets() {
        return this.insets;
    }

    public void setInsets(RectangleInsets insets) {
        this.setInsets(insets, true);
    }

    public void setInsets(RectangleInsets insets, boolean notify) {
        if (insets == null) {
            throw new IllegalArgumentException("Null 'insets' argument.");
        }
        if (!this.insets.equals(insets)) {
            this.insets = insets;
            if (notify) {
                this.fireChangeEvent();
            }
        }
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        if (paint == null) {
            if (this.backgroundPaint != null) {
                this.backgroundPaint = null;
                this.fireChangeEvent();
            }
        } else {
            if (this.backgroundPaint != null && this.backgroundPaint.equals(paint)) {
                return;
            }
            this.backgroundPaint = paint;
            this.fireChangeEvent();
        }
    }

    public float getBackgroundAlpha() {
        return this.backgroundAlpha;
    }

    public void setBackgroundAlpha(float alpha) {
        if (this.backgroundAlpha != alpha) {
            this.backgroundAlpha = alpha;
            this.fireChangeEvent();
        }
    }

    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        Plot p = this.getParent();
        result = p != null ? p.getDrawingSupplier() : this.drawingSupplier;
        return result;
    }

    public void setDrawingSupplier(DrawingSupplier supplier) {
        this.drawingSupplier = supplier;
        this.fireChangeEvent();
    }

    public void setDrawingSupplier(DrawingSupplier supplier, boolean notify) {
        this.drawingSupplier = supplier;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
        this.fireChangeEvent();
    }

    public int getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }

    public void setBackgroundImageAlignment(int alignment) {
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            this.fireChangeEvent();
        }
    }

    public float getBackgroundImageAlpha() {
        return this.backgroundImageAlpha;
    }

    public void setBackgroundImageAlpha(float alpha) {
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("The 'alpha' value must be in the range 0.0f to 1.0f.");
        }
        if (this.backgroundImageAlpha != alpha) {
            this.backgroundImageAlpha = alpha;
            this.fireChangeEvent();
        }
    }

    public boolean isOutlineVisible() {
        return this.outlineVisible;
    }

    public void setOutlineVisible(boolean visible) {
        this.outlineVisible = visible;
        this.fireChangeEvent();
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            if (this.outlineStroke != null) {
                this.outlineStroke = null;
                this.fireChangeEvent();
            }
        } else {
            if (this.outlineStroke != null && this.outlineStroke.equals(stroke)) {
                return;
            }
            this.outlineStroke = stroke;
            this.fireChangeEvent();
        }
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint paint) {
        if (paint == null) {
            if (this.outlinePaint != null) {
                this.outlinePaint = null;
                this.fireChangeEvent();
            }
        } else {
            if (this.outlinePaint != null && this.outlinePaint.equals(paint)) {
                return;
            }
            this.outlinePaint = paint;
            this.fireChangeEvent();
        }
    }

    public float getForegroundAlpha() {
        return this.foregroundAlpha;
    }

    public void setForegroundAlpha(float alpha) {
        if (this.foregroundAlpha != alpha) {
            this.foregroundAlpha = alpha;
            this.fireChangeEvent();
        }
    }

    public LegendItemCollection getLegendItems() {
        return null;
    }

    public boolean isNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
        if (notify) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }

    public void addChangeListener(PlotChangeListener listener) {
        this.listenerList.add(class$org$jfree$chart$event$PlotChangeListener == null ? (class$org$jfree$chart$event$PlotChangeListener = Plot.class$("org.jfree.chart.event.PlotChangeListener")) : class$org$jfree$chart$event$PlotChangeListener, listener);
    }

    public void removeChangeListener(PlotChangeListener listener) {
        this.listenerList.remove(class$org$jfree$chart$event$PlotChangeListener == null ? (class$org$jfree$chart$event$PlotChangeListener = Plot.class$("org.jfree.chart.event.PlotChangeListener")) : class$org$jfree$chart$event$PlotChangeListener, listener);
    }

    public void notifyListeners(PlotChangeEvent event) {
        if (!this.notify) {
            return;
        }
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$org$jfree$chart$event$PlotChangeListener == null ? Plot.class$("org.jfree.chart.event.PlotChangeListener") : class$org$jfree$chart$event$PlotChangeListener)) continue;
            ((PlotChangeListener)listeners[i + 1]).plotChanged(event);
        }
    }

    protected void fireChangeEvent() {
        this.notifyListeners(new PlotChangeEvent(this));
    }

    public abstract void draw(Graphics2D var1, Rectangle2D var2, Point2D var3, PlotState var4, PlotRenderingInfo var5);

    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        this.fillBackground(g2, area);
        this.drawBackgroundImage(g2, area);
    }

    protected void fillBackground(Graphics2D g2, Rectangle2D area) {
        this.fillBackground(g2, area, PlotOrientation.VERTICAL);
    }

    protected void fillBackground(Graphics2D g2, Rectangle2D area, PlotOrientation orientation) {
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        if (this.backgroundPaint == null) {
            return;
        }
        Paint p = this.backgroundPaint;
        if (p instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint)p;
            if (orientation == PlotOrientation.VERTICAL) {
                p = new GradientPaint((float)area.getCenterX(), (float)area.getMaxY(), gp.getColor1(), (float)area.getCenterX(), (float)area.getMinY(), gp.getColor2());
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                p = new GradientPaint((float)area.getMinX(), (float)area.getCenterY(), gp.getColor1(), (float)area.getMaxX(), (float)area.getCenterY(), gp.getColor2());
            }
        }
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, this.backgroundAlpha));
        g2.setPaint(p);
        g2.fill(area);
        g2.setComposite(originalComposite);
    }

    public void drawBackgroundImage(Graphics2D g2, Rectangle2D area) {
        if (this.backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, this.backgroundImageAlpha));
            Rectangle2D.Double dest = new Rectangle2D.Double(0.0, 0.0, this.backgroundImage.getWidth(null), this.backgroundImage.getHeight(null));
            Align.align(dest, area, this.backgroundImageAlignment);
            g2.drawImage(this.backgroundImage, (int)((RectangularShape)dest).getX(), (int)((RectangularShape)dest).getY(), (int)((RectangularShape)dest).getWidth() + 1, (int)((RectangularShape)dest).getHeight() + 1, null);
            g2.setComposite(originalComposite);
        }
    }

    public void drawOutline(Graphics2D g2, Rectangle2D area) {
        if (!this.outlineVisible) {
            return;
        }
        if (this.outlineStroke != null && this.outlinePaint != null) {
            g2.setStroke(this.outlineStroke);
            g2.setPaint(this.outlinePaint);
            g2.draw(area);
        }
    }

    protected void drawNoDataMessage(Graphics2D g2, Rectangle2D area) {
        Shape savedClip = g2.getClip();
        g2.clip(area);
        String message = this.noDataMessage;
        if (message != null) {
            g2.setFont(this.noDataMessageFont);
            g2.setPaint(this.noDataMessagePaint);
            TextBlock block = TextUtilities.createTextBlock(this.noDataMessage, this.noDataMessageFont, this.noDataMessagePaint, 0.9f * (float)area.getWidth(), new G2TextMeasurer(g2));
            block.draw(g2, (float)area.getCenterX(), (float)area.getCenterY(), TextBlockAnchor.CENTER);
        }
        g2.setClip(savedClip);
    }

    protected void createAndAddEntity(Rectangle2D dataArea, PlotRenderingInfo plotState, String toolTip, String urlText) {
        EntityCollection e;
        if (plotState != null && plotState.getOwner() != null && (e = plotState.getOwner().getEntityCollection()) != null) {
            e.add(new PlotEntity(dataArea, this, toolTip, urlText));
        }
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
    }

    public void zoom(double percent) {
    }

    public void axisChanged(AxisChangeEvent event) {
        this.fireChangeEvent();
    }

    public void datasetChanged(DatasetChangeEvent event) {
        PlotChangeEvent newEvent = new PlotChangeEvent(this);
        newEvent.setType(ChartChangeEventType.DATASET_UPDATED);
        this.notifyListeners(newEvent);
    }

    public void markerChanged(MarkerChangeEvent event) {
        this.fireChangeEvent();
    }

    protected double getRectX(double x, double w1, double w2, RectangleEdge edge) {
        double result = x;
        if (edge == RectangleEdge.LEFT) {
            result += w1;
        } else if (edge == RectangleEdge.RIGHT) {
            result += w2;
        }
        return result;
    }

    protected double getRectY(double y, double h1, double h2, RectangleEdge edge) {
        double result = y;
        if (edge == RectangleEdge.TOP) {
            result += h1;
        } else if (edge == RectangleEdge.BOTTOM) {
            result += h2;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Plot)) {
            return false;
        }
        Plot that = (Plot)obj;
        if (!ObjectUtilities.equal(this.noDataMessage, that.noDataMessage)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.noDataMessageFont, that.noDataMessageFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.noDataMessagePaint, that.noDataMessagePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.insets, that.insets)) {
            return false;
        }
        if (this.outlineVisible != that.outlineVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundImage, that.backgroundImage)) {
            return false;
        }
        if (this.backgroundImageAlignment != that.backgroundImageAlignment) {
            return false;
        }
        if (this.backgroundImageAlpha != that.backgroundImageAlpha) {
            return false;
        }
        if (this.foregroundAlpha != that.foregroundAlpha) {
            return false;
        }
        if (this.backgroundAlpha != that.backgroundAlpha) {
            return false;
        }
        if (!this.drawingSupplier.equals(that.drawingSupplier)) {
            return false;
        }
        return this.notify == that.notify;
    }

    public Object clone() throws CloneNotSupportedException {
        Plot clone = (Plot)super.clone();
        if (this.datasetGroup != null) {
            clone.datasetGroup = (DatasetGroup)ObjectUtilities.clone(this.datasetGroup);
        }
        clone.drawingSupplier = (DrawingSupplier)ObjectUtilities.clone(this.drawingSupplier);
        clone.listenerList = new EventListenerList();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.noDataMessagePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.noDataMessagePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.listenerList = new EventListenerList();
    }

    public static RectangleEdge resolveDomainAxisLocation(AxisLocation location, PlotOrientation orientation) {
        if (location == null) {
            throw new IllegalArgumentException("Null 'location' argument.");
        }
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        RectangleEdge result = null;
        if (location == AxisLocation.TOP_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.RIGHT;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.TOP;
            }
        } else if (location == AxisLocation.TOP_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.LEFT;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.TOP;
            }
        } else if (location == AxisLocation.BOTTOM_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.RIGHT;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.BOTTOM;
            }
        } else if (location == AxisLocation.BOTTOM_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.LEFT;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.BOTTOM;
            }
        }
        if (result == null) {
            throw new IllegalStateException("resolveDomainAxisLocation()");
        }
        return result;
    }

    public static RectangleEdge resolveRangeAxisLocation(AxisLocation location, PlotOrientation orientation) {
        if (location == null) {
            throw new IllegalArgumentException("Null 'location' argument.");
        }
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        RectangleEdge result = null;
        if (location == AxisLocation.TOP_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.TOP;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.RIGHT;
            }
        } else if (location == AxisLocation.TOP_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.TOP;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.LEFT;
            }
        } else if (location == AxisLocation.BOTTOM_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.BOTTOM;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.RIGHT;
            }
        } else if (location == AxisLocation.BOTTOM_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.BOTTOM;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.LEFT;
            }
        }
        if (result == null) {
            throw new IllegalStateException("resolveRangeAxisLocation()");
        }
        return result;
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

