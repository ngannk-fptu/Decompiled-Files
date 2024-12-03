/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hslf.model.textproperties.HSLFTabStop;
import org.apache.poi.hslf.model.textproperties.HSLFTabStopPropCollection;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianOutputStream;

public final class TextRulerAtom
extends RecordAtom {
    private static final BitField DEFAULT_TAB_SIZE = BitFieldFactory.getInstance(1);
    private static final BitField C_LEVELS = BitFieldFactory.getInstance(2);
    private static final BitField TAB_STOPS = BitFieldFactory.getInstance(4);
    private static final BitField[] LEFT_MARGIN_LVL_MASK = new BitField[]{BitFieldFactory.getInstance(8), BitFieldFactory.getInstance(16), BitFieldFactory.getInstance(32), BitFieldFactory.getInstance(64), BitFieldFactory.getInstance(128)};
    private static final BitField[] INDENT_LVL_MASK = new BitField[]{BitFieldFactory.getInstance(256), BitFieldFactory.getInstance(512), BitFieldFactory.getInstance(1024), BitFieldFactory.getInstance(2048), BitFieldFactory.getInstance(4096)};
    private final byte[] _header = new byte[8];
    private Integer defaultTabSize;
    private Integer numLevels;
    private final List<HSLFTabStop> tabStops = new ArrayList<HSLFTabStop>();
    private final Integer[] leftMargin = new Integer[5];
    private final Integer[] indent = new Integer[5];

    public TextRulerAtom() {
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
    }

    TextRulerAtom(byte[] source, int start, int len) {
        LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(source, start, Math.min(len, TextRulerAtom.getMaxRecordLength()));
        try {
            IOUtils.readFully(leis, this._header);
            this.read(leis);
        }
        catch (IOException e) {
            LOG.atError().withThrowable(e).log("Failed to parse TextRulerAtom");
        }
    }

    @Override
    public long getRecordType() {
        return RecordTypes.TextRulerAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream(200);
        LittleEndianOutputStream lbos = new LittleEndianOutputStream((OutputStream)bos);
        int mask = 0;
        mask |= TextRulerAtom.writeIf(lbos, this.numLevels, C_LEVELS);
        mask |= TextRulerAtom.writeIf(lbos, this.defaultTabSize, DEFAULT_TAB_SIZE);
        mask |= TextRulerAtom.writeIf(lbos, this.tabStops, TAB_STOPS);
        for (int i = 0; i < 5; ++i) {
            mask |= TextRulerAtom.writeIf(lbos, this.leftMargin[i], LEFT_MARGIN_LVL_MASK[i]);
            mask |= TextRulerAtom.writeIf(lbos, this.indent[i], INDENT_LVL_MASK[i]);
        }
        LittleEndian.putInt(this._header, 4, bos.size() + 4);
        out.write(this._header);
        LittleEndian.putUShort(mask, out);
        LittleEndian.putUShort(0, out);
        bos.writeTo(out);
    }

    private static int writeIf(LittleEndianOutputStream lbos, Integer value, BitField bit) {
        boolean isSet = false;
        if (value != null) {
            lbos.writeShort(value);
            isSet = true;
        }
        return bit.setBoolean(0, isSet);
    }

    private static int writeIf(LittleEndianOutputStream lbos, List<HSLFTabStop> value, BitField bit) {
        boolean isSet = false;
        if (value != null && !value.isEmpty()) {
            HSLFTabStopPropCollection.writeTabStops(lbos, value);
            isSet = true;
        }
        return bit.setBoolean(0, isSet);
    }

    private void read(LittleEndianByteArrayInputStream leis) {
        int mask = leis.readInt();
        this.numLevels = TextRulerAtom.readIf(leis, mask, C_LEVELS);
        this.defaultTabSize = TextRulerAtom.readIf(leis, mask, DEFAULT_TAB_SIZE);
        if (TAB_STOPS.isSet(mask)) {
            this.tabStops.addAll(HSLFTabStopPropCollection.readTabStops(leis));
        }
        for (int i = 0; i < 5; ++i) {
            this.leftMargin[i] = TextRulerAtom.readIf(leis, mask, LEFT_MARGIN_LVL_MASK[i]);
            this.indent[i] = TextRulerAtom.readIf(leis, mask, INDENT_LVL_MASK[i]);
        }
    }

    private static Integer readIf(LittleEndianByteArrayInputStream leis, int mask, BitField bit) {
        return bit.isSet(mask) ? Integer.valueOf(leis.readShort()) : null;
    }

    public int getDefaultTabSize() {
        return this.defaultTabSize == null ? 0 : this.defaultTabSize;
    }

    public int getNumberOfLevels() {
        return this.numLevels == null ? 0 : this.numLevels;
    }

    public List<HSLFTabStop> getTabStops() {
        return this.tabStops;
    }

    public Integer[] getTextOffsets() {
        return this.leftMargin;
    }

    public Integer[] getBulletOffsets() {
        return this.indent;
    }

    public static TextRulerAtom getParagraphInstance() {
        TextRulerAtom tra = new TextRulerAtom();
        tra.indent[0] = 249;
        tra.indent[1] = tra.leftMargin[1] = Integer.valueOf(321);
        return tra;
    }

    public void setParagraphIndent(short leftMargin, short indent) {
        Arrays.fill((Object[])this.leftMargin, null);
        Arrays.fill((Object[])this.indent, null);
        this.leftMargin[0] = leftMargin;
        this.indent[0] = indent;
        this.indent[1] = indent;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("defaultTabSize", this::getDefaultTabSize, "numLevels", this::getNumberOfLevels, "tabStops", this::getTabStops, "leftMargins", () -> this.leftMargin, "indents", () -> this.indent);
    }
}

