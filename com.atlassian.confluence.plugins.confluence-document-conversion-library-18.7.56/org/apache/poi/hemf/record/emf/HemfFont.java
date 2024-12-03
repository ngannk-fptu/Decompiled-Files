/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.hwmf.record.HwmfFont;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfFont
extends HwmfFont {
    private static final int LOGFONT_SIZE = 92;
    private static final int LOGFONTPANOSE_SIZE = 320;
    protected String fullname;
    protected String style;
    protected String script;
    protected LogFontDetails details;

    @Override
    public int init(LittleEndianInputStream leis, long recordSize) throws IOException {
        this.height = leis.readInt();
        this.width = leis.readInt();
        this.escapement = leis.readInt();
        this.orientation = leis.readInt();
        this.weight = leis.readInt();
        this.italic = leis.readUByte() != 0;
        this.underline = leis.readUByte() != 0;
        this.strikeOut = leis.readUByte() != 0;
        this.charSet = FontCharset.valueOf(leis.readUByte());
        this.outPrecision = HwmfFont.WmfOutPrecision.valueOf(leis.readUByte());
        this.clipPrecision.init(leis);
        this.quality = HwmfFont.WmfFontQuality.valueOf(leis.readUByte());
        this.pitchAndFamily = leis.readUByte();
        int size = 28;
        StringBuilder sb = new StringBuilder();
        int readBytes = this.readString(leis, sb, 32);
        if (readBytes == -1) {
            throw new IOException("Font facename can't be determined.");
        }
        this.facename = sb.toString();
        size += readBytes;
        if (recordSize <= 92L) {
            return size;
        }
        readBytes = this.readString(leis, sb, 64);
        if (readBytes == -1) {
            throw new IOException("Font fullname can't be determined.");
        }
        this.fullname = sb.toString();
        size += readBytes;
        readBytes = this.readString(leis, sb, 32);
        if (readBytes == -1) {
            throw new IOException("Font style can't be determined.");
        }
        this.style = sb.toString();
        size += readBytes;
        if (recordSize == 320L) {
            LogFontPanose logPan = new LogFontPanose();
            this.details = logPan;
            int version = leis.readInt();
            logPan.styleSize = (int)leis.readUInt();
            int match = leis.readInt();
            int reserved = leis.readInt();
            logPan.vendorId = leis.readInt();
            logPan.culture = leis.readInt();
            logPan.familyType = LogFontPanose.FamilyType.values()[leis.readUByte()];
            logPan.serifStyle = LogFontPanose.SerifType.values()[leis.readUByte()];
            logPan.weight = LogFontPanose.FontWeight.values()[leis.readUByte()];
            logPan.proportion = LogFontPanose.Proportion.values()[leis.readUByte()];
            logPan.contrast = LogFontPanose.Contrast.values()[leis.readUByte()];
            logPan.strokeVariation = LogFontPanose.StrokeVariation.values()[leis.readUByte()];
            logPan.armStyle = LogFontPanose.ArmStyle.values()[leis.readUByte()];
            logPan.letterform = LogFontPanose.Letterform.values()[leis.readUByte()];
            logPan.midLine = LogFontPanose.MidLine.values()[leis.readUByte()];
            logPan.xHeight = LogFontPanose.XHeight.values()[leis.readUByte()];
            long skipped = IOUtils.skipFully(leis, 2L);
            if (skipped != 2L) {
                throw new IOException("Didn't skip 2: " + skipped);
            }
            size += 36;
        } else {
            LogFontExDv logEx = new LogFontExDv();
            this.details = logEx;
            readBytes = this.readString(leis, sb, 32);
            if (readBytes == -1) {
                throw new IOException("Font script can't be determined.");
            }
            this.script = sb.toString();
            size += readBytes;
            int signature = leis.readInt();
            int numAxes = leis.readInt();
            size += 8;
            if (0 <= numAxes && numAxes <= 16) {
                logEx.designVector = new int[numAxes];
                for (int i = 0; i < numAxes; ++i) {
                    logEx.designVector[i] = leis.readInt();
                }
                size += numAxes * 4;
            }
        }
        return size;
    }

    @Override
    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "fullname", () -> this.fullname, "style", () -> this.style, "script", () -> this.script, "details", () -> this.details);
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public void setStrikeOut(boolean strikeOut) {
        this.strikeOut = strikeOut;
    }

    @Override
    public void setTypeface(String typeface) {
        this.facename = typeface;
    }

    @Override
    protected int readString(LittleEndianInputStream leis, StringBuilder sb, int limit) {
        byte b2;
        byte b1;
        sb.setLength(0);
        byte[] buf = new byte[limit * 2];
        leis.readFully(buf);
        int readBytes = 0;
        do {
            if (readBytes == limit * 2) {
                return -1;
            }
            b1 = buf[readBytes++];
            b2 = buf[readBytes++];
        } while ((b1 != 0 || b2 != 0) && b1 != -1 && b2 != -1 && readBytes <= limit * 2);
        sb.append(new String(buf, 0, readBytes - 2, StandardCharsets.UTF_16LE));
        return limit * 2;
    }

    protected static class LogFontPanose
    implements LogFontDetails,
    GenericRecord {
        protected int styleSize;
        protected int vendorId;
        protected int culture;
        protected FamilyType familyType;
        protected SerifType serifStyle;
        protected FontWeight weight;
        protected Proportion proportion;
        protected Contrast contrast;
        protected StrokeVariation strokeVariation;
        protected ArmStyle armStyle;
        protected Letterform letterform;
        protected MidLine midLine;
        protected XHeight xHeight;

        protected LogFontPanose() {
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("styleSize", () -> this.styleSize);
            m.put("vendorId", () -> this.vendorId);
            m.put("culture", () -> this.culture);
            m.put("familyType", () -> this.familyType);
            m.put("serifStyle", () -> this.serifStyle);
            m.put("weight", () -> this.weight);
            m.put("proportion", () -> this.proportion);
            m.put("contrast", () -> this.contrast);
            m.put("strokeVariation", () -> this.strokeVariation);
            m.put("armStyle", () -> this.armStyle);
            m.put("letterform", () -> this.letterform);
            m.put("midLine", () -> this.midLine);
            m.put("xHeight", () -> this.xHeight);
            return Collections.unmodifiableMap(m);
        }

        static enum XHeight {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_XHEIGHT_CONSTANT_SMALL,
            PAN_XHEIGHT_CONSTANT_STD,
            PAN_XHEIGHT_CONSTANT_LARGE,
            PAN_XHEIGHT_DUCKING_SMALL,
            PAN_XHEIGHT_DUCKING_STD,
            PAN_XHEIGHT_DUCKING_LARGE;

        }

        static enum MidLine {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_MIDLINE_STANDARD_TRIMMED,
            PAN_MIDLINE_STANDARD_POINTED,
            PAN_MIDLINE_STANDARD_SERIFED,
            PAN_MIDLINE_HIGH_TRIMMED,
            PAN_MIDLINE_HIGH_POINTED,
            PAN_MIDLINE_HIGH_SERIFED,
            PAN_MIDLINE_CONSTANT_TRIMMED,
            PAN_MIDLINE_CONSTANT_POINTED,
            PAN_MIDLINE_CONSTANT_SERIFED,
            PAN_MIDLINE_LOW_TRIMMED,
            PAN_MIDLINE_LOW_POINTED,
            PAN_MIDLINE_LOW_SERIFED;

        }

        static enum Letterform {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_LETT_NORMAL_CONTACT,
            PAN_LETT_NORMAL_WEIGHTED,
            PAN_LETT_NORMAL_BOXED,
            PAN_LETT_NORMAL_FLATTENED,
            PAN_LETT_NORMAL_ROUNDED,
            PAN_LETT_NORMAL_OFF_CENTER,
            PAN_LETT_NORMAL_SQUARE,
            PAN_LETT_OBLIQUE_CONTACT,
            PAN_LETT_OBLIQUE_WEIGHTED,
            PAN_LETT_OBLIQUE_BOXED,
            PAN_LETT_OBLIQUE_FLATTENED,
            PAN_LETT_OBLIQUE_ROUNDED,
            PAN_LETT_OBLIQUE_OFF_CENTER,
            PAN_LETT_OBLIQUE_SQUARE;

        }

        static enum ArmStyle {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_STRAIGHT_ARMS_HORZ,
            PAN_STRAIGHT_ARMS_WEDGE,
            PAN_STRAIGHT_ARMS_VERT,
            PAN_STRAIGHT_ARMS_SINGLE_SERIF,
            PAN_STRAIGHT_ARMS_DOUBLE_SERIF,
            PAN_BENT_ARMS_HORZ,
            PAN_BENT_ARMS_WEDGE,
            PAN_BENT_ARMS_VERT,
            PAN_BENT_ARMS_SINGLE_SERIF,
            PAN_BENT_ARMS_DOUBLE_SERIF;

        }

        static enum StrokeVariation {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_STROKE_GRADUAL_DIAG,
            PAN_STROKE_GRADUAL_TRAN,
            PAN_STROKE_GRADUAL_VERT,
            PAN_STROKE_GRADUAL_HORZ,
            PAN_STROKE_RAPID_VERT,
            PAN_STROKE_RAPID_HORZ,
            PAN_STROKE_INSTANT_VERT;

        }

        static enum Contrast {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_CONTRAST_NONE,
            PAN_CONTRAST_VERY_LOW,
            PAN_CONTRAST_LOW,
            PAN_CONTRAST_MEDIUM_LOW,
            PAN_CONTRAST_MEDIUM,
            PAN_CONTRAST_MEDIUM_HIGH,
            PAN_CONTRAST_HIGH,
            PAN_CONTRAST_VERY_HIGH;

        }

        static enum Proportion {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_PROP_OLD_STYLE,
            PAN_PROP_MODERN,
            PAN_PROP_EVEN_WIDTH,
            PAN_PROP_EXPANDED,
            PAN_PROP_CONDENSED,
            PAN_PROP_VERY_EXPANDED,
            PAN_PROP_VERY_CONDENSED,
            PAN_PROP_MONOSPACED;

        }

        static enum FontWeight {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_WEIGHT_VERY_LIGHT,
            PAN_WEIGHT_LIGHT,
            PAN_WEIGHT_THIN,
            PAN_WEIGHT_BOOK,
            PAN_WEIGHT_MEDIUM,
            PAN_WEIGHT_DEMI,
            PAN_WEIGHT_BOLD,
            PAN_WEIGHT_HEAVY,
            PAN_WEIGHT_BLACK,
            PAN_WEIGHT_NORD;

        }

        static enum SerifType {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_SERIF_COVE,
            PAN_SERIF_OBTUSE_COVE,
            PAN_SERIF_SQUARE_COVE,
            PAN_SERIF_OBTUSE_SQUARE_COVE,
            PAN_SERIF_SQUARE,
            PAN_SERIF_THIN,
            PAN_SERIF_BONE,
            PAN_SERIF_EXAGGERATED,
            PAN_SERIF_TRIANGLE,
            PAN_SERIF_NORMAL_SANS,
            PAN_SERIF_OBTUSE_SANS,
            PAN_SERIF_PERP_SANS,
            PAN_SERIF_FLARED,
            PAN_SERIF_ROUNDED;

        }

        static enum FamilyType {
            PAN_ANY,
            PAN_NO_FIT,
            PAN_FAMILY_TEXT_DISPLAY,
            PAN_FAMILY_SCRIPT,
            PAN_FAMILY_DECORATIVE,
            PAN_FAMILY_PICTORIAL;

        }
    }

    protected static class LogFontExDv
    implements LogFontDetails {
        protected int[] designVector;

        protected LogFontExDv() {
        }

        public String toString() {
            return "{ designVectorLen: " + (this.designVector == null ? 0 : this.designVector.length) + " }";
        }
    }

    protected static interface LogFontDetails {
    }
}

