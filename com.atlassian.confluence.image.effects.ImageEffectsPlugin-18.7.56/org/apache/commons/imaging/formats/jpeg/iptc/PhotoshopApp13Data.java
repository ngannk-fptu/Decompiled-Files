/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.iptc;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcBlock;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;

public class PhotoshopApp13Data {
    private final List<IptcRecord> records;
    private final List<IptcBlock> rawBlocks;

    public PhotoshopApp13Data(List<IptcRecord> records, List<IptcBlock> rawBlocks) {
        this.rawBlocks = rawBlocks;
        this.records = records;
    }

    public List<IptcRecord> getRecords() {
        return new ArrayList<IptcRecord>(this.records);
    }

    public List<IptcBlock> getRawBlocks() {
        return new ArrayList<IptcBlock>(this.rawBlocks);
    }

    public List<IptcBlock> getNonIptcBlocks() {
        ArrayList<IptcBlock> result = new ArrayList<IptcBlock>();
        for (IptcBlock block : this.rawBlocks) {
            if (block.isIPTCBlock()) continue;
            result.add(block);
        }
        return result;
    }
}

