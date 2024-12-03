/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;

public class PlotUtilities {
    public static boolean isEmptyOrNull(XYPlot plot) {
        if (plot != null) {
            int n = plot.getDatasetCount();
            for (int i = 0; i < n; ++i) {
                XYDataset dataset = plot.getDataset(i);
                if (DatasetUtilities.isEmptyOrNull(dataset)) continue;
                return false;
            }
        }
        return true;
    }
}

