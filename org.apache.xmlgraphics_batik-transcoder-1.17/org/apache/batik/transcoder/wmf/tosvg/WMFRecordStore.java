/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder.wmf.tosvg;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.batik.transcoder.wmf.tosvg.AbstractWMFReader;
import org.apache.batik.transcoder.wmf.tosvg.MetaRecord;

public class WMFRecordStore
extends AbstractWMFReader {
    private URL url;
    protected int numRecords;
    protected float vpX;
    protected float vpY;
    protected List records;
    private boolean _bext = true;

    public WMFRecordStore() {
        this.reset();
    }

    @Override
    public void reset() {
        this.numRecords = 0;
        this.vpX = 0.0f;
        this.vpY = 0.0f;
        this.vpW = 1000;
        this.vpH = 1000;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleXY = 1.0f;
        this.inch = 84;
        this.records = new ArrayList(20);
    }

    @Override
    protected boolean readRecords(DataInputStream is) throws IOException {
        int functionId = 1;
        int recSize = 0;
        this.numRecords = 0;
        while (functionId > 0) {
            recSize = this.readInt(is);
            recSize -= 3;
            functionId = this.readShort(is);
            if (functionId <= 0) break;
            MetaRecord mr = new MetaRecord();
            switch (functionId) {
                case 259: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    short mapmode = this.readShort(is);
                    if (mapmode == 8) {
                        this.isotropic = false;
                    }
                    mr.addElement(mapmode);
                    this.records.add(mr);
                    break;
                }
                case 1583: {
                    for (int i = 0; i < recSize; ++i) {
                        short recData = this.readShort(is);
                    }
                    --this.numRecords;
                    break;
                }
                case 2610: {
                    int i;
                    int yVal = this.readShort(is) * this.ySign;
                    int xVal = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int lenText = this.readShort(is);
                    short flag = this.readShort(is);
                    int read = 4;
                    boolean clipped = false;
                    int x1 = 0;
                    int y1 = 0;
                    int x2 = 0;
                    int y2 = 0;
                    if ((flag & 4) != 0) {
                        x1 = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                        y1 = this.readShort(is) * this.ySign;
                        x2 = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                        y2 = this.readShort(is) * this.ySign;
                        read += 4;
                        clipped = true;
                    }
                    byte[] bstr = new byte[lenText];
                    for (i = 0; i < lenText; ++i) {
                        bstr[i] = is.readByte();
                    }
                    read += (lenText + 1) / 2;
                    if (lenText % 2 != 0) {
                        is.readByte();
                    }
                    if (read < recSize) {
                        for (int j = read; j < recSize; ++j) {
                            this.readShort(is);
                        }
                    }
                    mr = new MetaRecord.ByteRecord(bstr);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(xVal);
                    mr.addElement(yVal);
                    mr.addElement(flag);
                    if (clipped) {
                        mr.addElement(x1);
                        mr.addElement(y1);
                        mr.addElement(x2);
                        mr.addElement(y2);
                    }
                    this.records.add(mr);
                    break;
                }
                case 1313: {
                    int i;
                    int len = this.readShort(is);
                    int read = 1;
                    byte[] bstr = new byte[len];
                    for (i = 0; i < len; ++i) {
                        bstr[i] = is.readByte();
                    }
                    if (len % 2 != 0) {
                        is.readByte();
                    }
                    read += (len + 1) / 2;
                    int yVal = this.readShort(is) * this.ySign;
                    int xVal = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    if ((read += 2) < recSize) {
                        for (int j = read; j < recSize; ++j) {
                            this.readShort(is);
                        }
                    }
                    mr = new MetaRecord.ByteRecord(bstr);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(xVal);
                    mr.addElement(yVal);
                    this.records.add(mr);
                    break;
                }
                case 763: {
                    short lfHeight = this.readShort(is);
                    short lfWidth = this.readShort(is);
                    short lfEscapement = this.readShort(is);
                    short lfOrientation = this.readShort(is);
                    short lfWeight = this.readShort(is);
                    byte lfItalic = is.readByte();
                    byte lfUnderline = is.readByte();
                    byte lfStrikeOut = is.readByte();
                    int lfCharSet = is.readByte() & 0xFF;
                    byte lfOutPrecision = is.readByte();
                    byte lfClipPrecision = is.readByte();
                    byte lfQuality = is.readByte();
                    byte lfPitchAndFamily = is.readByte();
                    int len = 2 * (recSize - 9);
                    byte[] lfFaceName = new byte[len];
                    for (int i = 0; i < len; ++i) {
                        lfFaceName[i] = is.readByte();
                    }
                    String str = new String(lfFaceName);
                    mr = new MetaRecord.StringRecord(str);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(lfHeight);
                    mr.addElement(lfItalic);
                    mr.addElement(lfWeight);
                    mr.addElement(lfCharSet);
                    mr.addElement(lfUnderline);
                    mr.addElement(lfStrikeOut);
                    mr.addElement(lfOrientation);
                    mr.addElement(lfEscapement);
                    this.records.add(mr);
                    break;
                }
                case 523: 
                case 524: 
                case 525: 
                case 526: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    short height = this.readShort(is);
                    short width = this.readShort(is);
                    if (width < 0) {
                        width = -width;
                        this.xSign = -1;
                    }
                    if (height < 0) {
                        height = -height;
                        this.ySign = -1;
                    }
                    if (this._bext && functionId == 524) {
                        this.vpW = width;
                        this.vpH = height;
                        this._bext = false;
                    }
                    if (!this.isAldus) {
                        this.width = this.vpW;
                        this.height = this.vpH;
                    }
                    mr.addElement((int)((float)width * this.scaleXY));
                    mr.addElement(height);
                    this.records.add(mr);
                    break;
                }
                case 527: 
                case 529: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int y = this.readShort(is) * this.ySign;
                    int x = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    mr.addElement(x);
                    mr.addElement(y);
                    this.records.add(mr);
                    break;
                }
                case 1040: 
                case 1042: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    short ydenom = this.readShort(is);
                    short ynum = this.readShort(is);
                    short xdenom = this.readShort(is);
                    short xnum = this.readShort(is);
                    mr.addElement(xdenom);
                    mr.addElement(ydenom);
                    mr.addElement(xnum);
                    mr.addElement(ynum);
                    this.records.add(mr);
                    this.scaleX = this.scaleX * (float)xdenom / (float)xnum;
                    this.scaleY = this.scaleY * (float)ydenom / (float)ynum;
                    break;
                }
                case 764: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(this.readShort(is));
                    int colorref = this.readInt(is);
                    int red = colorref & 0xFF;
                    int green = (colorref & 0xFF00) >> 8;
                    int blue = (colorref & 0xFF0000) >> 16;
                    int flags = (colorref & 0x3000000) >> 24;
                    mr.addElement(red);
                    mr.addElement(green);
                    mr.addElement(blue);
                    mr.addElement(this.readShort(is));
                    this.records.add(mr);
                    break;
                }
                case 762: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(this.readShort(is));
                    int width = this.readInt(is);
                    int colorref = this.readInt(is);
                    if (recSize == 6) {
                        this.readShort(is);
                    }
                    int red = colorref & 0xFF;
                    int green = (colorref & 0xFF00) >> 8;
                    int blue = (colorref & 0xFF0000) >> 16;
                    int flags = (colorref & 0x3000000) >> 24;
                    mr.addElement(red);
                    mr.addElement(green);
                    mr.addElement(blue);
                    mr.addElement(width);
                    this.records.add(mr);
                    break;
                }
                case 302: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    short align = this.readShort(is);
                    if (recSize > 1) {
                        for (int i = 1; i < recSize; ++i) {
                            this.readShort(is);
                        }
                    }
                    mr.addElement(align);
                    this.records.add(mr);
                    break;
                }
                case 513: 
                case 521: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int colorref = this.readInt(is);
                    int red = colorref & 0xFF;
                    int green = (colorref & 0xFF00) >> 8;
                    int blue = (colorref & 0xFF0000) >> 16;
                    int flags = (colorref & 0x3000000) >> 24;
                    mr.addElement(red);
                    mr.addElement(green);
                    mr.addElement(blue);
                    this.records.add(mr);
                    break;
                }
                case 531: 
                case 532: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int y = this.readShort(is) * this.ySign;
                    int x = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    mr.addElement(x);
                    mr.addElement(y);
                    this.records.add(mr);
                    break;
                }
                case 262: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int mode = this.readShort(is);
                    if (recSize > 1) {
                        for (int i = 1; i < recSize; ++i) {
                            this.readShort(is);
                        }
                    }
                    mr.addElement(mode);
                    this.records.add(mr);
                    break;
                }
                case 1336: {
                    int i;
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int count = this.readShort(is);
                    int[] pts = new int[count];
                    int ptCount = 0;
                    for (i = 0; i < count; ++i) {
                        pts[i] = this.readShort(is);
                        ptCount += pts[i];
                    }
                    mr.addElement(count);
                    for (i = 0; i < count; ++i) {
                        mr.addElement(pts[i]);
                    }
                    int offset = count + 1;
                    for (int i2 = 0; i2 < count; ++i2) {
                        int nPoints = pts[i2];
                        for (int j = 0; j < nPoints; ++j) {
                            mr.addElement((int)((float)(this.readShort(is) * this.xSign) * this.scaleXY));
                            mr.addElement(this.readShort(is) * this.ySign);
                        }
                    }
                    this.records.add(mr);
                    break;
                }
                case 804: 
                case 805: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int count = this.readShort(is);
                    mr.addElement(count);
                    for (int i = 0; i < count; ++i) {
                        mr.addElement((int)((float)(this.readShort(is) * this.xSign) * this.scaleXY));
                        mr.addElement(this.readShort(is) * this.ySign);
                    }
                    this.records.add(mr);
                    break;
                }
                case 1046: 
                case 1048: 
                case 1051: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int bottom = this.readShort(is) * this.ySign;
                    int right = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int top = this.readShort(is) * this.ySign;
                    int left = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    mr.addElement(left);
                    mr.addElement(top);
                    mr.addElement(right);
                    mr.addElement(bottom);
                    this.records.add(mr);
                    break;
                }
                case 1791: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int left = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int top = this.readShort(is) * this.ySign;
                    int right = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int bottom = this.readShort(is) * this.ySign;
                    mr.addElement(left);
                    mr.addElement(top);
                    mr.addElement(right);
                    mr.addElement(bottom);
                    this.records.add(mr);
                    break;
                }
                case 1564: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int el_height = this.readShort(is) * this.ySign;
                    int el_width = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int bottom = this.readShort(is) * this.ySign;
                    int right = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int top = this.readShort(is) * this.ySign;
                    int left = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    mr.addElement(left);
                    mr.addElement(top);
                    mr.addElement(right);
                    mr.addElement(bottom);
                    mr.addElement(el_width);
                    mr.addElement(el_height);
                    this.records.add(mr);
                    break;
                }
                case 2071: 
                case 2074: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int yend = this.readShort(is) * this.ySign;
                    int xend = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int ystart = this.readShort(is) * this.ySign;
                    int xstart = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int bottom = this.readShort(is) * this.ySign;
                    int right = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int top = this.readShort(is) * this.ySign;
                    int left = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    mr.addElement(left);
                    mr.addElement(top);
                    mr.addElement(right);
                    mr.addElement(bottom);
                    mr.addElement(xstart);
                    mr.addElement(ystart);
                    mr.addElement(xend);
                    mr.addElement(yend);
                    this.records.add(mr);
                    break;
                }
                case 1565: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int rop = this.readInt(is);
                    int height = this.readShort(is) * this.ySign;
                    int width = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int left = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int top = this.readShort(is) * this.ySign;
                    mr.addElement(rop);
                    mr.addElement(height);
                    mr.addElement(width);
                    mr.addElement(top);
                    mr.addElement(left);
                    this.records.add(mr);
                    break;
                }
                case 258: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int mode = this.readShort(is);
                    mr.addElement(mode);
                    if (recSize > 1) {
                        for (int i = 1; i < recSize; ++i) {
                            this.readShort(is);
                        }
                    }
                    this.records.add(mr);
                    break;
                }
                case 260: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    int rop = recSize == 1 ? (int)this.readShort(is) : this.readInt(is);
                    mr.addElement(rop);
                    this.records.add(mr);
                    break;
                }
                case 2881: {
                    int mode = is.readInt() & 0xFF;
                    int heightSrc = this.readShort(is) * this.ySign;
                    int widthSrc = this.readShort(is) * this.xSign;
                    int sy = this.readShort(is) * this.ySign;
                    int sx = this.readShort(is) * this.xSign;
                    int heightDst = this.readShort(is) * this.ySign;
                    int widthDst = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int dy = this.readShort(is) * this.ySign;
                    int dx = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int len = 2 * recSize - 20;
                    byte[] bitmap = new byte[len];
                    for (int i = 0; i < len; ++i) {
                        bitmap[i] = is.readByte();
                    }
                    mr = new MetaRecord.ByteRecord(bitmap);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(mode);
                    mr.addElement(heightSrc);
                    mr.addElement(widthSrc);
                    mr.addElement(sy);
                    mr.addElement(sx);
                    mr.addElement(heightDst);
                    mr.addElement(widthDst);
                    mr.addElement(dy);
                    mr.addElement(dx);
                    this.records.add(mr);
                    break;
                }
                case 3907: {
                    int i;
                    int mode = is.readInt() & 0xFF;
                    short usage = this.readShort(is);
                    int heightSrc = this.readShort(is) * this.ySign;
                    int widthSrc = this.readShort(is) * this.xSign;
                    int sy = this.readShort(is) * this.ySign;
                    int sx = this.readShort(is) * this.xSign;
                    int heightDst = this.readShort(is) * this.ySign;
                    int widthDst = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int dy = this.readShort(is) * this.ySign;
                    int dx = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int len = 2 * recSize - 22;
                    byte[] bitmap = new byte[len];
                    for (i = 0; i < len; ++i) {
                        bitmap[i] = is.readByte();
                    }
                    mr = new MetaRecord.ByteRecord(bitmap);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(mode);
                    mr.addElement(heightSrc);
                    mr.addElement(widthSrc);
                    mr.addElement(sy);
                    mr.addElement(sx);
                    mr.addElement(heightDst);
                    mr.addElement(widthDst);
                    mr.addElement(dy);
                    mr.addElement(dx);
                    this.records.add(mr);
                    break;
                }
                case 2368: {
                    int mode = is.readInt() & 0xFF;
                    short sy = this.readShort(is);
                    short sx = this.readShort(is);
                    short hdc = this.readShort(is);
                    short height = this.readShort(is);
                    int width = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    short dy = this.readShort(is);
                    int dx = (int)((float)(this.readShort(is) * this.xSign) * this.scaleXY);
                    int len = 2 * recSize - 18;
                    if (len > 0) {
                        byte[] bitmap = new byte[len];
                        for (int i = 0; i < len; ++i) {
                            bitmap[i] = is.readByte();
                        }
                        mr = new MetaRecord.ByteRecord(bitmap);
                        mr.numPoints = recSize;
                        mr.functionId = functionId;
                    } else {
                        mr.numPoints = recSize;
                        mr.functionId = functionId;
                        for (int i = 0; i < len; ++i) {
                            is.readByte();
                        }
                    }
                    mr.addElement(mode);
                    mr.addElement(height);
                    mr.addElement(width);
                    mr.addElement(sy);
                    mr.addElement(sx);
                    mr.addElement(dy);
                    mr.addElement(dx);
                    this.records.add(mr);
                    break;
                }
                case 322: {
                    int i;
                    int type = is.readInt() & 0xFF;
                    int len = 2 * recSize - 4;
                    byte[] bitmap = new byte[len];
                    for (i = 0; i < len; ++i) {
                        bitmap[i] = is.readByte();
                    }
                    mr = new MetaRecord.ByteRecord(bitmap);
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    mr.addElement(type);
                    this.records.add(mr);
                    break;
                }
                default: {
                    mr.numPoints = recSize;
                    mr.functionId = functionId;
                    for (int j = 0; j < recSize; ++j) {
                        mr.addElement(this.readShort(is));
                    }
                    this.records.add(mr);
                }
            }
            ++this.numRecords;
        }
        if (!this.isAldus) {
            this.right = (int)this.vpX;
            this.left = (int)(this.vpX + (float)this.vpW);
            this.top = (int)this.vpY;
            this.bottom = (int)(this.vpY + (float)this.vpH);
        }
        this.setReading(false);
        return true;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(URL newUrl) {
        this.url = newUrl;
    }

    public MetaRecord getRecord(int idx) {
        return (MetaRecord)this.records.get(idx);
    }

    public int getNumRecords() {
        return this.numRecords;
    }

    public float getVpX() {
        return this.vpX;
    }

    public float getVpY() {
        return this.vpY;
    }

    public void setVpX(float newValue) {
        this.vpX = newValue;
    }

    public void setVpY(float newValue) {
        this.vpY = newValue;
    }
}

