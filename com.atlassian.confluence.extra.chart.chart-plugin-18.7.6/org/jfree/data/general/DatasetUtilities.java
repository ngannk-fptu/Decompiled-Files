/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.data.DomainInfo;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.KeyedValues;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryRangeInfo;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYDomainInfo;
import org.jfree.data.xy.XYRangeInfo;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ArrayUtilities;

public final class DatasetUtilities {
    private DatasetUtilities() {
    }

    public static double calculatePieDatasetTotal(PieDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        List keys = dataset.getKeys();
        double totalValue = 0.0;
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable current = (Comparable)iterator.next();
            if (current == null) continue;
            Number value = dataset.getValue(current);
            double v = 0.0;
            if (value != null) {
                v = value.doubleValue();
            }
            if (!(v > 0.0)) continue;
            totalValue += v;
        }
        return totalValue;
    }

    public static PieDataset createPieDatasetForRow(CategoryDataset dataset, Comparable rowKey) {
        int row = dataset.getRowIndex(rowKey);
        return DatasetUtilities.createPieDatasetForRow(dataset, row);
    }

    public static PieDataset createPieDatasetForRow(CategoryDataset dataset, int row) {
        DefaultPieDataset result = new DefaultPieDataset();
        int columnCount = dataset.getColumnCount();
        for (int current = 0; current < columnCount; ++current) {
            Comparable columnKey = dataset.getColumnKey(current);
            result.setValue(columnKey, dataset.getValue(row, current));
        }
        return result;
    }

    public static PieDataset createPieDatasetForColumn(CategoryDataset dataset, Comparable columnKey) {
        int column = dataset.getColumnIndex(columnKey);
        return DatasetUtilities.createPieDatasetForColumn(dataset, column);
    }

    public static PieDataset createPieDatasetForColumn(CategoryDataset dataset, int column) {
        DefaultPieDataset result = new DefaultPieDataset();
        int rowCount = dataset.getRowCount();
        for (int i = 0; i < rowCount; ++i) {
            Comparable rowKey = dataset.getRowKey(i);
            result.setValue(rowKey, dataset.getValue(i, column));
        }
        return result;
    }

    public static PieDataset createConsolidatedPieDataset(PieDataset source, Comparable key, double minimumPercent) {
        return DatasetUtilities.createConsolidatedPieDataset(source, key, minimumPercent, 2);
    }

    public static PieDataset createConsolidatedPieDataset(PieDataset source, Comparable key, double minimumPercent, int minItems) {
        DefaultPieDataset result = new DefaultPieDataset();
        double total = DatasetUtilities.calculatePieDatasetTotal(source);
        List keys = source.getKeys();
        ArrayList<Comparable> otherKeys = new ArrayList<Comparable>();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            double value;
            Comparable currentKey = (Comparable)iterator.next();
            Number dataValue = source.getValue(currentKey);
            if (dataValue == null || !((value = dataValue.doubleValue()) / total < minimumPercent)) continue;
            otherKeys.add(currentKey);
        }
        iterator = keys.iterator();
        double otherValue = 0.0;
        while (iterator.hasNext()) {
            Comparable currentKey = (Comparable)iterator.next();
            Number dataValue = source.getValue(currentKey);
            if (dataValue == null) continue;
            if (otherKeys.contains(currentKey) && otherKeys.size() >= minItems) {
                otherValue += dataValue.doubleValue();
                continue;
            }
            result.setValue(currentKey, dataValue);
        }
        if (otherKeys.size() >= minItems) {
            result.setValue(key, otherValue);
        }
        return result;
    }

    public static CategoryDataset createCategoryDataset(String rowKeyPrefix, String columnKeyPrefix, double[][] data) {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; ++r) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; ++c) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(new Double(data[r][c]), (Comparable)((Object)rowKey), (Comparable)((Object)columnKey));
            }
        }
        return result;
    }

    public static CategoryDataset createCategoryDataset(String rowKeyPrefix, String columnKeyPrefix, Number[][] data) {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; ++r) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; ++c) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(data[r][c], (Comparable)((Object)rowKey), (Comparable)((Object)columnKey));
            }
        }
        return result;
    }

    public static CategoryDataset createCategoryDataset(Comparable[] rowKeys, Comparable[] columnKeys, double[][] data) {
        if (rowKeys == null) {
            throw new IllegalArgumentException("Null 'rowKeys' argument.");
        }
        if (columnKeys == null) {
            throw new IllegalArgumentException("Null 'columnKeys' argument.");
        }
        if (ArrayUtilities.hasDuplicateItems(rowKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'rowKeys'.");
        }
        if (ArrayUtilities.hasDuplicateItems(columnKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'columnKeys'.");
        }
        if (rowKeys.length != data.length) {
            throw new IllegalArgumentException("The number of row keys does not match the number of rows in the data array.");
        }
        int columnCount = 0;
        for (int r = 0; r < data.length; ++r) {
            columnCount = Math.max(columnCount, data[r].length);
        }
        if (columnKeys.length != columnCount) {
            throw new IllegalArgumentException("The number of column keys does not match the number of columns in the data array.");
        }
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; ++r) {
            Comparable rowKey = rowKeys[r];
            for (int c = 0; c < data[r].length; ++c) {
                Comparable columnKey = columnKeys[c];
                result.addValue(new Double(data[r][c]), rowKey, columnKey);
            }
        }
        return result;
    }

    public static CategoryDataset createCategoryDataset(Comparable rowKey, KeyedValues rowData) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (rowData == null) {
            throw new IllegalArgumentException("Null 'rowData' argument.");
        }
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int i = 0; i < rowData.getItemCount(); ++i) {
            result.addValue(rowData.getValue(i), rowKey, rowData.getKey(i));
        }
        return result;
    }

    public static XYDataset sampleFunction2D(Function2D f, double start, double end, int samples, Comparable seriesKey) {
        XYSeries series = DatasetUtilities.sampleFunction2DToSeries(f, start, end, samples, seriesKey);
        XYSeriesCollection collection = new XYSeriesCollection(series);
        return collection;
    }

    public static XYSeries sampleFunction2DToSeries(Function2D f, double start, double end, int samples, Comparable seriesKey) {
        if (f == null) {
            throw new IllegalArgumentException("Null 'f' argument.");
        }
        if (seriesKey == null) {
            throw new IllegalArgumentException("Null 'seriesKey' argument.");
        }
        if (start >= end) {
            throw new IllegalArgumentException("Requires 'start' < 'end'.");
        }
        if (samples < 2) {
            throw new IllegalArgumentException("Requires 'samples' > 1");
        }
        XYSeries series = new XYSeries(seriesKey);
        double step = (end - start) / (double)(samples - 1);
        for (int i = 0; i < samples; ++i) {
            double x = start + step * (double)i;
            series.add(x, f.getValue(x));
        }
        return series;
    }

    public static boolean isEmptyOrNull(PieDataset dataset) {
        if (dataset == null) {
            return true;
        }
        int itemCount = dataset.getItemCount();
        if (itemCount == 0) {
            return true;
        }
        for (int item = 0; item < itemCount; ++item) {
            double yy;
            Number y = dataset.getValue(item);
            if (y == null || !((yy = y.doubleValue()) > 0.0)) continue;
            return false;
        }
        return true;
    }

    public static boolean isEmptyOrNull(CategoryDataset dataset) {
        if (dataset == null) {
            return true;
        }
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        if (rowCount == 0 || columnCount == 0) {
            return true;
        }
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < columnCount; ++c) {
                if (dataset.getValue(r, c) == null) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyOrNull(XYDataset dataset) {
        if (dataset != null) {
            for (int s = 0; s < dataset.getSeriesCount(); ++s) {
                if (dataset.getItemCount(s) <= 0) continue;
                return false;
            }
        }
        return true;
    }

    public static Range findDomainBounds(XYDataset dataset) {
        return DatasetUtilities.findDomainBounds(dataset, true);
    }

    public static Range findDomainBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo)((Object)dataset);
            result = info.getDomainBounds(includeInterval);
        } else {
            result = DatasetUtilities.iterateDomainBounds(dataset, includeInterval);
        }
        return result;
    }

    public static Range findDomainBounds(XYDataset dataset, List visibleSeriesKeys, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof XYDomainInfo) {
            XYDomainInfo info = (XYDomainInfo)((Object)dataset);
            result = info.getDomainBounds(visibleSeriesKeys, includeInterval);
        } else {
            result = DatasetUtilities.iterateToFindDomainBounds(dataset, visibleSeriesKeys, includeInterval);
        }
        return result;
    }

    public static Range iterateDomainBounds(XYDataset dataset) {
        return DatasetUtilities.iterateDomainBounds(dataset, true);
    }

    public static Range iterateDomainBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        if (includeInterval && dataset instanceof IntervalXYDataset) {
            IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
            for (int series = 0; series < seriesCount; ++series) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double lvalue = intervalXYData.getStartXValue(series, item);
                    double uvalue = intervalXYData.getEndXValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (Double.isNaN(uvalue)) continue;
                    maximum = Math.max(maximum, uvalue);
                }
            }
        } else {
            for (int series = 0; series < seriesCount; ++series) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double lvalue;
                    double uvalue = lvalue = dataset.getXValue(series, item);
                    if (Double.isNaN(lvalue)) continue;
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range findRangeBounds(CategoryDataset dataset) {
        return DatasetUtilities.findRangeBounds(dataset, true);
    }

    public static Range findRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo)((Object)dataset);
            result = info.getRangeBounds(includeInterval);
        } else {
            result = DatasetUtilities.iterateRangeBounds(dataset, includeInterval);
        }
        return result;
    }

    public static Range findRangeBounds(CategoryDataset dataset, List visibleSeriesKeys, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof CategoryRangeInfo) {
            CategoryRangeInfo info = (CategoryRangeInfo)((Object)dataset);
            result = info.getRangeBounds(visibleSeriesKeys, includeInterval);
        } else {
            result = DatasetUtilities.iterateToFindRangeBounds(dataset, visibleSeriesKeys, includeInterval);
        }
        return result;
    }

    public static Range findRangeBounds(XYDataset dataset) {
        return DatasetUtilities.findRangeBounds(dataset, true);
    }

    public static Range findRangeBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo)((Object)dataset);
            result = info.getRangeBounds(includeInterval);
        } else {
            result = DatasetUtilities.iterateRangeBounds(dataset, includeInterval);
        }
        return result;
    }

    public static Range findRangeBounds(XYDataset dataset, List visibleSeriesKeys, Range xRange, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof XYRangeInfo) {
            XYRangeInfo info = (XYRangeInfo)((Object)dataset);
            result = info.getRangeBounds(visibleSeriesKeys, xRange, includeInterval);
        } else {
            result = DatasetUtilities.iterateToFindRangeBounds(dataset, visibleSeriesKeys, xRange, includeInterval);
        }
        return result;
    }

    public static Range iterateCategoryRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        return DatasetUtilities.iterateRangeBounds(dataset, includeInterval);
    }

    public static Range iterateRangeBounds(CategoryDataset dataset) {
        return DatasetUtilities.iterateRangeBounds(dataset, true);
    }

    public static Range iterateRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        if (includeInterval && dataset instanceof IntervalCategoryDataset) {
            IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
            for (int row = 0; row < rowCount; ++row) {
                for (int column = 0; column < columnCount; ++column) {
                    Number lvalue = icd.getStartValue(row, column);
                    Number uvalue = icd.getEndValue(row, column);
                    if (lvalue != null && !Double.isNaN(lvalue.doubleValue())) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue == null || Double.isNaN(uvalue.doubleValue())) continue;
                    maximum = Math.max(maximum, uvalue.doubleValue());
                }
            }
        } else {
            for (int row = 0; row < rowCount; ++row) {
                for (int column = 0; column < columnCount; ++column) {
                    double v;
                    Number value = dataset.getValue(row, column);
                    if (value == null || Double.isNaN(v = value.doubleValue())) continue;
                    minimum = Math.min(minimum, v);
                    maximum = Math.max(maximum, v);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateToFindRangeBounds(CategoryDataset dataset, List visibleSeriesKeys, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (visibleSeriesKeys == null) {
            throw new IllegalArgumentException("Null 'visibleSeriesKeys' argument.");
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int columnCount = dataset.getColumnCount();
        if (includeInterval && dataset instanceof BoxAndWhiskerCategoryDataset) {
            BoxAndWhiskerCategoryDataset bx = (BoxAndWhiskerCategoryDataset)dataset;
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.getRowIndex(seriesKey);
                int itemCount = dataset.getColumnCount();
                for (int item = 0; item < itemCount; ++item) {
                    Number uvalue;
                    Number lvalue = bx.getMinRegularValue(series, item);
                    if (lvalue == null) {
                        lvalue = bx.getValue(series, item);
                    }
                    if ((uvalue = bx.getMaxRegularValue(series, item)) == null) {
                        uvalue = bx.getValue(series, item);
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue == null) continue;
                    maximum = Math.max(maximum, uvalue.doubleValue());
                }
            }
        } else if (includeInterval && dataset instanceof IntervalCategoryDataset) {
            IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.getRowIndex(seriesKey);
                for (int column = 0; column < columnCount; ++column) {
                    Number lvalue = icd.getStartValue(series, column);
                    Number uvalue = icd.getEndValue(series, column);
                    if (lvalue != null && !Double.isNaN(lvalue.doubleValue())) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue == null || Double.isNaN(uvalue.doubleValue())) continue;
                    maximum = Math.max(maximum, uvalue.doubleValue());
                }
            }
        } else if (includeInterval && dataset instanceof StatisticalCategoryDataset) {
            StatisticalCategoryDataset scd = (StatisticalCategoryDataset)dataset;
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.getRowIndex(seriesKey);
                for (int column = 0; column < columnCount; ++column) {
                    double mean;
                    Number meanN = scd.getMeanValue(series, column);
                    if (meanN == null) continue;
                    double std = 0.0;
                    Number stdN = scd.getStdDevValue(series, column);
                    if (stdN != null && Double.isNaN(std = stdN.doubleValue())) {
                        std = 0.0;
                    }
                    if (Double.isNaN(mean = meanN.doubleValue())) continue;
                    minimum = Math.min(minimum, mean - std);
                    maximum = Math.max(maximum, mean + std);
                }
            }
        } else {
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.getRowIndex(seriesKey);
                for (int column = 0; column < columnCount; ++column) {
                    double v;
                    Number value = dataset.getValue(series, column);
                    if (value == null || Double.isNaN(v = value.doubleValue())) continue;
                    minimum = Math.min(minimum, v);
                    maximum = Math.max(maximum, v);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateXYRangeBounds(XYDataset dataset) {
        return DatasetUtilities.iterateRangeBounds(dataset);
    }

    public static Range iterateRangeBounds(XYDataset dataset) {
        return DatasetUtilities.iterateRangeBounds(dataset, true);
    }

    public static Range iterateRangeBounds(XYDataset dataset, boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        if (includeInterval && dataset instanceof IntervalXYDataset) {
            IntervalXYDataset ixyd = (IntervalXYDataset)dataset;
            for (int series = 0; series < seriesCount; ++series) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double lvalue = ixyd.getStartYValue(series, item);
                    double uvalue = ixyd.getEndYValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (Double.isNaN(uvalue)) continue;
                    maximum = Math.max(maximum, uvalue);
                }
            }
        } else if (includeInterval && dataset instanceof OHLCDataset) {
            OHLCDataset ohlc = (OHLCDataset)dataset;
            for (int series = 0; series < seriesCount; ++series) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double lvalue = ohlc.getLowValue(series, item);
                    double uvalue = ohlc.getHighValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (Double.isNaN(uvalue)) continue;
                    maximum = Math.max(maximum, uvalue);
                }
            }
        } else {
            for (int series = 0; series < seriesCount; ++series) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double value = dataset.getYValue(series, item);
                    if (Double.isNaN(value)) continue;
                    minimum = Math.min(minimum, value);
                    maximum = Math.max(maximum, value);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateToFindDomainBounds(XYDataset dataset, List visibleSeriesKeys, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (visibleSeriesKeys == null) {
            throw new IllegalArgumentException("Null 'visibleSeriesKeys' argument.");
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        if (includeInterval && dataset instanceof IntervalXYDataset) {
            IntervalXYDataset ixyd = (IntervalXYDataset)dataset;
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double lvalue = ixyd.getStartXValue(series, item);
                    double uvalue = ixyd.getEndXValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (Double.isNaN(uvalue)) continue;
                    maximum = Math.max(maximum, uvalue);
                }
            }
        } else {
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double x = dataset.getXValue(series, item);
                    if (Double.isNaN(x)) continue;
                    minimum = Math.min(minimum, x);
                    maximum = Math.max(maximum, x);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateToFindRangeBounds(XYDataset dataset, List visibleSeriesKeys, Range xRange, boolean includeInterval) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (visibleSeriesKeys == null) {
            throw new IllegalArgumentException("Null 'visibleSeriesKeys' argument.");
        }
        if (xRange == null) {
            throw new IllegalArgumentException("Null 'xRange' argument");
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        if (includeInterval && dataset instanceof OHLCDataset) {
            OHLCDataset ohlc = (OHLCDataset)dataset;
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double x = ohlc.getXValue(series, item);
                    if (!xRange.contains(x)) continue;
                    double lvalue = ohlc.getLowValue(series, item);
                    double uvalue = ohlc.getHighValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (Double.isNaN(uvalue)) continue;
                    maximum = Math.max(maximum, uvalue);
                }
            }
        } else if (includeInterval && dataset instanceof BoxAndWhiskerXYDataset) {
            BoxAndWhiskerXYDataset bx = (BoxAndWhiskerXYDataset)dataset;
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double x = bx.getXValue(series, item);
                    if (!xRange.contains(x)) continue;
                    Number lvalue = bx.getMinRegularValue(series, item);
                    Number uvalue = bx.getMaxRegularValue(series, item);
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue == null) continue;
                    maximum = Math.max(maximum, uvalue.doubleValue());
                }
            }
        } else if (includeInterval && dataset instanceof IntervalXYDataset) {
            IntervalXYDataset ixyd = (IntervalXYDataset)dataset;
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double x = ixyd.getXValue(series, item);
                    if (!xRange.contains(x)) continue;
                    double lvalue = ixyd.getStartYValue(series, item);
                    double uvalue = ixyd.getEndYValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (Double.isNaN(uvalue)) continue;
                    maximum = Math.max(maximum, uvalue);
                }
            }
        } else {
            Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                Comparable seriesKey = (Comparable)iterator.next();
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; ++item) {
                    double x = dataset.getXValue(series, item);
                    double y = dataset.getYValue(series, item);
                    if (!xRange.contains(x) || Double.isNaN(y)) continue;
                    minimum = Math.min(minimum, y);
                    maximum = Math.max(maximum, y);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Number findMinimumDomainValue(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Double result = null;
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo)((Object)dataset);
            return new Double(info.getDomainLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; ++series) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; ++item) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    value = intervalXYData.getStartXValue(series, item);
                } else {
                    value = dataset.getXValue(series, item);
                }
                if (Double.isNaN(value)) continue;
                minimum = Math.min(minimum, value);
            }
        }
        result = minimum == Double.POSITIVE_INFINITY ? null : new Double(minimum);
        return result;
    }

    public static Number findMaximumDomainValue(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Double result = null;
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo)((Object)dataset);
            return new Double(info.getDomainUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; ++series) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; ++item) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    value = intervalXYData.getEndXValue(series, item);
                } else {
                    value = dataset.getXValue(series, item);
                }
                if (Double.isNaN(value)) continue;
                maximum = Math.max(maximum, value);
            }
        }
        result = maximum == Double.NEGATIVE_INFINITY ? null : new Double(maximum);
        return result;
    }

    public static Number findMinimumRangeValue(CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo)((Object)dataset);
            return new Double(info.getRangeLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        int seriesCount = dataset.getRowCount();
        int itemCount = dataset.getColumnCount();
        for (int series = 0; series < seriesCount; ++series) {
            for (int item = 0; item < itemCount; ++item) {
                Number value;
                if (dataset instanceof IntervalCategoryDataset) {
                    IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
                    value = icd.getStartValue(series, item);
                } else {
                    value = dataset.getValue(series, item);
                }
                if (value == null) continue;
                minimum = Math.min(minimum, value.doubleValue());
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Double(minimum);
    }

    public static Number findMinimumRangeValue(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo)((Object)dataset);
            return new Double(info.getRangeLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; ++series) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; ++item) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    value = intervalXYData.getStartYValue(series, item);
                } else if (dataset instanceof OHLCDataset) {
                    OHLCDataset highLowData = (OHLCDataset)dataset;
                    value = highLowData.getLowValue(series, item);
                } else {
                    value = dataset.getYValue(series, item);
                }
                if (Double.isNaN(value)) continue;
                minimum = Math.min(minimum, value);
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Double(minimum);
    }

    public static Number findMaximumRangeValue(CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo)((Object)dataset);
            return new Double(info.getRangeUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getRowCount();
        int itemCount = dataset.getColumnCount();
        for (int series = 0; series < seriesCount; ++series) {
            for (int item = 0; item < itemCount; ++item) {
                Number value;
                if (dataset instanceof IntervalCategoryDataset) {
                    IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
                    value = icd.getEndValue(series, item);
                } else {
                    value = dataset.getValue(series, item);
                }
                if (value == null) continue;
                maximum = Math.max(maximum, value.doubleValue());
            }
        }
        if (maximum == Double.NEGATIVE_INFINITY) {
            return null;
        }
        return new Double(maximum);
    }

    public static Number findMaximumRangeValue(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo)((Object)dataset);
            return new Double(info.getRangeUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; ++series) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; ++item) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
                    value = intervalXYData.getEndYValue(series, item);
                } else if (dataset instanceof OHLCDataset) {
                    OHLCDataset highLowData = (OHLCDataset)dataset;
                    value = highLowData.getHighValue(series, item);
                } else {
                    value = dataset.getYValue(series, item);
                }
                if (Double.isNaN(value)) continue;
                maximum = Math.max(maximum, value);
            }
        }
        if (maximum == Double.NEGATIVE_INFINITY) {
            return null;
        }
        return new Double(maximum);
    }

    public static Range findStackedRangeBounds(CategoryDataset dataset) {
        return DatasetUtilities.findStackedRangeBounds(dataset, 0.0);
    }

    public static Range findStackedRangeBounds(CategoryDataset dataset, double base) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; ++item) {
            double positive = base;
            double negative = base;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; ++series) {
                Number number = dataset.getValue(series, item);
                if (number == null) continue;
                double value = number.doubleValue();
                if (value > 0.0) {
                    positive += value;
                }
                if (!(value < 0.0)) continue;
                negative += value;
            }
            minimum = Math.min(minimum, negative);
            maximum = Math.max(maximum, positive);
        }
        if (minimum <= maximum) {
            result = new Range(minimum, maximum);
        }
        return result;
    }

    public static Range findStackedRangeBounds(CategoryDataset dataset, KeyToGroupMap map) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        boolean hasValidData = false;
        Range result = null;
        int[] groupIndex = new int[dataset.getRowCount()];
        for (int i = 0; i < dataset.getRowCount(); ++i) {
            groupIndex[i] = map.getGroupIndex(map.getGroup(dataset.getRowKey(i)));
        }
        int groupCount = map.getGroupCount();
        double[] minimum = new double[groupCount];
        double[] maximum = new double[groupCount];
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; ++item) {
            double[] positive = new double[groupCount];
            double[] negative = new double[groupCount];
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; ++series) {
                Number number = dataset.getValue(series, item);
                if (number == null) continue;
                hasValidData = true;
                double value = number.doubleValue();
                if (value > 0.0) {
                    positive[groupIndex[series]] = positive[groupIndex[series]] + value;
                }
                if (!(value < 0.0)) continue;
                negative[groupIndex[series]] = negative[groupIndex[series]] + value;
            }
            for (int g = 0; g < groupCount; ++g) {
                minimum[g] = Math.min(minimum[g], negative[g]);
                maximum[g] = Math.max(maximum[g], positive[g]);
            }
        }
        if (hasValidData) {
            for (int j = 0; j < groupCount; ++j) {
                result = Range.combine(result, new Range(minimum[j], maximum[j]));
            }
        }
        return result;
    }

    public static Number findMinimumStackedRangeValue(CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Double result = null;
        boolean hasValidData = false;
        double minimum = 0.0;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; ++item) {
            double total = 0.0;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; ++series) {
                Number number = dataset.getValue(series, item);
                if (number == null) continue;
                hasValidData = true;
                double value = number.doubleValue();
                if (!(value < 0.0)) continue;
                total += value;
            }
            minimum = Math.min(minimum, total);
        }
        if (hasValidData) {
            result = new Double(minimum);
        }
        return result;
    }

    public static Number findMaximumStackedRangeValue(CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Double result = null;
        boolean hasValidData = false;
        double maximum = 0.0;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; ++item) {
            double total = 0.0;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; ++series) {
                Number number = dataset.getValue(series, item);
                if (number == null) continue;
                hasValidData = true;
                double value = number.doubleValue();
                if (!(value > 0.0)) continue;
                total += value;
            }
            maximum = Math.max(maximum, total);
        }
        if (hasValidData) {
            result = new Double(maximum);
        }
        return result;
    }

    public static Range findStackedRangeBounds(TableXYDataset dataset) {
        return DatasetUtilities.findStackedRangeBounds(dataset, 0.0);
    }

    public static Range findStackedRangeBounds(TableXYDataset dataset, double base) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        double minimum = base;
        double maximum = base;
        for (int itemNo = 0; itemNo < dataset.getItemCount(); ++itemNo) {
            double positive = base;
            double negative = base;
            int seriesCount = dataset.getSeriesCount();
            for (int seriesNo = 0; seriesNo < seriesCount; ++seriesNo) {
                double y = dataset.getYValue(seriesNo, itemNo);
                if (Double.isNaN(y)) continue;
                if (y > 0.0) {
                    positive += y;
                    continue;
                }
                negative += y;
            }
            if (positive > maximum) {
                maximum = positive;
            }
            if (!(negative < minimum)) continue;
            minimum = negative;
        }
        if (minimum <= maximum) {
            return new Range(minimum, maximum);
        }
        return null;
    }

    public static double calculateStackTotal(TableXYDataset dataset, int item) {
        double total = 0.0;
        int seriesCount = dataset.getSeriesCount();
        for (int s = 0; s < seriesCount; ++s) {
            double value = dataset.getYValue(s, item);
            if (Double.isNaN(value)) continue;
            total += value;
        }
        return total;
    }

    public static Range findCumulativeRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        boolean allItemsNull = true;
        double minimum = 0.0;
        double maximum = 0.0;
        for (int row = 0; row < dataset.getRowCount(); ++row) {
            double runningTotal = 0.0;
            for (int column = 0; column <= dataset.getColumnCount() - 1; ++column) {
                Number n = dataset.getValue(row, column);
                if (n == null) continue;
                allItemsNull = false;
                double value = n.doubleValue();
                if (Double.isNaN(value)) continue;
                minimum = Math.min(minimum, runningTotal += value);
                maximum = Math.max(maximum, runningTotal);
            }
        }
        if (!allItemsNull) {
            return new Range(minimum, maximum);
        }
        return null;
    }
}

