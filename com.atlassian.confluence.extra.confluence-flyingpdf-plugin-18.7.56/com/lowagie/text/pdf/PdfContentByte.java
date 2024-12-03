/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Annotation;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgJBIG2;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.exceptions.IllegalPdfSyntaxException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.ColorDetails;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.FontDetails;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PatternColor;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfLayerMembership;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPSXObject;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfPrinterGraphics2D;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTextArray;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.ShadingColor;
import com.lowagie.text.pdf.SpotColor;
import com.lowagie.text.pdf.internal.PdfAnnotationsImp;
import com.lowagie.text.pdf.internal.PdfXConformanceImp;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PdfContentByte {
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 2;
    public static final int LINE_CAP_BUTT = 0;
    public static final int LINE_CAP_ROUND = 1;
    public static final int LINE_CAP_PROJECTING_SQUARE = 2;
    public static final int LINE_JOIN_MITER = 0;
    public static final int LINE_JOIN_ROUND = 1;
    public static final int LINE_JOIN_BEVEL = 2;
    public static final int TEXT_RENDER_MODE_FILL = 0;
    public static final int TEXT_RENDER_MODE_STROKE = 1;
    public static final int TEXT_RENDER_MODE_FILL_STROKE = 2;
    public static final int TEXT_RENDER_MODE_INVISIBLE = 3;
    public static final int TEXT_RENDER_MODE_FILL_CLIP = 4;
    public static final int TEXT_RENDER_MODE_STROKE_CLIP = 5;
    public static final int TEXT_RENDER_MODE_FILL_STROKE_CLIP = 6;
    public static final int TEXT_RENDER_MODE_CLIP = 7;
    static final float MIN_FONT_SIZE = 1.0E-4f;
    private static final float[] unitRect = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
    protected ByteBuffer content = new ByteBuffer();
    protected PdfWriter writer;
    protected PdfDocument pdf;
    protected GraphicState state = new GraphicState();
    private static Map<PdfName, String> abrev = new HashMap<PdfName, String>();
    protected List<GraphicState> stateList = new ArrayList<GraphicState>();
    protected int separator = 10;
    private int mcDepth = 0;
    private boolean inText = false;
    protected List<Integer> layerDepth;

    public PdfContentByte(PdfWriter wr) {
        if (wr != null) {
            this.writer = wr;
            this.pdf = this.writer.getPdfDocument();
        }
    }

    public String toString() {
        return this.content.toString();
    }

    public ByteBuffer getInternalBuffer() {
        return this.content;
    }

    public byte[] toPdf(PdfWriter writer) {
        this.sanityCheck();
        return this.content.toByteArray();
    }

    public void add(PdfContentByte other) {
        if (other.writer != null && this.writer != other.writer) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("inconsistent.writers.are.you.mixing.two.documents"));
        }
        this.content.append(other.content);
    }

    public float getXTLM() {
        return this.state.xTLM;
    }

    public float getYTLM() {
        return this.state.yTLM;
    }

    public float getLeading() {
        return this.state.leading;
    }

    public float getCharacterSpacing() {
        return this.state.charSpace;
    }

    public float getWordSpacing() {
        return this.state.wordSpace;
    }

    public float getHorizontalScaling() {
        return this.state.scale;
    }

    public void setFlatness(float flatness) {
        if (flatness >= 0.0f && flatness <= 100.0f) {
            this.content.append(flatness).append(" i").append_i(this.separator);
        }
    }

    public void setLineCap(int style) {
        if (style >= 0 && style <= 2) {
            this.content.append(style).append(" J").append_i(this.separator);
        }
    }

    public void setLineDash(float phase) {
        this.content.append("[] ").append(phase).append(" d").append_i(this.separator);
    }

    public void setLineDash(float unitsOn, float phase) {
        this.content.append("[").append(unitsOn).append("] ").append(phase).append(" d").append_i(this.separator);
    }

    public void setLineDash(float unitsOn, float unitsOff, float phase) {
        this.content.append("[").append(unitsOn).append(' ').append(unitsOff).append("] ").append(phase).append(" d").append_i(this.separator);
    }

    public final void setLineDash(float[] array, float phase) {
        this.content.append("[");
        for (int i = 0; i < array.length; ++i) {
            this.content.append(array[i]);
            if (i >= array.length - 1) continue;
            this.content.append(' ');
        }
        this.content.append("] ").append(phase).append(" d").append_i(this.separator);
    }

    public void setLineJoin(int style) {
        if (style >= 0 && style <= 2) {
            this.content.append(style).append(" j").append_i(this.separator);
        }
    }

    public void setLineWidth(float w) {
        this.content.append(w).append(" w").append_i(this.separator);
    }

    public void setMiterLimit(float miterLimit) {
        if (miterLimit > 1.0f) {
            this.content.append(miterLimit).append(" M").append_i(this.separator);
        }
    }

    public void clip() {
        this.content.append("W").append_i(this.separator);
    }

    public void eoClip() {
        this.content.append("W*").append_i(this.separator);
    }

    public void setGrayFill(float gray) {
        this.content.append(gray).append(" g").append_i(this.separator);
    }

    public void resetGrayFill() {
        this.content.append("0 g").append_i(this.separator);
    }

    public void setGrayStroke(float gray) {
        this.content.append(gray).append(" G").append_i(this.separator);
    }

    public void resetGrayStroke() {
        this.content.append("0 G").append_i(this.separator);
    }

    private void HelperRGB(float red, float green, float blue) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 3, null);
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 1.0f) {
            red = 1.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 1.0f) {
            green = 1.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 1.0f) {
            blue = 1.0f;
        }
        this.content.append(red).append(' ').append(green).append(' ').append(blue);
    }

    public void setRGBColorFillF(float red, float green, float blue) {
        this.HelperRGB(red, green, blue);
        this.content.append(" rg").append_i(this.separator);
    }

    public void resetRGBColorFill() {
        this.content.append("0 g").append_i(this.separator);
    }

    public void setRGBColorStrokeF(float red, float green, float blue) {
        this.HelperRGB(red, green, blue);
        this.content.append(" RG").append_i(this.separator);
    }

    public void resetRGBColorStroke() {
        this.content.append("0 G").append_i(this.separator);
    }

    private void HelperCMYK(float cyan, float magenta, float yellow, float black) {
        if (cyan < 0.0f) {
            cyan = 0.0f;
        } else if (cyan > 1.0f) {
            cyan = 1.0f;
        }
        if (magenta < 0.0f) {
            magenta = 0.0f;
        } else if (magenta > 1.0f) {
            magenta = 1.0f;
        }
        if (yellow < 0.0f) {
            yellow = 0.0f;
        } else if (yellow > 1.0f) {
            yellow = 1.0f;
        }
        if (black < 0.0f) {
            black = 0.0f;
        } else if (black > 1.0f) {
            black = 1.0f;
        }
        this.content.append(cyan).append(' ').append(magenta).append(' ').append(yellow).append(' ').append(black);
    }

    public void setCMYKColorFillF(float cyan, float magenta, float yellow, float black) {
        this.HelperCMYK(cyan, magenta, yellow, black);
        this.content.append(" k").append_i(this.separator);
    }

    public void resetCMYKColorFill() {
        this.content.append("0 0 0 1 k").append_i(this.separator);
    }

    public void setCMYKColorStrokeF(float cyan, float magenta, float yellow, float black) {
        this.HelperCMYK(cyan, magenta, yellow, black);
        this.content.append(" K").append_i(this.separator);
    }

    public void resetCMYKColorStroke() {
        this.content.append("0 0 0 1 K").append_i(this.separator);
    }

    public void moveTo(float x, float y) {
        this.content.append(x).append(' ').append(y).append(" m").append_i(this.separator);
    }

    public void lineTo(float x, float y) {
        this.content.append(x).append(' ').append(y).append(" l").append_i(this.separator);
    }

    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.content.append(x1).append(' ').append(y1).append(' ').append(x2).append(' ').append(y2).append(' ').append(x3).append(' ').append(y3).append(" c").append_i(this.separator);
    }

    public void curveTo(float x2, float y2, float x3, float y3) {
        this.content.append(x2).append(' ').append(y2).append(' ').append(x3).append(' ').append(y3).append(" v").append_i(this.separator);
    }

    public void curveFromTo(float x1, float y1, float x3, float y3) {
        this.content.append(x1).append(' ').append(y1).append(' ').append(x3).append(' ').append(y3).append(" y").append_i(this.separator);
    }

    public void circle(float x, float y, float r) {
        float b = 0.5523f;
        this.moveTo(x + r, y);
        this.curveTo(x + r, y + r * b, x + r * b, y + r, x, y + r);
        this.curveTo(x - r * b, y + r, x - r, y + r * b, x - r, y);
        this.curveTo(x - r, y - r * b, x - r * b, y - r, x, y - r);
        this.curveTo(x + r * b, y - r, x + r, y - r * b, x + r, y);
    }

    public void rectangle(float x, float y, float w, float h) {
        this.content.append(x).append(' ').append(y).append(' ').append(w).append(' ').append(h).append(" re").append_i(this.separator);
    }

    private boolean compareColors(Color c1, Color c2) {
        if (c1 == null && c2 == null) {
            return true;
        }
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1 instanceof ExtendedColor) {
            return c1.equals(c2);
        }
        return c2.equals(c1);
    }

    public void variableRectangle(Rectangle rect) {
        boolean bb;
        boolean bt;
        float t = rect.getTop();
        float b = rect.getBottom();
        float r = rect.getRight();
        float l = rect.getLeft();
        float wt = rect.getBorderWidthTop();
        float wb = rect.getBorderWidthBottom();
        float wr = rect.getBorderWidthRight();
        float wl = rect.getBorderWidthLeft();
        Color ct = rect.getBorderColorTop();
        Color cb = rect.getBorderColorBottom();
        Color cr = rect.getBorderColorRight();
        Color cl = rect.getBorderColorLeft();
        this.saveState();
        this.setLineCap(0);
        this.setLineJoin(0);
        float clw = 0.0f;
        boolean cdef = false;
        Color ccol = null;
        boolean cdefi = false;
        Color cfil = null;
        if (wt > 0.0f) {
            clw = wt;
            this.setLineWidth(clw);
            cdef = true;
            if (ct == null) {
                this.resetRGBColorStroke();
            } else {
                this.setColorStroke(ct);
            }
            ccol = ct;
            this.moveTo(l, t - wt / 2.0f);
            this.lineTo(r, t - wt / 2.0f);
            this.stroke();
        }
        if (wb > 0.0f) {
            if (wb != clw) {
                clw = wb;
                this.setLineWidth(clw);
            }
            if (!cdef || !this.compareColors(ccol, cb)) {
                cdef = true;
                if (cb == null) {
                    this.resetRGBColorStroke();
                } else {
                    this.setColorStroke(cb);
                }
                ccol = cb;
            }
            this.moveTo(r, b + wb / 2.0f);
            this.lineTo(l, b + wb / 2.0f);
            this.stroke();
        }
        if (wr > 0.0f) {
            if (wr != clw) {
                clw = wr;
                this.setLineWidth(clw);
            }
            if (!cdef || !this.compareColors(ccol, cr)) {
                cdef = true;
                if (cr == null) {
                    this.resetRGBColorStroke();
                } else {
                    this.setColorStroke(cr);
                }
                ccol = cr;
            }
            bt = this.compareColors(ct, cr);
            bb = this.compareColors(cb, cr);
            this.moveTo(r - wr / 2.0f, bt ? t : t - wt);
            this.lineTo(r - wr / 2.0f, bb ? b : b + wb);
            this.stroke();
            if (!bt || !bb) {
                cdefi = true;
                if (cr == null) {
                    this.resetRGBColorFill();
                } else {
                    this.setColorFill(cr);
                }
                cfil = cr;
                if (!bt) {
                    this.moveTo(r, t);
                    this.lineTo(r, t - wt);
                    this.lineTo(r - wr, t - wt);
                    this.fill();
                }
                if (!bb) {
                    this.moveTo(r, b);
                    this.lineTo(r, b + wb);
                    this.lineTo(r - wr, b + wb);
                    this.fill();
                }
            }
        }
        if (wl > 0.0f) {
            if (wl != clw) {
                this.setLineWidth(wl);
            }
            if (!cdef || !this.compareColors(ccol, cl)) {
                if (cl == null) {
                    this.resetRGBColorStroke();
                } else {
                    this.setColorStroke(cl);
                }
            }
            bt = this.compareColors(ct, cl);
            bb = this.compareColors(cb, cl);
            this.moveTo(l + wl / 2.0f, bt ? t : t - wt);
            this.lineTo(l + wl / 2.0f, bb ? b : b + wb);
            this.stroke();
            if (!bt || !bb) {
                if (!cdefi || !this.compareColors(cfil, cl)) {
                    if (cl == null) {
                        this.resetRGBColorFill();
                    } else {
                        this.setColorFill(cl);
                    }
                }
                if (!bt) {
                    this.moveTo(l, t);
                    this.lineTo(l, t - wt);
                    this.lineTo(l + wl, t - wt);
                    this.fill();
                }
                if (!bb) {
                    this.moveTo(l, b);
                    this.lineTo(l, b + wb);
                    this.lineTo(l + wl, b + wb);
                    this.fill();
                }
            }
        }
        this.restoreState();
    }

    public void rectangle(Rectangle rectangle) {
        float x1 = rectangle.getLeft();
        float y1 = rectangle.getBottom();
        float x2 = rectangle.getRight();
        float y2 = rectangle.getTop();
        Color background = rectangle.getBackgroundColor();
        if (background != null) {
            this.saveState();
            this.setColorFill(background);
            this.rectangle(x1, y1, x2 - x1, y2 - y1);
            this.fill();
            this.restoreState();
        }
        if (!rectangle.hasBorders()) {
            return;
        }
        if (rectangle.isUseVariableBorders()) {
            this.variableRectangle(rectangle);
        } else {
            Color color;
            if (rectangle.getBorderWidth() != -1.0f) {
                this.setLineWidth(rectangle.getBorderWidth());
            }
            if ((color = rectangle.getBorderColor()) != null) {
                this.setColorStroke(color);
            }
            if (rectangle.hasBorder(15)) {
                this.rectangle(x1, y1, x2 - x1, y2 - y1);
            } else {
                if (rectangle.hasBorder(8)) {
                    this.moveTo(x2, y1);
                    this.lineTo(x2, y2);
                }
                if (rectangle.hasBorder(4)) {
                    this.moveTo(x1, y1);
                    this.lineTo(x1, y2);
                }
                if (rectangle.hasBorder(2)) {
                    this.moveTo(x1, y1);
                    this.lineTo(x2, y1);
                }
                if (rectangle.hasBorder(1)) {
                    this.moveTo(x1, y2);
                    this.lineTo(x2, y2);
                }
            }
            this.stroke();
            if (color != null) {
                this.resetRGBColorStroke();
            }
        }
    }

    public void closePath() {
        this.content.append("h").append_i(this.separator);
    }

    public void newPath() {
        this.content.append("n").append_i(this.separator);
    }

    public void stroke() {
        this.content.append("S").append_i(this.separator);
    }

    public void closePathStroke() {
        this.content.append("s").append_i(this.separator);
    }

    public void fill() {
        this.content.append("f").append_i(this.separator);
    }

    public void eoFill() {
        this.content.append("f*").append_i(this.separator);
    }

    public void fillStroke() {
        this.content.append("B").append_i(this.separator);
    }

    public void closePathFillStroke() {
        this.content.append("b").append_i(this.separator);
    }

    public void eoFillStroke() {
        this.content.append("B*").append_i(this.separator);
    }

    public void closePathEoFillStroke() {
        this.content.append("b*").append_i(this.separator);
    }

    public void addImage(Image image) throws DocumentException {
        this.addImage(image, false);
    }

    public void addImage(Image image, boolean inlineImage) throws DocumentException {
        if (!image.hasAbsoluteY()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.image.must.have.absolute.positioning"));
        }
        float[] matrix = image.matrix();
        matrix[4] = image.getAbsoluteX() - matrix[4];
        matrix[5] = image.getAbsoluteY() - matrix[5];
        this.addImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5], inlineImage);
    }

    public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
        this.addImage(image, a, b, c, d, e, f, false);
    }

    public void addImage(Image image, float a, float b, float c, float d, float e, float f, boolean inlineImage) throws DocumentException {
        try {
            Annotation annot;
            if (image.getLayer() != null) {
                this.beginLayer(image.getLayer());
            }
            if (image.isImgTemplate()) {
                this.writer.addDirectImageSimple(image);
                PdfTemplate template = image.getTemplateData();
                float w = template.getWidth();
                float h = template.getHeight();
                this.addTemplate(template, a / w, b / w, c / h, d / h, e, f);
            } else {
                this.content.append("q ");
                this.content.append(a).append(' ');
                this.content.append(b).append(' ');
                this.content.append(c).append(' ');
                this.content.append(d).append(' ');
                this.content.append(e).append(' ');
                this.content.append(f).append(" cm");
                if (inlineImage) {
                    Object globals;
                    this.content.append("\nBI\n");
                    PdfImage pimage = new PdfImage(image, "", null);
                    if (image instanceof ImgJBIG2 && (globals = ((ImgJBIG2)image).getGlobalBytes()) != null) {
                        PdfDictionary decodeparms = new PdfDictionary();
                        decodeparms.put(PdfName.JBIG2GLOBALS, this.writer.getReferenceJBIG2Globals((byte[])globals));
                        pimage.put(PdfName.DECODEPARMS, decodeparms);
                    }
                    globals = pimage.getKeys().iterator();
                    while (globals.hasNext()) {
                        PdfArray ar;
                        PdfName key = (PdfName)globals.next();
                        PdfObject value = pimage.get(key);
                        String s = abrev.get(key);
                        if (s == null) continue;
                        this.content.append(s);
                        boolean check = true;
                        if (key.equals(PdfName.COLORSPACE) && value.isArray() && (ar = (PdfArray)value).size() == 4 && PdfName.INDEXED.equals(ar.getAsName(0)) && ar.getPdfObject(1).isName() && ar.getPdfObject(2).isNumber() && ar.getPdfObject(3).isString()) {
                            check = false;
                        }
                        if (check && key.equals(PdfName.COLORSPACE) && !value.isName()) {
                            PdfName cs = this.writer.getColorspaceName();
                            PageResources prs = this.getPageResources();
                            prs.addColor(cs, this.writer.addToBody(value).getIndirectReference());
                            value = cs;
                        }
                        value.toPdf(null, this.content);
                        this.content.append('\n');
                    }
                    this.content.append("ID\n");
                    pimage.writeContent(this.content);
                    this.content.append("\nEI\nQ").append_i(this.separator);
                } else {
                    PdfName name;
                    PageResources prs = this.getPageResources();
                    Image maskImage = image.getImageMask();
                    if (maskImage != null) {
                        name = this.writer.addDirectImageSimple(maskImage);
                        prs.addXObject(name, this.writer.getImageReference(name));
                    }
                    name = this.writer.addDirectImageSimple(image);
                    name = prs.addXObject(name, this.writer.getImageReference(name));
                    this.content.append(' ').append(name.getBytes()).append(" Do Q").append_i(this.separator);
                }
            }
            if (image.hasBorders()) {
                this.saveState();
                float w = image.getWidth();
                float h = image.getHeight();
                this.concatCTM(a / w, b / w, c / h, d / h, e, f);
                this.rectangle(image);
                this.restoreState();
            }
            if (image.getLayer() != null) {
                this.endLayer();
            }
            if ((annot = image.getAnnotation()) == null) {
                return;
            }
            float[] r = new float[unitRect.length];
            for (int k = 0; k < unitRect.length; k += 2) {
                r[k] = a * unitRect[k] + c * unitRect[k + 1] + e;
                r[k + 1] = b * unitRect[k] + d * unitRect[k + 1] + f;
            }
            float llx = r[0];
            float lly = r[1];
            float urx = llx;
            float ury = lly;
            for (int k = 2; k < r.length; k += 2) {
                llx = Math.min(llx, r[k]);
                lly = Math.min(lly, r[k + 1]);
                urx = Math.max(urx, r[k]);
                ury = Math.max(ury, r[k + 1]);
            }
            annot = new Annotation(annot);
            annot.setDimensions(llx, lly, urx, ury);
            PdfAnnotation an = PdfAnnotationsImp.convertAnnotation(this.writer, annot, new Rectangle(llx, lly, urx, ury));
            if (an == null) {
                return;
            }
            this.addAnnotation(an);
        }
        catch (Exception ee) {
            throw new DocumentException(ee);
        }
    }

    public void reset() {
        this.reset(true);
    }

    public void reset(boolean validateContent) {
        this.content.reset();
        if (validateContent) {
            this.sanityCheck();
        }
        this.state = new GraphicState();
    }

    public void beginText() {
        if (this.inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        this.inText = true;
        this.state.xTLM = 0.0f;
        this.state.yTLM = 0.0f;
        this.content.append("BT").append_i(this.separator);
    }

    public void endText() {
        if (!this.inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        this.inText = false;
        this.content.append("ET").append_i(this.separator);
    }

    public void saveState() {
        this.content.append("q").append_i(this.separator);
        this.stateList.add(new GraphicState(this.state));
    }

    public void restoreState() {
        this.content.append("Q").append_i(this.separator);
        int idx = this.stateList.size() - 1;
        if (idx < 0) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.save.restore.state.operators"));
        }
        this.state = this.stateList.get(idx);
        this.stateList.remove(idx);
    }

    public void setCharacterSpacing(float charSpace) {
        this.state.charSpace = charSpace;
        this.content.append(charSpace).append(" Tc").append_i(this.separator);
    }

    public void setWordSpacing(float wordSpace) {
        this.state.wordSpace = wordSpace;
        this.content.append(wordSpace).append(" Tw").append_i(this.separator);
    }

    public void setHorizontalScaling(float scale) {
        this.state.scale = scale;
        this.content.append(scale).append(" Tz").append_i(this.separator);
    }

    public void setLeading(float leading) {
        this.state.leading = leading;
        this.content.append(leading).append(" TL").append_i(this.separator);
    }

    public void setFontAndSize(BaseFont bf, float size) {
        this.checkWriter();
        if (size < 1.0E-4f && size > -1.0E-4f) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("font.size.too.small.1", String.valueOf(size)));
        }
        this.state.size = size;
        this.state.fontDetails = this.writer.addSimple(bf);
        PageResources prs = this.getPageResources();
        PdfName name = this.state.fontDetails.getFontName();
        name = prs.addFont(name, this.state.fontDetails.getIndirectReference());
        this.content.append(name.getBytes()).append(' ').append(size).append(" Tf").append_i(this.separator);
    }

    public void setTextRenderingMode(int rendering) {
        this.content.append(rendering).append(" Tr").append_i(this.separator);
    }

    public void setTextRise(float rise) {
        this.content.append(rise).append(" Ts").append_i(this.separator);
    }

    private void showText2(String text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        }
        byte[] b = this.state.fontDetails.convertToBytes(text);
        PdfContentByte.escapeString(b, this.content);
    }

    public void showText(String text) {
        this.showText2(text);
        this.content.append("Tj").append_i(this.separator);
    }

    public void showText(GlyphVector glyphVector) {
        byte[] b = this.state.fontDetails.convertToBytes(glyphVector);
        PdfContentByte.escapeString(b, this.content);
        this.content.append("Tj").append_i(this.separator);
    }

    public static PdfTextArray getKernArray(String text, BaseFont font) {
        PdfTextArray pa = new PdfTextArray();
        StringBuilder acc = new StringBuilder();
        int len = text.length() - 1;
        char[] c = text.toCharArray();
        if (len >= 0) {
            acc.append(c, 0, 1);
        }
        for (int k = 0; k < len; ++k) {
            char c2 = c[k + 1];
            int kern = font.getKerning(c[k], c2);
            if (kern == 0) {
                acc.append(c2);
                continue;
            }
            pa.add(acc.toString());
            acc.setLength(0);
            acc.append(c, k + 1, 1);
            pa.add(-kern);
        }
        pa.add(acc.toString());
        return pa;
    }

    public void showTextKerned(String text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        }
        BaseFont bf = this.state.fontDetails.getBaseFont();
        if (bf.hasKernPairs()) {
            this.showText(PdfContentByte.getKernArray(text, bf));
        } else {
            this.showText(text);
        }
    }

    public void newlineShowText(String text) {
        this.state.yTLM -= this.state.leading;
        this.showText2(text);
        this.content.append("'").append_i(this.separator);
    }

    public void newlineShowText(float wordSpacing, float charSpacing, String text) {
        this.state.yTLM -= this.state.leading;
        this.content.append(wordSpacing).append(' ').append(charSpacing);
        this.showText2(text);
        this.content.append("\"").append_i(this.separator);
        this.state.charSpace = charSpacing;
        this.state.wordSpace = wordSpacing;
    }

    public void setTextMatrix(float a, float b, float c, float d, float x, float y) {
        this.state.xTLM = x;
        this.state.yTLM = y;
        this.content.append(a).append(' ').append(b).append_i(32).append(c).append_i(32).append(d).append_i(32).append(x).append_i(32).append(y).append(" Tm").append_i(this.separator);
    }

    public void setTextMatrix(float x, float y) {
        this.setTextMatrix(1.0f, 0.0f, 0.0f, 1.0f, x, y);
    }

    public void moveText(float x, float y) {
        this.state.xTLM += x;
        this.state.yTLM += y;
        this.content.append(x).append(' ').append(y).append(" Td").append_i(this.separator);
    }

    public void moveTextWithLeading(float x, float y) {
        this.state.xTLM += x;
        this.state.yTLM += y;
        this.state.leading = -y;
        this.content.append(x).append(' ').append(y).append(" TD").append_i(this.separator);
    }

    public void newlineText() {
        this.state.yTLM -= this.state.leading;
        this.content.append("T*").append_i(this.separator);
    }

    int size() {
        return this.content.size();
    }

    static byte[] escapeString(byte[] b) {
        ByteBuffer content = new ByteBuffer();
        PdfContentByte.escapeString(b, content);
        return content.toByteArray();
    }

    static void escapeString(byte[] b, ByteBuffer content) {
        content.append_i(40);
        block8: for (byte c : b) {
            switch (c) {
                case 13: {
                    content.append("\\r");
                    continue block8;
                }
                case 10: {
                    content.append("\\n");
                    continue block8;
                }
                case 9: {
                    content.append("\\t");
                    continue block8;
                }
                case 8: {
                    content.append("\\b");
                    continue block8;
                }
                case 12: {
                    content.append("\\f");
                    continue block8;
                }
                case 40: 
                case 41: 
                case 92: {
                    content.append_i(92).append_i(c);
                    continue block8;
                }
                default: {
                    content.append_i(c);
                }
            }
        }
        content.append(")");
    }

    public void addOutline(PdfOutline outline, String name) {
        this.checkWriter();
        this.pdf.addOutline(outline, name);
    }

    public PdfOutline getRootOutline() {
        this.checkWriter();
        return this.pdf.getRootOutline();
    }

    public float getEffectiveStringWidth(String text, boolean kerned) {
        BaseFont bf = this.state.fontDetails.getBaseFont();
        float w = kerned ? bf.getWidthPointKerned(text, this.state.size) : bf.getWidthPoint(text, this.state.size);
        if (this.state.charSpace != 0.0f && text.length() > 1) {
            w += this.state.charSpace * (float)(text.length() - 1);
        }
        int ft = bf.getFontType();
        if (this.state.wordSpace != 0.0f && (ft == 0 || ft == 1 || ft == 5)) {
            for (int i = 0; i < text.length() - 1; ++i) {
                if (text.charAt(i) != ' ') continue;
                w += this.state.wordSpace;
            }
        }
        if ((double)this.state.scale != 100.0) {
            w = w * this.state.scale / 100.0f;
        }
        return w;
    }

    public void showTextAligned(int alignment, String text, float x, float y, float rotation) {
        this.showTextAligned(alignment, text, x, y, rotation, false);
    }

    private void showTextAligned(int alignment, String text, float x, float y, float rotation, boolean kerned) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        }
        if (rotation == 0.0f) {
            switch (alignment) {
                case 1: {
                    x -= this.getEffectiveStringWidth(text, kerned) / 2.0f;
                    break;
                }
                case 2: {
                    x -= this.getEffectiveStringWidth(text, kerned);
                }
            }
            this.setTextMatrix(x, y);
            if (kerned) {
                this.showTextKerned(text);
            } else {
                this.showText(text);
            }
        } else {
            double alpha = (double)rotation * Math.PI / 180.0;
            float cos = (float)Math.cos(alpha);
            float sin = (float)Math.sin(alpha);
            switch (alignment) {
                case 1: {
                    float len = this.getEffectiveStringWidth(text, kerned) / 2.0f;
                    x -= len * cos;
                    y -= len * sin;
                    break;
                }
                case 2: {
                    float len = this.getEffectiveStringWidth(text, kerned);
                    x -= len * cos;
                    y -= len * sin;
                }
            }
            this.setTextMatrix(cos, sin, -sin, cos, x, y);
            if (kerned) {
                this.showTextKerned(text);
            } else {
                this.showText(text);
            }
            this.setTextMatrix(0.0f, 0.0f);
        }
    }

    public void showTextAlignedKerned(int alignment, String text, float x, float y, float rotation) {
        this.showTextAligned(alignment, text, x, y, rotation, true);
    }

    public void concatCTM(float a, float b, float c, float d, float e, float f) {
        this.content.append(a).append(' ').append(b).append(' ').append(c).append(' ');
        this.content.append(d).append(' ').append(e).append(' ').append(f).append(" cm").append_i(this.separator);
    }

    public static List<float[]> bezierArc(float x1, float y1, float x2, float y2, float startAng, float extent) {
        int Nfrag;
        float fragAngle;
        float tmp;
        if (x1 > x2) {
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y2 > y1) {
            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        if (Math.abs(extent) <= 90.0f) {
            fragAngle = extent;
            Nfrag = 1;
        } else {
            Nfrag = (int)Math.ceil(Math.abs(extent) / 90.0f);
            fragAngle = extent / (float)Nfrag;
        }
        float x_cen = (x1 + x2) / 2.0f;
        float y_cen = (y1 + y2) / 2.0f;
        float rx = (x2 - x1) / 2.0f;
        float ry = (y2 - y1) / 2.0f;
        float halfAng = (float)((double)fragAngle * Math.PI / 360.0);
        float kappa = (float)Math.abs(1.3333333333333333 * (1.0 - Math.cos(halfAng)) / Math.sin(halfAng));
        ArrayList<float[]> pointList = new ArrayList<float[]>();
        for (int i = 0; i < Nfrag; ++i) {
            float theta0 = (float)((double)(startAng + (float)i * fragAngle) * Math.PI / 180.0);
            float theta1 = (float)((double)(startAng + (float)(i + 1) * fragAngle) * Math.PI / 180.0);
            float cos0 = (float)Math.cos(theta0);
            float cos1 = (float)Math.cos(theta1);
            float sin0 = (float)Math.sin(theta0);
            float sin1 = (float)Math.sin(theta1);
            if (fragAngle > 0.0f) {
                pointList.add(new float[]{x_cen + rx * cos0, y_cen - ry * sin0, x_cen + rx * (cos0 - kappa * sin0), y_cen - ry * (sin0 + kappa * cos0), x_cen + rx * (cos1 + kappa * sin1), y_cen - ry * (sin1 - kappa * cos1), x_cen + rx * cos1, y_cen - ry * sin1});
                continue;
            }
            pointList.add(new float[]{x_cen + rx * cos0, y_cen - ry * sin0, x_cen + rx * (cos0 + kappa * sin0), y_cen - ry * (sin0 - kappa * cos0), x_cen + rx * (cos1 - kappa * sin1), y_cen - ry * (sin1 + kappa * cos1), x_cen + rx * cos1, y_cen - ry * sin1});
        }
        return pointList;
    }

    public void arc(float x1, float y1, float x2, float y2, float startAng, float extent) {
        List<float[]> ar = PdfContentByte.bezierArc(x1, y1, x2, y2, startAng, extent);
        if (ar.isEmpty()) {
            return;
        }
        float[] pt = ar.get(0);
        this.moveTo(pt[0], pt[1]);
        Iterator<float[]> iterator = ar.iterator();
        while (iterator.hasNext()) {
            float[] anAr;
            pt = anAr = iterator.next();
            this.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
        }
    }

    public void ellipse(float x1, float y1, float x2, float y2) {
        this.arc(x1, y1, x2, y2, 0.0f, 360.0f);
    }

    public PdfPatternPainter createPattern(float width, float height, float xstep, float ystep) {
        this.checkWriter();
        if (xstep == 0.0f || ystep == 0.0f) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("xstep.or.ystep.can.not.be.zero"));
        }
        PdfPatternPainter painter = new PdfPatternPainter(this.writer);
        painter.setWidth(width);
        painter.setHeight(height);
        painter.setXStep(xstep);
        painter.setYStep(ystep);
        this.writer.addSimplePattern(painter);
        return painter;
    }

    public PdfPatternPainter createPattern(float width, float height) {
        return this.createPattern(width, height, width, height);
    }

    public PdfPatternPainter createPattern(float width, float height, float xstep, float ystep, Color color) {
        this.checkWriter();
        if (xstep == 0.0f || ystep == 0.0f) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("xstep.or.ystep.can.not.be.zero"));
        }
        PdfPatternPainter painter = new PdfPatternPainter(this.writer, color);
        painter.setWidth(width);
        painter.setHeight(height);
        painter.setXStep(xstep);
        painter.setYStep(ystep);
        this.writer.addSimplePattern(painter);
        return painter;
    }

    public PdfPatternPainter createPattern(float width, float height, Color color) {
        return this.createPattern(width, height, width, height, color);
    }

    public PdfTemplate createTemplate(float width, float height) {
        return this.createTemplate(width, height, null);
    }

    PdfTemplate createTemplate(float width, float height, PdfName forcedName) {
        this.checkWriter();
        PdfTemplate template = new PdfTemplate(this.writer);
        template.setWidth(width);
        template.setHeight(height);
        this.writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }

    public PdfAppearance createAppearance(float width, float height) {
        return this.createAppearance(width, height, null);
    }

    PdfAppearance createAppearance(float width, float height, PdfName forcedName) {
        this.checkWriter();
        PdfAppearance template = new PdfAppearance(this.writer);
        template.setWidth(width);
        template.setHeight(height);
        this.writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }

    public void addPSXObject(PdfPSXObject psobject) {
        this.checkWriter();
        PdfName name = this.writer.addDirectTemplateSimple(psobject, null);
        PageResources prs = this.getPageResources();
        name = prs.addXObject(name, psobject.getIndirectReference());
        this.content.append(name.getBytes()).append(" Do").append_i(this.separator);
    }

    public void addTemplate(PdfTemplate template, float a, float b, float c, float d, float e, float f) {
        this.checkWriter();
        this.checkNoPattern(template);
        PdfName name = this.writer.addDirectTemplateSimple(template, null);
        PageResources prs = this.getPageResources();
        name = prs.addXObject(name, template.getIndirectReference());
        this.content.append("q ");
        this.content.append(a).append(' ');
        this.content.append(b).append(' ');
        this.content.append(c).append(' ');
        this.content.append(d).append(' ');
        this.content.append(e).append(' ');
        this.content.append(f).append(" cm ");
        this.content.append(name.getBytes()).append(" Do Q").append_i(this.separator);
    }

    void addTemplateReference(PdfIndirectReference template, PdfName name, float a, float b, float c, float d, float e, float f) {
        this.checkWriter();
        PageResources prs = this.getPageResources();
        name = prs.addXObject(name, template);
        this.content.append("q ");
        this.content.append(a).append(' ');
        this.content.append(b).append(' ');
        this.content.append(c).append(' ');
        this.content.append(d).append(' ');
        this.content.append(e).append(' ');
        this.content.append(f).append(" cm ");
        this.content.append(name.getBytes()).append(" Do Q").append_i(this.separator);
    }

    public void addTemplate(PdfTemplate template, float x, float y) {
        this.addTemplate(template, 1.0f, 0.0f, 0.0f, 1.0f, x, y);
    }

    public void setCMYKColorFill(int cyan, int magenta, int yellow, int black) {
        this.content.append((float)(cyan & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(magenta & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(yellow & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(black & 0xFF) / 255.0f);
        this.content.append(" k").append_i(this.separator);
    }

    public void setCMYKColorStroke(int cyan, int magenta, int yellow, int black) {
        this.content.append((float)(cyan & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(magenta & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(yellow & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(black & 0xFF) / 255.0f);
        this.content.append(" K").append_i(this.separator);
    }

    public void setRGBColorFill(int red, int green, int blue) {
        this.HelperRGB((float)(red & 0xFF) / 255.0f, (float)(green & 0xFF) / 255.0f, (float)(blue & 0xFF) / 255.0f);
        this.content.append(" rg").append_i(this.separator);
    }

    public void setRGBColorStroke(int red, int green, int blue) {
        this.HelperRGB((float)(red & 0xFF) / 255.0f, (float)(green & 0xFF) / 255.0f, (float)(blue & 0xFF) / 255.0f);
        this.content.append(" RG").append_i(this.separator);
    }

    public void setColorStroke(Color color) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 1: {
                this.setGrayStroke(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                CMYKColor cmyk = (CMYKColor)color;
                this.setCMYKColorStrokeF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
                break;
            }
            case 3: {
                SpotColor spot = (SpotColor)color;
                this.setColorStroke(spot.getPdfSpotColor(), spot.getTint());
                break;
            }
            case 4: {
                PatternColor pat = (PatternColor)color;
                this.setPatternStroke(pat.getPainter());
                break;
            }
            case 5: {
                ShadingColor shading = (ShadingColor)color;
                this.setShadingStroke(shading.getPdfShadingPattern());
                break;
            }
            default: {
                this.setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
            }
        }
    }

    public void setColorFill(Color color) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 1: {
                this.setGrayFill(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                CMYKColor cmyk = (CMYKColor)color;
                this.setCMYKColorFillF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
                break;
            }
            case 3: {
                SpotColor spot = (SpotColor)color;
                this.setColorFill(spot.getPdfSpotColor(), spot.getTint());
                break;
            }
            case 4: {
                PatternColor pat = (PatternColor)color;
                this.setPatternFill(pat.getPainter());
                break;
            }
            case 5: {
                ShadingColor shading = (ShadingColor)color;
                this.setShadingFill(shading.getPdfShadingPattern());
                break;
            }
            default: {
                this.setRGBColorFill(color.getRed(), color.getGreen(), color.getBlue());
            }
        }
    }

    public void setColorFill(PdfSpotColor sp, float tint) {
        this.checkWriter();
        this.state.colorDetails = this.writer.addSimple(sp);
        PageResources prs = this.getPageResources();
        PdfName name = this.state.colorDetails.getColorName();
        name = prs.addColor(name, this.state.colorDetails.getIndirectReference());
        this.content.append(name.getBytes()).append(" cs ").append(tint).append(" scn").append_i(this.separator);
    }

    public void setColorStroke(PdfSpotColor sp, float tint) {
        this.checkWriter();
        this.state.colorDetails = this.writer.addSimple(sp);
        PageResources prs = this.getPageResources();
        PdfName name = this.state.colorDetails.getColorName();
        name = prs.addColor(name, this.state.colorDetails.getIndirectReference());
        this.content.append(name.getBytes()).append(" CS ").append(tint).append(" SCN").append_i(this.separator);
    }

    public void setPatternFill(PdfPatternPainter p) {
        if (p.isStencil()) {
            this.setPatternFill(p, p.getDefaultColor());
            return;
        }
        this.checkWriter();
        PageResources prs = this.getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(this.separator);
    }

    void outputColorNumbers(Color color, float tint) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 0: {
                this.content.append((float)color.getRed() / 255.0f);
                this.content.append(' ');
                this.content.append((float)color.getGreen() / 255.0f);
                this.content.append(' ');
                this.content.append((float)color.getBlue() / 255.0f);
                break;
            }
            case 1: {
                this.content.append(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                CMYKColor cmyk = (CMYKColor)color;
                this.content.append(cmyk.getCyan()).append(' ').append(cmyk.getMagenta());
                this.content.append(' ').append(cmyk.getYellow()).append(' ').append(cmyk.getBlack());
                break;
            }
            case 3: {
                this.content.append(tint);
                break;
            }
            default: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.color.type"));
            }
        }
    }

    public void setPatternFill(PdfPatternPainter p, Color color) {
        if (ExtendedColor.getType(color) == 3) {
            this.setPatternFill(p, color, ((SpotColor)color).getTint());
        } else {
            this.setPatternFill(p, color, 0.0f);
        }
    }

    public void setPatternFill(PdfPatternPainter p, Color color, float tint) {
        this.checkWriter();
        if (!p.isStencil()) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.pattern.was.expected"));
        }
        PageResources prs = this.getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        ColorDetails csDetail = this.writer.addSimplePatternColorspace(color);
        PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
        this.content.append(cName.getBytes()).append(" cs").append_i(this.separator);
        this.outputColorNumbers(color, tint);
        this.content.append(' ').append(name.getBytes()).append(" scn").append_i(this.separator);
    }

    public void setPatternStroke(PdfPatternPainter p, Color color) {
        if (ExtendedColor.getType(color) == 3) {
            this.setPatternStroke(p, color, ((SpotColor)color).getTint());
        } else {
            this.setPatternStroke(p, color, 0.0f);
        }
    }

    public void setPatternStroke(PdfPatternPainter p, Color color, float tint) {
        this.checkWriter();
        if (!p.isStencil()) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.pattern.was.expected"));
        }
        PageResources prs = this.getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        ColorDetails csDetail = this.writer.addSimplePatternColorspace(color);
        PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
        this.content.append(cName.getBytes()).append(" CS").append_i(this.separator);
        this.outputColorNumbers(color, tint);
        this.content.append(' ').append(name.getBytes()).append(" SCN").append_i(this.separator);
    }

    public void setPatternStroke(PdfPatternPainter p) {
        if (p.isStencil()) {
            this.setPatternStroke(p, p.getDefaultColor());
            return;
        }
        this.checkWriter();
        PageResources prs = this.getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(this.separator);
    }

    public void paintShading(PdfShading shading) {
        this.writer.addSimpleShading(shading);
        PageResources prs = this.getPageResources();
        PdfName name = prs.addShading(shading.getShadingName(), shading.getShadingReference());
        this.content.append(name.getBytes()).append(" sh").append_i(this.separator);
        ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }

    public void paintShading(PdfShadingPattern shading) {
        this.paintShading(shading.getShading());
    }

    public void setShadingFill(PdfShadingPattern shading) {
        this.writer.addSimpleShadingPattern(shading);
        PageResources prs = this.getPageResources();
        PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(this.separator);
        ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }

    public void setShadingStroke(PdfShadingPattern shading) {
        this.writer.addSimpleShadingPattern(shading);
        PageResources prs = this.getPageResources();
        PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(this.separator);
        ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }

    protected void checkWriter() {
        if (this.writer == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("the.writer.in.pdfcontentbyte.is.null"));
        }
    }

    public void showText(PdfTextArray text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        }
        this.content.append("[");
        List arrayList = text.getArrayList();
        boolean lastWasNumber = false;
        for (Object obj : arrayList) {
            if (obj instanceof String) {
                this.showText2((String)obj);
                lastWasNumber = false;
                continue;
            }
            if (lastWasNumber) {
                this.content.append(' ');
            } else {
                lastWasNumber = true;
            }
            this.content.append(((Float)obj).floatValue());
        }
        this.content.append("]TJ").append_i(this.separator);
    }

    public PdfWriter getPdfWriter() {
        return this.writer;
    }

    public PdfDocument getPdfDocument() {
        return this.pdf;
    }

    public void localGoto(String name, float llx, float lly, float urx, float ury) {
        this.pdf.localGoto(name, llx, lly, urx, ury);
    }

    public boolean localDestination(String name, PdfDestination destination) {
        return this.pdf.localDestination(name, destination);
    }

    public PdfContentByte getDuplicate() {
        return new PdfContentByte(this.writer);
    }

    public void remoteGoto(String filename, String name, float llx, float lly, float urx, float ury) {
        this.pdf.remoteGoto(filename, name, llx, lly, urx, ury);
    }

    public void remoteGoto(String filename, int page, float llx, float lly, float urx, float ury) {
        this.pdf.remoteGoto(filename, page, llx, lly, urx, ury);
    }

    public void roundRectangle(float x, float y, float w, float h, float r) {
        if (w < 0.0f) {
            x += w;
            w = -w;
        }
        if (h < 0.0f) {
            y += h;
            h = -h;
        }
        if (r < 0.0f) {
            r = -r;
        }
        float b = 0.4477f;
        this.moveTo(x + r, y);
        this.lineTo(x + w - r, y);
        this.curveTo(x + w - r * b, y, x + w, y + r * b, x + w, y + r);
        this.lineTo(x + w, y + h - r);
        this.curveTo(x + w, y + h - r * b, x + w - r * b, y + h, x + w - r, y + h);
        this.lineTo(x + r, y + h);
        this.curveTo(x + r * b, y + h, x, y + h - r * b, x, y + h - r);
        this.lineTo(x, y + r);
        this.curveTo(x, y + r * b, x + r * b, y, x + r, y);
    }

    public void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
        this.pdf.setAction(action, llx, lly, urx, ury);
    }

    public void setLiteral(String s) {
        this.content.append(s);
    }

    public void setLiteral(char c) {
        this.content.append(c);
    }

    public void setLiteral(float n) {
        this.content.append(n);
    }

    void checkNoPattern(PdfTemplate t) {
        if (t.getType() == 3) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.use.of.a.pattern.a.template.was.expected"));
        }
    }

    public void drawRadioField(float llx, float lly, float urx, float ury, boolean on) {
        if (llx > urx) {
            float x = llx;
            llx = urx;
            urx = x;
        }
        if (lly > ury) {
            float y = lly;
            lly = ury;
            ury = y;
        }
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(192, 192, 192));
        this.arc(llx + 1.0f, lly + 1.0f, urx - 1.0f, ury - 1.0f, 0.0f, 360.0f);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(160, 160, 160));
        this.arc(llx + 0.5f, lly + 0.5f, urx - 0.5f, ury - 0.5f, 45.0f, 180.0f);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(0, 0, 0));
        this.arc(llx + 1.5f, lly + 1.5f, urx - 1.5f, ury - 1.5f, 45.0f, 180.0f);
        this.stroke();
        if (on) {
            this.setLineWidth(1.0f);
            this.setLineCap(1);
            this.setColorFill(new Color(0, 0, 0));
            this.arc(llx + 4.0f, lly + 4.0f, urx - 4.0f, ury - 4.0f, 0.0f, 360.0f);
            this.fill();
        }
    }

    public void drawTextField(float llx, float lly, float urx, float ury) {
        if (llx > urx) {
            float x = llx;
            llx = urx;
            urx = x;
        }
        if (lly > ury) {
            float y = lly;
            lly = ury;
            ury = y;
        }
        this.setColorStroke(new Color(192, 192, 192));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.rectangle(llx, lly, urx - llx, ury - lly);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.setColorFill(new Color(255, 255, 255));
        this.rectangle(llx + 0.5f, lly + 0.5f, urx - llx - 1.0f, ury - lly - 1.0f);
        this.fill();
        this.setColorStroke(new Color(192, 192, 192));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 1.0f, lly + 1.5f);
        this.lineTo(urx - 1.5f, lly + 1.5f);
        this.lineTo(urx - 1.5f, ury - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(160, 160, 160));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 1.0f, lly + 1.0f);
        this.lineTo(llx + 1.0f, ury - 1.0f);
        this.lineTo(urx - 1.0f, ury - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(0, 0, 0));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 2.0f, lly + 2.0f);
        this.lineTo(llx + 2.0f, ury - 2.0f);
        this.lineTo(urx - 2.0f, ury - 2.0f);
        this.stroke();
    }

    public void drawButton(float llx, float lly, float urx, float ury, String text, BaseFont bf, float size) {
        if (llx > urx) {
            float x = llx;
            llx = urx;
            urx = x;
        }
        if (lly > ury) {
            float y = lly;
            lly = ury;
            ury = y;
        }
        this.setColorStroke(new Color(0, 0, 0));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.rectangle(llx, lly, urx - llx, ury - lly);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.setColorFill(new Color(192, 192, 192));
        this.rectangle(llx + 0.5f, lly + 0.5f, urx - llx - 1.0f, ury - lly - 1.0f);
        this.fill();
        this.setColorStroke(new Color(255, 255, 255));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 1.0f, lly + 1.0f);
        this.lineTo(llx + 1.0f, ury - 1.0f);
        this.lineTo(urx - 1.0f, ury - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(160, 160, 160));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 1.0f, lly + 1.0f);
        this.lineTo(urx - 1.0f, lly + 1.0f);
        this.lineTo(urx - 1.0f, ury - 1.0f);
        this.stroke();
        this.resetRGBColorFill();
        this.beginText();
        this.setFontAndSize(bf, size);
        this.showTextAligned(1, text, llx + (urx - llx) / 2.0f, lly + (ury - lly - size) / 2.0f, 0.0f);
        this.endText();
    }

    public Graphics2D createGraphicsShapes(float width, float height) {
        return new PdfGraphics2D(this, width, height, null, true, false, 0.0f);
    }

    public Graphics2D createPrinterGraphicsShapes(float width, float height, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, null, true, false, 0.0f, printerJob);
    }

    public Graphics2D createGraphics(float width, float height) {
        return new PdfGraphics2D(this, width, height, null, false, false, 0.0f);
    }

    public Graphics2D createPrinterGraphics(float width, float height, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, null, false, false, 0.0f, printerJob);
    }

    public Graphics2D createGraphics(float width, float height, boolean convertImagesToJPEG, float quality) {
        return new PdfGraphics2D(this, width, height, null, false, convertImagesToJPEG, quality);
    }

    public Graphics2D createPrinterGraphics(float width, float height, boolean convertImagesToJPEG, float quality, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, null, false, convertImagesToJPEG, quality, printerJob);
    }

    public Graphics2D createGraphicsShapes(float width, float height, boolean convertImagesToJPEG, float quality) {
        return new PdfGraphics2D(this, width, height, null, true, convertImagesToJPEG, quality);
    }

    public Graphics2D createPrinterGraphicsShapes(float width, float height, boolean convertImagesToJPEG, float quality, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, null, true, convertImagesToJPEG, quality, printerJob);
    }

    public Graphics2D createGraphics(float width, float height, FontMapper fontMapper) {
        return new PdfGraphics2D(this, width, height, fontMapper, false, false, 0.0f);
    }

    public Graphics2D createPrinterGraphics(float width, float height, FontMapper fontMapper, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, fontMapper, false, false, 0.0f, printerJob);
    }

    public Graphics2D createGraphics(float width, float height, FontMapper fontMapper, boolean convertImagesToJPEG, float quality) {
        return new PdfGraphics2D(this, width, height, fontMapper, false, convertImagesToJPEG, quality);
    }

    public Graphics2D createPrinterGraphics(float width, float height, FontMapper fontMapper, boolean convertImagesToJPEG, float quality, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, fontMapper, false, convertImagesToJPEG, quality, printerJob);
    }

    PageResources getPageResources() {
        return this.pdf.getPageResources();
    }

    public void setGState(PdfGState gstate) {
        PdfObject[] obj = this.writer.addSimpleExtGState(gstate);
        PageResources prs = this.getPageResources();
        PdfName name = prs.addExtGState((PdfName)obj[0], (PdfIndirectReference)obj[1]);
        this.content.append(name.getBytes()).append(" gs").append_i(this.separator);
    }

    public void beginLayer(PdfOCG layer) {
        if (layer instanceof PdfLayer && ((PdfLayer)layer).getTitle() != null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.title.is.not.a.layer"));
        }
        if (this.layerDepth == null) {
            this.layerDepth = new ArrayList<Integer>();
        }
        if (layer instanceof PdfLayerMembership) {
            this.layerDepth.add(1);
            this.beginLayer2(layer);
            return;
        }
        int n = 0;
        for (PdfLayer la = (PdfLayer)layer; la != null; la = la.getParent()) {
            if (la.getTitle() != null) continue;
            this.beginLayer2(la);
            ++n;
        }
        this.layerDepth.add(n);
    }

    private void beginLayer2(PdfOCG layer) {
        PdfName name = (PdfName)this.writer.addSimpleProperty(layer, layer.getRef())[0];
        PageResources prs = this.getPageResources();
        name = prs.addProperty(name, layer.getRef());
        this.content.append("/OC ").append(name.getBytes()).append(" BDC").append_i(this.separator);
    }

    public void endLayer() {
        int n = 1;
        if (this.layerDepth != null && !this.layerDepth.isEmpty()) {
            n = this.layerDepth.get(this.layerDepth.size() - 1);
            this.layerDepth.remove(this.layerDepth.size() - 1);
        } else {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.layer.operators"));
        }
        while (n-- > 0) {
            this.content.append("EMC").append_i(this.separator);
        }
    }

    public void transform(AffineTransform af) {
        double[] arr = new double[6];
        af.getMatrix(arr);
        this.content.append(arr[0]).append(' ').append(arr[1]).append(' ').append(arr[2]).append(' ');
        this.content.append(arr[3]).append(' ').append(arr[4]).append(' ').append(arr[5]).append(" cm").append_i(this.separator);
    }

    void addAnnotation(PdfAnnotation annot) {
        this.writer.addAnnotation(annot);
    }

    public void setDefaultColorspace(PdfName name, PdfObject obj) {
        PageResources prs = this.getPageResources();
        prs.addDefaultColor(name, obj);
    }

    public void beginMarkedContentSequence(PdfStructureElement struc) {
        PdfDictionary dict = new PdfDictionary();
        this.beginMarkedContentSequence(struc, dict);
    }

    public void beginMarkedContentSequence(PdfStructureElement struc, PdfDictionary dict) {
        PdfObject obj = struc.get(PdfName.K);
        int mark = this.pdf.getMarkPoint();
        if (obj != null) {
            PdfArray ar = null;
            if (obj.isNumber()) {
                ar = new PdfArray();
                ar.add(obj);
                struc.put(PdfName.K, ar);
            } else if (obj.isArray()) {
                ar = (PdfArray)obj;
                if (!ar.getPdfObject(0).isNumber()) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.structure.has.kids"));
                }
            } else {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("unknown.object.at.k.1", obj.getClass().toString()));
            }
            PdfDictionary dic = new PdfDictionary(PdfName.MCR);
            dic.put(PdfName.PG, this.writer.getCurrentPage());
            dic.put(PdfName.MCID, new PdfNumber(mark));
            ar.add(dic);
            struc.setPageMark(this.writer.getPageNumber() - 1, -1);
        } else {
            struc.setPageMark(this.writer.getPageNumber() - 1, mark);
            struc.put(PdfName.PG, this.writer.getCurrentPage());
        }
        this.pdf.incMarkPoint();
        ++this.mcDepth;
        dict.put(PdfName.MCID, new PdfNumber(mark));
        this.content.append(struc.get(PdfName.S).getBytes()).append(" ");
        try {
            dict.toPdf(this.writer, this.content);
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
        this.content.append(" BDC").append_i(this.separator);
    }

    public void endMarkedContentSequence() {
        if (this.mcDepth == 0) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.marked.content.operators"));
        }
        --this.mcDepth;
        this.content.append("EMC").append_i(this.separator);
    }

    public void beginMarkedContentSequence(PdfName tag, PdfDictionary property, boolean inline) {
        if (property == null) {
            this.content.append(tag.getBytes()).append(" BMC").append_i(this.separator);
            return;
        }
        this.content.append(tag.getBytes()).append(' ');
        if (inline) {
            try {
                property.toPdf(this.writer, this.content);
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        } else {
            PdfObject[] objs = this.writer.propertyExists(property) ? this.writer.addSimpleProperty(property, null) : this.writer.addSimpleProperty(property, this.writer.getPdfIndirectReference());
            PdfName name = (PdfName)objs[0];
            PageResources prs = this.getPageResources();
            name = prs.addProperty(name, (PdfIndirectReference)objs[1]);
            this.content.append(name.getBytes());
        }
        this.content.append(" BDC").append_i(this.separator);
        ++this.mcDepth;
    }

    public void beginMarkedContentSequence(PdfName tag) {
        this.beginMarkedContentSequence(tag, null, false);
    }

    public void sanityCheck() {
        if (this.mcDepth != 0) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.marked.content.operators"));
        }
        if (this.inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        if (this.layerDepth != null && !this.layerDepth.isEmpty()) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.layer.operators"));
        }
        if (!this.stateList.isEmpty()) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.save.restore.state.operators"));
        }
    }

    static {
        abrev.put(PdfName.BITSPERCOMPONENT, "/BPC ");
        abrev.put(PdfName.COLORSPACE, "/CS ");
        abrev.put(PdfName.DECODE, "/D ");
        abrev.put(PdfName.DECODEPARMS, "/DP ");
        abrev.put(PdfName.FILTER, "/F ");
        abrev.put(PdfName.HEIGHT, "/H ");
        abrev.put(PdfName.IMAGEMASK, "/IM ");
        abrev.put(PdfName.INTENT, "/Intent ");
        abrev.put(PdfName.INTERPOLATE, "/I ");
        abrev.put(PdfName.WIDTH, "/W ");
    }

    static class GraphicState {
        FontDetails fontDetails;
        ColorDetails colorDetails;
        float size;
        protected float xTLM = 0.0f;
        protected float yTLM = 0.0f;
        protected float leading = 0.0f;
        protected float scale = 100.0f;
        protected float charSpace = 0.0f;
        protected float wordSpace = 0.0f;

        GraphicState() {
        }

        GraphicState(GraphicState cp) {
            this.fontDetails = cp.fontDetails;
            this.colorDetails = cp.colorDetails;
            this.size = cp.size;
            this.xTLM = cp.xTLM;
            this.yTLM = cp.yTLM;
            this.leading = cp.leading;
            this.scale = cp.scale;
            this.charSpace = cp.charSpace;
            this.wordSpace = cp.wordSpace;
        }
    }
}

