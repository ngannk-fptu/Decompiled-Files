/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.chart;

import com.atlassian.confluence.extra.chart.ChartDefaults;
import com.atlassian.confluence.extra.chart.ChartUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.text.NumberFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Rotation;

public abstract class ConfluenceChartFactory
extends ChartFactory {
    public static JFreeChart createPieChart(String title, PieDataset dataset, boolean legend, boolean tooltips, boolean urls, boolean is3d) {
        if (is3d) {
            return ConfluenceChartFactory.createPieChart3D(title, dataset, legend, tooltips, urls);
        }
        return ConfluenceChartFactory.createPieChart(title, dataset, legend, tooltips, urls);
    }

    public static JFreeChart createPieChart(String title, PieDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, legend, tooltips, urls);
        ConfluenceChartFactory.setPieChartDefaults(chart, dataset);
        return chart;
    }

    public static JFreeChart createPieChart3D(String title, PieDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createPieChart3D(title, dataset, legend, tooltips, urls);
        ConfluenceChartFactory.setPieChartDefaults(chart, dataset);
        return chart;
    }

    public static JFreeChart createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, boolean is3d, boolean isStacked) {
        if (is3d && isStacked) {
            return ConfluenceChartFactory.createStackedBarChart3D(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        }
        if (isStacked) {
            return ConfluenceChartFactory.createStackedBarChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        }
        if (is3d) {
            return ConfluenceChartFactory.createBarChart3D(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        }
        return ConfluenceChartFactory.createBarChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
    }

    public static JFreeChart createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createBarChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setBarChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createStackedBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createStackedBarChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setBarChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createBarChart3D(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createBarChart3D(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setBarChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createStackedBarChart3D(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createStackedBarChart3D(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setBarChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createAreaChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, boolean isStacked) {
        if (isStacked) {
            return ConfluenceChartFactory.createStackedAreaChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        }
        return ConfluenceChartFactory.createAreaChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
    }

    public static JFreeChart createAreaChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createAreaChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setAreaChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createStackedAreaChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createStackedAreaChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setAreaChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createLineChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, boolean is3d, boolean showShapes) {
        if (is3d) {
            return ConfluenceChartFactory.createLineChart3D(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls, showShapes);
        }
        return ConfluenceChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls, showShapes);
    }

    public static JFreeChart createLineChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        return ConfluenceChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls, true);
    }

    public static JFreeChart createLineChart3D(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        return ConfluenceChartFactory.createLineChart3D(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls, true);
    }

    public static JFreeChart createLineChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, boolean showShapes) {
        JFreeChart chart = ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setLineChartDefaults(chart, showShapes);
        return chart;
    }

    public static JFreeChart createLineChart3D(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, boolean showShapes) {
        JFreeChart chart = ChartFactory.createLineChart3D(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setLineChartDefaults(chart, showShapes);
        return chart;
    }

    public static JFreeChart createGanttChart(String title, String categoryAxisLabel, String dateAxisLabel, IntervalCategoryDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createGanttChart(title, categoryAxisLabel, dateAxisLabel, dataset, legend, tooltips, urls);
        ConfluenceChartFactory.setGanttChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createScatterPlot(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setScatterPlotDefaults(chart);
        return chart;
    }

    public static JFreeChart createXYBarChart(String title, String xAxisLabel, boolean dateAxis, String yAxisLabel, IntervalXYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createXYBarChart(title, xAxisLabel, dateAxis, yAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setXYBarChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createXYAreaChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createXYAreaChart(title, xAxisLabel, yAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setXYAreaChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createStackedXYAreaChart(String title, String xAxisLabel, String yAxisLabel, TableXYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createStackedXYAreaChart(title, xAxisLabel, yAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setXYAreaChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createXYLineChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setXYLineChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createXYStepChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createXYStepChart(title, xAxisLabel, yAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setXYStepChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createXYStepAreaChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createXYStepAreaChart(title, xAxisLabel, yAxisLabel, dataset, orientation, legend, tooltips, urls);
        ConfluenceChartFactory.setXYStepAreaChartDefaults(chart);
        return chart;
    }

    public static JFreeChart createTimeSeriesChart(String title, String timeAxisLabel, String valueAxisLabel, XYDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset, legend, tooltips, urls);
        ConfluenceChartFactory.setTimeSeriesChartDefaults(chart);
        return chart;
    }

    private static void setPieChartDefaults(JFreeChart chart, PieDataset dataset) {
        ChartUtil.setDefaults(chart);
        PiePlot plot = (PiePlot)chart.getPlot();
        plot.setBackgroundPaint(ChartDefaults.transparent);
        plot.setOutlinePaint(ChartDefaults.transparent);
        plot.setCircular(true);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setIgnoreNullValues(true);
        plot.setIgnoreZeroValues(true);
        plot.setStartAngle(290.0);
        plot.setShadowXOffset(0.0);
        plot.setShadowYOffset(0.0);
        plot.setBaseSectionOutlinePaint(ChartDefaults.outlinePaintColor);
        plot.setBaseSectionOutlineStroke(new BasicStroke(2.0f));
        plot.setToolTipGenerator(new StandardPieToolTipGenerator("{0} {1} ({2})"));
        for (int j = 0; j < dataset.getItemCount() && j < ChartDefaults.darkColors.length && dataset.getValue(j).intValue() > 0; ++j) {
            if (ChartUtil.isVersion103Capable()) {
                plot.setSectionPaint(dataset.getKey(j), (Paint)ChartDefaults.darkColors[j]);
                continue;
            }
            plot.setSectionPaint(j, (Paint)ChartDefaults.darkColors[j]);
        }
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));
        plot.setLabelGap(0.04);
        plot.setLabelBackgroundPaint(ChartDefaults.transparent);
        plot.setLabelOutlinePaint(Color.gray.brighter());
        plot.setLabelShadowPaint(ChartDefaults.transparent);
        plot.setLabelFont(ChartDefaults.defaultFont);
        plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({1} - {2})"));
        plot.setLegendItemShape(new Rectangle(0, 0, 10, 10));
    }

    private static void setBarChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        plot.setAxisOffset(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelsVisible(false);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        StandardCategoryToolTipGenerator generator = new StandardCategoryToolTipGenerator("{1}, {2}", NumberFormat.getInstance());
        renderer.setBaseToolTipGenerator(generator);
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.1);
        renderer.setItemMargin(0.02f);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
    }

    private static void setAreaChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        AreaRenderer renderer = (AreaRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
    }

    private static void setLineChartDefaults(JFreeChart chart, boolean showShapes) {
        ChartUtil.setDefaults(chart);
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
        renderer.setBaseShapesVisible(showShapes);
        renderer.setBaseStroke(ChartDefaults.defaultStroke);
        renderer.setStroke(ChartDefaults.defaultStroke);
    }

    private static void setGanttChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        plot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
    }

    private static void setScatterPlotDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        XYPlot plot = (XYPlot)chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
            renderer.setSeriesStroke(j, ChartDefaults.defaultStroke);
        }
        renderer.setBaseStroke(ChartDefaults.defaultStroke);
    }

    private static void setXYBarChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setAxisOffset(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
        XYBarRenderer renderer = (XYBarRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelsVisible(false);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        StandardXYToolTipGenerator generator = new StandardXYToolTipGenerator("{1}, {2}", NumberFormat.getInstance(), NumberFormat.getInstance());
        renderer.setBaseToolTipGenerator(generator);
        renderer.setDrawBarOutline(false);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
    }

    private static void setXYAreaChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        XYPlot plot = chart.getXYPlot();
        AbstractRenderer renderer = (AbstractRenderer)((Object)plot.getRenderer());
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
    }

    private static void setXYLineChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        XYPlot plot = (XYPlot)chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        renderer.setBaseShapesVisible(false);
        renderer.setBaseStroke(ChartDefaults.defaultStroke);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesStroke(j, ChartDefaults.defaultStroke);
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
    }

    private static void setXYStepChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setDomainGridlinesVisible(false);
        XYStepRenderer renderer = (XYStepRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseStroke(ChartDefaults.defaultStroke);
        renderer.setShapesVisible(false);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesStroke(j, ChartDefaults.defaultStroke);
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
    }

    private static void setXYStepAreaChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setDomainGridlinesVisible(false);
        XYStepAreaRenderer renderer = (XYStepAreaRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        renderer.setShapesVisible(false);
        renderer.setBaseStroke(ChartDefaults.defaultStroke);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesStroke(j, ChartDefaults.defaultStroke);
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
        }
        StandardXYToolTipGenerator generator = new StandardXYToolTipGenerator("{1}, {2}", NumberFormat.getInstance(), NumberFormat.getInstance());
        renderer.setBaseToolTipGenerator(generator);
    }

    private static void setTimeSeriesChartDefaults(JFreeChart chart) {
        ChartUtil.setDefaults(chart);
        XYPlot plot = (XYPlot)chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
        renderer.setBaseItemLabelFont(ChartDefaults.defaultFont);
        renderer.setBaseItemLabelPaint(ChartDefaults.axisLabelColor);
        for (int j = 0; j < ChartDefaults.darkColors.length; ++j) {
            renderer.setSeriesPaint(j, ChartDefaults.darkColors[j]);
            renderer.setSeriesStroke(j, ChartDefaults.defaultStroke);
        }
        renderer.setBaseShapesVisible(false);
        renderer.setBaseStroke(ChartDefaults.defaultStroke);
    }
}

