/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.internal.PolylineShape;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class PdfGraphics2D
extends Graphics2D {
    private static final int FILL = 1;
    private static final int STROKE = 2;
    private static final int CLIP = 3;
    private BasicStroke strokeOne = new BasicStroke(1.0f);
    private static final AffineTransform IDENTITY = new AffineTransform();
    private Font font;
    private BaseFont baseFont;
    private float fontSize;
    private AffineTransform transform;
    private Paint paint;
    private Color background;
    private float width;
    private float height;
    private Area clip;
    private RenderingHints rhints = new RenderingHints(null);
    private Stroke stroke;
    private Stroke originalStroke;
    private PdfContentByte cb;
    private Map<String, BaseFont> baseFonts;
    private boolean disposeCalled = false;
    private FontMapper fontMapper;
    private List<Object> kids;
    private boolean kid = false;
    private Graphics2D dg2 = new BufferedImage(2, 2, 1).createGraphics();
    private boolean onlyShapes = false;
    private Stroke oldStroke;
    private Paint paintFill;
    private Paint paintStroke;
    private MediaTracker mediaTracker;
    protected boolean underline;
    protected PdfGState[] fillGState = new PdfGState[256];
    protected PdfGState[] strokeGState = new PdfGState[256];
    protected int currentFillGState = 255;
    protected int currentStrokeGState = 255;
    public static final int AFM_DIVISOR = 1000;
    private boolean convertImagesToJPEG = false;
    private float jpegQuality = 0.95f;
    private float alpha;
    private Composite composite;
    private Paint realPaint;
    private final CompositeFontDrawer compositeFontDrawer = new CompositeFontDrawer();

    private PdfGraphics2D() {
        this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
    }

    public PdfGraphics2D(PdfContentByte cb, float width, float height) {
        this(cb, width, height, null, false, false, 0.0f);
    }

    public PdfGraphics2D(PdfContentByte cb, float width, float height, FontMapper fontMapper, boolean onlyShapes, boolean convertImagesToJPEG, float quality) {
        this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        this.convertImagesToJPEG = convertImagesToJPEG;
        this.jpegQuality = quality;
        this.onlyShapes = onlyShapes;
        this.transform = new AffineTransform();
        this.baseFonts = new HashMap<String, BaseFont>();
        if (!onlyShapes) {
            this.fontMapper = fontMapper;
            if (this.fontMapper == null) {
                this.fontMapper = new DefaultFontMapper();
            }
        }
        this.paint = Color.black;
        this.background = Color.white;
        this.setFont(new Font("sanserif", 0, 12));
        this.cb = cb;
        cb.saveState();
        this.width = width;
        this.height = height;
        this.clip = new Area(new Rectangle2D.Float(0.0f, 0.0f, width, height));
        this.clip(this.clip);
        this.stroke = this.oldStroke = this.strokeOne;
        this.originalStroke = this.oldStroke;
        this.setStrokeDiff(this.stroke, null);
        cb.saveState();
    }

    @Override
    public void draw(Shape s) {
        this.followPath(s, 2);
    }

    @Override
    public boolean drawImage(java.awt.Image img, AffineTransform xform, ImageObserver obs) {
        return this.drawImage(img, null, xform, null, obs);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        BufferedImage result = img;
        if (op != null) {
            result = op.createCompatibleDestImage(img, img.getColorModel());
            result = op.filter(img, result);
        }
        this.drawImage((java.awt.Image)result, x, y, null);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        BufferedImage image = null;
        if (img instanceof BufferedImage) {
            image = (BufferedImage)img;
        } else {
            ColorModel cm = img.getColorModel();
            int width = img.getWidth();
            int height = img.getHeight();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            Hashtable<String, Object> properties = new Hashtable<String, Object>();
            String[] keys = img.getPropertyNames();
            if (keys != null) {
                for (String key : keys) {
                    properties.put(key, img.getProperty(key));
                }
            }
            BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
            img.copyData(raster);
            image = result;
        }
        this.drawImage(image, xform, null);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        this.drawRenderedImage(img.createDefaultRendering(), xform);
    }

    @Override
    public void drawString(String s, int x, int y) {
        this.drawString(s, (float)x, (float)y);
    }

    public static double asPoints(double d, int i) {
        return d * (double)i / 1000.0;
    }

    protected void doAttributes(AttributedCharacterIterator iter) {
        this.underline = false;
        Set<AttributedCharacterIterator.Attribute> set = iter.getAttributes().keySet();
        for (AttributedCharacterIterator.Attribute attribute : set) {
            Font font;
            if (!(attribute instanceof TextAttribute)) continue;
            TextAttribute textattribute = (TextAttribute)attribute;
            if (textattribute.equals(TextAttribute.FONT)) {
                font = (Font)iter.getAttributes().get(textattribute);
                this.setFont(font);
                continue;
            }
            if (textattribute.equals(TextAttribute.UNDERLINE)) {
                if (iter.getAttributes().get(textattribute) != TextAttribute.UNDERLINE_ON) continue;
                this.underline = true;
                continue;
            }
            if (textattribute.equals(TextAttribute.SIZE)) {
                Object obj = iter.getAttributes().get(textattribute);
                if (obj instanceof Integer) {
                    int i = (Integer)obj;
                    this.setFont(this.getFont().deriveFont(this.getFont().getStyle(), i));
                    continue;
                }
                if (!(obj instanceof Float)) continue;
                float f = ((Float)obj).floatValue();
                this.setFont(this.getFont().deriveFont(this.getFont().getStyle(), f));
                continue;
            }
            if (textattribute.equals(TextAttribute.FOREGROUND)) {
                this.setColor((Color)iter.getAttributes().get(textattribute));
                continue;
            }
            if (textattribute.equals(TextAttribute.FAMILY)) {
                font = this.getFont();
                Map<TextAttribute, ?> fontAttributes = font.getAttributes();
                fontAttributes.put(TextAttribute.FAMILY, iter.getAttributes().get(textattribute));
                this.setFont(font.deriveFont(fontAttributes));
                continue;
            }
            if (textattribute.equals(TextAttribute.POSTURE)) {
                font = this.getFont();
                Map<TextAttribute, ?> fontAttributes = font.getAttributes();
                fontAttributes.put(TextAttribute.POSTURE, iter.getAttributes().get(textattribute));
                this.setFont(font.deriveFont(fontAttributes));
                continue;
            }
            if (!textattribute.equals(TextAttribute.WEIGHT)) continue;
            font = this.getFont();
            Map<TextAttribute, ?> fontAttributes = font.getAttributes();
            fontAttributes.put(TextAttribute.WEIGHT, iter.getAttributes().get(textattribute));
            this.setFont(font.deriveFont(fontAttributes));
        }
    }

    @Override
    public void drawString(String s, float x, float y) {
        if (s.length() == 0) {
            return;
        }
        this.setFillPaint();
        if (this.onlyShapes) {
            this.drawGlyphVector(this.font.layoutGlyphVector(this.getFontRenderContext(), s.toCharArray(), 0, s.length(), 0), x, y);
        } else {
            if (!Float.isFinite(this.fontSize) || this.fontSize < 1.0E-4f) {
                return;
            }
            double width = 0.0;
            if (CompositeFontDrawer.isSupported() && this.compositeFontDrawer.isCompositeFont(this.font)) {
                width = this.compositeFontDrawer.drawString(s, this.font, x, y, this::getCachedBaseFont, this::drawString);
            } else {
                List<String> substrings = this.splitIntoSubstringsByVisibility(s);
                for (String str : substrings) {
                    width += this.drawString(str, this.baseFont, (double)x + width, y);
                }
            }
            if (this.underline) {
                int UnderlineThickness = 50;
                double d = PdfGraphics2D.asPoints(UnderlineThickness, (int)this.fontSize);
                Stroke savedStroke = this.originalStroke;
                this.setStroke(new BasicStroke((float)d));
                y = (float)((double)y + PdfGraphics2D.asPoints(UnderlineThickness, (int)this.fontSize));
                Line2D.Double line = new Line2D.Double(x, y, width + (double)x, y);
                this.draw(line);
                this.setStroke(savedStroke);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private double drawString(String s, BaseFont baseFont, double x, double y) {
        boolean restoreTextRenderingMode = false;
        AffineTransform at = this.getTransform();
        try {
            Object url;
            AffineTransform at2 = this.getTransform();
            at2.translate(x, y);
            at2.concatenate(this.font.getTransform());
            this.setTransform(at2);
            AffineTransform inverse = this.normalizeMatrix();
            AffineTransform flipper = AffineTransform.getScaleInstance(1.0, -1.0);
            inverse.concatenate(flipper);
            double[] mx = new double[6];
            inverse.getMatrix(mx);
            this.cb.beginText();
            this.cb.setFontAndSize(baseFont, this.fontSize);
            if (this.font.isItalic()) {
                float angle = baseFont.getFontDescriptor(4, 1000.0f);
                float angle2 = this.font.getItalicAngle();
                if (Objects.equals(this.font.getFontName(), this.font.getName()) || angle == 0.0f && angle2 == 0.0f) {
                    angle2 = angle2 == 0.0f ? 15.0f : -angle2;
                    if (angle == 0.0f) {
                        mx[2] = angle2 / 100.0f;
                    }
                }
            }
            this.cb.setTextMatrix((float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
            Float fontTextAttributeWidth = (Float)this.font.getAttributes().get(TextAttribute.WIDTH);
            Float f = fontTextAttributeWidth = fontTextAttributeWidth == null ? TextAttribute.WIDTH_REGULAR : fontTextAttributeWidth;
            if (!TextAttribute.WIDTH_REGULAR.equals(fontTextAttributeWidth)) {
                this.cb.setHorizontalScaling(100.0f / fontTextAttributeWidth.floatValue());
            }
            if (!baseFont.getPostscriptFontName().toLowerCase(Locale.ROOT).contains("bold")) {
                float strokeWidth;
                Float weight = (Float)this.font.getAttributes().get(TextAttribute.WEIGHT);
                if (weight == null) {
                    Float f2 = weight = this.font.isBold() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR;
                }
                if ((this.font.isBold() || weight.floatValue() >= TextAttribute.WEIGHT_SEMIBOLD.floatValue()) && this.font.getFontName().equals(this.font.getName()) && (strokeWidth = this.font.getSize2D() * (weight.floatValue() - TextAttribute.WEIGHT_REGULAR.floatValue()) / 30.0f) != 1.0f && this.realPaint instanceof Color) {
                    this.cb.setTextRenderingMode(2);
                    this.cb.setLineWidth(strokeWidth);
                    Color color = (Color)this.realPaint;
                    int alpha = color.getAlpha();
                    if (alpha != this.currentStrokeGState) {
                        this.currentStrokeGState = alpha;
                        PdfGState gs = this.strokeGState[alpha];
                        if (gs == null) {
                            gs = new PdfGState();
                            gs.setStrokeOpacity((float)alpha / 255.0f);
                            this.strokeGState[alpha] = gs;
                        }
                        this.cb.setGState(gs);
                    }
                    this.setStrokePaint();
                    restoreTextRenderingMode = true;
                }
            }
            double width = 0.0;
            if (this.font.getSize2D() > 0.0f) {
                float scale = 1000.0f / this.font.getSize2D();
                Font derivedFont = this.font.deriveFont(AffineTransform.getScaleInstance(scale, scale));
                width = derivedFont.getStringBounds(s, this.getFontRenderContext()).getWidth();
                if (derivedFont.isTransformed()) {
                    width /= (double)scale;
                }
            }
            if ((url = this.getRenderingHint(HyperLinkKey.KEY_INSTANCE)) != null && !url.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)) {
                float scale = 1000.0f / this.font.getSize2D();
                Font derivedFont = this.font.deriveFont(AffineTransform.getScaleInstance(scale, scale));
                double height = derivedFont.getStringBounds(s, this.getFontRenderContext()).getHeight();
                if (derivedFont.isTransformed()) {
                    height /= (double)scale;
                }
                double leftX = this.cb.getXTLM();
                double leftY = this.cb.getYTLM();
                PdfAction action = new PdfAction(url.toString());
                this.cb.setAction(action, (float)leftX, (float)leftY, (float)(leftX + width), (float)(leftY + height));
            }
            if (s.length() > 1) {
                float adv = ((float)width - baseFont.getWidthPoint(s, this.fontSize)) / (float)(s.length() - 1);
                this.cb.setCharacterSpacing(adv);
            }
            this.cb.showText(s);
            if (s.length() > 1) {
                this.cb.setCharacterSpacing(0.0f);
            }
            if (!TextAttribute.WIDTH_REGULAR.equals(fontTextAttributeWidth)) {
                this.cb.setHorizontalScaling(100.0f);
            }
            if (restoreTextRenderingMode) {
                this.cb.setTextRenderingMode(0);
            }
            this.cb.endText();
            double d = width;
            return d;
        }
        finally {
            this.setTransform(at);
        }
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        this.drawString(iterator, (float)x, (float)y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iter, float x, float y) {
        StringBuilder stringbuffer = new StringBuilder(iter.getEndIndex());
        char c = iter.first();
        while (c != '\uffff') {
            if (iter.getIndex() == iter.getRunStart()) {
                if (stringbuffer.length() > 0) {
                    this.drawString(stringbuffer.toString(), x, y);
                    FontMetrics fontmetrics = this.getFontMetrics();
                    x = (float)((double)x + fontmetrics.getStringBounds(stringbuffer.toString(), this).getWidth());
                    stringbuffer.delete(0, stringbuffer.length());
                }
                this.doAttributes(iter);
            }
            stringbuffer.append(c);
            c = iter.next();
        }
        this.drawString(stringbuffer.toString(), x, y);
        this.underline = false;
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape s = g.getOutline(x, y);
        this.fill(s);
    }

    @Override
    public void fill(Shape s) {
        this.followPath(s, 1);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        if (onStroke) {
            s = this.stroke.createStrokedShape(s);
        }
        s = this.transform.createTransformedShape(s);
        Area area = new Area(s);
        if (this.clip != null) {
            area.intersect(this.clip);
        }
        return area.intersects(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return this.dg2.getDeviceConfiguration();
    }

    @Override
    public void setComposite(Composite comp) {
        AlphaComposite composite;
        if (comp instanceof AlphaComposite && (composite = (AlphaComposite)comp).getRule() == 3) {
            this.alpha = composite.getAlpha();
            this.composite = composite;
            if (this.realPaint != null && this.realPaint instanceof Color) {
                Color c = (Color)this.realPaint;
                this.paint = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)((float)c.getAlpha() * this.alpha));
            }
            return;
        }
        this.composite = comp;
        this.alpha = 1.0f;
    }

    @Override
    public void setPaint(Paint paint) {
        AlphaComposite co;
        if (paint == null) {
            return;
        }
        this.paint = paint;
        this.realPaint = paint;
        if (this.composite instanceof AlphaComposite && paint instanceof Color && (co = (AlphaComposite)this.composite).getRule() == 3) {
            Color c = (Color)paint;
            this.paint = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)((float)c.getAlpha() * this.alpha));
            this.realPaint = paint;
        }
    }

    private Stroke transformStroke(Stroke stroke) {
        if (!(stroke instanceof BasicStroke)) {
            return stroke;
        }
        BasicStroke st = (BasicStroke)stroke;
        float scale = (float)Math.sqrt(Math.abs(this.transform.getDeterminant()));
        float[] dash = st.getDashArray();
        if (dash != null) {
            int k = 0;
            while (k < dash.length) {
                int n = k++;
                dash[n] = dash[n] * scale;
            }
        }
        return new BasicStroke(st.getLineWidth() * scale, st.getEndCap(), st.getLineJoin(), st.getMiterLimit(), dash, st.getDashPhase() * scale);
    }

    private void setStrokeDiff(Stroke newStroke, Stroke oldStroke) {
        if (newStroke == oldStroke) {
            return;
        }
        if (!(newStroke instanceof BasicStroke)) {
            return;
        }
        BasicStroke nStroke = (BasicStroke)newStroke;
        boolean oldOk = oldStroke instanceof BasicStroke;
        BasicStroke oStroke = null;
        if (oldOk) {
            oStroke = (BasicStroke)oldStroke;
        }
        if (!oldOk || nStroke.getLineWidth() != oStroke.getLineWidth()) {
            this.cb.setLineWidth(nStroke.getLineWidth());
        }
        if (!oldOk || nStroke.getEndCap() != oStroke.getEndCap()) {
            switch (nStroke.getEndCap()) {
                case 0: {
                    this.cb.setLineCap(0);
                    break;
                }
                case 2: {
                    this.cb.setLineCap(2);
                    break;
                }
                default: {
                    this.cb.setLineCap(1);
                }
            }
        }
        if (!oldOk || nStroke.getLineJoin() != oStroke.getLineJoin()) {
            switch (nStroke.getLineJoin()) {
                case 0: {
                    this.cb.setLineJoin(0);
                    break;
                }
                case 2: {
                    this.cb.setLineJoin(2);
                    break;
                }
                default: {
                    this.cb.setLineJoin(1);
                }
            }
        }
        if (!oldOk || nStroke.getMiterLimit() != oStroke.getMiterLimit()) {
            this.cb.setMiterLimit(nStroke.getMiterLimit());
        }
        boolean makeDash = oldOk ? (nStroke.getDashArray() != null ? (nStroke.getDashPhase() != oStroke.getDashPhase() ? true : !Arrays.equals(nStroke.getDashArray(), oStroke.getDashArray())) : oStroke.getDashArray() != null) : true;
        if (makeDash) {
            float[] dash = nStroke.getDashArray();
            if (dash == null) {
                this.cb.setLiteral("[]0 d\n");
            } else {
                this.cb.setLiteral('[');
                int lim = dash.length;
                for (float dash1 : dash) {
                    this.cb.setLiteral(dash1);
                    this.cb.setLiteral(' ');
                }
                this.cb.setLiteral(']');
                this.cb.setLiteral(nStroke.getDashPhase());
                this.cb.setLiteral(" d\n");
            }
        }
    }

    @Override
    public void setStroke(Stroke s) {
        this.originalStroke = s;
        this.stroke = this.transformStroke(s);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key arg0, Object arg1) {
        if (arg1 != null) {
            this.rhints.put(arg0, arg1);
        } else if (arg0 instanceof HyperLinkKey) {
            this.rhints.put(arg0, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        } else {
            this.rhints.remove(arg0);
        }
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key arg0) {
        return this.rhints.get(arg0);
    }

    public void setRenderingHints(Map hints) {
        this.rhints.clear();
        this.rhints.putAll((Map<?, ?>)hints);
    }

    public void addRenderingHints(Map hints) {
        this.rhints.putAll((Map<?, ?>)hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return this.rhints;
    }

    @Override
    public void translate(int x, int y) {
        this.translate((double)x, (double)y);
    }

    @Override
    public void translate(double tx, double ty) {
        this.transform.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        this.transform.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        this.transform.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        this.transform.scale(sx, sy);
        this.stroke = this.transformStroke(this.originalStroke);
    }

    @Override
    public void shear(double shx, double shy) {
        this.transform.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform tx) {
        this.transform.concatenate(tx);
        this.stroke = this.transformStroke(this.originalStroke);
    }

    @Override
    public void setTransform(AffineTransform t) {
        this.transform = new AffineTransform(t);
        this.stroke = this.transformStroke(this.originalStroke);
    }

    @Override
    public AffineTransform getTransform() {
        return new AffineTransform(this.transform);
    }

    @Override
    public Paint getPaint() {
        if (this.realPaint != null) {
            return this.realPaint;
        }
        return this.paint;
    }

    @Override
    public Composite getComposite() {
        return this.composite;
    }

    @Override
    public void setBackground(Color color) {
        this.background = color;
    }

    @Override
    public Color getBackground() {
        return this.background;
    }

    @Override
    public Stroke getStroke() {
        return this.originalStroke;
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        boolean antialias = RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals(this.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        boolean fractions = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(this.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
        return new FontRenderContext(new AffineTransform(), antialias, fractions);
    }

    @Override
    public Graphics create() {
        PdfGraphics2D g2 = new PdfGraphics2D();
        g2.rhints.putAll((Map<?, ?>)this.rhints);
        g2.onlyShapes = this.onlyShapes;
        g2.transform = new AffineTransform(this.transform);
        g2.baseFonts = this.baseFonts;
        g2.fontMapper = this.fontMapper;
        g2.paint = this.paint;
        g2.fillGState = this.fillGState;
        g2.currentFillGState = this.currentFillGState;
        g2.currentStrokeGState = this.currentStrokeGState;
        g2.strokeGState = this.strokeGState;
        g2.background = this.background;
        g2.mediaTracker = this.mediaTracker;
        g2.convertImagesToJPEG = this.convertImagesToJPEG;
        g2.jpegQuality = this.jpegQuality;
        g2.setFont(this.font);
        g2.cb = this.cb.getDuplicate();
        g2.cb.saveState();
        g2.width = this.width;
        g2.height = this.height;
        g2.followPath(new Area(new Rectangle2D.Float(0.0f, 0.0f, this.width, this.height)), 3);
        if (this.clip != null) {
            g2.clip = new Area(this.clip);
        }
        g2.composite = this.composite;
        g2.stroke = this.stroke;
        g2.originalStroke = this.originalStroke;
        g2.strokeOne = (BasicStroke)g2.transformStroke(g2.strokeOne);
        g2.oldStroke = g2.strokeOne;
        g2.setStrokeDiff(g2.oldStroke, null);
        g2.cb.saveState();
        if (g2.clip != null) {
            g2.followPath(g2.clip, 3);
        }
        g2.kid = true;
        if (this.kids == null) {
            this.kids = new ArrayList<Object>();
        }
        this.kids.add(this.cb.getInternalBuffer().size());
        this.kids.add(g2);
        return g2;
    }

    public PdfContentByte getContent() {
        return this.cb;
    }

    @Override
    public Color getColor() {
        if (this.paint instanceof Color) {
            return (Color)this.paint;
        }
        return Color.black;
    }

    @Override
    public void setColor(Color color) {
        this.setPaint(color);
    }

    @Override
    public void setPaintMode() {
    }

    @Override
    public void setXORMode(Color c1) {
    }

    @Override
    public Font getFont() {
        return this.font;
    }

    @Override
    public void setFont(Font f) {
        if (f == null) {
            return;
        }
        if (this.onlyShapes) {
            this.font = f;
            return;
        }
        if (f == this.font) {
            return;
        }
        this.font = f;
        this.fontSize = f.getSize2D();
        this.baseFont = this.getCachedBaseFont(f);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BaseFont getCachedBaseFont(Font f) {
        Map<String, BaseFont> map = this.baseFonts;
        synchronized (map) {
            BaseFont bf = this.baseFonts.get(f.getFontName());
            if (bf == null) {
                bf = this.fontMapper.awtToPdf(f);
                this.baseFonts.put(f.getFontName(), bf);
            }
            return bf;
        }
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return this.dg2.getFontMetrics(f);
    }

    @Override
    public Rectangle getClipBounds() {
        if (this.clip == null) {
            return null;
        }
        return this.getClip().getBounds();
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        Rectangle2D.Double rect = new Rectangle2D.Double(x, y, width, height);
        this.clip(rect);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        Rectangle2D.Double rect = new Rectangle2D.Double(x, y, width, height);
        this.setClip(rect);
    }

    @Override
    public void clip(Shape s) {
        if (s == null) {
            this.setClip(null);
            return;
        }
        s = this.transform.createTransformedShape(s);
        if (this.clip == null) {
            this.clip = new Area(s);
        } else {
            this.clip.intersect(new Area(s));
        }
        this.followPath(s, 3);
    }

    @Override
    public Shape getClip() {
        try {
            return this.transform.createInverse().createTransformedShape(this.clip);
        }
        catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    @Override
    public void setClip(Shape s) {
        this.cb.restoreState();
        this.cb.saveState();
        if (s != null) {
            s = this.transform.createTransformedShape(s);
        }
        if (s == null) {
            this.clip = null;
        } else {
            this.clip = new Area(s);
            this.followPath(s, 3);
        }
        this.paintStroke = null;
        this.paintFill = null;
        this.currentStrokeGState = -1;
        this.currentFillGState = -1;
        this.oldStroke = this.strokeOne;
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
        this.draw(line);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        this.draw(new Rectangle(x, y, width, height));
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        this.fill(new Rectangle(x, y, width, height));
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        Paint temp = this.paint;
        this.setPaint(this.background);
        this.fillRect(x, y, width, height);
        this.setPaint(temp);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight);
        this.draw(rect);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight);
        this.fill(rect);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        Ellipse2D.Float oval = new Ellipse2D.Float(x, y, width, height);
        this.draw(oval);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        Ellipse2D.Float oval = new Ellipse2D.Float(x, y, width, height);
        this.fill(oval);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        Arc2D.Double arc = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 0);
        this.draw(arc);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        Arc2D.Double arc = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 2);
        this.fill(arc);
    }

    @Override
    public void drawPolyline(int[] x, int[] y, int nPoints) {
        PolylineShape polyline = new PolylineShape(x, y, nPoints);
        this.draw(polyline);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon poly = new Polygon(xPoints, yPoints, nPoints);
        this.draw(poly);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon poly = new Polygon();
        for (int i = 0; i < nPoints; ++i) {
            poly.addPoint(xPoints[i], yPoints[i]);
        }
        this.fill(poly);
    }

    @Override
    public boolean drawImage(java.awt.Image img, int x, int y, ImageObserver observer) {
        return this.drawImage(img, x, y, null, observer);
    }

    @Override
    public boolean drawImage(java.awt.Image img, int x, int y, int width, int height, ImageObserver observer) {
        return this.drawImage(img, x, y, width, height, null, observer);
    }

    @Override
    public boolean drawImage(java.awt.Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        this.waitForImage(img);
        return this.drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), bgcolor, observer);
    }

    @Override
    public boolean drawImage(java.awt.Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        this.waitForImage(img);
        double scalex = (double)width / (double)img.getWidth(observer);
        double scaley = (double)height / (double)img.getHeight(observer);
        AffineTransform tx = AffineTransform.getTranslateInstance(x, y);
        tx.scale(scalex, scaley);
        return this.drawImage(img, null, tx, bgcolor, observer);
    }

    @Override
    public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return this.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
    }

    @Override
    public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        this.waitForImage(img);
        double dwidth = (double)dx2 - (double)dx1;
        double dheight = (double)dy2 - (double)dy1;
        double swidth = (double)sx2 - (double)sx1;
        double sheight = (double)sy2 - (double)sy1;
        if (dwidth == 0.0 || dheight == 0.0 || swidth == 0.0 || sheight == 0.0) {
            return true;
        }
        double scalex = dwidth / swidth;
        double scaley = dheight / sheight;
        double transx = (double)sx1 * scalex;
        double transy = (double)sy1 * scaley;
        AffineTransform tx = AffineTransform.getTranslateInstance((double)dx1 - transx, (double)dy1 - transy);
        tx.scale(scalex, scaley);
        BufferedImage mask = new BufferedImage(img.getWidth(observer), img.getHeight(observer), 12);
        Graphics g = mask.getGraphics();
        g.fillRect(sx1, sy1, (int)swidth, (int)sheight);
        this.drawImage(img, mask, tx, null, observer);
        g.dispose();
        return true;
    }

    @Override
    public void dispose() {
        if (this.kid) {
            return;
        }
        if (!this.disposeCalled) {
            this.disposeCalled = true;
            this.cb.restoreState();
            this.cb.restoreState();
            this.dg2.dispose();
            this.dg2 = null;
            if (this.kids != null) {
                ByteBuffer buf = new ByteBuffer();
                this.internalDispose(buf);
                ByteBuffer buf2 = this.cb.getInternalBuffer();
                buf2.reset();
                buf2.append(buf);
            }
        }
    }

    private void internalDispose(ByteBuffer buf) {
        int last = 0;
        int pos = 0;
        ByteBuffer buf2 = this.cb.getInternalBuffer();
        if (this.kids != null) {
            for (int k = 0; k < this.kids.size(); k += 2) {
                pos = (Integer)this.kids.get(k);
                PdfGraphics2D g2 = (PdfGraphics2D)this.kids.get(k + 1);
                g2.cb.restoreState();
                g2.cb.restoreState();
                buf.append(buf2.getBuffer(), last, pos - last);
                g2.dg2.dispose();
                g2.dg2 = null;
                g2.internalDispose(buf);
                last = pos;
            }
        }
        buf.append(buf2.getBuffer(), last, buf2.size() - last);
    }

    private void followPath(Shape s, int drawType) {
        if (s == null) {
            return;
        }
        if (drawType == 2 && !(this.stroke instanceof BasicStroke)) {
            s = this.stroke.createStrokedShape(s);
            this.followPath(s, 1);
            return;
        }
        if (drawType == 2) {
            this.setStrokeDiff(this.stroke, this.oldStroke);
            this.oldStroke = this.stroke;
            this.setStrokePaint();
        } else if (drawType == 1) {
            this.setFillPaint();
        }
        int traces = 0;
        PathIterator points = drawType == 3 ? s.getPathIterator(IDENTITY) : s.getPathIterator(this.transform);
        float[] coords = new float[6];
        while (!points.isDone()) {
            ++traces;
            int segtype = points.currentSegment(coords);
            this.normalizeY(coords);
            switch (segtype) {
                case 4: {
                    this.cb.closePath();
                    break;
                }
                case 3: {
                    this.cb.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                }
                case 1: {
                    this.cb.lineTo(coords[0], coords[1]);
                    break;
                }
                case 0: {
                    this.cb.moveTo(coords[0], coords[1]);
                    break;
                }
                case 2: {
                    this.cb.curveTo(coords[0], coords[1], coords[2], coords[3]);
                }
            }
            points.next();
        }
        switch (drawType) {
            case 1: {
                if (traces <= 0) break;
                if (points.getWindingRule() == 0) {
                    this.cb.eoFill();
                    break;
                }
                this.cb.fill();
                break;
            }
            case 2: {
                if (traces <= 0) break;
                this.cb.stroke();
                break;
            }
            default: {
                if (traces == 0) {
                    this.cb.rectangle(0.0f, 0.0f, 0.0f, 0.0f);
                }
                if (points.getWindingRule() == 0) {
                    this.cb.eoClip();
                } else {
                    this.cb.clip();
                }
                this.cb.newPath();
            }
        }
    }

    private float normalizeY(float y) {
        return this.height - y;
    }

    private void normalizeY(float[] coords) {
        coords[1] = this.normalizeY(coords[1]);
        coords[3] = this.normalizeY(coords[3]);
        coords[5] = this.normalizeY(coords[5]);
    }

    private AffineTransform normalizeMatrix() {
        double[] mx = new double[6];
        AffineTransform result = AffineTransform.getTranslateInstance(0.0, 0.0);
        result.getMatrix(mx);
        mx[3] = -1.0;
        mx[5] = this.height;
        result = new AffineTransform(mx);
        result.concatenate(this.transform);
        return result;
    }

    private boolean drawImage(java.awt.Image img, java.awt.Image mask, AffineTransform xform, Color bgColor, ImageObserver obs) {
        PdfGState gs;
        xform = xform == null ? new AffineTransform() : new AffineTransform(xform);
        xform.translate(0.0, img.getHeight(obs));
        xform.scale(img.getWidth(obs), img.getHeight(obs));
        AffineTransform inverse = this.normalizeMatrix();
        AffineTransform flipper = AffineTransform.getScaleInstance(1.0, -1.0);
        inverse.concatenate(xform);
        inverse.concatenate(flipper);
        double[] mx = new double[6];
        inverse.getMatrix(mx);
        if (this.currentFillGState != 255) {
            gs = this.fillGState[255];
            if (gs == null) {
                gs = new PdfGState();
                gs.setFillOpacity(1.0f);
                this.fillGState[255] = gs;
            }
            this.cb.setGState(gs);
        }
        try {
            Image image = null;
            if (!this.convertImagesToJPEG) {
                image = Image.getInstance(img, bgColor);
            } else {
                BufferedImage scaled = new BufferedImage(img.getWidth(null), img.getHeight(null), 1);
                Graphics2D g3 = scaled.createGraphics();
                g3.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                g3.dispose();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JPEGImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
                iwparam.setCompressionMode(2);
                iwparam.setCompressionQuality(this.jpegQuality);
                ImageWriter iw = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                iw.setOutput(ios);
                iw.write(null, new IIOImage(scaled, null, null), iwparam);
                iw.dispose();
                ios.close();
                scaled.flush();
                scaled = null;
                image = Image.getInstance(baos.toByteArray());
            }
            if (mask != null) {
                Image msk = Image.getInstance(mask, null, true);
                msk.makeMask();
                msk.setInverted(true);
                image.setImageMask(msk);
            }
            this.cb.addImage(image, (float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
            Object url = this.getRenderingHint(HyperLinkKey.KEY_INSTANCE);
            if (url != null && !url.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)) {
                PdfAction action = new PdfAction(url.toString());
                this.cb.setAction(action, (float)mx[4], (float)mx[5], (float)(mx[0] + mx[4]), (float)(mx[3] + mx[5]));
            }
        }
        catch (Exception ex) {
            throw new IllegalArgumentException();
        }
        if (this.currentFillGState != 255 && this.currentFillGState != -1) {
            gs = this.fillGState[this.currentFillGState];
            this.cb.setGState(gs);
        }
        return true;
    }

    private boolean checkNewPaint(Paint oldPaint) {
        if (this.paint == oldPaint) {
            return false;
        }
        return !(this.paint instanceof Color) || !this.paint.equals(oldPaint);
    }

    private void setFillPaint() {
        if (this.checkNewPaint(this.paintFill)) {
            this.paintFill = this.paint;
            this.setPaint(false, 0.0, 0.0, true);
        }
    }

    private void setStrokePaint() {
        if (this.checkNewPaint(this.paintStroke)) {
            this.paintStroke = this.paint;
            this.setPaint(false, 0.0, 0.0, false);
        }
    }

    private void setPaint(boolean invert, double xoffset, double yoffset, boolean fill) {
        block27: {
            if (this.paint instanceof Color) {
                Color color = (Color)this.paint;
                int alpha = color.getAlpha();
                if (fill) {
                    if (alpha != this.currentFillGState) {
                        this.currentFillGState = alpha;
                        PdfGState gs = this.fillGState[alpha];
                        if (gs == null) {
                            gs = new PdfGState();
                            gs.setFillOpacity((float)alpha / 255.0f);
                            this.fillGState[alpha] = gs;
                        }
                        this.cb.setGState(gs);
                    }
                    this.cb.setColorFill(color);
                } else {
                    if (alpha != this.currentStrokeGState) {
                        this.currentStrokeGState = alpha;
                        PdfGState gs = this.strokeGState[alpha];
                        if (gs == null) {
                            gs = new PdfGState();
                            gs.setStrokeOpacity((float)alpha / 255.0f);
                            this.strokeGState[alpha] = gs;
                        }
                        this.cb.setGState(gs);
                    }
                    this.cb.setColorStroke(color);
                }
            } else if (this.paint instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint)this.paint;
                Point2D p1 = gp.getPoint1();
                this.transform.transform(p1, p1);
                Point2D p2 = gp.getPoint2();
                this.transform.transform(p2, p2);
                Color c1 = gp.getColor1();
                Color c2 = gp.getColor2();
                PdfShading shading = PdfShading.simpleAxial(this.cb.getPdfWriter(), (float)p1.getX(), this.normalizeY((float)p1.getY()), (float)p2.getX(), this.normalizeY((float)p2.getY()), c1, c2);
                PdfShadingPattern pat = new PdfShadingPattern(shading);
                if (fill) {
                    this.cb.setShadingFill(pat);
                } else {
                    this.cb.setShadingStroke(pat);
                }
            } else if (this.paint instanceof TexturePaint) {
                try {
                    TexturePaint tp = (TexturePaint)this.paint;
                    BufferedImage img = tp.getImage();
                    Rectangle2D rect = tp.getAnchorRect();
                    Image image = Image.getInstance(img, null);
                    PdfPatternPainter pattern = this.cb.createPattern(image.getWidth(), image.getHeight());
                    AffineTransform inverse = this.normalizeMatrix();
                    inverse.translate(rect.getX(), rect.getY());
                    inverse.scale(rect.getWidth() / (double)image.getWidth(), -rect.getHeight() / (double)image.getHeight());
                    double[] mx = new double[6];
                    inverse.getMatrix(mx);
                    pattern.setPatternMatrix((float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
                    image.setAbsolutePosition(0.0f, 0.0f);
                    pattern.addImage(image);
                    if (fill) {
                        this.cb.setPatternFill(pattern);
                        break block27;
                    }
                    this.cb.setPatternStroke(pattern);
                }
                catch (Exception ex) {
                    if (fill) {
                        this.cb.setColorFill(Color.gray);
                        break block27;
                    }
                    this.cb.setColorStroke(Color.gray);
                }
            } else {
                try {
                    BufferedImage img = null;
                    int type = 6;
                    if (this.paint.getTransparency() == 1) {
                        type = 5;
                    }
                    img = new BufferedImage((int)this.width, (int)this.height, type);
                    Graphics2D g = (Graphics2D)img.getGraphics();
                    g.transform(this.transform);
                    AffineTransform inv = this.transform.createInverse();
                    Shape fillRect = new Rectangle2D.Double(0.0, 0.0, img.getWidth(), img.getHeight());
                    fillRect = inv.createTransformedShape(fillRect);
                    g.setPaint(this.paint);
                    g.fill(fillRect);
                    if (invert) {
                        AffineTransform tx = new AffineTransform();
                        tx.scale(1.0, -1.0);
                        tx.translate(-xoffset, -yoffset);
                        g.drawImage(img, tx, null);
                    }
                    g.dispose();
                    g = null;
                    Image image = Image.getInstance(img, null);
                    PdfPatternPainter pattern = this.cb.createPattern(this.width, this.height);
                    image.setAbsolutePosition(0.0f, 0.0f);
                    pattern.addImage(image);
                    if (fill) {
                        if (this.currentFillGState != 255) {
                            this.currentFillGState = 255;
                            PdfGState gs = this.fillGState[255];
                            if (gs == null) {
                                gs = new PdfGState();
                                gs.setFillOpacity(1.0f);
                                this.fillGState[255] = gs;
                            }
                            this.cb.setGState(gs);
                        }
                        this.cb.setPatternFill(pattern);
                    } else {
                        this.cb.setPatternStroke(pattern);
                    }
                }
                catch (Exception ex) {
                    if (fill) {
                        this.cb.setColorFill(Color.gray);
                    }
                    this.cb.setColorStroke(Color.gray);
                }
            }
        }
    }

    private synchronized void waitForImage(java.awt.Image image) {
        if (this.mediaTracker == null) {
            this.mediaTracker = new MediaTracker(new FakeComponent());
        }
        this.mediaTracker.addImage(image, 0);
        try {
            this.mediaTracker.waitForID(0);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        this.mediaTracker.removeImage(image);
    }

    private List<String> splitIntoSubstringsByVisibility(String s) {
        ArrayList<String> stringParts = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean displayableLastChar = true;
        for (int charIndex = 0; charIndex < s.length(); ++charIndex) {
            char c = s.charAt(charIndex);
            boolean b = this.font.canDisplay(c);
            if (charIndex > 0 && displayableLastChar != b) {
                stringParts.add(sb.toString());
                sb.setLength(0);
            }
            displayableLastChar = b;
            sb.append(c);
        }
        stringParts.add(sb.toString());
        return stringParts;
    }

    private static class CompositeFontDrawer {
        private static final String COMPOSITE_FONT_CLASS_NAME = "sun.font.CompositeFont";
        private static final Class<?> COMPOSITE_FONT_CLASS;
        private static final String GET_NUM_SLOTS_METHOD_NAME = "getNumSlots";
        private static final Method GET_NUM_SLOTS_METHOD;
        private static final String GET_SLOT_FONT_METHOD_NAME = "getSlotFont";
        private static final Method GET_SLOT_FONT_METHOD;
        private static final String FONT_UTILITIES_CLASS_NAME = "sun.font.FontUtilities";
        private static final Class<?> FONT_UTILITIES_CLASS;
        private static final String GET_FONT2D_METHOD_NAME = "getFont2D";
        private static final Method GET_FONT2D_METHOD;
        private static final String FONT2D_CLASS_NAME = "sun.font.Font2D";
        private static final Class<?> FONT2D_CLASS;
        private static final String CAN_DISPLAY_METHOD_NAME = "canDisplay";
        private static final Method CAN_DYSPLAY_METHOD;
        private static final String GET_FONT_NAME_METHOD_NAME = "getFontName";
        private static final Method GET_FONT_NAME_METHOD;
        private static final boolean SUPPORTED;
        private final transient StringBuilder sb = new StringBuilder();
        private final transient List<String> stringParts = new ArrayList<String>();
        private final transient List<Font> correspondingFontsForParts = new ArrayList<Font>();
        private final transient Map<String, Boolean> fontFamilyComposite = new HashMap<String, Boolean>();

        private CompositeFontDrawer() {
        }

        static boolean isSupported() {
            return SUPPORTED;
        }

        private static Class<?> getClassForName(String className) {
            try {
                return Class.forName(className);
            }
            catch (Exception e) {
                return null;
            }
        }

        private static Method getMethod(Class<?> clazz, String methodName, Class<?> ... parameterTypes) {
            Method method;
            if (clazz == null) {
                return null;
            }
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
            }
            catch (Exception e) {
                method = null;
            }
            return method;
        }

        boolean isCompositeFont(Font font) {
            if (!CompositeFontDrawer.isSupported() || font == null) {
                assert (false);
                return false;
            }
            String fontFamily = font.getFamily();
            if (fontFamily != null && this.fontFamilyComposite.containsKey(fontFamily)) {
                return this.fontFamilyComposite.get(fontFamily);
            }
            try {
                boolean composite;
                Object result = GET_FONT2D_METHOD.invoke(null, font);
                boolean bl = composite = result != null && result.getClass() == COMPOSITE_FONT_CLASS;
                if (fontFamily != null) {
                    this.fontFamilyComposite.put(fontFamily, composite);
                }
                return composite;
            }
            catch (Exception e) {
                return false;
            }
        }

        double drawString(String s, Font compositeFont, double x, double y, Function<Font, BaseFont> fontConverter, DrawStringFunction defaultDrawingFunction) {
            String fontFamily = compositeFont.getFamily();
            if (!CompositeFontDrawer.isSupported() || fontFamily != null && !this.fontFamilyComposite.get(fontFamily).booleanValue()) {
                assert (false);
                return defaultDrawingFunction.drawString(s, fontConverter.apply(compositeFont), x, y);
            }
            try {
                this.splitStringIntoDisplayableParts(s, compositeFont);
                double width = 0.0;
                for (int i = 0; i < this.stringParts.size(); ++i) {
                    String strPart = this.stringParts.get(i);
                    Font correspondingFont = this.correspondingFontsForParts.get(i);
                    BaseFont correspondingBaseFont = fontConverter.apply(correspondingFont);
                    BaseFont baseFont = correspondingBaseFont == null ? fontConverter.apply(compositeFont) : correspondingBaseFont;
                    width += defaultDrawingFunction.drawString(strPart, baseFont, x + width, y);
                }
                return width;
            }
            catch (Exception e) {
                BaseFont baseFont = fontConverter.apply(compositeFont);
                return defaultDrawingFunction.drawString(s, baseFont, x, y);
            }
        }

        private void splitStringIntoDisplayableParts(String s, Font compositeFont) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Object result = GET_FONT2D_METHOD.invoke(null, compositeFont);
            if (result.getClass() != COMPOSITE_FONT_CLASS) {
                throw new IllegalArgumentException("Given font isn't a composite font.");
            }
            this.sb.setLength(0);
            this.stringParts.clear();
            this.correspondingFontsForParts.clear();
            Object lastPhysicalFont = null;
            Object numSlotsResult = GET_NUM_SLOTS_METHOD.invoke(result, new Object[0]);
            int numSlots = (Integer)numSlotsResult;
            for (int charIndex = 0; charIndex < s.length(); ++charIndex) {
                char c = s.charAt(charIndex);
                boolean found = false;
                for (int slotIndex = 0; slotIndex < numSlots; ++slotIndex) {
                    Object fontNameResult;
                    Object phFont = GET_SLOT_FONT_METHOD.invoke(result, slotIndex);
                    if (phFont == null) continue;
                    Boolean canDysplayResult = (Boolean)CAN_DYSPLAY_METHOD.invoke(phFont, Character.valueOf(c));
                    if (!canDysplayResult.booleanValue()) continue;
                    if (this.sb.length() == 0) {
                        fontNameResult = GET_FONT_NAME_METHOD.invoke(phFont, new Object[]{null});
                        this.correspondingFontsForParts.add(new Font((String)fontNameResult, compositeFont.getStyle(), compositeFont.getSize()));
                        lastPhysicalFont = phFont;
                    } else if (!Objects.equals(lastPhysicalFont, phFont)) {
                        this.stringParts.add(this.sb.toString());
                        this.sb.setLength(0);
                        fontNameResult = GET_FONT_NAME_METHOD.invoke(phFont, new Object[]{null});
                        this.correspondingFontsForParts.add(new Font((String)fontNameResult, compositeFont.getStyle(), compositeFont.getSize()));
                        lastPhysicalFont = phFont;
                    }
                    this.sb.append(c);
                    found = true;
                    break;
                }
                if (found) continue;
                if (this.sb.length() == 0) {
                    this.correspondingFontsForParts.add(compositeFont);
                    lastPhysicalFont = null;
                } else if (lastPhysicalFont != null) {
                    this.stringParts.add(this.sb.toString());
                    this.sb.setLength(0);
                    this.correspondingFontsForParts.add(compositeFont);
                    lastPhysicalFont = null;
                }
                this.sb.append(c);
            }
            this.stringParts.add(this.sb.toString());
            this.sb.setLength(0);
        }

        static {
            String osName = System.getProperty("os.name", "unknownOS");
            boolean windowsOS = osName.startsWith("Windows");
            if (windowsOS) {
                FONT_UTILITIES_CLASS = CompositeFontDrawer.getClassForName(FONT_UTILITIES_CLASS_NAME);
                GET_FONT2D_METHOD = CompositeFontDrawer.getMethod(FONT_UTILITIES_CLASS, GET_FONT2D_METHOD_NAME, Font.class);
                COMPOSITE_FONT_CLASS = CompositeFontDrawer.getClassForName(COMPOSITE_FONT_CLASS_NAME);
                GET_NUM_SLOTS_METHOD = CompositeFontDrawer.getMethod(COMPOSITE_FONT_CLASS, GET_NUM_SLOTS_METHOD_NAME, new Class[0]);
                GET_SLOT_FONT_METHOD = CompositeFontDrawer.getMethod(COMPOSITE_FONT_CLASS, GET_SLOT_FONT_METHOD_NAME, Integer.TYPE);
                FONT2D_CLASS = CompositeFontDrawer.getClassForName(FONT2D_CLASS_NAME);
                CAN_DYSPLAY_METHOD = CompositeFontDrawer.getMethod(FONT2D_CLASS, CAN_DISPLAY_METHOD_NAME, Character.TYPE);
                GET_FONT_NAME_METHOD = CompositeFontDrawer.getMethod(FONT2D_CLASS, GET_FONT_NAME_METHOD_NAME, Locale.class);
            } else {
                FONT_UTILITIES_CLASS = null;
                GET_FONT2D_METHOD = null;
                COMPOSITE_FONT_CLASS = null;
                GET_NUM_SLOTS_METHOD = null;
                GET_SLOT_FONT_METHOD = null;
                FONT2D_CLASS = null;
                CAN_DYSPLAY_METHOD = null;
                GET_FONT_NAME_METHOD = null;
            }
            SUPPORTED = FONT_UTILITIES_CLASS != null && COMPOSITE_FONT_CLASS != null && FONT2D_CLASS != null && GET_FONT2D_METHOD != null && GET_NUM_SLOTS_METHOD != null && GET_SLOT_FONT_METHOD != null && CAN_DYSPLAY_METHOD != null && GET_FONT_NAME_METHOD != null;
        }

        @FunctionalInterface
        public static interface DrawStringFunction {
            public double drawString(String var1, BaseFont var2, double var3, double var5);
        }
    }

    public static class HyperLinkKey
    extends RenderingHints.Key {
        public static final HyperLinkKey KEY_INSTANCE = new HyperLinkKey(9999);
        public static final Object VALUE_HYPERLINKKEY_OFF = "0";

        protected HyperLinkKey(int arg0) {
            super(arg0);
        }

        @Override
        public boolean isCompatibleValue(Object val) {
            return true;
        }

        public String toString() {
            return "HyperLinkKey";
        }
    }

    private static class FakeComponent
    extends Component {
        private static final long serialVersionUID = 6450197945596086638L;

        private FakeComponent() {
        }
    }
}

