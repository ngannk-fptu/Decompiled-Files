/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlotState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

public class PiePlot3D
extends PiePlot
implements Serializable {
    private static final long serialVersionUID = 3408984188945161432L;
    private double depthFactor = 0.12;
    private boolean darkerSides = false;

    public PiePlot3D() {
        this(null);
    }

    public PiePlot3D(PieDataset dataset) {
        super(dataset);
        this.setCircular(false, false);
    }

    public double getDepthFactor() {
        return this.depthFactor;
    }

    public void setDepthFactor(double factor) {
        this.depthFactor = factor;
        this.fireChangeEvent();
    }

    public boolean getDarkerSides() {
        return this.darkerSides;
    }

    public void setDarkerSides(boolean darker) {
        this.darkerSides = darker;
        this.fireChangeEvent();
    }

    public void draw(Graphics2D g2, Rectangle2D plotArea, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        Comparable key;
        Arc2D segment;
        Stroke outlineStroke;
        Paint outlinePaint;
        Paint paint;
        List sectionKeys;
        RectangleInsets insets = this.getInsets();
        insets.trim(plotArea);
        Rectangle2D originalPlotArea = (Rectangle2D)plotArea.clone();
        if (info != null) {
            info.setPlotArea(plotArea);
            info.setDataArea(plotArea);
        }
        this.drawBackground(g2, plotArea);
        Shape savedClip = g2.getClip();
        g2.clip(plotArea);
        double gapPercent = this.getInteriorGap();
        double labelPercent = 0.0;
        if (this.getLabelGenerator() != null) {
            labelPercent = this.getLabelGap() + this.getMaximumLabelWidth();
        }
        double gapHorizontal = plotArea.getWidth() * (gapPercent + labelPercent) * 2.0;
        double gapVertical = plotArea.getHeight() * gapPercent * 2.0;
        double linkX = plotArea.getX() + gapHorizontal / 2.0;
        double linkY = plotArea.getY() + gapVertical / 2.0;
        double linkW = plotArea.getWidth() - gapHorizontal;
        double linkH = plotArea.getHeight() - gapVertical;
        if (this.isCircular()) {
            double min = Math.min(linkW, linkH) / 2.0;
            linkX = (linkX + linkX + linkW) / 2.0 - min;
            linkY = (linkY + linkY + linkH) / 2.0 - min;
            linkW = 2.0 * min;
            linkH = 2.0 * min;
        }
        PiePlotState state = this.initialise(g2, plotArea, this, null, info);
        Rectangle2D.Double linkAreaXX = new Rectangle2D.Double(linkX, linkY, linkW, linkH * (1.0 - this.depthFactor));
        state.setLinkArea(linkAreaXX);
        double hh = linkW * this.getLabelLinkMargin();
        double vv = linkH * this.getLabelLinkMargin();
        Rectangle2D.Double explodeArea = new Rectangle2D.Double(linkX + hh / 2.0, linkY + vv / 2.0, linkW - hh, linkH - vv);
        state.setExplodedPieArea(explodeArea);
        double maximumExplodePercent = this.getMaximumExplodePercent();
        double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);
        double h1 = ((RectangularShape)explodeArea).getWidth() * percent;
        double v1 = ((RectangularShape)explodeArea).getHeight() * percent;
        Rectangle2D.Double pieArea = new Rectangle2D.Double(((RectangularShape)explodeArea).getX() + h1 / 2.0, ((RectangularShape)explodeArea).getY() + v1 / 2.0, ((RectangularShape)explodeArea).getWidth() - h1, ((RectangularShape)explodeArea).getHeight() - v1);
        int depth = (int)(((RectangularShape)pieArea).getHeight() * this.depthFactor);
        Rectangle2D.Double linkArea = new Rectangle2D.Double(linkX, linkY, linkW, linkH - (double)depth);
        state.setLinkArea(linkArea);
        state.setPieArea(pieArea);
        state.setPieCenterX(pieArea.getCenterX());
        state.setPieCenterY(pieArea.getCenterY() - (double)depth / 2.0);
        state.setPieWRadius(((RectangularShape)pieArea).getWidth() / 2.0);
        state.setPieHRadius((((RectangularShape)pieArea).getHeight() - (double)depth) / 2.0);
        PieDataset dataset = this.getDataset();
        if (DatasetUtilities.isEmptyOrNull(this.getDataset())) {
            this.drawNoDataMessage(g2, plotArea);
            g2.setClip(savedClip);
            this.drawOutline(g2, plotArea);
            return;
        }
        if ((double)dataset.getKeys().size() > plotArea.getWidth()) {
            String text = "Too many elements";
            Font sfont = new Font("dialog", 1, 10);
            g2.setFont(sfont);
            FontMetrics fm = g2.getFontMetrics(sfont);
            int stringWidth = fm.stringWidth(text);
            g2.drawString(text, (int)(plotArea.getX() + (plotArea.getWidth() - (double)stringWidth) / 2.0), (int)(plotArea.getY() + plotArea.getHeight() / 2.0));
            return;
        }
        if (this.isCircular()) {
            double min = Math.min(plotArea.getWidth(), plotArea.getHeight()) / 2.0;
            plotArea = new Rectangle2D.Double(plotArea.getCenterX() - min, plotArea.getCenterY() - min, 2.0 * min, 2.0 * min);
        }
        if ((sectionKeys = dataset.getKeys()).size() == 0) {
            return;
        }
        double arcX = ((RectangularShape)pieArea).getX();
        double arcY = ((RectangularShape)pieArea).getY();
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
        double totalValue = DatasetUtilities.calculatePieDatasetTotal(dataset);
        double runningTotal = 0.0;
        if (depth < 0) {
            return;
        }
        ArrayList<Arc2D.Double> arcList = new ArrayList<Arc2D.Double>();
        Iterator iterator = sectionKeys.iterator();
        while (iterator.hasNext()) {
            double angle1;
            double direction;
            Comparable currentKey = (Comparable)iterator.next();
            Number dataValue = dataset.getValue(currentKey);
            if (dataValue == null) {
                arcList.add(null);
                continue;
            }
            double value = dataValue.doubleValue();
            if (value <= 0.0) {
                arcList.add(null);
                continue;
            }
            double startAngle = this.getStartAngle();
            double angle2 = startAngle + (direction = this.getDirection().getFactor()) * (runningTotal + value) * 360.0 / totalValue;
            if (Math.abs(angle2 - (angle1 = startAngle + direction * (runningTotal * 360.0) / totalValue)) > this.getMinimumArcAngleToDraw()) {
                arcList.add(new Arc2D.Double(arcX, arcY + (double)depth, ((RectangularShape)pieArea).getWidth(), ((RectangularShape)pieArea).getHeight() - (double)depth, angle1, angle2 - angle1, 2));
            } else {
                arcList.add(null);
            }
            runningTotal += value;
        }
        Shape oldClip = g2.getClip();
        Ellipse2D.Double top = new Ellipse2D.Double(((RectangularShape)pieArea).getX(), ((RectangularShape)pieArea).getY(), ((RectangularShape)pieArea).getWidth(), ((RectangularShape)pieArea).getHeight() - (double)depth);
        Ellipse2D.Double bottom = new Ellipse2D.Double(((RectangularShape)pieArea).getX(), ((RectangularShape)pieArea).getY() + (double)depth, ((RectangularShape)pieArea).getWidth(), ((RectangularShape)pieArea).getHeight() - (double)depth);
        Rectangle2D.Double lower = new Rectangle2D.Double(((RectangularShape)top).getX(), top.getCenterY(), ((RectangularShape)pieArea).getWidth(), bottom.getMaxY() - top.getCenterY());
        Rectangle2D.Double upper = new Rectangle2D.Double(((RectangularShape)pieArea).getX(), ((RectangularShape)top).getY(), ((RectangularShape)pieArea).getWidth(), bottom.getCenterY() - ((RectangularShape)top).getY());
        Area a = new Area(top);
        a.add(new Area(lower));
        Area b = new Area(bottom);
        b.add(new Area(upper));
        Area pie = new Area(a);
        pie.intersect(b);
        Area front = new Area(pie);
        front.subtract(new Area(top));
        Area back = new Area(pie);
        back.subtract(new Area(bottom));
        Arc2D.Double arc = new Arc2D.Double(arcX, arcY + (double)depth, ((RectangularShape)pieArea).getWidth(), ((RectangularShape)pieArea).getHeight() - (double)depth, 0.0, 360.0, 2);
        int categoryCount = arcList.size();
        for (int categoryIndex = 0; categoryIndex < categoryCount; ++categoryIndex) {
            arc = (Arc2D.Double)arcList.get(categoryIndex);
            if (arc == null) continue;
            Comparable key2 = this.getSectionKey(categoryIndex);
            paint = this.lookupSectionPaint(key2);
            outlinePaint = this.lookupSectionOutlinePaint(key2);
            outlineStroke = this.lookupSectionOutlineStroke(key2);
            g2.setPaint(paint);
            g2.fill(arc);
            g2.setPaint(outlinePaint);
            g2.setStroke(outlineStroke);
            g2.draw(arc);
            g2.setPaint(paint);
            Point2D p1 = arc.getStartPoint();
            int[] xs = new int[]{(int)arc.getCenterX(), (int)arc.getCenterX(), (int)p1.getX(), (int)p1.getX()};
            int[] ys = new int[]{(int)arc.getCenterY(), (int)arc.getCenterY() - depth, (int)p1.getY() - depth, (int)p1.getY()};
            Polygon polygon = new Polygon(xs, ys, 4);
            g2.setPaint(Color.lightGray);
            g2.fill(polygon);
            g2.setPaint(outlinePaint);
            g2.setStroke(outlineStroke);
            g2.draw(polygon);
            g2.setPaint(paint);
        }
        g2.setPaint(Color.gray);
        g2.fill(back);
        g2.fill(front);
        int cat = 0;
        iterator = arcList.iterator();
        while (iterator.hasNext()) {
            segment = (Arc2D)iterator.next();
            if (segment != null) {
                key = this.getSectionKey(cat);
                paint = this.lookupSectionPaint(key);
                outlinePaint = this.lookupSectionOutlinePaint(key);
                outlineStroke = this.lookupSectionOutlineStroke(key);
                this.drawSide(g2, pieArea, segment, front, back, paint, outlinePaint, outlineStroke, false, true);
            }
            ++cat;
        }
        cat = 0;
        iterator = arcList.iterator();
        while (iterator.hasNext()) {
            segment = (Arc2D)iterator.next();
            if (segment != null) {
                key = this.getSectionKey(cat);
                paint = this.lookupSectionPaint(key);
                outlinePaint = this.lookupSectionOutlinePaint(key);
                outlineStroke = this.lookupSectionOutlineStroke(key);
                this.drawSide(g2, pieArea, segment, front, back, paint, outlinePaint, outlineStroke, true, false);
            }
            ++cat;
        }
        g2.setClip(oldClip);
        for (int sectionIndex = 0; sectionIndex < categoryCount; ++sectionIndex) {
            EntityCollection entities;
            arc = (Arc2D.Double)arcList.get(sectionIndex);
            if (arc == null) continue;
            Arc2D.Double upperArc = new Arc2D.Double(arcX, arcY, ((RectangularShape)pieArea).getWidth(), ((RectangularShape)pieArea).getHeight() - (double)depth, arc.getAngleStart(), arc.getAngleExtent(), 2);
            Comparable currentKey = (Comparable)sectionKeys.get(sectionIndex);
            paint = this.lookupSectionPaint(currentKey, true);
            outlinePaint = this.lookupSectionOutlinePaint(currentKey);
            outlineStroke = this.lookupSectionOutlineStroke(currentKey);
            g2.setPaint(paint);
            g2.fill(upperArc);
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(upperArc);
            if (info == null || (entities = info.getOwner().getEntityCollection()) == null) continue;
            String tip = null;
            PieToolTipGenerator tipster = this.getToolTipGenerator();
            if (tipster != null) {
                tip = tipster.generateToolTip(dataset, currentKey);
            }
            String url = null;
            if (this.getURLGenerator() != null) {
                url = this.getURLGenerator().generateURL(dataset, currentKey, this.getPieIndex());
            }
            PieSectionEntity entity = new PieSectionEntity(upperArc, dataset, this.getPieIndex(), sectionIndex, currentKey, tip, url);
            entities.add(entity);
        }
        List keys = dataset.getKeys();
        Rectangle2D.Double adjustedPlotArea = new Rectangle2D.Double(originalPlotArea.getX(), originalPlotArea.getY(), originalPlotArea.getWidth(), originalPlotArea.getHeight() - (double)depth);
        if (this.getSimpleLabels()) {
            this.drawSimpleLabels(g2, keys, totalValue, adjustedPlotArea, linkArea, state);
        } else {
            this.drawLabels(g2, keys, totalValue, adjustedPlotArea, linkArea, state);
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        this.drawOutline(g2, originalPlotArea);
    }

    protected void drawSide(Graphics2D g2, Rectangle2D plotArea, Arc2D arc, Area front, Area back, Paint paint, Paint outlinePaint, Stroke outlineStroke, boolean drawFront, boolean drawBack) {
        if (this.getDarkerSides() && paint instanceof Color) {
            Color c = (Color)paint;
            c = c.darker();
            paint = c;
        }
        double start = arc.getAngleStart();
        double extent = arc.getAngleExtent();
        double end = start + extent;
        g2.setStroke(outlineStroke);
        if (extent < 0.0) {
            if (this.isAngleAtFront(start)) {
                if (!this.isAngleAtBack(end)) {
                    if (extent > -180.0) {
                        if (drawFront) {
                            Area side = new Area(new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(), arc.getStartPoint().getX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                            side.intersect(front);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    } else {
                        Area side1 = new Area(new Rectangle2D.Double(plotArea.getX(), plotArea.getY(), arc.getStartPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                        side1.intersect(front);
                        Area side2 = new Area(new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                        side2.intersect(front);
                        g2.setPaint(paint);
                        if (drawFront) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }
                        if (drawBack) {
                            g2.fill(back);
                        }
                        g2.setPaint(outlinePaint);
                        if (drawFront) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }
                        if (drawBack) {
                            g2.draw(back);
                        }
                    }
                } else {
                    if (drawBack) {
                        Area side2 = new Area(new Rectangle2D.Double(plotArea.getX(), plotArea.getY(), arc.getEndPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                        side2.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }
                    if (drawFront) {
                        Area side1 = new Area(new Rectangle2D.Double(plotArea.getX(), plotArea.getY(), arc.getStartPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                        side1.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }
                }
            } else if (!this.isAngleAtFront(end)) {
                if (extent > -180.0) {
                    if (drawBack) {
                        Area side = new Area(new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(), arc.getEndPoint().getX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                        side.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side);
                        g2.setPaint(outlinePaint);
                        g2.draw(side);
                    }
                } else {
                    Area side1 = new Area(new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                    side1.intersect(back);
                    Area side2 = new Area(new Rectangle2D.Double(plotArea.getX(), plotArea.getY(), arc.getEndPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                    side2.intersect(back);
                    g2.setPaint(paint);
                    if (drawBack) {
                        g2.fill(side1);
                        g2.fill(side2);
                    }
                    if (drawFront) {
                        g2.fill(front);
                    }
                    g2.setPaint(outlinePaint);
                    if (drawBack) {
                        g2.draw(side1);
                        g2.draw(side2);
                    }
                    if (drawFront) {
                        g2.draw(front);
                    }
                }
            } else {
                if (drawBack) {
                    Area side1 = new Area(new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                    side1.intersect(back);
                    g2.setPaint(paint);
                    g2.fill(side1);
                    g2.setPaint(outlinePaint);
                    g2.draw(side1);
                }
                if (drawFront) {
                    Area side2 = new Area(new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                    side2.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(side2);
                    g2.setPaint(outlinePaint);
                    g2.draw(side2);
                }
            }
        } else if (extent > 0.0) {
            if (this.isAngleAtFront(start)) {
                if (!this.isAngleAtBack(end)) {
                    if (extent < 180.0) {
                        if (drawFront) {
                            Area side = new Area(new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(), arc.getEndPoint().getX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                            side.intersect(front);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    } else {
                        Area side1 = new Area(new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                        side1.intersect(front);
                        Area side2 = new Area(new Rectangle2D.Double(plotArea.getX(), plotArea.getY(), arc.getEndPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                        side2.intersect(front);
                        g2.setPaint(paint);
                        if (drawFront) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }
                        if (drawBack) {
                            g2.fill(back);
                        }
                        g2.setPaint(outlinePaint);
                        if (drawFront) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }
                        if (drawBack) {
                            g2.draw(back);
                        }
                    }
                } else {
                    if (drawBack) {
                        Area side2 = new Area(new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                        side2.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }
                    if (drawFront) {
                        Area side1 = new Area(new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                        side1.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }
                }
            } else if (!this.isAngleAtFront(end)) {
                if (extent < 180.0) {
                    if (drawBack) {
                        Area side = new Area(new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(), arc.getStartPoint().getX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                        side.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side);
                        g2.setPaint(outlinePaint);
                        g2.draw(side);
                    }
                } else {
                    Area side1 = new Area(new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                    side1.intersect(back);
                    Area side2 = new Area(new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                    side2.intersect(back);
                    g2.setPaint(paint);
                    if (drawBack) {
                        g2.fill(side1);
                        g2.fill(side2);
                    }
                    if (drawFront) {
                        g2.fill(front);
                    }
                    g2.setPaint(outlinePaint);
                    if (drawBack) {
                        g2.draw(side1);
                        g2.draw(side2);
                    }
                    if (drawFront) {
                        g2.draw(front);
                    }
                }
            } else {
                if (drawBack) {
                    Area side1 = new Area(new Rectangle2D.Double(plotArea.getX(), plotArea.getY(), arc.getStartPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                    side1.intersect(back);
                    g2.setPaint(paint);
                    g2.fill(side1);
                    g2.setPaint(outlinePaint);
                    g2.draw(side1);
                }
                if (drawFront) {
                    Area side2 = new Area(new Rectangle2D.Double(plotArea.getX(), plotArea.getY(), arc.getEndPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                    side2.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(side2);
                    g2.setPaint(outlinePaint);
                    g2.draw(side2);
                }
            }
        }
    }

    public String getPlotType() {
        return localizationResources.getString("Pie_3D_Plot");
    }

    private boolean isAngleAtFront(double angle) {
        return Math.sin(Math.toRadians(angle)) < 0.0;
    }

    private boolean isAngleAtBack(double angle) {
        return Math.sin(Math.toRadians(angle)) > 0.0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PiePlot3D)) {
            return false;
        }
        PiePlot3D that = (PiePlot3D)obj;
        if (this.depthFactor != that.depthFactor) {
            return false;
        }
        if (this.darkerSides != that.darkerSides) {
            return false;
        }
        return super.equals(obj);
    }
}

