/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYAnnotationBoundsInfo;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.renderer.RendererUtilities;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class XYPlot
extends Plot
implements ValueAxisPlot,
Pannable,
Zoomable,
RendererChangeListener,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 7044148245716569264L;
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, 0, 2, 0.0f, new float[]{2.0f, 2.0f}, 0.0f);
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
    public static final Stroke DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;
    public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.blue;
    protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    private PlotOrientation orientation;
    private RectangleInsets axisOffset;
    private ObjectList domainAxes;
    private ObjectList domainAxisLocations;
    private ObjectList rangeAxes;
    private ObjectList rangeAxisLocations;
    private ObjectList datasets;
    private ObjectList renderers;
    private Map datasetToDomainAxesMap;
    private Map datasetToRangeAxesMap;
    private transient Point2D quadrantOrigin = new Point2D.Double(0.0, 0.0);
    private transient Paint[] quadrantPaint = new Paint[]{null, null, null, null};
    private boolean domainGridlinesVisible;
    private transient Stroke domainGridlineStroke;
    private transient Paint domainGridlinePaint;
    private boolean rangeGridlinesVisible;
    private transient Stroke rangeGridlineStroke;
    private transient Paint rangeGridlinePaint;
    private boolean domainMinorGridlinesVisible;
    private transient Stroke domainMinorGridlineStroke;
    private transient Paint domainMinorGridlinePaint;
    private boolean rangeMinorGridlinesVisible;
    private transient Stroke rangeMinorGridlineStroke;
    private transient Paint rangeMinorGridlinePaint;
    private boolean domainZeroBaselineVisible;
    private transient Stroke domainZeroBaselineStroke;
    private transient Paint domainZeroBaselinePaint;
    private boolean rangeZeroBaselineVisible;
    private transient Stroke rangeZeroBaselineStroke;
    private transient Paint rangeZeroBaselinePaint;
    private boolean domainCrosshairVisible;
    private double domainCrosshairValue;
    private transient Stroke domainCrosshairStroke;
    private transient Paint domainCrosshairPaint;
    private boolean domainCrosshairLockedOnData = true;
    private boolean rangeCrosshairVisible;
    private double rangeCrosshairValue;
    private transient Stroke rangeCrosshairStroke;
    private transient Paint rangeCrosshairPaint;
    private boolean rangeCrosshairLockedOnData = true;
    private Map foregroundDomainMarkers;
    private Map backgroundDomainMarkers;
    private Map foregroundRangeMarkers;
    private Map backgroundRangeMarkers;
    private List annotations;
    private transient Paint domainTickBandPaint;
    private transient Paint rangeTickBandPaint;
    private AxisSpace fixedDomainAxisSpace;
    private AxisSpace fixedRangeAxisSpace;
    private DatasetRenderingOrder datasetRenderingOrder = DatasetRenderingOrder.REVERSE;
    private SeriesRenderingOrder seriesRenderingOrder = SeriesRenderingOrder.REVERSE;
    private int weight = 1;
    private LegendItemCollection fixedLegendItems;
    private boolean domainPannable;
    private boolean rangePannable;

    public XYPlot() {
        this(null, null, null, null);
    }

    public XYPlot(XYDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer) {
        this.orientation = PlotOrientation.VERTICAL;
        this.axisOffset = RectangleInsets.ZERO_INSETS;
        this.domainAxes = new ObjectList();
        this.domainAxisLocations = new ObjectList();
        this.foregroundDomainMarkers = new HashMap();
        this.backgroundDomainMarkers = new HashMap();
        this.rangeAxes = new ObjectList();
        this.rangeAxisLocations = new ObjectList();
        this.foregroundRangeMarkers = new HashMap();
        this.backgroundRangeMarkers = new HashMap();
        this.datasets = new ObjectList();
        this.renderers = new ObjectList();
        this.datasetToDomainAxesMap = new TreeMap();
        this.datasetToRangeAxesMap = new TreeMap();
        this.annotations = new ArrayList();
        this.datasets.set(0, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.renderers.set(0, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.domainAxes.set(0, domainAxis);
        this.mapDatasetToDomainAxis(0, 0);
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.domainAxisLocations.set(0, AxisLocation.BOTTOM_OR_LEFT);
        this.rangeAxes.set(0, rangeAxis);
        this.mapDatasetToRangeAxis(0, 0);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        this.rangeAxisLocations.set(0, AxisLocation.BOTTOM_OR_LEFT);
        this.configureDomainAxes();
        this.configureRangeAxes();
        this.domainGridlinesVisible = true;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.domainMinorGridlinesVisible = false;
        this.domainMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainMinorGridlinePaint = Color.white;
        this.domainZeroBaselineVisible = false;
        this.domainZeroBaselinePaint = Color.black;
        this.domainZeroBaselineStroke = new BasicStroke(0.5f);
        this.rangeGridlinesVisible = true;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeMinorGridlinesVisible = false;
        this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeMinorGridlinePaint = Color.white;
        this.rangeZeroBaselineVisible = false;
        this.rangeZeroBaselinePaint = Color.black;
        this.rangeZeroBaselineStroke = new BasicStroke(0.5f);
        this.domainCrosshairVisible = false;
        this.domainCrosshairValue = 0.0;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.rangeCrosshairVisible = false;
        this.rangeCrosshairValue = 0.0;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
    }

    public String getPlotType() {
        return localizationResources.getString("XY_Plot");
    }

    public PlotOrientation getOrientation() {
        return this.orientation;
    }

    public void setOrientation(PlotOrientation orientation) {
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        if (orientation != this.orientation) {
            this.orientation = orientation;
            this.fireChangeEvent();
        }
    }

    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    public void setAxisOffset(RectangleInsets offset) {
        if (offset == null) {
            throw new IllegalArgumentException("Null 'offset' argument.");
        }
        this.axisOffset = offset;
        this.fireChangeEvent();
    }

    public ValueAxis getDomainAxis() {
        return this.getDomainAxis(0);
    }

    public ValueAxis getDomainAxis(int index) {
        Plot parent;
        ValueAxis result = null;
        if (index < this.domainAxes.size()) {
            result = (ValueAxis)this.domainAxes.get(index);
        }
        if (result == null && (parent = this.getParent()) instanceof XYPlot) {
            XYPlot xy = (XYPlot)parent;
            result = xy.getDomainAxis(index);
        }
        return result;
    }

    public void setDomainAxis(ValueAxis axis) {
        this.setDomainAxis(0, axis);
    }

    public void setDomainAxis(int index, ValueAxis axis) {
        this.setDomainAxis(index, axis, true);
    }

    public void setDomainAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = this.getDomainAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.domainAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setDomainAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; ++i) {
            this.setDomainAxis(i, axes[i], false);
        }
        this.fireChangeEvent();
    }

    public AxisLocation getDomainAxisLocation() {
        return (AxisLocation)this.domainAxisLocations.get(0);
    }

    public void setDomainAxisLocation(AxisLocation location) {
        this.setDomainAxisLocation(0, location, true);
    }

    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        this.setDomainAxisLocation(0, location, notify);
    }

    public RectangleEdge getDomainAxisEdge() {
        return Plot.resolveDomainAxisLocation(this.getDomainAxisLocation(), this.orientation);
    }

    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }

    public void clearDomainAxes() {
        for (int i = 0; i < this.domainAxes.size(); ++i) {
            ValueAxis axis = (ValueAxis)this.domainAxes.get(i);
            if (axis == null) continue;
            axis.removeChangeListener(this);
        }
        this.domainAxes.clear();
        this.fireChangeEvent();
    }

    public void configureDomainAxes() {
        for (int i = 0; i < this.domainAxes.size(); ++i) {
            ValueAxis axis = (ValueAxis)this.domainAxes.get(i);
            if (axis == null) continue;
            axis.configure();
        }
    }

    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = null;
        if (index < this.domainAxisLocations.size()) {
            result = (AxisLocation)this.domainAxisLocations.get(index);
        }
        if (result == null) {
            result = AxisLocation.getOpposite(this.getDomainAxisLocation());
        }
        return result;
    }

    public void setDomainAxisLocation(int index, AxisLocation location) {
        this.setDomainAxisLocation(index, location, true);
    }

    public void setDomainAxisLocation(int index, AxisLocation location, boolean notify) {
        if (index == 0 && location == null) {
            throw new IllegalArgumentException("Null 'location' for index 0 not permitted.");
        }
        this.domainAxisLocations.set(index, location);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public RectangleEdge getDomainAxisEdge(int index) {
        AxisLocation location = this.getDomainAxisLocation(index);
        RectangleEdge result = Plot.resolveDomainAxisLocation(location, this.orientation);
        if (result == null) {
            result = RectangleEdge.opposite(this.getDomainAxisEdge());
        }
        return result;
    }

    public ValueAxis getRangeAxis() {
        return this.getRangeAxis(0);
    }

    public void setRangeAxis(ValueAxis axis) {
        ValueAxis existing;
        if (axis != null) {
            axis.setPlot(this);
        }
        if ((existing = this.getRangeAxis()) != null) {
            existing.removeChangeListener(this);
        }
        this.rangeAxes.set(0, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        this.fireChangeEvent();
    }

    public AxisLocation getRangeAxisLocation() {
        return (AxisLocation)this.rangeAxisLocations.get(0);
    }

    public void setRangeAxisLocation(AxisLocation location) {
        this.setRangeAxisLocation(0, location, true);
    }

    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        this.setRangeAxisLocation(0, location, notify);
    }

    public RectangleEdge getRangeAxisEdge() {
        return Plot.resolveRangeAxisLocation(this.getRangeAxisLocation(), this.orientation);
    }

    public ValueAxis getRangeAxis(int index) {
        Plot parent;
        ValueAxis result = null;
        if (index < this.rangeAxes.size()) {
            result = (ValueAxis)this.rangeAxes.get(index);
        }
        if (result == null && (parent = this.getParent()) instanceof XYPlot) {
            XYPlot xy = (XYPlot)parent;
            result = xy.getRangeAxis(index);
        }
        return result;
    }

    public void setRangeAxis(int index, ValueAxis axis) {
        this.setRangeAxis(index, axis, true);
    }

    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = this.getRangeAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.rangeAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setRangeAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; ++i) {
            this.setRangeAxis(i, axes[i], false);
        }
        this.fireChangeEvent();
    }

    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }

    public void clearRangeAxes() {
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
            if (axis == null) continue;
            axis.removeChangeListener(this);
        }
        this.rangeAxes.clear();
        this.fireChangeEvent();
    }

    public void configureRangeAxes() {
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
            if (axis == null) continue;
            axis.configure();
        }
    }

    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = null;
        if (index < this.rangeAxisLocations.size()) {
            result = (AxisLocation)this.rangeAxisLocations.get(index);
        }
        if (result == null) {
            result = AxisLocation.getOpposite(this.getRangeAxisLocation());
        }
        return result;
    }

    public void setRangeAxisLocation(int index, AxisLocation location) {
        this.setRangeAxisLocation(index, location, true);
    }

    public void setRangeAxisLocation(int index, AxisLocation location, boolean notify) {
        if (index == 0 && location == null) {
            throw new IllegalArgumentException("Null 'location' for index 0 not permitted.");
        }
        this.rangeAxisLocations.set(index, location);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public RectangleEdge getRangeAxisEdge(int index) {
        AxisLocation location = this.getRangeAxisLocation(index);
        RectangleEdge result = Plot.resolveRangeAxisLocation(location, this.orientation);
        if (result == null) {
            result = RectangleEdge.opposite(this.getRangeAxisEdge());
        }
        return result;
    }

    public XYDataset getDataset() {
        return this.getDataset(0);
    }

    public XYDataset getDataset(int index) {
        XYDataset result = null;
        if (this.datasets.size() > index) {
            result = (XYDataset)this.datasets.get(index);
        }
        return result;
    }

    public void setDataset(XYDataset dataset) {
        this.setDataset(0, dataset);
    }

    public void setDataset(int index, XYDataset dataset) {
        XYDataset existing = this.getDataset(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.set(index, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        this.datasetChanged(event);
    }

    public int getDatasetCount() {
        return this.datasets.size();
    }

    public int indexOf(XYDataset dataset) {
        int result = -1;
        for (int i = 0; i < this.datasets.size(); ++i) {
            if (dataset != this.datasets.get(i)) continue;
            result = i;
            break;
        }
        return result;
    }

    public void mapDatasetToDomainAxis(int index, int axisIndex) {
        ArrayList<Integer> axisIndices = new ArrayList<Integer>(1);
        axisIndices.add(new Integer(axisIndex));
        this.mapDatasetToDomainAxes(index, axisIndices);
    }

    public void mapDatasetToDomainAxes(int index, List axisIndices) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires 'index' >= 0.");
        }
        this.checkAxisIndices(axisIndices);
        Integer key = new Integer(index);
        this.datasetToDomainAxesMap.put(key, new ArrayList(axisIndices));
        this.datasetChanged(new DatasetChangeEvent(this, this.getDataset(index)));
    }

    public void mapDatasetToRangeAxis(int index, int axisIndex) {
        ArrayList<Integer> axisIndices = new ArrayList<Integer>(1);
        axisIndices.add(new Integer(axisIndex));
        this.mapDatasetToRangeAxes(index, axisIndices);
    }

    public void mapDatasetToRangeAxes(int index, List axisIndices) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires 'index' >= 0.");
        }
        this.checkAxisIndices(axisIndices);
        Integer key = new Integer(index);
        this.datasetToRangeAxesMap.put(key, new ArrayList(axisIndices));
        this.datasetChanged(new DatasetChangeEvent(this, this.getDataset(index)));
    }

    private void checkAxisIndices(List indices) {
        if (indices == null) {
            return;
        }
        int count = indices.size();
        if (count == 0) {
            throw new IllegalArgumentException("Empty list not permitted.");
        }
        HashSet set = new HashSet();
        for (int i = 0; i < count; ++i) {
            Object item = indices.get(i);
            if (!(item instanceof Integer)) {
                throw new IllegalArgumentException("Indices must be Integer instances.");
            }
            if (set.contains(item)) {
                throw new IllegalArgumentException("Indices must be unique.");
            }
            set.add(item);
        }
    }

    public int getRendererCount() {
        return this.renderers.size();
    }

    public XYItemRenderer getRenderer() {
        return this.getRenderer(0);
    }

    public XYItemRenderer getRenderer(int index) {
        XYItemRenderer result = null;
        if (this.renderers.size() > index) {
            result = (XYItemRenderer)this.renderers.get(index);
        }
        return result;
    }

    public void setRenderer(XYItemRenderer renderer) {
        this.setRenderer(0, renderer);
    }

    public void setRenderer(int index, XYItemRenderer renderer) {
        this.setRenderer(index, renderer, true);
    }

    public void setRenderer(int index, XYItemRenderer renderer, boolean notify) {
        XYItemRenderer existing = this.getRenderer(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.set(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.configureDomainAxes();
        this.configureRangeAxes();
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void setRenderers(XYItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; ++i) {
            this.setRenderer(i, renderers[i], false);
        }
        this.fireChangeEvent();
    }

    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.datasetRenderingOrder;
    }

    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.datasetRenderingOrder = order;
        this.fireChangeEvent();
    }

    public SeriesRenderingOrder getSeriesRenderingOrder() {
        return this.seriesRenderingOrder;
    }

    public void setSeriesRenderingOrder(SeriesRenderingOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.seriesRenderingOrder = order;
        this.fireChangeEvent();
    }

    public int getIndexOf(XYItemRenderer renderer) {
        return this.renderers.indexOf(renderer);
    }

    public XYItemRenderer getRendererForDataset(XYDataset dataset) {
        XYItemRenderer result = null;
        for (int i = 0; i < this.datasets.size(); ++i) {
            if (this.datasets.get(i) != dataset) continue;
            result = (XYItemRenderer)this.renderers.get(i);
            if (result != null) break;
            result = this.getRenderer();
            break;
        }
        return result;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        this.fireChangeEvent();
    }

    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            this.fireChangeEvent();
        }
    }

    public boolean isDomainMinorGridlinesVisible() {
        return this.domainMinorGridlinesVisible;
    }

    public void setDomainMinorGridlinesVisible(boolean visible) {
        if (this.domainMinorGridlinesVisible != visible) {
            this.domainMinorGridlinesVisible = visible;
            this.fireChangeEvent();
        }
    }

    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    public void setDomainGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainGridlineStroke = stroke;
        this.fireChangeEvent();
    }

    public Stroke getDomainMinorGridlineStroke() {
        return this.domainMinorGridlineStroke;
    }

    public void setDomainMinorGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainMinorGridlineStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    public void setDomainGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainGridlinePaint = paint;
        this.fireChangeEvent();
    }

    public Paint getDomainMinorGridlinePaint() {
        return this.domainMinorGridlinePaint;
    }

    public void setDomainMinorGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainMinorGridlinePaint = paint;
        this.fireChangeEvent();
    }

    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            this.fireChangeEvent();
        }
    }

    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    public void setRangeGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeGridlineStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    public void setRangeGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeGridlinePaint = paint;
        this.fireChangeEvent();
    }

    public boolean isRangeMinorGridlinesVisible() {
        return this.rangeMinorGridlinesVisible;
    }

    public void setRangeMinorGridlinesVisible(boolean visible) {
        if (this.rangeMinorGridlinesVisible != visible) {
            this.rangeMinorGridlinesVisible = visible;
            this.fireChangeEvent();
        }
    }

    public Stroke getRangeMinorGridlineStroke() {
        return this.rangeMinorGridlineStroke;
    }

    public void setRangeMinorGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeMinorGridlineStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getRangeMinorGridlinePaint() {
        return this.rangeMinorGridlinePaint;
    }

    public void setRangeMinorGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeMinorGridlinePaint = paint;
        this.fireChangeEvent();
    }

    public boolean isDomainZeroBaselineVisible() {
        return this.domainZeroBaselineVisible;
    }

    public void setDomainZeroBaselineVisible(boolean visible) {
        this.domainZeroBaselineVisible = visible;
        this.fireChangeEvent();
    }

    public Stroke getDomainZeroBaselineStroke() {
        return this.domainZeroBaselineStroke;
    }

    public void setDomainZeroBaselineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainZeroBaselineStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getDomainZeroBaselinePaint() {
        return this.domainZeroBaselinePaint;
    }

    public void setDomainZeroBaselinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainZeroBaselinePaint = paint;
        this.fireChangeEvent();
    }

    public boolean isRangeZeroBaselineVisible() {
        return this.rangeZeroBaselineVisible;
    }

    public void setRangeZeroBaselineVisible(boolean visible) {
        this.rangeZeroBaselineVisible = visible;
        this.fireChangeEvent();
    }

    public Stroke getRangeZeroBaselineStroke() {
        return this.rangeZeroBaselineStroke;
    }

    public void setRangeZeroBaselineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeZeroBaselineStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getRangeZeroBaselinePaint() {
        return this.rangeZeroBaselinePaint;
    }

    public void setRangeZeroBaselinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeZeroBaselinePaint = paint;
        this.fireChangeEvent();
    }

    public Paint getDomainTickBandPaint() {
        return this.domainTickBandPaint;
    }

    public void setDomainTickBandPaint(Paint paint) {
        this.domainTickBandPaint = paint;
        this.fireChangeEvent();
    }

    public Paint getRangeTickBandPaint() {
        return this.rangeTickBandPaint;
    }

    public void setRangeTickBandPaint(Paint paint) {
        this.rangeTickBandPaint = paint;
        this.fireChangeEvent();
    }

    public Point2D getQuadrantOrigin() {
        return this.quadrantOrigin;
    }

    public void setQuadrantOrigin(Point2D origin) {
        if (origin == null) {
            throw new IllegalArgumentException("Null 'origin' argument.");
        }
        this.quadrantOrigin = origin;
        this.fireChangeEvent();
    }

    public Paint getQuadrantPaint(int index) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3.");
        }
        return this.quadrantPaint[index];
    }

    public void setQuadrantPaint(int index, Paint paint) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3.");
        }
        this.quadrantPaint[index] = paint;
        this.fireChangeEvent();
    }

    public void addDomainMarker(Marker marker) {
        this.addDomainMarker(marker, Layer.FOREGROUND);
    }

    public void addDomainMarker(Marker marker, Layer layer) {
        this.addDomainMarker(0, marker, layer);
    }

    public void clearDomainMarkers() {
        Integer key;
        Iterator iterator;
        Set keys;
        if (this.backgroundDomainMarkers != null) {
            keys = this.backgroundDomainMarkers.keySet();
            iterator = keys.iterator();
            while (iterator.hasNext()) {
                key = (Integer)iterator.next();
                this.clearDomainMarkers(key);
            }
            this.backgroundDomainMarkers.clear();
        }
        if (this.foregroundDomainMarkers != null) {
            keys = this.foregroundDomainMarkers.keySet();
            iterator = keys.iterator();
            while (iterator.hasNext()) {
                key = (Integer)iterator.next();
                this.clearDomainMarkers(key);
            }
            this.foregroundDomainMarkers.clear();
        }
        this.fireChangeEvent();
    }

    public void clearDomainMarkers(int index) {
        Marker m;
        Iterator iterator;
        Collection markers;
        Integer key = new Integer(index);
        if (this.backgroundDomainMarkers != null && (markers = (Collection)this.backgroundDomainMarkers.get(key)) != null) {
            iterator = markers.iterator();
            while (iterator.hasNext()) {
                m = (Marker)iterator.next();
                m.removeChangeListener(this);
            }
            markers.clear();
        }
        if (this.foregroundRangeMarkers != null && (markers = (Collection)this.foregroundDomainMarkers.get(key)) != null) {
            iterator = markers.iterator();
            while (iterator.hasNext()) {
                m = (Marker)iterator.next();
                m.removeChangeListener(this);
            }
            markers.clear();
        }
        this.fireChangeEvent();
    }

    public void addDomainMarker(int index, Marker marker, Layer layer) {
        this.addDomainMarker(index, marker, layer, true);
    }

    public void addDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
        if (marker == null) {
            throw new IllegalArgumentException("Null 'marker' not permitted.");
        }
        if (layer == null) {
            throw new IllegalArgumentException("Null 'layer' not permitted.");
        }
        if (layer == Layer.FOREGROUND) {
            ArrayList<Marker> markers = (ArrayList<Marker>)this.foregroundDomainMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList<Marker>();
                this.foregroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            ArrayList<Marker> markers = (ArrayList<Marker>)this.backgroundDomainMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList<Marker>();
                this.backgroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean removeDomainMarker(Marker marker) {
        return this.removeDomainMarker(marker, Layer.FOREGROUND);
    }

    public boolean removeDomainMarker(Marker marker, Layer layer) {
        return this.removeDomainMarker(0, marker, layer);
    }

    public boolean removeDomainMarker(int index, Marker marker, Layer layer) {
        return this.removeDomainMarker(index, marker, layer, true);
    }

    public boolean removeDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
        ArrayList markers = layer == Layer.FOREGROUND ? (ArrayList)this.foregroundDomainMarkers.get(new Integer(index)) : (ArrayList)this.backgroundDomainMarkers.get(new Integer(index));
        if (markers == null) {
            return false;
        }
        boolean removed = markers.remove(marker);
        if (removed && notify) {
            this.fireChangeEvent();
        }
        return removed;
    }

    public void addRangeMarker(Marker marker) {
        this.addRangeMarker(marker, Layer.FOREGROUND);
    }

    public void addRangeMarker(Marker marker, Layer layer) {
        this.addRangeMarker(0, marker, layer);
    }

    public void clearRangeMarkers() {
        Integer key;
        Iterator iterator;
        Set keys;
        if (this.backgroundRangeMarkers != null) {
            keys = this.backgroundRangeMarkers.keySet();
            iterator = keys.iterator();
            while (iterator.hasNext()) {
                key = (Integer)iterator.next();
                this.clearRangeMarkers(key);
            }
            this.backgroundRangeMarkers.clear();
        }
        if (this.foregroundRangeMarkers != null) {
            keys = this.foregroundRangeMarkers.keySet();
            iterator = keys.iterator();
            while (iterator.hasNext()) {
                key = (Integer)iterator.next();
                this.clearRangeMarkers(key);
            }
            this.foregroundRangeMarkers.clear();
        }
        this.fireChangeEvent();
    }

    public void addRangeMarker(int index, Marker marker, Layer layer) {
        this.addRangeMarker(index, marker, layer, true);
    }

    public void addRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        if (layer == Layer.FOREGROUND) {
            ArrayList<Marker> markers = (ArrayList<Marker>)this.foregroundRangeMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList<Marker>();
                this.foregroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            ArrayList<Marker> markers = (ArrayList<Marker>)this.backgroundRangeMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList<Marker>();
                this.backgroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public void clearRangeMarkers(int index) {
        Marker m;
        Iterator iterator;
        Collection markers;
        Integer key = new Integer(index);
        if (this.backgroundRangeMarkers != null && (markers = (Collection)this.backgroundRangeMarkers.get(key)) != null) {
            iterator = markers.iterator();
            while (iterator.hasNext()) {
                m = (Marker)iterator.next();
                m.removeChangeListener(this);
            }
            markers.clear();
        }
        if (this.foregroundRangeMarkers != null && (markers = (Collection)this.foregroundRangeMarkers.get(key)) != null) {
            iterator = markers.iterator();
            while (iterator.hasNext()) {
                m = (Marker)iterator.next();
                m.removeChangeListener(this);
            }
            markers.clear();
        }
        this.fireChangeEvent();
    }

    public boolean removeRangeMarker(Marker marker) {
        return this.removeRangeMarker(marker, Layer.FOREGROUND);
    }

    public boolean removeRangeMarker(Marker marker, Layer layer) {
        return this.removeRangeMarker(0, marker, layer);
    }

    public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
        return this.removeRangeMarker(index, marker, layer, true);
    }

    public boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        if (marker == null) {
            throw new IllegalArgumentException("Null 'marker' argument.");
        }
        ArrayList markers = layer == Layer.FOREGROUND ? (ArrayList)this.foregroundRangeMarkers.get(new Integer(index)) : (ArrayList)this.backgroundRangeMarkers.get(new Integer(index));
        if (markers == null) {
            return false;
        }
        boolean removed = markers.remove(marker);
        if (removed && notify) {
            this.fireChangeEvent();
        }
        return removed;
    }

    public void addAnnotation(XYAnnotation annotation) {
        this.addAnnotation(annotation, true);
    }

    public void addAnnotation(XYAnnotation annotation, boolean notify) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        this.annotations.add(annotation);
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean removeAnnotation(XYAnnotation annotation) {
        return this.removeAnnotation(annotation, true);
    }

    public boolean removeAnnotation(XYAnnotation annotation, boolean notify) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        boolean removed = this.annotations.remove(annotation);
        if (removed && notify) {
            this.fireChangeEvent();
        }
        return removed;
    }

    public List getAnnotations() {
        return new ArrayList(this.annotations);
    }

    public void clearAnnotations() {
        this.annotations.clear();
        this.fireChangeEvent();
    }

    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        AxisSpace space = new AxisSpace();
        space = this.calculateRangeAxisSpace(g2, plotArea, space);
        Rectangle2D revPlotArea = space.shrink(plotArea, null);
        space = this.calculateDomainAxisSpace(g2, revPlotArea, space);
        return space;
    }

    protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
        block4: {
            block2: {
                block3: {
                    if (space == null) {
                        space = new AxisSpace();
                    }
                    if (this.fixedDomainAxisSpace == null) break block2;
                    if (this.orientation != PlotOrientation.HORIZONTAL) break block3;
                    space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
                    space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
                    break block4;
                }
                if (this.orientation != PlotOrientation.VERTICAL) break block4;
                space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
                break block4;
            }
            for (int i = 0; i < this.domainAxes.size(); ++i) {
                Axis axis = (Axis)this.domainAxes.get(i);
                if (axis == null) continue;
                RectangleEdge edge = this.getDomainAxisEdge(i);
                space = axis.reserveSpace(g2, this, plotArea, edge, space);
            }
        }
        return space;
    }

    protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
        block4: {
            block2: {
                block3: {
                    if (space == null) {
                        space = new AxisSpace();
                    }
                    if (this.fixedRangeAxisSpace == null) break block2;
                    if (this.orientation != PlotOrientation.HORIZONTAL) break block3;
                    space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
                    space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
                    break block4;
                }
                if (this.orientation != PlotOrientation.VERTICAL) break block4;
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
                break block4;
            }
            for (int i = 0; i < this.rangeAxes.size(); ++i) {
                Axis axis = (Axis)this.rangeAxes.get(i);
                if (axis == null) continue;
                RectangleEdge edge = this.getRangeAxisEdge(i);
                space = axis.reserveSpace(g2, this, plotArea, edge, space);
            }
        }
        return space;
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        int i;
        ValueAxis rangeAxis;
        ValueAxis domainAxis;
        XYItemRenderer r;
        int i2;
        int rendererCount;
        int i3;
        AxisState rangeAxisState;
        boolean b2;
        boolean b1 = area.getWidth() <= 10.0;
        boolean bl = b2 = area.getHeight() <= 10.0;
        if (b1 || b2) {
            return;
        }
        if (info != null) {
            info.setPlotArea(area);
        }
        RectangleInsets insets = this.getInsets();
        insets.trim(area);
        AxisSpace space = this.calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.axisOffset.trim(dataArea);
        this.createAndAddEntity((Rectangle2D)dataArea.clone(), info, null, null);
        if (info != null) {
            info.setDataArea(dataArea);
        }
        this.drawBackground(g2, dataArea);
        Map axisStateMap = this.drawAxes(g2, area, dataArea, info);
        PlotOrientation orient = this.getOrientation();
        if (anchor != null && !dataArea.contains(anchor)) {
            anchor = null;
        }
        CrosshairState crosshairState = new CrosshairState();
        crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairState.setAnchor(anchor);
        crosshairState.setAnchorX(Double.NaN);
        crosshairState.setAnchorY(Double.NaN);
        if (anchor != null) {
            ValueAxis rangeAxis2;
            ValueAxis domainAxis2 = this.getDomainAxis();
            if (domainAxis2 != null) {
                double x = orient == PlotOrientation.VERTICAL ? domainAxis2.java2DToValue(anchor.getX(), dataArea, this.getDomainAxisEdge()) : domainAxis2.java2DToValue(anchor.getY(), dataArea, this.getDomainAxisEdge());
                crosshairState.setAnchorX(x);
            }
            if ((rangeAxis2 = this.getRangeAxis()) != null) {
                double y = orient == PlotOrientation.VERTICAL ? rangeAxis2.java2DToValue(anchor.getY(), dataArea, this.getRangeAxisEdge()) : rangeAxis2.java2DToValue(anchor.getX(), dataArea, this.getRangeAxisEdge());
                crosshairState.setAnchorY(y);
            }
        }
        crosshairState.setCrosshairX(this.getDomainCrosshairValue());
        crosshairState.setCrosshairY(this.getRangeCrosshairValue());
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();
        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
        AxisState domainAxisState = (AxisState)axisStateMap.get(this.getDomainAxis());
        if (domainAxisState == null && parentState != null) {
            domainAxisState = (AxisState)parentState.getSharedAxisStates().get(this.getDomainAxis());
        }
        if ((rangeAxisState = (AxisState)axisStateMap.get(this.getRangeAxis())) == null && parentState != null) {
            rangeAxisState = (AxisState)parentState.getSharedAxisStates().get(this.getRangeAxis());
        }
        if (domainAxisState != null) {
            this.drawDomainTickBands(g2, dataArea, domainAxisState.getTicks());
        }
        if (rangeAxisState != null) {
            this.drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks());
        }
        if (domainAxisState != null) {
            this.drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
            this.drawZeroDomainBaseline(g2, dataArea);
        }
        if (rangeAxisState != null) {
            this.drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
            this.drawZeroRangeBaseline(g2, dataArea);
        }
        for (i3 = 0; i3 < this.renderers.size(); ++i3) {
            this.drawDomainMarkers(g2, dataArea, i3, Layer.BACKGROUND);
        }
        for (i3 = 0; i3 < this.renderers.size(); ++i3) {
            this.drawRangeMarkers(g2, dataArea, i3, Layer.BACKGROUND);
        }
        boolean foundData = false;
        DatasetRenderingOrder order = this.getDatasetRenderingOrder();
        if (order == DatasetRenderingOrder.FORWARD) {
            rendererCount = this.renderers.size();
            for (i2 = 0; i2 < rendererCount; ++i2) {
                r = this.getRenderer(i2);
                if (r == null) continue;
                domainAxis = this.getDomainAxisForDataset(i2);
                rangeAxis = this.getRangeAxisForDataset(i2);
                r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.BACKGROUND, info);
            }
            for (i2 = 0; i2 < this.getDatasetCount(); ++i2) {
                foundData = this.render(g2, dataArea, i2, info, crosshairState) || foundData;
            }
            for (i2 = 0; i2 < rendererCount; ++i2) {
                r = this.getRenderer(i2);
                if (r == null) continue;
                domainAxis = this.getDomainAxisForDataset(i2);
                rangeAxis = this.getRangeAxisForDataset(i2);
                r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.FOREGROUND, info);
            }
        } else if (order == DatasetRenderingOrder.REVERSE) {
            rendererCount = this.renderers.size();
            for (i2 = rendererCount - 1; i2 >= 0; --i2) {
                r = this.getRenderer(i2);
                if (i2 >= this.getDatasetCount() || r == null) continue;
                domainAxis = this.getDomainAxisForDataset(i2);
                rangeAxis = this.getRangeAxisForDataset(i2);
                r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.BACKGROUND, info);
            }
            for (i2 = this.getDatasetCount() - 1; i2 >= 0; --i2) {
                foundData = this.render(g2, dataArea, i2, info, crosshairState) || foundData;
            }
            for (i2 = rendererCount - 1; i2 >= 0; --i2) {
                r = this.getRenderer(i2);
                if (i2 >= this.getDatasetCount() || r == null) continue;
                domainAxis = this.getDomainAxisForDataset(i2);
                rangeAxis = this.getRangeAxisForDataset(i2);
                r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.FOREGROUND, info);
            }
        }
        int xAxisIndex = crosshairState.getDomainAxisIndex();
        ValueAxis xAxis = this.getDomainAxis(xAxisIndex);
        RectangleEdge xAxisEdge = this.getDomainAxisEdge(xAxisIndex);
        if (!this.domainCrosshairLockedOnData && anchor != null) {
            double xx = orient == PlotOrientation.VERTICAL ? xAxis.java2DToValue(anchor.getX(), dataArea, xAxisEdge) : xAxis.java2DToValue(anchor.getY(), dataArea, xAxisEdge);
            crosshairState.setCrosshairX(xx);
        }
        this.setDomainCrosshairValue(crosshairState.getCrosshairX(), false);
        if (this.isDomainCrosshairVisible()) {
            double x = this.getDomainCrosshairValue();
            Paint paint = this.getDomainCrosshairPaint();
            Stroke stroke = this.getDomainCrosshairStroke();
            this.drawDomainCrosshair(g2, dataArea, orient, x, xAxis, stroke, paint);
        }
        int yAxisIndex = crosshairState.getRangeAxisIndex();
        ValueAxis yAxis = this.getRangeAxis(yAxisIndex);
        RectangleEdge yAxisEdge = this.getRangeAxisEdge(yAxisIndex);
        if (!this.rangeCrosshairLockedOnData && anchor != null) {
            double yy = orient == PlotOrientation.VERTICAL ? yAxis.java2DToValue(anchor.getY(), dataArea, yAxisEdge) : yAxis.java2DToValue(anchor.getX(), dataArea, yAxisEdge);
            crosshairState.setCrosshairY(yy);
        }
        this.setRangeCrosshairValue(crosshairState.getCrosshairY(), false);
        if (this.isRangeCrosshairVisible()) {
            double y = this.getRangeCrosshairValue();
            Paint paint = this.getRangeCrosshairPaint();
            Stroke stroke = this.getRangeCrosshairStroke();
            this.drawRangeCrosshair(g2, dataArea, orient, y, yAxis, stroke, paint);
        }
        if (!foundData) {
            this.drawNoDataMessage(g2, dataArea);
        }
        for (i = 0; i < this.renderers.size(); ++i) {
            this.drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (i = 0; i < this.renderers.size(); ++i) {
            this.drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        this.drawAnnotations(g2, dataArea, info);
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        this.drawOutline(g2, dataArea);
    }

    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        this.fillBackground(g2, area, this.orientation);
        this.drawQuadrants(g2, area);
        this.drawBackgroundImage(g2, area);
    }

    protected void drawQuadrants(Graphics2D g2, Rectangle2D area) {
        boolean somethingToDraw = false;
        ValueAxis xAxis = this.getDomainAxis();
        if (xAxis == null) {
            return;
        }
        double x = xAxis.getRange().constrain(this.quadrantOrigin.getX());
        double xx = xAxis.valueToJava2D(x, area, this.getDomainAxisEdge());
        ValueAxis yAxis = this.getRangeAxis();
        if (yAxis == null) {
            return;
        }
        double y = yAxis.getRange().constrain(this.quadrantOrigin.getY());
        double yy = yAxis.valueToJava2D(y, area, this.getRangeAxisEdge());
        double xmin = xAxis.getLowerBound();
        double xxmin = xAxis.valueToJava2D(xmin, area, this.getDomainAxisEdge());
        double xmax = xAxis.getUpperBound();
        double xxmax = xAxis.valueToJava2D(xmax, area, this.getDomainAxisEdge());
        double ymin = yAxis.getLowerBound();
        double yymin = yAxis.valueToJava2D(ymin, area, this.getRangeAxisEdge());
        double ymax = yAxis.getUpperBound();
        double yymax = yAxis.valueToJava2D(ymax, area, this.getRangeAxisEdge());
        Rectangle2D[] r = new Rectangle2D[]{null, null, null, null};
        if (this.quadrantPaint[0] != null && x > xmin && y < ymax) {
            r[0] = this.orientation == PlotOrientation.HORIZONTAL ? new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmin, xx), Math.abs(yy - yymax), Math.abs(xx - xxmin)) : new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymax, yy), Math.abs(xx - xxmin), Math.abs(yy - yymax));
            somethingToDraw = true;
        }
        if (this.quadrantPaint[1] != null && x < xmax && y < ymax) {
            r[1] = this.orientation == PlotOrientation.HORIZONTAL ? new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmax, xx), Math.abs(yy - yymax), Math.abs(xx - xxmax)) : new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymax, yy), Math.abs(xx - xxmax), Math.abs(yy - yymax));
            somethingToDraw = true;
        }
        if (this.quadrantPaint[2] != null && x > xmin && y > ymin) {
            r[2] = this.orientation == PlotOrientation.HORIZONTAL ? new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmin, xx), Math.abs(yy - yymin), Math.abs(xx - xxmin)) : new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymin, yy), Math.abs(xx - xxmin), Math.abs(yy - yymin));
            somethingToDraw = true;
        }
        if (this.quadrantPaint[3] != null && x < xmax && y > ymin) {
            r[3] = this.orientation == PlotOrientation.HORIZONTAL ? new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmax, xx), Math.abs(yy - yymin), Math.abs(xx - xxmax)) : new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymin, yy), Math.abs(xx - xxmax), Math.abs(yy - yymin));
            somethingToDraw = true;
        }
        if (somethingToDraw) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, this.getBackgroundAlpha()));
            for (int i = 0; i < 4; ++i) {
                if (this.quadrantPaint[i] == null || r[i] == null) continue;
                g2.setPaint(this.quadrantPaint[i]);
                g2.fill(r[i]);
            }
            g2.setComposite(originalComposite);
        }
    }

    public void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        Paint bandPaint = this.getDomainTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            ValueAxis xAxis = this.getDomainAxis();
            double previous = xAxis.getLowerBound();
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick)iterator.next();
                double current = tick.getValue();
                if (fillBand) {
                    this.getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = xAxis.getUpperBound();
            if (fillBand) {
                this.getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, end);
            }
        }
    }

    public void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        Paint bandPaint = this.getRangeTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            ValueAxis axis = this.getRangeAxis();
            double previous = axis.getLowerBound();
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick)iterator.next();
                double current = tick.getValue();
                if (fillBand) {
                    this.getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = axis.getUpperBound();
            if (fillBand) {
                this.getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, end);
            }
        }
    }

    protected Map drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
        AxisState info;
        ValueAxis axis;
        int index;
        AxisCollection axisCollection = new AxisCollection();
        for (index = 0; index < this.domainAxes.size(); ++index) {
            ValueAxis axis2 = (ValueAxis)this.domainAxes.get(index);
            if (axis2 == null) continue;
            axisCollection.add(axis2, this.getDomainAxisEdge(index));
        }
        for (index = 0; index < this.rangeAxes.size(); ++index) {
            ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(index);
            if (yAxis == null) continue;
            axisCollection.add(yAxis, this.getRangeAxisEdge(index));
        }
        HashMap<ValueAxis, AxisState> axisStateMap = new HashMap<ValueAxis, AxisState>();
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        Iterator iterator = axisCollection.getAxesAtTop().iterator();
        while (iterator.hasNext()) {
            axis = (ValueAxis)iterator.next();
            info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        iterator = axisCollection.getAxesAtBottom().iterator();
        while (iterator.hasNext()) {
            axis = (ValueAxis)iterator.next();
            info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        iterator = axisCollection.getAxesAtLeft().iterator();
        while (iterator.hasNext()) {
            axis = (ValueAxis)iterator.next();
            info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        iterator = axisCollection.getAxesAtRight().iterator();
        while (iterator.hasNext()) {
            axis = (ValueAxis)iterator.next();
            info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        return axisStateMap;
    }

    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CrosshairState crosshairState) {
        boolean foundData;
        block12: {
            foundData = false;
            XYDataset dataset = this.getDataset(index);
            if (DatasetUtilities.isEmptyOrNull(dataset)) break block12;
            foundData = true;
            ValueAxis xAxis = this.getDomainAxisForDataset(index);
            ValueAxis yAxis = this.getRangeAxisForDataset(index);
            if (xAxis == null || yAxis == null) {
                return foundData;
            }
            XYItemRenderer renderer = this.getRenderer(index);
            if (renderer == null && (renderer = this.getRenderer()) == null) {
                return foundData;
            }
            XYItemRendererState state = renderer.initialise(g2, dataArea, this, dataset, info);
            int passCount = renderer.getPassCount();
            SeriesRenderingOrder seriesOrder = this.getSeriesRenderingOrder();
            if (seriesOrder == SeriesRenderingOrder.REVERSE) {
                for (int pass = 0; pass < passCount; ++pass) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = seriesCount - 1; series >= 0; --series) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (lastItem == -1) continue;
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtilities.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; ++item) {
                            renderer.drawItem(g2, state, dataArea, info, this, xAxis, yAxis, dataset, series, item, crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                    }
                }
            } else {
                for (int pass = 0; pass < passCount; ++pass) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = 0; series < seriesCount; ++series) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtilities.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; ++item) {
                            renderer.drawItem(g2, state, dataArea, info, this, xAxis, yAxis, dataset, series, item, crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                    }
                }
            }
        }
        return foundData;
    }

    public ValueAxis getDomainAxisForDataset(int index) {
        int upper = Math.max(this.getDatasetCount(), this.getRendererCount());
        if (index < 0 || index >= upper) {
            throw new IllegalArgumentException("Index " + index + " out of bounds.");
        }
        ValueAxis valueAxis = null;
        List axisIndices = (List)this.datasetToDomainAxesMap.get(new Integer(index));
        if (axisIndices != null) {
            Integer axisIndex = (Integer)axisIndices.get(0);
            valueAxis = this.getDomainAxis(axisIndex);
        } else {
            valueAxis = this.getDomainAxis(0);
        }
        return valueAxis;
    }

    public ValueAxis getRangeAxisForDataset(int index) {
        int upper = Math.max(this.getDatasetCount(), this.getRendererCount());
        if (index < 0 || index >= upper) {
            throw new IllegalArgumentException("Index " + index + " out of bounds.");
        }
        ValueAxis valueAxis = null;
        List axisIndices = (List)this.datasetToRangeAxesMap.get(new Integer(index));
        if (axisIndices != null) {
            Integer axisIndex = (Integer)axisIndices.get(0);
            valueAxis = this.getRangeAxis(axisIndex);
        } else {
            valueAxis = this.getRangeAxis(0);
        }
        return valueAxis;
    }

    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (this.getRenderer() == null) {
            return;
        }
        if (this.isDomainGridlinesVisible() || this.isDomainMinorGridlinesVisible()) {
            Stroke gridStroke = null;
            Paint gridPaint = null;
            Iterator iterator = ticks.iterator();
            boolean paintLine = false;
            while (iterator.hasNext()) {
                XYItemRenderer r;
                paintLine = false;
                ValueTick tick = (ValueTick)iterator.next();
                if (tick.getTickType() == TickType.MINOR && this.isDomainMinorGridlinesVisible()) {
                    gridStroke = this.getDomainMinorGridlineStroke();
                    gridPaint = this.getDomainMinorGridlinePaint();
                    paintLine = true;
                } else if (tick.getTickType() == TickType.MAJOR && this.isDomainGridlinesVisible()) {
                    gridStroke = this.getDomainGridlineStroke();
                    gridPaint = this.getDomainGridlinePaint();
                    paintLine = true;
                }
                if (!((r = this.getRenderer()) instanceof AbstractXYItemRenderer) || !paintLine) continue;
                ((AbstractXYItemRenderer)r).drawDomainLine(g2, this, this.getDomainAxis(), dataArea, tick.getValue(), gridPaint, gridStroke);
            }
        }
    }

    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D area, List ticks) {
        if (this.getRenderer() == null) {
            return;
        }
        if (this.isRangeGridlinesVisible() || this.isRangeMinorGridlinesVisible()) {
            Stroke gridStroke = null;
            Paint gridPaint = null;
            ValueAxis axis = this.getRangeAxis();
            if (axis != null) {
                Iterator iterator = ticks.iterator();
                boolean paintLine = false;
                while (iterator.hasNext()) {
                    paintLine = false;
                    ValueTick tick = (ValueTick)iterator.next();
                    if (tick.getTickType() == TickType.MINOR && this.isRangeMinorGridlinesVisible()) {
                        gridStroke = this.getRangeMinorGridlineStroke();
                        gridPaint = this.getRangeMinorGridlinePaint();
                        paintLine = true;
                    } else if (tick.getTickType() == TickType.MAJOR && this.isRangeGridlinesVisible()) {
                        gridStroke = this.getRangeGridlineStroke();
                        gridPaint = this.getRangeGridlinePaint();
                        paintLine = true;
                    }
                    if (tick.getValue() == 0.0 && this.isRangeZeroBaselineVisible() || !paintLine) continue;
                    this.getRenderer().drawRangeLine(g2, this, this.getRangeAxis(), area, tick.getValue(), gridPaint, gridStroke);
                }
            }
        }
    }

    protected void drawZeroDomainBaseline(Graphics2D g2, Rectangle2D area) {
        XYItemRenderer r;
        if (this.isDomainZeroBaselineVisible() && (r = this.getRenderer()) instanceof AbstractXYItemRenderer) {
            AbstractXYItemRenderer renderer = (AbstractXYItemRenderer)r;
            renderer.drawDomainLine(g2, this, this.getDomainAxis(), area, 0.0, this.domainZeroBaselinePaint, this.domainZeroBaselineStroke);
        }
    }

    protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
        if (this.isRangeZeroBaselineVisible()) {
            this.getRenderer().drawRangeLine(g2, this, this.getRangeAxis(), area, 0.0, this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke);
        }
    }

    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
        Iterator iterator = this.annotations.iterator();
        while (iterator.hasNext()) {
            XYAnnotation annotation = (XYAnnotation)iterator.next();
            ValueAxis xAxis = this.getDomainAxis();
            ValueAxis yAxis = this.getRangeAxis();
            annotation.draw(g2, this, dataArea, xAxis, yAxis, 0, info);
        }
    }

    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        XYItemRenderer r = this.getRenderer(index);
        if (r == null) {
            return;
        }
        if (index >= this.getDatasetCount()) {
            return;
        }
        Collection markers = this.getDomainMarkers(index, layer);
        ValueAxis axis = this.getDomainAxisForDataset(index);
        if (markers != null && axis != null) {
            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker)iterator.next();
                r.drawDomainMarker(g2, this, axis, marker, dataArea);
            }
        }
    }

    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        XYItemRenderer r = this.getRenderer(index);
        if (r == null) {
            return;
        }
        if (index >= this.getDatasetCount()) {
            return;
        }
        Collection markers = this.getRangeMarkers(index, layer);
        ValueAxis axis = this.getRangeAxisForDataset(index);
        if (markers != null && axis != null) {
            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker)iterator.next();
                r.drawRangeMarker(g2, this, axis, marker, dataArea);
            }
        }
    }

    public Collection getDomainMarkers(Layer layer) {
        return this.getDomainMarkers(0, layer);
    }

    public Collection getRangeMarkers(Layer layer) {
        return this.getRangeMarkers(0, layer);
    }

    public Collection getDomainMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection)this.foregroundDomainMarkers.get(key);
        } else if (layer == Layer.BACKGROUND) {
            result = (Collection)this.backgroundDomainMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    public Collection getRangeMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection)this.foregroundRangeMarkers.get(key);
        } else if (layer == Layer.BACKGROUND) {
            result = (Collection)this.backgroundRangeMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        ValueAxis axis = this.getRangeAxis();
        if (this.getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = this.getDomainAxis();
        }
        if (axis.getRange().contains(value)) {
            double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
            Line2D.Double line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (axis.getRange().contains(value)) {
            Line2D.Double line = null;
            if (orientation == PlotOrientation.VERTICAL) {
                double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
                line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            } else {
                double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            }
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        ValueAxis axis = this.getDomainAxis();
        if (this.getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = this.getRangeAxis();
        }
        if (axis.getRange().contains(value)) {
            double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
            Line2D.Double line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (axis.getRange().contains(value)) {
            Line2D.Double line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
                line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            } else {
                double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            }
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            ValueAxis yaxis;
            ValueAxis xaxis = this.getDomainAxis();
            if (xaxis != null) {
                double hvalue = xaxis.java2DToValue(x, info.getDataArea(), this.getDomainAxisEdge());
                this.setDomainCrosshairValue(hvalue);
            }
            if ((yaxis = this.getRangeAxis()) != null) {
                double vvalue = yaxis.java2DToValue(y, info.getDataArea(), this.getRangeAxisEdge());
                this.setRangeCrosshairValue(vvalue);
            }
        }
    }

    private List getDatasetsMappedToDomainAxis(Integer axisIndex) {
        if (axisIndex == null) {
            throw new IllegalArgumentException("Null 'axisIndex' argument.");
        }
        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < this.datasets.size(); ++i) {
            List mappedAxes = (List)this.datasetToDomainAxesMap.get(new Integer(i));
            if (mappedAxes == null) {
                if (!axisIndex.equals(ZERO)) continue;
                result.add(this.datasets.get(i));
                continue;
            }
            if (!mappedAxes.contains(axisIndex)) continue;
            result.add(this.datasets.get(i));
        }
        return result;
    }

    private List getDatasetsMappedToRangeAxis(Integer axisIndex) {
        if (axisIndex == null) {
            throw new IllegalArgumentException("Null 'axisIndex' argument.");
        }
        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < this.datasets.size(); ++i) {
            List mappedAxes = (List)this.datasetToRangeAxesMap.get(new Integer(i));
            if (mappedAxes == null) {
                if (!axisIndex.equals(ZERO)) continue;
                result.add(this.datasets.get(i));
                continue;
            }
            if (!mappedAxes.contains(axisIndex)) continue;
            result.add(this.datasets.get(i));
        }
        return result;
    }

    public int getDomainAxisIndex(ValueAxis axis) {
        Plot parent;
        int result = this.domainAxes.indexOf(axis);
        if (result < 0 && (parent = this.getParent()) instanceof XYPlot) {
            XYPlot p = (XYPlot)parent;
            result = p.getDomainAxisIndex(axis);
        }
        return result;
    }

    public int getRangeAxisIndex(ValueAxis axis) {
        Plot parent;
        int result = this.rangeAxes.indexOf(axis);
        if (result < 0 && (parent = this.getParent()) instanceof XYPlot) {
            XYPlot p = (XYPlot)parent;
            result = p.getRangeAxisIndex(axis);
        }
        return result;
    }

    public Range getDataRange(ValueAxis axis) {
        Iterator iterator;
        int rangeIndex;
        Range result = null;
        ArrayList mappedDatasets = new ArrayList();
        ArrayList<XYAnnotation> includedAnnotations = new ArrayList<XYAnnotation>();
        boolean isDomainAxis = true;
        int domainIndex = this.getDomainAxisIndex(axis);
        if (domainIndex >= 0) {
            isDomainAxis = true;
            mappedDatasets.addAll(this.getDatasetsMappedToDomainAxis(new Integer(domainIndex)));
            if (domainIndex == 0) {
                Iterator iterator2 = this.annotations.iterator();
                while (iterator2.hasNext()) {
                    XYAnnotation annotation = (XYAnnotation)iterator2.next();
                    if (!(annotation instanceof XYAnnotationBoundsInfo)) continue;
                    includedAnnotations.add(annotation);
                }
            }
        }
        if ((rangeIndex = this.getRangeAxisIndex(axis)) >= 0) {
            isDomainAxis = false;
            mappedDatasets.addAll(this.getDatasetsMappedToRangeAxis(new Integer(rangeIndex)));
            if (rangeIndex == 0) {
                iterator = this.annotations.iterator();
                while (iterator.hasNext()) {
                    XYAnnotation annotation = (XYAnnotation)iterator.next();
                    if (!(annotation instanceof XYAnnotationBoundsInfo)) continue;
                    includedAnnotations.add(annotation);
                }
            }
        }
        iterator = mappedDatasets.iterator();
        while (iterator.hasNext()) {
            XYDataset d = (XYDataset)iterator.next();
            if (d == null) continue;
            XYItemRenderer r = this.getRendererForDataset(d);
            result = isDomainAxis ? (r != null ? Range.combine(result, r.findDomainBounds(d)) : Range.combine(result, DatasetUtilities.findDomainBounds(d))) : (r != null ? Range.combine(result, r.findRangeBounds(d)) : Range.combine(result, DatasetUtilities.findRangeBounds(d)));
            if (!(r instanceof AbstractXYItemRenderer)) continue;
            AbstractXYItemRenderer rr = (AbstractXYItemRenderer)r;
            Collection c = rr.getAnnotations();
            Iterator i = c.iterator();
            while (i.hasNext()) {
                XYAnnotation a = (XYAnnotation)i.next();
                if (!(a instanceof XYAnnotationBoundsInfo)) continue;
                includedAnnotations.add(a);
            }
        }
        Iterator it = includedAnnotations.iterator();
        while (it.hasNext()) {
            XYAnnotationBoundsInfo xyabi = (XYAnnotationBoundsInfo)it.next();
            if (!xyabi.getIncludeInDataBounds()) continue;
            if (isDomainAxis) {
                result = Range.combine(result, xyabi.getXRange());
                continue;
            }
            result = Range.combine(result, xyabi.getYRange());
        }
        return result;
    }

    public void datasetChanged(DatasetChangeEvent event) {
        this.configureDomainAxes();
        this.configureRangeAxes();
        if (this.getParent() != null) {
            this.getParent().datasetChanged(event);
        } else {
            PlotChangeEvent e = new PlotChangeEvent(this);
            e.setType(ChartChangeEventType.DATASET_UPDATED);
            this.notifyListeners(e);
        }
    }

    public void rendererChanged(RendererChangeEvent event) {
        if (event.getSeriesVisibilityChanged()) {
            this.configureDomainAxes();
            this.configureRangeAxes();
        }
        this.fireChangeEvent();
    }

    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    public void setDomainCrosshairVisible(boolean flag) {
        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            this.fireChangeEvent();
        }
    }

    public boolean isDomainCrosshairLockedOnData() {
        return this.domainCrosshairLockedOnData;
    }

    public void setDomainCrosshairLockedOnData(boolean flag) {
        if (this.domainCrosshairLockedOnData != flag) {
            this.domainCrosshairLockedOnData = flag;
            this.fireChangeEvent();
        }
    }

    public double getDomainCrosshairValue() {
        return this.domainCrosshairValue;
    }

    public void setDomainCrosshairValue(double value) {
        this.setDomainCrosshairValue(value, true);
    }

    public void setDomainCrosshairValue(double value, boolean notify) {
        this.domainCrosshairValue = value;
        if (this.isDomainCrosshairVisible() && notify) {
            this.fireChangeEvent();
        }
    }

    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    public void setDomainCrosshairStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainCrosshairStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    public void setDomainCrosshairPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainCrosshairPaint = paint;
        this.fireChangeEvent();
    }

    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    public void setRangeCrosshairVisible(boolean flag) {
        if (this.rangeCrosshairVisible != flag) {
            this.rangeCrosshairVisible = flag;
            this.fireChangeEvent();
        }
    }

    public boolean isRangeCrosshairLockedOnData() {
        return this.rangeCrosshairLockedOnData;
    }

    public void setRangeCrosshairLockedOnData(boolean flag) {
        if (this.rangeCrosshairLockedOnData != flag) {
            this.rangeCrosshairLockedOnData = flag;
            this.fireChangeEvent();
        }
    }

    public double getRangeCrosshairValue() {
        return this.rangeCrosshairValue;
    }

    public void setRangeCrosshairValue(double value) {
        this.setRangeCrosshairValue(value, true);
    }

    public void setRangeCrosshairValue(double value, boolean notify) {
        this.rangeCrosshairValue = value;
        if (this.isRangeCrosshairVisible() && notify) {
            this.fireChangeEvent();
        }
    }

    public Stroke getRangeCrosshairStroke() {
        return this.rangeCrosshairStroke;
    }

    public void setRangeCrosshairStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeCrosshairStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    public void setRangeCrosshairPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeCrosshairPaint = paint;
        this.fireChangeEvent();
    }

    public AxisSpace getFixedDomainAxisSpace() {
        return this.fixedDomainAxisSpace;
    }

    public void setFixedDomainAxisSpace(AxisSpace space) {
        this.setFixedDomainAxisSpace(space, true);
    }

    public void setFixedDomainAxisSpace(AxisSpace space, boolean notify) {
        this.fixedDomainAxisSpace = space;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public AxisSpace getFixedRangeAxisSpace() {
        return this.fixedRangeAxisSpace;
    }

    public void setFixedRangeAxisSpace(AxisSpace space) {
        this.setFixedRangeAxisSpace(space, true);
    }

    public void setFixedRangeAxisSpace(AxisSpace space, boolean notify) {
        this.fixedRangeAxisSpace = space;
        if (notify) {
            this.fireChangeEvent();
        }
    }

    public boolean isDomainPannable() {
        return this.domainPannable;
    }

    public void setDomainPannable(boolean pannable) {
        this.domainPannable = pannable;
    }

    public boolean isRangePannable() {
        return this.rangePannable;
    }

    public void setRangePannable(boolean pannable) {
        this.rangePannable = pannable;
    }

    public void panDomainAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (!this.isDomainPannable()) {
            return;
        }
        int domainAxisCount = this.getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; ++i) {
            ValueAxis axis = this.getDomainAxis(i);
            if (axis == null) continue;
            if (axis.isInverted()) {
                percent = -percent;
            }
            axis.pan(percent);
        }
    }

    public void panRangeAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (!this.isRangePannable()) {
            return;
        }
        int rangeAxisCount = this.getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; ++i) {
            ValueAxis axis = this.getRangeAxis(i);
            if (axis == null) continue;
            if (axis.isInverted()) {
                percent = -percent;
            }
            axis.pan(percent);
        }
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
        this.zoomDomainAxes(factor, info, source, false);
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        for (int i = 0; i < this.domainAxes.size(); ++i) {
            ValueAxis domainAxis = (ValueAxis)this.domainAxes.get(i);
            if (domainAxis == null) continue;
            if (useAnchor) {
                double sourceX = source.getX();
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    sourceX = source.getY();
                }
                double anchorX = domainAxis.java2DToValue(sourceX, info.getDataArea(), this.getDomainAxisEdge());
                domainAxis.resizeRange2(factor, anchorX);
                continue;
            }
            domainAxis.resizeRange(factor);
        }
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        for (int i = 0; i < this.domainAxes.size(); ++i) {
            ValueAxis domainAxis = (ValueAxis)this.domainAxes.get(i);
            if (domainAxis == null) continue;
            domainAxis.zoomRange(lowerPercent, upperPercent);
        }
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
        this.zoomRangeAxes(factor, info, source, false);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
            if (rangeAxis == null) continue;
            if (useAnchor) {
                double sourceY = source.getY();
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    sourceY = source.getX();
                }
                double anchorY = rangeAxis.java2DToValue(sourceY, info.getDataArea(), this.getRangeAxisEdge());
                rangeAxis.resizeRange2(factor, anchorY);
                continue;
            }
            rangeAxis.resizeRange(factor);
        }
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
            if (rangeAxis == null) continue;
            rangeAxis.zoomRange(lowerPercent, upperPercent);
        }
    }

    public boolean isDomainZoomable() {
        return true;
    }

    public boolean isRangeZoomable() {
        return true;
    }

    public int getSeriesCount() {
        int result = 0;
        XYDataset dataset = this.getDataset();
        if (dataset != null) {
            result = dataset.getSeriesCount();
        }
        return result;
    }

    public LegendItemCollection getFixedLegendItems() {
        return this.fixedLegendItems;
    }

    public void setFixedLegendItems(LegendItemCollection items) {
        this.fixedLegendItems = items;
        this.fireChangeEvent();
    }

    public LegendItemCollection getLegendItems() {
        if (this.fixedLegendItems != null) {
            return this.fixedLegendItems;
        }
        LegendItemCollection result = new LegendItemCollection();
        int count = this.datasets.size();
        for (int datasetIndex = 0; datasetIndex < count; ++datasetIndex) {
            XYDataset dataset = this.getDataset(datasetIndex);
            if (dataset == null) continue;
            XYItemRenderer renderer = this.getRenderer(datasetIndex);
            if (renderer == null) {
                renderer = this.getRenderer(0);
            }
            if (renderer == null) continue;
            int seriesCount = dataset.getSeriesCount();
            for (int i = 0; i < seriesCount; ++i) {
                LegendItem item;
                if (!renderer.isSeriesVisible(i) || !renderer.isSeriesVisibleInLegend(i) || (item = renderer.getLegendItem(datasetIndex, i)) == null) continue;
                result.add(item);
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYPlot)) {
            return false;
        }
        XYPlot that = (XYPlot)obj;
        if (this.weight != that.weight) {
            return false;
        }
        if (this.orientation != that.orientation) {
            return false;
        }
        if (!this.domainAxes.equals(that.domainAxes)) {
            return false;
        }
        if (!this.domainAxisLocations.equals(that.domainAxisLocations)) {
            return false;
        }
        if (this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (this.rangeGridlinesVisible != that.rangeGridlinesVisible) {
            return false;
        }
        if (this.domainMinorGridlinesVisible != that.domainMinorGridlinesVisible) {
            return false;
        }
        if (this.rangeMinorGridlinesVisible != that.rangeMinorGridlinesVisible) {
            return false;
        }
        if (this.domainZeroBaselineVisible != that.domainZeroBaselineVisible) {
            return false;
        }
        if (this.rangeZeroBaselineVisible != that.rangeZeroBaselineVisible) {
            return false;
        }
        if (this.domainCrosshairVisible != that.domainCrosshairVisible) {
            return false;
        }
        if (this.domainCrosshairValue != that.domainCrosshairValue) {
            return false;
        }
        if (this.domainCrosshairLockedOnData != that.domainCrosshairLockedOnData) {
            return false;
        }
        if (this.rangeCrosshairVisible != that.rangeCrosshairVisible) {
            return false;
        }
        if (this.rangeCrosshairValue != that.rangeCrosshairValue) {
            return false;
        }
        if (!ObjectUtilities.equal(this.axisOffset, that.axisOffset)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.renderers, that.renderers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeAxes, that.rangeAxes)) {
            return false;
        }
        if (!this.rangeAxisLocations.equals(that.rangeAxisLocations)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.datasetToDomainAxesMap, that.datasetToDomainAxesMap)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.datasetToRangeAxesMap, that.datasetToRangeAxesMap)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainGridlineStroke, that.domainGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeGridlineStroke, that.rangeGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainMinorGridlineStroke, that.domainMinorGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainMinorGridlinePaint, that.domainMinorGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeMinorGridlineStroke, that.rangeMinorGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeMinorGridlinePaint, that.rangeMinorGridlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainZeroBaselinePaint, that.domainZeroBaselinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainZeroBaselineStroke, that.domainZeroBaselineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeZeroBaselinePaint, that.rangeZeroBaselinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeZeroBaselineStroke, that.rangeZeroBaselineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainCrosshairStroke, that.domainCrosshairStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainCrosshairPaint, that.domainCrosshairPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeCrosshairStroke, that.rangeCrosshairStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.foregroundDomainMarkers, that.foregroundDomainMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundDomainMarkers, that.backgroundDomainMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.foregroundDomainMarkers, that.foregroundDomainMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundDomainMarkers, that.backgroundDomainMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.annotations, that.annotations)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainTickBandPaint, that.domainTickBandPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeTickBandPaint, that.rangeTickBandPaint)) {
            return false;
        }
        if (!this.quadrantOrigin.equals(that.quadrantOrigin)) {
            return false;
        }
        for (int i = 0; i < 4; ++i) {
            if (PaintUtilities.equal(this.quadrantPaint[i], that.quadrantPaint[i])) continue;
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        ValueAxis clonedAxis;
        ValueAxis axis;
        int i;
        XYPlot clone = (XYPlot)super.clone();
        clone.domainAxes = (ObjectList)ObjectUtilities.clone(this.domainAxes);
        for (i = 0; i < this.domainAxes.size(); ++i) {
            axis = (ValueAxis)this.domainAxes.get(i);
            if (axis == null) continue;
            clonedAxis = (ValueAxis)axis.clone();
            clone.domainAxes.set(i, clonedAxis);
            clonedAxis.setPlot(clone);
            clonedAxis.addChangeListener(clone);
        }
        clone.domainAxisLocations = (ObjectList)this.domainAxisLocations.clone();
        clone.rangeAxes = (ObjectList)ObjectUtilities.clone(this.rangeAxes);
        for (i = 0; i < this.rangeAxes.size(); ++i) {
            axis = (ValueAxis)this.rangeAxes.get(i);
            if (axis == null) continue;
            clonedAxis = (ValueAxis)axis.clone();
            clone.rangeAxes.set(i, clonedAxis);
            clonedAxis.setPlot(clone);
            clonedAxis.addChangeListener(clone);
        }
        clone.rangeAxisLocations = (ObjectList)ObjectUtilities.clone(this.rangeAxisLocations);
        clone.datasets = (ObjectList)ObjectUtilities.clone(this.datasets);
        for (i = 0; i < clone.datasets.size(); ++i) {
            XYDataset d = this.getDataset(i);
            if (d == null) continue;
            d.addChangeListener(clone);
        }
        clone.datasetToDomainAxesMap = new TreeMap();
        clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
        clone.datasetToRangeAxesMap = new TreeMap();
        clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);
        clone.renderers = (ObjectList)ObjectUtilities.clone(this.renderers);
        for (i = 0; i < this.renderers.size(); ++i) {
            XYItemRenderer renderer2 = (XYItemRenderer)this.renderers.get(i);
            if (!(renderer2 instanceof PublicCloneable)) continue;
            PublicCloneable pc = (PublicCloneable)((Object)renderer2);
            clone.renderers.set(i, pc.clone());
        }
        clone.foregroundDomainMarkers = (Map)ObjectUtilities.clone(this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = (Map)ObjectUtilities.clone(this.backgroundDomainMarkers);
        clone.foregroundRangeMarkers = (Map)ObjectUtilities.clone(this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = (Map)ObjectUtilities.clone(this.backgroundRangeMarkers);
        clone.annotations = (List)ObjectUtilities.deepClone(this.annotations);
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = (AxisSpace)ObjectUtilities.clone(this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = (AxisSpace)ObjectUtilities.clone(this.fixedRangeAxisSpace);
        }
        clone.quadrantOrigin = (Point2D)ObjectUtilities.clone(this.quadrantOrigin);
        clone.quadrantPaint = (Paint[])this.quadrantPaint.clone();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
        SerialUtilities.writeStroke(this.domainMinorGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainMinorGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeMinorGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeMinorGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeZeroBaselineStroke, stream);
        SerialUtilities.writePaint(this.rangeZeroBaselinePaint, stream);
        SerialUtilities.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtilities.writePaint(this.domainCrosshairPaint, stream);
        SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
        SerialUtilities.writePaint(this.domainTickBandPaint, stream);
        SerialUtilities.writePaint(this.rangeTickBandPaint, stream);
        SerialUtilities.writePoint2D(this.quadrantOrigin, stream);
        for (int i = 0; i < 4; ++i) {
            SerialUtilities.writePaint(this.quadrantPaint[i], stream);
        }
        SerialUtilities.writeStroke(this.domainZeroBaselineStroke, stream);
        SerialUtilities.writePaint(this.domainZeroBaselinePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        this.domainMinorGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainMinorGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeMinorGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeMinorGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeZeroBaselineStroke = SerialUtilities.readStroke(stream);
        this.rangeZeroBaselinePaint = SerialUtilities.readPaint(stream);
        this.domainCrosshairStroke = SerialUtilities.readStroke(stream);
        this.domainCrosshairPaint = SerialUtilities.readPaint(stream);
        this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
        this.domainTickBandPaint = SerialUtilities.readPaint(stream);
        this.rangeTickBandPaint = SerialUtilities.readPaint(stream);
        this.quadrantOrigin = SerialUtilities.readPoint2D(stream);
        this.quadrantPaint = new Paint[4];
        for (int i = 0; i < 4; ++i) {
            this.quadrantPaint[i] = SerialUtilities.readPaint(stream);
        }
        this.domainZeroBaselineStroke = SerialUtilities.readStroke(stream);
        this.domainZeroBaselinePaint = SerialUtilities.readPaint(stream);
        int domainAxisCount = this.domainAxes.size();
        for (int i = 0; i < domainAxisCount; ++i) {
            Axis axis = (Axis)this.domainAxes.get(i);
            if (axis == null) continue;
            axis.setPlot(this);
            axis.addChangeListener(this);
        }
        int rangeAxisCount = this.rangeAxes.size();
        for (int i = 0; i < rangeAxisCount; ++i) {
            Axis axis = (Axis)this.rangeAxes.get(i);
            if (axis == null) continue;
            axis.setPlot(this);
            axis.addChangeListener(this);
        }
        int datasetCount = this.datasets.size();
        for (int i = 0; i < datasetCount; ++i) {
            Dataset dataset = (Dataset)this.datasets.get(i);
            if (dataset == null) continue;
            dataset.addChangeListener(this);
        }
        int rendererCount = this.renderers.size();
        for (int i = 0; i < rendererCount; ++i) {
            XYItemRenderer renderer = (XYItemRenderer)this.renderers.get(i);
            if (renderer == null) continue;
            renderer.addChangeListener(this);
        }
    }
}

