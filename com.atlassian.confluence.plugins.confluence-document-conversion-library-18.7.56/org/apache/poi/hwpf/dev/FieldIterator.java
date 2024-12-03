/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.dev;

import org.apache.poi.hwpf.dev.RecordUtil;
import org.apache.poi.util.Internal;

@Internal
public class FieldIterator {
    protected int offset;

    public String calcSize(int fieldNumber, String fieldName, String size, String type) {
        String result = " + ";
        if (type.startsWith("custom:")) {
            String javaFieldName = RecordUtil.getFieldName(fieldNumber, fieldName, 0);
            return result + javaFieldName + ".getSize()";
        }
        if ("var".equals(size)) {
            String javaFieldName = RecordUtil.getFieldName(fieldNumber, fieldName, 0);
            return result + " ( " + javaFieldName + ".length() *2)";
        }
        if ("varword".equals(size)) {
            String javaFieldName = RecordUtil.getFieldName(fieldNumber, fieldName, 0);
            return result + javaFieldName + ".length * 2 + 2";
        }
        return result + size;
    }

    public String fillDecoder(String size, String type) {
        String result = "";
        if (type.equals("short[]")) {
            result = "LittleEndian.getShortArray( data, 0x" + Integer.toHexString(this.offset) + " + offset, " + size + " )";
        } else if (type.equals("byte[]")) {
            result = "LittleEndian.getByteArray( data, 0x" + Integer.toHexString(this.offset) + " + offset," + size + " )";
        } else if (type.equals("BorderCode")) {
            result = "new BorderCode( data, 0x" + Integer.toHexString(this.offset) + " + offset )";
        } else if (type.equals("Colorref")) {
            result = "new Colorref( data, 0x" + Integer.toHexString(this.offset) + " + offset )";
        } else if (type.equals("DateAndTime")) {
            result = "new DateAndTime( data, 0x" + Integer.toHexString(this.offset) + " + offset )";
        } else if (type.equals("Grfhic")) {
            result = "new Grfhic( data, 0x" + Integer.toHexString(this.offset) + " + offset )";
        } else if (size.equals("2")) {
            result = "LittleEndian.getShort( data, 0x" + Integer.toHexString(this.offset) + " + offset )";
        } else if (size.equals("4")) {
            result = type.equals("long") ? "LittleEndian.getUInt( data, 0x" + Integer.toHexString(this.offset) + " + offset )" : "LittleEndian.getInt( data, 0x" + Integer.toHexString(this.offset) + " + offset )";
        } else if (size.equals("1")) {
            result = type.equals("short") ? "LittleEndian.getUByte( data, 0x" + Integer.toHexString(this.offset) + " + offset )" : (type.equals("int") || type.equals("long") ? "LittleEndian.getUnsignedByte( data, 0x" + Integer.toHexString(this.offset) + " + offset )" : "data[ 0x" + Integer.toHexString(this.offset) + " + offset ]");
        } else if (type.equals("double")) {
            result = "LittleEndian.getDouble(data, 0x" + Integer.toHexString(this.offset) + " + offset )";
        }
        try {
            this.offset += Integer.parseInt(size);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return result;
    }

    public String serialiseEncoder(int fieldNumber, String fieldName, String size, String type) {
        String javaFieldName = RecordUtil.getFieldName(fieldNumber, fieldName, 0);
        String result = "";
        if (type.equals("short[]")) {
            result = "LittleEndian.putShortArray( data, 0x" + Integer.toHexString(this.offset) + " + offset, " + javaFieldName + " );";
        } else if (type.equals("byte[]")) {
            result = "System.arraycopy( " + javaFieldName + ", 0, data, 0x" + Integer.toHexString(this.offset) + " + offset, " + javaFieldName + ".length );";
        } else if (type.equals("BorderCode")) {
            result = javaFieldName + ".serialize( data, 0x" + Integer.toHexString(this.offset) + " + offset );";
        } else if (type.equals("Colorref")) {
            result = javaFieldName + ".serialize( data, 0x" + Integer.toHexString(this.offset) + " + offset );";
        } else if (type.equals("DateAndTime")) {
            result = javaFieldName + ".serialize( data, 0x" + Integer.toHexString(this.offset) + " + offset );";
        } else if (type.equals("Grfhic")) {
            result = javaFieldName + ".serialize( data, 0x" + Integer.toHexString(this.offset) + " + offset );";
        } else if (size.equals("2")) {
            result = type.equals("short") ? "LittleEndian.putShort( data, 0x" + Integer.toHexString(this.offset) + " + offset, " + javaFieldName + " );" : (type.equals("int") ? "LittleEndian.putUShort( data, 0x" + Integer.toHexString(this.offset) + " + offset, " + javaFieldName + " );" : "LittleEndian.putShort( data, 0x" + Integer.toHexString(this.offset) + " + offset, (short)" + javaFieldName + " );");
        } else if (size.equals("4")) {
            result = type.equals("long") ? "LittleEndian.putUInt( data, 0x" + Integer.toHexString(this.offset) + " + offset, " + javaFieldName + " );" : "LittleEndian.putInt( data, 0x" + Integer.toHexString(this.offset) + " + offset, " + javaFieldName + " );";
        } else if (size.equals("1")) {
            result = type.equals("byte") ? "data[ 0x" + Integer.toHexString(this.offset) + " + offset ] = " + javaFieldName + ";" : "LittleEndian.putUByte( data, 0x" + Integer.toHexString(this.offset) + " + offset, " + javaFieldName + " );";
        } else if (type.equals("double")) {
            result = "LittleEndian.putDouble(data, 0x" + Integer.toHexString(this.offset) + " + offset, " + javaFieldName + " );";
        }
        try {
            this.offset += Integer.parseInt(size);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return result;
    }
}

