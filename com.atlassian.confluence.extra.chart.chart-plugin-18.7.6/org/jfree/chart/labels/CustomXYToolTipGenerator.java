/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.labels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class CustomXYToolTipGenerator
implements XYToolTipGenerator,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 8636030004670141362L;
    private List toolTipSeries = new ArrayList();

    public int getListCount() {
        return this.toolTipSeries.size();
    }

    public int getToolTipCount(int list) {
        int result = 0;
        List tooltips = (List)this.toolTipSeries.get(list);
        if (tooltips != null) {
            result = tooltips.size();
        }
        return result;
    }

    public String getToolTipText(int series, int item) {
        List tooltips;
        String result = null;
        if (series < this.getListCount() && (tooltips = (List)this.toolTipSeries.get(series)) != null && item < tooltips.size()) {
            result = (String)tooltips.get(item);
        }
        return result;
    }

    public void addToolTipSeries(List toolTips) {
        this.toolTipSeries.add(toolTips);
    }

    public String generateToolTip(XYDataset data, int series, int item) {
        return this.getToolTipText(series, item);
    }

    public Object clone() throws CloneNotSupportedException {
        CustomXYToolTipGenerator clone = (CustomXYToolTipGenerator)super.clone();
        if (this.toolTipSeries != null) {
            clone.toolTipSeries = new ArrayList(this.toolTipSeries);
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CustomXYToolTipGenerator) {
            CustomXYToolTipGenerator generator = (CustomXYToolTipGenerator)obj;
            boolean result = true;
            for (int series = 0; series < this.getListCount(); ++series) {
                for (int item = 0; item < this.getToolTipCount(series); ++item) {
                    String t1 = this.getToolTipText(series, item);
                    String t2 = generator.getToolTipText(series, item);
                    result = t1 != null ? result && t1.equals(t2) : result && t2 == null;
                }
            }
            return result;
        }
        return false;
    }
}

