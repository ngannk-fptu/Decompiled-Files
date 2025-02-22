/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import org.jfree.data.DomainOrder;
import org.jfree.data.xy.XYDataset;

public class RendererUtilities {
    public static int findLiveItemsLowerBound(XYDataset dataset, int series, double xLow, double xHigh) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (xLow >= xHigh) {
            throw new IllegalArgumentException("Requires xLow < xHigh.");
        }
        int itemCount = dataset.getItemCount(series);
        if (itemCount <= 1) {
            return 0;
        }
        if (dataset.getDomainOrder() == DomainOrder.ASCENDING) {
            int low = 0;
            int high = itemCount - 1;
            double lowValue = dataset.getXValue(series, low);
            if (lowValue >= xLow) {
                return low;
            }
            double highValue = dataset.getXValue(series, high);
            if (highValue < xLow) {
                return high;
            }
            while (high - low > 1) {
                int mid = (low + high) / 2;
                double midV = dataset.getXValue(series, mid);
                if (midV >= xLow) {
                    high = mid;
                    continue;
                }
                low = mid;
            }
            return high;
        }
        if (dataset.getDomainOrder() == DomainOrder.DESCENDING) {
            int low = 0;
            int high = itemCount - 1;
            double lowValue = dataset.getXValue(series, low);
            if (lowValue <= xHigh) {
                return low;
            }
            double highValue = dataset.getXValue(series, high);
            if (highValue > xHigh) {
                return high;
            }
            while (high - low > 1) {
                int mid = (low + high) / 2;
                double midV = dataset.getXValue(series, mid);
                if (midV > xHigh) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return high;
        }
        int index = 0;
        double x = dataset.getXValue(series, index);
        while (index < itemCount && (x < xLow || x > xHigh)) {
            if (++index >= itemCount) continue;
            x = dataset.getXValue(series, index);
        }
        return Math.min(Math.max(0, index), itemCount - 1);
    }

    public static int findLiveItemsUpperBound(XYDataset dataset, int series, double xLow, double xHigh) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (xLow >= xHigh) {
            throw new IllegalArgumentException("Requires xLow < xHigh.");
        }
        int itemCount = dataset.getItemCount(series);
        if (itemCount <= 1) {
            return 0;
        }
        if (dataset.getDomainOrder() == DomainOrder.ASCENDING) {
            int low = 0;
            int high = itemCount - 1;
            double lowValue = dataset.getXValue(series, low);
            if (lowValue > xHigh) {
                return low;
            }
            double highValue = dataset.getXValue(series, high);
            if (highValue <= xHigh) {
                return high;
            }
            int mid = (low + high) / 2;
            while (high - low > 1) {
                double midV = dataset.getXValue(series, mid);
                if (midV <= xHigh) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return mid;
        }
        if (dataset.getDomainOrder() == DomainOrder.DESCENDING) {
            int low = 0;
            int high = itemCount - 1;
            int mid = (low + high) / 2;
            double lowValue = dataset.getXValue(series, low);
            if (lowValue < xLow) {
                return low;
            }
            double highValue = dataset.getXValue(series, high);
            if (highValue >= xLow) {
                return high;
            }
            while (high - low > 1) {
                double midV = dataset.getXValue(series, mid);
                if (midV >= xLow) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return mid;
        }
        int index = itemCount - 1;
        double x = dataset.getXValue(series, index);
        while (index >= 0 && (x < xLow || x > xHigh)) {
            if (--index < 0) continue;
            x = dataset.getXValue(series, index);
        }
        return Math.max(index, 0);
    }

    public static int[] findLiveItems(XYDataset dataset, int series, double xLow, double xHigh) {
        int i1;
        int i0 = RendererUtilities.findLiveItemsLowerBound(dataset, series, xLow, xHigh);
        if (i0 > (i1 = RendererUtilities.findLiveItemsUpperBound(dataset, series, xLow, xHigh))) {
            i0 = i1;
        }
        return new int[]{i0, i1};
    }
}

