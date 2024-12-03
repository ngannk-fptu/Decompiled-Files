/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.renderer.Outlier;

public class OutlierList {
    private List outliers = new ArrayList();
    private Outlier averagedOutlier;
    private boolean multiple = false;

    public OutlierList(Outlier outlier) {
        this.setAveragedOutlier(outlier);
    }

    public boolean add(Outlier outlier) {
        return this.outliers.add(outlier);
    }

    public int getItemCount() {
        return this.outliers.size();
    }

    public Outlier getAveragedOutlier() {
        return this.averagedOutlier;
    }

    public void setAveragedOutlier(Outlier averagedOutlier) {
        this.averagedOutlier = averagedOutlier;
    }

    public boolean isMultiple() {
        return this.multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isOverlapped(Outlier other) {
        if (other == null) {
            return false;
        }
        boolean result = other.overlaps(this.getAveragedOutlier());
        return result;
    }

    public void updateAveragedOutlier() {
        double totalXCoords = 0.0;
        double totalYCoords = 0.0;
        int size = this.getItemCount();
        Iterator iterator = this.outliers.iterator();
        while (iterator.hasNext()) {
            Outlier o = (Outlier)iterator.next();
            totalXCoords += o.getX();
            totalYCoords += o.getY();
        }
        this.getAveragedOutlier().getPoint().setLocation(new Point2D.Double(totalXCoords / (double)size, totalYCoords / (double)size));
    }
}

