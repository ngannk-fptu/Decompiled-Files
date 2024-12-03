/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hmef.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.hmef.attribute.MAPIDateAttribute;
import org.apache.poi.hmef.attribute.MAPIRtfAttribute;
import org.apache.poi.hmef.attribute.MAPIStringAttribute;
import org.apache.poi.hmef.attribute.TNEFAttribute;
import org.apache.poi.hmef.attribute.TNEFProperty;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

public class MAPIAttribute {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_COUNT = 10000;
    private final MAPIProperty property;
    private final int type;
    private final byte[] data;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public MAPIAttribute(MAPIProperty property, int type, byte[] data) {
        this.property = property;
        this.type = type;
        this.data = (byte[])data.clone();
    }

    public MAPIProperty getProperty() {
        return this.property;
    }

    public int getType() {
        return this.type;
    }

    public byte[] getData() {
        return this.data;
    }

    public String toString() {
        String hex;
        if (this.data.length <= 16) {
            hex = HexDump.toHex(this.data);
        } else {
            byte[] d = Arrays.copyOf(this.data, 16);
            hex = HexDump.toHex(d);
            hex = hex.substring(0, hex.length() - 1) + ", ....]";
        }
        return this.property + " " + hex;
    }

    public static List<MAPIAttribute> create(TNEFAttribute parent) throws IOException {
        if (parent.getProperty() != TNEFProperty.ID_MAPIPROPERTIES && parent.getProperty() != TNEFProperty.ID_ATTACHMENT) {
            throw new IllegalArgumentException("Can only create from a MAPIProperty attribute, instead received a " + parent.getProperty() + " one");
        }
        try (UnsynchronizedByteArrayInputStream inp = new UnsynchronizedByteArrayInputStream(parent.getData());){
            int count = LittleEndian.readInt((InputStream)inp);
            ArrayList<MAPIAttribute> attrs = new ArrayList<MAPIAttribute>();
            for (int i = 0; i < count; ++i) {
                Types.MAPIType type;
                int typeAndMV = LittleEndian.readUShort((InputStream)inp);
                int id = LittleEndian.readUShort((InputStream)inp);
                boolean isMV = false;
                boolean isVL = false;
                int typeId = typeAndMV;
                if ((typeAndMV & 0x1000) != 0) {
                    isMV = true;
                    typeId -= 4096;
                }
                if (typeId == Types.ASCII_STRING.getId() || typeId == Types.UNICODE_STRING.getId() || typeId == Types.BINARY.getId() || typeId == Types.DIRECTORY.getId()) {
                    isVL = true;
                }
                if ((type = Types.getById(typeId)) == null) {
                    type = Types.createCustom(typeId);
                }
                MAPIProperty prop = MAPIProperty.get(id);
                if (id >= 32768 && id <= 65535) {
                    String name;
                    byte[] guid = new byte[16];
                    if (IOUtils.readFully((InputStream)inp, guid) < 0) {
                        throw new IOException("Not enough data to read guid");
                    }
                    int mptype = LittleEndian.readInt((InputStream)inp);
                    if (mptype == 0) {
                        int mpid = LittleEndian.readInt((InputStream)inp);
                        MAPIProperty base = MAPIProperty.get(mpid);
                        name = base.name;
                    } else {
                        int mplen = LittleEndian.readInt((InputStream)inp);
                        byte[] mpdata = IOUtils.safelyAllocate(mplen, MAX_RECORD_LENGTH);
                        if (IOUtils.readFully((InputStream)inp, mpdata) < 0) {
                            throw new IOException("Not enough data to read " + mplen + " bytes for attribute name");
                        }
                        name = StringUtil.getFromUnicodeLE(mpdata, 0, mplen / 2 - 1);
                        MAPIAttribute.skipToBoundary(mplen, (InputStream)inp);
                    }
                    prop = MAPIProperty.createCustom(id, type, name);
                }
                if (prop == MAPIProperty.UNKNOWN) {
                    prop = MAPIProperty.createCustom(id, type, "(unknown " + Integer.toHexString(id) + ")");
                }
                int values = 1;
                if (isMV || isVL) {
                    values = LittleEndian.readInt((InputStream)inp);
                    IOUtils.safelyAllocateCheck(values, MAX_RECORD_COUNT);
                }
                if (type == Types.NULL && values > 1) {
                    throw new IOException("Placeholder/NULL arrays aren't supported.");
                }
                for (int j = 0; j < values; ++j) {
                    int len = MAPIAttribute.getLength(type, (InputStream)inp);
                    byte[] data = IOUtils.safelyAllocate(len, MAX_RECORD_LENGTH);
                    if (IOUtils.readFully((InputStream)inp, data) < 0) {
                        throw new IOException("Not enough data to read " + len + " bytes of attribute value");
                    }
                    MAPIAttribute.skipToBoundary(len, (InputStream)inp);
                    MAPIAttribute attr = type == Types.UNICODE_STRING || type == Types.ASCII_STRING ? new MAPIStringAttribute(prop, typeId, data) : (type == Types.APP_TIME || type == Types.TIME ? new MAPIDateAttribute(prop, typeId, data) : (id == MAPIProperty.RTF_COMPRESSED.id ? new MAPIRtfAttribute(prop, typeId, data) : new MAPIAttribute(prop, typeId, data)));
                    attrs.add(attr);
                }
            }
            ArrayList<MAPIAttribute> arrayList = attrs;
            return arrayList;
        }
    }

    private static int getLength(Types.MAPIType type, InputStream inp) throws IOException {
        if (type.isFixedLength()) {
            return type.getLength();
        }
        if (type == Types.ASCII_STRING || type == Types.UNICODE_STRING || type == Types.DIRECTORY || type == Types.BINARY) {
            return LittleEndian.readInt(inp);
        }
        throw new IllegalArgumentException("Unknown type " + type);
    }

    private static void skipToBoundary(int length, InputStream inp) throws IOException {
        int toSkip;
        long skipped;
        if (length % 4 != 0 && (skipped = IOUtils.skipFully(inp, toSkip = 4 - length % 4)) != (long)toSkip) {
            throw new IOException("tried to skip " + toSkip + " but only skipped:" + skipped);
        }
    }
}

