/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

public class XYSplineRenderer
extends XYLineAndShapeRenderer {
    private Vector points;
    private int precision;

    public XYSplineRenderer() {
        this(5);
    }

    public XYSplineRenderer(int precision) {
        if (precision <= 0) {
            throw new IllegalArgumentException("Requires precision > 0.");
        }
        this.precision = precision;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int p) {
        if (p <= 0) {
            throw new IllegalArgumentException("Requires p > 0.");
        }
        this.precision = p;
        this.fireChangeEvent();
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYLineAndShapeRenderer.State state = (XYLineAndShapeRenderer.State)super.initialise(g2, dataArea, plot, data, info);
        state.setProcessVisibleItemsOnly(false);
        this.points = new Vector();
        this.setDrawSeriesLineAsPath(true);
        return state;
    }

    protected void drawPrimaryLineAsPath(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        ControlPoint p;
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (!(Double.isNaN(transX1) || Double.isNaN(transY1) || this.points.contains(p = new ControlPoint(plot.getOrientation() == PlotOrientation.HORIZONTAL ? (float)transY1 : (float)transX1, plot.getOrientation() == PlotOrientation.HORIZONTAL ? (float)transX1 : (float)transY1)))) {
            this.points.add(p);
        }
        if (item == dataset.getItemCount(series) - 1) {
            XYLineAndShapeRenderer.State s = (XYLineAndShapeRenderer.State)state;
            if (this.points.size() > 1) {
                ControlPoint cp0 = (ControlPoint)this.points.get(0);
                s.seriesPath.moveTo(cp0.x, cp0.y);
                if (this.points.size() == 2) {
                    ControlPoint cp1 = (ControlPoint)this.points.get(1);
                    s.seriesPath.lineTo(cp1.x, cp1.y);
                } else {
                    int i;
                    int i2;
                    int np = this.points.size();
                    float[] d = new float[np];
                    float[] x = new float[np];
                    float oldy = 0.0f;
                    float oldt = 0.0f;
                    float[] a = new float[np];
                    float[] h = new float[np];
                    for (i2 = 0; i2 < np; ++i2) {
                        ControlPoint cpi = (ControlPoint)this.points.get(i2);
                        x[i2] = cpi.x;
                        d[i2] = cpi.y;
                    }
                    for (i2 = 1; i2 <= np - 1; ++i2) {
                        h[i2] = x[i2] - x[i2 - 1];
                    }
                    float[] sub = new float[np - 1];
                    float[] diag = new float[np - 1];
                    float[] sup = new float[np - 1];
                    for (i = 1; i <= np - 2; ++i) {
                        diag[i] = (h[i] + h[i + 1]) / 3.0f;
                        sup[i] = h[i + 1] / 6.0f;
                        sub[i] = h[i] / 6.0f;
                        a[i] = (d[i + 1] - d[i]) / h[i + 1] - (d[i] - d[i - 1]) / h[i];
                    }
                    this.solveTridiag(sub, diag, sup, a, np - 2);
                    oldt = x[0];
                    oldy = d[0];
                    s.seriesPath.moveTo(oldt, oldy);
                    for (i = 1; i <= np - 1; ++i) {
                        for (int j = 1; j <= this.precision; ++j) {
                            float t1 = h[i] * (float)j / (float)this.precision;
                            float t2 = h[i] - t1;
                            float y = ((-a[i - 1] / 6.0f * (t2 + h[i]) * t1 + d[i - 1]) * t2 + (-a[i] / 6.0f * (t1 + h[i]) * t2 + d[i]) * t1) / h[i];
                            float t = x[i - 1] + t1;
                            s.seriesPath.lineTo(t, y);
                            oldt = t;
                            oldy = y;
                        }
                    }
                }
                this.drawFirstPassShape(g2, pass, series, item, s.seriesPath);
            }
            this.points = new Vector();
        }
    }

    private void solveTridiag(float[] sub, float[] diag, float[] sup, float[] b, int n) {
        int i;
        for (i = 2; i <= n; ++i) {
            sub[i] = sub[i] / diag[i - 1];
            diag[i] = diag[i] - sub[i] * sup[i - 1];
            b[i] = b[i] - sub[i] * b[i - 1];
        }
        b[n] = b[n] / diag[n];
        for (i = n - 1; i >= 1; --i) {
            b[i] = (b[i] - sup[i] * b[i + 1]) / diag[i];
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSplineRenderer)) {
            return false;
        }
        XYSplineRenderer that = (XYSplineRenderer)obj;
        if (this.precision != that.precision) {
            return false;
        }
        return super.equals(obj);
    }

    class ControlPoint {
        public float x;
        public float y;

        public ControlPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ControlPoint)) {
                return false;
            }
            ControlPoint that = (ControlPoint)obj;
            return this.x == that.x;
        }
    }
}

