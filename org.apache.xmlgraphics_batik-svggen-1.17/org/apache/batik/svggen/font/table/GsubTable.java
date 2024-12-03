/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.DirectoryEntry;
import org.apache.batik.svggen.font.table.FeatureList;
import org.apache.batik.svggen.font.table.LigatureSubst;
import org.apache.batik.svggen.font.table.LookupList;
import org.apache.batik.svggen.font.table.LookupSubtable;
import org.apache.batik.svggen.font.table.LookupSubtableFactory;
import org.apache.batik.svggen.font.table.ScriptList;
import org.apache.batik.svggen.font.table.SingleSubst;
import org.apache.batik.svggen.font.table.Table;

public class GsubTable
implements Table,
LookupSubtableFactory {
    private ScriptList scriptList;
    private FeatureList featureList;
    private LookupList lookupList;

    protected GsubTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        raf.readInt();
        int scriptListOffset = raf.readUnsignedShort();
        int featureListOffset = raf.readUnsignedShort();
        int lookupListOffset = raf.readUnsignedShort();
        this.scriptList = new ScriptList(raf, de.getOffset() + scriptListOffset);
        this.featureList = new FeatureList(raf, de.getOffset() + featureListOffset);
        this.lookupList = new LookupList(raf, de.getOffset() + lookupListOffset, this);
    }

    @Override
    public LookupSubtable read(int type, RandomAccessFile raf, int offset) throws IOException {
        LookupSubtable s = null;
        switch (type) {
            case 1: {
                s = SingleSubst.read(raf, offset);
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                s = LigatureSubst.read(raf, offset);
                break;
            }
            case 5: {
                break;
            }
        }
        return s;
    }

    @Override
    public int getType() {
        return 1196643650;
    }

    public ScriptList getScriptList() {
        return this.scriptList;
    }

    public FeatureList getFeatureList() {
        return this.featureList;
    }

    public LookupList getLookupList() {
        return this.lookupList;
    }

    public String toString() {
        return "GSUB";
    }
}

