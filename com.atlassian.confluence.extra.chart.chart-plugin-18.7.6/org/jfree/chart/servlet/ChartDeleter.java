/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSessionBindingEvent
 *  javax.servlet.http.HttpSessionBindingListener
 */
package org.jfree.chart.servlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class ChartDeleter
implements HttpSessionBindingListener,
Serializable {
    private List chartNames = new ArrayList();

    public void addChart(String filename) {
        this.chartNames.add(filename);
    }

    public boolean isChartAvailable(String filename) {
        return this.chartNames.contains(filename);
    }

    public void valueBound(HttpSessionBindingEvent event) {
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        ListIterator iter = this.chartNames.listIterator();
        while (iter.hasNext()) {
            String filename = (String)iter.next();
            File file = new File(System.getProperty("java.io.tmpdir"), filename);
            if (!file.exists()) continue;
            file.delete();
        }
    }
}

