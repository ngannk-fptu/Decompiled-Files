/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;

public class JFreeChartEntity
extends ChartEntity {
    private static final long serialVersionUID = -4445994133561919083L;
    private JFreeChart chart;

    public JFreeChartEntity(Shape area, JFreeChart chart) {
        this(area, chart, null);
    }

    public JFreeChartEntity(Shape area, JFreeChart chart, String toolTipText) {
        this(area, chart, toolTipText, null);
    }

    public JFreeChartEntity(Shape area, JFreeChart chart, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        if (chart == null) {
            throw new IllegalArgumentException("Null 'chart' argument.");
        }
        this.chart = chart;
    }

    public JFreeChart getChart() {
        return this.chart;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("JFreeChartEntity: ");
        buf.append("tooltip = ");
        buf.append(this.getToolTipText());
        return buf.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof JFreeChartEntity)) {
            return false;
        }
        JFreeChartEntity that = (JFreeChartEntity)obj;
        if (!this.getArea().equals(that.getArea())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.getToolTipText(), that.getToolTipText())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.getURLText(), that.getURLText())) {
            return false;
        }
        return this.chart.equals(that.chart);
    }

    public int hashCode() {
        int result = 39;
        result = HashUtilities.hashCode(result, this.getToolTipText());
        result = HashUtilities.hashCode(result, this.getURLText());
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.getArea(), stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.setArea(SerialUtilities.readShape(stream));
    }
}

