/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.Polygon2D
 *  org.apache.batik.ext.awt.geom.Polyline2D
 */
package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.ext.awt.geom.Polyline2D;
import org.apache.batik.transcoder.wmf.tosvg.AbstractWMFReader;
import org.apache.batik.transcoder.wmf.tosvg.GdiObject;
import org.apache.batik.transcoder.wmf.tosvg.WMFFont;
import org.apache.batik.transcoder.wmf.tosvg.WMFUtilities;

public class WMFHeaderProperties
extends AbstractWMFReader {
    private static final Integer INTEGER_0 = 0;
    protected DataInputStream stream;
    private int _bleft;
    private int _bright;
    private int _btop;
    private int _bbottom;
    private int _bwidth;
    private int _bheight;
    private int _ileft;
    private int _iright;
    private int _itop;
    private int _ibottom;
    private float scale = 1.0f;
    private int startX = 0;
    private int startY = 0;
    private int currentHorizAlign = 0;
    private int currentVertAlign = 0;
    private WMFFont wf = null;
    private static final FontRenderContext fontCtx = new FontRenderContext(new AffineTransform(), false, true);
    private transient boolean firstEffectivePaint = true;
    public static final int PEN = 1;
    public static final int BRUSH = 2;
    public static final int FONT = 3;
    public static final int NULL_PEN = 4;
    public static final int NULL_BRUSH = 5;
    public static final int PALETTE = 6;
    public static final int OBJ_BITMAP = 7;
    public static final int OBJ_REGION = 8;

    public WMFHeaderProperties(File wmffile) throws IOException {
        this.reset();
        this.stream = new DataInputStream(new BufferedInputStream(new FileInputStream(wmffile)));
        this.read(this.stream);
        this.stream.close();
    }

    public WMFHeaderProperties() {
    }

    public void closeResource() {
        try {
            if (this.stream != null) {
                this.stream.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void setFile(File wmffile) throws IOException {
        this.stream = new DataInputStream(new BufferedInputStream(new FileInputStream(wmffile)));
        this.read(this.stream);
        this.stream.close();
    }

    @Override
    public void reset() {
        this.left = 0;
        this.right = 0;
        this.top = 1000;
        this.bottom = 1000;
        this.inch = 84;
        this._bleft = -1;
        this._bright = -1;
        this._btop = -1;
        this._bbottom = -1;
        this._ileft = -1;
        this._iright = -1;
        this._itop = -1;
        this._ibottom = -1;
        this._bwidth = -1;
        this._bheight = -1;
        this.vpW = -1;
        this.vpH = -1;
        this.vpX = 0;
        this.vpY = 0;
        this.startX = 0;
        this.startY = 0;
        this.scaleXY = 1.0f;
        this.firstEffectivePaint = true;
    }

    public DataInputStream getStream() {
        return this.stream;
    }

    @Override
    protected boolean readRecords(DataInputStream is) throws IOException {
        int functionId = 1;
        int recSize = 0;
        int brushObject = -1;
        int penObject = -1;
        int fontObject = -1;
        block34: while (functionId > 0) {
            recSize = this.readInt(is);
            recSize -= 3;
            functionId = this.readShort(is);
            if (functionId <= 0) break;
            switch (functionId) {
                case 259: {
                    short mapmode = this.readShort(is);
                    if (mapmode != 8) continue block34;
                    this.isotropic = false;
                    continue block34;
                }
                case 523: {
                    this.vpY = this.readShort(is);
                    this.vpX = this.readShort(is);
                    continue block34;
                }
                case 524: {
                    this.vpH = this.readShort(is);
                    this.vpW = this.readShort(is);
                    if (!this.isotropic) {
                        this.scaleXY = (float)this.vpW / (float)this.vpH;
                    }
                    this.vpW = (int)((float)this.vpW * this.scaleXY);
                    continue block34;
                }
                case 762: {
                    int objIndex = 0;
                    short penStyle = this.readShort(is);
                    this.readInt(is);
                    int colorref = this.readInt(is);
                    int red = colorref & 0xFF;
                    int green = (colorref & 0xFF00) >> 8;
                    int blue = (colorref & 0xFF0000) >> 16;
                    Color color = new Color(red, green, blue);
                    if (recSize == 6) {
                        this.readShort(is);
                    }
                    if (penStyle == 5) {
                        objIndex = this.addObjectAt(4, color, objIndex);
                        continue block34;
                    }
                    objIndex = this.addObjectAt(1, color, objIndex);
                    continue block34;
                }
                case 764: {
                    int objIndex = 0;
                    short brushStyle = this.readShort(is);
                    int colorref = this.readInt(is);
                    int red = colorref & 0xFF;
                    int green = (colorref & 0xFF00) >> 8;
                    int blue = (colorref & 0xFF0000) >> 16;
                    Color color = new Color(red, green, blue);
                    this.readShort(is);
                    if (brushStyle == 5) {
                        objIndex = this.addObjectAt(5, color, objIndex);
                        continue block34;
                    }
                    objIndex = this.addObjectAt(2, color, objIndex);
                    continue block34;
                }
                case 302: {
                    short align = this.readShort(is);
                    if (recSize > 1) {
                        for (int i = 1; i < recSize; ++i) {
                            this.readShort(is);
                        }
                    }
                    this.currentHorizAlign = WMFUtilities.getHorizontalAlignment(align);
                    this.currentVertAlign = WMFUtilities.getVerticalAlignment(align);
                    continue block34;
                }
                case 2610: {
                    short y = this.readShort(is);
                    int x = (int)((float)this.readShort(is) * this.scaleXY);
                    int lenText = this.readShort(is);
                    short flag = this.readShort(is);
                    int read = 4;
                    boolean clipped = false;
                    int x1 = 0;
                    short y1 = 0;
                    int x2 = 0;
                    short y2 = 0;
                    if ((flag & 4) != 0) {
                        x1 = (int)((float)this.readShort(is) * this.scaleXY);
                        y1 = this.readShort(is);
                        x2 = (int)((float)this.readShort(is) * this.scaleXY);
                        y2 = this.readShort(is);
                        read += 4;
                        clipped = true;
                    }
                    byte[] bstr = new byte[lenText];
                    for (int i = 0; i < lenText; ++i) {
                        bstr[i] = is.readByte();
                    }
                    String sr = WMFUtilities.decodeString(this.wf, bstr);
                    read += (lenText + 1) / 2;
                    if (lenText % 2 != 0) {
                        is.readByte();
                    }
                    if (read < recSize) {
                        for (int j = read; j < recSize; ++j) {
                            this.readShort(is);
                        }
                    }
                    TextLayout layout = new TextLayout(sr, this.wf.font, fontCtx);
                    int lfWidth = (int)layout.getBounds().getWidth();
                    x = (int)layout.getBounds().getX();
                    int lfHeight = (int)this.getVerticalAlignmentValue(layout, this.currentVertAlign);
                    this.resizeBounds(x, y);
                    this.resizeBounds(x + lfWidth, y + lfHeight);
                    this.firstEffectivePaint = false;
                    continue block34;
                }
                case 1313: 
                case 1583: {
                    int len = this.readShort(is);
                    int read = 1;
                    byte[] bstr = new byte[len];
                    for (int i = 0; i < len; ++i) {
                        bstr[i] = is.readByte();
                    }
                    String sr = WMFUtilities.decodeString(this.wf, bstr);
                    if (len % 2 != 0) {
                        is.readByte();
                    }
                    read += (len + 1) / 2;
                    short y = this.readShort(is);
                    int x = (int)((float)this.readShort(is) * this.scaleXY);
                    if ((read += 2) < recSize) {
                        for (int j = read; j < recSize; ++j) {
                            this.readShort(is);
                        }
                    }
                    TextLayout layout = new TextLayout(sr, this.wf.font, fontCtx);
                    int lfWidth = (int)layout.getBounds().getWidth();
                    x = (int)layout.getBounds().getX();
                    int lfHeight = (int)this.getVerticalAlignmentValue(layout, this.currentVertAlign);
                    this.resizeBounds(x, y);
                    this.resizeBounds(x + lfWidth, y + lfHeight);
                    continue block34;
                }
                case 763: {
                    int d;
                    short lfHeight = this.readShort(is);
                    float size = (int)(this.scaleY * (float)lfHeight);
                    short lfWidth = this.readShort(is);
                    short escape = this.readShort(is);
                    short orient = this.readShort(is);
                    short weight = this.readShort(is);
                    byte italic = is.readByte();
                    byte underline = is.readByte();
                    byte strikeOut = is.readByte();
                    int charset = is.readByte() & 0xFF;
                    byte lfOutPrecision = is.readByte();
                    byte lfClipPrecision = is.readByte();
                    byte lfQuality = is.readByte();
                    byte lfPitchAndFamily = is.readByte();
                    int style = italic > 0 ? 2 : 0;
                    style |= weight > 400 ? 1 : 0;
                    int len = 2 * (recSize - 9);
                    byte[] lfFaceName = new byte[len];
                    for (int i = 0; i < len; ++i) {
                        lfFaceName[i] = is.readByte();
                    }
                    String face = new String(lfFaceName);
                    for (d = 0; d < face.length() && (Character.isLetterOrDigit(face.charAt(d)) || Character.isWhitespace(face.charAt(d))); ++d) {
                    }
                    face = d > 0 ? face.substring(0, d) : "System";
                    if (size < 0.0f) {
                        size = -size;
                    }
                    int objIndex = 0;
                    Font f = new Font(face, style, (int)size);
                    f = f.deriveFont(size);
                    WMFFont wf = new WMFFont(f, charset, underline, strikeOut, italic, weight, orient, escape);
                    objIndex = this.addObjectAt(3, wf, objIndex);
                    continue block34;
                }
                case 1791: {
                    int objIndex = 0;
                    for (int j = 0; j < recSize; ++j) {
                        this.readShort(is);
                    }
                    objIndex = this.addObjectAt(6, INTEGER_0, 0);
                    continue block34;
                }
                case 247: {
                    int objIndex = 0;
                    for (int j = 0; j < recSize; ++j) {
                        this.readShort(is);
                    }
                    objIndex = this.addObjectAt(8, INTEGER_0, 0);
                    continue block34;
                }
                case 301: {
                    int gdiIndex = this.readShort(is);
                    if ((gdiIndex & Integer.MIN_VALUE) != 0) continue block34;
                    GdiObject gdiObj = this.getObject(gdiIndex);
                    if (!gdiObj.used) continue block34;
                    switch (gdiObj.type) {
                        case 1: {
                            penObject = gdiIndex;
                            break;
                        }
                        case 2: {
                            brushObject = gdiIndex;
                            break;
                        }
                        case 3: {
                            this.wf = (WMFFont)gdiObj.obj;
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
                    continue block34;
                }
                case 496: {
                    int gdiIndex = this.readShort(is);
                    GdiObject gdiObj = this.getObject(gdiIndex);
                    if (gdiIndex == brushObject) {
                        brushObject = -1;
                    } else if (gdiIndex == penObject) {
                        penObject = -1;
                    } else if (gdiIndex == fontObject) {
                        fontObject = -1;
                    }
                    gdiObj.clear();
                    continue block34;
                }
                case 531: {
                    short y = this.readShort(is);
                    int x = (int)((float)this.readShort(is) * this.scaleXY);
                    if (penObject >= 0) {
                        this.resizeBounds(this.startX, this.startY);
                        this.resizeBounds(x, y);
                        this.firstEffectivePaint = false;
                    }
                    this.startX = x;
                    this.startY = y;
                    continue block34;
                }
                case 532: {
                    this.startY = this.readShort(is);
                    this.startX = (int)((float)this.readShort(is) * this.scaleXY);
                    continue block34;
                }
                case 1336: {
                    int count = this.readShort(is);
                    int[] pts = new int[count];
                    int ptCount = 0;
                    for (int i = 0; i < count; ++i) {
                        pts[i] = this.readShort(is);
                        ptCount += pts[i];
                    }
                    int offset = count + 1;
                    for (int i = 0; i < count; ++i) {
                        for (int j = 0; j < pts[i]; ++j) {
                            int x = (int)((float)this.readShort(is) * this.scaleXY);
                            short y = this.readShort(is);
                            if (brushObject < 0 && penObject < 0) continue;
                            this.resizeBounds(x, y);
                        }
                    }
                    this.firstEffectivePaint = false;
                    continue block34;
                }
                case 804: {
                    int count = this.readShort(is);
                    float[] _xpts = new float[count + 1];
                    float[] _ypts = new float[count + 1];
                    for (int i = 0; i < count; ++i) {
                        _xpts[i] = (float)this.readShort(is) * this.scaleXY;
                        _ypts[i] = this.readShort(is);
                    }
                    _xpts[count] = _xpts[0];
                    _ypts[count] = _ypts[0];
                    Polygon2D pol = new Polygon2D(_xpts, _ypts, count);
                    this.paint(brushObject, penObject, (Shape)pol);
                    continue block34;
                }
                case 805: {
                    int count = this.readShort(is);
                    float[] _xpts = new float[count];
                    float[] _ypts = new float[count];
                    for (int i = 0; i < count; ++i) {
                        _xpts[i] = (float)this.readShort(is) * this.scaleXY;
                        _ypts[i] = this.readShort(is);
                    }
                    Polyline2D pol = new Polyline2D(_xpts, _ypts, count);
                    this.paintWithPen(penObject, (Shape)pol);
                    continue block34;
                }
                case 1046: 
                case 1048: 
                case 1051: {
                    short bot = this.readShort(is);
                    int right = (int)((float)this.readShort(is) * this.scaleXY);
                    short top = this.readShort(is);
                    int left = (int)((float)this.readShort(is) * this.scaleXY);
                    Rectangle2D.Float rec = new Rectangle2D.Float(left, top, right - left, bot - top);
                    this.paint(brushObject, penObject, rec);
                    continue block34;
                }
                case 1564: {
                    this.readShort(is);
                    this.readShort(is);
                    short bot = this.readShort(is);
                    int right = (int)((float)this.readShort(is) * this.scaleXY);
                    short top = this.readShort(is);
                    int left = (int)((float)this.readShort(is) * this.scaleXY);
                    Rectangle2D.Float rec = new Rectangle2D.Float(left, top, right - left, bot - top);
                    this.paint(brushObject, penObject, rec);
                    continue block34;
                }
                case 2071: 
                case 2074: 
                case 2096: {
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    short bot = this.readShort(is);
                    int right = (int)((float)this.readShort(is) * this.scaleXY);
                    short top = this.readShort(is);
                    int left = (int)((float)this.readShort(is) * this.scaleXY);
                    Rectangle2D.Float rec = new Rectangle2D.Float(left, top, right - left, bot - top);
                    this.paint(brushObject, penObject, rec);
                    continue block34;
                }
                case 1565: {
                    this.readInt(is);
                    short height = this.readShort(is);
                    int width = (int)((float)this.readShort(is) * this.scaleXY);
                    int left = (int)((float)this.readShort(is) * this.scaleXY);
                    short top = this.readShort(is);
                    if (penObject >= 0) {
                        this.resizeBounds(left, top);
                    }
                    if (penObject < 0) continue block34;
                    this.resizeBounds(left + width, top + height);
                    continue block34;
                }
                case 2881: {
                    is.readInt();
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    float heightDst = this.readShort(is);
                    float widthDst = (float)this.readShort(is) * this.scaleXY;
                    float dy = (float)this.readShort(is) * this.getVpWFactor() * (float)this.inch / PIXEL_PER_INCH;
                    float dx = (float)this.readShort(is) * this.getVpWFactor() * (float)this.inch / PIXEL_PER_INCH * this.scaleXY;
                    widthDst = widthDst * this.getVpWFactor() * (float)this.inch / PIXEL_PER_INCH;
                    heightDst = heightDst * this.getVpHFactor() * (float)this.inch / PIXEL_PER_INCH;
                    this.resizeImageBounds((int)dx, (int)dy);
                    this.resizeImageBounds((int)(dx + widthDst), (int)(dy + heightDst));
                    int len = 2 * recSize - 20;
                    for (int i = 0; i < len; ++i) {
                        is.readByte();
                    }
                    continue block34;
                }
                case 3907: {
                    is.readInt();
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    float heightDst = this.readShort(is);
                    float widthDst = (float)this.readShort(is) * this.scaleXY;
                    float dy = (float)this.readShort(is) * this.getVpHFactor() * (float)this.inch / PIXEL_PER_INCH;
                    float dx = (float)this.readShort(is) * this.getVpHFactor() * (float)this.inch / PIXEL_PER_INCH * this.scaleXY;
                    widthDst = widthDst * this.getVpWFactor() * (float)this.inch / PIXEL_PER_INCH;
                    heightDst = heightDst * this.getVpHFactor() * (float)this.inch / PIXEL_PER_INCH;
                    this.resizeImageBounds((int)dx, (int)dy);
                    this.resizeImageBounds((int)(dx + widthDst), (int)(dy + heightDst));
                    int len = 2 * recSize - 22;
                    byte[] bitmap = new byte[len];
                    for (int i = 0; i < len; ++i) {
                        bitmap[i] = is.readByte();
                    }
                    continue block34;
                }
                case 2368: {
                    is.readInt();
                    this.readShort(is);
                    this.readShort(is);
                    this.readShort(is);
                    float height = (float)this.readShort(is) * (float)this.inch / PIXEL_PER_INCH * this.getVpHFactor();
                    float width = (float)this.readShort(is) * (float)this.inch / PIXEL_PER_INCH * this.getVpWFactor() * this.scaleXY;
                    float dy = (float)this.inch / PIXEL_PER_INCH * this.getVpHFactor() * (float)this.readShort(is);
                    float dx = (float)this.inch / PIXEL_PER_INCH * this.getVpWFactor() * (float)this.readShort(is) * this.scaleXY;
                    this.resizeImageBounds((int)dx, (int)dy);
                    this.resizeImageBounds((int)(dx + width), (int)(dy + height));
                    continue block34;
                }
            }
            for (int j = 0; j < recSize; ++j) {
                this.readShort(is);
            }
        }
        if (!this.isAldus) {
            this.width = this.vpW;
            this.height = this.vpH;
            this.right = this.vpX;
            this.left = this.vpX + this.vpW;
            this.top = this.vpY;
            this.bottom = this.vpY + this.vpH;
        }
        this.resetBounds();
        return true;
    }

    public int getWidthBoundsPixels() {
        return this._bwidth;
    }

    public int getHeightBoundsPixels() {
        return this._bheight;
    }

    public int getWidthBoundsUnits() {
        return (int)((float)this.inch * (float)this._bwidth / PIXEL_PER_INCH);
    }

    public int getHeightBoundsUnits() {
        return (int)((float)this.inch * (float)this._bheight / PIXEL_PER_INCH);
    }

    public int getXOffset() {
        return this._bleft;
    }

    public int getYOffset() {
        return this._btop;
    }

    private void resetBounds() {
        this.scale = (float)this.getWidthPixels() / (float)this.vpW;
        if (this._bright != -1) {
            this._bright = (int)(this.scale * (float)(this.vpX + this._bright));
            this._bleft = (int)(this.scale * (float)(this.vpX + this._bleft));
            this._bbottom = (int)(this.scale * (float)(this.vpY + this._bbottom));
            this._btop = (int)(this.scale * (float)(this.vpY + this._btop));
        }
        if (this._iright != -1) {
            this._iright = (int)((float)this._iright * (float)this.getWidthPixels() / (float)this.width);
            this._ileft = (int)((float)this._ileft * (float)this.getWidthPixels() / (float)this.width);
            this._ibottom = (int)((float)this._ibottom * (float)this.getWidthPixels() / (float)this.width);
            this._itop = (int)((float)this._itop * (float)this.getWidthPixels() / (float)this.width);
            if (this._bright == -1 || this._iright > this._bright) {
                this._bright = this._iright;
            }
            if (this._bleft == -1 || this._ileft < this._bleft) {
                this._bleft = this._ileft;
            }
            if (this._btop == -1 || this._itop < this._btop) {
                this._btop = this._itop;
            }
            if (this._bbottom == -1 || this._ibottom > this._bbottom) {
                this._bbottom = this._ibottom;
            }
        }
        if (this._bleft != -1 && this._bright != -1) {
            this._bwidth = this._bright - this._bleft;
        }
        if (this._btop != -1 && this._bbottom != -1) {
            this._bheight = this._bbottom - this._btop;
        }
    }

    private void resizeBounds(int x, int y) {
        if (this._bleft == -1) {
            this._bleft = x;
        } else if (x < this._bleft) {
            this._bleft = x;
        }
        if (this._bright == -1) {
            this._bright = x;
        } else if (x > this._bright) {
            this._bright = x;
        }
        if (this._btop == -1) {
            this._btop = y;
        } else if (y < this._btop) {
            this._btop = y;
        }
        if (this._bbottom == -1) {
            this._bbottom = y;
        } else if (y > this._bbottom) {
            this._bbottom = y;
        }
    }

    private void resizeImageBounds(int x, int y) {
        if (this._ileft == -1) {
            this._ileft = x;
        } else if (x < this._ileft) {
            this._ileft = x;
        }
        if (this._iright == -1) {
            this._iright = x;
        } else if (x > this._iright) {
            this._iright = x;
        }
        if (this._itop == -1) {
            this._itop = y;
        } else if (y < this._itop) {
            this._itop = y;
        }
        if (this._ibottom == -1) {
            this._ibottom = y;
        } else if (y > this._ibottom) {
            this._ibottom = y;
        }
    }

    private Color getColorFromObject(int brushObject) {
        Object color = null;
        if (brushObject >= 0) {
            GdiObject gdiObj = this.getObject(brushObject);
            return (Color)gdiObj.obj;
        }
        return null;
    }

    private void paint(int brushObject, int penObject, Shape shape) {
        if (brushObject >= 0 || penObject >= 0) {
            Color col = brushObject >= 0 ? this.getColorFromObject(brushObject) : this.getColorFromObject(penObject);
            if (!this.firstEffectivePaint || !col.equals(Color.white)) {
                Rectangle rec = shape.getBounds();
                this.resizeBounds((int)rec.getMinX(), (int)rec.getMinY());
                this.resizeBounds((int)rec.getMaxX(), (int)rec.getMaxY());
                this.firstEffectivePaint = false;
            }
        }
    }

    private void paintWithPen(int penObject, Shape shape) {
        if (penObject >= 0) {
            Color col = this.getColorFromObject(penObject);
            if (!this.firstEffectivePaint || !col.equals(Color.white)) {
                Rectangle rec = shape.getBounds();
                this.resizeBounds((int)rec.getMinX(), (int)rec.getMinY());
                this.resizeBounds((int)rec.getMaxX(), (int)rec.getMaxY());
                this.firstEffectivePaint = false;
            }
        }
    }

    private float getVerticalAlignmentValue(TextLayout layout, int vertAlign) {
        if (vertAlign == 24) {
            return -layout.getAscent();
        }
        if (vertAlign == 0) {
            return layout.getAscent() + layout.getDescent();
        }
        return 0.0f;
    }
}

