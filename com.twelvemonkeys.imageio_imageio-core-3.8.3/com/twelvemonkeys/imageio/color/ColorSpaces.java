/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 *  com.twelvemonkeys.util.LRUHashMap
 */
package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.imageio.color.CMYKColorSpace;
import com.twelvemonkeys.imageio.color.ColorProfiles;
import com.twelvemonkeys.imageio.color.ProfileDeferralActivator;
import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.LRUHashMap;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;

public final class ColorSpaces {
    static final boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.color.debug"));
    public static final int CS_ADOBE_RGB_1998 = 5000;
    public static final int CS_GENERIC_CMYK = 5001;
    private static WeakReference<ICC_Profile> adobeRGB1998 = new WeakReference<Object>(null);
    private static WeakReference<ICC_Profile> genericCMYK = new WeakReference<Object>(null);
    private static final Map<Key, ICC_ColorSpace> cache = new LRUHashMap(16);

    private ColorSpaces() {
    }

    public static ICC_ColorSpace createColorSpace(ICC_Profile iCC_Profile) {
        Validate.notNull((Object)iCC_Profile, (String)"profile");
        ColorProfiles.fixProfile(iCC_Profile);
        byte[] byArray = ColorProfiles.getProfileHeaderWithProfileId(iCC_Profile);
        ICC_ColorSpace iCC_ColorSpace = ColorSpaces.getInternalCS(iCC_Profile.getColorSpaceType(), byArray);
        if (iCC_ColorSpace != null) {
            return iCC_ColorSpace;
        }
        return ColorSpaces.getCachedOrCreateCS(iCC_Profile, byArray);
    }

    static ICC_ColorSpace getInternalCS(int n, byte[] byArray) {
        if (n == 5 && Arrays.equals(byArray, ColorProfiles.sRGB.header)) {
            return (ICC_ColorSpace)ColorSpace.getInstance(1000);
        }
        if (n == 6 && Arrays.equals(byArray, ColorProfiles.GRAY.header)) {
            return (ICC_ColorSpace)ColorSpace.getInstance(1003);
        }
        if (n == 13 && Arrays.equals(byArray, ColorProfiles.PYCC.header)) {
            return (ICC_ColorSpace)ColorSpace.getInstance(1002);
        }
        if (n == 5 && Arrays.equals(byArray, ColorProfiles.LINEAR_RGB.header)) {
            return (ICC_ColorSpace)ColorSpace.getInstance(1004);
        }
        if (n == 0 && Arrays.equals(byArray, ColorProfiles.CIEXYZ.header)) {
            return (ICC_ColorSpace)ColorSpace.getInstance(1001);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static ICC_ColorSpace getCachedOrCreateCS(ICC_Profile iCC_Profile, byte[] byArray) {
        Key key = new Key(byArray);
        Map<Key, ICC_ColorSpace> map = cache;
        synchronized (map) {
            ICC_ColorSpace iCC_ColorSpace = ColorSpaces.getCachedCS(key);
            if (iCC_ColorSpace == null) {
                iCC_ColorSpace = new ICC_ColorSpace(iCC_Profile);
                ColorSpaces.validateColorSpace(iCC_ColorSpace);
                cache.put(key, iCC_ColorSpace);
                if (ColorProfiles.validationAltersProfileHeader()) {
                    cache.put(new Key(ColorProfiles.getProfileHeaderWithProfileId(iCC_ColorSpace.getProfile())), iCC_ColorSpace);
                }
            }
            return iCC_ColorSpace;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static ICC_ColorSpace getCachedCS(Key key) {
        Map<Key, ICC_ColorSpace> map = cache;
        synchronized (map) {
            return cache.get(key);
        }
    }

    static ICC_ColorSpace getCachedCS(byte[] byArray) {
        return ColorSpaces.getCachedCS(new Key(byArray));
    }

    static void validateColorSpace(ICC_ColorSpace iCC_ColorSpace) {
        iCC_ColorSpace.fromRGB(new float[]{0.999f, 0.5f, 0.001f});
        iCC_ColorSpace.getProfile().getData();
    }

    @Deprecated
    public static boolean isCS_sRGB(ICC_Profile iCC_Profile) {
        return ColorProfiles.isCS_sRGB(iCC_Profile);
    }

    @Deprecated
    public static boolean isCS_GRAY(ICC_Profile iCC_Profile) {
        return ColorProfiles.isCS_GRAY(iCC_Profile);
    }

    @Deprecated
    public static ICC_Profile validateProfile(ICC_Profile iCC_Profile) {
        return ColorProfiles.validateProfile(iCC_Profile);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ColorSpace getColorSpace(int n) {
        switch (n) {
            case 5000: {
                Class<ColorSpaces> clazz = ColorSpaces.class;
                synchronized (ColorSpaces.class) {
                    ICC_Profile iCC_Profile = (ICC_Profile)adobeRGB1998.get();
                    if (iCC_Profile == null) {
                        iCC_Profile = ColorProfiles.readProfileFromPath(ColorProfiles.Profiles.getPath("ADOBE_RGB_1998"));
                        if (iCC_Profile == null && (iCC_Profile = ColorProfiles.readProfileFromClasspathResource("/profiles/ClayRGB1998.icc")) == null) {
                            throw new IllegalStateException("Could not read AdobeRGB1998 profile");
                        }
                        if (iCC_Profile.getColorSpaceType() != 5) {
                            throw new IllegalStateException("Configured AdobeRGB1998 profile is not TYPE_RGB");
                        }
                        adobeRGB1998 = new WeakReference<ICC_Profile>(iCC_Profile);
                    }
                    // ** MonitorExit[var2_1] (shouldn't be in output)
                    return ColorSpaces.createColorSpace(iCC_Profile);
                }
            }
            case 5001: {
                Class<ColorSpaces> clazz = ColorSpaces.class;
                synchronized (ColorSpaces.class) {
                    ICC_Profile iCC_Profile = (ICC_Profile)genericCMYK.get();
                    if (iCC_Profile == null) {
                        iCC_Profile = ColorProfiles.readProfileFromPath(ColorProfiles.Profiles.getPath("GENERIC_CMYK"));
                        if (iCC_Profile == null) {
                            if (DEBUG) {
                                System.out.println("Using fallback profile");
                            }
                            // ** MonitorExit[var2_2] (shouldn't be in output)
                            return CMYKColorSpace.getInstance();
                        }
                        if (iCC_Profile.getColorSpaceType() != 9) {
                            throw new IllegalStateException("Configured Generic CMYK profile is not TYPE_CMYK");
                        }
                        genericCMYK = new WeakReference<ICC_Profile>(iCC_Profile);
                    }
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return ColorSpaces.createColorSpace(iCC_Profile);
                }
            }
        }
        return ColorSpace.getInstance(n);
    }

    static {
        ProfileDeferralActivator.activateProfiles();
    }

    private static final class Key {
        private final byte[] data;

        Key(byte[] byArray) {
            this.data = byArray;
        }

        public boolean equals(Object object) {
            return object instanceof Key && Arrays.equals(this.data, ((Key)object).data);
        }

        public int hashCode() {
            return Arrays.hashCode(this.data);
        }

        public String toString() {
            return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
        }
    }
}

