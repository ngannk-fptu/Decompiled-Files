/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.font.PDCIDSystemInfo;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontLike;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDVectorFont;
import org.apache.pdfbox.util.Vector;

public abstract class PDCIDFont
implements COSObjectable,
PDFontLike,
PDVectorFont {
    private static final Log LOG = LogFactory.getLog(PDCIDFont.class);
    protected final PDType0Font parent;
    private Map<Integer, Float> widths;
    private float defaultWidth;
    private float averageWidth;
    private final Map<Integer, Float> verticalDisplacementY = new HashMap<Integer, Float>();
    private final Map<Integer, Vector> positionVectors = new HashMap<Integer, Vector>();
    private float[] dw2 = new float[]{880.0f, -1000.0f};
    protected final COSDictionary dict;
    private PDFontDescriptor fontDescriptor;

    PDCIDFont(COSDictionary fontDictionary, PDType0Font parent) {
        this.dict = fontDictionary;
        this.parent = parent;
        this.readWidths();
        this.readVerticalDisplacements();
    }

    private void readWidths() {
        this.widths = new HashMap<Integer, Float>();
        COSBase wBase = this.dict.getDictionaryObject(COSName.W);
        if (wBase instanceof COSArray) {
            COSArray wArray = (COSArray)wBase;
            int size = wArray.size();
            int counter = 0;
            while (counter < size - 1) {
                COSBase next;
                COSBase firstCodeBase;
                if (!((firstCodeBase = wArray.getObject(counter++)) instanceof COSNumber)) {
                    LOG.warn((Object)("Expected a number array member, got " + firstCodeBase));
                    continue;
                }
                COSNumber firstCode = (COSNumber)firstCodeBase;
                if ((next = wArray.getObject(counter++)) instanceof COSArray) {
                    COSArray array = (COSArray)next;
                    int startRange = firstCode.intValue();
                    int arraySize = array.size();
                    for (int i = 0; i < arraySize; ++i) {
                        COSBase widthBase = array.getObject(i);
                        if (widthBase instanceof COSNumber) {
                            COSNumber width = (COSNumber)widthBase;
                            this.widths.put(startRange + i, Float.valueOf(width.floatValue()));
                            continue;
                        }
                        LOG.warn((Object)("Expected a number array member, got " + widthBase));
                    }
                    continue;
                }
                if (counter >= size) {
                    LOG.warn((Object)"premature end of widths array");
                    break;
                }
                COSBase secondCodeBase = next;
                COSBase rangeWidthBase = wArray.getObject(counter++);
                if (!(secondCodeBase instanceof COSNumber) || !(rangeWidthBase instanceof COSNumber)) {
                    LOG.warn((Object)("Expected two numbers, got " + secondCodeBase + " and " + rangeWidthBase));
                    continue;
                }
                COSNumber secondCode = (COSNumber)secondCodeBase;
                COSNumber rangeWidth = (COSNumber)rangeWidthBase;
                int startRange = firstCode.intValue();
                int endRange = secondCode.intValue();
                float width = rangeWidth.floatValue();
                for (int i = startRange; i <= endRange; ++i) {
                    this.widths.put(i, Float.valueOf(width));
                }
            }
        }
    }

    private void readVerticalDisplacements() {
        COSBase w2Base;
        COSBase dw2Base = this.dict.getDictionaryObject(COSName.DW2);
        if (dw2Base instanceof COSArray) {
            COSArray dw2Array = (COSArray)dw2Base;
            COSBase base0 = dw2Array.getObject(0);
            COSBase base1 = dw2Array.getObject(1);
            if (base0 instanceof COSNumber && base1 instanceof COSNumber) {
                this.dw2[0] = ((COSNumber)base0).floatValue();
                this.dw2[1] = ((COSNumber)base1).floatValue();
            }
        }
        if ((w2Base = this.dict.getDictionaryObject(COSName.W2)) instanceof COSArray) {
            COSArray w2Array = (COSArray)w2Base;
            for (int i = 0; i < w2Array.size(); ++i) {
                COSBase next;
                COSNumber c = (COSNumber)w2Array.getObject(i);
                if ((next = w2Array.getObject(++i)) instanceof COSArray) {
                    COSArray array = (COSArray)next;
                    for (int j = 0; j < array.size(); ++j) {
                        int cid = c.intValue() + j / 3;
                        COSNumber w1y = (COSNumber)array.getObject(j);
                        COSNumber v1x = (COSNumber)array.getObject(++j);
                        COSNumber v1y = (COSNumber)array.getObject(++j);
                        this.verticalDisplacementY.put(cid, Float.valueOf(w1y.floatValue()));
                        this.positionVectors.put(cid, new Vector(v1x.floatValue(), v1y.floatValue()));
                    }
                    continue;
                }
                int first = c.intValue();
                int last = ((COSNumber)next).intValue();
                COSNumber w1y = (COSNumber)w2Array.getObject(++i);
                COSNumber v1x = (COSNumber)w2Array.getObject(++i);
                COSNumber v1y = (COSNumber)w2Array.getObject(++i);
                for (int cid = first; cid <= last; ++cid) {
                    this.verticalDisplacementY.put(cid, Float.valueOf(w1y.floatValue()));
                    this.positionVectors.put(cid, new Vector(v1x.floatValue(), v1y.floatValue()));
                }
            }
        }
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    public String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    @Override
    public String getName() {
        return this.getBaseFont();
    }

    @Override
    public PDFontDescriptor getFontDescriptor() {
        COSDictionary fd;
        if (this.fontDescriptor == null && (fd = (COSDictionary)this.dict.getDictionaryObject(COSName.FONT_DESC)) != null) {
            this.fontDescriptor = new PDFontDescriptor(fd);
        }
        return this.fontDescriptor;
    }

    public final PDType0Font getParent() {
        return this.parent;
    }

    private float getDefaultWidth() {
        if (this.defaultWidth == 0.0f) {
            COSBase base = this.dict.getDictionaryObject(COSName.DW);
            this.defaultWidth = base instanceof COSNumber ? ((COSNumber)base).floatValue() : 1000.0f;
        }
        return this.defaultWidth;
    }

    private Vector getDefaultPositionVector(int cid) {
        return new Vector(this.getWidthForCID(cid) / 2.0f, this.dw2[0]);
    }

    private float getWidthForCID(int cid) {
        Float width = this.widths.get(cid);
        if (width == null) {
            width = Float.valueOf(this.getDefaultWidth());
        }
        return width.floatValue();
    }

    @Override
    public boolean hasExplicitWidth(int code) throws IOException {
        return this.widths.get(this.codeToCID(code)) != null;
    }

    @Override
    public Vector getPositionVector(int code) {
        int cid = this.codeToCID(code);
        Vector v = this.positionVectors.get(cid);
        if (v == null) {
            v = this.getDefaultPositionVector(cid);
        }
        return v;
    }

    public float getVerticalDisplacementVectorY(int code) {
        int cid = this.codeToCID(code);
        Float w1y = this.verticalDisplacementY.get(cid);
        if (w1y == null) {
            w1y = Float.valueOf(this.dw2[1]);
        }
        return w1y.floatValue();
    }

    @Override
    public float getWidth(int code) throws IOException {
        return this.getWidthForCID(this.codeToCID(code));
    }

    @Override
    public float getAverageFontWidth() {
        if (this.averageWidth == 0.0f) {
            float totalWidths = 0.0f;
            int characterCount = 0;
            if (this.widths != null) {
                for (Float width : this.widths.values()) {
                    if (!(width.floatValue() > 0.0f)) continue;
                    totalWidths += width.floatValue();
                    ++characterCount;
                }
            }
            if (characterCount != 0) {
                this.averageWidth = totalWidths / (float)characterCount;
            }
            if (this.averageWidth <= 0.0f || Float.isNaN(this.averageWidth)) {
                this.averageWidth = this.getDefaultWidth();
            }
        }
        return this.averageWidth;
    }

    public PDCIDSystemInfo getCIDSystemInfo() {
        COSBase base = this.dict.getDictionaryObject(COSName.CIDSYSTEMINFO);
        if (base instanceof COSDictionary) {
            return new PDCIDSystemInfo((COSDictionary)base);
        }
        return null;
    }

    public abstract int codeToCID(int var1);

    public abstract int codeToGID(int var1) throws IOException;

    protected abstract byte[] encode(int var1) throws IOException;

    final int[] readCIDToGIDMap() throws IOException {
        int[] cid2gid = null;
        COSBase map = this.dict.getDictionaryObject(COSName.CID_TO_GID_MAP);
        if (map instanceof COSStream) {
            COSStream stream = (COSStream)map;
            COSInputStream is = stream.createInputStream();
            byte[] mapAsBytes = IOUtils.toByteArray(is);
            IOUtils.closeQuietly(is);
            int numberOfInts = mapAsBytes.length / 2;
            cid2gid = new int[numberOfInts];
            int offset = 0;
            for (int index = 0; index < numberOfInts; ++index) {
                int gid;
                cid2gid[index] = gid = (mapAsBytes[offset] & 0xFF) << 8 | mapAsBytes[offset + 1] & 0xFF;
                offset += 2;
            }
        }
        return cid2gid;
    }
}

