/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.model.SEPX;
import org.apache.poi.hwpf.model.SectionDescriptor;
import org.apache.poi.hwpf.model.SectionTable;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class OldSectionTable
extends SectionTable {
    @Deprecated
    public OldSectionTable(byte[] documentStream, int offset, int size, int fcMin, TextPieceTable tpt) {
        this(documentStream, offset, size);
    }

    public OldSectionTable(byte[] documentStream, int offset, int size) {
        PlexOfCps sedPlex = new PlexOfCps(documentStream, offset, size, 12);
        int length = sedPlex.length();
        for (int x = 0; x < length; ++x) {
            SEPX sepx;
            GenericPropertyNode node = sedPlex.getProperty(x);
            SectionDescriptor sed = new SectionDescriptor(node.getBytes(), 0);
            int fileOffset = sed.getFc();
            int startAt = node.getStart();
            int endAt = node.getEnd();
            if (fileOffset == -1) {
                sepx = new SEPX(sed, startAt, endAt, new byte[0]);
            } else {
                short sepxSize = LittleEndian.getShort(documentStream, fileOffset);
                byte[] buf = IOUtils.safelyClone(documentStream, fileOffset += 2, sepxSize + 2, 32769);
                sepx = new SEPX(sed, startAt, endAt, buf);
            }
            this._sections.add(sepx);
        }
        this._sections.sort(PropertyNode.StartComparator);
    }
}

