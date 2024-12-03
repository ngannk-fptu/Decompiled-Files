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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Map;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

class TiledImageGraphics
extends Graphics2D {
    private static final Class GRAPHICS2D_CLASS = class$java$awt$Graphics2D == null ? (class$java$awt$Graphics2D = TiledImageGraphics.class$("java.awt.Graphics2D")) : class$java$awt$Graphics2D;
    private static final int PAINT_MODE = 1;
    private static final int XOR_MODE = 2;
    private TiledImage tiledImage;
    Hashtable properties;
    private RenderingHints renderingHints;
    private int tileWidth;
    private int tileHeight;
    private int tileXMinimum;
    private int tileXMaximum;
    private int tileYMinimum;
    private int tileYMaximum;
    private ColorModel colorModel;
    private Point origin;
    private Shape clip;
    private Color color;
    private Font font;
    private int paintMode = 1;
    private Color XORColor;
    private Color background;
    private Composite composite;
    private Paint paint;
    private Stroke stroke;
    private AffineTransform transform;
    static /* synthetic */ Class class$java$awt$Graphics2D;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$awt$Image;
    static /* synthetic */ Class class$java$awt$image$ImageObserver;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$java$awt$geom$AffineTransform;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderableImage;
    static /* synthetic */ Class class$java$awt$Color;
    static /* synthetic */ Class class$java$awt$Shape;
    static /* synthetic */ Class class$java$awt$image$BufferedImage;
    static /* synthetic */ Class class$java$awt$image$BufferedImageOp;
    static /* synthetic */ Class class$java$text$AttributedCharacterIterator;
    static /* synthetic */ Class class$java$awt$font$GlyphVector;

    private static final Rectangle getBoundingBox(int[] xPoints, int[] yPoints, int nPoints) {
        int maxY;
        int maxX;
        if (nPoints <= 0) {
            return null;
        }
        int minX = maxX = xPoints[0];
        int minY = maxY = yPoints[0];
        for (int i = 1; i < nPoints; ++i) {
            minX = Math.min(minX, xPoints[i]);
            maxX = Math.max(maxX, xPoints[i]);
            minY = Math.min(minY, yPoints[i]);
            maxY = Math.max(maxY, yPoints[i]);
        }
        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    public TiledImageGraphics(TiledImage im) {
        int dataType = im.getSampleModel().getTransferType();
        if (dataType != 0 && dataType != 2 && dataType != 1 && dataType != 3) {
            throw new UnsupportedOperationException(JaiI18N.getString("TiledImageGraphics0"));
        }
        this.tiledImage = im;
        this.tileWidth = im.getTileWidth();
        this.tileHeight = im.getTileHeight();
        this.tileXMinimum = im.getMinTileX();
        this.tileXMaximum = im.getMaxTileX();
        this.tileYMinimum = im.getMinTileY();
        this.tileYMaximum = im.getMaxTileY();
        this.colorModel = TiledImageGraphics.getColorModel(this.tiledImage);
        Graphics2D g = this.getBogusGraphics2D(false);
        this.origin = new Point(0, 0);
        this.setClip(this.tiledImage.getBounds());
        this.setColor(g.getColor());
        this.setFont(g.getFont());
        this.setPaintMode();
        this.setBackground(g.getBackground());
        this.setComposite(g.getComposite());
        this.setStroke(g.getStroke());
        this.setTransform(g.getTransform());
        g.dispose();
        this.properties = this.tiledImage.getProperties();
        this.renderingHints = new RenderingHints(this.properties);
    }

    private void copyState(Graphics2D g2d) {
        g2d.translate(this.origin.x, this.origin.y);
        this.setClip(this.getClip());
        g2d.setColor(this.getColor());
        if (this.paintMode == 1) {
            g2d.setPaintMode();
        } else if (this.XORColor != null) {
            g2d.setXORMode(this.XORColor);
        }
        g2d.setFont(this.getFont());
        g2d.setBackground(this.getBackground());
        g2d.setComposite(this.getComposite());
        if (this.paint != null) {
            g2d.setPaint(this.getPaint());
        }
        g2d.setRenderingHints(this.renderingHints);
        g2d.setStroke(this.getStroke());
        g2d.setTransform(this.getTransform());
    }

    private Graphics2D getBogusGraphics2D(boolean shouldCopyState) {
        Raster r = this.tiledImage.getTile(this.tileXMinimum, this.tileYMinimum);
        WritableRaster wr = r.createCompatibleWritableRaster(1, 1);
        BufferedImage bi = new BufferedImage(this.colorModel, wr, this.colorModel.isAlphaPremultiplied(), this.properties);
        Graphics2D bogusG2D = bi.createGraphics();
        if (shouldCopyState) {
            this.copyState(bogusG2D);
        }
        return bogusG2D;
    }

    private static ColorModel getColorModel(TiledImage ti) {
        ColorModel colorModel = ti.getColorModel();
        if (colorModel == null && colorModel == null) {
            ColorModel cm;
            SampleModel sm = ti.getSampleModel();
            colorModel = PlanarImage.createColorModel(sm);
            if (colorModel == null && JDKWorkarounds.areCompatibleDataModels(sm, cm = ColorModel.getRGBdefault())) {
                colorModel = cm;
            }
            if (colorModel == null) {
                throw new UnsupportedOperationException(JaiI18N.getString("TiledImageGraphics1"));
            }
        }
        return colorModel;
    }

    private boolean doGraphicsOp(int x, int y, int width, int height, String name, Class[] argTypes, Object[] args) {
        int maxTileY;
        int maxTileX;
        int minTileY;
        boolean returnValue = false;
        Method method = null;
        try {
            method = GRAPHICS2D_CLASS.getMethod(name, argTypes);
        }
        catch (Exception e) {
            String message = JaiI18N.getString("TiledImageGraphics2") + name;
            this.sendExceptionToListener(message, new ImagingException(e));
        }
        Rectangle bounds = new Rectangle(x, y, width, height);
        bounds = this.getTransform().createTransformedShape(bounds).getBounds();
        int minTileX = this.tiledImage.XToTileX(bounds.x);
        if (minTileX < this.tileXMinimum) {
            minTileX = this.tileXMinimum;
        }
        if ((minTileY = this.tiledImage.YToTileY(bounds.y)) < this.tileYMinimum) {
            minTileY = this.tileYMinimum;
        }
        if ((maxTileX = this.tiledImage.XToTileX(bounds.x + bounds.width - 1)) > this.tileXMaximum) {
            maxTileX = this.tileXMaximum;
        }
        if ((maxTileY = this.tiledImage.YToTileY(bounds.y + bounds.height - 1)) > this.tileYMaximum) {
            maxTileY = this.tileYMaximum;
        }
        for (int tileY = minTileY; tileY <= maxTileY; ++tileY) {
            int tileMinY = this.tiledImage.tileYToY(tileY);
            for (int tileX = minTileX; tileX <= maxTileX; ++tileX) {
                String message;
                int tileMinX = this.tiledImage.tileXToX(tileX);
                WritableRaster wr = this.tiledImage.getWritableTile(tileX, tileY);
                wr = wr.createWritableTranslatedChild(0, 0);
                BufferedImage bi = new BufferedImage(this.colorModel, wr, this.colorModel.isAlphaPremultiplied(), this.properties);
                Graphics2D g2d = bi.createGraphics();
                this.copyState(g2d);
                try {
                    Point2D origin2D = g2d.getTransform().transform(new Point2D.Double(), null);
                    Point pt = new Point((int)origin2D.getX() - tileMinX, (int)origin2D.getY() - tileMinY);
                    Point2D pt2D = g2d.getTransform().inverseTransform(pt, null);
                    g2d.translate(pt2D.getX(), pt2D.getY());
                }
                catch (Exception e) {
                    message = JaiI18N.getString("TiledImageGraphics3");
                    this.sendExceptionToListener(message, new ImagingException(e));
                }
                try {
                    Object retVal = method.invoke((Object)g2d, args);
                    if (retVal != null && retVal.getClass() == Boolean.TYPE) {
                        returnValue = (Boolean)retVal;
                    }
                }
                catch (Exception e) {
                    message = JaiI18N.getString("TiledImageGraphics3") + " " + name;
                    this.sendExceptionToListener(message, new ImagingException(e));
                }
                g2d.dispose();
                this.tiledImage.releaseWritableTile(tileX, tileY);
            }
        }
        return returnValue;
    }

    private boolean doGraphicsOp(Shape s, String name, Class[] argTypes, Object[] args) {
        Rectangle r = s.getBounds();
        return this.doGraphicsOp(r.x, r.y, r.width, r.height, name, argTypes, args);
    }

    public Graphics create() {
        TiledImageGraphics tig = new TiledImageGraphics(this.tiledImage);
        this.copyState(tig);
        return tig;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public void setPaintMode() {
        this.paintMode = 1;
    }

    public void setXORMode(Color c1) {
        this.paintMode = 2;
        this.XORColor = c1;
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public FontMetrics getFontMetrics(Font f) {
        Graphics2D g2d = this.getBogusGraphics2D(true);
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
        this.setClip(new Rectangle(x, y, width, height));
    }

    public Shape getClip() {
        return this.clip;
    }

    public void setClip(Shape clip) {
        this.clip = clip;
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        Rectangle rect = TiledImageGraphics.getBoundingBox(new int[]{x, x + dx, x + width - 1, x + width - 1 + dx}, new int[]{y, y + dy, y + height - 1, y + height - 1 + dy}, 4);
        this.doGraphicsOp(rect, "copyArea", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(dx), new Integer(dy)});
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        Rectangle rect = TiledImageGraphics.getBoundingBox(new int[]{x1, x2}, new int[]{y1, y2}, 2);
        this.doGraphicsOp(rect, "drawLine", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x1), new Integer(y1), new Integer(x2), new Integer(y2)});
    }

    public void fillRect(int x, int y, int width, int height) {
        this.doGraphicsOp(x, y, width, height, "fillRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public void clearRect(int x, int y, int width, int height) {
        this.doGraphicsOp(x, y, width, height, "clearRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.doGraphicsOp(x - arcWidth, y - arcHeight, width + 2 * arcWidth, height + 2 * arcHeight, "drawRoundRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(arcWidth), new Integer(arcHeight)});
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.doGraphicsOp(x - arcWidth, y - arcHeight, width + 2 * arcWidth, height + 2 * arcHeight, "fillRoundRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(arcWidth), new Integer(arcHeight)});
    }

    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        this.doGraphicsOp(x, y, width, height, "draw3DRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Boolean(raised)});
    }

    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        this.doGraphicsOp(x, y, width, height, "fill3DRect", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Boolean(raised)});
    }

    public void drawOval(int x, int y, int width, int height) {
        this.doGraphicsOp(x, y, width, height, "drawOval", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public void fillOval(int x, int y, int width, int height) {
        this.doGraphicsOp(x, y, width, height, "fillOval", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height)});
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.doGraphicsOp(x, y, width, height, "drawArc", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(startAngle), new Integer(arcAngle)});
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.doGraphicsOp(x, y, width, height, "fillArc", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(startAngle), new Integer(arcAngle)});
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        Class<?> intArrayClass = xPoints.getClass();
        Rectangle box = TiledImageGraphics.getBoundingBox(xPoints, yPoints, nPoints);
        if (box == null) {
            return;
        }
        this.doGraphicsOp(box, "drawPolyline", new Class[]{intArrayClass, intArrayClass, Integer.TYPE}, new Object[]{xPoints, yPoints, new Integer(nPoints)});
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Class<?> intArrayClass = xPoints.getClass();
        Rectangle box = TiledImageGraphics.getBoundingBox(xPoints, yPoints, nPoints);
        if (box == null) {
            return;
        }
        this.doGraphicsOp(box, "drawPolygon", new Class[]{intArrayClass, intArrayClass, Integer.TYPE}, new Object[]{xPoints, yPoints, new Integer(nPoints)});
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Class<?> intArrayClass = xPoints.getClass();
        Rectangle box = TiledImageGraphics.getBoundingBox(xPoints, yPoints, nPoints);
        if (box == null) {
            return;
        }
        this.doGraphicsOp(box, "fillPolygon", new Class[]{intArrayClass, intArrayClass, Integer.TYPE}, new Object[]{xPoints, yPoints, new Integer(nPoints)});
    }

    public void drawString(String str, int x, int y) {
        Rectangle2D r2d = this.getFontMetrics(this.getFont()).getStringBounds(str, this);
        r2d.setRect(x, (double)y - r2d.getHeight() + 1.0, r2d.getWidth(), r2d.getHeight());
        this.doGraphicsOp(r2d, "drawString", new Class[]{class$java$lang$String == null ? (class$java$lang$String = TiledImageGraphics.class$("java.lang.String")) : class$java$lang$String, Integer.TYPE, Integer.TYPE}, new Object[]{str, new Integer(x), new Integer(y)});
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return this.doGraphicsOp(x, y, img.getWidth(observer), img.getHeight(observer), "drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = TiledImageGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = TiledImageGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(x), new Integer(y), observer});
    }

    public void drawRenderedImage(RenderedImage im, AffineTransform transform) {
        Rectangle2D.Double srcRect = new Rectangle2D.Double(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        Rectangle2D dstRect = transform.createTransformedShape(srcRect).getBounds2D();
        this.doGraphicsOp(dstRect, "drawRenderedImage", new Class[]{class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = TiledImageGraphics.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage, class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = TiledImageGraphics.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform}, new Object[]{im, transform});
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        Rectangle2D.Double srcRect = new Rectangle2D.Double(img.getMinX(), img.getMinY(), img.getWidth(), img.getHeight());
        Rectangle2D dstRect = xform.createTransformedShape(srcRect).getBounds2D();
        this.doGraphicsOp(dstRect, "drawRenderableImage", new Class[]{class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = TiledImageGraphics.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage, class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = TiledImageGraphics.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform}, new Object[]{img, xform});
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return this.doGraphicsOp(x, y, width, height, "drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = TiledImageGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = TiledImageGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(x), new Integer(y), new Integer(width), new Integer(height), observer});
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return this.doGraphicsOp(x, y, img.getWidth(observer), img.getHeight(observer), "drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = TiledImageGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, class$java$awt$Color == null ? (class$java$awt$Color = TiledImageGraphics.class$("java.awt.Color")) : class$java$awt$Color, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = TiledImageGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(x), new Integer(y), bgcolor, observer});
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return this.doGraphicsOp(x, y, width, height, "drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = TiledImageGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, class$java$awt$Color == null ? (class$java$awt$Color = TiledImageGraphics.class$("java.awt.Color")) : class$java$awt$Color, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = TiledImageGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(x), new Integer(y), new Integer(width), new Integer(height), bgcolor, observer});
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return this.doGraphicsOp(dx1, dy1, dx2 - dx1 + 1, dy2 - dy1 + 1, "drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = TiledImageGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = TiledImageGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(dx1), new Integer(dy1), new Integer(dx2), new Integer(dy2), new Integer(sx1), new Integer(sy1), new Integer(sx2), new Integer(sy2), observer});
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return this.doGraphicsOp(dx1, dy1, dx2 - dx1 + 1, dy2 - dy1 + 1, "drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = TiledImageGraphics.class$("java.awt.Image")) : class$java$awt$Image, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, class$java$awt$Color == null ? (class$java$awt$Color = TiledImageGraphics.class$("java.awt.Color")) : class$java$awt$Color, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = TiledImageGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, new Integer(dx1), new Integer(dy1), new Integer(dx2), new Integer(dy2), new Integer(sx1), new Integer(sy1), new Integer(sx2), new Integer(sy2), bgcolor, observer});
    }

    public void dispose() {
    }

    public void addRenderingHints(Map hints) {
        this.renderingHints.putAll((Map<?, ?>)hints);
    }

    public void draw(Shape s) {
        this.doGraphicsOp(s.getBounds(), "draw", new Class[]{class$java$awt$Shape == null ? (class$java$awt$Shape = TiledImageGraphics.class$("java.awt.Shape")) : class$java$awt$Shape}, new Object[]{s});
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        Rectangle2D.Double srcRect = new Rectangle2D.Double(0.0, 0.0, img.getWidth(obs), img.getHeight(obs));
        Rectangle2D dstRect = this.transform.createTransformedShape(srcRect).getBounds2D();
        return this.doGraphicsOp(dstRect, "drawImage", new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = TiledImageGraphics.class$("java.awt.Image")) : class$java$awt$Image, class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = TiledImageGraphics.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform, class$java$awt$image$ImageObserver == null ? (class$java$awt$image$ImageObserver = TiledImageGraphics.class$("java.awt.image.ImageObserver")) : class$java$awt$image$ImageObserver}, new Object[]{img, xform, obs});
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        this.doGraphicsOp(op.getBounds2D(img), "drawImage", new Class[]{class$java$awt$image$BufferedImage == null ? (class$java$awt$image$BufferedImage = TiledImageGraphics.class$("java.awt.image.BufferedImage")) : class$java$awt$image$BufferedImage, class$java$awt$image$BufferedImageOp == null ? (class$java$awt$image$BufferedImageOp = TiledImageGraphics.class$("java.awt.image.BufferedImageOp")) : class$java$awt$image$BufferedImageOp, Integer.TYPE, Integer.TYPE}, new Object[]{img, op, new Integer(x), new Integer(y)});
    }

    public void drawString(String s, float x, float y) {
        Rectangle2D r2d = this.getFontMetrics(this.getFont()).getStringBounds(s, this);
        r2d.setRect(x, (double)y - r2d.getHeight() + 1.0, r2d.getWidth(), r2d.getHeight());
        this.doGraphicsOp(r2d, "drawString", new Class[]{class$java$lang$String == null ? (class$java$lang$String = TiledImageGraphics.class$("java.lang.String")) : class$java$lang$String, Float.TYPE, Float.TYPE}, new Object[]{s, new Float(x), new Float(y)});
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        Rectangle2D r2d = this.getFontMetrics(this.getFont()).getStringBounds(iterator, iterator.getBeginIndex(), iterator.getEndIndex(), (Graphics)this);
        r2d.setRect(x, (double)y - r2d.getHeight() + 1.0, r2d.getWidth(), r2d.getHeight());
        this.doGraphicsOp(r2d, "drawString", new Class[]{class$java$text$AttributedCharacterIterator == null ? (class$java$text$AttributedCharacterIterator = TiledImageGraphics.class$("java.text.AttributedCharacterIterator")) : class$java$text$AttributedCharacterIterator, Integer.TYPE, Integer.TYPE}, new Object[]{iterator, new Integer(x), new Integer(y)});
    }

    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        Rectangle2D r2d = this.getFontMetrics(this.getFont()).getStringBounds(iterator, iterator.getBeginIndex(), iterator.getEndIndex(), (Graphics)this);
        r2d.setRect(x, (double)y - r2d.getHeight() + 1.0, r2d.getWidth(), r2d.getHeight());
        this.doGraphicsOp(r2d, "drawString", new Class[]{class$java$text$AttributedCharacterIterator == null ? (class$java$text$AttributedCharacterIterator = TiledImageGraphics.class$("java.text.AttributedCharacterIterator")) : class$java$text$AttributedCharacterIterator, Float.TYPE, Float.TYPE}, new Object[]{iterator, new Float(x), new Float(y)});
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        this.doGraphicsOp(g.getVisualBounds(), "drawGlyphVector", new Class[]{class$java$awt$font$GlyphVector == null ? (class$java$awt$font$GlyphVector = TiledImageGraphics.class$("java.awt.font.GlyphVector")) : class$java$awt$font$GlyphVector, Float.TYPE, Float.TYPE}, new Object[]{g, new Float(x), new Float(y)});
    }

    public void fill(Shape s) {
        this.doGraphicsOp(s.getBounds(), "fill", new Class[]{class$java$awt$Shape == null ? (class$java$awt$Shape = TiledImageGraphics.class$("java.awt.Shape")) : class$java$awt$Shape}, new Object[]{s});
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        Graphics2D g2d = this.getBogusGraphics2D(true);
        boolean hitTarget = g2d.hit(rect, s, onStroke);
        g2d.dispose();
        return hitTarget;
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        Graphics2D g2d = this.getBogusGraphics2D(true);
        GraphicsConfiguration gConf = g2d.getDeviceConfiguration();
        g2d.dispose();
        return gConf;
    }

    public void setComposite(Composite comp) {
        this.composite = comp;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setStroke(Stroke s) {
        this.stroke = s;
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.renderingHints.put(hintKey, hintValue);
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.renderingHints.get(hintKey);
    }

    public void setRenderingHints(Map hints) {
        this.renderingHints.putAll((Map<?, ?>)hints);
    }

    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    public void translate(int x, int y) {
        this.origin = new Point(x, y);
        this.transform.translate(x, y);
    }

    public void translate(double x, double y) {
        this.transform.translate(x, y);
    }

    public void rotate(double theta) {
        this.transform.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        this.transform.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        this.transform.scale(sx, sy);
    }

    public void shear(double shx, double shy) {
        this.transform.shear(shx, shy);
    }

    public void transform(AffineTransform Tx) {
        this.transform.concatenate(Tx);
    }

    public void setTransform(AffineTransform Tx) {
        this.transform = Tx;
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
    }

    public FontRenderContext getFontRenderContext() {
        Graphics2D g2d = this.getBogusGraphics2D(true);
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();
        g2d.dispose();
        return fontRenderContext;
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

