/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.cf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndian;

public final class FontFormatting
implements Duplicatable,
GenericRecord {
    private static final int OFFSET_FONT_NAME = 0;
    private static final int OFFSET_FONT_HEIGHT = 64;
    private static final int OFFSET_FONT_OPTIONS = 68;
    private static final int OFFSET_FONT_WEIGHT = 72;
    private static final int OFFSET_ESCAPEMENT_TYPE = 74;
    private static final int OFFSET_UNDERLINE_TYPE = 76;
    private static final int OFFSET_FONT_COLOR_INDEX = 80;
    private static final int OFFSET_OPTION_FLAGS = 88;
    private static final int OFFSET_ESCAPEMENT_TYPE_MODIFIED = 92;
    private static final int OFFSET_UNDERLINE_TYPE_MODIFIED = 96;
    private static final int OFFSET_FONT_WEIGHT_MODIFIED = 100;
    private static final int OFFSET_NOT_USED1 = 104;
    private static final int OFFSET_NOT_USED2 = 108;
    private static final int OFFSET_NOT_USED3 = 112;
    private static final int OFFSET_FONT_FORMATING_END = 116;
    private static final int RAW_DATA_SIZE = 118;
    public static final int FONT_CELL_HEIGHT_PRESERVED = -1;
    private static final BitField POSTURE = BitFieldFactory.getInstance(2);
    private static final BitField OUTLINE = BitFieldFactory.getInstance(8);
    private static final BitField SHADOW = BitFieldFactory.getInstance(16);
    private static final BitField CANCELLATION = BitFieldFactory.getInstance(128);
    private static final short FONT_WEIGHT_NORMAL = 400;
    private static final short FONT_WEIGHT_BOLD = 700;
    private final byte[] _rawData = new byte[118];

    public FontFormatting() {
        this.setFontHeight(-1);
        this.setItalic(false);
        this.setFontWieghtModified(false);
        this.setOutline(false);
        this.setShadow(false);
        this.setStrikeout(false);
        this.setEscapementType((short)0);
        this.setUnderlineType((short)0);
        this.setFontColorIndex((short)-1);
        this.setFontStyleModified(false);
        this.setFontOutlineModified(false);
        this.setFontShadowModified(false);
        this.setFontCancellationModified(false);
        this.setEscapementTypeModified(false);
        this.setUnderlineTypeModified(false);
        this.setShort(0, 0);
        this.setInt(104, 1);
        this.setInt(108, 0);
        this.setInt(112, Integer.MAX_VALUE);
        this.setShort(116, 1);
    }

    public FontFormatting(FontFormatting other) {
        System.arraycopy(other._rawData, 0, this._rawData, 0, 118);
    }

    public FontFormatting(RecordInputStream in) {
        in.readFully(this._rawData);
    }

    private short getShort(int offset) {
        return LittleEndian.getShort(this._rawData, offset);
    }

    private void setShort(int offset, int value) {
        LittleEndian.putShort(this._rawData, offset, (short)value);
    }

    private int getInt(int offset) {
        return LittleEndian.getInt(this._rawData, offset);
    }

    private void setInt(int offset, int value) {
        LittleEndian.putInt(this._rawData, offset, value);
    }

    public byte[] getRawRecord() {
        return this._rawData;
    }

    public int getDataLength() {
        return 118;
    }

    public void setFontHeight(int height) {
        this.setInt(64, height);
    }

    public int getFontHeight() {
        return this.getInt(64);
    }

    private void setFontOption(boolean option, BitField field) {
        int options = this.getInt(68);
        options = field.setBoolean(options, option);
        this.setInt(68, options);
    }

    private boolean getFontOption(BitField field) {
        int options = this.getInt(68);
        return field.isSet(options);
    }

    public void setItalic(boolean italic) {
        this.setFontOption(italic, POSTURE);
    }

    public boolean isItalic() {
        return this.getFontOption(POSTURE);
    }

    public void setOutline(boolean on) {
        this.setFontOption(on, OUTLINE);
    }

    public boolean isOutlineOn() {
        return this.getFontOption(OUTLINE);
    }

    public void setShadow(boolean on) {
        this.setFontOption(on, SHADOW);
    }

    public boolean isShadowOn() {
        return this.getFontOption(SHADOW);
    }

    public void setStrikeout(boolean strike) {
        this.setFontOption(strike, CANCELLATION);
    }

    public boolean isStruckout() {
        return this.getFontOption(CANCELLATION);
    }

    private void setFontWeight(short bw) {
        this.setShort(72, Math.max(100, Math.min(1000, bw)));
    }

    public void setBold(boolean bold) {
        this.setFontWeight(bold ? (short)700 : 400);
    }

    public short getFontWeight() {
        return this.getShort(72);
    }

    public boolean isBold() {
        return this.getFontWeight() == 700;
    }

    public short getEscapementType() {
        return this.getShort(74);
    }

    public void setEscapementType(short escapementType) {
        this.setShort(74, escapementType);
    }

    public short getUnderlineType() {
        return this.getShort(76);
    }

    public void setUnderlineType(short underlineType) {
        this.setShort(76, underlineType);
    }

    public short getFontColorIndex() {
        return (short)this.getInt(80);
    }

    public void setFontColorIndex(short fci) {
        this.setInt(80, fci);
    }

    private boolean getOptionFlag(BitField field) {
        int optionFlags = this.getInt(88);
        int value = field.getValue(optionFlags);
        return value == 0;
    }

    private void setOptionFlag(boolean modified, BitField field) {
        int value = modified ? 0 : 1;
        int optionFlags = this.getInt(88);
        optionFlags = field.setValue(optionFlags, value);
        this.setInt(88, optionFlags);
    }

    public boolean isFontStyleModified() {
        return this.getOptionFlag(POSTURE);
    }

    public void setFontStyleModified(boolean modified) {
        this.setOptionFlag(modified, POSTURE);
    }

    public boolean isFontOutlineModified() {
        return this.getOptionFlag(OUTLINE);
    }

    public void setFontOutlineModified(boolean modified) {
        this.setOptionFlag(modified, OUTLINE);
    }

    public boolean isFontShadowModified() {
        return this.getOptionFlag(SHADOW);
    }

    public void setFontShadowModified(boolean modified) {
        this.setOptionFlag(modified, SHADOW);
    }

    public void setFontCancellationModified(boolean modified) {
        this.setOptionFlag(modified, CANCELLATION);
    }

    public boolean isFontCancellationModified() {
        return this.getOptionFlag(CANCELLATION);
    }

    public void setEscapementTypeModified(boolean modified) {
        int value = modified ? 0 : 1;
        this.setInt(92, value);
    }

    public boolean isEscapementTypeModified() {
        int escapementModified = this.getInt(92);
        return escapementModified == 0;
    }

    public void setUnderlineTypeModified(boolean modified) {
        int value = modified ? 0 : 1;
        this.setInt(96, value);
    }

    public boolean isUnderlineTypeModified() {
        int underlineModified = this.getInt(96);
        return underlineModified == 0;
    }

    public void setFontWieghtModified(boolean modified) {
        int value = modified ? 0 : 1;
        this.setInt(100, value);
    }

    public boolean isFontWeightModified() {
        int fontStyleModified = this.getInt(100);
        return fontStyleModified == 0;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("fontHeight", this::getFontHeight);
        m.put("options", GenericRecordUtil.getBitsAsString(() -> this.getInt(88), new BitField[]{POSTURE, OUTLINE, SHADOW, CANCELLATION}, new String[]{"POSTURE_MODIFIED", "OUTLINE_MODIFIED", "SHADOW_MODIFIED", "STRUCKOUT_MODIFIED"}));
        m.put("fontOptions", GenericRecordUtil.getBitsAsString(() -> this.getInt(68), new BitField[]{POSTURE, OUTLINE, SHADOW, CANCELLATION}, new String[]{"ITALIC", "OUTLINE", "SHADOW", "STRUCKOUT"}));
        m.put("fontWEightModified", this::isFontWeightModified);
        m.put("fontWeight", GenericRecordUtil.getEnumBitsAsString(this::getFontWeight, new int[]{400, 700}, new String[]{"NORMAL", "BOLD"}));
        m.put("escapementTypeModified", this::isEscapementTypeModified);
        m.put("escapementType", this::getEscapementType);
        m.put("underlineTypeModified", this::isUnderlineTypeModified);
        m.put("underlineType", this::getUnderlineType);
        m.put("colorIndex", this::getFontColorIndex);
        return Collections.unmodifiableMap(m);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public FontFormatting copy() {
        return new FontFormatting(this);
    }
}

