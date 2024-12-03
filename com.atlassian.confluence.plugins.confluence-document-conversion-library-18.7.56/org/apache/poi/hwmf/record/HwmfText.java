/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfDraw;
import org.apache.poi.hwmf.record.HwmfFont;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.hwmf.usermodel.HwmfCharsetAware;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.LocaleUtil;

public class HwmfText {
    private static final Logger LOG = LogManager.getLogger(HwmfText.class);
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public static class WmfCreateFontIndirect
    implements HwmfRecord,
    HwmfObjectTableEntry {
        protected final HwmfFont font;

        public WmfCreateFontIndirect() {
            this(new HwmfFont());
        }

        protected WmfCreateFontIndirect(HwmfFont font) {
            this.font = font;
        }

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.createFontIndirect;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return this.font.init(leis, recordSize);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            ctx.getProperties().setFont(this.font);
        }

        public HwmfFont getFont() {
            return this.font;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("font", this::getFont);
        }
    }

    public static class WmfSetTextAlign
    implements HwmfRecord {
        private static final BitField TA_UPDATECP = BitFieldFactory.getInstance(1);
        private static final BitField TA_RTLREADING = BitFieldFactory.getInstance(256);
        private static final BitField ALIGN_MASK = BitFieldFactory.getInstance(6);
        private static final int ALIGN_LEFT = 0;
        private static final int ALIGN_RIGHT = 1;
        private static final int ALIGN_CENTER = 3;
        private static final BitField VALIGN_MASK = BitFieldFactory.getInstance(24);
        private static final int VALIGN_TOP = 0;
        private static final int VALIGN_BOTTOM = 1;
        private static final int VALIGN_BASELINE = 3;
        protected int textAlignmentMode;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setTextAlign;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.textAlignmentMode = leis.readUShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties props = ctx.getProperties();
            props.setTextAlignLatin(this.getAlignLatin());
            props.setTextVAlignLatin(this.getVAlignLatin());
            props.setTextAlignAsian(this.getAlignAsian());
            props.setTextVAlignAsian(this.getVAlignAsian());
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("align", this::getAlignLatin, "valign", this::getVAlignLatin, "alignAsian", this::getAlignAsian, "valignAsian", this::getVAlignAsian);
        }

        private HwmfTextAlignment getAlignLatin() {
            switch (ALIGN_MASK.getValue(this.textAlignmentMode)) {
                default: {
                    return HwmfTextAlignment.LEFT;
                }
                case 3: {
                    return HwmfTextAlignment.CENTER;
                }
                case 1: 
            }
            return HwmfTextAlignment.RIGHT;
        }

        private HwmfTextVerticalAlignment getVAlignLatin() {
            switch (VALIGN_MASK.getValue(this.textAlignmentMode)) {
                default: {
                    return HwmfTextVerticalAlignment.TOP;
                }
                case 3: {
                    return HwmfTextVerticalAlignment.BASELINE;
                }
                case 1: 
            }
            return HwmfTextVerticalAlignment.BOTTOM;
        }

        private HwmfTextAlignment getAlignAsian() {
            switch (this.getVAlignLatin()) {
                default: {
                    return HwmfTextAlignment.RIGHT;
                }
                case BASELINE: {
                    return HwmfTextAlignment.CENTER;
                }
                case BOTTOM: 
            }
            return HwmfTextAlignment.LEFT;
        }

        private HwmfTextVerticalAlignment getVAlignAsian() {
            switch (this.getAlignLatin()) {
                default: {
                    return HwmfTextVerticalAlignment.TOP;
                }
                case CENTER: {
                    return HwmfTextVerticalAlignment.BASELINE;
                }
                case RIGHT: 
            }
            return HwmfTextVerticalAlignment.BOTTOM;
        }
    }

    public static enum HwmfTextVerticalAlignment {
        TOP,
        BOTTOM,
        BASELINE;

    }

    public static enum HwmfTextAlignment {
        LEFT,
        RIGHT,
        CENTER;

    }

    public static class WmfExtTextOut
    implements HwmfRecord,
    HwmfCharsetAware {
        protected final Point2D reference = new Point2D.Double();
        protected int stringLength;
        protected final WmfExtTextOutOptions options;
        protected final Rectangle2D bounds = new Rectangle2D.Double();
        protected byte[] rawTextBytes;
        protected final List<Integer> dx = new ArrayList<Integer>();
        protected Supplier<Charset> charsetProvider = () -> LocaleUtil.CHARSET_1252;

        public WmfExtTextOut() {
            this(new WmfExtTextOutOptions());
        }

        protected WmfExtTextOut(WmfExtTextOutOptions options) {
            this.options = options;
        }

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.extTextOut;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            int remainingRecordSize = (int)(recordSize - 6L);
            int size = HwmfDraw.readPointS(leis, this.reference);
            this.stringLength = leis.readShort();
            size += 2;
            if ((this.options.isOpaque() || this.options.isClipped()) && (size += this.options.init(leis)) + 8 <= remainingRecordSize) {
                size += HwmfDraw.readRectS(leis, this.bounds);
            }
            this.rawTextBytes = IOUtils.safelyAllocate((long)this.stringLength + (long)(this.stringLength & 1), MAX_RECORD_LENGTH);
            leis.readFully(this.rawTextBytes);
            if ((size += this.rawTextBytes.length) >= remainingRecordSize) {
                LOG.atInfo().log("META_EXTTEXTOUT doesn't contain character tracking info");
                return size;
            }
            int dxLen = Math.min(this.stringLength, (remainingRecordSize - size) / 2);
            if (dxLen < this.stringLength) {
                LOG.atWarn().log("META_EXTTEXTOUT tracking info doesn't cover all characters");
            }
            for (int i = 0; i < dxLen; ++i) {
                this.dx.add(Integer.valueOf(leis.readShort()));
                size += 2;
            }
            return size;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.setCharsetProvider(this.charsetProvider);
            ctx.drawString(this.rawTextBytes, this.stringLength, this.reference, null, this.bounds, this.options, this.dx, false);
        }

        public String getText(Charset charset) throws IOException {
            if (this.rawTextBytes == null) {
                return "";
            }
            String ret = new String(this.rawTextBytes, charset);
            return ret.substring(0, Math.min(ret.length(), this.stringLength));
        }

        public Point2D getReference() {
            return this.reference;
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        public WmfExtTextOutOptions getOptions() {
            return this.options;
        }

        protected boolean isUnicode() {
            return false;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        private String getGenericText() {
            try {
                return this.getText(this.isUnicode() ? StandardCharsets.UTF_16LE : this.charsetProvider.get());
            }
            catch (IOException e) {
                return "";
            }
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("reference", this::getReference, "bounds", this::getBounds, "options", this::getOptions, "text", this::getGenericText, "dx", () -> this.dx);
        }

        @Override
        public void setCharsetProvider(Supplier<Charset> provider) {
            this.charsetProvider = provider;
        }
    }

    public static class WmfExtTextOutOptions
    implements GenericRecord {
        private static final BitField ETO_OPAQUE = BitFieldFactory.getInstance(2);
        private static final BitField ETO_CLIPPED = BitFieldFactory.getInstance(4);
        private static final BitField ETO_GLYPH_INDEX = BitFieldFactory.getInstance(16);
        private static final BitField ETO_RTLREADING = BitFieldFactory.getInstance(128);
        private static final BitField ETO_NO_RECT = BitFieldFactory.getInstance(256);
        private static final BitField ETO_SMALL_CHARS = BitFieldFactory.getInstance(512);
        private static final BitField ETO_NUMERICSLOCAL = BitFieldFactory.getInstance(1024);
        private static final BitField ETO_NUMERICSLATIN = BitFieldFactory.getInstance(2048);
        private static final BitField ETO_IGNORELANGUAGE = BitFieldFactory.getInstance(4096);
        private static final BitField ETO_PDY = BitFieldFactory.getInstance(8192);
        private static final BitField ETO_REVERSE_INDEX_MAP = BitFieldFactory.getInstance(65536);
        private static final int[] FLAGS_MASKS = new int[]{2, 4, 16, 128, 256, 512, 1024, 2048, 4096, 8192, 65536};
        private static final String[] FLAGS_NAMES = new String[]{"OPAQUE", "CLIPPED", "GLYPH_INDEX", "RTLREADING", "NO_RECT", "SMALL_CHARS", "NUMERICSLOCAL", "NUMERICSLATIN", "IGNORELANGUAGE", "PDY", "REVERSE_INDEX_MAP"};
        protected int flags;

        public int init(LittleEndianInputStream leis) {
            this.flags = leis.readUShort();
            return 2;
        }

        public boolean isOpaque() {
            return ETO_OPAQUE.isSet(this.flags);
        }

        public boolean isClipped() {
            return ETO_CLIPPED.isSet(this.flags);
        }

        public boolean isYDisplaced() {
            return ETO_PDY.isSet(this.flags);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", GenericRecordUtil.getBitsAsString(() -> this.flags, FLAGS_MASKS, FLAGS_NAMES));
        }
    }

    public static class WmfTextOut
    implements HwmfRecord,
    HwmfCharsetAware {
        private int stringLength;
        private byte[] rawTextBytes;
        protected Point2D reference = new Point2D.Double();
        protected Supplier<Charset> charsetProvider = () -> LocaleUtil.CHARSET_1252;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.textOut;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.stringLength = leis.readShort();
            this.rawTextBytes = IOUtils.safelyAllocate((long)this.stringLength + (long)(this.stringLength & 1), MAX_RECORD_LENGTH);
            leis.readFully(this.rawTextBytes);
            short yStart = leis.readShort();
            short xStart = leis.readShort();
            this.reference.setLocation(xStart, yStart);
            return 6 + this.rawTextBytes.length;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.setCharsetProvider(this.charsetProvider);
            ctx.drawString(this.getTextBytes(), this.stringLength, this.reference);
        }

        public String getText(Charset charset) {
            return new String(this.getTextBytes(), charset);
        }

        private byte[] getTextBytes() {
            return IOUtils.safelyClone(this.rawTextBytes, 0, this.stringLength, MAX_RECORD_LENGTH);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("text", () -> this.getText(this.charsetProvider.get()), "reference", () -> this.reference);
        }

        @Override
        public void setCharsetProvider(Supplier<Charset> provider) {
            this.charsetProvider = provider;
        }
    }

    public static class WmfSetTextJustification
    implements HwmfRecord {
        private int breakCount;
        private int breakExtra;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setTextJustification;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.breakCount = leis.readUShort();
            this.breakExtra = leis.readUShort();
            return 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("breakCount", () -> this.breakCount, "breakExtra", () -> this.breakExtra);
        }
    }

    public static class WmfSetTextColor
    implements HwmfRecord {
        protected final HwmfColorRef colorRef = new HwmfColorRef();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setTextColor;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return this.colorRef.init(leis);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.getProperties().setTextColor(this.colorRef);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfColorRef getColorRef() {
            return this.colorRef;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("colorRef", this::getColorRef);
        }
    }

    public static class WmfSetTextCharExtra
    implements HwmfRecord {
        private int charExtra;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setTextCharExtra;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.charExtra = leis.readUShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("charExtra", () -> this.charExtra);
        }
    }
}

