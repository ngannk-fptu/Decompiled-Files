/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color.profile;

import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ProfileRGB;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class ColorProfileUtil {
    private ColorProfileUtil() {
    }

    public static String getICCProfileDescription(ICC_Profile profile) {
        byte[] data = profile.getData(1684370275);
        if (data == null) {
            return null;
        }
        int length = data[8] << 24 | data[9] << 16 | data[10] << 8 | data[11];
        --length;
        try {
            return new String(data, 12, length, "US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Incompatible VM");
        }
    }

    public static boolean isDefaultsRGB(ICC_Profile profile) {
        if (!(profile instanceof ICC_ProfileRGB)) {
            return false;
        }
        ICC_Profile sRGBProfile = ICC_Profile.getInstance(1000);
        if (profile.getProfileClass() != sRGBProfile.getProfileClass()) {
            return false;
        }
        if (profile.getMajorVersion() != sRGBProfile.getMajorVersion()) {
            return false;
        }
        if (profile.getMinorVersion() != sRGBProfile.getMinorVersion()) {
            return false;
        }
        return Arrays.equals(profile.getData(), sRGBProfile.getData());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ICC_Profile getICC_Profile(byte[] data) {
        Class<ICC_Profile> clazz = ICC_Profile.class;
        synchronized (ICC_Profile.class) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return ICC_Profile.getInstance(data);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ICC_Profile getICC_Profile(int colorSpace) {
        Class<ICC_Profile> clazz = ICC_Profile.class;
        synchronized (ICC_Profile.class) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return ICC_Profile.getInstance(colorSpace);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ICC_Profile getICC_Profile(InputStream in) throws IOException {
        Class<ICC_Profile> clazz = ICC_Profile.class;
        synchronized (ICC_Profile.class) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return ICC_Profile.getInstance(in);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ICC_Profile getICC_Profile(String fileName) throws IOException {
        Class<ICC_Profile> clazz = ICC_Profile.class;
        synchronized (ICC_Profile.class) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return ICC_Profile.getInstance(fileName);
        }
    }
}

