/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hmef.attribute.TNEFDateAttribute;
import org.apache.poi.hmef.attribute.TNEFMAPIAttribute;
import org.apache.poi.hmef.attribute.TNEFProperty;
import org.apache.poi.hmef.attribute.TNEFStringAttribute;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class TNEFAttribute {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 20000000;
    private static int MAX_RECORD_LENGTH = 20000000;
    private final TNEFProperty property;
    private final int type;
    private final byte[] data;
    private final int checksum;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    protected TNEFAttribute(int id, int type, InputStream inp) throws IOException {
        this.type = type;
        int length = LittleEndian.readInt(inp);
        this.property = TNEFProperty.getBest(id, type);
        this.data = IOUtils.safelyAllocate(length, MAX_RECORD_LENGTH);
        IOUtils.readFully(inp, this.data);
        this.checksum = LittleEndian.readUShort(inp);
    }

    public static TNEFAttribute create(InputStream inp) throws IOException {
        int id = LittleEndian.readUShort(inp);
        int type = LittleEndian.readUShort(inp);
        if (id == TNEFProperty.ID_MAPIPROPERTIES.id || id == TNEFProperty.ID_ATTACHMENT.id) {
            return new TNEFMAPIAttribute(id, type, inp);
        }
        if (type == 1 || type == 2) {
            return new TNEFStringAttribute(id, type, inp);
        }
        if (type == 3) {
            return new TNEFDateAttribute(id, type, inp);
        }
        return new TNEFAttribute(id, type, inp);
    }

    public TNEFProperty getProperty() {
        return this.property;
    }

    public int getType() {
        return this.type;
    }

    public byte[] getData() {
        return this.data;
    }

    public String toString() {
        return "Attribute " + this.property + ", type=" + this.type + ", data length=" + this.data.length;
    }
}

