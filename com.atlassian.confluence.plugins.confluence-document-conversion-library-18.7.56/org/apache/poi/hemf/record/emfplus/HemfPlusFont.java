/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfFont;
import org.apache.poi.hemf.record.emfplus.HemfPlusDraw;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.StringUtil;

public class HemfPlusFont {

    public static class EmfPlusFont
    implements HemfPlusObject.EmfPlusObjectData {
        private static final BitField BOLD = BitFieldFactory.getInstance(1);
        private static final BitField ITALIC = BitFieldFactory.getInstance(2);
        private static final BitField UNDERLINE = BitFieldFactory.getInstance(4);
        private static final BitField STRIKEOUT = BitFieldFactory.getInstance(8);
        private final HemfPlusHeader.EmfPlusGraphicsVersion graphicsVersion = new HemfPlusHeader.EmfPlusGraphicsVersion();
        private double emSize;
        private HemfPlusDraw.EmfPlusUnitType sizeUnit;
        private int styleFlags;
        private String family;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, HemfPlusObject.EmfPlusObjectType objectType, int flags) throws IOException {
            long size = this.graphicsVersion.init(leis);
            this.emSize = leis.readFloat();
            this.sizeUnit = HemfPlusDraw.EmfPlusUnitType.valueOf(leis.readInt());
            this.styleFlags = leis.readInt();
            leis.skipFully(4);
            int len = leis.readInt();
            size += 20L;
            this.family = StringUtil.readUnicodeLE(leis, len);
            return size += (long)len * 2L;
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            HemfFont font = new HemfFont();
            font.initDefaults();
            font.setTypeface(this.family);
            font.setHeight(this.emSize);
            font.setStrikeOut(STRIKEOUT.isSet(this.styleFlags));
            font.setUnderline(UNDERLINE.isSet(this.styleFlags));
            font.setWeight(BOLD.isSet(this.styleFlags) ? 700 : 400);
            font.setItalic(ITALIC.isSet(this.styleFlags));
            prop.setFont(font);
        }

        @Override
        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("graphicsVersion", () -> this.graphicsVersion, "emSize", () -> this.emSize, "sizeUnit", () -> this.sizeUnit, "styleFlags", () -> this.styleFlags, "family", () -> this.family);
        }

        public HemfPlusObject.EmfPlusObjectType getGenericRecordType() {
            return HemfPlusObject.EmfPlusObjectType.FONT;
        }
    }
}

