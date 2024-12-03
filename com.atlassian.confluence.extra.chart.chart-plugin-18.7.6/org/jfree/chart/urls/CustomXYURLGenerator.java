/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.urls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class CustomXYURLGenerator
implements XYURLGenerator,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -8565933356596551832L;
    private ArrayList urlSeries = new ArrayList();

    public int getListCount() {
        return this.urlSeries.size();
    }

    public int getURLCount(int list) {
        int result = 0;
        List urls = (List)this.urlSeries.get(list);
        if (urls != null) {
            result = urls.size();
        }
        return result;
    }

    public String getURL(int series, int item) {
        List urls;
        String result = null;
        if (series < this.getListCount() && (urls = (List)this.urlSeries.get(series)) != null && item < urls.size()) {
            result = (String)urls.get(item);
        }
        return result;
    }

    public String generateURL(XYDataset dataset, int series, int item) {
        return this.getURL(series, item);
    }

    public void addURLSeries(List urls) {
        ArrayList listToAdd = null;
        if (urls != null) {
            listToAdd = new ArrayList(urls);
        }
        this.urlSeries.add(listToAdd);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CustomXYURLGenerator)) {
            return false;
        }
        CustomXYURLGenerator that = (CustomXYURLGenerator)obj;
        int listCount = this.getListCount();
        if (listCount != that.getListCount()) {
            return false;
        }
        for (int series = 0; series < listCount; ++series) {
            int urlCount = this.getURLCount(series);
            if (urlCount != that.getURLCount(series)) {
                return false;
            }
            for (int item = 0; item < urlCount; ++item) {
                String u1 = this.getURL(series, item);
                String u2 = that.getURL(series, item);
                if (!(u1 != null ? !u1.equals(u2) : u2 != null)) continue;
                return false;
            }
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        CustomXYURLGenerator clone = (CustomXYURLGenerator)super.clone();
        clone.urlSeries = new ArrayList(this.urlSeries);
        return clone;
    }
}

