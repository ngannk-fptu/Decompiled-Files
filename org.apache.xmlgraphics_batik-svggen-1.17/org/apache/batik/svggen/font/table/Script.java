/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.LangSys;
import org.apache.batik.svggen.font.table.LangSysRecord;

public class Script {
    private int defaultLangSysOffset;
    private int langSysCount;
    private LangSysRecord[] langSysRecords;
    private LangSys defaultLangSys;
    private LangSys[] langSys;

    protected Script(RandomAccessFile raf, int offset) throws IOException {
        int i;
        raf.seek(offset);
        this.defaultLangSysOffset = raf.readUnsignedShort();
        this.langSysCount = raf.readUnsignedShort();
        if (this.langSysCount > 0) {
            this.langSysRecords = new LangSysRecord[this.langSysCount];
            for (i = 0; i < this.langSysCount; ++i) {
                this.langSysRecords[i] = new LangSysRecord(raf);
            }
        }
        if (this.langSysCount > 0) {
            this.langSys = new LangSys[this.langSysCount];
            for (i = 0; i < this.langSysCount; ++i) {
                raf.seek(offset + this.langSysRecords[i].getOffset());
                this.langSys[i] = new LangSys(raf);
            }
        }
        if (this.defaultLangSysOffset > 0) {
            raf.seek(offset + this.defaultLangSysOffset);
            this.defaultLangSys = new LangSys(raf);
        }
    }

    public LangSys getDefaultLangSys() {
        return this.defaultLangSys;
    }
}

