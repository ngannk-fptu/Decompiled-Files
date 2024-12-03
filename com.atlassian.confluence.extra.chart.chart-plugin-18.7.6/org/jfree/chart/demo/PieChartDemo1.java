/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.demo;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PieChartDemo1
extends ApplicationFrame {
    public PieChartDemo1(String title) {
        super(title);
        this.setContentPane(PieChartDemo1.createDemoPanel());
    }

    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue((Comparable)((Object)"One"), new Double(43.2));
        dataset.setValue((Comparable)((Object)"Two"), new Double(10.0));
        dataset.setValue((Comparable)((Object)"Three"), new Double(27.5));
        dataset.setValue((Comparable)((Object)"Four"), new Double(17.5));
        dataset.setValue((Comparable)((Object)"Five"), new Double(11.0));
        dataset.setValue((Comparable)((Object)"Six"), new Double(19.4));
        return dataset;
    }

    private static JFreeChart createChart(PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart("Pie Chart Demo 1", dataset, true, true, false);
        PiePlot plot = (PiePlot)chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setNoDataMessage("No data available");
        return chart;
    }

    public static JPanel createDemoPanel() {
        JFreeChart chart = PieChartDemo1.createChart(PieChartDemo1.createDataset());
        return new ChartPanel(chart);
    }

    public static void main(String[] args) {
        PieChartDemo1 demo = new PieChartDemo1("Pie Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}

