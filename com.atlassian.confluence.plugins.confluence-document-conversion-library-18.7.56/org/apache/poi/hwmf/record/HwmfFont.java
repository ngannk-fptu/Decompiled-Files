/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfFont
implements FontInfo,
GenericRecord {
    protected double height;
    protected int width;
    protected int escapement;
    protected int orientation;
    protected int weight;
    protected boolean italic;
    protected boolean underline;
    protected boolean strikeOut;
    protected FontCharset charSet;
    protected WmfOutPrecision outPrecision;
    protected final WmfClipPrecision clipPrecision = new WmfClipPrecision();
    protected WmfFontQuality quality;
    protected int pitchAndFamily;
    protected FontFamily family;
    protected FontPitch pitch;
    protected String facename;

    public int init(LittleEndianInputStream leis, long recordSize) throws IOException {
        this.height = leis.readShort();
        this.width = leis.readShort();
        this.escapement = leis.readShort();
        this.orientation = leis.readShort();
        this.weight = leis.readShort();
        this.italic = leis.readByte() != 0;
        this.underline = leis.readByte() != 0;
        this.strikeOut = leis.readByte() != 0;
        this.charSet = FontCharset.valueOf(leis.readUByte());
        this.outPrecision = WmfOutPrecision.valueOf(leis.readUByte());
        this.clipPrecision.init(leis);
        this.quality = WmfFontQuality.valueOf(leis.readUByte());
        this.pitchAndFamily = leis.readUByte();
        StringBuilder sb = new StringBuilder();
        Charset actualCharset = this.charSet == null ? null : this.charSet.getCharset();
        int readBytes = this.readString(leis, sb, 32, actualCharset);
        if (readBytes == -1) {
            throw new IOException("Font facename can't be determined.");
        }
        this.facename = sb.toString();
        return 18 + readBytes;
    }

    public void initDefaults() {
        this.height = -12.0;
        this.width = 0;
        this.escapement = 0;
        this.weight = 400;
        this.italic = false;
        this.underline = false;
        this.strikeOut = false;
        this.charSet = FontCharset.ANSI;
        this.outPrecision = WmfOutPrecision.OUT_DEFAULT_PRECIS;
        this.quality = WmfFontQuality.ANTIALIASED_QUALITY;
        this.pitchAndFamily = FontFamily.FF_DONTCARE.getFlag() | FontPitch.DEFAULT.getNativeId() << 6;
        this.facename = "SansSerif";
    }

    public double getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getEscapement() {
        return this.escapement;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean isItalic() {
        return this.italic;
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public boolean isStrikeOut() {
        return this.strikeOut;
    }

    public WmfOutPrecision getOutPrecision() {
        return this.outPrecision;
    }

    public WmfClipPrecision getClipPrecision() {
        return this.clipPrecision;
    }

    public WmfFontQuality getQuality() {
        return this.quality;
    }

    public int getPitchAndFamily() {
        return this.pitchAndFamily;
    }

    @Override
    public FontFamily getFamily() {
        return FontFamily.valueOf(this.pitchAndFamily & 0xF);
    }

    @Override
    public FontPitch getPitch() {
        return FontPitch.valueOf(this.pitchAndFamily >>> 6 & 3);
    }

    @Override
    public String getTypeface() {
        return this.facename;
    }

    @Override
    public FontCharset getCharset() {
        return this.charSet;
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    protected int readString(LittleEndianInputStream leis, StringBuilder sb, int limit) {
        return this.readString(leis, sb, limit, StandardCharsets.ISO_8859_1);
    }

    protected int readString(LittleEndianInputStream leis, StringBuilder sb, int limit, Charset charset) {
        byte b;
        byte[] buf = new byte[limit];
        int readBytes = 0;
        do {
            if (readBytes == limit) {
                return -1;
            }
            int n = readBytes;
            readBytes = (byte)(readBytes + 1);
            buf[n] = b = leis.readByte();
        } while (b != 0 && b != -1 && readBytes <= limit);
        sb.append(new String(buf, 0, readBytes - 1, charset == null ? StandardCharsets.ISO_8859_1 : charset));
        return readBytes;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("height", this::getHeight);
        m.put("width", this::getWidth);
        m.put("escapment", this::getEscapement);
        m.put("weight", this::getWeight);
        m.put("italic", this::isItalic);
        m.put("underline", this::isUnderline);
        m.put("strikeOut", this::isStrikeOut);
        m.put("charset", this::getCharset);
        m.put("outPrecision", this::getOutPrecision);
        m.put("clipPrecision", this::getClipPrecision);
        m.put("quality", this::getQuality);
        m.put("pitch", this::getPitch);
        m.put("family", this::getFamily);
        m.put("typeface", this::getTypeface);
        return Collections.unmodifiableMap(m);
    }

    public static enum WmfFontQuality {
        DEFAULT_QUALITY(0),
        DRAFT_QUALITY(1),
        PROOF_QUALITY(2),
        NONANTIALIASED_QUALITY(3),
        ANTIALIASED_QUALITY(4),
        CLEARTYPE_QUALITY(5);

        int flag;

        private WmfFontQuality(int flag) {
            this.flag = flag;
        }

        public static WmfFontQuality valueOf(int flag) {
            for (WmfFontQuality fq : WmfFontQuality.values()) {
                if (fq.flag != flag) continue;
                return fq;
            }
            return null;
        }
    }

    public static class WmfClipPrecision
    implements GenericRecord {
        private static final BitField DEFAULT_PRECIS = BitFieldFactory.getInstance(3);
        private static final BitField CHARACTER_PRECIS = BitFieldFactory.getInstance(1);
        private static final BitField STROKE_PRECIS = BitFieldFactory.getInstance(2);
        private static final BitField LH_ANGLES = BitFieldFactory.getInstance(16);
        private static final BitField TT_ALWAYS = BitFieldFactory.getInstance(32);
        private static final BitField DFA_DISABLE = BitFieldFactory.getInstance(64);
        private static final BitField EMBEDDED = BitFieldFactory.getInstance(128);
        private static final int[] FLAG_MASKS = new int[]{1, 2, 16, 32, 64, 128};
        private static final String[] FLAG_NAMES = new String[]{"CHARACTER_PRECIS", "STROKE_PRECIS", "LH_ANGLES", "TT_ALWAYS", "DFA_DISABLE", "EMBEDDED"};
        private int flag;

        public int init(LittleEndianInputStream leis) {
            this.flag = leis.readUByte();
            return 1;
        }

        public boolean isDefaultPrecision() {
            return !DEFAULT_PRECIS.isSet(this.flag);
        }

        public boolean isCharacterPrecision() {
            return CHARACTER_PRECIS.isSet(this.flag);
        }

        public boolean isStrokePrecision() {
            return STROKE_PRECIS.isSet(this.flag);
        }

        public boolean isLeftHandAngles() {
            return LH_ANGLES.isSet(this.flag);
        }

        public boolean isTrueTypeAlways() {
            return TT_ALWAYS.isSet(this.flag);
        }

        public boolean isFontAssociated() {
            return !DFA_DISABLE.isSet(this.flag);
        }

        public boolean useEmbeddedFont() {
            return EMBEDDED.isSet(this.flag);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("isDefaultPrecision", this::isDefaultPrecision, "flag", GenericRecordUtil.getBitsAsString(() -> this.flag, FLAG_MASKS, FLAG_NAMES));
        }
    }

    public static enum WmfOutPrecision {
        OUT_DEFAULT_PRECIS(0),
        OUT_STRING_PRECIS(1),
        OUT_STROKE_PRECIS(3),
        OUT_TT_PRECIS(4),
        OUT_DEVICE_PRECIS(5),
        OUT_RASTER_PRECIS(6),
        OUT_TT_ONLY_PRECIS(7),
        OUT_OUTLINE_PRECIS(8),
        OUT_SCREEN_OUTLINE_PRECIS(9),
        OUT_PS_ONLY_PRECIS(10);

        int flag;

        private WmfOutPrecision(int flag) {
            this.flag = flag;
        }

        public static WmfOutPrecision valueOf(int flag) {
            for (WmfOutPrecision op : WmfOutPrecision.values()) {
                if (op.flag != flag) continue;
                return op;
            }
            return null;
        }
    }
}

