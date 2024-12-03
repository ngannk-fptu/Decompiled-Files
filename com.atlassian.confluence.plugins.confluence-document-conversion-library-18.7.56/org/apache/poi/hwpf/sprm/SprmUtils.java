/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.List;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Removal;

@Internal
public final class SprmUtils {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000;
    static int MAX_RECORD_LENGTH = 100000;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    @Removal(version="6.0.0")
    public static byte[] shortArrayToByteArray(short[] convert) {
        byte[] buf = IOUtils.safelyAllocate((long)convert.length * 2L, MAX_RECORD_LENGTH);
        for (int x = 0; x < convert.length; ++x) {
            LittleEndian.putShort(buf, x * 2, convert[x]);
        }
        return buf;
    }

    public static int addSpecialSprm(short instruction, byte[] varParam, List<byte[]> list) {
        byte[] sprm = new byte[varParam.length + 4];
        System.arraycopy(varParam, 0, sprm, 4, varParam.length);
        LittleEndian.putShort(sprm, 0, instruction);
        LittleEndian.putShort(sprm, 2, (short)(varParam.length + 1));
        list.add(sprm);
        return sprm.length;
    }

    public static int addSprm(short instruction, boolean param, List<byte[]> list) {
        return SprmUtils.addSprm(instruction, param ? 1 : 0, null, list);
    }

    public static int addSprm(short instruction, int param, byte[] varParam, List<byte[]> list) {
        int type = (instruction & 0xE000) >> 13;
        byte[] sprm = null;
        switch (type) {
            case 0: 
            case 1: {
                sprm = new byte[3];
                sprm[2] = (byte)param;
                break;
            }
            case 2: {
                sprm = new byte[4];
                LittleEndian.putShort(sprm, 2, (short)param);
                break;
            }
            case 3: {
                sprm = new byte[6];
                LittleEndian.putInt(sprm, 2, param);
                break;
            }
            case 4: 
            case 5: {
                sprm = new byte[4];
                LittleEndian.putShort(sprm, 2, (short)param);
                break;
            }
            case 6: {
                assert (varParam != null);
                sprm = new byte[3 + varParam.length];
                sprm[2] = (byte)varParam.length;
                System.arraycopy(varParam, 0, sprm, 3, varParam.length);
                break;
            }
            case 7: {
                sprm = new byte[5];
                byte[] temp = new byte[4];
                LittleEndian.putInt(temp, 0, param);
                System.arraycopy(temp, 0, sprm, 2, 3);
                break;
            }
            default: {
                throw new RuntimeException("Invalid sprm type");
            }
        }
        LittleEndian.putShort(sprm, 0, instruction);
        list.add(sprm);
        return sprm.length;
    }

    public static byte[] getGrpprl(List<byte[]> sprmList, int size) {
        byte[] grpprl = IOUtils.safelyAllocate(size, MAX_RECORD_LENGTH);
        int index = 0;
        for (int listSize = sprmList.size() - 1; listSize >= 0; --listSize) {
            byte[] sprm = sprmList.remove(0);
            System.arraycopy(sprm, 0, grpprl, index, sprm.length);
            index += sprm.length;
        }
        return grpprl;
    }

    public static int convertBrcToInt(short[] brc) {
        byte[] buf = new byte[4];
        LittleEndian.putShort(buf, 0, brc[0]);
        LittleEndian.putShort(buf, 2, brc[1]);
        return LittleEndian.getInt(buf);
    }
}

