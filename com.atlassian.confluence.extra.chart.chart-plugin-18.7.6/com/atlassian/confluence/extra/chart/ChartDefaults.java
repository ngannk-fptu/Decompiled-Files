/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;

public class ChartDefaults {
    public static final AxisLocation rangeAxisLocation = AxisLocation.BOTTOM_OR_LEFT;
    public static final Font defaultFont = new Font("Helvetica", 0, 10);
    public static final Font titleFont = new Font("Helvetica", 1, 20);
    public static final Stroke defaultStroke = new BasicStroke(3.0f);
    public static Color axisLabelColor = Color.GRAY;
    public static Color axisLineColor = Color.BLACK;
    public static Color legendTextColor = Color.BLACK;
    public static Color titleTextColor = Color.GRAY;
    public static Color gridLineColor = Color.LIGHT_GRAY;
    public static Color outlinePaintColor = Color.WHITE;
    public static Color transparent = new Color(0, 0, 0, 0);
    private static final Color TOPLINE_LIGHT = new Color(230, 242, 250);
    private static final Color LIGHT_BLUE = new Color(71, 142, 199);
    private static final Color KHAKI = new Color(118, 152, 16);
    private static final Color ORANGE_XY = new Color(215, 86, 31);
    private static final Color YELLOW_XY = new Color(222, 228, 57);
    private static final Color DARK_BLUE = new Color(12, 67, 131);
    private static final Color GREEN = new Color(95, 190, 65);
    private static final Color LIGHT_ORANGE = new Color(245, 131, 43);
    private static final Color LIGHT_YELLOW = new Color(237, 239, 0);
    private static final Color BRIGHT_BLUE = new Color(12, 135, 201);
    private static final Color DIRTY_RED = new Color(173, 42, 21);
    private static final Color GREEN2 = new Color(174, 191, 71);
    public static final Color[] darkColors = new Color[]{LIGHT_BLUE, ORANGE_XY, KHAKI, YELLOW_XY, GREEN, DARK_BLUE, LIGHT_ORANGE, LIGHT_YELLOW, BRIGHT_BLUE, DIRTY_RED, TOPLINE_LIGHT, GREEN2};
    public static final DrawingSupplier darkColorDrawingSupplier = new DefaultDrawingSupplier(darkColors, DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
}

