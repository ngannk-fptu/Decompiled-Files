/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.hwpf.sprm.SprmUtils;
import org.apache.poi.hwpf.usermodel.TableCellDescriptor;
import org.apache.poi.hwpf.usermodel.TableProperties;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class TableSprmCompressor {
    public static byte[] compressTableProperty(TableProperties newTAP) {
        int size = 0;
        ArrayList<byte[]> sprmList = new ArrayList<byte[]>();
        if (newTAP.getJc() != 0) {
            size += SprmUtils.addSprm((short)21504, newTAP.getJc(), null, sprmList);
        }
        if (newTAP.getFCantSplit()) {
            size += SprmUtils.addSprm((short)13315, 1, null, sprmList);
        }
        if (newTAP.getFTableHeader()) {
            size += SprmUtils.addSprm((short)13316, 1, null, sprmList);
        }
        byte[] brcBuf = new byte[24];
        int offset = 0;
        newTAP.getBrcTop().serialize(brcBuf, offset);
        newTAP.getBrcLeft().serialize(brcBuf, offset += 4);
        newTAP.getBrcBottom().serialize(brcBuf, offset += 4);
        newTAP.getBrcRight().serialize(brcBuf, offset += 4);
        newTAP.getBrcHorizontal().serialize(brcBuf, offset += 4);
        newTAP.getBrcVertical().serialize(brcBuf, offset += 4);
        byte[] compare = new byte[24];
        if (!Arrays.equals(brcBuf, compare)) {
            size += SprmUtils.addSprm((short)-10747, 0, brcBuf, sprmList);
        }
        if (newTAP.getDyaRowHeight() != 0) {
            size += SprmUtils.addSprm((short)-27641, newTAP.getDyaRowHeight(), null, sprmList);
        }
        if (newTAP.getItcMac() > 0) {
            short itcMac = newTAP.getItcMac();
            byte[] buf = IOUtils.safelyAllocate(1L + 2L * ((long)itcMac + 1L) + 20L * (long)itcMac, SprmUtils.MAX_RECORD_LENGTH);
            buf[0] = (byte)itcMac;
            short[] dxaCenters = newTAP.getRgdxaCenter();
            for (int x = 0; x < dxaCenters.length; ++x) {
                LittleEndian.putShort(buf, 1 + x * 2, dxaCenters[x]);
            }
            TableCellDescriptor[] cellDescriptors = newTAP.getRgtc();
            for (int x = 0; x < cellDescriptors.length; ++x) {
                cellDescriptors[x].serialize(buf, 1 + (itcMac + 1) * 2 + x * 20);
            }
            size += SprmUtils.addSpecialSprm((short)-10744, buf, sprmList);
        }
        if (newTAP.getTlp() != null && !newTAP.getTlp().isEmpty()) {
            byte[] buf = new byte[4];
            newTAP.getTlp().serialize(buf, 0);
            size += SprmUtils.addSprm((short)29706, 0, buf, sprmList);
        }
        return SprmUtils.getGrpprl(sprmList, size);
    }
}

