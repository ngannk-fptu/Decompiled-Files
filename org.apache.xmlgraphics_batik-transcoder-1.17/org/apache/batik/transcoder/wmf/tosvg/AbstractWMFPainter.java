/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import org.apache.batik.transcoder.wmf.tosvg.WMFFont;
import org.apache.batik.transcoder.wmf.tosvg.WMFRecordStore;

public class AbstractWMFPainter {
    public static final String WMF_FILE_EXTENSION = ".wmf";
    protected WMFFont wmfFont = null;
    protected int currentHorizAlign = 0;
    protected int currentVertAlign = 0;
    public static final int PEN = 1;
    public static final int BRUSH = 2;
    public static final int FONT = 3;
    public static final int NULL_PEN = 4;
    public static final int NULL_BRUSH = 5;
    public static final int PALETTE = 6;
    public static final int OBJ_BITMAP = 7;
    public static final int OBJ_REGION = 8;
    protected WMFRecordStore currentStore;
    protected transient boolean bReadingWMF = true;
    protected transient BufferedInputStream bufStream = null;

    protected BufferedImage getImage(byte[] bit, int width, int height) {
        int _width = (bit[7] & 0xFF) << 24 | (bit[6] & 0xFF) << 16 | (bit[5] & 0xFF) << 8 | bit[4] & 0xFF;
        int _height = (bit[11] & 0xFF) << 24 | (bit[10] & 0xFF) << 16 | (bit[9] & 0xFF) << 8 | bit[8] & 0xFF;
        if (width != _width || height != _height) {
            return null;
        }
        return this.getImage(bit);
    }

    protected Dimension getImageDimension(byte[] bit) {
        int _width = (bit[7] & 0xFF) << 24 | (bit[6] & 0xFF) << 16 | (bit[5] & 0xFF) << 8 | bit[4] & 0xFF;
        int _height = (bit[11] & 0xFF) << 24 | (bit[10] & 0xFF) << 16 | (bit[9] & 0xFF) << 8 | bit[8] & 0xFF;
        return new Dimension(_width, _height);
    }

    protected BufferedImage getImage(byte[] bit) {
        int _width = (bit[7] & 0xFF) << 24 | (bit[6] & 0xFF) << 16 | (bit[5] & 0xFF) << 8 | bit[4] & 0xFF;
        int _height = (bit[11] & 0xFF) << 24 | (bit[10] & 0xFF) << 16 | (bit[9] & 0xFF) << 8 | bit[8] & 0xFF;
        int[] bitI = new int[_width * _height];
        BufferedImage img = new BufferedImage(_width, _height, 1);
        WritableRaster raster = img.getRaster();
        int _headerSize = (bit[3] & 0xFF) << 24 | (bit[2] & 0xFF) << 16 | (bit[1] & 0xFF) << 8 | bit[0] & 0xFF;
        int _planes = (bit[13] & 0xFF) << 8 | bit[12] & 0xFF;
        int _nbit = (bit[15] & 0xFF) << 8 | bit[14] & 0xFF;
        int _size = (bit[23] & 0xFF) << 24 | (bit[22] & 0xFF) << 16 | (bit[21] & 0xFF) << 8 | bit[20] & 0xFF;
        if (_size == 0) {
            _size = ((_width * _nbit + 31 & 0xFFFFFFE0) >> 3) * _height;
        }
        int _clrused = (bit[35] & 0xFF) << 24 | (bit[34] & 0xFF) << 16 | (bit[33] & 0xFF) << 8 | bit[32] & 0xFF;
        if (_nbit == 24) {
            int pad = _size / _height - _width * 3;
            int offset = _headerSize;
            for (int j = 0; j < _height; ++j) {
                for (int i = 0; i < _width; ++i) {
                    bitI[_width * (_height - j - 1) + i] = 0xFF000000 | (bit[offset + 2] & 0xFF) << 16 | (bit[offset + 1] & 0xFF) << 8 | bit[offset] & 0xFF;
                    offset += 3;
                }
                offset += pad;
            }
        } else if (_nbit == 8) {
            int nbColors = 0;
            nbColors = _clrused > 0 ? _clrused : 256;
            int offset = _headerSize;
            int[] palette = new int[nbColors];
            for (int i = 0; i < nbColors; ++i) {
                palette[i] = 0xFF000000 | (bit[offset + 2] & 0xFF) << 16 | (bit[offset + 1] & 0xFF) << 8 | bit[offset] & 0xFF;
                offset += 4;
            }
            _size = bit.length - offset;
            int pad = _size / _height - _width;
            for (int j = 0; j < _height; ++j) {
                for (int i = 0; i < _width; ++i) {
                    bitI[_width * (_height - j - 1) + i] = palette[bit[offset] & 0xFF];
                    ++offset;
                }
                offset += pad;
            }
        } else if (_nbit == 1) {
            int nbColors = 2;
            int offset = _headerSize;
            int[] palette = new int[nbColors];
            for (int i = 0; i < nbColors; ++i) {
                palette[i] = 0xFF000000 | (bit[offset + 2] & 0xFF) << 16 | (bit[offset + 1] & 0xFF) << 8 | bit[offset] & 0xFF;
                offset += 4;
            }
            int pos = 7;
            byte currentByte = bit[offset];
            int pad = _size / _height - _width / 8;
            for (int j = 0; j < _height; ++j) {
                for (int i = 0; i < _width; ++i) {
                    bitI[_width * (_height - j - 1) + i] = (currentByte & 1 << pos) != 0 ? palette[1] : palette[0];
                    if (--pos != -1) continue;
                    pos = 7;
                    if (++offset >= bit.length) continue;
                    currentByte = bit[offset];
                }
                pos = 7;
                if ((offset += pad) >= bit.length) continue;
                currentByte = bit[offset];
            }
        }
        raster.setDataElements(0, 0, _width, _height, bitI);
        return img;
    }

    protected AttributedCharacterIterator getCharacterIterator(Graphics2D g2d, String sr, WMFFont wmffont) {
        return this.getAttributedString(g2d, sr, wmffont).getIterator();
    }

    protected AttributedCharacterIterator getCharacterIterator(Graphics2D g2d, String sr, WMFFont wmffont, int align) {
        AttributedString ats = this.getAttributedString(g2d, sr, wmffont);
        return ats.getIterator();
    }

    protected AttributedString getAttributedString(Graphics2D g2d, String sr, WMFFont wmffont) {
        AttributedString ats = new AttributedString(sr);
        Font font = g2d.getFont();
        ats.addAttribute(TextAttribute.SIZE, Float.valueOf(font.getSize2D()));
        ats.addAttribute(TextAttribute.FONT, font);
        if (this.wmfFont.underline != 0) {
            ats.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }
        if (this.wmfFont.italic != 0) {
            ats.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        } else {
            ats.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
        }
        if (this.wmfFont.weight > 400) {
            ats.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        } else {
            ats.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
        }
        return ats;
    }

    public void setRecordStore(WMFRecordStore currentStore) {
        if (currentStore == null) {
            throw new IllegalArgumentException();
        }
        this.currentStore = currentStore;
    }

    public WMFRecordStore getRecordStore() {
        return this.currentStore;
    }

    protected int addObject(WMFRecordStore store, int type, Object obj) {
        return this.currentStore.addObject(type, obj);
    }

    protected int addObjectAt(WMFRecordStore store, int type, Object obj, int idx) {
        return this.currentStore.addObjectAt(type, obj, idx);
    }
}

