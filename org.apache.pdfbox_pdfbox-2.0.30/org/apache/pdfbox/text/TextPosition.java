/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.text;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;

public final class TextPosition {
    private static final Log LOG = LogFactory.getLog(TextPosition.class);
    private static final Map<Integer, String> DIACRITICS = TextPosition.createDiacritics();
    private final Matrix textMatrix;
    private final float endX;
    private final float endY;
    private final float maxHeight;
    private final int rotation;
    private final float x;
    private final float y;
    private final float pageHeight;
    private final float pageWidth;
    private final float widthOfSpace;
    private final int[] charCodes;
    private final PDFont font;
    private final float fontSize;
    private final int fontSizePt;
    private float[] widths;
    private String unicode;
    private float direction = -1.0f;

    public TextPosition(int pageRotation, float pageWidth, float pageHeight, Matrix textMatrix, float endX, float endY, float maxHeight, float individualWidth, float spaceWidth, String unicode, int[] charCodes, PDFont font, float fontSize, int fontSizeInPt) {
        this.textMatrix = textMatrix;
        this.endX = endX;
        this.endY = endY;
        this.rotation = pageRotation;
        this.maxHeight = maxHeight;
        this.pageHeight = pageHeight;
        this.pageWidth = pageWidth;
        this.widths = new float[]{individualWidth};
        this.widthOfSpace = spaceWidth;
        this.unicode = unicode;
        this.charCodes = charCodes;
        this.font = font;
        this.fontSize = fontSize;
        this.fontSizePt = fontSizeInPt;
        this.x = this.getXRot(this.rotation);
        this.y = this.rotation == 0 || this.rotation == 180 ? this.pageHeight - this.getYLowerLeftRot(this.rotation) : this.pageWidth - this.getYLowerLeftRot(this.rotation);
    }

    private static Map<Integer, String> createDiacritics() {
        HashMap<Integer, String> map = new HashMap<Integer, String>(31);
        map.put(96, "\u0300");
        map.put(715, "\u0300");
        map.put(39, "\u0301");
        map.put(697, "\u0301");
        map.put(714, "\u0301");
        map.put(94, "\u0302");
        map.put(710, "\u0302");
        map.put(126, "\u0303");
        map.put(713, "\u0304");
        map.put(176, "\u030a");
        map.put(698, "\u030b");
        map.put(711, "\u030c");
        map.put(712, "\u030d");
        map.put(34, "\u030e");
        map.put(699, "\u0312");
        map.put(700, "\u0313");
        map.put(1158, "\u0313");
        map.put(1370, "\u0313");
        map.put(701, "\u0314");
        map.put(1157, "\u0314");
        map.put(1369, "\u0314");
        map.put(724, "\u031d");
        map.put(725, "\u031e");
        map.put(726, "\u031f");
        map.put(727, "\u0320");
        map.put(690, "\u0321");
        map.put(716, "\u0329");
        map.put(695, "\u032b");
        map.put(717, "\u0331");
        map.put(95, "\u0332");
        map.put(8270, "\u0359");
        return map;
    }

    public String getUnicode() {
        return this.unicode;
    }

    public String getVisuallyOrderedUnicode() {
        String text = this.getUnicode();
        int length = text.length();
        int index = 0;
        while (index < length) {
            int codePoint = text.codePointAt(index);
            int nextIndex = index + Character.charCount(codePoint);
            byte directionality = Character.getDirectionality(codePoint);
            if (!(directionality != 2 && directionality != 1 || index == 0 && nextIndex >= length)) {
                return new StringBuilder(text).reverse().toString();
            }
            index = nextIndex;
        }
        return text;
    }

    public int[] getCharacterCodes() {
        return this.charCodes;
    }

    public Matrix getTextMatrix() {
        return this.textMatrix;
    }

    public float getDir() {
        if (this.direction < 0.0f) {
            float a = this.textMatrix.getScaleY();
            float b = this.textMatrix.getShearY();
            float c = this.textMatrix.getShearX();
            float d = this.textMatrix.getScaleX();
            this.direction = a > 0.0f && Math.abs(b) < d && Math.abs(c) < a && d > 0.0f ? 0.0f : (a < 0.0f && Math.abs(b) < Math.abs(d) && Math.abs(c) < Math.abs(a) && d < 0.0f ? 180.0f : (Math.abs(a) < Math.abs(c) && b > 0.0f && c < 0.0f && Math.abs(d) < b ? 90.0f : (Math.abs(a) < c && b < 0.0f && c > 0.0f && Math.abs(d) < Math.abs(b) ? 270.0f : 0.0f)));
        }
        return this.direction;
    }

    private float getXRot(float rotation) {
        if (rotation == 0.0f) {
            return this.textMatrix.getTranslateX();
        }
        if (rotation == 90.0f) {
            return this.textMatrix.getTranslateY();
        }
        if (rotation == 180.0f) {
            return this.pageWidth - this.textMatrix.getTranslateX();
        }
        if (rotation == 270.0f) {
            return this.pageHeight - this.textMatrix.getTranslateY();
        }
        return 0.0f;
    }

    public float getX() {
        return this.x;
    }

    public float getXDirAdj() {
        return this.getXRot(this.getDir());
    }

    private float getYLowerLeftRot(float rotation) {
        if (rotation == 0.0f) {
            return this.textMatrix.getTranslateY();
        }
        if (rotation == 90.0f) {
            return this.pageWidth - this.textMatrix.getTranslateX();
        }
        if (rotation == 180.0f) {
            return this.pageHeight - this.textMatrix.getTranslateY();
        }
        if (rotation == 270.0f) {
            return this.textMatrix.getTranslateX();
        }
        return 0.0f;
    }

    public float getY() {
        return this.y;
    }

    public float getYDirAdj() {
        float dir = this.getDir();
        if (dir == 0.0f || dir == 180.0f) {
            return this.pageHeight - this.getYLowerLeftRot(dir);
        }
        return this.pageWidth - this.getYLowerLeftRot(dir);
    }

    private float getWidthRot(float rotation) {
        if (rotation == 90.0f || rotation == 270.0f) {
            return Math.abs(this.endY - this.textMatrix.getTranslateY());
        }
        return Math.abs(this.endX - this.textMatrix.getTranslateX());
    }

    public float getWidth() {
        return this.getWidthRot(this.rotation);
    }

    public float getWidthDirAdj() {
        return this.getWidthRot(this.getDir());
    }

    public float getHeight() {
        return this.maxHeight;
    }

    public float getHeightDir() {
        return this.maxHeight;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public float getFontSizeInPt() {
        return this.fontSizePt;
    }

    public PDFont getFont() {
        return this.font;
    }

    public float getWidthOfSpace() {
        return this.widthOfSpace;
    }

    public float getXScale() {
        return this.textMatrix.getScalingFactorX();
    }

    public float getYScale() {
        return this.textMatrix.getScalingFactorY();
    }

    public float[] getIndividualWidths() {
        return this.widths;
    }

    public boolean contains(TextPosition tp2) {
        double thisXstart = this.getXDirAdj();
        double thisWidth = this.getWidthDirAdj();
        double thisXend = thisXstart + thisWidth;
        double tp2Xstart = tp2.getXDirAdj();
        double tp2Xend = tp2Xstart + (double)tp2.getWidthDirAdj();
        if (tp2Xend <= thisXstart || tp2Xstart >= thisXend) {
            return false;
        }
        double thisYstart = this.getYDirAdj();
        double tp2Ystart = tp2.getYDirAdj();
        if (tp2Ystart + (double)tp2.getHeightDir() < thisYstart || tp2Ystart > thisYstart + (double)this.getHeightDir()) {
            return false;
        }
        if (tp2Xstart > thisXstart && tp2Xend > thisXend) {
            double overlap = thisXend - tp2Xstart;
            double overlapPercent = overlap / thisWidth;
            return overlapPercent > 0.15;
        }
        if (tp2Xstart < thisXstart && tp2Xend < thisXend) {
            double overlap = tp2Xend - thisXstart;
            double overlapPercent = overlap / thisWidth;
            return overlapPercent > 0.15;
        }
        return true;
    }

    public void mergeDiacritic(TextPosition diacritic) {
        if (diacritic.getUnicode().length() > 1) {
            return;
        }
        float diacXStart = diacritic.getXDirAdj();
        float diacXEnd = diacXStart + diacritic.widths[0];
        float currCharXStart = this.getXDirAdj();
        int strLen = this.unicode.length();
        boolean wasAdded = false;
        for (int i = 0; i < strLen && !wasAdded; ++i) {
            if (i >= this.widths.length) {
                LOG.info((Object)("diacritic " + diacritic.getUnicode() + " on ligature " + this.unicode + " is not supported yet and is ignored (PDFBOX-2831)"));
                break;
            }
            float currCharXEnd = currCharXStart + this.widths[i];
            if (diacXStart < currCharXStart && diacXEnd <= currCharXEnd) {
                if (i == 0) {
                    this.insertDiacritic(i, diacritic);
                } else {
                    float distanceOverlapping1 = diacXEnd - currCharXStart;
                    float percentage1 = distanceOverlapping1 / this.widths[i];
                    float distanceOverlapping2 = currCharXStart - diacXStart;
                    float percentage2 = distanceOverlapping2 / this.widths[i - 1];
                    if (percentage1 >= percentage2) {
                        this.insertDiacritic(i, diacritic);
                    } else {
                        this.insertDiacritic(i - 1, diacritic);
                    }
                }
                wasAdded = true;
            } else if (diacXStart < currCharXStart) {
                this.insertDiacritic(i, diacritic);
                wasAdded = true;
            } else if (diacXEnd <= currCharXEnd) {
                this.insertDiacritic(i, diacritic);
                wasAdded = true;
            } else if (i == strLen - 1) {
                this.insertDiacritic(i, diacritic);
                wasAdded = true;
            }
            currCharXStart += this.widths[i];
        }
    }

    private void insertDiacritic(int i, TextPosition diacritic) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.unicode, 0, i);
        float[] widths2 = new float[this.widths.length + 1];
        System.arraycopy(this.widths, 0, widths2, 0, i);
        sb.append(this.unicode.charAt(i));
        widths2[i] = this.widths[i];
        sb.append(this.combineDiacritic(diacritic.getUnicode()));
        widths2[i + 1] = 0.0f;
        sb.append(this.unicode.substring(i + 1));
        System.arraycopy(this.widths, i + 1, widths2, i + 2, this.widths.length - i - 1);
        this.unicode = sb.toString();
        this.widths = widths2;
    }

    private String combineDiacritic(String str) {
        int codePoint = str.codePointAt(0);
        if (DIACRITICS.containsKey(codePoint)) {
            return DIACRITICS.get(codePoint);
        }
        return Normalizer.normalize(str, Normalizer.Form.NFKC).trim();
    }

    public boolean isDiacritic() {
        String text = this.getUnicode();
        if (text.length() != 1) {
            return false;
        }
        if ("\u30fc".equals(text)) {
            return false;
        }
        int type = Character.getType(text.charAt(0));
        return type == 6 || type == 27 || type == 4;
    }

    public String toString() {
        return this.getUnicode();
    }

    public float getEndX() {
        return this.endX;
    }

    public float getEndY() {
        return this.endY;
    }

    public int getRotation() {
        return this.rotation;
    }

    public float getPageHeight() {
        return this.pageHeight;
    }

    public float getPageWidth() {
        return this.pageWidth;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextPosition)) {
            return false;
        }
        TextPosition that = (TextPosition)o;
        if (Float.compare(that.endX, this.endX) != 0) {
            return false;
        }
        if (Float.compare(that.endY, this.endY) != 0) {
            return false;
        }
        if (Float.compare(that.maxHeight, this.maxHeight) != 0) {
            return false;
        }
        if (this.rotation != that.rotation) {
            return false;
        }
        if (Float.compare(that.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(that.y, this.y) != 0) {
            return false;
        }
        if (Float.compare(that.pageHeight, this.pageHeight) != 0) {
            return false;
        }
        if (Float.compare(that.pageWidth, this.pageWidth) != 0) {
            return false;
        }
        if (Float.compare(that.widthOfSpace, this.widthOfSpace) != 0) {
            return false;
        }
        if (Float.compare(that.fontSize, this.fontSize) != 0) {
            return false;
        }
        if (this.fontSizePt != that.fontSizePt) {
            return false;
        }
        if (this.textMatrix != null ? !this.textMatrix.equals(that.textMatrix) : that.textMatrix != null) {
            return false;
        }
        if (!Arrays.equals(this.charCodes, that.charCodes)) {
            return false;
        }
        return this.font != null ? this.font.equals(that.font) : that.font == null;
    }

    public int hashCode() {
        int result = this.textMatrix != null ? this.textMatrix.hashCode() : 0;
        result = 31 * result + Float.floatToIntBits(this.endX);
        result = 31 * result + Float.floatToIntBits(this.endY);
        result = 31 * result + Float.floatToIntBits(this.maxHeight);
        result = 31 * result + this.rotation;
        result = 31 * result + Float.floatToIntBits(this.x);
        result = 31 * result + Float.floatToIntBits(this.y);
        result = 31 * result + Float.floatToIntBits(this.pageHeight);
        result = 31 * result + Float.floatToIntBits(this.pageWidth);
        result = 31 * result + Float.floatToIntBits(this.widthOfSpace);
        result = 31 * result + Arrays.hashCode(this.charCodes);
        result = 31 * result + (this.font != null ? this.font.hashCode() : 0);
        result = 31 * result + Float.floatToIntBits(this.fontSize);
        result = 31 * result + this.fontSizePt;
        return result;
    }
}

