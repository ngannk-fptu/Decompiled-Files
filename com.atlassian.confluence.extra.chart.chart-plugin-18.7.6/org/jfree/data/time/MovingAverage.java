/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MovingAverage {
    public static TimeSeriesCollection createMovingAverage(TimeSeriesCollection source, String suffix, int periodCount, int skip) {
        if (source == null) {
            throw new IllegalArgumentException("Null 'source' argument.");
        }
        if (periodCount < 1) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 1.");
        }
        TimeSeriesCollection result = new TimeSeriesCollection();
        for (int i = 0; i < source.getSeriesCount(); ++i) {
            TimeSeries sourceSeries = source.getSeries(i);
            TimeSeries maSeries = MovingAverage.createMovingAverage(sourceSeries, sourceSeries.getKey() + suffix, periodCount, skip);
            result.addSeries(maSeries);
        }
        return result;
    }

    public static TimeSeries createMovingAverage(TimeSeries source, String name, int periodCount, int skip) {
        if (source == null) {
            throw new IllegalArgumentException("Null source.");
        }
        if (periodCount < 1) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 1.");
        }
        TimeSeries result = new TimeSeries((Comparable)((Object)name));
        if (source.getItemCount() > 0) {
            long firstSerial = source.getDataItem(0).getPeriod().getSerialIndex() + (long)skip;
            for (int i = source.getItemCount() - 1; i >= 0; --i) {
                TimeSeriesDataItem current = source.getDataItem(i);
                RegularTimePeriod period = current.getPeriod();
                long serial = period.getSerialIndex();
                if (serial < firstSerial) continue;
                int n = 0;
                double sum = 0.0;
                long serialLimit = period.getSerialIndex() - (long)periodCount;
                boolean finished = false;
                for (int offset = 0; offset < periodCount && !finished; ++offset) {
                    if (i - offset < 0) continue;
                    TimeSeriesDataItem item = source.getDataItem(i - offset);
                    RegularTimePeriod p = item.getPeriod();
                    Number v = item.getValue();
                    long currentIndex = p.getSerialIndex();
                    if (currentIndex > serialLimit) {
                        if (v == null) continue;
                        sum += v.doubleValue();
                        ++n;
                        continue;
                    }
                    finished = true;
                }
                if (n > 0) {
                    result.add(period, sum / (double)n);
                    continue;
                }
                result.add(period, null);
            }
        }
        return result;
    }

    public static TimeSeries createPointMovingAverage(TimeSeries source, String name, int pointCount) {
        if (source == null) {
            throw new IllegalArgumentException("Null 'source'.");
        }
        if (pointCount < 2) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 2.");
        }
        TimeSeries result = new TimeSeries((Comparable)((Object)name));
        double rollingSumForPeriod = 0.0;
        for (int i = 0; i < source.getItemCount(); ++i) {
            TimeSeriesDataItem current = source.getDataItem(i);
            RegularTimePeriod period = current.getPeriod();
            rollingSumForPeriod += current.getValue().doubleValue();
            if (i > pointCount - 1) {
                TimeSeriesDataItem startOfMovingAvg = source.getDataItem(i - pointCount);
                result.add(period, (rollingSumForPeriod -= startOfMovingAvg.getValue().doubleValue()) / (double)pointCount);
                continue;
            }
            if (i != pointCount - 1) continue;
            result.add(period, rollingSumForPeriod / (double)pointCount);
        }
        return result;
    }

    public static XYDataset createMovingAverage(XYDataset source, String suffix, long period, long skip) {
        return MovingAverage.createMovingAverage(source, suffix, (double)period, (double)skip);
    }

    public static XYDataset createMovingAverage(XYDataset source, String suffix, double period, double skip) {
        if (source == null) {
            throw new IllegalArgumentException("Null source (XYDataset).");
        }
        XYSeriesCollection result = new XYSeriesCollection();
        for (int i = 0; i < source.getSeriesCount(); ++i) {
            XYSeries s = MovingAverage.createMovingAverage(source, i, source.getSeriesKey(i) + suffix, period, skip);
            result.addSeries(s);
        }
        return result;
    }

    public static XYSeries createMovingAverage(XYDataset source, int series, String name, double period, double skip) {
        if (source == null) {
            throw new IllegalArgumentException("Null source (XYDataset).");
        }
        if (period < Double.MIN_VALUE) {
            throw new IllegalArgumentException("period must be positive.");
        }
        if (skip < 0.0) {
            throw new IllegalArgumentException("skip must be >= 0.0.");
        }
        XYSeries result = new XYSeries((Comparable)((Object)name));
        if (source.getItemCount(series) > 0) {
            double first = source.getXValue(series, 0) + skip;
            for (int i = source.getItemCount(series) - 1; i >= 0; --i) {
                double x = source.getXValue(series, i);
                if (!(x >= first)) continue;
                int n = 0;
                double sum = 0.0;
                double limit = x - period;
                int offset = 0;
                boolean finished = false;
                while (!finished) {
                    if (i - offset >= 0) {
                        double xx = source.getXValue(series, i - offset);
                        Number yy = source.getY(series, i - offset);
                        if (xx > limit) {
                            if (yy != null) {
                                sum += yy.doubleValue();
                                ++n;
                            }
                        } else {
                            finished = true;
                        }
                    } else {
                        finished = true;
                    }
                    ++offset;
                }
                if (n > 0) {
                    result.add(x, sum / (double)n);
                    continue;
                }
                result.add(x, (Number)null);
            }
        }
        return result;
    }
}

