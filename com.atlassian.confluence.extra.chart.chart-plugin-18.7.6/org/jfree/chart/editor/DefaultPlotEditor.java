/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.editor.DefaultAxisEditor;
import org.jfree.chart.editor.DefaultColorBarEditor;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.PaintSample;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.StrokeChooserPanel;
import org.jfree.ui.StrokeSample;
import org.jfree.util.BooleanUtilities;

class DefaultPlotEditor
extends JPanel
implements ActionListener {
    private static final String[] orientationNames = new String[]{"Vertical", "Horizontal"};
    private static final int ORIENTATION_VERTICAL = 0;
    private static final int ORIENTATION_HORIZONTAL = 1;
    private PaintSample backgroundPaintSample;
    private StrokeSample outlineStrokeSample;
    private PaintSample outlinePaintSample;
    private DefaultAxisEditor domainAxisPropertyPanel;
    private DefaultAxisEditor rangeAxisPropertyPanel;
    private DefaultColorBarEditor colorBarAxisPropertyPanel;
    private StrokeSample[] availableStrokeSamples;
    private RectangleInsets plotInsets;
    private PlotOrientation plotOrientation;
    private JComboBox orientationCombo;
    private Boolean drawLines;
    private JCheckBox drawLinesCheckBox;
    private Boolean drawShapes;
    private JCheckBox drawShapesCheckBox;
    protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.editor.LocalizationBundle");

    public DefaultPlotEditor(Plot plot) {
        AbstractRenderer r;
        LegendItemSource renderer;
        this.plotInsets = plot.getInsets();
        this.backgroundPaintSample = new PaintSample(plot.getBackgroundPaint());
        this.outlineStrokeSample = new StrokeSample(plot.getOutlineStroke());
        this.outlinePaintSample = new PaintSample(plot.getOutlinePaint());
        if (plot instanceof CategoryPlot) {
            this.plotOrientation = ((CategoryPlot)plot).getOrientation();
        } else if (plot instanceof XYPlot) {
            this.plotOrientation = ((XYPlot)plot).getOrientation();
        }
        if (plot instanceof CategoryPlot) {
            renderer = ((CategoryPlot)plot).getRenderer();
            if (renderer instanceof LineAndShapeRenderer) {
                r = (LineAndShapeRenderer)renderer;
                this.drawLines = BooleanUtilities.valueOf(((LineAndShapeRenderer)r).getBaseLinesVisible());
                this.drawShapes = BooleanUtilities.valueOf(((LineAndShapeRenderer)r).getBaseShapesVisible());
            }
        } else if (plot instanceof XYPlot && (renderer = ((XYPlot)plot).getRenderer()) instanceof StandardXYItemRenderer) {
            r = (StandardXYItemRenderer)renderer;
            this.drawLines = BooleanUtilities.valueOf(((StandardXYItemRenderer)r).getPlotLines());
            this.drawShapes = BooleanUtilities.valueOf(((StandardXYItemRenderer)r).getBaseShapesVisible());
        }
        this.setLayout(new BorderLayout());
        this.availableStrokeSamples = new StrokeSample[4];
        this.availableStrokeSamples[0] = new StrokeSample(null);
        this.availableStrokeSamples[1] = new StrokeSample(new BasicStroke(1.0f));
        this.availableStrokeSamples[2] = new StrokeSample(new BasicStroke(2.0f));
        this.availableStrokeSamples[3] = new StrokeSample(new BasicStroke(3.0f));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), plot.getPlotType() + localizationResources.getString(":")));
        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(localizationResources.getString("General")));
        JPanel interior = new JPanel(new LCBLayout(7));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        interior.add(new JLabel(localizationResources.getString("Outline_stroke")));
        JButton button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("OutlineStroke");
        button.addActionListener(this);
        interior.add(this.outlineStrokeSample);
        interior.add(button);
        interior.add(new JLabel(localizationResources.getString("Outline_Paint")));
        button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("OutlinePaint");
        button.addActionListener(this);
        interior.add(this.outlinePaintSample);
        interior.add(button);
        interior.add(new JLabel(localizationResources.getString("Background_paint")));
        button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(this.backgroundPaintSample);
        interior.add(button);
        if (this.plotOrientation != null) {
            boolean isVertical = this.plotOrientation.equals(PlotOrientation.VERTICAL);
            int index = isVertical ? 0 : 1;
            interior.add(new JLabel(localizationResources.getString("Orientation")));
            this.orientationCombo = new JComboBox<String>(orientationNames);
            this.orientationCombo.setSelectedIndex(index);
            this.orientationCombo.setActionCommand("Orientation");
            this.orientationCombo.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.orientationCombo);
        }
        if (this.drawLines != null) {
            interior.add(new JLabel(localizationResources.getString("Draw_lines")));
            this.drawLinesCheckBox = new JCheckBox();
            this.drawLinesCheckBox.setSelected(this.drawLines);
            this.drawLinesCheckBox.setActionCommand("DrawLines");
            this.drawLinesCheckBox.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.drawLinesCheckBox);
        }
        if (this.drawShapes != null) {
            interior.add(new JLabel(localizationResources.getString("Draw_shapes")));
            this.drawShapesCheckBox = new JCheckBox();
            this.drawShapesCheckBox.setSelected(this.drawShapes);
            this.drawShapesCheckBox.setActionCommand("DrawShapes");
            this.drawShapesCheckBox.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.drawShapesCheckBox);
        }
        general.add((Component)interior, "North");
        JPanel appearance = new JPanel(new BorderLayout());
        appearance.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        appearance.add((Component)general, "North");
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        Axis domainAxis = null;
        if (plot instanceof CategoryPlot) {
            domainAxis = ((CategoryPlot)plot).getDomainAxis();
        } else if (plot instanceof XYPlot) {
            domainAxis = ((XYPlot)plot).getDomainAxis();
        }
        this.domainAxisPropertyPanel = DefaultAxisEditor.getInstance(domainAxis);
        if (this.domainAxisPropertyPanel != null) {
            this.domainAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.add(localizationResources.getString("Domain_Axis"), this.domainAxisPropertyPanel);
        }
        ValueAxis rangeAxis = null;
        if (plot instanceof CategoryPlot) {
            rangeAxis = ((CategoryPlot)plot).getRangeAxis();
        } else if (plot instanceof XYPlot) {
            rangeAxis = ((XYPlot)plot).getRangeAxis();
        }
        this.rangeAxisPropertyPanel = DefaultAxisEditor.getInstance(rangeAxis);
        if (this.rangeAxisPropertyPanel != null) {
            this.rangeAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.add(localizationResources.getString("Range_Axis"), this.rangeAxisPropertyPanel);
        }
        ColorBar colorBar = null;
        if (plot instanceof ContourPlot) {
            colorBar = ((ContourPlot)plot).getColorBar();
        }
        this.colorBarAxisPropertyPanel = DefaultColorBarEditor.getInstance(colorBar);
        if (this.colorBarAxisPropertyPanel != null) {
            this.colorBarAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.add(localizationResources.getString("Color_Bar"), this.colorBarAxisPropertyPanel);
        }
        tabs.add(localizationResources.getString("Appearance"), appearance);
        panel.add(tabs);
        this.add(panel);
    }

    public RectangleInsets getPlotInsets() {
        if (this.plotInsets == null) {
            this.plotInsets = new RectangleInsets(0.0, 0.0, 0.0, 0.0);
        }
        return this.plotInsets;
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaintSample.getPaint();
    }

    public Stroke getOutlineStroke() {
        return this.outlineStrokeSample.getStroke();
    }

    public Paint getOutlinePaint() {
        return this.outlinePaintSample.getPaint();
    }

    public DefaultAxisEditor getDomainAxisPropertyEditPanel() {
        return this.domainAxisPropertyPanel;
    }

    public DefaultAxisEditor getRangeAxisPropertyEditPanel() {
        return this.rangeAxisPropertyPanel;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            this.attemptBackgroundPaintSelection();
        } else if (command.equals("OutlineStroke")) {
            this.attemptOutlineStrokeSelection();
        } else if (command.equals("OutlinePaint")) {
            this.attemptOutlinePaintSelection();
        } else if (command.equals("Orientation")) {
            this.attemptOrientationSelection();
        } else if (command.equals("DrawLines")) {
            this.attemptDrawLinesSelection();
        } else if (command.equals("DrawShapes")) {
            this.attemptDrawShapesSelection();
        }
    }

    private void attemptBackgroundPaintSelection() {
        Color c = JColorChooser.showDialog(this, localizationResources.getString("Background_Color"), Color.blue);
        if (c != null) {
            this.backgroundPaintSample.setPaint(c);
        }
    }

    private void attemptOutlineStrokeSelection() {
        StrokeChooserPanel panel = new StrokeChooserPanel(this.outlineStrokeSample, this.availableStrokeSamples);
        int result = JOptionPane.showConfirmDialog(this, panel, localizationResources.getString("Stroke_Selection"), 2, -1);
        if (result == 0) {
            this.outlineStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }

    private void attemptOutlinePaintSelection() {
        Color c = JColorChooser.showDialog(this, localizationResources.getString("Outline_Color"), Color.blue);
        if (c != null) {
            this.outlinePaintSample.setPaint(c);
        }
    }

    private void attemptOrientationSelection() {
        int index = this.orientationCombo.getSelectedIndex();
        this.plotOrientation = index == 0 ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL;
    }

    private void attemptDrawLinesSelection() {
        this.drawLines = BooleanUtilities.valueOf(this.drawLinesCheckBox.isSelected());
    }

    private void attemptDrawShapesSelection() {
        this.drawShapes = BooleanUtilities.valueOf(this.drawShapesCheckBox.isSelected());
    }

    public void updatePlotProperties(Plot plot) {
        LegendItemSource r;
        Plot p;
        Plot p2;
        plot.setOutlinePaint(this.getOutlinePaint());
        plot.setOutlineStroke(this.getOutlineStroke());
        plot.setBackgroundPaint(this.getBackgroundPaint());
        plot.setInsets(this.getPlotInsets());
        if (this.domainAxisPropertyPanel != null) {
            Axis domainAxis = null;
            if (plot instanceof CategoryPlot) {
                p2 = (CategoryPlot)plot;
                domainAxis = ((CategoryPlot)p2).getDomainAxis();
            } else if (plot instanceof XYPlot) {
                p2 = (XYPlot)plot;
                domainAxis = ((XYPlot)p2).getDomainAxis();
            }
            if (domainAxis != null) {
                this.domainAxisPropertyPanel.setAxisProperties(domainAxis);
            }
        }
        if (this.rangeAxisPropertyPanel != null) {
            ValueAxis rangeAxis = null;
            if (plot instanceof CategoryPlot) {
                p2 = (CategoryPlot)plot;
                rangeAxis = ((CategoryPlot)p2).getRangeAxis();
            } else if (plot instanceof XYPlot) {
                p2 = (XYPlot)plot;
                rangeAxis = ((XYPlot)p2).getRangeAxis();
            }
            if (rangeAxis != null) {
                this.rangeAxisPropertyPanel.setAxisProperties(rangeAxis);
            }
        }
        if (this.plotOrientation != null) {
            if (plot instanceof CategoryPlot) {
                p = (CategoryPlot)plot;
                ((CategoryPlot)p).setOrientation(this.plotOrientation);
            } else if (plot instanceof XYPlot) {
                p = (XYPlot)plot;
                ((XYPlot)p).setOrientation(this.plotOrientation);
            }
        }
        if (this.drawLines != null) {
            if (plot instanceof CategoryPlot) {
                p = (CategoryPlot)plot;
                r = ((CategoryPlot)p).getRenderer();
                if (r instanceof LineAndShapeRenderer) {
                    ((LineAndShapeRenderer)r).setLinesVisible((boolean)this.drawLines);
                }
            } else if (plot instanceof XYPlot && (r = ((XYPlot)(p = (XYPlot)plot)).getRenderer()) instanceof StandardXYItemRenderer) {
                ((StandardXYItemRenderer)r).setPlotLines(this.drawLines);
            }
        }
        if (this.drawShapes != null) {
            if (plot instanceof CategoryPlot) {
                p = (CategoryPlot)plot;
                r = ((CategoryPlot)p).getRenderer();
                if (r instanceof LineAndShapeRenderer) {
                    ((LineAndShapeRenderer)r).setShapesVisible((boolean)this.drawShapes);
                }
            } else if (plot instanceof XYPlot && (r = ((XYPlot)(p = (XYPlot)plot)).getRenderer()) instanceof StandardXYItemRenderer) {
                ((StandardXYItemRenderer)r).setBaseShapesVisible(this.drawShapes);
            }
        }
        if (this.colorBarAxisPropertyPanel != null) {
            ColorBar colorBar = null;
            if (plot instanceof ContourPlot) {
                p2 = (ContourPlot)plot;
                colorBar = ((ContourPlot)p2).getColorBar();
            }
            if (colorBar != null) {
                this.colorBarAxisPropertyPanel.setAxisProperties(colorBar);
            }
        }
    }
}

