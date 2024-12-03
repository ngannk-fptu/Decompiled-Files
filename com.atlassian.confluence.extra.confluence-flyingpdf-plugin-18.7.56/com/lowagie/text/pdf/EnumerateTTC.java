/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.TrueTypeFont;
import java.io.IOException;
import java.util.HashMap;

class EnumerateTTC
extends TrueTypeFont {
    protected String[] names;

    EnumerateTTC(String ttcFile) throws DocumentException, IOException {
        this.fileName = ttcFile;
        this.rf = new RandomAccessFileOrArray(ttcFile);
        this.findNames();
    }

    EnumerateTTC(byte[] ttcArray) throws DocumentException, IOException {
        this.fileName = "Byte array TTC";
        this.rf = new RandomAccessFileOrArray(ttcArray);
        this.findNames();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void findNames() throws DocumentException, IOException {
        this.tables = new HashMap();
        try {
            String mainTag = this.readStandardString(4);
            if (!mainTag.equals("ttcf")) {
                throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.valid.ttc.file", this.fileName));
            }
            this.rf.skipBytes(4);
            int dirCount = this.rf.readInt();
            this.names = new String[dirCount];
            int dirPos = this.rf.getFilePointer();
            for (int dirIdx = 0; dirIdx < dirCount; ++dirIdx) {
                this.tables.clear();
                this.rf.seek(dirPos);
                this.rf.skipBytes(dirIdx * 4);
                this.directoryOffset = this.rf.readInt();
                this.rf.seek(this.directoryOffset);
                if (this.rf.readInt() != 65536) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.valid.ttf.file", this.fileName));
                }
                int num_tables = this.rf.readUnsignedShort();
                this.rf.skipBytes(6);
                for (int k = 0; k < num_tables; ++k) {
                    String tag = this.readStandardString(4);
                    this.rf.skipBytes(4);
                    int[] table_location = new int[]{this.rf.readInt(), this.rf.readInt()};
                    this.tables.put(tag, table_location);
                }
                this.names[dirIdx] = this.getBaseFont();
            }
        }
        finally {
            if (this.rf != null) {
                this.rf.close();
            }
        }
    }

    String[] getNames() {
        return this.names;
    }
}

