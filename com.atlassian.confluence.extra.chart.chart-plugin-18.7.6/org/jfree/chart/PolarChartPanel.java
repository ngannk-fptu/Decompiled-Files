/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;

public class PolarChartPanel
extends ChartPanel {
    private static final String POLAR_ZOOM_IN_ACTION_COMMAND = "Polar Zoom In";
    private static final String POLAR_ZOOM_OUT_ACTION_COMMAND = "Polar Zoom Out";
    private static final String POLAR_AUTO_RANGE_ACTION_COMMAND = "Polar Auto Range";

    public PolarChartPanel(JFreeChart chart) {
        this(chart, true);
    }

    public PolarChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
        this.checkChart(chart);
        this.setMinimumDrawWidth(200);
        this.setMinimumDrawHeight(200);
        this.setMaximumDrawWidth(2000);
        this.setMaximumDrawHeight(2000);
    }

    public void setChart(JFreeChart chart) {
        this.checkChart(chart);
        super.setChart(chart);
    }

    protected JPopupMenu createPopupMenu(boolean properties, boolean save, boolean print, boolean zoom) {
        JPopupMenu result = super.createPopupMenu(properties, save, print, zoom);
        int zoomInIndex = this.getPopupMenuItem(result, "Zoom In");
        int zoomOutIndex = this.getPopupMenuItem(result, "Zoom Out");
        int autoIndex = this.getPopupMenuItem(result, "Auto Range");
        if (zoom) {
            JMenuItem zoomIn = new JMenuItem("Zoom In");
            zoomIn.setActionCommand(POLAR_ZOOM_IN_ACTION_COMMAND);
            zoomIn.addActionListener(this);
            JMenuItem zoomOut = new JMenuItem("Zoom Out");
            zoomOut.setActionCommand(POLAR_ZOOM_OUT_ACTION_COMMAND);
            zoomOut.addActionListener(this);
            JMenuItem auto = new JMenuItem("Auto Range");
            auto.setActionCommand(POLAR_AUTO_RANGE_ACTION_COMMAND);
            auto.addActionListener(this);
            if (zoomInIndex != -1) {
                result.remove(zoomInIndex);
            } else {
                zoomInIndex = result.getComponentCount() - 1;
            }
            result.add((Component)zoomIn, zoomInIndex);
            if (zoomOutIndex != -1) {
                result.remove(zoomOutIndex);
            } else {
                zoomOutIndex = zoomInIndex + 1;
            }
            result.add((Component)zoomOut, zoomOutIndex);
            if (autoIndex != -1) {
                result.remove(autoIndex);
            } else {
                autoIndex = zoomOutIndex + 1;
            }
            result.add((Component)auto, autoIndex);
        }
        return result;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals(POLAR_ZOOM_IN_ACTION_COMMAND)) {
            PolarPlot plot = (PolarPlot)this.getChart().getPlot();
            plot.zoom(0.5);
        } else if (command.equals(POLAR_ZOOM_OUT_ACTION_COMMAND)) {
            PolarPlot plot = (PolarPlot)this.getChart().getPlot();
            plot.zoom(2.0);
        } else if (command.equals(POLAR_AUTO_RANGE_ACTION_COMMAND)) {
            PolarPlot plot = (PolarPlot)this.getChart().getPlot();
            plot.getAxis().setAutoRange(true);
        } else {
            super.actionPerformed(event);
        }
    }

    private void checkChart(JFreeChart chart) {
        Plot plot = chart.getPlot();
        if (!(plot instanceof PolarPlot)) {
            throw new IllegalArgumentException("plot is not a PolarPlot");
        }
    }

    private int getPopupMenuItem(JPopupMenu menu, String text) {
        int index = -1;
        for (int i = 0; index == -1 && i < menu.getComponentCount(); ++i) {
            JMenuItem item;
            Component comp = menu.getComponent(i);
            if (!(comp instanceof JMenuItem) || !text.equals((item = (JMenuItem)comp).getText())) continue;
            index = i;
        }
        return index;
    }
}

