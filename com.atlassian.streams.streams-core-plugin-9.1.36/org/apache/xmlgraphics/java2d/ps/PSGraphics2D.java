/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.ps;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlgraphics.java2d.AbstractGraphics2D;
import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.GraphicsConfigurationWithoutTransparency;
import org.apache.xmlgraphics.java2d.StrokingTextHandler;
import org.apache.xmlgraphics.java2d.TextHandler;
import org.apache.xmlgraphics.java2d.ps.PSTilingPattern;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.PSImageUtils;

public class PSGraphics2D
extends AbstractGraphics2D {
    private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
    private static final boolean DEBUG = false;
    protected List<Integer> pathHashCache = new ArrayList<Integer>();
    protected boolean startCache;
    protected PSGraphics2D rootG2D;
    protected PSGenerator gen;
    protected boolean clippingDisabled;
    protected TextHandler fallbackTextHandler = new StrokingTextHandler();
    protected TextHandler customTextHandler;
    protected Color currentColour = new Color(0, 0, 0);
    private Graphics2D fmg;

    public PSGraphics2D(boolean textAsShapes) {
        super(textAsShapes);
        BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
    }

    public PSGraphics2D(boolean textAsShapes, PSGenerator gen) {
        this(textAsShapes);
        this.setPSGenerator(gen);
    }

    public PSGraphics2D(PSGraphics2D g) {
        super(g);
        BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
        this.rootG2D = g.rootG2D != null ? g.rootG2D : g;
        this.setPSGenerator(g.gen);
        this.clippingDisabled = g.clippingDisabled;
        this.customTextHandler = g.customTextHandler;
        this.currentColour = g.currentColour;
    }

    public void setPSGenerator(PSGenerator gen) {
        this.gen = gen;
    }

    public PSGenerator getPSGenerator() {
        return this.gen;
    }

    public void setGraphicContext(GraphicContext c) {
        this.gc = c;
    }

    public TextHandler getFallbackTextHandler() {
        return this.fallbackTextHandler;
    }

    public TextHandler getCustomTextHandler() {
        return this.customTextHandler;
    }

    public void setCustomTextHandler(TextHandler handler) {
        this.customTextHandler = handler;
    }

    public void disableClipping(boolean b) {
        this.clippingDisabled = b;
    }

    @Override
    public Graphics create() {
        this.preparePainting();
        return new PSGraphics2D(this);
    }

    public void handleIOException(IOException ioe) {
        ioe.printStackTrace();
    }

    public void preparePainting() {
        if (this.rootG2D != null) {
            this.rootG2D.preparePainting();
        }
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return this.drawImage(img, x, y, observer, null);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer, Color mask) {
        return this.drawImage(img, x, y, observer, mask, null);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer, Color mask, RenderedImage imageMask) {
        this.preparePainting();
        int width = img.getWidth(observer);
        int height = img.getHeight(observer);
        if (width == -1 || height == -1) {
            return false;
        }
        Dimension size = new Dimension(width, height);
        BufferedImage buf = this.buildBufferedImage(size);
        Graphics2D g = buf.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.setBackground(new Color(1, 1, 1, 0));
        g.fillRect(0, 0, width, height);
        g.clip(new Rectangle(0, 0, buf.getWidth(), buf.getHeight()));
        if (!g.drawImage(img, 0, 0, observer)) {
            return false;
        }
        g.dispose();
        try {
            AffineTransform at = this.getTransform();
            this.gen.saveGraphicsState();
            this.gen.concatMatrix(at);
            Shape imclip = this.getClip();
            this.writeClip(imclip);
            if (imageMask == null) {
                PSImageUtils.renderBitmapImage(buf, x, y, width, height, this.gen, mask, false);
            } else {
                PSImageUtils.renderBitmapImage(imageMask, x, y, width, height, this.gen, mask, true);
            }
            this.gen.restoreGraphicsState();
        }
        catch (IOException ioe) {
            this.handleIOException(ioe);
        }
        return true;
    }

    public BufferedImage buildBufferedImage(Dimension size) {
        return new BufferedImage(size.width, size.height, 2);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        this.preparePainting();
        System.err.println("NYI: drawImage");
        return true;
    }

    @Override
    public void dispose() {
        this.gen = null;
        this.fallbackTextHandler = null;
        this.customTextHandler = null;
        this.currentColour = null;
    }

    public int processShape(Shape s, boolean cached) throws IOException {
        if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D)s;
            this.gen.defineRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            return 1;
        }
        PathIterator iter = s.getPathIterator(IDENTITY_TRANSFORM);
        if (cached) {
            this.processPathIteratorCached(s);
        } else {
            this.processPathIterator(iter);
        }
        return iter.getWindingRule();
    }

    protected String processPathIteratorToString(PathIterator iter) throws IOException {
        StringBuilder cmd = new StringBuilder();
        double[] vals = new double[6];
        while (!iter.isDone()) {
            int type = iter.currentSegment(vals);
            switch (type) {
                case 3: {
                    cmd.append(this.gen.formatDouble(vals[0])).append(" ").append(this.gen.formatDouble(vals[1])).append(" ").append(this.gen.formatDouble(vals[2])).append(" ").append(this.gen.formatDouble(vals[3])).append(" ").append(this.gen.formatDouble(vals[4])).append(" ").append(this.gen.formatDouble(vals[5])).append(" ").append(this.gen.mapCommand("curveto")).append("\n");
                    break;
                }
                case 1: {
                    cmd.append(this.gen.formatDouble(vals[0])).append(" ").append(this.gen.formatDouble(vals[1])).append(" ").append(this.gen.mapCommand("lineto")).append("\n");
                    break;
                }
                case 0: {
                    cmd.append(this.gen.formatDouble(vals[0])).append(" ").append(this.gen.formatDouble(vals[1])).append(" ").append(this.gen.mapCommand("moveto")).append("\n");
                    break;
                }
                case 2: {
                    cmd.append(this.gen.formatDouble(vals[0])).append(" ").append(this.gen.formatDouble(vals[1])).append(" ").append(this.gen.formatDouble(vals[2])).append(" ").append(this.gen.formatDouble(vals[3])).append(" QT").append("\n");
                    break;
                }
                case 4: {
                    cmd.append(this.gen.mapCommand("closepath")).append("\n");
                    break;
                }
            }
            iter.next();
        }
        return cmd.toString().trim();
    }

    protected void processPathIteratorCached(Shape s) throws IOException {
        String cmd = this.processPathIteratorToString(s.getPathIterator(IDENTITY_TRANSFORM));
        int hash = cmd.hashCode();
        if (!this.startCache) {
            if (this.pathHashCache.contains(hash)) {
                this.startCache = true;
                this.pathHashCache.clear();
            } else {
                this.gen.writeln(cmd);
                this.pathHashCache.add(hash);
            }
        }
        if (this.startCache) {
            if (!this.pathHashCache.contains(hash)) {
                this.gen.writeln("/f" + hash + "{" + cmd + "}def");
                this.pathHashCache.add(hash);
            }
            this.gen.writeln("f" + hash);
        }
    }

    public void processPathIterator(PathIterator iter) throws IOException {
        this.gen.writeln(this.processPathIteratorToString(iter));
    }

    @Override
    public void draw(Shape s) {
        this.preparePainting();
        try {
            Shape imclip;
            boolean newTransform;
            this.gen.saveGraphicsState();
            AffineTransform trans = this.getTransform();
            boolean bl = newTransform = !trans.isIdentity();
            if (newTransform) {
                this.gen.concatMatrix(trans);
            }
            if (this.shouldBeClipped(imclip = this.getClip(), s)) {
                this.writeClip(imclip);
            }
            this.establishColor(this.getColor());
            this.applyPaint(this.getPaint(), false);
            this.applyStroke(this.getStroke());
            this.gen.writeln(this.gen.mapCommand("newpath"));
            this.processShape(s, false);
            this.doDrawing(false, true, false);
            this.gen.restoreGraphicsState();
        }
        catch (IOException ioe) {
            this.handleIOException(ioe);
        }
    }

    public boolean shouldBeClipped(Shape clip, Shape s) {
        if (clip == null || s == null) {
            return false;
        }
        if (s instanceof Line2D) {
            return true;
        }
        Area as = new Area(s);
        Area imclip = new Area(clip);
        imclip.intersect(as);
        return !imclip.equals(as);
    }

    public void writeClip(Shape s) {
        if (s == null) {
            return;
        }
        if (!this.clippingDisabled) {
            this.preparePainting();
            try {
                this.gen.writeln(this.gen.mapCommand("newpath"));
                this.processShape(s, false);
                this.gen.writeln(this.gen.mapCommand("clip"));
            }
            catch (IOException ioe) {
                this.handleIOException(ioe);
            }
        }
    }

    protected void applyPaint(Paint paint, boolean fill) {
        this.preparePainting();
        if (paint instanceof GradientPaint) {
            System.err.println("NYI: Gradient paint");
        } else if (paint instanceof TexturePaint && fill) {
            try {
                PSTilingPattern psTilingPattern = new PSTilingPattern("Pattern1", (TexturePaint)paint, 0.0, 0.0, 3, null);
                this.gen.write(psTilingPattern.toString(this.gen.isAcrobatDownsample()));
                this.gen.writeln("/Pattern " + this.gen.mapCommand("setcolorspace"));
                this.gen.writeln(psTilingPattern.getName() + " " + this.gen.mapCommand("setcolor"));
            }
            catch (IOException ioe) {
                this.handleIOException(ioe);
            }
        }
    }

    protected void applyStroke(Stroke stroke) {
        this.preparePainting();
        try {
            PSGraphics2D.applyStroke(stroke, this.gen);
        }
        catch (IOException ioe) {
            this.handleIOException(ioe);
        }
    }

    public static void applyStroke(Stroke stroke, PSGenerator gen) throws IOException {
        if (stroke instanceof BasicStroke) {
            BasicStroke basicStroke = (BasicStroke)stroke;
            float[] da = basicStroke.getDashArray();
            if (da != null) {
                StringBuffer sb = new StringBuffer("[");
                for (int count = 0; count < da.length; ++count) {
                    sb.append(gen.formatDouble(da[count]));
                    if (count >= da.length - 1) continue;
                    sb.append(" ");
                }
                sb.append("] ");
                float offset = basicStroke.getDashPhase();
                sb.append(gen.formatDouble(offset));
                gen.useDash(sb.toString());
            } else {
                gen.useDash(null);
            }
            int ec = basicStroke.getEndCap();
            switch (ec) {
                case 0: {
                    gen.useLineCap(0);
                    break;
                }
                case 1: {
                    gen.useLineCap(1);
                    break;
                }
                case 2: {
                    gen.useLineCap(2);
                    break;
                }
                default: {
                    System.err.println("Unsupported line cap: " + ec);
                }
            }
            int lj = basicStroke.getLineJoin();
            switch (lj) {
                case 0: {
                    gen.useLineJoin(0);
                    float ml = basicStroke.getMiterLimit();
                    gen.useMiterLimit(ml >= -1.0f ? ml : 1.0f);
                    break;
                }
                case 1: {
                    gen.useLineJoin(1);
                    break;
                }
                case 2: {
                    gen.useLineJoin(2);
                    break;
                }
                default: {
                    System.err.println("Unsupported line join: " + lj);
                }
            }
            float lw = basicStroke.getLineWidth();
            gen.useLineWidth(lw);
        } else {
            System.err.println("Stroke not supported: " + stroke.toString());
        }
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        this.preparePainting();
        try {
            AffineTransform at = this.getTransform();
            this.gen.saveGraphicsState();
            this.gen.concatMatrix(at);
            this.gen.concatMatrix(xform);
            Shape imclip = this.getClip();
            this.writeClip(imclip);
            PSImageUtils.renderBitmapImage(img, 0.0f, 0.0f, img.getWidth(), img.getHeight(), this.gen, null, false);
            this.gen.restoreGraphicsState();
        }
        catch (IOException ioe) {
            this.handleIOException(ioe);
        }
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        this.preparePainting();
        System.err.println("NYI: drawRenderableImage");
    }

    public void establishColor(Color c) throws IOException {
        this.gen.useColor(c);
    }

    @Override
    public void drawString(String s, float x, float y) {
        try {
            if (this.customTextHandler != null && !this.textAsShapes) {
                this.customTextHandler.drawString(this, s, x, y);
            } else {
                this.fallbackTextHandler.drawString(this, s, x, y);
            }
        }
        catch (IOException ioe) {
            this.handleIOException(ioe);
        }
    }

    @Override
    public void fill(Shape s) {
        if (!this.hasAlpha()) {
            this.preparePainting();
            try {
                Shape imclip;
                boolean newTransform;
                this.gen.saveGraphicsState();
                AffineTransform trans = this.getTransform();
                boolean bl = newTransform = !trans.isIdentity();
                if (newTransform) {
                    this.gen.concatMatrix(trans);
                }
                if (this.shouldBeClipped(imclip = this.getClip(), s)) {
                    this.writeClip(imclip);
                }
                this.establishColor(this.getColor());
                this.applyPaint(this.getPaint(), true);
                this.gen.writeln(this.gen.mapCommand("newpath"));
                int windingRule = this.processShape(s, true);
                this.doDrawing(true, false, windingRule == 0);
                this.gen.restoreGraphicsState();
            }
            catch (IOException ioe) {
                this.handleIOException(ioe);
            }
        }
    }

    private boolean hasAlpha() {
        Composite composite = this.getComposite();
        return composite instanceof AlphaComposite && ((AlphaComposite)composite).getAlpha() == 0.0f;
    }

    protected void doDrawing(boolean fill, boolean stroke, boolean nonzero) throws IOException {
        this.preparePainting();
        if (fill) {
            if (stroke) {
                if (!nonzero) {
                    this.gen.writeln(this.gen.mapCommand("gsave") + " " + this.gen.mapCommand("fill") + " " + this.gen.mapCommand("grestore") + " " + this.gen.mapCommand("stroke"));
                } else {
                    this.gen.writeln(this.gen.mapCommand("gsave") + " " + this.gen.mapCommand("eofill") + " " + this.gen.mapCommand("grestore") + " " + this.gen.mapCommand("stroke"));
                }
            } else if (!nonzero) {
                this.gen.writeln(this.gen.mapCommand("fill"));
            } else {
                this.gen.writeln(this.gen.mapCommand("eofill"));
            }
        } else {
            this.gen.writeln(this.gen.mapCommand("stroke"));
        }
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return new GraphicsConfigurationWithoutTransparency();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return this.fmg.getFontMetrics(f);
    }

    @Override
    public void setXORMode(Color c1) {
        System.err.println("NYI: setXORMode");
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        System.err.println("NYI: copyArea");
    }
}

