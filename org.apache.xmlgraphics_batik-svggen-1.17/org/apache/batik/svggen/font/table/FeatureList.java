/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.Feature;
import org.apache.batik.svggen.font.table.FeatureRecord;
import org.apache.batik.svggen.font.table.LangSys;

public class FeatureList {
    private int featureCount;
    private FeatureRecord[] featureRecords;
    private Feature[] features;

    public FeatureList(RandomAccessFile raf, int offset) throws IOException {
        int i;
        raf.seek(offset);
        this.featureCount = raf.readUnsignedShort();
        this.featureRecords = new FeatureRecord[this.featureCount];
        this.features = new Feature[this.featureCount];
        for (i = 0; i < this.featureCount; ++i) {
            this.featureRecords[i] = new FeatureRecord(raf);
        }
        for (i = 0; i < this.featureCount; ++i) {
            this.features[i] = new Feature(raf, offset + this.featureRecords[i].getOffset());
        }
    }

    public Feature findFeature(LangSys langSys, String tag) {
        if (tag.length() != 4) {
            return null;
        }
        int tagVal = tag.charAt(0) << 24 | tag.charAt(1) << 16 | tag.charAt(2) << 8 | tag.charAt(3);
        for (int i = 0; i < this.featureCount; ++i) {
            if (this.featureRecords[i].getTag() != tagVal || !langSys.isFeatureIndexed(i)) continue;
            return this.features[i];
        }
        return null;
    }
}

