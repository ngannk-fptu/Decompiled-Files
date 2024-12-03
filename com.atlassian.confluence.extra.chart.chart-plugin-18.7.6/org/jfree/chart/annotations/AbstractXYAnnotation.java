/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.util.ObjectUtilities;

public abstract class AbstractXYAnnotation
implements XYAnnotation {
    private String toolTipText = null;
    private String url = null;

    protected AbstractXYAnnotation() {
    }

    public String getToolTipText() {
        return this.toolTipText;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public abstract void draw(Graphics2D var1, XYPlot var2, Rectangle2D var3, ValueAxis var4, ValueAxis var5, int var6, PlotRenderingInfo var7);

    protected void addEntity(PlotRenderingInfo info, Shape hotspot, int rendererIndex, String toolTipText, String urlText) {
        if (info == null) {
            return;
        }
        EntityCollection entities = info.getOwner().getEntityCollection();
        if (entities == null) {
            return;
        }
        XYAnnotationEntity entity = new XYAnnotationEntity(hotspot, rendererIndex, toolTipText, urlText);
        entities.add(entity);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractXYAnnotation)) {
            return false;
        }
        AbstractXYAnnotation that = (AbstractXYAnnotation)obj;
        if (!ObjectUtilities.equal(this.toolTipText, that.toolTipText)) {
            return false;
        }
        return ObjectUtilities.equal(this.url, that.url);
    }

    public int hashCode() {
        int result = 193;
        if (this.toolTipText != null) {
            result = 37 * result + this.toolTipText.hashCode();
        }
        if (this.url != null) {
            result = 37 * result + this.url.hashCode();
        }
        return result;
    }
}

