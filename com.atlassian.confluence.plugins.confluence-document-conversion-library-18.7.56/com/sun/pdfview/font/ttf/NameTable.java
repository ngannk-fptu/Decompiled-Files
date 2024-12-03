/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class NameTable
extends TrueTypeTable {
    public static final short PLATFORMID_UNICODE = 0;
    public static final short PLATFORMID_MACINTOSH = 1;
    public static final short PLATFORMID_MICROSOFT = 3;
    public static final short ENCODINGID_MAC_ROMAN = 0;
    public static final short ENCODINGID_UNICODE_DEFAULT = 0;
    public static final short ENCODINGID_UNICODE_V11 = 1;
    public static final short ENCODINGID_UNICODE_V2 = 3;
    public static final short LANGUAGEID_MAC_ENGLISH = 0;
    public static final short NAMEID_COPYRIGHT = 0;
    public static final short NAMEID_FAMILY = 1;
    public static final short NAMEID_SUBFAMILY = 2;
    public static final short NAMEID_SUBFAMILY_UNIQUE = 3;
    public static final short NAMEID_FULL_NAME = 4;
    public static final short NAMEID_VERSION = 5;
    public static final short NAMEID_POSTSCRIPT_NAME = 6;
    public static final short NAMEID_TRADEMARK = 7;
    private short format;
    private SortedMap<NameRecord, String> records = Collections.synchronizedSortedMap(new TreeMap());

    protected NameTable() {
        super(1851878757);
    }

    public void addRecord(short platformID, short platformSpecificID, short languageID, short nameID, String value) {
        NameRecord rec = new NameRecord(platformID, platformSpecificID, languageID, nameID);
        this.records.put(rec, value);
    }

    public String getRecord(short platformID, short platformSpecificID, short languageID, short nameID) {
        NameRecord rec = new NameRecord(platformID, platformSpecificID, languageID, nameID);
        return (String)this.records.get(rec);
    }

    public void removeRecord(short platformID, short platformSpecificID, short languageID, short nameID) {
        NameRecord rec = new NameRecord(platformID, platformSpecificID, languageID, nameID);
        this.records.remove(rec);
    }

    public boolean hasRecords(short platformID) {
        for (NameRecord rec : this.records.keySet()) {
            if (rec.platformID != platformID) continue;
            return true;
        }
        return false;
    }

    public boolean hasRecords(short platformID, short platformSpecificID) {
        for (NameRecord rec : this.records.keySet()) {
            if (rec.platformID != platformID || rec.platformSpecificID != platformSpecificID) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setData(ByteBuffer data) {
        this.setFormat(data.getShort());
        int count = data.getShort();
        short stringOffset = data.getShort();
        for (int i = 0; i < count; ++i) {
            short platformID = data.getShort();
            short platformSpecificID = data.getShort();
            short languageID = data.getShort();
            short nameID = data.getShort();
            int length = data.getShort() & 0xFFFF;
            int offset = data.getShort() & 0xFFFF;
            data.mark();
            data.position(stringOffset + offset);
            ByteBuffer stringBuf = data.slice();
            stringBuf.limit(length);
            data.reset();
            String charsetName = NameTable.getCharsetName(platformID, platformSpecificID);
            Charset charset = Charset.forName(charsetName);
            String value = charset.decode(stringBuf).toString();
            this.addRecord(platformID, platformSpecificID, languageID, nameID, value);
        }
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        short headerLength = (short)(6 + 12 * this.getCount());
        buf.putShort(this.getFormat());
        buf.putShort(this.getCount());
        buf.putShort(headerLength);
        short curOffset = 0;
        for (NameRecord rec : this.records.keySet()) {
            String value = (String)this.records.get(rec);
            String charsetName = NameTable.getCharsetName(rec.platformID, rec.platformSpecificID);
            Charset charset = Charset.forName(charsetName);
            ByteBuffer strBuf = charset.encode(value);
            short strLen = (short)(strBuf.remaining() & 0xFFFF);
            buf.putShort(rec.platformID);
            buf.putShort(rec.platformSpecificID);
            buf.putShort(rec.languageID);
            buf.putShort(rec.nameID);
            buf.putShort(strLen);
            buf.putShort(curOffset);
            buf.mark();
            buf.position(headerLength + curOffset);
            buf.put(strBuf);
            buf.reset();
            curOffset = (short)(curOffset + strLen);
        }
        buf.position(headerLength + curOffset);
        buf.flip();
        return buf;
    }

    @Override
    public int getLength() {
        int length = 6 + 12 * this.getCount();
        for (NameRecord rec : this.records.keySet()) {
            String value = (String)this.records.get(rec);
            String charsetName = NameTable.getCharsetName(rec.platformID, rec.platformSpecificID);
            Charset charset = Charset.forName(charsetName);
            ByteBuffer buf = charset.encode(value);
            length += buf.remaining();
        }
        return length;
    }

    public short getFormat() {
        return this.format;
    }

    public void setFormat(short format) {
        this.format = format;
    }

    public short getCount() {
        return (short)this.records.size();
    }

    public static String getCharsetName(int platformID, int encodingID) {
        String charset = "US-ASCII";
        switch (platformID) {
            case 0: {
                charset = "UTF-16";
                break;
            }
            case 3: {
                charset = "UTF-16";
            }
        }
        return charset;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
        buf.append(indent + "Format: " + this.getFormat() + "\n");
        buf.append(indent + "Count : " + this.getCount() + "\n");
        for (NameRecord rec : this.records.keySet()) {
            buf.append(indent + " platformID: " + rec.platformID);
            buf.append(" platformSpecificID: " + rec.platformSpecificID);
            buf.append(" languageID: " + rec.languageID);
            buf.append(" nameID: " + rec.nameID + "\n");
            buf.append(indent + "  " + (String)this.records.get(rec) + "\n");
        }
        return buf.toString();
    }

    class NameRecord
    implements Comparable {
        short platformID;
        short platformSpecificID;
        short languageID;
        short nameID;

        NameRecord(short platformID, short platformSpecificID, short languageID, short nameID) {
            this.platformID = platformID;
            this.platformSpecificID = platformSpecificID;
            this.languageID = languageID;
            this.nameID = nameID;
        }

        public boolean equals(Object o) {
            return this.compareTo(o) == 0;
        }

        public int compareTo(Object obj) {
            if (!(obj instanceof NameRecord)) {
                return -1;
            }
            NameRecord rec = (NameRecord)obj;
            if (this.platformID > rec.platformID) {
                return 1;
            }
            if (this.platformID < rec.platformID) {
                return -1;
            }
            if (this.platformSpecificID > rec.platformSpecificID) {
                return 1;
            }
            if (this.platformSpecificID < rec.platformSpecificID) {
                return -1;
            }
            if (this.languageID > rec.languageID) {
                return 1;
            }
            if (this.languageID < rec.languageID) {
                return -1;
            }
            if (this.nameID > rec.nameID) {
                return 1;
            }
            if (this.nameID < rec.nameID) {
                return -1;
            }
            return 0;
        }
    }
}

