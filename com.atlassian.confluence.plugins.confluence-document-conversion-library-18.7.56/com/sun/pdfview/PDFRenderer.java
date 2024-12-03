/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.BaseWatchable;
import com.sun.pdfview.ImageInfo;
import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFParseException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class PDFRenderer
extends BaseWatchable
implements Runnable {
    private PDFPage page;
    private int currentCommand;
    private WeakReference imageRef;
    private Graphics2D g;
    private GraphicsState state;
    private Stack<GraphicsState> stack;
    private Rectangle2D globalDirtyRegion;
    private List<ImageObserver> observers;
    private GeneralPath lastShape;
    private ImageInfo imageinfo;
    private long then = 0L;
    private Rectangle2D unupdatedRegion;
    public static final long UPDATE_DURATION = 200L;
    public static final float NOPHASE = -1000.0f;
    public static final float NOWIDTH = -1000.0f;
    public static final float NOLIMIT = -1000.0f;
    public static final int NOCAP = -1000;
    public static final float[] NODASH = null;
    public static final int NOJOIN = -1000;

    public PDFRenderer(PDFPage page, ImageInfo imageinfo, BufferedImage bi) {
        this.page = page;
        this.imageinfo = imageinfo;
        this.imageRef = new WeakReference<BufferedImage>(bi);
        this.observers = new ArrayList<ImageObserver>();
    }

    public PDFRenderer(PDFPage page, Graphics2D g, Rectangle imgbounds, Rectangle2D clip, Color bgColor) {
        this.page = page;
        this.g = g;
        this.imageinfo = new ImageInfo(imgbounds.width, imgbounds.height, clip, bgColor);
        g.translate(imgbounds.x, imgbounds.y);
        this.observers = new ArrayList<ImageObserver>();
    }

    private void setupRendering(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        if (this.imageinfo.bgColor != null) {
            g.setColor(this.imageinfo.bgColor);
            g.fillRect(0, 0, this.imageinfo.width, this.imageinfo.height);
        }
        g.setColor(Color.BLACK);
        AffineTransform at = this.getInitialTransform();
        g.transform(at);
        this.state = new GraphicsState();
        this.state.cliprgn = null;
        this.state.stroke = new BasicStroke();
        this.state.fillPaint = this.state.strokePaint = PDFPaint.getColorPaint(Color.black);
        this.state.fillAlpha = AlphaComposite.getInstance(2);
        this.state.strokeAlpha = AlphaComposite.getInstance(2);
        this.state.xform = g.getTransform();
        this.stack = new Stack();
        this.currentCommand = 0;
    }

    public void push() {
        this.state.cliprgn = this.g.getClip();
        this.stack.push(this.state);
        this.state = (GraphicsState)this.state.clone();
    }

    public void pop() {
        this.state = this.stack.pop();
        this.setTransform(this.state.xform);
        this.setClip(this.state.cliprgn);
    }

    public Rectangle2D stroke(GeneralPath s) {
        this.g.setComposite(this.state.strokeAlpha);
        s = new GeneralPath(this.autoAdjustStrokeWidth(this.g, this.state.stroke).createStrokedShape(s));
        return this.state.strokePaint.fill(this, this.g, s);
    }

    private BasicStroke autoAdjustStrokeWidth(Graphics2D g, BasicStroke bs) {
        AffineTransform bt = new AffineTransform(g.getTransform());
        float width = bs.getLineWidth() * (float)bt.getScaleX();
        BasicStroke stroke = bs;
        if (width < 1.0f) {
            if (bt.getScaleX() > 0.01) {
                width = 1.0f / (float)bt.getScaleX();
                stroke = new BasicStroke(width, bs.getEndCap(), bs.getLineJoin(), bs.getMiterLimit(), bs.getDashArray(), bs.getDashPhase());
            } else {
                width = 1.0f;
            }
        }
        return stroke;
    }

    public void draw(GeneralPath p, BasicStroke bs) {
        this.g.setComposite(this.state.fillAlpha);
        this.g.setPaint(this.state.fillPaint.getPaint());
        this.g.setStroke(this.autoAdjustStrokeWidth(this.g, bs));
        this.g.draw(p);
    }

    public Rectangle2D fill(GeneralPath s) {
        if (s == null) {
            Shape clip = this.g.getClip();
            if (clip == null) {
                clip = this.g.getDeviceConfiguration().getBounds();
            }
            s = new GeneralPath(clip);
        }
        this.g.setComposite(this.state.fillAlpha);
        return this.state.fillPaint.fill(this, this.g, s);
    }

    public Rectangle2D drawImage(PDFImage image) {
        AffineTransform at = new AffineTransform(1.0f / (float)image.getWidth(), 0.0f, 0.0f, -1.0f / (float)image.getHeight(), 0.0f, 1.0f);
        BufferedImage bi = image.getImage();
        if (image.isImageMask()) {
            bi = this.getMaskedImage(bi);
        }
        this.g.setComposite(AlphaComposite.getInstance(3));
        if (!this.g.drawImage(bi, at, null)) {
            System.out.println("Image not completed!");
        }
        AffineTransform bt = new AffineTransform(this.g.getTransform());
        bt.concatenate(at);
        double minx = bi.getMinX();
        double miny = bi.getMinY();
        double[] points = new double[]{minx, miny, minx + (double)bi.getWidth(), miny + (double)bi.getHeight()};
        bt.transform(points, 0, points, 0, 2);
        return new Rectangle2D.Double(points[0], points[1], points[2] - points[0], points[3] - points[1]);
    }

    public void clip(GeneralPath s) {
        this.g.clip(s);
    }

    private void setClip(Shape s) {
        this.state.cliprgn = s;
        this.g.setClip(null);
        this.g.clip(s);
    }

    public AffineTransform getTransform() {
        return this.state.xform;
    }

    public void transform(AffineTransform at) {
        this.state.xform.concatenate(at);
        this.g.setTransform(this.state.xform);
    }

    public void setTransform(AffineTransform at) {
        this.state.xform = at;
        this.g.setTransform(this.state.xform);
    }

    public AffineTransform getInitialTransform() {
        return this.page.getInitialTransform(this.imageinfo.width, this.imageinfo.height, this.imageinfo.clip);
    }

    public void setStrokeParts(float w, int cap, int join, float limit, float[] ary, float phase) {
        if (w == -1000.0f) {
            w = this.state.stroke.getLineWidth();
        }
        if (cap == -1000) {
            cap = this.state.stroke.getEndCap();
        }
        if (join == -1000) {
            join = this.state.stroke.getLineJoin();
        }
        if (limit == -1000.0f) {
            limit = this.state.stroke.getMiterLimit();
        }
        if (phase == -1000.0f) {
            ary = this.state.stroke.getDashArray();
            phase = this.state.stroke.getDashPhase();
        }
        if (ary != null && ary.length == 0) {
            ary = null;
        }
        this.state.stroke = phase == -1000.0f ? new BasicStroke(w, cap, join, limit) : new BasicStroke(w, cap, join, limit, ary, phase);
    }

    public BasicStroke getStroke() {
        return this.state.stroke;
    }

    public void setStroke(BasicStroke bs) {
        this.state.stroke = bs;
    }

    public void setStrokePaint(PDFPaint paint) {
        this.state.strokePaint = paint;
    }

    public void setFillPaint(PDFPaint paint) {
        this.state.fillPaint = paint;
    }

    public void setStrokeAlpha(float alpha) {
        this.state.strokeAlpha = AlphaComposite.getInstance(3, alpha);
    }

    public void setFillAlpha(float alpha) {
        this.state.fillAlpha = AlphaComposite.getInstance(3, alpha);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addObserver(ImageObserver observer) {
        if (observer == null) {
            return;
        }
        Image i = (Image)this.imageRef.get();
        if (this.rendererFinished()) {
            observer.imageUpdate(i, 32, 0, 0, this.imageinfo.width, this.imageinfo.height);
            return;
        }
        List<ImageObserver> list = this.observers;
        synchronized (list) {
            this.observers.add(observer);
        }
        if (this.globalDirtyRegion != null) {
            observer.imageUpdate(i, 8, (int)this.globalDirtyRegion.getMinX(), (int)this.globalDirtyRegion.getMinY(), (int)this.globalDirtyRegion.getWidth(), (int)this.globalDirtyRegion.getHeight());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeObserver(ImageObserver observer) {
        List<ImageObserver> list = this.observers;
        synchronized (list) {
            this.observers.remove(observer);
        }
    }

    public void setLastShape(GeneralPath shape) {
        this.lastShape = shape;
    }

    public GeneralPath getLastShape() {
        return this.lastShape;
    }

    @Override
    public void setup() {
        Graphics2D graphics = null;
        if (this.imageRef != null) {
            BufferedImage bi = (BufferedImage)this.imageRef.get();
            if (bi != null) {
                graphics = bi.createGraphics();
            }
        } else {
            graphics = this.g;
        }
        if (graphics != null) {
            this.setupRendering(graphics);
        }
    }

    @Override
    public int iterate() throws Exception {
        PDFCmd cmd;
        if (this.page == null) {
            return 6;
        }
        BufferedImage bi = null;
        if (this.imageRef != null) {
            bi = (BufferedImage)this.imageRef.get();
            if (bi == null) {
                System.out.println("Image went away.  Stopping");
                return 5;
            }
            this.g = bi.createGraphics();
        }
        if (this.currentCommand >= this.page.getCommandCount()) {
            if (this.page.isFinished()) {
                return 6;
            }
            return 3;
        }
        if ((cmd = this.page.getCommand(this.currentCommand++)) == null) {
            throw new PDFParseException("Command not found!");
        }
        Rectangle2D dirtyRegion = null;
        try {
            dirtyRegion = cmd.execute(this);
        }
        catch (Throwable t) {
            return 4;
        }
        this.globalDirtyRegion = this.addDirtyRegion(dirtyRegion, this.globalDirtyRegion);
        this.unupdatedRegion = this.addDirtyRegion(dirtyRegion, this.unupdatedRegion);
        long now = System.currentTimeMillis();
        if (now > this.then || this.rendererFinished()) {
            this.notifyObservers(bi, this.unupdatedRegion);
            this.unupdatedRegion = null;
            this.then = now + 200L;
        }
        if (this.imageRef != null) {
            this.g = null;
        }
        return 4;
    }

    @Override
    public void cleanup() {
        this.page = null;
        this.state = null;
        this.stack = null;
        this.globalDirtyRegion = null;
        this.lastShape = null;
        this.observers.clear();
    }

    private Rectangle2D addDirtyRegion(Rectangle2D region, Rectangle2D glob) {
        if (region == null) {
            return glob;
        }
        if (glob == null) {
            return region;
        }
        Rectangle2D.union(glob, region, glob);
        return glob;
    }

    private boolean rendererFinished() {
        if (this.page == null) {
            return true;
        }
        return this.page.isFinished() && this.currentCommand == this.page.getCommandCount();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyObservers(BufferedImage bi, Rectangle2D region) {
        int height;
        int width;
        int starty;
        int startx;
        if (bi == null) {
            return;
        }
        int flags = 0;
        if (region == null && !this.rendererFinished() || this.observers == null || this.observers.size() == 0) {
            return;
        }
        if (region != null) {
            startx = (int)Math.floor(region.getMinX());
            starty = (int)Math.floor(region.getMinY());
            width = (int)Math.ceil(region.getWidth());
            height = (int)Math.ceil(region.getHeight());
            if (width < 0) {
                startx += width;
                width = -width;
            }
            if (height < 0) {
                starty += height;
                height = -height;
            }
            flags = 0;
        } else {
            startx = 0;
            starty = 0;
            width = this.imageinfo.width;
            height = this.imageinfo.height;
        }
        if (this.rendererFinished()) {
            flags |= 0x20;
            this.g = null;
        } else {
            flags |= 8;
        }
        List<ImageObserver> list = this.observers;
        synchronized (list) {
            Iterator<ImageObserver> i = this.observers.iterator();
            while (i.hasNext()) {
                ImageObserver observer = i.next();
                boolean result = observer.imageUpdate(bi, flags, startx, starty, width, height);
                if (result) continue;
                i.remove();
            }
        }
    }

    private BufferedImage getMaskedImage(BufferedImage bi) {
        Color col = (Color)this.state.fillPaint.getPaint();
        int paintColor = col.getAlpha() << 24;
        paintColor |= col.getRed() << 16;
        paintColor |= col.getGreen() << 8;
        paintColor |= col.getBlue();
        int noColor = 0;
        int startX = bi.getMinX();
        int startY = bi.getMinY();
        int width = bi.getWidth();
        int height = bi.getHeight();
        BufferedImage dstImage = new BufferedImage(width, height, 2);
        for (int i = 0; i < height; ++i) {
            int[] srcPixels = new int[width];
            int[] dstPixels = new int[srcPixels.length];
            bi.getRGB(startX, startY + i, width, 1, srcPixels, 0, height);
            for (int j = 0; j < srcPixels.length; ++j) {
                dstPixels[j] = srcPixels[j] == -16777216 ? paintColor : noColor;
            }
            dstImage.setRGB(startX, startY + i, width, 1, dstPixels, 0, height);
        }
        return dstImage;
    }

    class GraphicsState
    implements Cloneable {
        Shape cliprgn;
        BasicStroke stroke;
        PDFPaint strokePaint;
        PDFPaint fillPaint;
        AlphaComposite strokeAlpha;
        AlphaComposite fillAlpha;
        AffineTransform xform;

        GraphicsState() {
        }

        public Object clone() {
            GraphicsState cState = new GraphicsState();
            cState.cliprgn = null;
            cState.strokePaint = this.strokePaint;
            cState.fillPaint = this.fillPaint;
            cState.strokeAlpha = this.strokeAlpha;
            cState.fillAlpha = this.fillAlpha;
            cState.stroke = new BasicStroke(this.stroke.getLineWidth(), this.stroke.getEndCap(), this.stroke.getLineJoin(), this.stroke.getMiterLimit(), this.stroke.getDashArray(), this.stroke.getDashPhase());
            cState.xform = (AffineTransform)this.xform.clone();
            return cState;
        }
    }
}

