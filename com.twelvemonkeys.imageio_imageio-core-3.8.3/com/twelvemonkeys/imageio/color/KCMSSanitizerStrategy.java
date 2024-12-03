/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.imageio.color.ICCProfileSanitizer;
import com.twelvemonkeys.lang.Validate;
import java.awt.color.ICC_Profile;

final class KCMSSanitizerStrategy
implements ICCProfileSanitizer {
    private static final int CORBIS_RGB_ALTERNATE_XYZ = 396690872;

    KCMSSanitizerStrategy() {
    }

    @Override
    public void fixProfile(ICC_Profile iCC_Profile) {
        Validate.notNull((Object)iCC_Profile, (String)"profile");
        byte[] byArray = iCC_Profile.getData(1751474532);
        if (KCMSSanitizerStrategy.intFromBigEndian(byArray, 64) != 0) {
            KCMSSanitizerStrategy.intToBigEndian(0, byArray, 64);
            iCC_Profile.setData(1751474532, byArray);
        }
        if (KCMSSanitizerStrategy.fixProfileXYZTag(iCC_Profile, 0x77747074)) {
            KCMSSanitizerStrategy.fixProfileXYZTag(iCC_Profile, 1918392666);
            KCMSSanitizerStrategy.fixProfileXYZTag(iCC_Profile, 1733843290);
            KCMSSanitizerStrategy.fixProfileXYZTag(iCC_Profile, 1649957210);
        }
    }

    @Override
    public boolean validationAltersProfileHeader() {
        return false;
    }

    private static boolean fixProfileXYZTag(ICC_Profile iCC_Profile, int n) {
        byte[] byArray = iCC_Profile.getData(n);
        if (byArray != null && KCMSSanitizerStrategy.intFromBigEndian(byArray, 0) == 396690872) {
            KCMSSanitizerStrategy.intToBigEndian(1482250784, byArray, 0);
            iCC_Profile.setData(n, byArray);
            return true;
        }
        return false;
    }

    private static int intFromBigEndian(byte[] byArray, int n) {
        return (byArray[n] & 0xFF) << 24 | (byArray[n + 1] & 0xFF) << 16 | (byArray[n + 2] & 0xFF) << 8 | byArray[n + 3] & 0xFF;
    }

    private static void intToBigEndian(int n, byte[] byArray, int n2) {
        byArray[n2] = (byte)(n >> 24);
        byArray[n2 + 1] = (byte)(n >> 16);
        byArray[n2 + 2] = (byte)(n >> 8);
        byArray[n2 + 3] = (byte)n;
    }
}

