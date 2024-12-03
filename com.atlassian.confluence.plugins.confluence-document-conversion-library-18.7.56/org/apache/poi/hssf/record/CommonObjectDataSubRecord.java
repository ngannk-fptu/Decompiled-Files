/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

public final class CommonObjectDataSubRecord
extends SubRecord {
    public static final short sid = 21;
    private static final BitField locked = BitFieldFactory.getInstance(1);
    private static final BitField printable = BitFieldFactory.getInstance(16);
    private static final BitField autofill = BitFieldFactory.getInstance(8192);
    private static final BitField autoline = BitFieldFactory.getInstance(16384);
    public static final short OBJECT_TYPE_GROUP = 0;
    public static final short OBJECT_TYPE_LINE = 1;
    public static final short OBJECT_TYPE_RECTANGLE = 2;
    public static final short OBJECT_TYPE_OVAL = 3;
    public static final short OBJECT_TYPE_ARC = 4;
    public static final short OBJECT_TYPE_CHART = 5;
    public static final short OBJECT_TYPE_TEXT = 6;
    public static final short OBJECT_TYPE_BUTTON = 7;
    public static final short OBJECT_TYPE_PICTURE = 8;
    public static final short OBJECT_TYPE_POLYGON = 9;
    public static final short OBJECT_TYPE_RESERVED1 = 10;
    public static final short OBJECT_TYPE_CHECKBOX = 11;
    public static final short OBJECT_TYPE_OPTION_BUTTON = 12;
    public static final short OBJECT_TYPE_EDIT_BOX = 13;
    public static final short OBJECT_TYPE_LABEL = 14;
    public static final short OBJECT_TYPE_DIALOG_BOX = 15;
    public static final short OBJECT_TYPE_SPINNER = 16;
    public static final short OBJECT_TYPE_SCROLL_BAR = 17;
    public static final short OBJECT_TYPE_LIST_BOX = 18;
    public static final short OBJECT_TYPE_GROUP_BOX = 19;
    public static final short OBJECT_TYPE_COMBO_BOX = 20;
    public static final short OBJECT_TYPE_RESERVED2 = 21;
    public static final short OBJECT_TYPE_RESERVED3 = 22;
    public static final short OBJECT_TYPE_RESERVED4 = 23;
    public static final short OBJECT_TYPE_RESERVED5 = 24;
    public static final short OBJECT_TYPE_COMMENT = 25;
    public static final short OBJECT_TYPE_RESERVED6 = 26;
    public static final short OBJECT_TYPE_RESERVED7 = 27;
    public static final short OBJECT_TYPE_RESERVED8 = 28;
    public static final short OBJECT_TYPE_RESERVED9 = 29;
    public static final short OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    private short field_1_objectType;
    private int field_2_objectId;
    private short field_3_option;
    private int field_4_reserved1;
    private int field_5_reserved2;
    private int field_6_reserved3;

    public CommonObjectDataSubRecord() {
    }

    public CommonObjectDataSubRecord(CommonObjectDataSubRecord other) {
        super(other);
        this.field_1_objectType = other.field_1_objectType;
        this.field_2_objectId = other.field_2_objectId;
        this.field_3_option = other.field_3_option;
        this.field_4_reserved1 = other.field_4_reserved1;
        this.field_5_reserved2 = other.field_5_reserved2;
        this.field_6_reserved3 = other.field_6_reserved3;
    }

    public CommonObjectDataSubRecord(LittleEndianInput in, int size) {
        this(in, size, -1);
    }

    CommonObjectDataSubRecord(LittleEndianInput in, int size, int cmoOt) {
        if (size != 18) {
            throw new RecordFormatException("Expected size 18 but got (" + size + ")");
        }
        this.field_1_objectType = in.readShort();
        this.field_2_objectId = in.readUShort();
        this.field_3_option = in.readShort();
        this.field_4_reserved1 = in.readInt();
        this.field_5_reserved2 = in.readInt();
        this.field_6_reserved3 = in.readInt();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(21);
        out.writeShort(this.getDataSize());
        out.writeShort(this.field_1_objectType);
        out.writeShort(this.field_2_objectId);
        out.writeShort(this.field_3_option);
        out.writeInt(this.field_4_reserved1);
        out.writeInt(this.field_5_reserved2);
        out.writeInt(this.field_6_reserved3);
    }

    @Override
    protected int getDataSize() {
        return 18;
    }

    public short getSid() {
        return 21;
    }

    @Override
    public CommonObjectDataSubRecord copy() {
        return new CommonObjectDataSubRecord(this);
    }

    public short getObjectType() {
        return this.field_1_objectType;
    }

    public void setObjectType(short field_1_objectType) {
        this.field_1_objectType = field_1_objectType;
    }

    public int getObjectId() {
        return this.field_2_objectId;
    }

    public void setObjectId(int field_2_objectId) {
        this.field_2_objectId = field_2_objectId;
    }

    public short getOption() {
        return this.field_3_option;
    }

    public void setOption(short field_3_option) {
        this.field_3_option = field_3_option;
    }

    public int getReserved1() {
        return this.field_4_reserved1;
    }

    public void setReserved1(int field_4_reserved1) {
        this.field_4_reserved1 = field_4_reserved1;
    }

    public int getReserved2() {
        return this.field_5_reserved2;
    }

    public void setReserved2(int field_5_reserved2) {
        this.field_5_reserved2 = field_5_reserved2;
    }

    public int getReserved3() {
        return this.field_6_reserved3;
    }

    public void setReserved3(int field_6_reserved3) {
        this.field_6_reserved3 = field_6_reserved3;
    }

    public void setLocked(boolean value) {
        this.field_3_option = locked.setShortBoolean(this.field_3_option, value);
    }

    public boolean isLocked() {
        return locked.isSet(this.field_3_option);
    }

    public void setPrintable(boolean value) {
        this.field_3_option = printable.setShortBoolean(this.field_3_option, value);
    }

    public boolean isPrintable() {
        return printable.isSet(this.field_3_option);
    }

    public void setAutofill(boolean value) {
        this.field_3_option = autofill.setShortBoolean(this.field_3_option, value);
    }

    public boolean isAutofill() {
        return autofill.isSet(this.field_3_option);
    }

    public void setAutoline(boolean value) {
        this.field_3_option = autoline.setShortBoolean(this.field_3_option, value);
    }

    public boolean isAutoline() {
        return autoline.isSet(this.field_3_option);
    }

    @Override
    public SubRecord.SubRecordTypes getGenericRecordType() {
        return SubRecord.SubRecordTypes.COMMON_OBJECT_DATA;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("objectType", this::getObjectType, "objectId", this::getObjectId, "option", GenericRecordUtil.getBitsAsString(this::getOption, new BitField[]{locked, printable, autofill, autoline}, new String[]{"LOCKED", "PRINTABLE", "AUTOFILL", "AUTOLINE"}), "reserved1", this::getReserved1, "reserved2", this::getReserved2, "reserved3", this::getReserved3);
    }
}

