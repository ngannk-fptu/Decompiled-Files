/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.chart;

import com.atlassian.confluence.extra.chart.ChartDefaults;
import java.awt.BasicStroke;
import java.awt.Color;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;

public class ChartUtil {
    public static final String JFREECHART_VERSION = JFreeChart.INFO.getVersion();
    public static final String JFREECHART_1_0_0 = "1.0.0";
    public static final String JFREECHART_1_0_3 = "1.0.3";
    public static final String JFREECHART_1_0_9 = "1.0.9";

    public static String getVersion() {
        return JFREECHART_VERSION;
    }

    public static int compareVersion(String version1, String version2) {
        if (StringUtils.isEmpty((String)version1) && StringUtils.isEmpty((String)version2)) {
            return 0;
        }
        if (StringUtils.isEmpty((String)version1)) {
            return -1;
        }
        if (StringUtils.isEmpty((String)version2)) {
            return 1;
        }
        return ChartUtil.compareVersion(ChartUtil.parseVersion(version1), ChartUtil.parseVersion(version2));
    }

    public static int compareVersion(int[] version1, int[] version2) {
        if (version1 == null) {
            version1 = new int[]{};
        }
        if (version2 == null) {
            version2 = new int[]{};
        }
        for (int i = 0; i < version1.length; ++i) {
            if (version2.length <= i || version1[i] > version2[i]) {
                return 1;
            }
            if (version1[i] >= version2[i]) continue;
            return -1;
        }
        if (version1.length == version2.length) {
            return 0;
        }
        return -1;
    }

    public static int[] parseVersion(String version) {
        int end;
        int start;
        if (version == null) {
            return new int[0];
        }
        char[] chars = version.toCharArray();
        for (start = 0; start < chars.length && (chars[start] < '0' || chars[start] > '9'); ++start) {
        }
        if (start == chars.length) {
            return new int[0];
        }
        int size = 1;
        for (end = start + 1; end < chars.length; ++end) {
            if (chars[end] == '.') {
                ++size;
                continue;
            }
            if (chars[end] < '0' || chars[end] > '9') break;
        }
        int[] retVersion = new int[size];
        for (int i = 0; i < size; ++i) {
            int dot = version.indexOf(46, start);
            if (dot == -1 || dot > end) {
                dot = end;
            }
            retVersion[i] = Integer.parseInt(version.substring(start, dot));
            start = dot + 1;
        }
        return retVersion;
    }

    public static boolean isVersion103Capable() {
        return ChartUtil.compareVersion(ChartUtil.getVersion(), JFREECHART_1_0_3) >= 0;
    }

    public static boolean isVersion109Capable() {
        return ChartUtil.compareVersion(ChartUtil.getVersion(), JFREECHART_1_0_9) >= 0;
    }

    public static void setDefaults(JFreeChart chart) {
        chart.setBackgroundPaint(ChartDefaults.transparent);
        chart.setBorderVisible(false);
        chart.getPlot().setNoDataMessage("No Data Available");
        ChartUtil.setupPlot(chart.getPlot());
        ChartUtil.setupTextTitle(chart.getTitle());
        ChartUtil.setupLegendTitle(chart.getLegend());
    }

    public static void setupPlot(Plot plot) {
        if (plot instanceof CategoryPlot) {
            ChartUtil.setupPlot((CategoryPlot)plot);
        } else if (plot instanceof XYPlot) {
            ChartUtil.setupPlot((XYPlot)plot);
        }
    }

    public static void setupPlot(CategoryPlot plot) {
        plot.setBackgroundPaint(ChartDefaults.transparent);
        plot.setOutlinePaint(ChartDefaults.transparent);
        plot.setRangeGridlinePaint(ChartDefaults.gridLineColor);
        plot.setRangeGridlineStroke(new BasicStroke(0.5f));
        plot.setRangeGridlinesVisible(true);
        plot.setRangeAxisLocation(ChartDefaults.rangeAxisLocation);
        plot.setDomainGridlinesVisible(false);
        ChartUtil.setupRangeAxis(plot.getRangeAxis());
        ChartUtil.setupDomainAxis(plot.getDomainAxis());
    }

    public static void setupPlot(XYPlot plot) {
        plot.setBackgroundPaint(ChartDefaults.transparent);
        plot.setOutlinePaint(ChartDefaults.transparent);
        plot.setRangeGridlinePaint(ChartDefaults.gridLineColor);
        plot.setRangeGridlineStroke(new BasicStroke(0.5f));
        plot.setRangeGridlinesVisible(true);
        plot.setRangeAxisLocation(ChartDefaults.rangeAxisLocation);
        plot.setDomainGridlinesVisible(true);
        ChartUtil.setupRangeAxis(plot.getRangeAxis());
        ChartUtil.setupDomainAxis(plot.getDomainAxis());
    }

    public static void setupRangeAxis(ValueAxis rangeAxis) {
        if (rangeAxis != null) {
            rangeAxis.setAxisLinePaint(ChartDefaults.gridLineColor);
            rangeAxis.setTickLabelPaint(ChartDefaults.axisLabelColor);
            rangeAxis.setTickMarksVisible(false);
            rangeAxis.setAxisLineVisible(false);
        }
    }

    public static void setupDomainAxis(CategoryAxis domainAxis) {
        if (domainAxis != null) {
            domainAxis.setAxisLineStroke(new BasicStroke(0.5f));
            domainAxis.setAxisLinePaint(Color.BLACK);
            domainAxis.setTickLabelPaint(ChartDefaults.axisLabelColor);
        }
    }

    public static void setupDomainAxis(ValueAxis domainAxis) {
        if (domainAxis != null) {
            domainAxis.setAxisLineStroke(new BasicStroke(0.5f));
            domainAxis.setAxisLinePaint(ChartDefaults.axisLineColor);
            domainAxis.setTickLabelPaint(ChartDefaults.axisLabelColor);
        }
    }

    public static void setupTextTitle(TextTitle title) {
        if (title != null) {
            title.setFont(ChartDefaults.titleFont);
            title.setTextAlignment(HorizontalAlignment.LEFT);
            title.setPaint(ChartDefaults.titleTextColor);
            title.setBackgroundPaint(ChartDefaults.transparent);
        }
    }

    public static void setupLegendTitle(LegendTitle legend) {
        if (legend != null) {
            legend.setBorder(0.0, 0.0, 0.0, 0.0);
            legend.setItemPaint(ChartDefaults.legendTextColor);
            legend.setMargin(2.0, 2.0, 2.0, 2.0);
        }
    }
}

