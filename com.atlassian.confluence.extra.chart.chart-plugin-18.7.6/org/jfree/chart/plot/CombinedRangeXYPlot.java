/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;

public class CombinedRangeXYPlot
extends XYPlot
implements PlotChangeListener {
    private static final long serialVersionUID = -5177814085082031168L;
    private List subplots = new ArrayList();
    private double gap = 5.0;
    private transient Rectangle2D[] subplotAreas;

    public CombinedRangeXYPlot() {
        this(new NumberAxis());
    }

    public CombinedRangeXYPlot(ValueAxis rangeAxis) {
        super(null, null, rangeAxis, null);
    }

    public String getPlotType() {
        return localizationResources.getString("Combined_Range_XYPlot");
    }

    public double getGap() {
        return this.gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    public void add(XYPlot subplot) {
        this.add(subplot, 1);
    }

    public void add(XYPlot subplot, int weight) {
        if (weight <= 0) {
            String msg = "The 'weight' must be positive.";
            throw new IllegalArgumentException(msg);
        }
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        subplot.setRangeAxis(null);
        subplot.addChangeListener(this);
        this.subplots.add(subplot);
        this.configureRangeAxes();
        this.fireChangeEvent();
    }

    public void remove(XYPlot subplot) {
        if (subplot == null) {
            throw new IllegalArgumentException(" Null 'subplot' argument.");
        }
        int position = -1;
        int size = this.subplots.size();
        for (int i = 0; position == -1 && i < size; ++i) {
            if (this.subplots.get(i) != subplot) continue;
            position = i;
        }
        if (position != -1) {
            this.subplots.remove(position);
            subplot.setParent(null);
            subplot.removeChangeListener(this);
            this.configureRangeAxes();
            this.fireChangeEvent();
        }
    }

    public List getSubplots() {
        if (this.subplots != null) {
            return Collections.unmodifiableList(this.subplots);
        }
        return Collections.EMPTY_LIST;
    }

    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        AxisSpace space = new AxisSpace();
        PlotOrientation orientation = this.getOrientation();
        AxisSpace fixed = this.getFixedRangeAxisSpace();
        if (fixed != null) {
            if (orientation == PlotOrientation.VERTICAL) {
                space.setLeft(fixed.getLeft());
                space.setRight(fixed.getRight());
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                space.setTop(fixed.getTop());
                space.setBottom(fixed.getBottom());
            }
        } else {
            ValueAxis valueAxis = this.getRangeAxis();
            RectangleEdge valueEdge = Plot.resolveRangeAxisLocation(this.getRangeAxisLocation(), orientation);
            if (valueAxis != null) {
                space = valueAxis.reserveSpace(g2, this, plotArea, valueEdge, space);
            }
        }
        Rectangle2D adjustedPlotArea = space.shrink(plotArea, null);
        int n = this.subplots.size();
        int totalWeight = 0;
        for (int i = 0; i < n; ++i) {
            XYPlot sub = (XYPlot)this.subplots.get(i);
            totalWeight += sub.getWeight();
        }
        this.subplotAreas = new Rectangle2D[n];
        double x = adjustedPlotArea.getX();
        double y = adjustedPlotArea.getY();
        double usableSize = 0.0;
        if (orientation == PlotOrientation.VERTICAL) {
            usableSize = adjustedPlotArea.getWidth() - this.gap * (double)(n - 1);
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = adjustedPlotArea.getHeight() - this.gap * (double)(n - 1);
        }
        for (int i = 0; i < n; ++i) {
            XYPlot plot = (XYPlot)this.subplots.get(i);
            if (orientation == PlotOrientation.VERTICAL) {
                double w = usableSize * (double)plot.getWeight() / (double)totalWeight;
                this.subplotAreas[i] = new Rectangle2D.Double(x, y, w, adjustedPlotArea.getHeight());
                x = x + w + this.gap;
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                double h = usableSize * (double)plot.getWeight() / (double)totalWeight;
                this.subplotAreas[i] = new Rectangle2D.Double(x, y, adjustedPlotArea.getWidth(), h);
                y = y + h + this.gap;
            }
            AxisSpace subSpace = plot.calculateDomainAxisSpace(g2, this.subplotAreas[i], null);
            space.ensureAtLeast(subSpace);
        }
        return space;
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        if (info != null) {
            info.setPlotArea(area);
        }
        RectangleInsets insets = this.getInsets();
        insets.trim(area);
        AxisSpace space = this.calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.setFixedDomainAxisSpaceForSubplots(space);
        ValueAxis axis = this.getRangeAxis();
        RectangleEdge edge = this.getRangeAxisEdge();
        double cursor = RectangleEdge.coordinate(dataArea, edge);
        AxisState axisState = axis.draw(g2, cursor, area, dataArea, edge, info);
        if (parentState == null) {
            parentState = new PlotState();
        }
        parentState.getSharedAxisStates().put(axis, axisState);
        for (int i = 0; i < this.subplots.size(); ++i) {
            XYPlot plot = (XYPlot)this.subplots.get(i);
            PlotRenderingInfo subplotInfo = null;
            if (info != null) {
                subplotInfo = new PlotRenderingInfo(info.getOwner());
                info.addSubplotInfo(subplotInfo);
            }
            plot.draw(g2, this.subplotAreas[i], anchor, parentState, subplotInfo);
        }
        if (info != null) {
            info.setDataArea(dataArea);
        }
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = this.getFixedLegendItems();
        if (result == null) {
            result = new LegendItemCollection();
            if (this.subplots != null) {
                Iterator iterator = this.subplots.iterator();
                while (iterator.hasNext()) {
                    XYPlot plot = (XYPlot)iterator.next();
                    LegendItemCollection more = plot.getLegendItems();
                    result.addAll(more);
                }
            }
        }
        return result;
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
        this.zoomDomainAxes(factor, info, source, false);
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        XYPlot subplot = this.findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomDomainAxes(factor, info, source, useAnchor);
        } else {
            Iterator iterator = this.getSubplots().iterator();
            while (iterator.hasNext()) {
                subplot = (XYPlot)iterator.next();
                subplot.zoomDomainAxes(factor, info, source, useAnchor);
            }
        }
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        XYPlot subplot = this.findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomDomainAxes(lowerPercent, upperPercent, info, source);
        } else {
            Iterator iterator = this.getSubplots().iterator();
            while (iterator.hasNext()) {
                subplot = (XYPlot)iterator.next();
                subplot.zoomDomainAxes(lowerPercent, upperPercent, info, source);
            }
        }
    }

    public XYPlot findSubplot(PlotRenderingInfo info, Point2D source) {
        if (info == null) {
            throw new IllegalArgumentException("Null 'info' argument.");
        }
        if (source == null) {
            throw new IllegalArgumentException("Null 'source' argument.");
        }
        XYPlot result = null;
        int subplotIndex = info.getSubplotIndex(source);
        if (subplotIndex >= 0) {
            result = (XYPlot)this.subplots.get(subplotIndex);
        }
        return result;
    }

    public void setRenderer(XYItemRenderer renderer) {
        super.setRenderer(renderer);
        Iterator iterator = this.subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot)iterator.next();
            plot.setRenderer(renderer);
        }
    }

    public void setOrientation(PlotOrientation orientation) {
        super.setOrientation(orientation);
        Iterator iterator = this.subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot)iterator.next();
            plot.setOrientation(orientation);
        }
    }

    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        if (this.subplots != null) {
            Iterator iterator = this.subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot subplot = (XYPlot)iterator.next();
                result = Range.combine(result, subplot.getDataRange(axis));
            }
        }
        return result;
    }

    protected void setFixedDomainAxisSpaceForSubplots(AxisSpace space) {
        Iterator iterator = this.subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot)iterator.next();
            plot.setFixedDomainAxisSpace(space, false);
        }
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            for (int i = 0; i < this.subplots.size(); ++i) {
                XYPlot subplot = (XYPlot)this.subplots.get(i);
                PlotRenderingInfo subplotInfo = info.getSubplotInfo(i);
                subplot.handleClick(x, y, subplotInfo);
            }
        }
    }

    public void plotChanged(PlotChangeEvent event) {
        this.notifyListeners(event);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CombinedRangeXYPlot)) {
            return false;
        }
        CombinedRangeXYPlot that = (CombinedRangeXYPlot)obj;
        if (this.gap != that.gap) {
            return false;
        }
        if (!ObjectUtilities.equal(this.subplots, that.subplots)) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        CombinedRangeXYPlot result = (CombinedRangeXYPlot)super.clone();
        result.subplots = (List)ObjectUtilities.deepClone(this.subplots);
        Iterator it = result.subplots.iterator();
        while (it.hasNext()) {
            Plot child = (Plot)it.next();
            child.setParent(result);
        }
        ValueAxis rangeAxis = result.getRangeAxis();
        if (rangeAxis != null) {
            rangeAxis.configure();
        }
        return result;
    }
}

