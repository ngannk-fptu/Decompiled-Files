/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.renderer.Outlier;
import org.jfree.chart.renderer.OutlierList;

public class OutlierListCollection {
    private List outlierLists = new ArrayList();
    private boolean highFarOut = false;
    private boolean lowFarOut = false;

    public boolean isHighFarOut() {
        return this.highFarOut;
    }

    public void setHighFarOut(boolean farOut) {
        this.highFarOut = farOut;
    }

    public boolean isLowFarOut() {
        return this.lowFarOut;
    }

    public void setLowFarOut(boolean farOut) {
        this.lowFarOut = farOut;
    }

    public boolean add(Outlier outlier) {
        if (this.outlierLists.isEmpty()) {
            return this.outlierLists.add(new OutlierList(outlier));
        }
        boolean updated = false;
        Iterator iterator = this.outlierLists.iterator();
        while (iterator.hasNext()) {
            OutlierList list = (OutlierList)iterator.next();
            if (!list.isOverlapped(outlier)) continue;
            updated = this.updateOutlierList(list, outlier);
        }
        if (!updated) {
            updated = this.outlierLists.add(new OutlierList(outlier));
        }
        return updated;
    }

    public Iterator iterator() {
        return this.outlierLists.iterator();
    }

    private boolean updateOutlierList(OutlierList list, Outlier outlier) {
        boolean result = false;
        result = list.add(outlier);
        list.updateAveragedOutlier();
        list.setMultiple(true);
        return result;
    }
}

