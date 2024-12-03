/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.util.Arrays;

final class StatsLib {
    private StatsLib() {
    }

    public static double avedev(double[] v) {
        double r = 0.0;
        double m = 0.0;
        double s = 0.0;
        for (double item : v) {
            s += item;
        }
        m = s / (double)v.length;
        s = 0.0;
        for (double value : v) {
            s += Math.abs(value - m);
        }
        r = s / (double)v.length;
        return r;
    }

    public static double stdev(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length > 1) {
            r = Math.sqrt(StatsLib.devsq(v) / (double)(v.length - 1));
        }
        return r;
    }

    public static double stdevp(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length > 1) {
            r = Math.sqrt(StatsLib.devsq(v) / (double)v.length);
        }
        return r;
    }

    public static double var(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length > 1) {
            r = StatsLib.devsq(v) / (double)(v.length - 1);
        }
        return r;
    }

    public static double varp(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length > 1) {
            r = StatsLib.devsq(v) / (double)v.length;
        }
        return r;
    }

    public static double median(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length >= 1) {
            int n = v.length;
            Arrays.sort(v);
            r = n % 2 == 0 ? (v[n / 2] + v[n / 2 - 1]) / 2.0 : v[n / 2];
        }
        return r;
    }

    public static double devsq(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length >= 1) {
            double m = 0.0;
            double s = 0.0;
            int n = v.length;
            for (double item : v) {
                s += item;
            }
            m = s / (double)n;
            s = 0.0;
            for (double value : v) {
                s += (value - m) * (value - m);
            }
            r = n == 1 ? 0.0 : s;
        }
        return r;
    }

    public static double kthLargest(double[] v, int k) {
        double r = Double.NaN;
        int index = k - 1;
        if (v != null && v.length > index && index >= 0) {
            Arrays.sort(v);
            r = v[v.length - index - 1];
        }
        return r;
    }

    public static double kthSmallest(double[] v, int k) {
        double r = Double.NaN;
        int index = k - 1;
        if (v != null && v.length > index && index >= 0) {
            Arrays.sort(v);
            r = v[index];
        }
        return r;
    }
}

