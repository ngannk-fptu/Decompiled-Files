/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BarChartDemo1
extends ApplicationFrame {
    public BarChartDemo1(String title) {
        super(title);
        CategoryDataset dataset = BarChartDemo1.createDataset();
        JFreeChart chart = BarChartDemo1.createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        this.setContentPane(chartPanel);
    }

    private static CategoryDataset createDataset() {
        String series1 = "First";
        String series2 = "Second";
        String series3 = "Third";
        String category1 = "Category 1";
        String category2 = "Category 2";
        String category3 = "Category 3";
        String category4 = "Category 4";
        String category5 = "Category 5";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, (Comparable)((Object)series1), (Comparable)((Object)category1));
        dataset.addValue(4.0, (Comparable)((Object)series1), (Comparable)((Object)category2));
        dataset.addValue(3.0, (Comparable)((Object)series1), (Comparable)((Object)category3));
        dataset.addValue(5.0, (Comparable)((Object)series1), (Comparable)((Object)category4));
        dataset.addValue(5.0, (Comparable)((Object)series1), (Comparable)((Object)category5));
        dataset.addValue(5.0, (Comparable)((Object)series2), (Comparable)((Object)category1));
        dataset.addValue(7.0, (Comparable)((Object)series2), (Comparable)((Object)category2));
        dataset.addValue(6.0, (Comparable)((Object)series2), (Comparable)((Object)category3));
        dataset.addValue(8.0, (Comparable)((Object)series2), (Comparable)((Object)category4));
        dataset.addValue(4.0, (Comparable)((Object)series2), (Comparable)((Object)category5));
        dataset.addValue(4.0, (Comparable)((Object)series3), (Comparable)((Object)category1));
        dataset.addValue(3.0, (Comparable)((Object)series3), (Comparable)((Object)category2));
        dataset.addValue(2.0, (Comparable)((Object)series3), (Comparable)((Object)category3));
        dataset.addValue(3.0, (Comparable)((Object)series3), (Comparable)((Object)category4));
        dataset.addValue(6.0, (Comparable)((Object)series3), (Comparable)((Object)category5));
        return dataset;
    }

    private static JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart("Bar Chart Demo 1", "Category", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setDrawBarOutline(false);
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.5235987755982988));
        return chart;
    }

    public static void main(String[] args) {
        BarChartDemo1 demo = new BarChartDemo1("Bar Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}

