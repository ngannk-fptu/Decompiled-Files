/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.codec.wmf.InputMeta;
import com.lowagie.text.pdf.codec.wmf.MetaBrush;
import com.lowagie.text.pdf.codec.wmf.MetaFont;
import com.lowagie.text.pdf.codec.wmf.MetaObject;
import com.lowagie.text.pdf.codec.wmf.MetaPen;
import com.lowagie.text.pdf.codec.wmf.MetaState;
import java.awt.Color;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

public class MetaDo {
    public static final int META_SETBKCOLOR = 513;
    public static final int META_SETBKMODE = 258;
    public static final int META_SETMAPMODE = 259;
    public static final int META_SETROP2 = 260;
    public static final int META_SETRELABS = 261;
    public static final int META_SETPOLYFILLMODE = 262;
    public static final int META_SETSTRETCHBLTMODE = 263;
    public static final int META_SETTEXTCHAREXTRA = 264;
    public static final int META_SETTEXTCOLOR = 521;
    public static final int META_SETTEXTJUSTIFICATION = 522;
    public static final int META_SETWINDOWORG = 523;
    public static final int META_SETWINDOWEXT = 524;
    public static final int META_SETVIEWPORTORG = 525;
    public static final int META_SETVIEWPORTEXT = 526;
    public static final int META_OFFSETWINDOWORG = 527;
    public static final int META_SCALEWINDOWEXT = 1040;
    public static final int META_OFFSETVIEWPORTORG = 529;
    public static final int META_SCALEVIEWPORTEXT = 1042;
    public static final int META_LINETO = 531;
    public static final int META_MOVETO = 532;
    public static final int META_EXCLUDECLIPRECT = 1045;
    public static final int META_INTERSECTCLIPRECT = 1046;
    public static final int META_ARC = 2071;
    public static final int META_ELLIPSE = 1048;
    public static final int META_FLOODFILL = 1049;
    public static final int META_PIE = 2074;
    public static final int META_RECTANGLE = 1051;
    public static final int META_ROUNDRECT = 1564;
    public static final int META_PATBLT = 1565;
    public static final int META_SAVEDC = 30;
    public static final int META_SETPIXEL = 1055;
    public static final int META_OFFSETCLIPRGN = 544;
    public static final int META_TEXTOUT = 1313;
    public static final int META_BITBLT = 2338;
    public static final int META_STRETCHBLT = 2851;
    public static final int META_POLYGON = 804;
    public static final int META_POLYLINE = 805;
    public static final int META_ESCAPE = 1574;
    public static final int META_RESTOREDC = 295;
    public static final int META_FILLREGION = 552;
    public static final int META_FRAMEREGION = 1065;
    public static final int META_INVERTREGION = 298;
    public static final int META_PAINTREGION = 299;
    public static final int META_SELECTCLIPREGION = 300;
    public static final int META_SELECTOBJECT = 301;
    public static final int META_SETTEXTALIGN = 302;
    public static final int META_CHORD = 2096;
    public static final int META_SETMAPPERFLAGS = 561;
    public static final int META_EXTTEXTOUT = 2610;
    public static final int META_SETDIBTODEV = 3379;
    public static final int META_SELECTPALETTE = 564;
    public static final int META_REALIZEPALETTE = 53;
    public static final int META_ANIMATEPALETTE = 1078;
    public static final int META_SETPALENTRIES = 55;
    public static final int META_POLYPOLYGON = 1336;
    public static final int META_RESIZEPALETTE = 313;
    public static final int META_DIBBITBLT = 2368;
    public static final int META_DIBSTRETCHBLT = 2881;
    public static final int META_DIBCREATEPATTERNBRUSH = 322;
    public static final int META_STRETCHDIB = 3907;
    public static final int META_EXTFLOODFILL = 1352;
    public static final int META_DELETEOBJECT = 496;
    public static final int META_CREATEPALETTE = 247;
    public static final int META_CREATEPATTERNBRUSH = 505;
    public static final int META_CREATEPENINDIRECT = 762;
    public static final int META_CREATEFONTINDIRECT = 763;
    public static final int META_CREATEBRUSHINDIRECT = 764;
    public static final int META_CREATEREGION = 1791;
    public PdfContentByte cb;
    public InputMeta in;
    int left;
    int top;
    int right;
    int bottom;
    int inch;
    MetaState state = new MetaState();

    public MetaDo(InputStream in, PdfContentByte cb) {
        this.cb = cb;
        this.in = new InputMeta(in);
    }

    public void readAll() throws IOException, DocumentException {
        if (this.in.readInt() != -1698247209) {
            throw new DocumentException(MessageLocalization.getComposedMessage("not.a.placeable.windows.metafile"));
        }
        this.in.readWord();
        this.left = this.in.readShort();
        this.top = this.in.readShort();
        this.right = this.in.readShort();
        this.bottom = this.in.readShort();
        this.inch = this.in.readWord();
        this.state.setScalingX((float)(this.right - this.left) / (float)this.inch * 72.0f);
        this.state.setScalingY((float)(this.bottom - this.top) / (float)this.inch * 72.0f);
        this.state.setOffsetWx(this.left);
        this.state.setOffsetWy(this.top);
        this.state.setExtentWx(this.right - this.left);
        this.state.setExtentWy(this.bottom - this.top);
        this.in.readInt();
        this.in.readWord();
        this.in.skip(18);
        this.cb.setLineCap(1);
        this.cb.setLineJoin(1);
        while (true) {
            int lenMarker = this.in.getLength();
            int tsize = this.in.readInt();
            if (tsize < 3) break;
            int function = this.in.readWord();
            switch (function) {
                case 0: {
                    break;
                }
                case 247: 
                case 322: 
                case 1791: {
                    this.state.addMetaObject(new MetaObject());
                    break;
                }
                case 762: {
                    MetaPen pen = new MetaPen();
                    pen.init(this.in);
                    this.state.addMetaObject(pen);
                    break;
                }
                case 764: {
                    MetaBrush brush = new MetaBrush();
                    brush.init(this.in);
                    this.state.addMetaObject(brush);
                    break;
                }
                case 763: {
                    MetaFont font = new MetaFont();
                    font.init(this.in);
                    this.state.addMetaObject(font);
                    break;
                }
                case 301: {
                    int idx = this.in.readWord();
                    this.state.selectMetaObject(idx, this.cb);
                    break;
                }
                case 496: {
                    int idx = this.in.readWord();
                    this.state.deleteMetaObject(idx);
                    break;
                }
                case 30: {
                    this.state.saveState(this.cb);
                    break;
                }
                case 295: {
                    int idx = this.in.readShort();
                    this.state.restoreState(idx, this.cb);
                    break;
                }
                case 523: {
                    this.state.setOffsetWy(this.in.readShort());
                    this.state.setOffsetWx(this.in.readShort());
                    break;
                }
                case 524: {
                    this.state.setExtentWy(this.in.readShort());
                    this.state.setExtentWx(this.in.readShort());
                    break;
                }
                case 532: {
                    int y = this.in.readShort();
                    Point p = new Point(this.in.readShort(), y);
                    this.state.setCurrentPoint(p);
                    break;
                }
                case 531: {
                    int y = this.in.readShort();
                    int x = this.in.readShort();
                    Point p = this.state.getCurrentPoint();
                    this.cb.moveTo(this.state.transformX(p.x), this.state.transformY(p.y));
                    this.cb.lineTo(this.state.transformX(x), this.state.transformY(y));
                    this.cb.stroke();
                    this.state.setCurrentPoint(new Point(x, y));
                    break;
                }
                case 805: {
                    this.state.setLineJoinPolygon(this.cb);
                    int len = this.in.readWord();
                    int x = this.in.readShort();
                    int y = this.in.readShort();
                    this.cb.moveTo(this.state.transformX(x), this.state.transformY(y));
                    for (int k = 1; k < len; ++k) {
                        x = this.in.readShort();
                        y = this.in.readShort();
                        this.cb.lineTo(this.state.transformX(x), this.state.transformY(y));
                    }
                    this.cb.stroke();
                    break;
                }
                case 804: {
                    if (this.isNullStrokeFill(false)) break;
                    int len = this.in.readWord();
                    int sx = this.in.readShort();
                    int sy = this.in.readShort();
                    this.cb.moveTo(this.state.transformX(sx), this.state.transformY(sy));
                    for (int k = 1; k < len; ++k) {
                        int x = this.in.readShort();
                        int y = this.in.readShort();
                        this.cb.lineTo(this.state.transformX(x), this.state.transformY(y));
                    }
                    this.cb.lineTo(this.state.transformX(sx), this.state.transformY(sy));
                    this.strokeAndFill();
                    break;
                }
                case 1336: {
                    if (this.isNullStrokeFill(false)) break;
                    int numPoly = this.in.readWord();
                    int[] lens = new int[numPoly];
                    for (int k = 0; k < lens.length; ++k) {
                        lens[k] = this.in.readWord();
                    }
                    for (int len : lens) {
                        int sx = this.in.readShort();
                        int sy = this.in.readShort();
                        this.cb.moveTo(this.state.transformX(sx), this.state.transformY(sy));
                        for (int k = 1; k < len; ++k) {
                            int x = this.in.readShort();
                            int y = this.in.readShort();
                            this.cb.lineTo(this.state.transformX(x), this.state.transformY(y));
                        }
                        this.cb.lineTo(this.state.transformX(sx), this.state.transformY(sy));
                    }
                    this.strokeAndFill();
                    break;
                }
                case 1048: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) break;
                    int b = this.in.readShort();
                    int r = this.in.readShort();
                    int t = this.in.readShort();
                    int l = this.in.readShort();
                    this.cb.arc(this.state.transformX(l), this.state.transformY(b), this.state.transformX(r), this.state.transformY(t), 0.0f, 360.0f);
                    this.strokeAndFill();
                    break;
                }
                case 2071: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) break;
                    float yend = this.state.transformY(this.in.readShort());
                    float xend = this.state.transformX(this.in.readShort());
                    float ystart = this.state.transformY(this.in.readShort());
                    float xstart = this.state.transformX(this.in.readShort());
                    float b = this.state.transformY(this.in.readShort());
                    float r = this.state.transformX(this.in.readShort());
                    float t = this.state.transformY(this.in.readShort());
                    float l = this.state.transformX(this.in.readShort());
                    float cx = (r + l) / 2.0f;
                    float cy = (t + b) / 2.0f;
                    float arc1 = MetaDo.getArc(cx, cy, xstart, ystart);
                    float arc2 = MetaDo.getArc(cx, cy, xend, yend);
                    if ((arc2 -= arc1) <= 0.0f) {
                        arc2 += 360.0f;
                    }
                    this.cb.arc(l, b, r, t, arc1, arc2);
                    this.cb.stroke();
                    break;
                }
                case 2074: {
                    List<float[]> ar;
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) break;
                    float yend = this.state.transformY(this.in.readShort());
                    float xend = this.state.transformX(this.in.readShort());
                    float ystart = this.state.transformY(this.in.readShort());
                    float xstart = this.state.transformX(this.in.readShort());
                    float b = this.state.transformY(this.in.readShort());
                    float r = this.state.transformX(this.in.readShort());
                    float t = this.state.transformY(this.in.readShort());
                    float l = this.state.transformX(this.in.readShort());
                    float cx = (r + l) / 2.0f;
                    float cy = (t + b) / 2.0f;
                    float arc1 = MetaDo.getArc(cx, cy, xstart, ystart);
                    float arc2 = MetaDo.getArc(cx, cy, xend, yend);
                    if ((arc2 -= arc1) <= 0.0f) {
                        arc2 += 360.0f;
                    }
                    if ((ar = PdfContentByte.bezierArc(l, b, r, t, arc1, arc2)).isEmpty()) break;
                    float[] pt = ar.get(0);
                    this.cb.moveTo(cx, cy);
                    this.cb.lineTo(pt[0], pt[1]);
                    Iterator<float[]> iterator = ar.iterator();
                    while (iterator.hasNext()) {
                        float[] anAr;
                        pt = anAr = iterator.next();
                        this.cb.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
                    }
                    this.cb.lineTo(cx, cy);
                    this.strokeAndFill();
                    break;
                }
                case 2096: {
                    List<float[]> ar;
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) break;
                    float yend = this.state.transformY(this.in.readShort());
                    float xend = this.state.transformX(this.in.readShort());
                    float ystart = this.state.transformY(this.in.readShort());
                    float xstart = this.state.transformX(this.in.readShort());
                    float b = this.state.transformY(this.in.readShort());
                    float r = this.state.transformX(this.in.readShort());
                    float t = this.state.transformY(this.in.readShort());
                    float l = this.state.transformX(this.in.readShort());
                    float cx = (r + l) / 2.0f;
                    float cy = (t + b) / 2.0f;
                    float arc1 = MetaDo.getArc(cx, cy, xstart, ystart);
                    float arc2 = MetaDo.getArc(cx, cy, xend, yend);
                    if ((arc2 -= arc1) <= 0.0f) {
                        arc2 += 360.0f;
                    }
                    if ((ar = PdfContentByte.bezierArc(l, b, r, t, arc1, arc2)).isEmpty()) break;
                    float[] pt = ar.get(0);
                    cx = pt[0];
                    cy = pt[1];
                    this.cb.moveTo(cx, cy);
                    Iterator<float[]> iterator = ar.iterator();
                    while (iterator.hasNext()) {
                        float[] floats;
                        pt = floats = iterator.next();
                        this.cb.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
                    }
                    this.cb.lineTo(cx, cy);
                    this.strokeAndFill();
                    break;
                }
                case 1051: {
                    if (this.isNullStrokeFill(true)) break;
                    float b = this.state.transformY(this.in.readShort());
                    float r = this.state.transformX(this.in.readShort());
                    float t = this.state.transformY(this.in.readShort());
                    float l = this.state.transformX(this.in.readShort());
                    this.cb.rectangle(l, b, r - l, t - b);
                    this.strokeAndFill();
                    break;
                }
                case 1564: {
                    if (this.isNullStrokeFill(true)) break;
                    float h = this.state.transformY(0) - this.state.transformY(this.in.readShort());
                    float w = this.state.transformX(this.in.readShort()) - this.state.transformX(0);
                    float b = this.state.transformY(this.in.readShort());
                    float r = this.state.transformX(this.in.readShort());
                    float t = this.state.transformY(this.in.readShort());
                    float l = this.state.transformX(this.in.readShort());
                    this.cb.roundRectangle(l, b, r - l, t - b, (h + w) / 4.0f);
                    this.strokeAndFill();
                    break;
                }
                case 1046: {
                    float b = this.state.transformY(this.in.readShort());
                    float r = this.state.transformX(this.in.readShort());
                    float t = this.state.transformY(this.in.readShort());
                    float l = this.state.transformX(this.in.readShort());
                    this.cb.rectangle(l, b, r - l, t - b);
                    this.cb.eoClip();
                    this.cb.newPath();
                    break;
                }
                case 2610: {
                    String s;
                    byte c;
                    int k;
                    int y = this.in.readShort();
                    int x = this.in.readShort();
                    int count = this.in.readWord();
                    int flag = this.in.readWord();
                    int x1 = 0;
                    int y1 = 0;
                    int x2 = 0;
                    int y2 = 0;
                    if ((flag & 6) != 0) {
                        x1 = this.in.readShort();
                        y1 = this.in.readShort();
                        x2 = this.in.readShort();
                        y2 = this.in.readShort();
                    }
                    byte[] text = new byte[count];
                    for (k = 0; k < count && (c = (byte)this.in.readByte()) != 0; ++k) {
                        text[k] = c;
                    }
                    try {
                        s = new String(text, 0, k, "Cp1252");
                    }
                    catch (UnsupportedEncodingException e) {
                        s = new String(text, 0, k);
                    }
                    this.outputText(x, y, flag, x1, y1, x2, y2, s);
                    break;
                }
                case 1313: {
                    String s;
                    byte c;
                    int k;
                    int count = this.in.readWord();
                    byte[] text = new byte[count];
                    for (k = 0; k < count && (c = (byte)this.in.readByte()) != 0; ++k) {
                        text[k] = c;
                    }
                    try {
                        s = new String(text, 0, k, "Cp1252");
                    }
                    catch (UnsupportedEncodingException e) {
                        s = new String(text, 0, k);
                    }
                    count = count + 1 & 0xFFFE;
                    this.in.skip(count - k);
                    int y = this.in.readShort();
                    int x = this.in.readShort();
                    this.outputText(x, y, 0, 0, 0, 0, 0, s);
                    break;
                }
                case 513: {
                    this.state.setCurrentBackgroundColor(this.in.readColor());
                    break;
                }
                case 521: {
                    this.state.setCurrentTextColor(this.in.readColor());
                    break;
                }
                case 302: {
                    this.state.setTextAlign(this.in.readWord());
                    break;
                }
                case 258: {
                    this.state.setBackgroundMode(this.in.readWord());
                    break;
                }
                case 262: {
                    this.state.setPolyFillMode(this.in.readWord());
                    break;
                }
                case 1055: {
                    Color color = this.in.readColor();
                    int y = this.in.readShort();
                    int x = this.in.readShort();
                    this.cb.saveState();
                    this.cb.setColorFill(color);
                    this.cb.rectangle(this.state.transformX(x), this.state.transformY(y), 0.2f, 0.2f);
                    this.cb.fill();
                    this.cb.restoreState();
                    break;
                }
            }
            this.in.skip(tsize * 2 - (this.in.getLength() - lenMarker));
        }
        this.state.cleanup(this.cb);
    }

    public void outputText(int x, int y, int flag, int x1, int y1, int x2, int y2, String text) {
        Color textColor;
        MetaFont font = this.state.getCurrentFont();
        float refX = this.state.transformX(x);
        float refY = this.state.transformY(y);
        float angle = this.state.transformAngle(font.getAngle());
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        float fontSize = font.getFontSize(this.state);
        BaseFont bf = font.getFont();
        int align = this.state.getTextAlign();
        float textWidth = bf.getWidthPoint(text, fontSize);
        float tx = 0.0f;
        float ty = 0.0f;
        float descender = bf.getFontDescriptor(3, fontSize);
        float ury = bf.getFontDescriptor(8, fontSize);
        this.cb.saveState();
        this.cb.concatCTM(cos, sin, -sin, cos, refX, refY);
        if ((align & 6) == 6) {
            tx = -textWidth / 2.0f;
        } else if ((align & 2) == 2) {
            tx = -textWidth;
        }
        ty = (align & 0x18) == 24 ? 0.0f : ((align & 8) == 8 ? -descender : -ury);
        if (this.state.getBackgroundMode() == 2) {
            textColor = this.state.getCurrentBackgroundColor();
            this.cb.setColorFill(textColor);
            this.cb.rectangle(tx, ty + descender, textWidth, ury - descender);
            this.cb.fill();
        }
        textColor = this.state.getCurrentTextColor();
        this.cb.setColorFill(textColor);
        this.cb.beginText();
        this.cb.setFontAndSize(bf, fontSize);
        this.cb.setTextMatrix(tx, ty);
        this.cb.showText(text);
        this.cb.endText();
        if (font.isUnderline()) {
            this.cb.rectangle(tx, ty - fontSize / 4.0f, textWidth, fontSize / 15.0f);
            this.cb.fill();
        }
        if (font.isStrikeout()) {
            this.cb.rectangle(tx, ty + fontSize / 3.0f, textWidth, fontSize / 15.0f);
            this.cb.fill();
        }
        this.cb.restoreState();
    }

    public boolean isNullStrokeFill(boolean isRectangle) {
        boolean result;
        MetaPen pen = this.state.getCurrentPen();
        MetaBrush brush = this.state.getCurrentBrush();
        boolean noPen = pen.getStyle() == 5;
        int style = brush.getStyle();
        boolean isBrush = style == 0 || style == 2 && this.state.getBackgroundMode() == 2;
        boolean bl = result = noPen && !isBrush;
        if (!noPen) {
            if (isRectangle) {
                this.state.setLineJoinRectangle(this.cb);
            } else {
                this.state.setLineJoinPolygon(this.cb);
            }
        }
        return result;
    }

    public void strokeAndFill() {
        MetaPen pen = this.state.getCurrentPen();
        MetaBrush brush = this.state.getCurrentBrush();
        int penStyle = pen.getStyle();
        int brushStyle = brush.getStyle();
        if (penStyle == 5) {
            this.cb.closePath();
            if (this.state.getPolyFillMode() == 1) {
                this.cb.eoFill();
            } else {
                this.cb.fill();
            }
        } else {
            boolean isBrush;
            boolean bl = isBrush = brushStyle == 0 || brushStyle == 2 && this.state.getBackgroundMode() == 2;
            if (isBrush) {
                if (this.state.getPolyFillMode() == 1) {
                    this.cb.closePathEoFillStroke();
                } else {
                    this.cb.closePathFillStroke();
                }
            } else {
                this.cb.closePathStroke();
            }
        }
    }

    static float getArc(float xCenter, float yCenter, float xDot, float yDot) {
        double s = Math.atan2(yDot - yCenter, xDot - xCenter);
        if (s < 0.0) {
            s += Math.PI * 2;
        }
        return (float)(s / Math.PI * 180.0);
    }

    public static byte[] wrapBMP(Image image) throws IOException {
        if (image.getOriginalType() != 4) {
            throw new IOException(MessageLocalization.getComposedMessage("only.bmp.can.be.wrapped.in.wmf"));
        }
        byte[] data = null;
        if (image.getOriginalData() == null) {
            InputStream imgIn = image.getUrl().openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int b = 0;
            while ((b = imgIn.read()) != -1) {
                out.write(b);
            }
            imgIn.close();
            data = out.toByteArray();
        } else {
            data = image.getOriginalData();
        }
        int sizeBmpWords = data.length - 14 + 1 >>> 1;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MetaDo.writeWord(os, 1);
        MetaDo.writeWord(os, 9);
        MetaDo.writeWord(os, 768);
        MetaDo.writeDWord(os, 23 + (13 + sizeBmpWords) + 3);
        MetaDo.writeWord(os, 1);
        MetaDo.writeDWord(os, 14 + sizeBmpWords);
        MetaDo.writeWord(os, 0);
        MetaDo.writeDWord(os, 4);
        MetaDo.writeWord(os, 259);
        MetaDo.writeWord(os, 8);
        MetaDo.writeDWord(os, 5);
        MetaDo.writeWord(os, 523);
        MetaDo.writeWord(os, 0);
        MetaDo.writeWord(os, 0);
        MetaDo.writeDWord(os, 5);
        MetaDo.writeWord(os, 524);
        MetaDo.writeWord(os, (int)image.getHeight());
        MetaDo.writeWord(os, (int)image.getWidth());
        MetaDo.writeDWord(os, 13 + sizeBmpWords);
        MetaDo.writeWord(os, 2881);
        MetaDo.writeDWord(os, 0xCC0020);
        MetaDo.writeWord(os, (int)image.getHeight());
        MetaDo.writeWord(os, (int)image.getWidth());
        MetaDo.writeWord(os, 0);
        MetaDo.writeWord(os, 0);
        MetaDo.writeWord(os, (int)image.getHeight());
        MetaDo.writeWord(os, (int)image.getWidth());
        MetaDo.writeWord(os, 0);
        MetaDo.writeWord(os, 0);
        os.write(data, 14, data.length - 14);
        if ((data.length & 1) == 1) {
            os.write(0);
        }
        MetaDo.writeDWord(os, 3);
        MetaDo.writeWord(os, 0);
        os.close();
        return os.toByteArray();
    }

    public static void writeWord(OutputStream os, int v) throws IOException {
        os.write(v & 0xFF);
        os.write(v >>> 8 & 0xFF);
    }

    public static void writeDWord(OutputStream os, int v) throws IOException {
        MetaDo.writeWord(os, v & 0xFFFF);
        MetaDo.writeWord(os, v >>> 16 & 0xFFFF);
    }
}

