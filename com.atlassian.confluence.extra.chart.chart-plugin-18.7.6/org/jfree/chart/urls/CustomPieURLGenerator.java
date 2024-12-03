/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.urls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.general.PieDataset;
import org.jfree.util.PublicCloneable;

public class CustomPieURLGenerator
implements PieURLGenerator,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 7100607670144900503L;
    private ArrayList urls = new ArrayList();

    public String generateURL(PieDataset dataset, Comparable key, int pieIndex) {
        return this.getURL(key, pieIndex);
    }

    public int getListCount() {
        return this.urls.size();
    }

    public int getURLCount(int list) {
        int result = 0;
        Map urlMap = (Map)this.urls.get(list);
        if (urlMap != null) {
            result = urlMap.size();
        }
        return result;
    }

    public String getURL(Comparable key, int mapIndex) {
        Map urlMap;
        String result = null;
        if (mapIndex < this.getListCount() && (urlMap = (Map)this.urls.get(mapIndex)) != null) {
            result = (String)urlMap.get(key);
        }
        return result;
    }

    public void addURLs(Map urlMap) {
        this.urls.add(urlMap);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CustomPieURLGenerator) {
            CustomPieURLGenerator generator = (CustomPieURLGenerator)o;
            if (this.getListCount() != generator.getListCount()) {
                return false;
            }
            for (int pieItem = 0; pieItem < this.getListCount(); ++pieItem) {
                if (this.getURLCount(pieItem) != generator.getURLCount(pieItem)) {
                    return false;
                }
                Set keySet = ((HashMap)this.urls.get(pieItem)).keySet();
                Iterator i = keySet.iterator();
                while (i.hasNext()) {
                    String key = (String)i.next();
                    if (this.getURL((Comparable)((Object)key), pieItem).equals(generator.getURL((Comparable)((Object)key), pieItem))) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        CustomPieURLGenerator urlGen = new CustomPieURLGenerator();
        Iterator i = this.urls.iterator();
        while (i.hasNext()) {
            Map map = (Map)i.next();
            HashMap newMap = new HashMap();
            Iterator j = map.keySet().iterator();
            while (j.hasNext()) {
                String key = (String)j.next();
                newMap.put(key, map.get(key));
            }
            urlGen.addURLs(newMap);
            newMap = null;
        }
        return urlGen;
    }
}

