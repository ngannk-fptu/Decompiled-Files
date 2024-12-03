/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartTransferable;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.OverlayChangeEvent;
import org.jfree.chart.event.OverlayChangeListener;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.ExtensionFileFilter;

public class ChartPanel
extends JPanel
implements ChartChangeListener,
ChartProgressListener,
ActionListener,
MouseListener,
MouseMotionListener,
OverlayChangeListener,
Printable,
Serializable {
    private static final long serialVersionUID = 6046366297214274674L;
    public static final boolean DEFAULT_BUFFER_USED = true;
    public static final int DEFAULT_WIDTH = 680;
    public static final int DEFAULT_HEIGHT = 420;
    public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 300;
    public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;
    public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 1024;
    public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 768;
    public static final int DEFAULT_ZOOM_TRIGGER_DISTANCE = 10;
    public static final String PROPERTIES_COMMAND = "PROPERTIES";
    public static final String COPY_COMMAND = "COPY";
    public static final String SAVE_COMMAND = "SAVE";
    public static final String PRINT_COMMAND = "PRINT";
    public static final String ZOOM_IN_BOTH_COMMAND = "ZOOM_IN_BOTH";
    public static final String ZOOM_IN_DOMAIN_COMMAND = "ZOOM_IN_DOMAIN";
    public static final String ZOOM_IN_RANGE_COMMAND = "ZOOM_IN_RANGE";
    public static final String ZOOM_OUT_BOTH_COMMAND = "ZOOM_OUT_BOTH";
    public static final String ZOOM_OUT_DOMAIN_COMMAND = "ZOOM_DOMAIN_BOTH";
    public static final String ZOOM_OUT_RANGE_COMMAND = "ZOOM_RANGE_BOTH";
    public static final String ZOOM_RESET_BOTH_COMMAND = "ZOOM_RESET_BOTH";
    public static final String ZOOM_RESET_DOMAIN_COMMAND = "ZOOM_RESET_DOMAIN";
    public static final String ZOOM_RESET_RANGE_COMMAND = "ZOOM_RESET_RANGE";
    private JFreeChart chart;
    private transient EventListenerList chartMouseListeners;
    private boolean useBuffer;
    private boolean refreshBuffer;
    private transient Image chartBuffer;
    private int chartBufferHeight;
    private int chartBufferWidth;
    private int minimumDrawWidth;
    private int minimumDrawHeight;
    private int maximumDrawWidth;
    private int maximumDrawHeight;
    private JPopupMenu popup;
    private ChartRenderingInfo info;
    private Point2D anchor;
    private double scaleX;
    private double scaleY;
    private PlotOrientation orientation = PlotOrientation.VERTICAL;
    private boolean domainZoomable = false;
    private boolean rangeZoomable = false;
    private Point2D zoomPoint = null;
    private transient Rectangle2D zoomRectangle = null;
    private boolean fillZoomRectangle = true;
    private int zoomTriggerDistance;
    private boolean horizontalAxisTrace = false;
    private boolean verticalAxisTrace = false;
    private transient Line2D verticalTraceLine;
    private transient Line2D horizontalTraceLine;
    private JMenuItem zoomInBothMenuItem;
    private JMenuItem zoomInDomainMenuItem;
    private JMenuItem zoomInRangeMenuItem;
    private JMenuItem zoomOutBothMenuItem;
    private JMenuItem zoomOutDomainMenuItem;
    private JMenuItem zoomOutRangeMenuItem;
    private JMenuItem zoomResetBothMenuItem;
    private JMenuItem zoomResetDomainMenuItem;
    private JMenuItem zoomResetRangeMenuItem;
    private File defaultDirectoryForSaveAs;
    private boolean enforceFileExtensions;
    private boolean ownToolTipDelaysActive;
    private int originalToolTipInitialDelay;
    private int originalToolTipReshowDelay;
    private int originalToolTipDismissDelay;
    private int ownToolTipInitialDelay;
    private int ownToolTipReshowDelay;
    private int ownToolTipDismissDelay;
    private double zoomInFactor = 0.5;
    private double zoomOutFactor = 2.0;
    private boolean zoomAroundAnchor;
    private transient Paint zoomOutlinePaint;
    private transient Paint zoomFillPaint;
    protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.LocalizationBundle");
    private double panW;
    private double panH;
    private Point panLast;
    private int panMask = 2;
    private List overlays;
    private Object mouseWheelHandler;
    static /* synthetic */ Class class$org$jfree$chart$ChartPanel;
    static /* synthetic */ Class class$org$jfree$chart$ChartMouseListener;

    public ChartPanel(JFreeChart chart) {
        this(chart, 680, 420, 300, 200, 1024, 768, true, true, true, true, true, true);
    }

    public ChartPanel(JFreeChart chart, boolean useBuffer) {
        this(chart, 680, 420, 300, 200, 1024, 768, useBuffer, true, true, true, true, true);
    }

    public ChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        this(chart, 680, 420, 300, 200, 1024, 768, true, properties, save, print, zoom, tooltips);
    }

    public ChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        this(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, true, save, print, zoom, tooltips);
    }

    public ChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean copy, boolean save, boolean print, boolean zoom, boolean tooltips) {
        this.setChart(chart);
        this.chartMouseListeners = new EventListenerList();
        this.info = new ChartRenderingInfo();
        this.setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.minimumDrawWidth = minimumDrawWidth;
        this.minimumDrawHeight = minimumDrawHeight;
        this.maximumDrawWidth = maximumDrawWidth;
        this.maximumDrawHeight = maximumDrawHeight;
        this.zoomTriggerDistance = 10;
        this.popup = null;
        if (properties || copy || save || print || zoom) {
            this.popup = this.createPopupMenu(properties, copy, save, print, zoom);
        }
        this.enableEvents(16L);
        this.enableEvents(32L);
        this.setDisplayToolTips(tooltips);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.defaultDirectoryForSaveAs = null;
        this.enforceFileExtensions = true;
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        this.ownToolTipInitialDelay = ttm.getInitialDelay();
        this.ownToolTipDismissDelay = ttm.getDismissDelay();
        this.ownToolTipReshowDelay = ttm.getReshowDelay();
        this.zoomAroundAnchor = false;
        this.zoomOutlinePaint = Color.blue;
        this.zoomFillPaint = new Color(0, 0, 255, 63);
        this.panMask = 2;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac os x")) {
            this.panMask = 8;
        }
        this.overlays = new ArrayList();
    }

    public JFreeChart getChart() {
        return this.chart;
    }

    public void setChart(JFreeChart chart) {
        if (this.chart != null) {
            this.chart.removeChangeListener(this);
            this.chart.removeProgressListener(this);
        }
        this.chart = chart;
        if (chart != null) {
            this.chart.addChangeListener(this);
            this.chart.addProgressListener(this);
            Plot plot = chart.getPlot();
            this.domainZoomable = false;
            this.rangeZoomable = false;
            if (plot instanceof Zoomable) {
                Zoomable z = (Zoomable)((Object)plot);
                this.domainZoomable = z.isDomainZoomable();
                this.rangeZoomable = z.isRangeZoomable();
                this.orientation = z.getOrientation();
            }
        } else {
            this.domainZoomable = false;
            this.rangeZoomable = false;
        }
        if (this.useBuffer) {
            this.refreshBuffer = true;
        }
        this.repaint();
    }

    public int getMinimumDrawWidth() {
        return this.minimumDrawWidth;
    }

    public void setMinimumDrawWidth(int width) {
        this.minimumDrawWidth = width;
    }

    public int getMaximumDrawWidth() {
        return this.maximumDrawWidth;
    }

    public void setMaximumDrawWidth(int width) {
        this.maximumDrawWidth = width;
    }

    public int getMinimumDrawHeight() {
        return this.minimumDrawHeight;
    }

    public void setMinimumDrawHeight(int height) {
        this.minimumDrawHeight = height;
    }

    public int getMaximumDrawHeight() {
        return this.maximumDrawHeight;
    }

    public void setMaximumDrawHeight(int height) {
        this.maximumDrawHeight = height;
    }

    public double getScaleX() {
        return this.scaleX;
    }

    public double getScaleY() {
        return this.scaleY;
    }

    public Point2D getAnchor() {
        return this.anchor;
    }

    protected void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }

    public JPopupMenu getPopupMenu() {
        return this.popup;
    }

    public void setPopupMenu(JPopupMenu popup) {
        this.popup = popup;
    }

    public ChartRenderingInfo getChartRenderingInfo() {
        return this.info;
    }

    public void setMouseZoomable(boolean flag) {
        this.setMouseZoomable(flag, true);
    }

    public void setMouseZoomable(boolean flag, boolean fillRectangle) {
        this.setDomainZoomable(flag);
        this.setRangeZoomable(flag);
        this.setFillZoomRectangle(fillRectangle);
    }

    public boolean isDomainZoomable() {
        return this.domainZoomable;
    }

    public void setDomainZoomable(boolean flag) {
        if (flag) {
            Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                Zoomable z = (Zoomable)((Object)plot);
                this.domainZoomable = flag && z.isDomainZoomable();
            }
        } else {
            this.domainZoomable = false;
        }
    }

    public boolean isRangeZoomable() {
        return this.rangeZoomable;
    }

    public void setRangeZoomable(boolean flag) {
        if (flag) {
            Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                Zoomable z = (Zoomable)((Object)plot);
                this.rangeZoomable = flag && z.isRangeZoomable();
            }
        } else {
            this.rangeZoomable = false;
        }
    }

    public boolean getFillZoomRectangle() {
        return this.fillZoomRectangle;
    }

    public void setFillZoomRectangle(boolean flag) {
        this.fillZoomRectangle = flag;
    }

    public int getZoomTriggerDistance() {
        return this.zoomTriggerDistance;
    }

    public void setZoomTriggerDistance(int distance) {
        this.zoomTriggerDistance = distance;
    }

    public boolean getHorizontalAxisTrace() {
        return this.horizontalAxisTrace;
    }

    public void setHorizontalAxisTrace(boolean flag) {
        this.horizontalAxisTrace = flag;
    }

    protected Line2D getHorizontalTraceLine() {
        return this.horizontalTraceLine;
    }

    protected void setHorizontalTraceLine(Line2D line) {
        this.horizontalTraceLine = line;
    }

    public boolean getVerticalAxisTrace() {
        return this.verticalAxisTrace;
    }

    public void setVerticalAxisTrace(boolean flag) {
        this.verticalAxisTrace = flag;
    }

    protected Line2D getVerticalTraceLine() {
        return this.verticalTraceLine;
    }

    protected void setVerticalTraceLine(Line2D line) {
        this.verticalTraceLine = line;
    }

    public File getDefaultDirectoryForSaveAs() {
        return this.defaultDirectoryForSaveAs;
    }

    public void setDefaultDirectoryForSaveAs(File directory) {
        if (directory != null && !directory.isDirectory()) {
            throw new IllegalArgumentException("The 'directory' argument is not a directory.");
        }
        this.defaultDirectoryForSaveAs = directory;
    }

    public boolean isEnforceFileExtensions() {
        return this.enforceFileExtensions;
    }

    public void setEnforceFileExtensions(boolean enforce) {
        this.enforceFileExtensions = enforce;
    }

    public boolean getZoomAroundAnchor() {
        return this.zoomAroundAnchor;
    }

    public void setZoomAroundAnchor(boolean zoomAroundAnchor) {
        this.zoomAroundAnchor = zoomAroundAnchor;
    }

    public Paint getZoomFillPaint() {
        return this.zoomFillPaint;
    }

    public void setZoomFillPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.zoomFillPaint = paint;
    }

    public Paint getZoomOutlinePaint() {
        return this.zoomOutlinePaint;
    }

    public void setZoomOutlinePaint(Paint paint) {
        this.zoomOutlinePaint = paint;
    }

    public boolean isMouseWheelEnabled() {
        return this.mouseWheelHandler != null;
    }

    public void setMouseWheelEnabled(boolean flag) {
        if (flag && this.mouseWheelHandler == null) {
            try {
                Class<?> c = Class.forName("org.jfree.chart.MouseWheelHandler");
                Constructor<?> cc = c.getConstructor(class$org$jfree$chart$ChartPanel == null ? (class$org$jfree$chart$ChartPanel = ChartPanel.class$("org.jfree.chart.ChartPanel")) : class$org$jfree$chart$ChartPanel);
                Object mwh = cc.newInstance(this);
                this.mouseWheelHandler = mwh;
            }
            catch (ClassNotFoundException e) {
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (this.mouseWheelHandler != null) {
            try {
                Class<?> mwl = Class.forName("java.awt.event.MouseWheelListener");
                Class c2 = class$org$jfree$chart$ChartPanel == null ? (class$org$jfree$chart$ChartPanel = ChartPanel.class$("org.jfree.chart.ChartPanel")) : class$org$jfree$chart$ChartPanel;
                Method m = c2.getMethod("removeMouseWheelListener", mwl);
                m.invoke((Object)this, this.mouseWheelHandler);
            }
            catch (ClassNotFoundException e) {
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void addOverlay(Overlay overlay) {
        if (overlay == null) {
            throw new IllegalArgumentException("Null 'overlay' argument.");
        }
        this.overlays.add(overlay);
        overlay.addChangeListener(this);
        this.repaint();
    }

    public void removeOverlay(Overlay overlay) {
        if (overlay == null) {
            throw new IllegalArgumentException("Null 'overlay' argument.");
        }
        boolean removed = this.overlays.remove(overlay);
        if (removed) {
            overlay.removeChangeListener(this);
            this.repaint();
        }
    }

    public void overlayChanged(OverlayChangeEvent event) {
        this.repaint();
    }

    public void setDisplayToolTips(boolean flag) {
        if (flag) {
            ToolTipManager.sharedInstance().registerComponent(this);
        } else {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }

    public String getToolTipText(MouseEvent e) {
        EntityCollection entities;
        String result = null;
        if (this.info != null && (entities = this.info.getEntityCollection()) != null) {
            Insets insets = this.getInsets();
            ChartEntity entity = entities.getEntity((int)((double)(e.getX() - insets.left) / this.scaleX), (int)((double)(e.getY() - insets.top) / this.scaleY));
            if (entity != null) {
                result = entity.getToolTipText();
            }
        }
        return result;
    }

    public Point translateJava2DToScreen(Point2D java2DPoint) {
        Insets insets = this.getInsets();
        int x = (int)(java2DPoint.getX() * this.scaleX + (double)insets.left);
        int y = (int)(java2DPoint.getY() * this.scaleY + (double)insets.top);
        return new Point(x, y);
    }

    public Point2D translateScreenToJava2D(Point screenPoint) {
        Insets insets = this.getInsets();
        double x = (screenPoint.getX() - (double)insets.left) / this.scaleX;
        double y = (screenPoint.getY() - (double)insets.top) / this.scaleY;
        return new Point2D.Double(x, y);
    }

    public Rectangle2D scale(Rectangle2D rect) {
        Insets insets = this.getInsets();
        double x = rect.getX() * this.getScaleX() + (double)insets.left;
        double y = rect.getY() * this.getScaleY() + (double)insets.top;
        double w = rect.getWidth() * this.getScaleX();
        double h = rect.getHeight() * this.getScaleY();
        return new Rectangle2D.Double(x, y, w, h);
    }

    public ChartEntity getEntityForPoint(int viewX, int viewY) {
        ChartEntity result = null;
        if (this.info != null) {
            Insets insets = this.getInsets();
            double x = (double)(viewX - insets.left) / this.scaleX;
            double y = (double)(viewY - insets.top) / this.scaleY;
            EntityCollection entities = this.info.getEntityCollection();
            result = entities != null ? entities.getEntity(x, y) : null;
        }
        return result;
    }

    public boolean getRefreshBuffer() {
        return this.refreshBuffer;
    }

    public void setRefreshBuffer(boolean flag) {
        this.refreshBuffer = flag;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.chart == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D)g.create();
        Dimension size = this.getSize();
        Insets insets = this.getInsets();
        Rectangle2D.Double available = new Rectangle2D.Double(insets.left, insets.top, size.getWidth() - (double)insets.left - (double)insets.right, size.getHeight() - (double)insets.top - (double)insets.bottom);
        boolean scale = false;
        double drawWidth = ((RectangularShape)available).getWidth();
        double drawHeight = ((RectangularShape)available).getHeight();
        this.scaleX = 1.0;
        this.scaleY = 1.0;
        if (drawWidth < (double)this.minimumDrawWidth) {
            this.scaleX = drawWidth / (double)this.minimumDrawWidth;
            drawWidth = this.minimumDrawWidth;
            scale = true;
        } else if (drawWidth > (double)this.maximumDrawWidth) {
            this.scaleX = drawWidth / (double)this.maximumDrawWidth;
            drawWidth = this.maximumDrawWidth;
            scale = true;
        }
        if (drawHeight < (double)this.minimumDrawHeight) {
            this.scaleY = drawHeight / (double)this.minimumDrawHeight;
            drawHeight = this.minimumDrawHeight;
            scale = true;
        } else if (drawHeight > (double)this.maximumDrawHeight) {
            this.scaleY = drawHeight / (double)this.maximumDrawHeight;
            drawHeight = this.maximumDrawHeight;
            scale = true;
        }
        Rectangle2D.Double chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth, drawHeight);
        if (this.useBuffer) {
            if (this.chartBuffer == null || (double)this.chartBufferWidth != ((RectangularShape)available).getWidth() || (double)this.chartBufferHeight != ((RectangularShape)available).getHeight()) {
                this.chartBufferWidth = (int)((RectangularShape)available).getWidth();
                this.chartBufferHeight = (int)((RectangularShape)available).getHeight();
                GraphicsConfiguration gc = g2.getDeviceConfiguration();
                this.chartBuffer = gc.createCompatibleImage(this.chartBufferWidth, this.chartBufferHeight, 3);
                this.refreshBuffer = true;
            }
            if (this.refreshBuffer) {
                this.refreshBuffer = false;
                Rectangle2D.Double bufferArea = new Rectangle2D.Double(0.0, 0.0, this.chartBufferWidth, this.chartBufferHeight);
                Graphics2D bufferG2 = (Graphics2D)this.chartBuffer.getGraphics();
                Rectangle r = new Rectangle(0, 0, this.chartBufferWidth, this.chartBufferHeight);
                bufferG2.setPaint(this.getBackground());
                bufferG2.fill(r);
                if (scale) {
                    AffineTransform saved = bufferG2.getTransform();
                    AffineTransform st = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                    bufferG2.transform(st);
                    this.chart.draw(bufferG2, chartArea, this.anchor, this.info);
                    bufferG2.setTransform(saved);
                } else {
                    this.chart.draw(bufferG2, bufferArea, this.anchor, this.info);
                }
            }
            g2.drawImage(this.chartBuffer, insets.left, insets.top, this);
        } else {
            AffineTransform saved = g2.getTransform();
            g2.translate(insets.left, insets.top);
            if (scale) {
                AffineTransform st = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                g2.transform(st);
            }
            this.chart.draw(g2, chartArea, this.anchor, this.info);
            g2.setTransform(saved);
        }
        Iterator iterator = this.overlays.iterator();
        while (iterator.hasNext()) {
            Overlay overlay = (Overlay)iterator.next();
            overlay.paintOverlay(g2, this);
        }
        this.drawZoomRectangle(g2, !this.useBuffer);
        g2.dispose();
        this.anchor = null;
        this.verticalTraceLine = null;
        this.horizontalTraceLine = null;
    }

    public void chartChanged(ChartChangeEvent event) {
        this.refreshBuffer = true;
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable)((Object)plot);
            this.orientation = z.getOrientation();
        }
        this.repaint();
    }

    public void chartProgress(ChartProgressEvent event) {
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        double screenX = -1.0;
        double screenY = -1.0;
        if (this.zoomPoint != null) {
            screenX = this.zoomPoint.getX();
            screenY = this.zoomPoint.getY();
        }
        if (command.equals(PROPERTIES_COMMAND)) {
            this.doEditChartProperties();
        } else if (command.equals(COPY_COMMAND)) {
            this.doCopy();
        } else if (command.equals(SAVE_COMMAND)) {
            try {
                this.doSaveAs();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else if (command.equals(PRINT_COMMAND)) {
            this.createChartPrintJob();
        } else if (command.equals(ZOOM_IN_BOTH_COMMAND)) {
            this.zoomInBoth(screenX, screenY);
        } else if (command.equals(ZOOM_IN_DOMAIN_COMMAND)) {
            this.zoomInDomain(screenX, screenY);
        } else if (command.equals(ZOOM_IN_RANGE_COMMAND)) {
            this.zoomInRange(screenX, screenY);
        } else if (command.equals(ZOOM_OUT_BOTH_COMMAND)) {
            this.zoomOutBoth(screenX, screenY);
        } else if (command.equals(ZOOM_OUT_DOMAIN_COMMAND)) {
            this.zoomOutDomain(screenX, screenY);
        } else if (command.equals(ZOOM_OUT_RANGE_COMMAND)) {
            this.zoomOutRange(screenX, screenY);
        } else if (command.equals(ZOOM_RESET_BOTH_COMMAND)) {
            this.restoreAutoBounds();
        } else if (command.equals(ZOOM_RESET_DOMAIN_COMMAND)) {
            this.restoreAutoDomainBounds();
        } else if (command.equals(ZOOM_RESET_RANGE_COMMAND)) {
            this.restoreAutoRangeBounds();
        }
    }

    public void mouseEntered(MouseEvent e) {
        if (!this.ownToolTipDelaysActive) {
            ToolTipManager ttm = ToolTipManager.sharedInstance();
            this.originalToolTipInitialDelay = ttm.getInitialDelay();
            ttm.setInitialDelay(this.ownToolTipInitialDelay);
            this.originalToolTipReshowDelay = ttm.getReshowDelay();
            ttm.setReshowDelay(this.ownToolTipReshowDelay);
            this.originalToolTipDismissDelay = ttm.getDismissDelay();
            ttm.setDismissDelay(this.ownToolTipDismissDelay);
            this.ownToolTipDelaysActive = true;
        }
    }

    public void mouseExited(MouseEvent e) {
        if (this.ownToolTipDelaysActive) {
            ToolTipManager ttm = ToolTipManager.sharedInstance();
            ttm.setInitialDelay(this.originalToolTipInitialDelay);
            ttm.setReshowDelay(this.originalToolTipReshowDelay);
            ttm.setDismissDelay(this.originalToolTipDismissDelay);
            this.ownToolTipDelaysActive = false;
        }
    }

    public void mousePressed(MouseEvent e) {
        Plot plot = this.chart.getPlot();
        int mods = e.getModifiers();
        if ((mods & this.panMask) == this.panMask) {
            Rectangle2D screenDataArea;
            Pannable pannable;
            if (plot instanceof Pannable && ((pannable = (Pannable)((Object)plot)).isDomainPannable() || pannable.isRangePannable()) && (screenDataArea = this.getScreenDataArea(e.getX(), e.getY())) != null && screenDataArea.contains(e.getPoint())) {
                this.panW = screenDataArea.getWidth();
                this.panH = screenDataArea.getHeight();
                this.panLast = e.getPoint();
                this.setCursor(Cursor.getPredefinedCursor(13));
            }
        } else if (this.zoomRectangle == null) {
            Rectangle2D screenDataArea = this.getScreenDataArea(e.getX(), e.getY());
            this.zoomPoint = screenDataArea != null ? this.getPointInRectangle(e.getX(), e.getY(), screenDataArea) : null;
            if (e.isPopupTrigger() && this.popup != null) {
                this.displayPopupMenu(e.getX(), e.getY());
            }
        }
    }

    private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
        double xx = Math.max(area.getMinX(), Math.min((double)x, area.getMaxX()));
        double yy = Math.max(area.getMinY(), Math.min((double)y, area.getMaxY()));
        return new Point2D.Double(xx, yy);
    }

    public void mouseDragged(MouseEvent e) {
        if (this.popup != null && this.popup.isShowing()) {
            return;
        }
        if (this.panLast != null) {
            double dx = (double)e.getX() - this.panLast.getX();
            double dy = (double)e.getY() - this.panLast.getY();
            if (dx == 0.0 && dy == 0.0) {
                return;
            }
            double wPercent = -dx / this.panW;
            double hPercent = dy / this.panH;
            boolean old = this.chart.getPlot().isNotify();
            this.chart.getPlot().setNotify(false);
            Pannable p = (Pannable)((Object)this.chart.getPlot());
            if (p.getOrientation() == PlotOrientation.VERTICAL) {
                p.panDomainAxes(wPercent, this.info.getPlotInfo(), this.panLast);
                p.panRangeAxes(hPercent, this.info.getPlotInfo(), this.panLast);
            } else {
                p.panDomainAxes(hPercent, this.info.getPlotInfo(), this.panLast);
                p.panRangeAxes(wPercent, this.info.getPlotInfo(), this.panLast);
            }
            this.panLast = e.getPoint();
            this.chart.getPlot().setNotify(old);
            return;
        }
        if (this.zoomPoint == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D)this.getGraphics();
        if (!this.useBuffer) {
            this.drawZoomRectangle(g2, true);
        }
        boolean hZoom = false;
        boolean vZoom = false;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            hZoom = this.rangeZoomable;
            vZoom = this.domainZoomable;
        } else {
            hZoom = this.domainZoomable;
            vZoom = this.rangeZoomable;
        }
        Rectangle2D scaledDataArea = this.getScreenDataArea((int)this.zoomPoint.getX(), (int)this.zoomPoint.getY());
        if (hZoom && vZoom) {
            double xmax = Math.min((double)e.getX(), scaledDataArea.getMaxX());
            double ymax = Math.min((double)e.getY(), scaledDataArea.getMaxY());
            this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), this.zoomPoint.getY(), xmax - this.zoomPoint.getX(), ymax - this.zoomPoint.getY());
        } else if (hZoom) {
            double xmax = Math.min((double)e.getX(), scaledDataArea.getMaxX());
            this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), scaledDataArea.getMinY(), xmax - this.zoomPoint.getX(), scaledDataArea.getHeight());
        } else if (vZoom) {
            double ymax = Math.min((double)e.getY(), scaledDataArea.getMaxY());
            this.zoomRectangle = new Rectangle2D.Double(scaledDataArea.getMinX(), this.zoomPoint.getY(), scaledDataArea.getWidth(), ymax - this.zoomPoint.getY());
        }
        if (this.useBuffer) {
            this.repaint();
        } else {
            this.drawZoomRectangle(g2, true);
        }
        g2.dispose();
    }

    public void mouseReleased(MouseEvent e) {
        if (this.panLast != null) {
            this.panLast = null;
            this.setCursor(Cursor.getDefaultCursor());
        } else if (this.zoomRectangle != null) {
            boolean zoomTrigger2;
            boolean hZoom = false;
            boolean vZoom = false;
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                hZoom = this.rangeZoomable;
                vZoom = this.domainZoomable;
            } else {
                hZoom = this.domainZoomable;
                vZoom = this.rangeZoomable;
            }
            boolean zoomTrigger1 = hZoom && Math.abs((double)e.getX() - this.zoomPoint.getX()) >= (double)this.zoomTriggerDistance;
            boolean bl = zoomTrigger2 = vZoom && Math.abs((double)e.getY() - this.zoomPoint.getY()) >= (double)this.zoomTriggerDistance;
            if (zoomTrigger1 || zoomTrigger2) {
                if (hZoom && (double)e.getX() < this.zoomPoint.getX() || vZoom && (double)e.getY() < this.zoomPoint.getY()) {
                    this.restoreAutoBounds();
                } else {
                    double h;
                    double w;
                    double y;
                    double x;
                    Rectangle2D screenDataArea = this.getScreenDataArea((int)this.zoomPoint.getX(), (int)this.zoomPoint.getY());
                    double maxX = screenDataArea.getMaxX();
                    double maxY = screenDataArea.getMaxY();
                    if (!vZoom) {
                        x = this.zoomPoint.getX();
                        y = screenDataArea.getMinY();
                        w = Math.min(this.zoomRectangle.getWidth(), maxX - this.zoomPoint.getX());
                        h = screenDataArea.getHeight();
                    } else if (!hZoom) {
                        x = screenDataArea.getMinX();
                        y = this.zoomPoint.getY();
                        w = screenDataArea.getWidth();
                        h = Math.min(this.zoomRectangle.getHeight(), maxY - this.zoomPoint.getY());
                    } else {
                        x = this.zoomPoint.getX();
                        y = this.zoomPoint.getY();
                        w = Math.min(this.zoomRectangle.getWidth(), maxX - this.zoomPoint.getX());
                        h = Math.min(this.zoomRectangle.getHeight(), maxY - this.zoomPoint.getY());
                    }
                    Rectangle2D.Double zoomArea = new Rectangle2D.Double(x, y, w, h);
                    this.zoom(zoomArea);
                }
                this.zoomPoint = null;
                this.zoomRectangle = null;
            } else {
                Graphics2D g2 = (Graphics2D)this.getGraphics();
                if (this.useBuffer) {
                    this.repaint();
                } else {
                    this.drawZoomRectangle(g2, true);
                }
                g2.dispose();
                this.zoomPoint = null;
                this.zoomRectangle = null;
            }
        } else if (e.isPopupTrigger() && this.popup != null) {
            this.displayPopupMenu(e.getX(), e.getY());
        }
    }

    public void mouseClicked(MouseEvent event) {
        EntityCollection entities;
        Insets insets = this.getInsets();
        int x = (int)((double)(event.getX() - insets.left) / this.scaleX);
        int y = (int)((double)(event.getY() - insets.top) / this.scaleY);
        this.anchor = new Point2D.Double(x, y);
        if (this.chart == null) {
            return;
        }
        this.chart.setNotify(true);
        EventListener[] listeners = this.chartMouseListeners.getListeners(class$org$jfree$chart$ChartMouseListener == null ? (class$org$jfree$chart$ChartMouseListener = ChartPanel.class$("org.jfree.chart.ChartMouseListener")) : class$org$jfree$chart$ChartMouseListener);
        if (listeners.length == 0) {
            return;
        }
        ChartEntity entity = null;
        if (this.info != null && (entities = this.info.getEntityCollection()) != null) {
            entity = entities.getEntity(x, y);
        }
        ChartMouseEvent chartEvent = new ChartMouseEvent(this.getChart(), event, entity);
        for (int i = listeners.length - 1; i >= 0; --i) {
            ((ChartMouseListener)listeners[i]).chartMouseClicked(chartEvent);
        }
    }

    public void mouseMoved(MouseEvent e) {
        EntityCollection entities;
        Graphics2D g2 = (Graphics2D)this.getGraphics();
        if (this.horizontalAxisTrace) {
            this.drawHorizontalAxisTrace(g2, e.getX());
        }
        if (this.verticalAxisTrace) {
            this.drawVerticalAxisTrace(g2, e.getY());
        }
        g2.dispose();
        EventListener[] listeners = this.chartMouseListeners.getListeners(class$org$jfree$chart$ChartMouseListener == null ? (class$org$jfree$chart$ChartMouseListener = ChartPanel.class$("org.jfree.chart.ChartMouseListener")) : class$org$jfree$chart$ChartMouseListener);
        if (listeners.length == 0) {
            return;
        }
        Insets insets = this.getInsets();
        int x = (int)((double)(e.getX() - insets.left) / this.scaleX);
        int y = (int)((double)(e.getY() - insets.top) / this.scaleY);
        ChartEntity entity = null;
        if (this.info != null && (entities = this.info.getEntityCollection()) != null) {
            entity = entities.getEntity(x, y);
        }
        if (this.chart != null) {
            ChartMouseEvent event = new ChartMouseEvent(this.getChart(), e, entity);
            for (int i = listeners.length - 1; i >= 0; --i) {
                ((ChartMouseListener)listeners[i]).chartMouseMoved(event);
            }
        }
    }

    public void zoomInBoth(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        this.zoomInDomain(x, y);
        this.zoomInRange(x, y);
        plot.setNotify(savedNotify);
    }

    public void zoomInDomain(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable)((Object)plot);
            z.zoomDomainAxes(this.zoomInFactor, this.info.getPlotInfo(), this.translateScreenToJava2D(new Point((int)x, (int)y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    public void zoomInRange(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable)((Object)plot);
            z.zoomRangeAxes(this.zoomInFactor, this.info.getPlotInfo(), this.translateScreenToJava2D(new Point((int)x, (int)y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    public void zoomOutBoth(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        this.zoomOutDomain(x, y);
        this.zoomOutRange(x, y);
        plot.setNotify(savedNotify);
    }

    public void zoomOutDomain(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable)((Object)plot);
            z.zoomDomainAxes(this.zoomOutFactor, this.info.getPlotInfo(), this.translateScreenToJava2D(new Point((int)x, (int)y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    public void zoomOutRange(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable)((Object)plot);
            z.zoomRangeAxes(this.zoomOutFactor, this.info.getPlotInfo(), this.translateScreenToJava2D(new Point((int)x, (int)y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    public void zoom(Rectangle2D selection) {
        Point2D selectOrigin = this.translateScreenToJava2D(new Point((int)Math.ceil(selection.getX()), (int)Math.ceil(selection.getY())));
        PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        Rectangle2D scaledDataArea = this.getScreenDataArea((int)selection.getCenterX(), (int)selection.getCenterY());
        if (selection.getHeight() > 0.0 && selection.getWidth() > 0.0) {
            double hLower = (selection.getMinX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            double hUpper = (selection.getMaxX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            double vLower = (scaledDataArea.getMaxY() - selection.getMaxY()) / scaledDataArea.getHeight();
            double vUpper = (scaledDataArea.getMaxY() - selection.getMinY()) / scaledDataArea.getHeight();
            Plot p = this.chart.getPlot();
            if (p instanceof Zoomable) {
                boolean savedNotify = p.isNotify();
                p.setNotify(false);
                Zoomable z = (Zoomable)((Object)p);
                if (z.getOrientation() == PlotOrientation.HORIZONTAL) {
                    z.zoomDomainAxes(vLower, vUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(hLower, hUpper, plotInfo, selectOrigin);
                } else {
                    z.zoomDomainAxes(hLower, hUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(vLower, vUpper, plotInfo, selectOrigin);
                }
                p.setNotify(savedNotify);
            }
        }
    }

    public void restoreAutoBounds() {
        Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        this.restoreAutoDomainBounds();
        this.restoreAutoRangeBounds();
        plot.setNotify(savedNotify);
    }

    public void restoreAutoDomainBounds() {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable)((Object)plot);
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Point2D zp = this.zoomPoint != null ? this.zoomPoint : new Point();
            z.zoomDomainAxes(0.0, this.info.getPlotInfo(), zp);
            plot.setNotify(savedNotify);
        }
    }

    public void restoreAutoRangeBounds() {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable)((Object)plot);
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Point2D zp = this.zoomPoint != null ? this.zoomPoint : new Point();
            z.zoomRangeAxes(0.0, this.info.getPlotInfo(), zp);
            plot.setNotify(savedNotify);
        }
    }

    public Rectangle2D getScreenDataArea() {
        Rectangle2D dataArea = this.info.getPlotInfo().getDataArea();
        Insets insets = this.getInsets();
        double x = dataArea.getX() * this.scaleX + (double)insets.left;
        double y = dataArea.getY() * this.scaleY + (double)insets.top;
        double w = dataArea.getWidth() * this.scaleX;
        double h = dataArea.getHeight() * this.scaleY;
        return new Rectangle2D.Double(x, y, w, h);
    }

    public Rectangle2D getScreenDataArea(int x, int y) {
        Rectangle2D result;
        PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        if (plotInfo.getSubplotCount() == 0) {
            result = this.getScreenDataArea();
        } else {
            Point2D selectOrigin = this.translateScreenToJava2D(new Point(x, y));
            int subplotIndex = plotInfo.getSubplotIndex(selectOrigin);
            if (subplotIndex == -1) {
                return null;
            }
            result = this.scale(plotInfo.getSubplotInfo(subplotIndex).getDataArea());
        }
        return result;
    }

    public int getInitialDelay() {
        return this.ownToolTipInitialDelay;
    }

    public int getReshowDelay() {
        return this.ownToolTipReshowDelay;
    }

    public int getDismissDelay() {
        return this.ownToolTipDismissDelay;
    }

    public void setInitialDelay(int delay) {
        this.ownToolTipInitialDelay = delay;
    }

    public void setReshowDelay(int delay) {
        this.ownToolTipReshowDelay = delay;
    }

    public void setDismissDelay(int delay) {
        this.ownToolTipDismissDelay = delay;
    }

    public double getZoomInFactor() {
        return this.zoomInFactor;
    }

    public void setZoomInFactor(double factor) {
        this.zoomInFactor = factor;
    }

    public double getZoomOutFactor() {
        return this.zoomOutFactor;
    }

    public void setZoomOutFactor(double factor) {
        this.zoomOutFactor = factor;
    }

    private void drawZoomRectangle(Graphics2D g2, boolean xor) {
        if (this.zoomRectangle != null) {
            if (xor) {
                g2.setXORMode(Color.gray);
            }
            if (this.fillZoomRectangle) {
                g2.setPaint(this.zoomFillPaint);
                g2.fill(this.zoomRectangle);
            } else {
                g2.setPaint(this.zoomOutlinePaint);
                g2.draw(this.zoomRectangle);
            }
            if (xor) {
                g2.setPaintMode();
            }
        }
    }

    private void drawHorizontalAxisTrace(Graphics2D g2, int x) {
        Rectangle2D dataArea = this.getScreenDataArea();
        g2.setXORMode(Color.orange);
        if ((int)dataArea.getMinX() < x && x < (int)dataArea.getMaxX()) {
            if (this.verticalTraceLine != null) {
                g2.draw(this.verticalTraceLine);
                this.verticalTraceLine.setLine(x, (int)dataArea.getMinY(), x, (int)dataArea.getMaxY());
            } else {
                this.verticalTraceLine = new Line2D.Float(x, (int)dataArea.getMinY(), x, (int)dataArea.getMaxY());
            }
            g2.draw(this.verticalTraceLine);
        }
        g2.setPaintMode();
    }

    private void drawVerticalAxisTrace(Graphics2D g2, int y) {
        Rectangle2D dataArea = this.getScreenDataArea();
        g2.setXORMode(Color.orange);
        if ((int)dataArea.getMinY() < y && y < (int)dataArea.getMaxY()) {
            if (this.horizontalTraceLine != null) {
                g2.draw(this.horizontalTraceLine);
                this.horizontalTraceLine.setLine((int)dataArea.getMinX(), y, (int)dataArea.getMaxX(), y);
            } else {
                this.horizontalTraceLine = new Line2D.Float((int)dataArea.getMinX(), y, (int)dataArea.getMaxX(), y);
            }
            g2.draw(this.horizontalTraceLine);
        }
        g2.setPaintMode();
    }

    public void doEditChartProperties() {
        ChartEditor editor = ChartEditorManager.getChartEditor(this.chart);
        int result = JOptionPane.showConfirmDialog(this, editor, localizationResources.getString("Chart_Properties"), 2, -1);
        if (result == 0) {
            editor.updateChart(this.chart);
        }
    }

    public void doCopy() {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        ChartTransferable selection = new ChartTransferable(this.chart, this.getWidth(), this.getHeight());
        systemClipboard.setContents(selection, null);
    }

    public void doSaveAs() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
        ExtensionFileFilter filter = new ExtensionFileFilter(localizationResources.getString("PNG_Image_Files"), ".png");
        fileChooser.addChoosableFileFilter(filter);
        int option = fileChooser.showSaveDialog(this);
        if (option == 0) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (this.isEnforceFileExtensions() && !filename.endsWith(".png")) {
                filename = filename + ".png";
            }
            ChartUtilities.saveChartAsPNG(new File(filename), this.chart, this.getWidth(), this.getHeight());
        }
    }

    public void createChartPrintJob() {
        PageFormat pf;
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf2 = job.pageDialog(pf = job.defaultPage());
        if (pf2 != pf) {
            job.setPrintable(this, pf2);
            if (job.printDialog()) {
                try {
                    job.print();
                }
                catch (PrinterException e) {
                    JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }

    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex != 0) {
            return 1;
        }
        Graphics2D g2 = (Graphics2D)g;
        double x = pf.getImageableX();
        double y = pf.getImageableY();
        double w = pf.getImageableWidth();
        double h = pf.getImageableHeight();
        this.chart.draw(g2, new Rectangle2D.Double(x, y, w, h), this.anchor, null);
        return 0;
    }

    public void addChartMouseListener(ChartMouseListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.chartMouseListeners.add(class$org$jfree$chart$ChartMouseListener == null ? (class$org$jfree$chart$ChartMouseListener = ChartPanel.class$("org.jfree.chart.ChartMouseListener")) : class$org$jfree$chart$ChartMouseListener, listener);
    }

    public void removeChartMouseListener(ChartMouseListener listener) {
        this.chartMouseListeners.remove(class$org$jfree$chart$ChartMouseListener == null ? (class$org$jfree$chart$ChartMouseListener = ChartPanel.class$("org.jfree.chart.ChartMouseListener")) : class$org$jfree$chart$ChartMouseListener, listener);
    }

    public EventListener[] getListeners(Class listenerType) {
        if (listenerType == (class$org$jfree$chart$ChartMouseListener == null ? (class$org$jfree$chart$ChartMouseListener = ChartPanel.class$("org.jfree.chart.ChartMouseListener")) : class$org$jfree$chart$ChartMouseListener)) {
            return this.chartMouseListeners.getListeners(listenerType);
        }
        return super.getListeners(listenerType);
    }

    protected JPopupMenu createPopupMenu(boolean properties, boolean save, boolean print, boolean zoom) {
        return this.createPopupMenu(properties, false, save, print, zoom);
    }

    protected JPopupMenu createPopupMenu(boolean properties, boolean copy, boolean save, boolean print, boolean zoom) {
        JPopupMenu result = new JPopupMenu("Chart:");
        boolean separator = false;
        if (properties) {
            JMenuItem propertiesItem = new JMenuItem(localizationResources.getString("Properties..."));
            propertiesItem.setActionCommand(PROPERTIES_COMMAND);
            propertiesItem.addActionListener(this);
            result.add(propertiesItem);
            separator = true;
        }
        if (copy) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            JMenuItem copyItem = new JMenuItem(localizationResources.getString("Copy"));
            copyItem.setActionCommand(COPY_COMMAND);
            copyItem.addActionListener(this);
            result.add(copyItem);
            boolean bl = separator = !save;
        }
        if (save) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            JMenuItem saveItem = new JMenuItem(localizationResources.getString("Save_as..."));
            saveItem.setActionCommand(SAVE_COMMAND);
            saveItem.addActionListener(this);
            result.add(saveItem);
            separator = true;
        }
        if (print) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            JMenuItem printItem = new JMenuItem(localizationResources.getString("Print..."));
            printItem.setActionCommand(PRINT_COMMAND);
            printItem.addActionListener(this);
            result.add(printItem);
            separator = true;
        }
        if (zoom) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            JMenu zoomInMenu = new JMenu(localizationResources.getString("Zoom_In"));
            this.zoomInBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomInBothMenuItem.setActionCommand(ZOOM_IN_BOTH_COMMAND);
            this.zoomInBothMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInBothMenuItem);
            zoomInMenu.addSeparator();
            this.zoomInDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomInDomainMenuItem.setActionCommand(ZOOM_IN_DOMAIN_COMMAND);
            this.zoomInDomainMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInDomainMenuItem);
            this.zoomInRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomInRangeMenuItem.setActionCommand(ZOOM_IN_RANGE_COMMAND);
            this.zoomInRangeMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInRangeMenuItem);
            result.add(zoomInMenu);
            JMenu zoomOutMenu = new JMenu(localizationResources.getString("Zoom_Out"));
            this.zoomOutBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomOutBothMenuItem.setActionCommand(ZOOM_OUT_BOTH_COMMAND);
            this.zoomOutBothMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutBothMenuItem);
            zoomOutMenu.addSeparator();
            this.zoomOutDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomOutDomainMenuItem.setActionCommand(ZOOM_OUT_DOMAIN_COMMAND);
            this.zoomOutDomainMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutDomainMenuItem);
            this.zoomOutRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomOutRangeMenuItem.setActionCommand(ZOOM_OUT_RANGE_COMMAND);
            this.zoomOutRangeMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutRangeMenuItem);
            result.add(zoomOutMenu);
            JMenu autoRangeMenu = new JMenu(localizationResources.getString("Auto_Range"));
            this.zoomResetBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomResetBothMenuItem.setActionCommand(ZOOM_RESET_BOTH_COMMAND);
            this.zoomResetBothMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetBothMenuItem);
            autoRangeMenu.addSeparator();
            this.zoomResetDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomResetDomainMenuItem.setActionCommand(ZOOM_RESET_DOMAIN_COMMAND);
            this.zoomResetDomainMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetDomainMenuItem);
            this.zoomResetRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomResetRangeMenuItem.setActionCommand(ZOOM_RESET_RANGE_COMMAND);
            this.zoomResetRangeMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetRangeMenuItem);
            result.addSeparator();
            result.add(autoRangeMenu);
        }
        return result;
    }

    protected void displayPopupMenu(int x, int y) {
        if (this.popup != null) {
            Plot plot = this.chart.getPlot();
            boolean isDomainZoomable = false;
            boolean isRangeZoomable = false;
            if (plot instanceof Zoomable) {
                Zoomable z = (Zoomable)((Object)plot);
                isDomainZoomable = z.isDomainZoomable();
                isRangeZoomable = z.isRangeZoomable();
            }
            if (this.zoomInDomainMenuItem != null) {
                this.zoomInDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomOutDomainMenuItem != null) {
                this.zoomOutDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomResetDomainMenuItem != null) {
                this.zoomResetDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomInRangeMenuItem != null) {
                this.zoomInRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomOutRangeMenuItem != null) {
                this.zoomOutRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomResetRangeMenuItem != null) {
                this.zoomResetRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomInBothMenuItem != null) {
                this.zoomInBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
            }
            if (this.zoomOutBothMenuItem != null) {
                this.zoomOutBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
            }
            if (this.zoomResetBothMenuItem != null) {
                this.zoomResetBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
            }
            this.popup.show(this, x, y);
        }
    }

    public void updateUI() {
        if (this.popup != null) {
            SwingUtilities.updateComponentTreeUI(this.popup);
        }
        super.updateUI();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.zoomFillPaint, stream);
        SerialUtilities.writePaint(this.zoomOutlinePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.zoomFillPaint = SerialUtilities.readPaint(stream);
        this.zoomOutlinePaint = SerialUtilities.readPaint(stream);
        this.chartMouseListeners = new EventListenerList();
        if (this.chart != null) {
            this.chart.addChangeListener(this);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

