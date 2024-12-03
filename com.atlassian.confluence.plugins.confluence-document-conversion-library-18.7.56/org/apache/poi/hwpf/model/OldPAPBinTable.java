/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PAPBinTable;
import org.apache.poi.hwpf.model.PAPFormattedDiskPage;
import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class OldPAPBinTable
extends PAPBinTable {
    public OldPAPBinTable(byte[] documentStream, int offset, int size, int fcMin, TextPieceTable tpt) {
        PlexOfCps binTable = new PlexOfCps(documentStream, offset, size, 2);
        int length = binTable.length();
        for (int x = 0; x < length; ++x) {
            GenericPropertyNode node = binTable.getProperty(x);
            int pageNum = LittleEndian.getUShort(node.getBytes());
            int pageOffset = 512 * pageNum;
            PAPFormattedDiskPage pfkp = new PAPFormattedDiskPage(documentStream, documentStream, pageOffset, tpt);
            for (PAPX papx : pfkp.getPAPXs()) {
                if (papx == null) continue;
                this._paragraphs.add(papx);
            }
        }
    }
}

