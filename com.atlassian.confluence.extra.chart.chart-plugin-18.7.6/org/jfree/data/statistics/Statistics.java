/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class Statistics {
    public static double calculateMean(Number[] values) {
        return Statistics.calculateMean(values, true);
    }

    public static double calculateMean(Number[] values, boolean includeNullAndNaN) {
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        double sum = 0.0;
        int counter = 0;
        for (int i = 0; i < values.length; ++i) {
            double current = values[i] != null ? values[i].doubleValue() : Double.NaN;
            if (!includeNullAndNaN && Double.isNaN(current)) continue;
            sum += current;
            ++counter;
        }
        double result = sum / (double)counter;
        return result;
    }

    public static double calculateMean(Collection values) {
        return Statistics.calculateMean(values, true);
    }

    public static double calculateMean(Collection values, boolean includeNullAndNaN) {
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        int count = 0;
        double total = 0.0;
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object == null) {
                if (!includeNullAndNaN) continue;
                return Double.NaN;
            }
            if (!(object instanceof Number)) continue;
            Number number = (Number)object;
            double value = number.doubleValue();
            if (Double.isNaN(value)) {
                if (!includeNullAndNaN) continue;
                return Double.NaN;
            }
            total += number.doubleValue();
            ++count;
        }
        return total / (double)count;
    }

    public static double calculateMedian(List values) {
        return Statistics.calculateMedian(values, true);
    }

    public static double calculateMedian(List values, boolean copyAndSort) {
        double result = Double.NaN;
        if (values != null) {
            int count;
            if (copyAndSort) {
                int itemCount = values.size();
                ArrayList copy = new ArrayList(itemCount);
                for (int i = 0; i < itemCount; ++i) {
                    copy.add(i, values.get(i));
                }
                Collections.sort(copy);
                values = copy;
            }
            if ((count = values.size()) > 0) {
                if (count % 2 == 1) {
                    Number value;
                    if (count > 1) {
                        value = (Number)values.get((count - 1) / 2);
                        result = value.doubleValue();
                    } else {
                        value = (Number)values.get(0);
                        result = value.doubleValue();
                    }
                } else {
                    Number value1 = (Number)values.get(count / 2 - 1);
                    Number value2 = (Number)values.get(count / 2);
                    result = (value1.doubleValue() + value2.doubleValue()) / 2.0;
                }
            }
        }
        return result;
    }

    public static double calculateMedian(List values, int start, int end) {
        return Statistics.calculateMedian(values, start, end, true);
    }

    public static double calculateMedian(List values, int start, int end, boolean copyAndSort) {
        double result = Double.NaN;
        if (copyAndSort) {
            ArrayList working = new ArrayList(end - start + 1);
            for (int i = start; i <= end; ++i) {
                working.add(values.get(i));
            }
            Collections.sort(working);
            result = Statistics.calculateMedian(working, false);
        } else {
            int count = end - start + 1;
            if (count > 0) {
                if (count % 2 == 1) {
                    if (count > 1) {
                        Number value = (Number)values.get(start + (count - 1) / 2);
                        result = value.doubleValue();
                    } else {
                        Number value = (Number)values.get(start);
                        result = value.doubleValue();
                    }
                } else {
                    Number value1 = (Number)values.get(start + count / 2 - 1);
                    Number value2 = (Number)values.get(start + count / 2);
                    result = (value1.doubleValue() + value2.doubleValue()) / 2.0;
                }
            }
        }
        return result;
    }

    public static double getStdDev(Number[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' array.");
        }
        if (data.length == 0) {
            throw new IllegalArgumentException("Zero length 'data' array.");
        }
        double avg = Statistics.calculateMean(data);
        double sum = 0.0;
        for (int counter = 0; counter < data.length; ++counter) {
            double diff = data[counter].doubleValue() - avg;
            sum += diff * diff;
        }
        return Math.sqrt(sum / (double)(data.length - 1));
    }

    public static double[] getLinearFit(Number[] xData, Number[] yData) {
        if (xData == null) {
            throw new IllegalArgumentException("Null 'xData' argument.");
        }
        if (yData == null) {
            throw new IllegalArgumentException("Null 'yData' argument.");
        }
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Statistics.getLinearFit(): array lengths must be equal.");
        }
        double[] result = new double[2];
        result[1] = Statistics.getSlope(xData, yData);
        result[0] = Statistics.calculateMean(yData) - result[1] * Statistics.calculateMean(xData);
        return result;
    }

    public static double getSlope(Number[] xData, Number[] yData) {
        int counter;
        if (xData == null) {
            throw new IllegalArgumentException("Null 'xData' argument.");
        }
        if (yData == null) {
            throw new IllegalArgumentException("Null 'yData' argument.");
        }
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Array lengths must be equal.");
        }
        double sx = 0.0;
        double sxx = 0.0;
        double sxy = 0.0;
        double sy = 0.0;
        for (counter = 0; counter < xData.length; ++counter) {
            sx += xData[counter].doubleValue();
            sxx += Math.pow(xData[counter].doubleValue(), 2.0);
            sxy += yData[counter].doubleValue() * xData[counter].doubleValue();
            sy += yData[counter].doubleValue();
        }
        return (sxy - sx * sy / (double)counter) / (sxx - sx * sx / (double)counter);
    }

    public static double getCorrelation(Number[] data1, Number[] data2) {
        if (data1 == null) {
            throw new IllegalArgumentException("Null 'data1' argument.");
        }
        if (data2 == null) {
            throw new IllegalArgumentException("Null 'data2' argument.");
        }
        if (data1.length != data2.length) {
            throw new IllegalArgumentException("'data1' and 'data2' arrays must have same length.");
        }
        int n = data1.length;
        double sumX = 0.0;
        double sumY = 0.0;
        double sumX2 = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        for (int i = 0; i < n; ++i) {
            double x = 0.0;
            if (data1[i] != null) {
                x = data1[i].doubleValue();
            }
            double y = 0.0;
            if (data2[i] != null) {
                y = data2[i].doubleValue();
            }
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }
        return ((double)n * sumXY - sumX * sumY) / Math.pow(((double)n * sumX2 - sumX * sumX) * ((double)n * sumY2 - sumY * sumY), 0.5);
    }

    public static double[][] getMovingAverage(Number[] xData, Number[] yData, int period) {
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Array lengths must be equal.");
        }
        if (period > xData.length) {
            throw new IllegalArgumentException("Period can't be longer than dataset.");
        }
        double[][] result = new double[xData.length - period][2];
        for (int i = 0; i < result.length; ++i) {
            result[i][0] = xData[i + period].doubleValue();
            double sum = 0.0;
            for (int j = 0; j < period; ++j) {
                sum += yData[i + j].doubleValue();
            }
            result[i][1] = sum /= (double)period;
        }
        return result;
    }
}

