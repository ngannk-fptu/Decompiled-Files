/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.Statistics;

public abstract class BoxAndWhiskerCalculator {
    public static BoxAndWhiskerItem calculateBoxAndWhiskerStatistics(List values) {
        return BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(values, true);
    }

    public static BoxAndWhiskerItem calculateBoxAndWhiskerStatistics(List values, boolean stripNullAndNaNItems) {
        ArrayList<Number> vlist;
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        if (stripNullAndNaNItems) {
            vlist = new ArrayList<Number>(values.size());
            ListIterator iterator = values.listIterator();
            while (iterator.hasNext()) {
                Number n;
                double v;
                Object obj = iterator.next();
                if (!(obj instanceof Number) || Double.isNaN(v = (n = (Number)obj).doubleValue())) continue;
                vlist.add(n);
            }
        } else {
            vlist = values;
        }
        Collections.sort(vlist);
        double mean = Statistics.calculateMean(vlist, false);
        double median = Statistics.calculateMedian(vlist, false);
        double q1 = BoxAndWhiskerCalculator.calculateQ1(vlist);
        double q3 = BoxAndWhiskerCalculator.calculateQ3(vlist);
        double interQuartileRange = q3 - q1;
        double upperOutlierThreshold = q3 + interQuartileRange * 1.5;
        double lowerOutlierThreshold = q1 - interQuartileRange * 1.5;
        double upperFaroutThreshold = q3 + interQuartileRange * 2.0;
        double lowerFaroutThreshold = q1 - interQuartileRange * 2.0;
        double minRegularValue = Double.POSITIVE_INFINITY;
        double maxRegularValue = Double.NEGATIVE_INFINITY;
        double minOutlier = Double.POSITIVE_INFINITY;
        double maxOutlier = Double.NEGATIVE_INFINITY;
        ArrayList<Number> outliers = new ArrayList<Number>();
        Iterator iterator = vlist.iterator();
        while (iterator.hasNext()) {
            Number number = (Number)iterator.next();
            double value = number.doubleValue();
            if (value > upperOutlierThreshold) {
                outliers.add(number);
                if (value > maxOutlier && value <= upperFaroutThreshold) {
                    maxOutlier = value;
                }
            } else if (value < lowerOutlierThreshold) {
                outliers.add(number);
                if (value < minOutlier && value >= lowerFaroutThreshold) {
                    minOutlier = value;
                }
            } else {
                minRegularValue = Math.min(minRegularValue, value);
                maxRegularValue = Math.max(maxRegularValue, value);
            }
            minOutlier = Math.min(minOutlier, minRegularValue);
            maxOutlier = Math.max(maxOutlier, maxRegularValue);
        }
        return new BoxAndWhiskerItem(new Double(mean), new Double(median), new Double(q1), new Double(q3), new Double(minRegularValue), new Double(maxRegularValue), new Double(minOutlier), new Double(maxOutlier), (List)outliers);
    }

    public static double calculateQ1(List values) {
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        double result = Double.NaN;
        int count = values.size();
        if (count > 0) {
            result = count % 2 == 1 ? (count > 1 ? Statistics.calculateMedian(values, 0, count / 2) : Statistics.calculateMedian(values, 0, 0)) : Statistics.calculateMedian(values, 0, count / 2 - 1);
        }
        return result;
    }

    public static double calculateQ3(List values) {
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        double result = Double.NaN;
        int count = values.size();
        if (count > 0) {
            result = count % 2 == 1 ? (count > 1 ? Statistics.calculateMedian(values, count / 2, count - 1) : Statistics.calculateMedian(values, 0, 0)) : Statistics.calculateMedian(values, count / 2, count - 1);
        }
        return result;
    }
}

