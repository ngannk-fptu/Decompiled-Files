/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.draw;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfBinaryRasterOp;
import org.apache.poi.hwmf.record.HwmfBrushStyle;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfFill;
import org.apache.poi.hwmf.record.HwmfFont;
import org.apache.poi.hwmf.record.HwmfHatchStyle;
import org.apache.poi.hwmf.record.HwmfMapMode;
import org.apache.poi.hwmf.record.HwmfMisc;
import org.apache.poi.hwmf.record.HwmfPalette;
import org.apache.poi.hwmf.record.HwmfPenStyle;
import org.apache.poi.hwmf.record.HwmfTernaryRasterOp;
import org.apache.poi.hwmf.record.HwmfText;
import org.apache.poi.sl.draw.ImageRenderer;

public class HwmfDrawProperties {
    private final Rectangle2D window;
    private Rectangle2D viewport;
    private final Point2D location;
    private HwmfMapMode mapMode;
    private HwmfColorRef backgroundColor;
    private HwmfBrushStyle brushStyle;
    private HwmfColorRef brushColor;
    private HwmfHatchStyle brushHatch;
    private ImageRenderer brushBitmap;
    private final AffineTransform brushTransform = new AffineTransform();
    private double penWidth;
    private HwmfPenStyle penStyle;
    private HwmfColorRef penColor;
    private double penMiterLimit;
    private HwmfMisc.WmfSetBkMode.HwmfBkMode bkMode;
    private HwmfFill.WmfSetPolyfillMode.HwmfPolyfillMode polyfillMode;
    private Shape region;
    private List<HwmfPalette.PaletteEntry> palette;
    private int paletteOffset;
    private HwmfFont font;
    private HwmfColorRef textColor;
    private HwmfText.HwmfTextAlignment textAlignLatin;
    private HwmfText.HwmfTextVerticalAlignment textVAlignLatin;
    private HwmfText.HwmfTextAlignment textAlignAsian;
    private HwmfText.HwmfTextVerticalAlignment textVAlignAsian;
    private HwmfBinaryRasterOp rasterOp2;
    private HwmfTernaryRasterOp rasterOp3;
    protected Shape clip;
    protected final AffineTransform transform = new AffineTransform();

    public HwmfDrawProperties() {
        this.window = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
        this.viewport = null;
        this.location = new Point2D.Double(0.0, 0.0);
        this.mapMode = HwmfMapMode.MM_ANISOTROPIC;
        this.backgroundColor = new HwmfColorRef(Color.BLACK);
        this.brushStyle = HwmfBrushStyle.BS_SOLID;
        this.brushColor = new HwmfColorRef(Color.BLACK);
        this.brushHatch = HwmfHatchStyle.HS_HORIZONTAL;
        this.penWidth = 1.0;
        this.penStyle = HwmfPenStyle.valueOf(0);
        this.penColor = new HwmfColorRef(Color.BLACK);
        this.penMiterLimit = 10.0;
        this.bkMode = HwmfMisc.WmfSetBkMode.HwmfBkMode.OPAQUE;
        this.polyfillMode = HwmfFill.WmfSetPolyfillMode.HwmfPolyfillMode.WINDING;
        this.textColor = new HwmfColorRef(Color.BLACK);
        this.textAlignLatin = HwmfText.HwmfTextAlignment.LEFT;
        this.textVAlignLatin = HwmfText.HwmfTextVerticalAlignment.TOP;
        this.textAlignAsian = HwmfText.HwmfTextAlignment.RIGHT;
        this.textVAlignAsian = HwmfText.HwmfTextVerticalAlignment.TOP;
        this.rasterOp2 = HwmfBinaryRasterOp.R2_COPYPEN;
        this.rasterOp3 = null;
        this.clip = null;
        this.font = new HwmfFont();
        this.font.initDefaults();
    }

    public HwmfDrawProperties(HwmfDrawProperties other) {
        this.window = other.window == null ? null : (Rectangle2D)other.window.clone();
        this.viewport = other.viewport == null ? null : (Rectangle2D)other.viewport.clone();
        this.location = (Point2D)other.location.clone();
        this.mapMode = other.mapMode;
        this.backgroundColor = other.backgroundColor == null ? null : other.backgroundColor.copy();
        this.brushStyle = other.brushStyle;
        this.brushColor = other.brushColor.copy();
        this.brushHatch = other.brushHatch;
        this.brushBitmap = other.brushBitmap;
        this.brushTransform.setTransform(other.brushTransform);
        this.penWidth = other.penWidth;
        this.penStyle = other.penStyle == null ? null : other.penStyle.copy();
        this.penColor = other.penColor == null ? null : other.penColor.copy();
        this.penMiterLimit = other.penMiterLimit;
        this.bkMode = other.bkMode;
        this.polyfillMode = other.polyfillMode;
        if (other.region instanceof Rectangle2D) {
            this.region = other.region.getBounds2D();
        } else if (other.region instanceof Area) {
            this.region = new Area(other.region);
        }
        this.palette = other.palette;
        this.paletteOffset = other.paletteOffset;
        this.font = other.font;
        this.textColor = other.textColor == null ? null : other.textColor.copy();
        this.textAlignLatin = other.textAlignLatin;
        this.textVAlignLatin = other.textVAlignLatin;
        this.textAlignAsian = other.textAlignAsian;
        this.textVAlignAsian = other.textVAlignAsian;
        this.rasterOp2 = other.rasterOp2;
        this.rasterOp3 = other.rasterOp3;
        this.transform.setTransform(other.transform);
        this.clip = other.clip;
    }

    public void setViewportExt(double width, double height) {
        if (this.viewport == null) {
            this.viewport = (Rectangle2D)this.window.clone();
        }
        double x = this.viewport.getX();
        double y = this.viewport.getY();
        double w = width != 0.0 ? width : this.viewport.getWidth();
        double h = height != 0.0 ? height : this.viewport.getHeight();
        this.viewport.setRect(x, y, w, h);
    }

    public void setViewportOrg(double x, double y) {
        if (this.viewport == null) {
            this.viewport = (Rectangle2D)this.window.clone();
        }
        double w = this.viewport.getWidth();
        double h = this.viewport.getHeight();
        this.viewport.setRect(x, y, w, h);
    }

    public Rectangle2D getViewport() {
        return this.viewport == null ? null : (Rectangle2D)this.viewport.clone();
    }

    public void setWindowExt(double width, double height) {
        double x = this.window.getX();
        double y = this.window.getY();
        double w = width != 0.0 ? width : this.window.getWidth();
        double h = height != 0.0 ? height : this.window.getHeight();
        this.window.setRect(x, y, w, h);
    }

    public void setWindowOrg(double x, double y) {
        double w = this.window.getWidth();
        double h = this.window.getHeight();
        this.window.setRect(x, y, w, h);
    }

    public Rectangle2D getWindow() {
        return (Rectangle2D)this.window.clone();
    }

    public void setLocation(double x, double y) {
        this.location.setLocation(x, y);
    }

    public void setLocation(Point2D point) {
        this.location.setLocation(point);
    }

    public Point2D getLocation() {
        return (Point2D)this.location.clone();
    }

    public void setMapMode(HwmfMapMode mapMode) {
        this.mapMode = mapMode;
    }

    public HwmfMapMode getMapMode() {
        return this.mapMode;
    }

    public HwmfBrushStyle getBrushStyle() {
        return this.brushStyle;
    }

    public void setBrushStyle(HwmfBrushStyle brushStyle) {
        this.brushStyle = brushStyle;
    }

    public HwmfHatchStyle getBrushHatch() {
        return this.brushHatch;
    }

    public void setBrushHatch(HwmfHatchStyle brushHatch) {
        this.brushHatch = brushHatch;
    }

    public HwmfColorRef getBrushColor() {
        return this.brushColor;
    }

    public void setBrushColor(HwmfColorRef brushColor) {
        this.brushColor = brushColor;
    }

    public HwmfMisc.WmfSetBkMode.HwmfBkMode getBkMode() {
        return this.bkMode;
    }

    public void setBkMode(HwmfMisc.WmfSetBkMode.HwmfBkMode bkMode) {
        this.bkMode = bkMode;
    }

    public HwmfPenStyle getPenStyle() {
        return this.penStyle;
    }

    public void setPenStyle(HwmfPenStyle penStyle) {
        this.penStyle = penStyle;
    }

    public HwmfColorRef getPenColor() {
        return this.penColor;
    }

    public void setPenColor(HwmfColorRef penColor) {
        this.penColor = penColor;
    }

    public double getPenWidth() {
        return this.penWidth;
    }

    public void setPenWidth(double penWidth) {
        this.penWidth = penWidth;
    }

    public double getPenMiterLimit() {
        return this.penMiterLimit;
    }

    public void setPenMiterLimit(double penMiterLimit) {
        this.penMiterLimit = penMiterLimit;
    }

    public HwmfColorRef getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(HwmfColorRef backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public HwmfFill.WmfSetPolyfillMode.HwmfPolyfillMode getPolyfillMode() {
        return this.polyfillMode;
    }

    public void setPolyfillMode(HwmfFill.WmfSetPolyfillMode.HwmfPolyfillMode polyfillMode) {
        this.polyfillMode = polyfillMode;
    }

    public ImageRenderer getBrushBitmap() {
        return this.brushBitmap;
    }

    public void setBrushBitmap(ImageRenderer brushBitmap) {
        this.brushBitmap = brushBitmap;
    }

    public void setBrushBitmap(BufferedImage brushBitmap) {
        this.brushBitmap = brushBitmap == null ? null : new HwmfGraphics.BufferedImageRenderer(brushBitmap);
    }

    public Shape getRegion() {
        return this.region;
    }

    public void setRegion(Shape region) {
        this.region = region;
    }

    public List<HwmfPalette.PaletteEntry> getPalette() {
        return this.palette;
    }

    public void setPalette(List<HwmfPalette.PaletteEntry> palette) {
        this.palette = palette;
    }

    public int getPaletteOffset() {
        return this.paletteOffset;
    }

    public void setPaletteOffset(int paletteOffset) {
        this.paletteOffset = paletteOffset;
    }

    public HwmfColorRef getTextColor() {
        return this.textColor;
    }

    public void setTextColor(HwmfColorRef textColor) {
        this.textColor = textColor;
    }

    public HwmfFont getFont() {
        return this.font;
    }

    public void setFont(HwmfFont font) {
        this.font = font;
    }

    public HwmfText.HwmfTextAlignment getTextAlignLatin() {
        return this.textAlignLatin;
    }

    public void setTextAlignLatin(HwmfText.HwmfTextAlignment textAlignLatin) {
        this.textAlignLatin = textAlignLatin;
    }

    public HwmfText.HwmfTextVerticalAlignment getTextVAlignLatin() {
        return this.textVAlignLatin;
    }

    public void setTextVAlignLatin(HwmfText.HwmfTextVerticalAlignment textVAlignLatin) {
        this.textVAlignLatin = textVAlignLatin;
    }

    public HwmfText.HwmfTextAlignment getTextAlignAsian() {
        return this.textAlignAsian;
    }

    public void setTextAlignAsian(HwmfText.HwmfTextAlignment textAlignAsian) {
        this.textAlignAsian = textAlignAsian;
    }

    public HwmfText.HwmfTextVerticalAlignment getTextVAlignAsian() {
        return this.textVAlignAsian;
    }

    public void setTextVAlignAsian(HwmfText.HwmfTextVerticalAlignment textVAlignAsian) {
        this.textVAlignAsian = textVAlignAsian;
    }

    public int getWindingRule() {
        return this.getPolyfillMode().awtFlag;
    }

    public HwmfTernaryRasterOp getRasterOp3() {
        return this.rasterOp3;
    }

    public void setRasterOp3(HwmfTernaryRasterOp rasterOp3) {
        this.rasterOp3 = rasterOp3;
    }

    public AffineTransform getTransform() {
        return this.transform;
    }

    public void setTransform(AffineTransform transform) {
        this.transform.setTransform(transform);
    }

    public Shape getClip() {
        return this.clip;
    }

    public void setClip(Shape clip) {
        this.clip = clip;
    }

    public AffineTransform getBrushTransform() {
        return this.brushTransform;
    }

    public void setBrushTransform(AffineTransform brushTransform) {
        if (brushTransform == null) {
            this.brushTransform.setToIdentity();
        } else {
            this.brushTransform.setTransform(brushTransform);
        }
    }

    public HwmfBinaryRasterOp getRasterOp2() {
        return this.rasterOp2;
    }

    public void setRasterOp2(HwmfBinaryRasterOp rasterOp2) {
        this.rasterOp2 = rasterOp2;
    }
}

