/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.Polygon2D
 *  org.apache.batik.ext.awt.geom.Polyline2D
 *  org.apache.batik.util.Platform
 */
package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.ext.awt.geom.Polyline2D;
import org.apache.batik.transcoder.wmf.tosvg.AbstractWMFPainter;
import org.apache.batik.transcoder.wmf.tosvg.GdiObject;
import org.apache.batik.transcoder.wmf.tosvg.MetaRecord;
import org.apache.batik.transcoder.wmf.tosvg.TextureFactory;
import org.apache.batik.transcoder.wmf.tosvg.WMFFont;
import org.apache.batik.transcoder.wmf.tosvg.WMFRecordStore;
import org.apache.batik.transcoder.wmf.tosvg.WMFUtilities;
import org.apache.batik.util.Platform;

public class WMFPainter
extends AbstractWMFPainter {
    private static final int INPUT_BUFFER_SIZE = 30720;
    private static final Integer INTEGER_0 = 0;
    private float scale;
    private float scaleX;
    private float scaleY;
    private float conv;
    private float xOffset;
    private float yOffset;
    private float vpX;
    private float vpY;
    private float vpW;
    private float vpH;
    private Color frgdColor;
    private Color bkgdColor;
    private boolean opaque = false;
    private transient boolean firstEffectivePaint = true;
    private static BasicStroke solid = new BasicStroke(1.0f, 0, 1);
    private static BasicStroke textSolid = new BasicStroke(1.0f, 0, 1);
    private transient ImageObserver observer = new ImageObserver(){

        @Override
        public boolean imageUpdate(Image img, int flags, int x, int y, int width, int height) {
            return false;
        }
    };
    private transient BufferedInputStream bufStream = null;

    public WMFPainter(WMFRecordStore currentStore, float scale) {
        this(currentStore, 0, 0, scale);
    }

    public WMFPainter(WMFRecordStore currentStore, int xOffset, int yOffset, float scale) {
        this.setRecordStore(currentStore);
        TextureFactory.getInstance().reset();
        this.conv = scale;
        this.xOffset = -xOffset;
        this.yOffset = -yOffset;
        this.scale = (float)currentStore.getWidthPixels() / (float)currentStore.getWidthUnits() * scale;
        this.scale = this.scale * (float)currentStore.getWidthPixels() / (float)currentStore.getVpW();
        float xfactor = (float)currentStore.getVpW() / (float)currentStore.getWidthPixels() * (float)currentStore.getWidthUnits() / (float)currentStore.getWidthPixels();
        float yfactor = (float)currentStore.getVpH() / (float)currentStore.getHeightPixels() * (float)currentStore.getHeightUnits() / (float)currentStore.getHeightPixels();
        this.xOffset *= xfactor;
        this.yOffset *= yfactor;
        this.scaleX = this.scale;
        this.scaleY = this.scale;
    }

    public void paint(Graphics g) {
        float fontHeight = 10.0f;
        float fontAngle = 0.0f;
        float penWidth = 0.0f;
        float startX = 0.0f;
        float startY = 0.0f;
        int brushObject = -1;
        int penObject = -1;
        int fontObject = -1;
        Object font = null;
        Stack<Serializable> dcStack = new Stack<Serializable>();
        int numRecords = this.currentStore.getNumRecords();
        int numObjects = this.currentStore.getNumObjects();
        this.vpX = this.currentStore.getVpX() * this.scale;
        this.vpY = this.currentStore.getVpY() * this.scale;
        this.vpW = (float)this.currentStore.getVpW() * this.scale;
        this.vpH = (float)this.currentStore.getVpH() * this.scale;
        if (!this.currentStore.isReading()) {
            GdiObject gdiObj;
            g.setPaintMode();
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(solid);
            brushObject = -1;
            penObject = -1;
            fontObject = -1;
            this.frgdColor = null;
            this.bkgdColor = Color.white;
            for (int i = 0; i < numObjects; ++i) {
                gdiObj = this.currentStore.getObject(i);
                gdiObj.clear();
            }
            float w = this.vpW;
            float h = this.vpH;
            g2d.setColor(Color.black);
            block62: for (int iRec = 0; iRec < numRecords; ++iRec) {
                MetaRecord mr = this.currentStore.getRecord(iRec);
                switch (mr.functionId) {
                    case 523: {
                        this.vpX = -mr.elementAt(0);
                        this.currentStore.setVpX(this.vpX);
                        this.vpY = -mr.elementAt(1);
                        this.currentStore.setVpY(this.vpY);
                        this.vpX *= this.scale;
                        this.vpY *= this.scale;
                        continue block62;
                    }
                    case 0: 
                    case 524: {
                        this.vpW = mr.elementAt(0);
                        this.vpH = mr.elementAt(1);
                        this.scaleX = this.scale;
                        this.scaleY = this.scale;
                        solid = new BasicStroke(this.scaleX * 2.0f, 0, 1);
                        continue block62;
                    }
                    case 525: 
                    case 526: 
                    case 527: 
                    case 529: 
                    case 1040: 
                    case 1042: {
                        continue block62;
                    }
                    case 262: {
                        continue block62;
                    }
                    case 762: {
                        Color newClr;
                        int objIndex = 0;
                        int penStyle = mr.elementAt(0);
                        if (penStyle == 5) {
                            newClr = Color.white;
                            objIndex = this.addObjectAt(this.currentStore, 4, newClr, objIndex);
                            continue block62;
                        }
                        penWidth = mr.elementAt(4);
                        this.setStroke(g2d, penStyle, penWidth, this.scaleX);
                        newClr = new Color(mr.elementAt(1), mr.elementAt(2), mr.elementAt(3));
                        objIndex = this.addObjectAt(this.currentStore, 1, newClr, objIndex);
                        continue block62;
                    }
                    case 764: {
                        int objIndex = 0;
                        int brushStyle = mr.elementAt(0);
                        Color clr = new Color(mr.elementAt(1), mr.elementAt(2), mr.elementAt(3));
                        if (brushStyle == 0) {
                            objIndex = this.addObjectAt(this.currentStore, 2, clr, objIndex);
                            continue block62;
                        }
                        if (brushStyle == 2) {
                            int hatch = mr.elementAt(4);
                            Paint paint = !this.opaque ? TextureFactory.getInstance().getTexture(hatch, clr) : TextureFactory.getInstance().getTexture(hatch, clr, this.bkgdColor);
                            if (paint != null) {
                                objIndex = this.addObjectAt(this.currentStore, 2, paint, objIndex);
                                continue block62;
                            }
                            clr = Color.black;
                            objIndex = this.addObjectAt(this.currentStore, 5, clr, objIndex);
                            continue block62;
                        }
                        clr = Color.black;
                        objIndex = this.addObjectAt(this.currentStore, 5, clr, objIndex);
                        continue block62;
                    }
                    case 763: {
                        int d;
                        float size = (int)(this.scaleY * (float)mr.elementAt(0));
                        int charset = mr.elementAt(3);
                        int italic = mr.elementAt(1);
                        int weight = mr.elementAt(2);
                        int style = italic > 0 ? 2 : 0;
                        style |= weight > 400 ? 1 : 0;
                        String face = ((MetaRecord.StringRecord)mr).text;
                        for (d = 0; d < face.length() && (Character.isLetterOrDigit(face.charAt(d)) || Character.isWhitespace(face.charAt(d))); ++d) {
                        }
                        face = d > 0 ? face.substring(0, d) : "System";
                        if (size < 0.0f) {
                            size = -size;
                        }
                        int objIndex = 0;
                        fontHeight = size;
                        Font f = new Font(face, style, (int)size);
                        f = f.deriveFont(size);
                        int underline = mr.elementAt(4);
                        int strikeOut = mr.elementAt(5);
                        int orient = mr.elementAt(6);
                        int escape = mr.elementAt(7);
                        WMFFont wf = new WMFFont(f, charset, underline, strikeOut, italic, weight, orient, escape);
                        objIndex = this.addObjectAt(this.currentStore, 3, wf, objIndex);
                        continue block62;
                    }
                    case 248: 
                    case 505: 
                    case 765: 
                    case 1790: 
                    case 1791: {
                        int size = this.addObjectAt(this.currentStore, 6, INTEGER_0, 0);
                        continue block62;
                    }
                    case 247: {
                        int size = this.addObjectAt(this.currentStore, 8, INTEGER_0, 0);
                        continue block62;
                    }
                    case 53: 
                    case 55: 
                    case 313: 
                    case 564: 
                    case 1078: {
                        continue block62;
                    }
                    case 301: {
                        int gdiIndex = mr.elementAt(0);
                        if ((gdiIndex & Integer.MIN_VALUE) != 0) continue block62;
                        if (gdiIndex >= numObjects) {
                            switch (gdiIndex -= numObjects) {
                                case 5: {
                                    brushObject = -1;
                                    continue block62;
                                }
                                case 8: {
                                    penObject = -1;
                                    continue block62;
                                }
                            }
                            continue block62;
                        }
                        gdiObj = this.currentStore.getObject(gdiIndex);
                        if (!gdiObj.used) continue block62;
                        switch (gdiObj.type) {
                            case 1: {
                                g2d.setColor((Color)gdiObj.obj);
                                penObject = gdiIndex;
                                break;
                            }
                            case 2: {
                                if (gdiObj.obj instanceof Color) {
                                    g2d.setColor((Color)gdiObj.obj);
                                } else if (gdiObj.obj instanceof Paint) {
                                    g2d.setPaint((Paint)gdiObj.obj);
                                } else {
                                    g2d.setPaint(this.getPaint((byte[])gdiObj.obj));
                                }
                                brushObject = gdiIndex;
                                break;
                            }
                            case 3: {
                                this.wmfFont = (WMFFont)gdiObj.obj;
                                Font f = this.wmfFont.font;
                                g2d.setFont(f);
                                fontObject = gdiIndex;
                                break;
                            }
                            case 4: {
                                penObject = -1;
                                break;
                            }
                            case 5: {
                                brushObject = -1;
                            }
                        }
                        continue block62;
                    }
                    case 496: {
                        int gdiIndex = mr.elementAt(0);
                        gdiObj = this.currentStore.getObject(gdiIndex);
                        if (gdiIndex == brushObject) {
                            brushObject = -1;
                        } else if (gdiIndex == penObject) {
                            penObject = -1;
                        } else if (gdiIndex == fontObject) {
                            fontObject = -1;
                        }
                        gdiObj.clear();
                        continue block62;
                    }
                    case 1336: {
                        int numPolygons = mr.elementAt(0);
                        int[] pts = new int[numPolygons];
                        for (int ip = 0; ip < numPolygons; ++ip) {
                            pts[ip] = mr.elementAt(ip + 1);
                        }
                        int offset = numPolygons + 1;
                        ArrayList<Polygon2D> v = new ArrayList<Polygon2D>(numPolygons);
                        for (int j = 0; j < numPolygons; ++j) {
                            int count = pts[j];
                            float[] xpts = new float[count];
                            float[] ypts = new float[count];
                            for (int k = 0; k < count; ++k) {
                                xpts[k] = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(offset + k * 2));
                                ypts[k] = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(offset + k * 2 + 1));
                            }
                            offset += count * 2;
                            Polygon2D pol = new Polygon2D(xpts, ypts, count);
                            v.add(pol);
                        }
                        if (brushObject >= 0) {
                            this.setBrushPaint(this.currentStore, g2d, brushObject);
                            this.fillPolyPolygon(g2d, v);
                            this.firstEffectivePaint = false;
                        }
                        if (penObject < 0) continue block62;
                        this.setPenColor(this.currentStore, g2d, penObject);
                        this.drawPolyPolygon(g2d, v);
                        this.firstEffectivePaint = false;
                        continue block62;
                    }
                    case 804: {
                        int count = mr.elementAt(0);
                        float[] _xpts = new float[count];
                        float[] _ypts = new float[count];
                        for (int k = 0; k < count; ++k) {
                            _xpts[k] = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(k * 2 + 1));
                            _ypts[k] = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(k * 2 + 2));
                        }
                        Polygon2D pol = new Polygon2D(_xpts, _ypts, count);
                        this.paint(brushObject, penObject, (Shape)pol, g2d);
                        continue block62;
                    }
                    case 532: {
                        startX = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(0));
                        startY = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(1));
                        continue block62;
                    }
                    case 531: {
                        float endX = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(0));
                        float endY = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(1));
                        Line2D.Float line = new Line2D.Float(startX, startY, endX, endY);
                        this.paintWithPen(penObject, line, g2d);
                        startX = endX;
                        startY = endY;
                        continue block62;
                    }
                    case 805: {
                        int count = mr.elementAt(0);
                        float[] _xpts = new float[count];
                        float[] _ypts = new float[count];
                        for (int k = 0; k < count; ++k) {
                            _xpts[k] = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(k * 2 + 1));
                            _ypts[k] = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(k * 2 + 2));
                        }
                        Polyline2D pol = new Polyline2D(_xpts, _ypts, count);
                        this.paintWithPen(penObject, (Shape)pol, g2d);
                        continue block62;
                    }
                    case 1051: {
                        float x1 = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(0));
                        float x2 = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(2));
                        float y1 = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(1));
                        float y2 = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(3));
                        Rectangle2D.Float rec = new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
                        this.paint(brushObject, penObject, rec, g2d);
                        continue block62;
                    }
                    case 1564: {
                        float x1 = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(0));
                        float x2 = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(2));
                        float x3 = this.scaleX * (float)mr.elementAt(4);
                        float y1 = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(1));
                        float y2 = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(3));
                        float y3 = this.scaleY * (float)mr.elementAt(5);
                        RoundRectangle2D.Float rec = new RoundRectangle2D.Float(x1, y1, x2 - x1, y2 - y1, x3, y3);
                        this.paint(brushObject, penObject, rec, g2d);
                        continue block62;
                    }
                    case 1048: {
                        float x1 = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(0));
                        float x2 = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(2));
                        float y1 = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(1));
                        float y2 = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(3));
                        Ellipse2D.Float el = new Ellipse2D.Float(x1, y1, x2 - x1, y2 - y1);
                        this.paint(brushObject, penObject, el, g2d);
                        continue block62;
                    }
                    case 302: {
                        this.currentHorizAlign = WMFUtilities.getHorizontalAlignment(mr.elementAt(0));
                        this.currentVertAlign = WMFUtilities.getVerticalAlignment(mr.elementAt(0));
                        continue block62;
                    }
                    case 521: {
                        this.frgdColor = new Color(mr.elementAt(0), mr.elementAt(1), mr.elementAt(2));
                        g2d.setColor(this.frgdColor);
                        continue block62;
                    }
                    case 513: {
                        this.bkgdColor = new Color(mr.elementAt(0), mr.elementAt(1), mr.elementAt(2));
                        g2d.setColor(this.bkgdColor);
                        continue block62;
                    }
                    case 2610: {
                        try {
                            byte[] bstr = ((MetaRecord.ByteRecord)mr).bstr;
                            String sr = WMFUtilities.decodeString(this.wmfFont, bstr);
                            float x = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(0));
                            float y = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(1));
                            if (this.frgdColor != null) {
                                g2d.setColor(this.frgdColor);
                            } else {
                                g2d.setColor(Color.black);
                            }
                            FontRenderContext frc = g2d.getFontRenderContext();
                            Point2D.Double pen = new Point2D.Double(0.0, 0.0);
                            GeneralPath gp = new GeneralPath(1);
                            TextLayout layout = new TextLayout(sr, g2d.getFont(), frc);
                            int flag = mr.elementAt(2);
                            int x1 = 0;
                            int y1 = 0;
                            int x2 = 0;
                            int y2 = 0;
                            boolean clipped = false;
                            Shape clip = null;
                            if ((flag & 4) != 0) {
                                clipped = true;
                                x1 = mr.elementAt(3);
                                y1 = mr.elementAt(4);
                                x2 = mr.elementAt(5);
                                y2 = mr.elementAt(6);
                                clip = g2d.getClip();
                                g2d.setClip(x1, y1, x2, y2);
                            }
                            this.firstEffectivePaint = false;
                            this.drawString(flag, g2d, this.getCharacterIterator(g2d, sr, this.wmfFont, this.currentHorizAlign), x, y += this.getVerticalAlignmentValue(layout, this.currentVertAlign), layout, this.wmfFont, this.currentHorizAlign);
                            if (!clipped) continue block62;
                            g2d.setClip(clip);
                        }
                        catch (Exception bstr) {}
                        continue block62;
                    }
                    case 1313: 
                    case 1583: {
                        try {
                            byte[] bstr = ((MetaRecord.ByteRecord)mr).bstr;
                            String sr = WMFUtilities.decodeString(this.wmfFont, bstr);
                            float x = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(0));
                            float y = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(1));
                            if (this.frgdColor != null) {
                                g2d.setColor(this.frgdColor);
                            } else {
                                g2d.setColor(Color.black);
                            }
                            FontRenderContext frc = g2d.getFontRenderContext();
                            Point2D.Double pen = new Point2D.Double(0.0, 0.0);
                            GeneralPath gp = new GeneralPath(1);
                            TextLayout layout = new TextLayout(sr, g2d.getFont(), frc);
                            this.firstEffectivePaint = false;
                            this.drawString(-1, g2d, this.getCharacterIterator(g2d, sr, this.wmfFont), x, y += this.getVerticalAlignmentValue(layout, this.currentVertAlign), layout, this.wmfFont, this.currentHorizAlign);
                        }
                        catch (Exception bstr) {}
                        continue block62;
                    }
                    case 2071: 
                    case 2074: 
                    case 2096: {
                        double left = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(0));
                        double top = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(1));
                        double right = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(2));
                        double bottom = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(3));
                        double xstart = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(4));
                        double ystart = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(5));
                        double xend = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(6));
                        double yend = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(7));
                        this.setBrushPaint(this.currentStore, g2d, brushObject);
                        double cx = left + (right - left) / 2.0;
                        double cy = top + (bottom - top) / 2.0;
                        double startAngle = -Math.toDegrees(Math.atan2(ystart - cy, xstart - cx));
                        double endAngle = -Math.toDegrees(Math.atan2(yend - cy, xend - cx));
                        double extentAngle = endAngle - startAngle;
                        if (extentAngle < 0.0) {
                            extentAngle += 360.0;
                        }
                        if (startAngle < 0.0) {
                            startAngle += 360.0;
                        }
                        switch (mr.functionId) {
                            case 2071: {
                                Arc2D.Double arc = new Arc2D.Double(left, top, right - left, bottom - top, startAngle, extentAngle, 0);
                                g2d.draw(arc);
                                break;
                            }
                            case 2074: {
                                Arc2D.Double arc = new Arc2D.Double(left, top, right - left, bottom - top, startAngle, extentAngle, 2);
                                this.paint(brushObject, penObject, arc, g2d);
                                break;
                            }
                            case 2096: {
                                Arc2D.Double arc = new Arc2D.Double(left, top, right - left, bottom - top, startAngle, extentAngle, 1);
                                this.paint(brushObject, penObject, arc, g2d);
                            }
                        }
                        this.firstEffectivePaint = false;
                        continue block62;
                    }
                    case 30: {
                        dcStack.push(Float.valueOf(penWidth));
                        dcStack.push(Float.valueOf(startX));
                        dcStack.push(Float.valueOf(startY));
                        dcStack.push(Integer.valueOf(brushObject));
                        dcStack.push(Integer.valueOf(penObject));
                        dcStack.push(Integer.valueOf(fontObject));
                        dcStack.push(this.frgdColor);
                        dcStack.push(this.bkgdColor);
                        continue block62;
                    }
                    case 295: {
                        this.bkgdColor = (Color)dcStack.pop();
                        this.frgdColor = (Color)dcStack.pop();
                        fontObject = (Integer)dcStack.pop();
                        penObject = (Integer)dcStack.pop();
                        brushObject = (Integer)dcStack.pop();
                        startY = ((Float)dcStack.pop()).floatValue();
                        startX = ((Float)dcStack.pop()).floatValue();
                        penWidth = ((Float)dcStack.pop()).floatValue();
                        continue block62;
                    }
                    case 4096: {
                        try {
                            this.setPenColor(this.currentStore, g2d, penObject);
                            int pointCount = mr.elementAt(0);
                            int bezierCount = (pointCount - 1) / 3;
                            float _startX = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(1));
                            float _startY = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(2));
                            GeneralPath gp = new GeneralPath(1);
                            gp.moveTo(_startX, _startY);
                            for (int j = 0; j < bezierCount; ++j) {
                                int j6 = j * 6;
                                float cp1X = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(j6 + 3));
                                float cp1Y = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(j6 + 4));
                                float cp2X = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(j6 + 5));
                                float cp2Y = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(j6 + 6));
                                float endX = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(j6 + 7));
                                float endY = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(j6 + 8));
                                gp.curveTo(cp1X, cp1Y, cp2X, cp2Y, endX, endY);
                                _startX = endX;
                                _startY = endY;
                            }
                            g2d.setStroke(solid);
                            g2d.draw(gp);
                            this.firstEffectivePaint = false;
                        }
                        catch (Exception pointCount) {}
                        continue block62;
                    }
                    case 259: 
                    case 261: 
                    case 263: 
                    case 264: 
                    case 300: 
                    case 522: 
                    case 544: 
                    case 1045: 
                    case 1046: 
                    case 1049: {
                        continue block62;
                    }
                    case 258: {
                        int mode = mr.elementAt(0);
                        this.opaque = mode == 2;
                        continue block62;
                    }
                    case 260: {
                        float rop = mr.ElementAt(0).intValue();
                        Paint paint = null;
                        boolean ok = false;
                        if (rop == 66.0f) {
                            paint = Color.black;
                            ok = true;
                        } else if (rop == 1.6711778E7f) {
                            paint = Color.white;
                            ok = true;
                        } else if (rop == 1.5728673E7f && brushObject >= 0) {
                            paint = this.getStoredPaint(this.currentStore, brushObject);
                            ok = true;
                        }
                        if (!ok) continue block62;
                        if (paint != null) {
                            g2d.setPaint(paint);
                            continue block62;
                        }
                        this.setBrushPaint(this.currentStore, g2d, brushObject);
                        continue block62;
                    }
                    case 1565: {
                        float rop = mr.elementAt(0);
                        float height = this.scaleY * (float)mr.elementAt(1);
                        float width = this.scaleX * (float)mr.elementAt(2);
                        float left = this.scaleX * (this.vpX + this.xOffset + (float)mr.elementAt(3));
                        float top = this.scaleY * (this.vpY + this.yOffset + (float)mr.elementAt(4));
                        Paint paint = null;
                        boolean ok = false;
                        if (rop == 66.0f) {
                            paint = Color.black;
                            ok = true;
                        } else if (rop == 1.6711778E7f) {
                            paint = Color.white;
                            ok = true;
                        } else if (rop == 1.5728673E7f && brushObject >= 0) {
                            paint = this.getStoredPaint(this.currentStore, brushObject);
                            ok = true;
                        }
                        if (!ok) continue block62;
                        Color oldClr = g2d.getColor();
                        if (paint != null) {
                            g2d.setPaint(paint);
                        } else {
                            this.setBrushPaint(this.currentStore, g2d, brushObject);
                        }
                        Rectangle2D.Float rec = new Rectangle2D.Float(left, top, width, height);
                        g2d.fill(rec);
                        g2d.setColor(oldClr);
                        continue block62;
                    }
                    case 2881: {
                        int height = mr.elementAt(1);
                        int width = mr.elementAt(2);
                        int sy = mr.elementAt(3);
                        int sx = mr.elementAt(4);
                        float dy = this.conv * this.currentStore.getVpWFactor() * (this.vpY + this.yOffset + (float)mr.elementAt(7));
                        float dx = this.conv * this.currentStore.getVpHFactor() * (this.vpX + this.xOffset + (float)mr.elementAt(8));
                        float heightDst = mr.elementAt(5);
                        float widthDst = mr.elementAt(6);
                        widthDst = widthDst * this.conv * this.currentStore.getVpWFactor();
                        heightDst = heightDst * this.conv * this.currentStore.getVpHFactor();
                        byte[] bitmap = ((MetaRecord.ByteRecord)mr).bstr;
                        BufferedImage img = this.getImage(bitmap, width, height);
                        if (img == null) continue block62;
                        g2d.drawImage(img, (int)dx, (int)dy, (int)(dx + widthDst), (int)(dy + heightDst), sx, sy, sx + width, sy + height, this.bkgdColor, this.observer);
                        continue block62;
                    }
                    case 3907: {
                        int height = mr.elementAt(1);
                        int width = mr.elementAt(2);
                        int sy = mr.elementAt(3);
                        int sx = mr.elementAt(4);
                        float dy = this.conv * this.currentStore.getVpWFactor() * (this.vpY + this.yOffset + (float)mr.elementAt(7));
                        float dx = this.conv * this.currentStore.getVpHFactor() * (this.vpX + this.xOffset + (float)mr.elementAt(8));
                        float heightDst = mr.elementAt(5);
                        float widthDst = mr.elementAt(6);
                        widthDst = widthDst * this.conv * this.currentStore.getVpWFactor();
                        heightDst = heightDst * this.conv * this.currentStore.getVpHFactor();
                        byte[] bitmap = ((MetaRecord.ByteRecord)mr).bstr;
                        BufferedImage img = this.getImage(bitmap, width, height);
                        if (img == null) continue block62;
                        if (this.opaque) {
                            g2d.drawImage(img, (int)dx, (int)dy, (int)(dx + widthDst), (int)(dy + heightDst), sx, sy, sx + width, sy + height, this.bkgdColor, this.observer);
                            continue block62;
                        }
                        g2d.drawImage(img, (int)dx, (int)dy, (int)(dx + widthDst), (int)(dy + heightDst), sx, sy, sx + width, sy + height, this.observer);
                        continue block62;
                    }
                    case 2368: {
                        int rop = mr.ElementAt(0);
                        float height = (float)mr.ElementAt(1).intValue() * this.conv * this.currentStore.getVpWFactor();
                        float width = (float)mr.ElementAt(2).intValue() * this.conv * this.currentStore.getVpHFactor();
                        int sy = mr.ElementAt(3);
                        int sx = mr.ElementAt(4);
                        float dy = this.conv * this.currentStore.getVpWFactor() * (this.vpY + this.yOffset + (float)mr.ElementAt(5).intValue());
                        float dx = this.conv * this.currentStore.getVpHFactor() * (this.vpX + this.xOffset + (float)mr.ElementAt(6).intValue());
                        if (mr instanceof MetaRecord.ByteRecord) {
                            byte[] bitmap = ((MetaRecord.ByteRecord)mr).bstr;
                            BufferedImage img = this.getImage(bitmap);
                            if (img == null) continue block62;
                            int withSrc = img.getWidth();
                            int heightSrc = img.getHeight();
                            if (this.opaque) {
                                g2d.drawImage(img, (int)dx, (int)dy, (int)(dx + width), (int)(dy + height), sx, sy, sx + withSrc, sy + heightSrc, this.bkgdColor, this.observer);
                                continue block62;
                            }
                            g2d.drawImage(img, (int)dx, (int)dy, (int)(dx + width), (int)(dy + height), sx, sy, sx + withSrc, sy + heightSrc, this.observer);
                            continue block62;
                        }
                        if (!this.opaque) continue block62;
                        Color col = g2d.getColor();
                        g2d.setColor(this.bkgdColor);
                        g2d.fill(new Rectangle2D.Float(dx, dy, width, height));
                        g2d.setColor(col);
                        continue block62;
                    }
                    case 322: {
                        int objIndex = 0;
                        byte[] bitmap = ((MetaRecord.ByteRecord)mr).bstr;
                        objIndex = this.addObjectAt(this.currentStore, 2, bitmap, objIndex);
                        continue block62;
                    }
                }
            }
        }
    }

    private Paint getPaint(byte[] bit) {
        Dimension d = this.getImageDimension(bit);
        BufferedImage img = this.getImage(bit);
        Rectangle2D.Float rec = new Rectangle2D.Float(0.0f, 0.0f, d.width, d.height);
        TexturePaint paint = new TexturePaint(img, rec);
        return paint;
    }

    private void drawString(int flag, Graphics2D g2d, AttributedCharacterIterator ati, float x, float y, TextLayout layout, WMFFont wmfFont, int align) {
        if (wmfFont.escape == 0) {
            if (flag != -1) {
                this.fillTextBackground(-1, flag, g2d, x, y, 0.0f, layout);
            }
            float width = (float)layout.getBounds().getWidth();
            if (align == 6) {
                g2d.drawString(ati, x - width / 2.0f, y);
            } else if (align == 2) {
                g2d.drawString(ati, x - width, y);
            } else {
                g2d.drawString(ati, x, y);
            }
        } else {
            AffineTransform tr = g2d.getTransform();
            float angle = -((float)((double)wmfFont.escape * Math.PI / 1800.0));
            float width = (float)layout.getBounds().getWidth();
            float height = (float)layout.getBounds().getHeight();
            if (align == 6) {
                g2d.translate(-width / 2.0f, height / 2.0f);
                g2d.rotate(angle, x - width / 2.0f, y);
            } else if (align == 2) {
                g2d.translate(-width / 2.0f, height / 2.0f);
                g2d.rotate(angle, x - width, y);
            } else {
                g2d.translate(0.0, height / 2.0f);
                g2d.rotate(angle, x, y);
            }
            if (flag != -1) {
                this.fillTextBackground(align, flag, g2d, x, y, width, layout);
            }
            Stroke _st = g2d.getStroke();
            g2d.setStroke(textSolid);
            g2d.drawString(ati, x, y);
            g2d.setStroke(_st);
            g2d.setTransform(tr);
        }
    }

    private void fillTextBackground(int align, int flag, Graphics2D g2d, float x, float y, float width, TextLayout layout) {
        float _x = x;
        if (align == 6) {
            _x = x - width / 2.0f;
        } else if (align == 2) {
            _x = x - width;
        }
        if ((flag & 2) != 0) {
            Color c = g2d.getColor();
            AffineTransform tr = g2d.getTransform();
            g2d.setColor(this.bkgdColor);
            g2d.translate(_x, y);
            g2d.fill(layout.getBounds());
            g2d.setColor(c);
            g2d.setTransform(tr);
        } else if (this.opaque) {
            Color c = g2d.getColor();
            AffineTransform tr = g2d.getTransform();
            g2d.setColor(this.bkgdColor);
            g2d.translate(_x, y);
            g2d.fill(layout.getBounds());
            g2d.setColor(c);
            g2d.setTransform(tr);
        }
    }

    private void drawPolyPolygon(Graphics2D g2d, List pols) {
        for (Object pol1 : pols) {
            Polygon2D pol = (Polygon2D)pol1;
            g2d.draw((Shape)pol);
        }
    }

    private void fillPolyPolygon(Graphics2D g2d, List pols) {
        if (pols.size() == 1) {
            g2d.fill((Shape)((Polygon2D)pols.get(0)));
        } else {
            GeneralPath path = new GeneralPath(0);
            for (Object pol1 : pols) {
                Polygon2D pol = (Polygon2D)pol1;
                path.append((Shape)pol, false);
            }
            g2d.fill(path);
        }
    }

    private void setStroke(Graphics2D g2d, int penStyle, float penWidth, float scale) {
        float _width = penWidth == 0.0f ? 1.0f : penWidth;
        float _scale = (float)Platform.getScreenResolution() / (float)this.currentStore.getMetaFileUnitsPerInch();
        float factor = scale / _scale;
        _width = _width * _scale * factor;
        _scale = (float)this.currentStore.getWidthPixels() * 1.0f / 350.0f;
        if (penStyle == 0) {
            BasicStroke stroke = new BasicStroke(_width, 0, 1);
            g2d.setStroke(stroke);
        } else if (penStyle == 2) {
            float[] dash = new float[]{1.0f * _scale, 5.0f * _scale};
            BasicStroke stroke = new BasicStroke(_width, 0, 1, 10.0f * _scale, dash, 0.0f);
            g2d.setStroke(stroke);
        } else if (penStyle == 1) {
            float[] dash = new float[]{5.0f * _scale, 2.0f * _scale};
            BasicStroke stroke = new BasicStroke(_width, 0, 1, 10.0f * _scale, dash, 0.0f);
            g2d.setStroke(stroke);
        } else if (penStyle == 3) {
            float[] dash = new float[]{5.0f * _scale, 2.0f * _scale, 1.0f * _scale, 2.0f * _scale};
            BasicStroke stroke = new BasicStroke(_width, 0, 1, 10.0f * _scale, dash, 0.0f);
            g2d.setStroke(stroke);
        } else if (penStyle == 4) {
            float[] dash = new float[]{5.0f * _scale, 2.0f * _scale, 1.0f * _scale, 2.0f * _scale, 1.0f * _scale, 2.0f * _scale};
            BasicStroke stroke = new BasicStroke(_width, 0, 1, 15.0f * _scale, dash, 0.0f);
            g2d.setStroke(stroke);
        } else {
            BasicStroke stroke = new BasicStroke(_width, 0, 1);
            g2d.setStroke(stroke);
        }
    }

    private void setPenColor(WMFRecordStore currentStore, Graphics2D g2d, int penObject) {
        if (penObject >= 0) {
            GdiObject gdiObj = currentStore.getObject(penObject);
            g2d.setColor((Color)gdiObj.obj);
            penObject = -1;
        }
    }

    private int getHorizontalAlignement(int align) {
        int v = align;
        v %= 24;
        if ((v %= 8) >= 6) {
            return 6;
        }
        if (v >= 2) {
            return 2;
        }
        return 0;
    }

    private void setBrushPaint(WMFRecordStore currentStore, Graphics2D g2d, int brushObject) {
        if (brushObject >= 0) {
            GdiObject gdiObj = currentStore.getObject(brushObject);
            if (gdiObj.obj instanceof Color) {
                g2d.setColor((Color)gdiObj.obj);
            } else if (gdiObj.obj instanceof Paint) {
                g2d.setPaint((Paint)gdiObj.obj);
            } else {
                g2d.setPaint(this.getPaint((byte[])gdiObj.obj));
            }
            brushObject = -1;
        }
    }

    private Paint getStoredPaint(WMFRecordStore currentStore, int object) {
        if (object >= 0) {
            GdiObject gdiObj = currentStore.getObject(object);
            if (gdiObj.obj instanceof Paint) {
                return (Paint)gdiObj.obj;
            }
            return this.getPaint((byte[])gdiObj.obj);
        }
        return null;
    }

    private void paint(int brushObject, int penObject, Shape shape, Graphics2D g2d) {
        Paint paint;
        if (brushObject >= 0) {
            paint = this.getStoredPaint(this.currentStore, brushObject);
            if (!this.firstEffectivePaint || !paint.equals(Color.white)) {
                this.setBrushPaint(this.currentStore, g2d, brushObject);
                g2d.fill(shape);
                this.firstEffectivePaint = false;
            }
        }
        if (penObject >= 0) {
            paint = this.getStoredPaint(this.currentStore, penObject);
            if (!this.firstEffectivePaint || !paint.equals(Color.white)) {
                this.setPenColor(this.currentStore, g2d, penObject);
                g2d.draw(shape);
                this.firstEffectivePaint = false;
            }
        }
    }

    private void paintWithPen(int penObject, Shape shape, Graphics2D g2d) {
        if (penObject >= 0) {
            Paint paint = this.getStoredPaint(this.currentStore, penObject);
            if (!this.firstEffectivePaint || !paint.equals(Color.white)) {
                this.setPenColor(this.currentStore, g2d, penObject);
                g2d.draw(shape);
                this.firstEffectivePaint = false;
            }
        }
    }

    private float getVerticalAlignmentValue(TextLayout layout, int vertAlign) {
        if (vertAlign == 8) {
            return -layout.getDescent();
        }
        if (vertAlign == 0) {
            return layout.getAscent();
        }
        return 0.0f;
    }

    @Override
    public WMFRecordStore getRecordStore() {
        return this.currentStore;
    }
}

