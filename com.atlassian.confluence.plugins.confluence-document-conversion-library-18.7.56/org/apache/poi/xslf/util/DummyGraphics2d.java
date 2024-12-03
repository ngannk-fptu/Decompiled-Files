/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class DummyGraphics2d
extends Graphics2D {
    private BufferedImage bufimg;
    private final Graphics2D g2D;
    private final PrintStream log;
    private static final String[] COMPOSITE_RULES = new String[]{"CLEAR", "SRC", "SRC_OVER", "DST_OVER", "SRC_IN", "DST_IN", "SRC_OUT", "DST_OUT", "DST", "SRC_ATOP", "DST_ATOP", "XOR"};
    private static final Object[] HINTS = new Object[]{RenderingHints.KEY_ANTIALIASING, "RenderingHints.KEY_ANTIALIASING", RenderingHints.VALUE_ANTIALIAS_ON, "RenderingHints.VALUE_ANTIALIAS_ON", RenderingHints.VALUE_ANTIALIAS_OFF, "RenderingHints.VALUE_ANTIALIAS_OFF", RenderingHints.VALUE_ANTIALIAS_DEFAULT, "RenderingHints.VALUE_ANTIALIAS_DEFAULT", RenderingHints.KEY_RENDERING, "RenderingHints.KEY_RENDERING", RenderingHints.VALUE_RENDER_SPEED, "RenderingHints.VALUE_RENDER_SPEED", RenderingHints.VALUE_RENDER_QUALITY, "RenderingHints.VALUE_RENDER_QUALITY", RenderingHints.VALUE_RENDER_DEFAULT, "RenderingHints.VALUE_RENDER_DEFAULT", RenderingHints.KEY_DITHERING, "RenderingHints.KEY_DITHERING", RenderingHints.VALUE_DITHER_DISABLE, "RenderingHints.VALUE_DITHER_DISABLE", RenderingHints.VALUE_DITHER_ENABLE, "RenderingHints.VALUE_DITHER_ENABLE", RenderingHints.VALUE_DITHER_DEFAULT, "RenderingHints.VALUE_DITHER_DEFAULT", RenderingHints.KEY_TEXT_ANTIALIASING, "RenderingHints.KEY_TEXT_ANTIALIASING", RenderingHints.VALUE_TEXT_ANTIALIAS_ON, "RenderingHints.VALUE_TEXT_ANTIALIAS_ON", RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, "RenderingHints.VALUE_TEXT_ANTIALIAS_OFF", RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, "RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT", RenderingHints.VALUE_TEXT_ANTIALIAS_GASP, "RenderingHints.VALUE_TEXT_ANTIALIAS_GASP", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB, "RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR, "RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB, "RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR, "RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR", RenderingHints.KEY_TEXT_LCD_CONTRAST, "RenderingHints.KEY_TEXT_LCD_CONTRAST", RenderingHints.KEY_FRACTIONALMETRICS, "RenderingHints.KEY_FRACTIONALMETRICS", RenderingHints.VALUE_FRACTIONALMETRICS_OFF, "RenderingHints.VALUE_FRACTIONALMETRICS_OFF", RenderingHints.VALUE_FRACTIONALMETRICS_ON, "RenderingHints.VALUE_FRACTIONALMETRICS_ON", RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT, "RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT", RenderingHints.KEY_INTERPOLATION, "RenderingHints.KEY_INTERPOLATION", RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, "RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR", RenderingHints.VALUE_INTERPOLATION_BILINEAR, "RenderingHints.VALUE_INTERPOLATION_BILINEAR", RenderingHints.VALUE_INTERPOLATION_BICUBIC, "RenderingHints.VALUE_INTERPOLATION_BICUBIC", RenderingHints.KEY_ALPHA_INTERPOLATION, "RenderingHints.KEY_ALPHA_INTERPOLATION", RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED, "RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED", RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY, "RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY", RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT, "RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT", RenderingHints.KEY_COLOR_RENDERING, "RenderingHints.KEY_COLOR_RENDERING", RenderingHints.VALUE_COLOR_RENDER_SPEED, "RenderingHints.VALUE_COLOR_RENDER_SPEED", RenderingHints.VALUE_COLOR_RENDER_QUALITY, "RenderingHints.VALUE_COLOR_RENDER_QUALITY", RenderingHints.VALUE_COLOR_RENDER_DEFAULT, "RenderingHints.VALUE_COLOR_RENDER_DEFAULT", RenderingHints.KEY_STROKE_CONTROL, "RenderingHints.KEY_STROKE_CONTROL", RenderingHints.VALUE_STROKE_DEFAULT, "RenderingHints.VALUE_STROKE_DEFAULT", RenderingHints.VALUE_STROKE_NORMALIZE, "RenderingHints.VALUE_STROKE_NORMALIZE", RenderingHints.VALUE_STROKE_PURE, "RenderingHints.VALUE_STROKE_PURE"};
    private static final Object[] ATTRS = new Object[]{TextAttribute.FAMILY, "TextAttribute.FAMILY", TextAttribute.WEIGHT, "TextAttribute.WEIGHT", TextAttribute.WEIGHT_EXTRA_LIGHT, "TextAttribute.WEIGHT_EXTRA_LIGHT", TextAttribute.WEIGHT_LIGHT, "TextAttribute.WEIGHT_LIGHT", TextAttribute.WEIGHT_DEMILIGHT, "TextAttribute.WEIGHT_DEMILIGHT", TextAttribute.WEIGHT_REGULAR, "TextAttribute.WEIGHT_REGULAR", TextAttribute.WEIGHT_SEMIBOLD, "TextAttribute.WEIGHT_SEMIBOLD", TextAttribute.WEIGHT_MEDIUM, "TextAttribute.WEIGHT_MEDIUM", TextAttribute.WEIGHT_DEMIBOLD, "TextAttribute.WEIGHT_DEMIBOLD", TextAttribute.WEIGHT_BOLD, "TextAttribute.WEIGHT_BOLD", TextAttribute.WEIGHT_HEAVY, "TextAttribute.WEIGHT_HEAVY", TextAttribute.WEIGHT_EXTRABOLD, "TextAttribute.WEIGHT_EXTRABOLD", TextAttribute.WEIGHT_ULTRABOLD, "TextAttribute.WEIGHT_ULTRABOLD", TextAttribute.WIDTH, "TextAttribute.WIDTH", TextAttribute.WIDTH_CONDENSED, "TextAttribute.WIDTH_CONDENSED", TextAttribute.WIDTH_SEMI_CONDENSED, "TextAttribute.WIDTH_SEMI_CONDENSED", TextAttribute.WIDTH_REGULAR, "TextAttribute.WIDTH_REGULAR", TextAttribute.WIDTH_SEMI_EXTENDED, "TextAttribute.WIDTH_SEMI_EXTENDED", TextAttribute.WIDTH_EXTENDED, "TextAttribute.WIDTH_EXTENDED", TextAttribute.POSTURE, "TextAttribute.POSTURE", TextAttribute.POSTURE_REGULAR, "TextAttribute.POSTURE_REGULAR", TextAttribute.POSTURE_OBLIQUE, "TextAttribute.POSTURE_OBLIQUE", TextAttribute.SIZE, "TextAttribute.SIZE", TextAttribute.TRANSFORM, "TextAttribute.TRANSFORM", TextAttribute.SUPERSCRIPT, "TextAttribute.SUPERSCRIPT", TextAttribute.SUPERSCRIPT_SUPER, "TextAttribute.SUPERSCRIPT_SUPER", TextAttribute.SUPERSCRIPT_SUB, "TextAttribute.SUPERSCRIPT_SUB", TextAttribute.FONT, "TextAttribute.FONT", TextAttribute.CHAR_REPLACEMENT, "TextAttribute.CHAR_REPLACEMENT", TextAttribute.FOREGROUND, "TextAttribute.FOREGROUND", TextAttribute.BACKGROUND, "TextAttribute.BACKGROUND", TextAttribute.UNDERLINE, "TextAttribute.UNDERLINE", TextAttribute.UNDERLINE_ON, "TextAttribute.UNDERLINE_ON", TextAttribute.STRIKETHROUGH, "TextAttribute.STRIKETHROUGH", TextAttribute.STRIKETHROUGH_ON, "TextAttribute.STRIKETHROUGH_ON", TextAttribute.RUN_DIRECTION, "TextAttribute.RUN_DIRECTION", TextAttribute.RUN_DIRECTION_LTR, "TextAttribute.RUN_DIRECTION_LTR", TextAttribute.RUN_DIRECTION_RTL, "TextAttribute.RUN_DIRECTION_RTL", TextAttribute.BIDI_EMBEDDING, "TextAttribute.BIDI_EMBEDDING", TextAttribute.JUSTIFICATION, "TextAttribute.JUSTIFICATION", TextAttribute.JUSTIFICATION_FULL, "TextAttribute.JUSTIFICATION_FULL", TextAttribute.JUSTIFICATION_NONE, "TextAttribute.JUSTIFICATION_NONE", TextAttribute.INPUT_METHOD_HIGHLIGHT, "TextAttribute.INPUT_METHOD_HIGHLIGHT", TextAttribute.INPUT_METHOD_UNDERLINE, "TextAttribute.INPUT_METHOD_UNDERLINE", TextAttribute.UNDERLINE_LOW_ONE_PIXEL, "TextAttribute.UNDERLINE_LOW_ONE_PIXEL", TextAttribute.UNDERLINE_LOW_TWO_PIXEL, "TextAttribute.UNDERLINE_LOW_TWO_PIXEL", TextAttribute.UNDERLINE_LOW_DOTTED, "TextAttribute.UNDERLINE_LOW_DOTTED", TextAttribute.UNDERLINE_LOW_GRAY, "TextAttribute.UNDERLINE_LOW_GRAY", TextAttribute.UNDERLINE_LOW_DASHED, "TextAttribute.UNDERLINE_LOW_DASHED", TextAttribute.SWAP_COLORS, "TextAttribute.SWAP_COLORS", TextAttribute.SWAP_COLORS_ON, "TextAttribute.SWAP_COLORS_ON", TextAttribute.NUMERIC_SHAPING, "TextAttribute.NUMERIC_SHAPING", TextAttribute.KERNING, "TextAttribute.KERNING", TextAttribute.KERNING_ON, "TextAttribute.KERNING_ON", TextAttribute.LIGATURES, "TextAttribute.LIGATURES", TextAttribute.LIGATURES_ON, "TextAttribute.LIGATURES_ON", TextAttribute.TRACKING, "TextAttribute.TRACKING", TextAttribute.TRACKING_TIGHT, "TextAttribute.TRACKING_TIGHT", TextAttribute.TRACKING_LOOSE, "TextAttribute.TRACKING_LOOSE"};

    public DummyGraphics2d() {
        this(System.out);
    }

    public DummyGraphics2d(PrintStream log) {
        this.bufimg = new BufferedImage(1000, 1000, 2);
        this.g2D = (Graphics2D)this.bufimg.getGraphics();
        this.log = log;
    }

    public DummyGraphics2d(PrintStream log, Graphics2D g2D) {
        this.g2D = g2D;
        this.log = log;
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        String l = "addRenderingHinds(Map):\n  hints = " + hints;
        this.log.println(l);
        this.g2D.addRenderingHints(hints);
    }

    @Override
    public void clip(Shape s) {
        String l = "clip(Shape):\n  s = " + s;
        this.log.println(l);
        this.g2D.clip(s);
    }

    private void pathToString(StringBuilder sb, Path2D p) {
        sb.append("Path2D p = new Path2D.Double(").append(p.getWindingRule()).append(");\n");
        double[] coords = new double[6];
        PathIterator pi = p.getPathIterator(null);
        while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case 0: {
                    sb.append("p.moveTo(").append(coords[0]).append(",").append(coords[1]).append(");\n");
                    break;
                }
                case 1: {
                    sb.append("p.lineTo(").append(coords[0]).append(",").append(coords[1]).append(");\n");
                    break;
                }
                case 2: {
                    sb.append("p.quadTo(").append(coords[0]).append(",").append(coords[1]).append(",").append(coords[2]).append(",").append(coords[3]).append(");\n");
                    break;
                }
                case 3: {
                    sb.append("p.curveTo(").append(coords[0]).append(",").append(coords[1]).append(",").append(coords[2]).append(",").append(coords[3]).append(",").append(coords[4]).append(",").append(coords[5]).append(");\n");
                    break;
                }
                case 4: {
                    sb.append("p.closePath();\n");
                }
            }
            pi.next();
        }
    }

    @Override
    public void draw(Shape s) {
        if (s instanceof Path2D) {
            StringBuilder sb = new StringBuilder();
            this.pathToString(sb, (Path2D)s);
            sb.append("g.draw(p);");
            this.log.println(sb);
        } else {
            this.log.println("g.draw(" + s + ")");
        }
        this.g2D.draw(s);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        String l = "drawGlyphVector(GlyphVector, float, float):\n  g = " + g + "\n  x = " + x + "\n  y = " + y;
        this.log.println(l);
        this.g2D.drawGlyphVector(g, x, y);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        String l = "drawImage(BufferedImage, BufferedImageOp, x, y):\n  img = " + img + "\n  op = " + op + "\n  x = " + x + "\n  y = " + y;
        this.log.println(l);
        this.g2D.drawImage(img, op, x, y);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        String l = "drawImage(Image,AfflineTransform,ImageObserver):\n  img = " + img + "\n  xform = " + xform + "\n  obs = " + obs;
        this.log.println(l);
        return this.g2D.drawImage(img, xform, obs);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        String l = "drawRenderableImage(RenderableImage, AfflineTransform):\n  img = " + img + "\n  xform = " + xform;
        this.log.println(l);
        this.g2D.drawRenderableImage(img, xform);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        String l = "drawRenderedImage(RenderedImage, AffineTransform):\n  img = " + img + "\n  xform = " + xform;
        this.log.println(l);
        this.g2D.drawRenderedImage(img, xform);
    }

    @Override
    public void drawString(String s, float x, float y) {
        String l = "drawString(s,x,y):\n  s = " + s + "\n  x = " + x + "\n  y = " + y;
        this.log.println(l);
        this.g2D.drawString(s, x, y);
    }

    @Override
    public void fill(Shape s) {
        if (s instanceof Path2D) {
            StringBuilder sb = new StringBuilder();
            this.pathToString(sb, (Path2D)s);
            sb.append("g.fill(p);");
            this.log.println(sb);
        } else {
            this.log.println("g.fill(" + s + ")");
        }
        this.g2D.fill(s);
    }

    @Override
    public Color getBackground() {
        this.log.println("getBackground():");
        return this.g2D.getBackground();
    }

    @Override
    public Composite getComposite() {
        this.log.println("getComposite():");
        return this.g2D.getComposite();
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        this.log.println("getDeviceConfiguration():");
        return this.g2D.getDeviceConfiguration();
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        this.log.println("getFontRenderContext():");
        return this.g2D.getFontRenderContext();
    }

    @Override
    public Paint getPaint() {
        this.log.println("getPaint():");
        return this.g2D.getPaint();
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        this.log.println("getRenderingHint(\"" + hintKey + "\")");
        return this.g2D.getRenderingHint(hintKey);
    }

    @Override
    public RenderingHints getRenderingHints() {
        this.log.println("getRenderingHints():");
        return this.g2D.getRenderingHints();
    }

    @Override
    public Stroke getStroke() {
        this.log.println("getStroke():");
        return this.g2D.getStroke();
    }

    @Override
    public AffineTransform getTransform() {
        this.log.println("getTransform():");
        return this.g2D.getTransform();
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        String l = "hit(Rectangle, Shape, onStroke):\n  rect = " + rect + "\n  s = " + s + "\n  onStroke = " + onStroke;
        this.log.println(l);
        return this.g2D.hit(rect, s, onStroke);
    }

    @Override
    public void rotate(double theta) {
        String l = "rotate(theta):\n  theta = " + theta;
        this.log.println(l);
        this.g2D.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        String l = "rotate(double,double,double):\n  theta = " + theta + "\n  x = " + x + "\n  y = " + y;
        this.log.println(l);
        this.g2D.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        this.log.println("g.scale(" + sx + "," + sy + ");");
        this.g2D.scale(sx, sy);
    }

    @Override
    public void setBackground(Color color) {
        this.log.printf(Locale.ROOT, "setBackground(new Color(0x%08X))%n", color.getRGB());
        this.g2D.setBackground(color);
    }

    @Override
    public void setComposite(Composite comp) {
        String l = "g.setComposite(";
        if (comp instanceof AlphaComposite) {
            AlphaComposite ac = (AlphaComposite)comp;
            l = l + "AlphaComposite.getInstance(AlphaComposite." + COMPOSITE_RULES[Math.max(0, Math.min(COMPOSITE_RULES.length - 1, ac.getRule()))] + ", " + ac.getAlpha() + "f));";
        } else {
            l = l + comp.toString() + ");";
        }
        this.log.println(l);
        this.g2D.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        String l = "g.setPaint(";
        l = paint instanceof Color ? l + String.format(Locale.ROOT, "new Color(0x%08X));", ((Color)paint).getRGB()) : l + paint.toString() + ");";
        this.log.println(l);
        this.g2D.setPaint(paint);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.log.println("g.setRenderingHint(" + DummyGraphics2d.mapHint(hintKey) + ", " + DummyGraphics2d.mapHint(hintValue) + ");");
        this.g2D.setRenderingHint(hintKey, hintValue);
    }

    private static String mapHint(Object hint) {
        if (hint == null) {
            return "null";
        }
        if (hint instanceof AffineTransform) {
            return DummyGraphics2d.mapTransform((AffineTransform)hint);
        }
        for (int i = 0; i < HINTS.length; i += 2) {
            if (hint != HINTS[i]) continue;
            return (String)HINTS[i + 1];
        }
        return "\"" + hint + "\"";
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        String l = "setRenderingHints(Map):\n  hints = " + hints;
        this.log.println(l);
        this.g2D.setRenderingHints(hints);
    }

    @Override
    public void setStroke(Stroke s) {
        String l;
        if (s instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke)s;
            String cap = (new String[]{"BUTT", "ROUND", "SQUARE"})[bs.getEndCap()];
            String join = (new String[]{"MITER", "ROUND", "BEVEL"})[bs.getLineJoin()];
            l = "g.setStroke(new BasicStroke(" + bs.getLineWidth() + "f, BasicStroke.CAP_" + cap + ", BasicStroke.JOIN_" + join + ", " + bs.getMiterLimit() + "f, " + Arrays.toString(bs.getDashArray()) + ", " + bs.getDashPhase() + "f));";
        } else {
            l = "g.setStroke(" + s + ");";
        }
        this.log.println(l);
        this.g2D.setStroke(s);
    }

    private static String mapTransform(AffineTransform tx) {
        return tx.isIdentity() ? "new AffineTransform()" : "new AffineTransform(" + tx.getScaleX() + "f," + tx.getShearY() + "f," + tx.getShearX() + "f," + tx.getScaleY() + "f," + tx.getTranslateX() + "f," + tx.getTranslateY() + "f)";
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        this.log.println("g.setTransform(" + DummyGraphics2d.mapTransform(Tx) + ");");
        this.g2D.setTransform(Tx);
    }

    @Override
    public void shear(double shx, double shy) {
        String l = "shear(shx, dhy):\n  shx = " + shx + "\n  shy = " + shy;
        this.log.println(l);
        this.g2D.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        String l = "transform(AffineTransform):\n  Tx = " + Tx;
        this.log.println(l);
        this.g2D.transform(Tx);
    }

    @Override
    public void translate(double tx, double ty) {
        String l = "translate(double, double):\n  tx = " + tx + "\n  ty = " + ty;
        this.log.println(l);
        this.g2D.translate(tx, ty);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        String l = "clearRect(int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height;
        this.log.println(l);
        this.g2D.clearRect(x, y, width, height);
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        String l = "clipRect(int, int, int, int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "height = " + height;
        this.log.println(l);
        this.g2D.clipRect(x, y, width, height);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        String l = "copyArea(int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height;
        this.log.println(l);
        this.g2D.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public Graphics create() {
        this.log.println("create():");
        return this.g2D.create();
    }

    @Override
    public Graphics create(int x, int y, int width, int height) {
        String l = "create(int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height;
        this.log.println(l);
        return this.g2D.create(x, y, width, height);
    }

    @Override
    public void dispose() {
        this.log.println("dispose():");
        this.g2D.dispose();
    }

    @Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        String l = "draw3DRect(int,int,int,int,boolean):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height + "\n  raised = " + raised;
        this.log.println(l);
        this.g2D.draw3DRect(x, y, width, height, raised);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        String l = "drawArc(int,int,int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height + "\n  startAngle = " + startAngle + "\n  arcAngle = " + arcAngle;
        this.log.println(l);
        this.g2D.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        String l = "drawBytes(byte[],int,int,int,int):\n  data = " + Arrays.toString(data) + "\n  offset = " + offset + "\n  length = " + length + "\n  x = " + x + "\n  y = " + y;
        this.log.println(l);
        this.g2D.drawBytes(data, offset, length, x, y);
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        String l = "drawChars(data,int,int,int,int):\n  data = " + Arrays.toString(data) + "\n  offset = " + offset + "\n  length = " + length + "\n  x = " + x + "\n  y = " + y;
        this.log.println(l);
        this.g2D.drawChars(data, offset, length, x, y);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        String l = "drawImage(Image,int,int,int,int,int,int,int,int,ImageObserver):\n  img = " + img + "\n  dx1 = " + dx1 + "\n  dy1 = " + dy1 + "\n  dx2 = " + dx2 + "\n  dy2 = " + dy2 + "\n  sx1 = " + sx1 + "\n  sy1 = " + sy1 + "\n  sx2 = " + sx2 + "\n  sy2 = " + sy2 + "\n  observer = " + observer;
        this.log.println(l);
        return this.g2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        String l = "drawImage(Image,int,int,int,int,int,int,int,int,Color,ImageObserver):\n  img = " + img + "\n  dx1 = " + dx1 + "\n  dy1 = " + dy1 + "\n  dx2 = " + dx2 + "\n  dy2 = " + dy2 + "\n  sx1 = " + sx1 + "\n  sy1 = " + sy1 + "\n  sx2 = " + sx2 + "\n  sy2 = " + sy2 + "\n  bgcolor = " + bgcolor + "\n  observer = " + observer;
        this.log.println(l);
        return this.g2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        String l = "drawImage(Image,int,int,Color,ImageObserver):\n  img = " + img + "\n  x = " + x + "\n  y = " + y + "\n  bgcolor = " + bgcolor + "\n  observer = " + observer;
        this.log.println(l);
        return this.g2D.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        String l = "drawImage(Image,int,int,observer):\n  img = " + img + "\n  x = " + x + "\n  y = " + y + "\n  observer = " + observer;
        this.log.println(l);
        return this.g2D.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        String l = "drawImage(Image,int,int,int,int,Color,ImageObserver):\n  img = " + img + "\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height + "\n  bgcolor = " + bgcolor + "\n  observer = " + observer;
        this.log.println(l);
        return this.g2D.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        String l = "drawImage(Image,int,int,width,height,observer):\n  img = " + img + "\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height + "\n  observer = " + observer;
        this.log.println(l);
        return this.g2D.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        String l = "drawLine(int,int,int,int):\n  x1 = " + x1 + "\n  y1 = " + y1 + "\n  x2 = " + x2 + "\n  y2 = " + y2;
        this.log.println(l);
        this.g2D.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        String l = "drawOval(int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height;
        this.log.println(l);
        this.g2D.drawOval(x, y, width, height);
    }

    @Override
    public void drawPolygon(Polygon p) {
        String l = "drawPolygon(Polygon):\n  p = " + p;
        this.log.println(l);
        this.g2D.drawPolygon(p);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        String l = "drawPolygon(int[],int[],int):\n  xPoints = " + Arrays.toString(xPoints) + "\n  yPoints = " + Arrays.toString(yPoints) + "\n  nPoints = " + nPoints;
        this.log.println(l);
        this.g2D.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        String l = "drawPolyline(int[],int[],int):\n  xPoints = " + Arrays.toString(xPoints) + "\n  yPoints = " + Arrays.toString(yPoints) + "\n  nPoints = " + nPoints;
        this.log.println(l);
        this.g2D.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        String l = "drawRect(int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height;
        this.log.println(l);
        this.g2D.drawRect(x, y, width, height);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        String l = "drawRoundRect(int,int,int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height + "\n  arcWidth = " + arcWidth + "\n  arcHeight = " + arcHeight;
        this.log.println(l);
        this.g2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    private static String mapAttribute(Object attr) {
        if (attr == null) {
            return "null";
        }
        if (attr instanceof Font) {
            Font f = (Font)attr;
            String[] STYLE = new String[]{"Font.PLAIN", "Font.BOLD", "Font.ITALIC", "Font.BOLD | Font.ITALIC"};
            return "new Font(\"" + f.getFamily(Locale.ROOT) + "\"," + STYLE[f.getStyle()] + "," + f.getSize() + ")";
        }
        if (attr instanceof Color) {
            return String.format(Locale.ROOT, "new Color(0x%08X)", ((Color)attr).getRGB());
        }
        for (int i = 0; i < ATTRS.length; i += 2) {
            if (attr != ATTRS[i]) continue;
            return (String)ATTRS[i + 1];
        }
        return "\"" + attr + "\"";
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        int startIdx = iterator.getIndex();
        HashMap attMap = new HashMap();
        StringBuilder sb = new StringBuilder();
        char ch = iterator.current();
        while (ch != '\uffff') {
            sb.append(ch);
            iterator.getAttributes().forEach((k, v) -> attMap.computeIfAbsent(k, k2 -> new LinkedHashMap()).put(iterator.getIndex(), v));
            ch = iterator.next();
        }
        String l = "AttributedString as = new AttributedString(\"" + sb + "\");\n";
        sb.setLength(0);
        sb.append(l);
        for (Map.Entry me : attMap.entrySet()) {
            int startPos = -2;
            int lastPos = -2;
            AttributedCharacterIterator.Attribute at = (AttributedCharacterIterator.Attribute)me.getKey();
            Object lastObj = null;
            for (Map.Entry mo : ((Map)me.getValue()).entrySet()) {
                int pos = (Integer)mo.getKey();
                Object obj = mo.getValue();
                if (lastPos < pos - 1 || obj != lastObj) {
                    if (startPos >= 0) {
                        sb.append("as.addAttribute(").append(DummyGraphics2d.mapAttribute(at)).append(",").append(DummyGraphics2d.mapAttribute(lastObj)).append(",").append(startPos).append(",").append(lastPos + 1).append(");\n");
                    }
                    startPos = pos;
                }
                lastPos = pos;
                lastObj = obj;
            }
            if (lastObj == null) continue;
            sb.append("as.addAttribute(").append(DummyGraphics2d.mapAttribute(at)).append(",").append(DummyGraphics2d.mapAttribute(lastObj)).append(",").append(startPos).append(",").append(lastPos + 1).append(");\n");
        }
        sb.append("g.drawString(as.getIterator(),").append(x).append("f,").append(y).append("f);");
        this.log.println(sb);
        iterator.setIndex(startIdx);
        this.g2D.drawString(iterator, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        this.drawString(iterator, (float)x, (float)y);
    }

    @Override
    public void drawString(String str, int x, int y) {
        String l = "drawString(str,int,int):\n  str = " + str + "\n  x = " + x + "\n  y = " + y;
        this.log.println(l);
        this.g2D.drawString(str, x, y);
    }

    @Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        String l = "fill3DRect(int,int,int,int,boolean):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height + "\n  raised = " + raised;
        this.log.println(l);
        this.g2D.fill3DRect(x, y, width, height, raised);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        String l = "fillArc(int,int,int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height + "\n  startAngle = " + startAngle + "\n  arcAngle = " + arcAngle;
        this.log.println(l);
        this.g2D.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        String l = "fillOval(int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height;
        this.log.println(l);
        this.g2D.fillOval(x, y, width, height);
    }

    @Override
    public void fillPolygon(Polygon p) {
        String l = "fillPolygon(Polygon):\n  p = " + p;
        this.log.println(l);
        this.g2D.fillPolygon(p);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        String l = "fillPolygon(int[],int[],int):\n  xPoints = " + Arrays.toString(xPoints) + "\n  yPoints = " + Arrays.toString(yPoints) + "\n  nPoints = " + nPoints;
        this.log.println(l);
        this.g2D.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        this.log.println("g.fillRect(" + x + "," + y + "," + width + "," + height + ");");
        this.g2D.fillRect(x, y, width, height);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.log.println("fillRoundRect(" + x + "," + y + "," + width + "," + height + "," + arcWidth + "," + arcHeight + ")");
        this.g2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public Shape getClip() {
        this.log.println("getClip():");
        return this.g2D.getClip();
    }

    @Override
    public Rectangle getClipBounds() {
        this.log.println("getClipBounds():");
        return this.g2D.getClipBounds();
    }

    @Override
    public Rectangle getClipBounds(Rectangle r) {
        String l = "getClipBounds(Rectangle):\n  r = " + r;
        this.log.println(l);
        return this.g2D.getClipBounds(r);
    }

    @Override
    public Color getColor() {
        this.log.println("getColor():");
        return this.g2D.getColor();
    }

    @Override
    public Font getFont() {
        this.log.println("getFont():");
        return this.g2D.getFont();
    }

    @Override
    public FontMetrics getFontMetrics() {
        this.log.println("getFontMetrics():");
        return this.g2D.getFontMetrics();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        this.log.println("getFontMetrics():");
        return this.g2D.getFontMetrics(f);
    }

    @Override
    public boolean hitClip(int x, int y, int width, int height) {
        String l = "hitClip(int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height;
        this.log.println(l);
        return this.g2D.hitClip(x, y, width, height);
    }

    @Override
    public void setClip(Shape clip) {
        String l = "setClip(Shape):\n  clip = " + clip;
        this.log.println(l);
        this.g2D.setClip(clip);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        String l = "setClip(int,int,int,int):\n  x = " + x + "\n  y = " + y + "\n  width = " + width + "\n  height = " + height;
        this.log.println(l);
        this.g2D.setClip(x, y, width, height);
    }

    @Override
    public void setColor(Color c) {
        this.log.printf(Locale.ROOT, "g.setColor(new Color(0x%08X));%n", c.getRGB());
        this.g2D.setColor(c);
    }

    @Override
    public void setFont(Font font) {
        String l = "setFont(Font):\n  font = " + font;
        this.log.println(l);
        this.g2D.setFont(font);
    }

    @Override
    public void setPaintMode() {
        this.log.println("setPaintMode():");
        this.g2D.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        String l = "setXORMode(Color):\n  c1 = " + c1;
        this.log.println(l);
        this.g2D.setXORMode(c1);
    }

    @Override
    public String toString() {
        this.log.println("toString():");
        return this.g2D.toString();
    }

    @Override
    public void translate(int x, int y) {
        String l = "translate(int,int):\n  x = " + x + "\n  y = " + y;
        this.log.println(l);
        this.g2D.translate(x, y);
    }
}

