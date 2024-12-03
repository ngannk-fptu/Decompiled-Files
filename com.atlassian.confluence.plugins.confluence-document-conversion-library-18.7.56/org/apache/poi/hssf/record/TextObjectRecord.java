/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.cont.ContinuableRecord;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.RecordFormatException;

public final class TextObjectRecord
extends ContinuableRecord {
    public static final short sid = 438;
    private static final int FORMAT_RUN_ENCODED_SIZE = 8;
    private static final BitField HorizontalTextAlignment = BitFieldFactory.getInstance(14);
    private static final BitField VerticalTextAlignment = BitFieldFactory.getInstance(112);
    private static final BitField textLocked = BitFieldFactory.getInstance(512);
    public static final short HORIZONTAL_TEXT_ALIGNMENT_LEFT_ALIGNED = 1;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_CENTERED = 2;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_RIGHT_ALIGNED = 3;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_JUSTIFIED = 4;
    public static final short VERTICAL_TEXT_ALIGNMENT_TOP = 1;
    public static final short VERTICAL_TEXT_ALIGNMENT_CENTER = 2;
    public static final short VERTICAL_TEXT_ALIGNMENT_BOTTOM = 3;
    public static final short VERTICAL_TEXT_ALIGNMENT_JUSTIFY = 4;
    public static final short TEXT_ORIENTATION_NONE = 0;
    public static final short TEXT_ORIENTATION_TOP_TO_BOTTOM = 1;
    public static final short TEXT_ORIENTATION_ROT_RIGHT = 2;
    public static final short TEXT_ORIENTATION_ROT_LEFT = 3;
    private int field_1_options;
    private int field_2_textOrientation;
    private int field_3_reserved4;
    private int field_4_reserved5;
    private int field_5_reserved6;
    private int field_8_reserved7;
    private HSSFRichTextString _text;
    private int _unknownPreFormulaInt;
    private OperandPtg _linkRefPtg;
    private Byte _unknownPostFormulaByte;

    public TextObjectRecord() {
    }

    public TextObjectRecord(TextObjectRecord other) {
        super(other);
        this.field_1_options = other.field_1_options;
        this.field_2_textOrientation = other.field_2_textOrientation;
        this.field_3_reserved4 = other.field_3_reserved4;
        this.field_4_reserved5 = other.field_4_reserved5;
        this.field_5_reserved6 = other.field_5_reserved6;
        this.field_8_reserved7 = other.field_8_reserved7;
        this._text = other._text;
        if (other._linkRefPtg != null) {
            this._unknownPreFormulaInt = other._unknownPreFormulaInt;
            this._linkRefPtg = other._linkRefPtg.copy();
            this._unknownPostFormulaByte = other._unknownPostFormulaByte;
        }
    }

    public TextObjectRecord(RecordInputStream in) {
        this.field_1_options = in.readUShort();
        this.field_2_textOrientation = in.readUShort();
        this.field_3_reserved4 = in.readUShort();
        this.field_4_reserved5 = in.readUShort();
        this.field_5_reserved6 = in.readUShort();
        int field_6_textLength = in.readUShort();
        int field_7_formattingDataLength = in.readUShort();
        this.field_8_reserved7 = in.readInt();
        if (in.remaining() > 0) {
            if (in.remaining() < 11) {
                throw new RecordFormatException("Not enough remaining data for a link formula");
            }
            int formulaSize = in.readUShort();
            this._unknownPreFormulaInt = in.readInt();
            Ptg[] ptgs = Ptg.readTokens(formulaSize, in);
            if (ptgs.length != 1) {
                throw new RecordFormatException("Read " + ptgs.length + " tokens but expected exactly 1");
            }
            this._linkRefPtg = (OperandPtg)ptgs[0];
            this._unknownPostFormulaByte = in.remaining() > 0 ? Byte.valueOf(in.readByte()) : null;
        } else {
            this._linkRefPtg = null;
        }
        if (in.remaining() > 0) {
            throw new RecordFormatException("Unused " + in.remaining() + " bytes at end of record");
        }
        String text = field_6_textLength > 0 ? TextObjectRecord.readRawString(in, field_6_textLength) : "";
        this._text = new HSSFRichTextString(text);
        if (field_7_formattingDataLength > 0) {
            TextObjectRecord.processFontRuns(in, this._text, field_7_formattingDataLength);
        }
    }

    private static String readRawString(RecordInputStream in, int textLength) {
        boolean isCompressed;
        byte compressByte = in.readByte();
        boolean bl = isCompressed = (compressByte & 1) == 0;
        if (isCompressed) {
            return in.readCompressedUnicode(textLength);
        }
        return in.readUnicodeLEString(textLength);
    }

    private static void processFontRuns(RecordInputStream in, HSSFRichTextString str, int formattingRunDataLength) {
        if (formattingRunDataLength % 8 != 0) {
            throw new RecordFormatException("Bad format run data length " + formattingRunDataLength + ")");
        }
        int nRuns = formattingRunDataLength / 8;
        for (int i = 0; i < nRuns; ++i) {
            short index = in.readShort();
            short iFont = in.readShort();
            in.readInt();
            str.applyFont((int)index, str.length(), iFont);
        }
    }

    @Override
    public short getSid() {
        return 438;
    }

    private void serializeTXORecord(ContinuableRecordOutput out) {
        out.writeShort(this.field_1_options);
        out.writeShort(this.field_2_textOrientation);
        out.writeShort(this.field_3_reserved4);
        out.writeShort(this.field_4_reserved5);
        out.writeShort(this.field_5_reserved6);
        out.writeShort(this._text.length());
        out.writeShort(this.getFormattingDataLength());
        out.writeInt(this.field_8_reserved7);
        if (this._linkRefPtg != null) {
            int formulaSize = this._linkRefPtg.getSize();
            out.writeShort(formulaSize);
            out.writeInt(this._unknownPreFormulaInt);
            this._linkRefPtg.write(out);
            if (this._unknownPostFormulaByte != null) {
                out.writeByte(this._unknownPostFormulaByte.byteValue());
            }
        }
    }

    private void serializeTrailingRecords(ContinuableRecordOutput out) {
        out.writeContinue();
        out.writeStringData(this._text.getString());
        out.writeContinue();
        TextObjectRecord.writeFormatData(out, this._text);
    }

    @Override
    protected void serialize(ContinuableRecordOutput out) {
        this.serializeTXORecord(out);
        if (this._text.getString().length() > 0) {
            this.serializeTrailingRecords(out);
        }
    }

    private int getFormattingDataLength() {
        if (this._text.length() < 1) {
            return 0;
        }
        return (this._text.numFormattingRuns() + 1) * 8;
    }

    private static void writeFormatData(ContinuableRecordOutput out, HSSFRichTextString str) {
        int nRuns = str.numFormattingRuns();
        for (int i = 0; i < nRuns; ++i) {
            out.writeShort(str.getIndexOfFormattingRun(i));
            short fontIndex = str.getFontOfFormattingRun(i);
            out.writeShort(fontIndex == 0 ? (short)0 : fontIndex);
            out.writeInt(0);
        }
        out.writeShort(str.length());
        out.writeShort(0);
        out.writeInt(0);
    }

    public void setHorizontalTextAlignment(int value) {
        this.field_1_options = HorizontalTextAlignment.setValue(this.field_1_options, value);
    }

    public int getHorizontalTextAlignment() {
        return HorizontalTextAlignment.getValue(this.field_1_options);
    }

    public void setVerticalTextAlignment(int value) {
        this.field_1_options = VerticalTextAlignment.setValue(this.field_1_options, value);
    }

    public int getVerticalTextAlignment() {
        return VerticalTextAlignment.getValue(this.field_1_options);
    }

    public void setTextLocked(boolean value) {
        this.field_1_options = textLocked.setBoolean(this.field_1_options, value);
    }

    public boolean isTextLocked() {
        return textLocked.isSet(this.field_1_options);
    }

    public int getTextOrientation() {
        return this.field_2_textOrientation;
    }

    public void setTextOrientation(int textOrientation) {
        this.field_2_textOrientation = textOrientation;
    }

    public HSSFRichTextString getStr() {
        return this._text;
    }

    public void setStr(HSSFRichTextString str) {
        this._text = str;
    }

    public Ptg getLinkRefPtg() {
        return this._linkRefPtg;
    }

    @Override
    public TextObjectRecord copy() {
        return new TextObjectRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.TEXT_OBJECT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("isHorizontal", this::getHorizontalTextAlignment);
        m.put("isVertical", this::getVerticalTextAlignment);
        m.put("textLocked", this::isTextLocked);
        m.put("textOrientation", this::getTextOrientation);
        m.put("string", this::getStr);
        m.put("reserved4", () -> this.field_3_reserved4);
        m.put("reserved5", () -> this.field_4_reserved5);
        m.put("reserved6", () -> this.field_5_reserved6);
        m.put("reserved7", () -> this.field_8_reserved7);
        return Collections.unmodifiableMap(m);
    }
}

