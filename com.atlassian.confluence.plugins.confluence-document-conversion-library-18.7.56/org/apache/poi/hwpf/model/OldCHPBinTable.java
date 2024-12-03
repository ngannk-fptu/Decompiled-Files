/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.CHPBinTable;
import org.apache.poi.hwpf.model.CHPFormattedDiskPage;
import org.apache.poi.hwpf.model.CHPX;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.OldTextPieceTable;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class OldCHPBinTable
extends CHPBinTable {
    public OldCHPBinTable(byte[] documentStream, int offset, int size, int fcMin, OldTextPieceTable tpt) {
        PlexOfCps binTable = new PlexOfCps(documentStream, offset, size, 2);
        int length = binTable.length();
        for (int x = 0; x < length; ++x) {
            GenericPropertyNode node = binTable.getProperty(x);
            int pageNum = LittleEndian.getUShort(node.getBytes());
            int pageOffset = 512 * pageNum;
            CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage(documentStream, pageOffset, tpt);
            for (CHPX chpx : cfkp.getCHPXs()) {
                if (chpx == null) continue;
                this._textRuns.add(chpx);
            }
        }
        this._textRuns.sort(PropertyNode.StartComparator);
    }
}

