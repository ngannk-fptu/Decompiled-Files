/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class RenderableGraphics
extends Graphics2D
implements RenderableImage {
    private static final Class GRAPHICS2D_CLASS = class$java$awt$Graphics2D == null ? (class$java$awt$Graphics2D = RenderableGraphics.class$("java.awt.Graphics2D")) : class$java$awt$Graphics2D;
    private Rectangle2D dimensions;
    private LinkedList opArgList;
    private Point origin;
    private Shape clip;
    private Color color;
    private Font font;
    private Color background;
    private Composite composite;
    private Paint paint;
    private Stroke stroke;
    private RenderingHints renderingHints = new RenderingHints(null);
    private AffineTransform transform;
    static /* synthetic */ Class class$java$awt$Graphics2D;
    static /* synthetic */ Class class$java$awt$Color;
    static /* synthetic */ Class class$java$awt$Font;
    static /* synthetic */ Class class$java$awt$Shape;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$awt$Image;
    static /* synthetic */ Class class$java$awt$image$ImageObserver;
    static /* synthetic */ Class class$java$util$Map;
    static /* synthetic */ Class class$java$awt$geom$AffineTransform;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderableImage;
    static /* synthetic */ Class class$java$awt$image$BufferedImage;
    static /* synthetic */ Class class$java$awt$image$BufferedImageOp;
    static /* synthetic */ Class class$java$text$AttributedCharacterIterator;
    static /* synthetic */ Class class$java$awt$font$GlyphVector;
    static /* synthetic */ Class class$java$awt$Composite;
    static /* synthetic */ Class class$java$awt$Paint;
    static /* synthetic */ Class class$java$awt$Stroke;
    static /* synthetic */ Class class$java$awt$RenderingHints$Key;
    static /* synthetic */ Class class$java$lang$Object;

    public RenderableGraphics(Rectangle2D dimensions) {
        this(dimensions, new LinkedList(), new Point(0, 0), null);
    }

    private RenderableGraphics(Rectangle2D dimensions, LinkedList opArgList, Point origin, Graphics2D g) {
        if (dimensions.isEmpty()) {
            throw new RuntimeException(JaiI18N.getString("RenderableGraphics0"));
        }
        this.dimensions = dimensions;
        this.opArgList = opArgList;
        Graphics2D g2d = g;
        if (g2d == null) {
            g2d = this.getBogusGraphics2D();
        }
        this.origin = (Point)origin.clone();
        this.setClip(g2d.getClip());
        this.setColor(g2d.getColor());
        this.setFont(g2d.getFont());
        this.setBackground(g2d.getBackground());
        this.setComposite(g2d.getComposite());
        this.setRenderingHints((Map)g2d.getRenderingHints());
        this.setStroke(g2d.getStroke());
        this.setTransform(g2d.getTransform());
        if (g == null) {
            g2d.dispose();
        }
    }

    private Graphics2D getBogusGraphics2D() {
        TiledImage ti = this.createTiledImage(this.renderingHints, this.dimensions.getBounds());
        return ti.createGraphics();
    }

    private TiledImage createTiledImage(RenderingHints hints, Rectangle bounds) {
        ImageLayout layout;
        int tileWidth = bounds.width;
        int tileHeight = bounds.height;
        SampleModel sm = null;
        ColorModel cm = null;
        RenderingHints hintsObserved = null;
        if (hints != null && (layout = (ImageLayout)hints.get(JAI.KEY_IMAGE_LAYOUT)) != null) {
            hintsObserved = new RenderingHints(null);
            ImageLayout layoutObserved = new ImageLayout();
            if (layout.isValid(256)) {
                sm = layout.getSampleModel(null);
                if (sm.getWidth() != tileWidth || sm.getHeight() != tileHeight) {
                    sm = sm.createCompatibleSampleModel(tileWidth, tileHeight);
                }
                if (layoutObserved != null) {
                    layoutObserved.setSampleModel(sm);
                }
            }
            if (layout.isValid(512)) {
                cm = layout.getColorModel(null);
                if (layoutObserved != null) {
                    layoutObserved.setColorModel(cm);
                }
            }
            if (layout.isValid(64)) {
                tileWidth = layout.getTileWidth(null);
                if (layoutObserved != null) {
                    layoutObserved.setTileWidth(tileWidth);
                }
            } else if (sm != null) {
                tileWidth = sm.getWidth();
            }
            if (layout.isValid(128)) {
                tileHeight = layout.getTileHeight(null);
                if (layoutObserved != null) {
                    layoutObserved.setTileHeight(tileHeight);
                }
            } else if (sm != null) {
                tileHeight = sm.getHeight();
            }
            hintsObserved.put(JAI.KEY_IMAGE_LAYOUT, layoutObserved);
        }
        if (sm != null && (sm.getWidth() != tileWidth || sm.getHeight() != tileHeight)) {
            sm = sm.createCompatibleSampleModel(tileWidth, tileHeight);
        }
        if (!(cm == null || sm != null && JDKWorkarounds.areCompatibleDataModels(sm, cm))) {
            sm = cm.createCompatibleSampleModel(tileWidth, tileHeight);
        } else if (cm == null && sm != null) {
            cm = PlanarImage.createColorModel(sm);
            ColorModel cmRGB = ColorModel.getRGBdefault();
            if (cm == null && JDKWorkarounds.areCompatibleDataModels(sm, cmRGB)) {
                cm = cmRGB;
            }
        }
        TiledImage ti = null;
        ti = sm != null ? new TiledImage(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, sm, cm) : TiledImage.createInterleaved(bounds.x, bounds.y, bounds.width, bounds.height, 3, 0, tileWidth, tileHeight, new int[]{0, 1, 2});
        if (hintsObserved != null) {
            ti.setProperty("HINTS_OBSERVED", hintsObserved);
        }
        return ti;
    }

    private void queueOpArg(String name, Class[] argTypes, Object[] args) {
        Method method = null;
        try {
            method = GRAPHICS2D_CLASS.getMethod(name, argTypes);
        }
        catch (Exception e) {
            String message = JaiI18N.getString("TiledGraphicsGraphics2") + name;
            this.sendExceptionToListener(message, new ImagingException(e));
        }
        this.opArgList.addLast(method);
        this.opArgList.addLast(args);
    }

    private void evaluateOpList(Graphics2D g2d) {
        if (this.opArgList == null) {
            return;
        }
        ListIterator li = this.opArgList.listIterator(0);
        while (li.hasNext()) {
            Method method = (Method)li.next();
            Object[] args = (Object[])li.next();
            try {
                method.invoke((Object)g2d, args);
            }
            catch (Exception e) {
                String message = JaiI18N.getString("TiledGraphicsGraphics4") + method;
                this.sendExceptionToListener(message, new ImagingException(e));
            }
        }
    }

    public Graphics create() {
        return new RenderableGraphics(this.dimensions, this.opArgList, this.origin, this);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color c) {
        this.color = c;
        this.queueOpArg("setColor", new Class[]{class$java$awt$Color == null ? (class$java$awt$Color = RenderableGraphics.class$("java.awt.Color")) : class$java$awt$Color}, new Object[]{c});
    }

    public void setPaintMode() {
        this.queueOpArg("setPaintMode", null, null);
    }

    public void setXORMode(Color c1) {
        this.queueOpArg("setXORMode", new Class[]{class$java$awt$Color == null ? (class$java$awt$Color = RenderableGraphics.class$("java.awt.Color")) : class$java$awt$Color}, new Object[]{c1});
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        this.font = font;
        this.queueOpArg("setFont", new Class[]{class$java$awt$Font == null ? (class$java$awt$Font = RenderableGraphics.class$("java.awt.Font")) : class$java$awt$Font}, new Object[]{font});
    }

    public FontMetrics getFontMetrics(Font f) {
        Graphics2D g2d = this.getBogusGraphics2D();
        FontMetrics fontMetrics = g2d.getFontMetrics(f);
        g2d.dispose();
        return fontMetrics;
    }

    public Rectangle getClipBounds() {
        return this.clip.getBounds();
    }

    public void clipRect(int x, int y, int width, int height) {
        this.clip(new Rectangle(x, y, width, height));
    }

    public void setClip(int x, int y, int width, int height) {
        this.clip = new Rectangle(x, y, width, height);
        this.queueOpArg("setClip", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public Shape getClip() {
        return this.clip;
    }

    public void setClip(Shape clip) {
        this.clip = clip;
        this.queueOpArg("setClip", new Class[]{class$java$awt$Shape == null ? (class$java$awt$Shape = RenderableGraphics.class$("java.awt.Shape")) : class$java$awt$Shape}, new Object[]{clip});
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        this.queueOpArg("copyArea", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(dx), new Integer(dy)});
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        this.queueOpArg("drawLine", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x1), new Integer(y1), new Integer(x2), new Integer(y2)});
    }

    public void fillRect(int x, int y, int width, int height) {
        this.queueOpArg("fillRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public void clearRect(int x, int y, int width, int height) {
        this.queueOpArg("clearRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.queueOpArg("drawRoundRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(arcWidth), new Integer(arcHeight)});
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.queueOpArg("fillRoundRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(arcWidth), new Integer(arcHeight)});
    }

    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        this.queueOpArg("draw3DRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Boolean(raised)});
    }

    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        this.queueOpArg("fill3DRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Boolean(raised)});
    }

    public void drawOval(int x, int y, int width, int height) {
        this.queueOpArg("drawOval", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public void fillOval(int x, int y, int width, int height) {
        this.queueOpArg("fillOval", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.queueOpArg("drawArc", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(startAngle), new Integer(arcAngle)});
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.queueOpArg("fillArc", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(startAngle), new Integer(arcAngle)});
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        Class<?> intArrayClass = xPoints.getClass();
        this.queueOpArg("drawPolyline", new Class[]{intArrayClass, intArrayClass, Integer.TYPE}, new Object[]{xPoints, yPoints, new Integer(nPoints)});
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Class<?> intArrayClass = xPoints.getClass();
        this.queueOpArg("drawPolygon", new Class[]{intArrayClass, intArrayClass, Integer.TYPE}, new Object[]{xPoints, yPoints, new Integer(nPoints)});
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Class<?> intArrayClass = xPoints.getClass();
        this.queueOpArg("fillPolygon", new Class[]{intArrayClass, intArrayClass, Integer.TYPE}, new Object[]{xPoints, yPoints, new Integer(nPoints)});
    }

    public void drawString(String str, int x, int y) {
        this.queueOpArg("drawString", new Class[]{class$java$lang$String == null ? (class$java$lang$String = RenderableGraphics.class$("java.lang.String")) : class$java$lang$String, Integer.TYPE, Integer.TYPE}, new Object[]{str, new Integer(x), new Integer(y)});
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        this.queueOpArg("drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = RenderableGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = RenderableGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(x), new Integer(y), observer});
        return true;
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        this.queueOpArg("drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = RenderableGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = RenderableGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(x), new Integer(y), new Integer(width), new Integer(height), observer});
        return true;
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        this.queueOpArg("drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = RenderableGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, class$java$awt$Color == null ? (class$java$awt$Color = RenderableGraphics.class$("java.awt.Color")) : class$java$awt$Color, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = RenderableGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(x), new Integer(y), bgcolor, observer});
        return true;
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        this.queueOpArg("drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = RenderableGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, class$java$awt$Color == null ? (class$java$awt$Color = RenderableGraphics.class$("java.awt.Color")) : class$java$awt$Color, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = RenderableGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(x), new Integer(y), new Integer(width), new Integer(height), bgcolor, observer});
        return true;
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        this.queueOpArg("drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = RenderableGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = RenderableGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(dx1), new Integer(dy1), new Integer(dx2), new Integer(dy2), new Integer(sx1), new Integer(sy1), new Integer(sx2), new Integer(sy2), observer});
        return true;
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        this.queueOpArg("drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = RenderableGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, class$java$awt$Color == null ? (class$java$awt$Color = RenderableGraphics.class$("java.awt.Color")) : class$java$awt$Color, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = RenderableGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(dx1), new Integer(dy1), new Integer(dx2), new Integer(dy2), new Integer(sx1), new Integer(sy1), new Integer(sx2), new Integer(sy2), bgcolor, observer});
        return true;
    }

    public void dispose() {
        this.queueOpArg("dispose", null, null);
    }

    public void addRenderingHints(Map hints) {
        this.renderingHints.putAll((Map<?, ?>)hints);
        this.queueOpArg("addRenderingHints", new Class[]{class$java$util$Map == null ? (class$java$util$Map = RenderableGraphics.class$("java.util.Map")) : class$java$util$Map}, new Object[]{hints});
    }

    public void draw(Shape s) {
        this.queueOpArg("draw", new Class[]{class$java$awt$Shape == null ? (class$java$awt$Shape = RenderableGraphics.class$("java.awt.Shape")) : class$java$awt$Shape}, new Object[]{s});
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        this.queueOpArg("drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = RenderableGraphics.class$("java.awt.Image")) : class$java$awt$Image, class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = RenderableGraphics.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = RenderableGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, xform, obs});
        return true;
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        this.queueOpArg("drawRenderedImage", new Class[]{class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = RenderableGraphics.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage, class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = RenderableGraphics.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform}, new Object[]{img, xform});
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        this.queueOpArg("drawRenderableImage", new Class[]{class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = RenderableGraphics.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage, class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = RenderableGraphics.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform}, new Object[]{img, xform});
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        this.queueOpArg("drawImage", new Class[]{class$java$awt$image$BufferedImage == null ? (class$java$awt$image$BufferedImage = RenderableGraphics.class$("java.awt.image.BufferedImage")) : class$java$awt$image$BufferedImage, class$java$awt$image$BufferedImageOp == null ? (class$java$awt$image$BufferedImageOp = RenderableGraphics.class$("java.awt.image.BufferedImageOp")) : class$java$awt$image$BufferedImageOp, Integer.TYPE, Integer.TYPE}, new Object[]{img, op, new Integer(x), new Integer(y)});
    }

    public void drawString(String s, float x, float y) {
        this.queueOpArg("drawString", new Class[]{class$java$lang$String == null ? (class$java$lang$String = RenderableGraphics.class$("java.lang.String")) : class$java$lang$String, Float.TYPE, Float.TYPE}, new Object[]{s, new Float(x), new Float(y)});
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        this.queueOpArg("drawString", new Class[]{class$java$text$AttributedCharacterIterator == null ? (class$java$text$AttributedCharacterIterator = RenderableGraphics.class$("java.text.AttributedCharacterIterator")) : class$java$text$AttributedCharacterIterator, Integer.TYPE, Integer.TYPE}, new Object[]{iterator, new Integer(x), new Integer(y)});
    }

    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        this.queueOpArg("drawString", new Class[]{class$java$text$AttributedCharacterIterator == null ? (class$java$text$AttributedCharacterIterator = RenderableGraphics.class$("java.text.AttributedCharacterIterator")) : class$java$text$AttributedCharacterIterator, Float.TYPE, Float.TYPE}, new Object[]{iterator, new Float(x), new Float(y)});
    }

    public void drawGlyphVector(GlyphVector v, float x, float y) {
        this.queueOpArg("drawGlyphVector", new Class[]{class$java$awt$font$GlyphVector == null ? (class$java$awt$font$GlyphVector = RenderableGraphics.class$("java.awt.font.GlyphVector")) : class$java$awt$font$GlyphVector, Float.TYPE, Float.TYPE}, new Object[]{v, new Float(x), new Float(y)});
    }

    public void fill(Shape s) {
        this.queueOpArg("fill", new Class[]{class$java$awt$Shape == null ? (class$java$awt$Shape = RenderableGraphics.class$("java.awt.Shape")) : class$java$awt$Shape}, new Object[]{s});
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        Graphics2D g2d = this.getBogusGraphics2D();
        boolean hitTarget = g2d.hit(rect, s, onStroke);
        g2d.dispose();
        return hitTarget;
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        Graphics2D g2d = this.getBogusGraphics2D();
        GraphicsConfiguration gConf = g2d.getDeviceConfiguration();
        g2d.dispose();
        return gConf;
    }

    public FontRenderContext getFontRenderContext() {
        Graphics2D g2d = this.getBogusGraphics2D();
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();
        g2d.dispose();
        return fontRenderContext;
    }

    public void setComposite(Composite comp) {
        this.composite = comp;
        this.queueOpArg("setComposite", new Class[]{class$java$awt$Composite == null ? (class$java$awt$Composite = RenderableGraphics.class$("java.awt.Composite")) : class$java$awt$Composite}, new Object[]{comp});
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
        this.queueOpArg("setPaint", new Class[]{class$java$awt$Paint == null ? (class$java$awt$Paint = RenderableGraphics.class$("java.awt.Paint")) : class$java$awt$Paint}, new Object[]{paint});
    }

    public void setStroke(Stroke s) {
        this.stroke = s;
        this.queueOpArg("setStroke", new Class[]{class$java$awt$Stroke == null ? (class$java$awt$Stroke = RenderableGraphics.class$("java.awt.Stroke")) : class$java$awt$Stroke}, new Object[]{s});
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.renderingHints.put(hintKey, hintValue);
        this.queueOpArg("setRenderingHint", new Class[]{class$java$awt$RenderingHints$Key == null ? (class$java$awt$RenderingHints$Key = RenderableGraphics.class$("java.awt.RenderingHints$Key")) : class$java$awt$RenderingHints$Key, class$java$lang$Object == null ? (class$java$lang$Object = RenderableGraphics.class$("java.lang.Object")) : class$java$lang$Object}, new Object[]{hintKey, hintValue});
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.renderingHints.get(hintKey);
    }

    public void setRenderingHints(Map hints) {
        this.renderingHints.putAll((Map<?, ?>)hints);
        this.queueOpArg("setRenderingHints", new Class[]{class$java$util$Map == null ? (class$java$util$Map = RenderableGraphics.class$("java.util.Map")) : class$java$util$Map}, new Object[]{hints});
    }

    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    public void translate(int x, int y) {
        this.origin = new Point(x, y);
        this.transform.translate(x, y);
        this.queueOpArg("translate", new Class[]{Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y)});
    }

    public void translate(double x, double y) {
        this.transform.translate(x, y);
        this.queueOpArg("translate", new Class[]{Double.TYPE, Double.TYPE}, new Object[]{new Double(x), new Double(y)});
    }

    public void rotate(double theta) {
        this.transform.rotate(theta);
        this.queueOpArg("rotate", new Class[]{Double.TYPE}, new Object[]{new Double(theta)});
    }

    public void rotate(double theta, double x, double y) {
        this.transform.rotate(theta, x, y);
        this.queueOpArg("rotate", new Class[]{Double.TYPE, Double.TYPE, Double.TYPE}, new Object[]{new Double(theta), new Double(x), new Double(y)});
    }

    public void scale(double sx, double sy) {
        this.transform.scale(sx, sy);
        this.queueOpArg("scale", new Class[]{Double.TYPE, Double.TYPE}, new Object[]{new Double(sx), new Double(sy)});
    }

    public void shear(double shx, double shy) {
        this.transform.shear(shx, shy);
        this.queueOpArg("shear", new Class[]{Double.TYPE, Double.TYPE}, new Object[]{new Double(shx), new Double(shy)});
    }

    public void transform(AffineTransform Tx) {
        this.transform.concatenate(Tx);
        this.queueOpArg("transform", new Class[]{class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = RenderableGraphics.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform}, new Object[]{Tx});
    }

    public void setTransform(AffineTransform Tx) {
        this.transform = Tx;
        this.queueOpArg("setTransform", new Class[]{class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = RenderableGraphics.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform}, new Object[]{Tx});
    }

    public AffineTransform getTransform() {
        return this.transform;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public Composite getComposite() {
        return this.composite;
    }

    public void setBackground(Color color) {
        this.background = color;
        this.queueOpArg("setBackground", new Class[]{class$java$awt$Color == null ? (class$java$awt$Color = RenderableGraphics.class$("java.awt.Color")) : class$java$awt$Color}, new Object[]{color});
    }

    public Color getBackground() {
        return this.background;
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public void clip(Shape s) {
        if (this.clip == null) {
            this.clip = s;
        } else {
            Area clipArea = this.clip instanceof Area ? (Area)this.clip : new Area(this.clip);
            clipArea.intersect(s instanceof Area ? (Area)s : new Area(s));
            this.clip = clipArea;
        }
        this.queueOpArg("clip", new Class[]{class$java$awt$Shape == null ? (class$java$awt$Shape = RenderableGraphics.class$("java.awt.Shape")) : class$java$awt$Shape}, new Object[]{s});
    }

    public Vector getSources() {
        return null;
    }

    public Object getProperty(String name) {
        return Image.UndefinedProperty;
    }

    public String[] getPropertyNames() {
        return null;
    }

    public boolean isDynamic() {
        return false;
    }

    public float getWidth() {
        return (float)this.dimensions.getWidth();
    }

    public float getHeight() {
        return (float)this.dimensions.getHeight();
    }

    public float getMinX() {
        return (float)this.dimensions.getMinX();
    }

    public float getMinY() {
        return (float)this.dimensions.getMinY();
    }

    public RenderedImage createScaledRendering(int w, int h, RenderingHints hints) {
        if (w <= 0 && h <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("RenderableGraphics1"));
        }
        if (w <= 0) {
            w = (int)Math.round((double)h * this.dimensions.getWidth() / this.dimensions.getHeight());
        } else if (h <= 0) {
            h = (int)Math.round((double)w * this.dimensions.getHeight() / this.dimensions.getWidth());
        }
        double sx = (double)w / this.dimensions.getWidth();
        double sy = (double)h / this.dimensions.getHeight();
        AffineTransform usr2dev = new AffineTransform();
        usr2dev.setToScale(sx, sy);
        return this.createRendering(new RenderContext(usr2dev, hints));
    }

    public RenderedImage createDefaultRendering() {
        return this.createRendering(new RenderContext(new AffineTransform()));
    }

    public RenderedImage createRendering(RenderContext renderContext) {
        AffineTransform usr2dev = renderContext.getTransform();
        if (usr2dev == null) {
            usr2dev = new AffineTransform();
        }
        RenderingHints hints = renderContext.getRenderingHints();
        Shape aoi = renderContext.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.dimensions.getBounds();
        }
        Shape transformedAOI = usr2dev.createTransformedShape(aoi);
        TiledImage ti = this.createTiledImage(hints, transformedAOI.getBounds());
        Graphics2D g2d = ti.createGraphics();
        if (!usr2dev.isIdentity()) {
            AffineTransform tf = this.getTransform();
            tf.concatenate(usr2dev);
            g2d.setTransform(tf);
        }
        if (hints != null) {
            g2d.addRenderingHints(hints);
        }
        g2d.setClip(aoi);
        this.evaluateOpList(g2d);
        g2d.dispose();
        return ti;
    }

    void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = null;
        if (this.renderingHints != null) {
            listener = (ImagingListener)this.renderingHints.get(JAI.KEY_IMAGING_LISTENER);
        }
        if (listener == null) {
            listener = JAI.getDefaultInstance().getImagingListener();
        }
        listener.errorOccurred(message, e, this, false);
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

