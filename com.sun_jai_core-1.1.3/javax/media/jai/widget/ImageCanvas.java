/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.widget;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.HashSet;
import java.util.Iterator;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.widget.JaiI18N;

public class ImageCanvas
extends Canvas {
    protected RenderedImage im;
    protected SampleModel sampleModel;
    protected ColorModel colorModel;
    protected int minTileX;
    protected int maxTileX;
    protected int minTileY;
    protected int maxTileY;
    protected int tileWidth;
    protected int tileHeight;
    protected int tileGridXOffset;
    protected int tileGridYOffset;
    protected int imWidth;
    protected int imHeight;
    protected int padX;
    protected int padY;
    protected boolean drawBorder = false;
    protected int originX;
    protected int originY;
    protected int canvasWidth = 0;
    protected int canvasHeight = 0;
    private Color grayColor = new Color(192, 192, 192);
    private Color backgroundColor = null;
    private HashSet paintListeners = new HashSet();

    private synchronized void initialize() {
        Object col;
        int mx = this.im.getMinX();
        int my = this.im.getMinY();
        if (mx < 0 || my < 0) {
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(this.im);
            pb.add((float)Math.max(-mx, 0));
            pb.add((float)Math.max(-my, 0));
            pb.add(new InterpolationNearest());
            this.im = JAI.create("translate", pb, null);
        }
        this.sampleModel = this.im.getSampleModel();
        this.colorModel = this.im.getColorModel();
        if (this.colorModel == null) {
            this.colorModel = PlanarImage.createColorModel(this.im.getSampleModel());
            if (this.colorModel == null) {
                throw new IllegalArgumentException(JaiI18N.getString("ImageCanvas0"));
            }
        }
        if ((col = this.im.getProperty("background_color")) != Image.UndefinedProperty) {
            this.backgroundColor = (Color)col;
        }
        this.minTileX = this.im.getMinTileX();
        this.maxTileX = this.im.getMinTileX() + this.im.getNumXTiles() - 1;
        this.minTileY = this.im.getMinTileY();
        this.maxTileY = this.im.getMinTileY() + this.im.getNumYTiles() - 1;
        this.tileWidth = this.im.getTileWidth();
        this.tileHeight = this.im.getTileHeight();
        this.tileGridXOffset = this.im.getTileGridXOffset();
        this.tileGridYOffset = this.im.getTileGridYOffset();
        this.imWidth = this.im.getMinX() + this.im.getWidth();
        this.imHeight = this.im.getMinY() + this.im.getHeight();
        this.originY = 0;
        this.originX = 0;
    }

    public ImageCanvas(RenderedImage im, boolean drawBorder) {
        this.im = im;
        this.drawBorder = drawBorder;
        this.initialize();
    }

    public ImageCanvas(RenderedImage im) {
        this(im, false);
    }

    public void addNotify() {
        super.addNotify();
        this.initialize();
    }

    public synchronized void set(RenderedImage im) {
        this.im = im;
        this.initialize();
        this.repaint();
    }

    public void setOrigin(int x, int y) {
        this.padX = 0;
        this.padY = 0;
        this.originX = x;
        this.originY = y;
        this.repaint();
    }

    public int getXOrigin() {
        return this.originX;
    }

    public int getYOrigin() {
        return this.originY;
    }

    public int getXPad() {
        return this.padX;
    }

    public int getYPad() {
        return this.padY;
    }

    public Dimension getMinimumSize() {
        return new Dimension(this.im.getMinX() + this.im.getWidth() + (this.drawBorder ? 4 : 0), this.im.getMinY() + this.im.getHeight() + (this.drawBorder ? 4 : 0));
    }

    public Dimension getPreferredSize() {
        return this.getMinimumSize();
    }

    public Dimension getMaximumSize() {
        return this.getMinimumSize();
    }

    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.padX = Math.max((this.canvasWidth - this.imWidth - (this.drawBorder ? 4 : 0)) / 2, 0);
        this.padY = Math.max((this.canvasHeight - this.imHeight - (this.drawBorder ? 4 : 0)) / 2, 0);
    }

    private int XtoTileX(int x) {
        return (int)Math.floor((double)(x - this.tileGridXOffset) / (double)this.tileWidth);
    }

    private int YtoTileY(int y) {
        return (int)Math.floor((double)(y - this.tileGridYOffset) / (double)this.tileHeight);
    }

    private int TileXtoX(int tx) {
        return tx * this.tileWidth + this.tileGridXOffset;
    }

    private int TileYtoY(int ty) {
        return ty * this.tileHeight + this.tileGridYOffset;
    }

    public void update(Graphics g) {
        this.paint(g);
    }

    public synchronized void paint(Graphics g) {
        Rectangle clipBounds;
        if (this.im == null) {
            return;
        }
        Graphics2D g2D = null;
        if (!(g instanceof Graphics2D)) {
            System.err.println(JaiI18N.getString("ImageCanvas1"));
            return;
        }
        g2D = (Graphics2D)g;
        Color saveColor = g2D.getColor();
        if (this.drawBorder) {
            g.setColor(new Color(171, 171, 171));
            g.draw3DRect(this.padX, this.padY, this.imWidth + 3, this.imHeight + 3, true);
            g.draw3DRect(this.padX + 1, this.padY + 1, this.imWidth + 1, this.imHeight + 1, true);
        }
        if ((clipBounds = g.getClipBounds()) == null) {
            clipBounds = new Rectangle(0, 0, this.canvasWidth, this.canvasHeight);
        }
        int border = this.drawBorder ? 2 : 0;
        int transX = this.padX + border - this.originX;
        int transY = this.padY + border - this.originY;
        clipBounds.translate(-transX, -transY);
        int txmin = this.XtoTileX(clipBounds.x);
        txmin = Math.max(txmin, this.minTileX);
        txmin = Math.min(txmin, this.maxTileX);
        int txmax = this.XtoTileX(clipBounds.x + clipBounds.width - 1);
        txmax = Math.max(txmax, this.minTileX);
        txmax = Math.min(txmax, this.maxTileX);
        int tymin = this.YtoTileY(clipBounds.y);
        tymin = Math.max(tymin, this.minTileY);
        tymin = Math.min(tymin, this.maxTileY);
        int tymax = this.YtoTileY(clipBounds.y + clipBounds.height - 1);
        tymax = Math.max(tymax, this.minTileY);
        tymax = Math.min(tymax, this.maxTileY);
        if (this.backgroundColor != null) {
            g2D.setColor(this.backgroundColor);
        } else {
            g2D.setColor(this.grayColor);
        }
        int xmin = this.im.getMinX();
        int xmax = this.im.getMinX() + this.im.getWidth();
        int ymin = this.im.getMinY();
        int ymax = this.im.getMinY() + this.im.getHeight();
        int screenX = clipBounds.x + clipBounds.width;
        int screenY = clipBounds.y + clipBounds.height;
        if (xmin > clipBounds.x) {
            g2D.fillRect(clipBounds.x + transX, clipBounds.y + transY, xmin - clipBounds.x, clipBounds.height);
        }
        if (xmax < screenX) {
            g2D.fillRect(xmax + transX, clipBounds.y + transY, screenX - xmax, clipBounds.height);
        }
        if (ymin > clipBounds.y) {
            g2D.fillRect(xmin + transX, clipBounds.y + transY, xmax - xmin, ymin - clipBounds.y);
        }
        if (ymax < screenY) {
            g2D.fillRect(xmin + transX, ymax + transY, xmax - xmin, screenY - ymax);
        }
        g2D.setClip(new Rectangle(transX + this.im.getMinX(), transY + this.im.getMinY(), this.im.getWidth(), this.im.getHeight()));
        Point[] tileIndices = new Point[(txmax - txmin + 1) * (tymax - tymin + 1)];
        int index = 0;
        for (int tj = tymin; tj <= tymax; ++tj) {
            for (int ti = txmin; ti <= txmax; ++ti) {
                tileIndices[index++] = new Point(ti, tj);
            }
        }
        Raster[] tiles = PlanarImage.wrapRenderedImage(this.im).getTiles(tileIndices);
        int numTiles = tiles.length;
        for (int tileNum = 0; tileNum < numTiles; ++tileNum) {
            Raster tile = tiles[tileNum];
            int tx = tile.getMinX();
            int ty = tile.getMinY();
            if (tile == null) continue;
            WritableRaster wr = tile instanceof WritableRaster ? ((WritableRaster)tile).createWritableTranslatedChild(0, 0) : Raster.createWritableRaster(this.sampleModel, tile.getDataBuffer(), new Point(0, 0));
            BufferedImage bi = new BufferedImage(this.colorModel, wr, this.colorModel.isAlphaPremultiplied(), null);
            AffineTransform transform = AffineTransform.getTranslateInstance(tx + transX, ty + transY);
            if (this.backgroundColor != null) {
                g2D.fillRect(tx + transX, ty + transY, this.tileWidth, this.tileHeight);
            }
            g2D.drawRenderedImage(bi, transform);
        }
        g2D.setColor(saveColor);
        this.notifyPaintListeners(g2D);
    }

    public void addPaintListener(PaintListener pl) {
        this.paintListeners.add(pl);
    }

    public void removePaintListener(PaintListener pl) {
        this.paintListeners.remove(pl);
    }

    private void notifyPaintListeners(Graphics g) {
        Iterator it = this.paintListeners.iterator();
        while (it.hasNext()) {
            ((PaintListener)it.next()).paint(this, g);
        }
    }

    public static interface PaintListener {
        public void paint(ImageCanvas var1, Graphics var2);
    }
}

