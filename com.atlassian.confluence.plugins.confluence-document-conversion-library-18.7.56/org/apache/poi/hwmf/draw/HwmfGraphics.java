/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.draw;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfImageRenderer;
import org.apache.poi.hwmf.record.HwmfBrushStyle;
import org.apache.poi.hwmf.record.HwmfFont;
import org.apache.poi.hwmf.record.HwmfMapMode;
import org.apache.poi.hwmf.record.HwmfMisc;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.hwmf.record.HwmfPenStyle;
import org.apache.poi.hwmf.record.HwmfRegionMode;
import org.apache.poi.hwmf.record.HwmfText;
import org.apache.poi.hwmf.usermodel.HwmfCharsetAware;
import org.apache.poi.sl.draw.BitmapImageRenderer;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawFontManager;
import org.apache.poi.sl.draw.DrawPictureShape;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;

public class HwmfGraphics
implements HwmfCharsetAware {
    private static final Float[] WEIGHT_MAP = new Float[]{Float.valueOf(900.0f), TextAttribute.WEIGHT_ULTRABOLD, Float.valueOf(800.0f), TextAttribute.WEIGHT_EXTRABOLD, Float.valueOf(750.0f), TextAttribute.WEIGHT_HEAVY, Float.valueOf(700.0f), TextAttribute.WEIGHT_BOLD, Float.valueOf(600.0f), TextAttribute.WEIGHT_DEMIBOLD, Float.valueOf(500.0f), TextAttribute.WEIGHT_MEDIUM, Float.valueOf(450.0f), TextAttribute.WEIGHT_SEMIBOLD, Float.valueOf(400.0f), TextAttribute.WEIGHT_REGULAR, Float.valueOf(300.0f), TextAttribute.WEIGHT_DEMILIGHT, Float.valueOf(200.0f), TextAttribute.WEIGHT_LIGHT, Float.valueOf(1.0f), TextAttribute.WEIGHT_EXTRA_LIGHT};
    private final List<HwmfDrawProperties> propStack = new LinkedList<HwmfDrawProperties>();
    protected HwmfDrawProperties prop;
    protected final Graphics2D graphicsCtx;
    protected final BitSet objectIndexes = new BitSet();
    protected final TreeMap<Integer, HwmfObjectTableEntry> objectTable = new TreeMap();
    private final AffineTransform initialAT = new AffineTransform();
    private final Rectangle2D bbox;
    private Supplier<Charset> charsetProvider = () -> LocaleUtil.CHARSET_1252;

    public HwmfGraphics(Graphics2D graphicsCtx, Rectangle2D bbox) {
        this.graphicsCtx = graphicsCtx;
        this.bbox = (Rectangle2D)bbox.clone();
        this.initialAT.setTransform(graphicsCtx.getTransform());
    }

    public HwmfDrawProperties getProperties() {
        if (this.prop == null) {
            this.prop = this.newProperties(null);
        }
        return this.prop;
    }

    protected HwmfDrawProperties newProperties(HwmfDrawProperties oldProps) {
        return oldProps == null ? new HwmfDrawProperties() : new HwmfDrawProperties(oldProps);
    }

    public void draw(Shape shape) {
        HwmfPenStyle ps = this.getProperties().getPenStyle();
        if (ps == null) {
            return;
        }
        HwmfPenStyle.HwmfLineDash lineDash = ps.getLineDash();
        if (lineDash == HwmfPenStyle.HwmfLineDash.NULL) {
            return;
        }
        BasicStroke stroke = this.getStroke();
        if (this.getProperties().getBkMode() == HwmfMisc.WmfSetBkMode.HwmfBkMode.OPAQUE && lineDash != HwmfPenStyle.HwmfLineDash.SOLID && lineDash != HwmfPenStyle.HwmfLineDash.INSIDEFRAME) {
            this.graphicsCtx.setStroke(new BasicStroke(stroke.getLineWidth()));
            this.graphicsCtx.setColor(this.getProperties().getBackgroundColor().getColor());
            this.graphicsCtx.draw(shape);
        }
        this.graphicsCtx.setStroke(stroke);
        this.graphicsCtx.setColor(this.getProperties().getPenColor().getColor());
        this.graphicsCtx.draw(shape);
    }

    public void fill(Shape shape) {
        HwmfDrawProperties prop = this.getProperties();
        if (prop.getBrushStyle() != HwmfBrushStyle.BS_NULL) {
            Composite old = this.graphicsCtx.getComposite();
            this.graphicsCtx.setComposite(AlphaComposite.getInstance(3));
            this.graphicsCtx.setPaint(this.getFill());
            this.graphicsCtx.fill(shape);
            this.graphicsCtx.setComposite(old);
        }
        this.draw(shape);
    }

    protected BasicStroke getStroke() {
        HwmfDrawProperties prop = this.getProperties();
        HwmfPenStyle ps = prop.getPenStyle();
        float width = (float)prop.getPenWidth();
        if (width == 0.0f) {
            width = 1.0f;
        }
        int cap = ps.getLineCap().awtFlag;
        int join = ps.getLineJoin().awtFlag;
        float miterLimit = (float)prop.getPenMiterLimit();
        float[] dashes = ps.getLineDashes();
        boolean dashAlt = ps.isAlternateDash();
        float dashStart = dashAlt && dashes != null && dashes.length > 1 ? dashes[0] : 0.0f;
        return new BasicStroke(width, cap, join, Math.max(1.0f, miterLimit), dashes, dashStart);
    }

    protected Paint getFill() {
        switch (this.getProperties().getBrushStyle()) {
            default: {
                return null;
            }
            case BS_PATTERN: 
            case BS_DIBPATTERN: 
            case BS_DIBPATTERNPT: {
                return this.getPatternPaint();
            }
            case BS_SOLID: {
                return this.getSolidFill();
            }
            case BS_HATCHED: {
                return this.getHatchedFill();
            }
            case BS_LINEAR_GRADIENT: 
        }
        return this.getLinearGradient();
    }

    protected Paint getLinearGradient() {
        return null;
    }

    protected Paint getSolidFill() {
        return this.getProperties().getBrushColor().getColor();
    }

    protected Paint getHatchedFill() {
        HwmfDrawProperties prop = this.getProperties();
        BufferedImage pattern = HwmfGraphics.getPatternFromLong(prop.getBrushHatch().getPattern(), prop.getBackgroundColor().getColor(), prop.getBrushColor().getColor(), prop.getBkMode() == HwmfMisc.WmfSetBkMode.HwmfBkMode.TRANSPARENT);
        return new TexturePaint(pattern, new Rectangle(0, 0, 8, 8));
    }

    public static BufferedImage getPatternFromLong(long patternLng, Color background, Color foreground, boolean hasAlpha) {
        int[] cmap = new int[]{background.getRGB(), foreground.getRGB()};
        IndexColorModel icm = new IndexColorModel(1, 2, cmap, 0, hasAlpha, hasAlpha ? 0 : -1, 0);
        BufferedImage pattern = new BufferedImage(8, 8, 13, icm);
        byte[] pt = new byte[64];
        for (int i = 0; i < pt.length; ++i) {
            pt[i] = (byte)(patternLng >>> i & 1L);
        }
        pattern.getRaster().setDataElements(0, 0, 8, 8, pt);
        return pattern;
    }

    protected Paint getPatternPaint() {
        HwmfDrawProperties prop = this.getProperties();
        ImageRenderer bb = prop.getBrushBitmap();
        if (bb == null) {
            return null;
        }
        Dimension2D dim = bb.getDimension();
        Rectangle2D rect = new Rectangle2D.Double(0.0, 0.0, dim.getWidth(), dim.getHeight());
        rect = prop.getBrushTransform().createTransformedShape(rect).getBounds2D();
        return new TexturePaint(bb.getImage(), rect);
    }

    public void addObjectTableEntry(HwmfObjectTableEntry entry) {
        int objIdx = this.objectIndexes.nextClearBit(0);
        this.objectIndexes.set(objIdx);
        this.objectTable.put(objIdx, entry);
    }

    public void applyObjectTableEntry(int index) {
        HwmfObjectTableEntry ote = this.objectTable.get(index);
        if (ote == null) {
            throw new NoSuchElementException("WMF reference exception - object table entry on index " + index + " was deleted before.");
        }
        ote.applyObject(this);
    }

    public void unsetObjectTableEntry(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        this.objectTable.remove(index);
        this.objectIndexes.clear(index);
    }

    public void saveProperties() {
        HwmfDrawProperties p = this.getProperties();
        assert (p != null);
        p.setTransform(this.graphicsCtx.getTransform());
        p.setClip(this.graphicsCtx.getClip());
        this.propStack.add(p);
        this.prop = this.newProperties(p);
    }

    public void restoreProperties(int index) {
        if (index == 0) {
            return;
        }
        int stackIndex = index;
        if (stackIndex < 0) {
            int curIdx = this.propStack.indexOf(this.getProperties());
            if (curIdx == -1) {
                curIdx = this.propStack.size();
            }
            stackIndex = curIdx + index;
        }
        if (stackIndex == -1) {
            stackIndex = this.propStack.size() - 1;
        }
        for (int i = this.propStack.size() - 1; i >= stackIndex; --i) {
            this.prop = this.propStack.remove(i);
        }
        this.graphicsCtx.setTransform(this.prop.getTransform());
        this.graphicsCtx.setClip(this.prop.getClip());
    }

    public void updateWindowMapMode() {
        Rectangle2D win = this.getProperties().getWindow();
        Rectangle2D view = this.getProperties().getViewport();
        HwmfMapMode mapMode = this.getProperties().getMapMode();
        this.graphicsCtx.setTransform(this.getInitTransform());
        switch (mapMode) {
            default: {
                if (view == null) break;
                this.graphicsCtx.translate(view.getCenterX(), view.getCenterY());
                this.graphicsCtx.scale(view.getWidth() / win.getWidth(), view.getHeight() / win.getHeight());
                this.graphicsCtx.translate(-win.getCenterX(), -win.getCenterY());
                break;
            }
            case MM_ISOTROPIC: {
                this.graphicsCtx.translate(this.bbox.getCenterX(), this.bbox.getCenterY());
                this.graphicsCtx.scale(this.bbox.getWidth() / win.getWidth(), this.bbox.getWidth() / win.getWidth());
                this.graphicsCtx.translate(-win.getCenterX(), -win.getCenterY());
                break;
            }
            case MM_LOMETRIC: 
            case MM_HIMETRIC: 
            case MM_LOENGLISH: 
            case MM_HIENGLISH: 
            case MM_TWIPS: {
                GraphicsConfiguration gc = this.graphicsCtx.getDeviceConfiguration();
                this.graphicsCtx.transform(gc.getNormalizingTransform());
                this.graphicsCtx.scale(1.0 / (double)mapMode.scale, -1.0 / (double)mapMode.scale);
                this.graphicsCtx.translate(-win.getX(), -win.getY());
            }
            case MM_TEXT: 
        }
    }

    public void drawString(byte[] text, int length, Point2D reference) {
        this.drawString(text, length, reference, null, null, null, null, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void drawString(byte[] text, int length, Point2D reference, Dimension2D scale, Rectangle2D clip, HwmfText.WmfExtTextOutOptions opts, List<Integer> dx, boolean isUnicode) {
        HwmfDrawProperties prop = this.getProperties();
        AffineTransform at = this.graphicsCtx.getTransform();
        try {
            at.createInverse();
        }
        catch (NoninvertibleTransformException e) {
            return;
        }
        HwmfFont font = prop.getFont();
        if (font == null || text == null || text.length == 0) {
            return;
        }
        Charset charset = this.getCharset(font, isUnicode);
        String textString = this.trimText(charset, text, length);
        if (textString.isEmpty()) {
            return;
        }
        DrawFontManager fontHandler = DrawFactory.getInstance(this.graphicsCtx).getFontManager(this.graphicsCtx);
        FontInfo fontInfo = fontHandler.getMappedFont(this.graphicsCtx, font);
        textString = fontHandler.mapFontCharset(this.graphicsCtx, fontInfo, textString);
        AttributedString as = new AttributedString(textString);
        this.addAttributes(as::addAttribute, font, fontInfo.getTypeface());
        FontRenderContext frc = this.graphicsCtx.getFontRenderContext();
        this.calculateDx(textString, dx, font, fontInfo, frc, as);
        LineBreakMeasurer lbm = new LineBreakMeasurer(as.getIterator(), frc);
        TextLayout textLayout = lbm.nextLayout(2.14748365E9f);
        double angle = Math.toRadians((double)(-font.getEscapement()) / 10.0);
        boolean useAsianAlign = opts == null && textString.codePoints().anyMatch(Character::isIdeographic) && charset.displayName(Locale.ROOT).startsWith("GB");
        Point2D dst = this.getRotatedOffset(angle, frc, as, useAsianAlign);
        Shape clipShape = this.graphicsCtx.getClip();
        try {
            this.updateClipping(this.graphicsCtx, clip, angle, opts);
            Point2D moveTo = reference.distance(0.0, 0.0) == 0.0 ? prop.getLocation() : reference;
            this.graphicsCtx.translate(moveTo.getX(), moveTo.getY());
            this.graphicsCtx.rotate(angle);
            if (scale != null) {
                this.graphicsCtx.scale(scale.getWidth() < 0.0 ? -1.0 : 1.0, scale.getHeight() < 0.0 ? -1.0 : 1.0);
            }
            this.graphicsCtx.scale(at.getScaleX() < 0.0 ? -1.0 : 1.0, at.getScaleY() < 0.0 ? -1.0 : 1.0);
            this.graphicsCtx.translate(dst.getX(), dst.getY());
            this.graphicsCtx.setColor(prop.getTextColor().getColor());
            this.graphicsCtx.drawString(as.getIterator(), 0, 0);
            AffineTransform atRev = new AffineTransform();
            atRev.translate(-dst.getX(), -dst.getY());
            if (scale != null) {
                atRev.scale(scale.getWidth() < 0.0 ? 1.0 : -1.0, scale.getHeight() < 0.0 ? 1.0 : -1.0);
            }
            atRev.rotate(-angle);
            Point2D.Double deltaX = new Point2D.Double(textLayout.getBounds().getWidth(), 0.0);
            Point2D oldLoc = prop.getLocation();
            prop.setLocation(oldLoc.getX() + ((Point2D)deltaX).getX(), oldLoc.getY() + ((Point2D)deltaX).getY());
        }
        finally {
            this.graphicsCtx.setTransform(at);
            this.graphicsCtx.setClip(clipShape);
        }
    }

    private void calculateDx(String textString, List<Integer> dx, HwmfFont font, FontInfo fontInfo, FontRenderContext frc, AttributedString as) {
        if (dx == null || dx.isEmpty()) {
            return;
        }
        ArrayList<DxLayout> dxList = new ArrayList<DxLayout>();
        HashMap<TextAttribute, Integer> fontAtt = new HashMap<TextAttribute, Integer>();
        this.addAttributes(fontAtt::put, font, fontInfo.getTypeface());
        GlyphVector gv0 = new Font(fontAtt).createGlyphVector(frc, textString);
        fontAtt.put(TextAttribute.TRACKING, 1);
        GlyphVector gv1 = new Font(fontAtt).createGlyphVector(frc, textString);
        int beginIndex = 0;
        for (int offset = 0; offset < dx.size() && beginIndex < textString.length(); ++offset) {
            DxLayout dxLayout = new DxLayout();
            dxLayout.dx = dx.get(offset).intValue();
            dxLayout.pos0 = gv0.getGlyphPosition(offset).getX();
            dxLayout.pos1 = gv1.getGlyphPosition(offset).getX();
            dxLayout.beginIndex = beginIndex;
            dxLayout.endIndex = textString.offsetByCodePoints(beginIndex, 1);
            dxList.add(dxLayout);
            beginIndex = dxLayout.endIndex;
        }
        DxLayout dx0 = null;
        for (DxLayout dx1 : dxList) {
            if (dx0 != null) {
                double y1 = 0.0;
                double x1 = dx1.pos0 - dx0.pos0;
                double y2 = 1.0;
                double x2 = dx1.pos1 - dx0.pos1;
                double track = (y2 - y1) / (x2 - x1) * dx0.dx + (y1 * x2 - y2 * x1) / (x2 - x1);
                as.addAttribute(TextAttribute.TRACKING, Float.valueOf((float)track), dx0.beginIndex, dx0.endIndex);
            }
            dx0 = dx1;
        }
    }

    private void addAttributes(BiConsumer<TextAttribute, Object> attributes, HwmfFont font, String typeface) {
        HashMap<TextAttribute, Object> att = new HashMap<TextAttribute, Object>();
        att.put(TextAttribute.FAMILY, typeface);
        att.put(TextAttribute.SIZE, this.getFontHeight(font));
        if (font.isStrikeOut()) {
            att.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        if (font.isUnderline()) {
            att.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }
        if (font.isItalic()) {
            att.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        int fw = font.getWeight();
        Float awtFW = TextAttribute.WEIGHT_REGULAR;
        for (int i = 0; i < WEIGHT_MAP.length; i += 2) {
            if (!((float)fw >= WEIGHT_MAP[i].floatValue())) continue;
            awtFW = WEIGHT_MAP[i + 1];
            break;
        }
        att.put(TextAttribute.WEIGHT, awtFW);
        att.put(TextAttribute.FONT, new Font(att));
        att.forEach(attributes);
    }

    private double getFontHeight(HwmfFont font) {
        double fontHeight = font.getHeight();
        if (fontHeight == 0.0) {
            return 12.0;
        }
        if (fontHeight < 0.0) {
            return -fontHeight;
        }
        return fontHeight * 3.0 / 4.0;
    }

    private Charset getCharset(HwmfFont font, boolean isUnicode) {
        if (isUnicode) {
            return StandardCharsets.UTF_16LE;
        }
        FontCharset fc = font.getCharset();
        if (fc == FontCharset.DEFAULT) {
            return this.charsetProvider.get();
        }
        Charset charset = fc.getCharset();
        return charset == null ? this.charsetProvider.get() : charset;
    }

    @Override
    public void setCharsetProvider(Supplier<Charset> provider) {
        this.charsetProvider = provider;
    }

    private String trimText(Charset charset, byte[] text, int length) {
        int trimLen;
        for (trimLen = 0; trimLen < text.length; trimLen += 2) {
            if (trimLen == text.length - 1) {
                if (text[trimLen] == 0) break;
                ++trimLen;
                break;
            }
            if (text[trimLen] == -1 && text[trimLen + 1] == -1 || (text[trimLen] & 0xE0) == 0 && text[trimLen + 1] == 0) break;
        }
        String textString = new String(text, 0, trimLen, charset);
        return textString.substring(0, Math.min(textString.length(), length));
    }

    private void updateHorizontalAlign(AffineTransform tx, TextLayout layout, boolean useAsianAlign) {
        switch (this.prop.getTextAlignLatin()) {
            default: {
                break;
            }
            case CENTER: {
                tx.translate(-layout.getBounds().getWidth() / 2.0, 0.0);
                break;
            }
            case RIGHT: {
                tx.translate(-layout.getAdvance(), 0.0);
            }
        }
    }

    private void updateVerticalAlign(AffineTransform tx, TextLayout layout, boolean useAsianAlign) {
        switch (useAsianAlign ? this.prop.getTextVAlignAsian() : this.prop.getTextVAlignLatin()) {
            case TOP: {
                tx.translate(0.0, layout.getAscent());
                break;
            }
            default: {
                break;
            }
            case BOTTOM: {
                tx.translate(0.0, -(layout.getBounds().getHeight() - (double)layout.getDescent()));
            }
        }
    }

    private void updateClipping(Graphics2D graphicsCtx, Rectangle2D clip, double angle, HwmfText.WmfExtTextOutOptions opts) {
        if (clip == null || clip.getBounds2D().isEmpty()) {
            return;
        }
        AffineTransform at = graphicsCtx.getTransform();
        graphicsCtx.translate(-clip.getCenterX(), -clip.getCenterY());
        graphicsCtx.rotate(angle);
        graphicsCtx.translate(clip.getCenterX(), clip.getCenterY());
        if (this.prop.getBkMode() == HwmfMisc.WmfSetBkMode.HwmfBkMode.OPAQUE && opts.isOpaque()) {
            graphicsCtx.setPaint(this.prop.getBackgroundColor().getColor());
            graphicsCtx.fill(clip);
        }
        if (opts.isClipped()) {
            graphicsCtx.setClip(clip);
        }
        graphicsCtx.setTransform(at);
    }

    private Point2D getRotatedOffset(double angle, FontRenderContext frc, AttributedString as, boolean useAsianAlign) {
        TextLayout layout = new TextLayout(as.getIterator(), frc);
        AffineTransform tx = new AffineTransform();
        this.updateHorizontalAlign(tx, layout, useAsianAlign);
        this.updateVerticalAlign(tx, layout, useAsianAlign);
        tx.rotate(angle);
        Point2D.Double src = new Point2D.Double();
        return tx.transform(src, null);
    }

    public void drawImage(BufferedImage img, Rectangle2D srcBounds, Rectangle2D dstBounds) {
        this.drawImage(new BufferedImageRenderer(img), srcBounds, dstBounds);
    }

    public void drawImage(ImageRenderer img, Rectangle2D srcBounds, Rectangle2D dstBounds) {
        if (srcBounds.isEmpty()) {
            return;
        }
        HwmfDrawProperties prop = this.getProperties();
        switch (prop.getRasterOp3()) {
            case D: {
                break;
            }
            case PATCOPY: {
                this.graphicsCtx.setPaint(this.getFill());
                this.graphicsCtx.fill(dstBounds);
                break;
            }
            case BLACKNESS: {
                this.graphicsCtx.setPaint(Color.BLACK);
                this.graphicsCtx.fill(dstBounds);
                break;
            }
            case WHITENESS: {
                this.graphicsCtx.setPaint(Color.WHITE);
                this.graphicsCtx.fill(dstBounds);
                break;
            }
            default: {
                int newComp;
                if (img == null) {
                    return;
                }
                Shape oldClip = this.graphicsCtx.getClip();
                AffineTransform oldTrans = this.graphicsCtx.getTransform();
                Rectangle2D normBounds = HwmfGraphics.normalizeRect(dstBounds);
                if (prop.getBkMode() == HwmfMisc.WmfSetBkMode.HwmfBkMode.OPAQUE) {
                    Paint oldPaint = this.graphicsCtx.getPaint();
                    this.graphicsCtx.setPaint(prop.getBackgroundColor().getColor());
                    this.graphicsCtx.fill(dstBounds);
                    this.graphicsCtx.setPaint(oldPaint);
                }
                this.graphicsCtx.translate(normBounds.getCenterX(), normBounds.getCenterY());
                this.graphicsCtx.scale(Math.signum(dstBounds.getWidth()), Math.signum(dstBounds.getHeight()));
                this.graphicsCtx.translate(-normBounds.getCenterX(), -normBounds.getCenterY());
                Composite old = this.graphicsCtx.getComposite();
                switch (prop.getRasterOp3()) {
                    default: {
                        newComp = 3;
                        break;
                    }
                    case SRCINVERT: {
                        newComp = 5;
                        break;
                    }
                    case SRCAND: {
                        newComp = 2;
                    }
                }
                this.graphicsCtx.setComposite(AlphaComposite.getInstance(newComp));
                boolean useDeviceBounds = img instanceof HwmfImageRenderer;
                img.drawImage(this.graphicsCtx, normBounds, HwmfGraphics.getSubImageInsets(srcBounds, useDeviceBounds ? img.getNativeBounds() : img.getBounds()));
                this.graphicsCtx.setComposite(old);
                this.graphicsCtx.setTransform(oldTrans);
                this.graphicsCtx.setClip(oldClip);
            }
        }
    }

    private static Rectangle2D normalizeRect(Rectangle2D dstBounds) {
        return new Rectangle2D.Double(dstBounds.getWidth() >= 0.0 ? dstBounds.getMinX() : dstBounds.getMaxX(), dstBounds.getHeight() >= 0.0 ? dstBounds.getMinY() : dstBounds.getMaxY(), Math.abs(dstBounds.getWidth()), Math.abs(dstBounds.getHeight()));
    }

    private static Insets getSubImageInsets(Rectangle2D srcBounds, Rectangle2D nativeBounds) {
        int left = (int)Math.round((srcBounds.getX() - nativeBounds.getX()) / nativeBounds.getWidth() * 100000.0);
        int top = (int)Math.round((srcBounds.getY() - nativeBounds.getY()) / nativeBounds.getHeight() * 100000.0);
        int right = (int)Math.round((nativeBounds.getMaxX() - srcBounds.getMaxX()) / nativeBounds.getWidth() * 100000.0);
        int bottom = (int)Math.round((nativeBounds.getMaxY() - srcBounds.getMaxY()) / nativeBounds.getHeight() * 100000.0);
        return new Insets(top, left, bottom, right);
    }

    public AffineTransform getInitTransform() {
        return new AffineTransform(this.initialAT);
    }

    public AffineTransform getTransform() {
        return new AffineTransform(this.graphicsCtx.getTransform());
    }

    public void setTransform(AffineTransform tx) {
        this.graphicsCtx.setTransform(tx);
    }

    public void setClip(Shape clip, HwmfRegionMode regionMode, boolean useInitialAT) {
        Shape newClip;
        Shape oldClip;
        AffineTransform at = this.graphicsCtx.getTransform();
        if (useInitialAT) {
            this.graphicsCtx.setTransform(this.getInitTransform());
        }
        if (!Objects.equals(oldClip = this.graphicsCtx.getClip(), newClip = regionMode.applyOp(oldClip, clip))) {
            this.graphicsCtx.setClip(newClip);
        }
        if (useInitialAT) {
            this.graphicsCtx.setTransform(at);
        }
        this.prop.setClip(this.graphicsCtx.getClip());
    }

    public ImageRenderer getImageRenderer(String contentType) {
        return DrawPictureShape.getImageRenderer(this.graphicsCtx, contentType);
    }

    @Internal
    static class BufferedImageRenderer
    extends BitmapImageRenderer {
        public BufferedImageRenderer(BufferedImage img) {
            this.img = img;
        }
    }

    private static class DxLayout {
        double dx;
        double pos0;
        double pos1;
        int beginIndex;
        int endIndex;

        private DxLayout() {
        }
    }

    public static enum FillDrawStyle {
        NONE(FillDrawStyle::fillNone),
        FILL(HwmfGraphics::fill),
        DRAW(HwmfGraphics::draw),
        FILL_DRAW(FillDrawStyle::fillDraw);

        public final BiConsumer<HwmfGraphics, Shape> handler;

        private FillDrawStyle(BiConsumer<HwmfGraphics, Shape> handler) {
            this.handler = handler;
        }

        private static void fillNone(HwmfGraphics g, Shape s) {
        }

        private static void fillDraw(HwmfGraphics g, Shape s) {
            g.fill(s);
            g.draw(s);
        }
    }
}

