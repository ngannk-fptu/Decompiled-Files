/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.io.FileUtil
 *  com.twelvemonkeys.lang.Platform
 *  com.twelvemonkeys.lang.SystemUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.imageio.color.ColorSpaces;
import com.twelvemonkeys.imageio.color.ICCProfileSanitizer;
import com.twelvemonkeys.imageio.color.ProfileDeferralActivator;
import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.lang.Platform;
import com.twelvemonkeys.lang.SystemUtil;
import com.twelvemonkeys.lang.Validate;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public final class ColorProfiles {
    private static final ICCProfileSanitizer profileCleaner = ICCProfileSanitizer.Factory.get();
    static final int ICC_PROFILE_MAGIC = 1633907568;
    static final int ICC_PROFILE_HEADER_SIZE = 128;

    private ColorProfiles() {
    }

    static byte[] getProfileHeaderWithProfileId(ICC_Profile iCC_Profile) {
        return ColorProfiles.getProfileHeaderWithProfileId(iCC_Profile.getData());
    }

    static byte[] getProfileHeaderWithProfileId(byte[] byArray) {
        byte[] byArray2 = Arrays.copyOf(byArray, 128);
        Arrays.fill(byArray2, 4, 8, (byte)0);
        Arrays.fill(byArray2, 40, 44, (byte)0);
        Arrays.fill(byArray2, 64, 68, (byte)0);
        Arrays.fill(byArray2, 80, 84, (byte)0);
        Arrays.fill(byArray2, 84, 100, (byte)0);
        byte[] byArray3 = ColorProfiles.computeMD5(byArray2, byArray);
        System.arraycopy(byArray3, 0, byArray2, 84, byArray3.length);
        return byArray2;
    }

    private static byte[] computeMD5(byte[] byArray, byte[] byArray2) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(byArray, 0, 128);
            messageDigest.update(byArray2, 128, byArray2.length - 128);
            return messageDigest.digest();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new IllegalStateException("Missing MD5 MessageDigest");
        }
    }

    public static boolean isCS_sRGB(ICC_Profile iCC_Profile) {
        Validate.notNull((Object)iCC_Profile, (String)"profile");
        return iCC_Profile.getColorSpaceType() == 5 && Arrays.equals(ColorProfiles.getProfileHeaderWithProfileId(iCC_Profile), sRGB.header);
    }

    public static boolean isCS_GRAY(ICC_Profile iCC_Profile) {
        Validate.notNull((Object)iCC_Profile, (String)"profile");
        return iCC_Profile.getColorSpaceType() == 6 && Arrays.equals(ColorProfiles.getProfileHeaderWithProfileId(iCC_Profile), GRAY.header);
    }

    static boolean isOffendingColorProfile(ICC_Profile iCC_Profile) {
        Validate.notNull((Object)iCC_Profile, (String)"profile");
        byte[] byArray = iCC_Profile.getData(1751474532);
        return byArray[64] != 0 || byArray[65] != 0 || byArray[66] != 0 || byArray[67] > 3;
    }

    public static ICC_Profile validateProfile(ICC_Profile iCC_Profile) {
        profileCleaner.fixProfile(iCC_Profile);
        ColorSpaces.validateColorSpace(new ICC_ColorSpace(iCC_Profile));
        return iCC_Profile;
    }

    public static ICC_Profile readProfileRaw(InputStream inputStream) throws IOException {
        Validate.notNull((Object)inputStream, (String)"input");
        return ICC_Profile.getInstance(inputStream);
    }

    public static ICC_Profile readProfile(InputStream inputStream) throws IOException {
        Validate.notNull((Object)inputStream, (String)"input");
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] byArray = new byte[128];
        try {
            dataInputStream.readFully(byArray);
            int n = ColorProfiles.validateHeaderAndGetSize(byArray);
            byte[] byArray2 = Arrays.copyOf(byArray, n);
            dataInputStream.readFully(byArray2, byArray.length, n - byArray.length);
            return ColorProfiles.createProfile(byArray2);
        }
        catch (EOFException eOFException) {
            throw new IllegalArgumentException("Truncated ICC Profile data", eOFException);
        }
    }

    public static ICC_Profile createProfileRaw(byte[] byArray) {
        int n = ColorProfiles.validateHeaderAndGetSize(byArray);
        return ICC_Profile.getInstance(ColorProfiles.limit(byArray, n));
    }

    public static ICC_Profile createProfile(byte[] byArray) {
        int n = ColorProfiles.validateAndGetSize(byArray);
        byte[] byArray2 = ColorProfiles.getProfileHeaderWithProfileId(byArray);
        ICC_Profile iCC_Profile = ColorProfiles.getInternalProfile(byArray2);
        if (iCC_Profile != null) {
            return iCC_Profile;
        }
        ICC_ColorSpace iCC_ColorSpace = ColorSpaces.getCachedCS(byArray2);
        if (iCC_ColorSpace != null) {
            return iCC_ColorSpace.getProfile();
        }
        ICC_Profile iCC_Profile2 = ICC_Profile.getInstance(ColorProfiles.limit(byArray, n));
        return ColorSpaces.createColorSpace(iCC_Profile2).getProfile();
    }

    private static byte[] limit(byte[] byArray, int n) {
        return byArray.length == n ? byArray : Arrays.copyOf(byArray, n);
    }

    private static int validateAndGetSize(byte[] byArray) {
        int n = ColorProfiles.validateHeaderAndGetSize(byArray);
        if (n < 0 || n > byArray.length) {
            throw new IllegalArgumentException("Truncated ICC profile data, length < " + n + ": " + byArray.length);
        }
        return n;
    }

    private static int validateHeaderAndGetSize(byte[] byArray) {
        Validate.notNull((Object)byArray, (String)"input");
        if (byArray.length < 128) {
            throw new IllegalArgumentException("Truncated ICC profile data, length < 128: " + byArray.length);
        }
        int n = ColorProfiles.intBigEndian(byArray, 0);
        if (ColorProfiles.intBigEndian(byArray, 36) != 1633907568) {
            throw new IllegalArgumentException("Not an ICC profile, missing file signature");
        }
        return n;
    }

    private static ICC_Profile getInternalProfile(byte[] byArray) {
        int n = ColorProfiles.getCsType(byArray);
        if (n == 5 && Arrays.equals(byArray, sRGB.header)) {
            return ICC_Profile.getInstance(1000);
        }
        if (n == 6 && Arrays.equals(byArray, GRAY.header)) {
            return ICC_Profile.getInstance(1003);
        }
        if (n == 13 && Arrays.equals(byArray, PYCC.header)) {
            return ICC_Profile.getInstance(1002);
        }
        if (n == 5 && Arrays.equals(byArray, LINEAR_RGB.header)) {
            return ICC_Profile.getInstance(1004);
        }
        if (n == 0 && Arrays.equals(byArray, CIEXYZ.header)) {
            return ICC_Profile.getInstance(1001);
        }
        return null;
    }

    private static int intBigEndian(byte[] byArray, int n) {
        return (byArray[n] & 0xFF) << 24 | (byArray[n + 1] & 0xFF) << 16 | (byArray[n + 2] & 0xFF) << 8 | byArray[n + 3] & 0xFF;
    }

    private static int getCsType(byte[] byArray) {
        int n = ColorProfiles.intBigEndian(byArray, 16);
        switch (n) {
            case 1482250784: {
                return 0;
            }
            case 1281450528: {
                return 1;
            }
            case 1282766368: {
                return 2;
            }
            case 1497588338: {
                return 3;
            }
            case 1501067552: {
                return 4;
            }
            case 1380401696: {
                return 5;
            }
            case 1196573017: {
                return 6;
            }
            case 1213421088: {
                return 7;
            }
            case 1212961568: {
                return 8;
            }
            case 1129142603: {
                return 9;
            }
            case 1129142560: {
                return 11;
            }
            case 843271250: {
                return 12;
            }
            case 860048466: {
                return 13;
            }
            case 876825682: {
                return 14;
            }
            case 893602898: {
                return 15;
            }
            case 910380114: {
                return 16;
            }
            case 927157330: {
                return 17;
            }
            case 943934546: {
                return 18;
            }
            case 960711762: {
                return 19;
            }
            case 1094929490: {
                return 20;
            }
            case 1111706706: {
                return 21;
            }
            case 1128483922: {
                return 22;
            }
            case 1145261138: {
                return 23;
            }
            case 1162038354: {
                return 24;
            }
            case 1178815570: {
                return 25;
            }
        }
        throw new IllegalArgumentException("Invalid ICC color space signature: " + n);
    }

    static ICC_Profile readProfileFromClasspathResource(String string) {
        InputStream inputStream = ColorSpaces.class.getResourceAsStream(string);
        if (inputStream != null) {
            if (ColorSpaces.DEBUG) {
                System.out.println("Loading profile from classpath resource: " + string);
            }
            try {
                ICC_Profile iCC_Profile = ICC_Profile.getInstance(inputStream);
                return iCC_Profile;
            }
            catch (IOException iOException) {
                if (ColorSpaces.DEBUG) {
                    iOException.printStackTrace();
                }
            }
            finally {
                FileUtil.close((InputStream)inputStream);
            }
        }
        return null;
    }

    static ICC_Profile readProfileFromPath(String string) {
        block4: {
            if (string != null) {
                if (ColorSpaces.DEBUG) {
                    System.out.println("Loading profile from: " + string);
                }
                try {
                    return ICC_Profile.getInstance(string);
                }
                catch (IOException | SecurityException exception) {
                    if (!ColorSpaces.DEBUG) break block4;
                    exception.printStackTrace();
                }
            }
        }
        return null;
    }

    static void fixProfile(ICC_Profile iCC_Profile) {
        profileCleaner.fixProfile(iCC_Profile);
    }

    static boolean validationAltersProfileHeader() {
        return profileCleaner.validationAltersProfileHeader();
    }

    static {
        ProfileDeferralActivator.activateProfiles();
    }

    static class Profiles {
        private static final Properties PROFILES = Profiles.loadProfiles();

        Profiles() {
        }

        private static Properties loadProfiles() {
            Properties properties;
            try {
                properties = SystemUtil.loadProperties(ColorSpaces.class, (String)("com/twelvemonkeys/imageio/color/icc_profiles_" + Platform.os().id()));
            }
            catch (IOException | SecurityException exception) {
                System.err.printf("Warning: Could not load system default ICC profile locations from %s, will use bundled fallback profiles.\n", exception.getMessage());
                if (ColorSpaces.DEBUG) {
                    exception.printStackTrace();
                }
                properties = null;
            }
            Properties properties2 = new Properties(properties);
            try {
                Properties properties3 = SystemUtil.loadProperties(ColorSpaces.class, (String)"com/twelvemonkeys/imageio/color/icc_profiles");
                properties2.putAll((Map<?, ?>)properties3);
            }
            catch (IOException | SecurityException exception) {
                // empty catch block
            }
            if (ColorSpaces.DEBUG) {
                System.out.println("User ICC profiles: " + properties2);
                System.out.println("System ICC profiles : " + properties);
            }
            return properties2;
        }

        static String getPath(String string) {
            return PROFILES.getProperty(string);
        }
    }

    static class LINEAR_RGB {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1004));

        LINEAR_RGB() {
        }
    }

    static class GRAY {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1003));

        GRAY() {
        }
    }

    static class PYCC {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1002));

        PYCC() {
        }
    }

    static class CIEXYZ {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1001));

        CIEXYZ() {
        }
    }

    static class sRGB {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1000));

        sRGB() {
        }
    }
}

