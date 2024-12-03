/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.common.log.Logger
 *  com.atlassian.extras.common.log.Logger$Log
 */
package com.atlassian.license;

import com.atlassian.extras.common.log.Logger;
import com.atlassian.license.License;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Deprecated
public class LicenseUtils {
    private static Logger.Log log = Logger.getInstance(LicenseUtils.class);
    public static final long POST_LICENSE_EVAL_PERIOD = 2592000000L;
    public static final long UPDATE_ALLOWED_PERIOD = 31622400000L;
    public static final long ALMOST_EXPIRED_PERIOD = 3628800000L;
    public static final String PARTNER_NOT_MATCHING_BUILD = "partner not matching build partner name";
    public static final String LICENSE_NO_PARTNER = "License does not contain a partner name";

    private LicenseUtils() {
    }

    public static String getString(byte[] byteArray) {
        char[] charByte = new char[byteArray.length * 2];
        for (int i = 0; i < byteArray.length; ++i) {
            byte aByte = byteArray[i];
            if (-128 <= aByte && aByte < -64) {
                charByte[i] = LicenseUtils.rndChar(0);
                charByte[byteArray.length + i] = LicenseUtils.getCharInRange(aByte + 128);
                continue;
            }
            if (-64 <= aByte && aByte < 0) {
                charByte[i] = LicenseUtils.rndChar(1);
                charByte[byteArray.length + i] = LicenseUtils.getCharInRange(aByte + 64);
                continue;
            }
            if (0 <= aByte && aByte < 64) {
                charByte[i] = LicenseUtils.rndChar(2);
                charByte[byteArray.length + i] = LicenseUtils.getCharInRange(aByte);
                continue;
            }
            if (64 <= aByte && aByte < 128) {
                charByte[i] = LicenseUtils.rndChar(3);
                charByte[byteArray.length + i] = LicenseUtils.getCharInRange(aByte - 64);
                continue;
            }
            log.debug((Object)("Invalid Char in stream " + aByte));
        }
        String str = new String(charByte);
        return str;
    }

    private static char rndChar(int i) {
        int c = i * 6 + (int)(Math.random() * 6.0);
        boolean u = (int)(Math.random() * 2.0) < 1;
        return (char)(c + (u ? 97 : 65));
    }

    private static char getCharInRange(int c1) {
        if (0 <= c1 && c1 <= 9) {
            return (char)(c1 + 48);
        }
        if (10 <= c1 && c1 <= 35) {
            return (char)(c1 - 10 + 65);
        }
        if (36 <= c1 && c1 <= 61) {
            return (char)(c1 - 36 + 97);
        }
        if (c1 == 62) {
            return '<';
        }
        if (c1 == 63) {
            return '>';
        }
        log.debug((Object)("Invalid int in stream " + c1));
        return '\u0000';
    }

    private static byte getByteInRange(char c1) {
        if ('0' <= c1 && c1 <= '9') {
            return (byte)(c1 - 48);
        }
        if ('A' <= c1 && c1 <= 'Z') {
            return (byte)(c1 - 65 + 10);
        }
        if ('a' <= c1 && c1 <= 'z') {
            return (byte)(c1 - 97 + 36);
        }
        if (c1 == '<') {
            return 62;
        }
        if (c1 == '>') {
            return 63;
        }
        log.debug((Object)("Incorrect character in stream " + c1));
        return 2;
    }

    public static byte[] getBytes(String string) {
        char[] charArray = string.toCharArray();
        byte[] bytes = new byte[charArray.length / 2];
        for (int i = 0; i < bytes.length; ++i) {
            if (Character.toLowerCase(charArray[i]) < 'g') {
                bytes[i] = (byte)(LicenseUtils.getByteInRange(charArray[bytes.length + i]) - 128);
                continue;
            }
            if (Character.toLowerCase(charArray[i]) < 'm') {
                bytes[i] = (byte)(LicenseUtils.getByteInRange(charArray[bytes.length + i]) - 64);
                continue;
            }
            if (Character.toLowerCase(charArray[i]) < 's') {
                bytes[i] = LicenseUtils.getByteInRange(charArray[bytes.length + i]);
                continue;
            }
            if (Character.toLowerCase(charArray[i]) < 'y') {
                bytes[i] = (byte)(LicenseUtils.getByteInRange(charArray[bytes.length + i]) + 64);
                continue;
            }
            log.debug((Object)("Invalid character in byte stream " + charArray[i]));
        }
        return bytes;
    }

    public static long getSupportPeriodEnd(License license) {
        return license.getDateCreated().getTime() + 31622400000L;
    }

    public static boolean isLicenseTooOldForBuild(License license, Date buildDate) {
        return LicenseUtils.getSupportPeriodEnd(license) < buildDate.getTime();
    }

    public static boolean confirmExtendLicenseExpired(Date dateConfirmed) {
        return new Date().getTime() > LicenseUtils.getNewBuildWithOldLicenseExpiryDate(dateConfirmed);
    }

    public static boolean confirmExtendLicenseExpired(String dateConfirmed) throws NumberFormatException {
        return LicenseUtils.confirmExtendLicenseExpired(new Date(Long.parseLong(dateConfirmed)));
    }

    private static long getNewBuildWithOldLicenseExpiryDate(Date dateConfirmed) {
        return dateConfirmed.getTime() + 2592000000L;
    }

    public static long getNewBuildWithOldLicenseExpiryDate(String dateConfirmed) {
        return LicenseUtils.getNewBuildWithOldLicenseExpiryDate(new Date(Long.parseLong(dateConfirmed)));
    }

    public static long getSupportPeriodAlmostExpiredDate(License license) {
        return LicenseUtils.getSupportPeriodEnd(license) - 3628800000L;
    }

    public static String isPartnerDetailsValid(License license, String buildPartnerName) {
        String licensePartnerName = license.getPartnerName();
        if (licensePartnerName != null && !licensePartnerName.equals("") && !licensePartnerName.equals(buildPartnerName)) {
            return PARTNER_NOT_MATCHING_BUILD;
        }
        if ((licensePartnerName == null || licensePartnerName.equals("")) && buildPartnerName != null && !buildPartnerName.equals("")) {
            return LICENSE_NO_PARTNER;
        }
        return "";
    }

    public static byte[] readKey(InputStream is) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int len = 0;
        byte[] bytes = new byte[512];
        while ((len = is.read(bytes)) > -1) {
            bout.write(bytes, 0, len);
        }
        return bout.toByteArray();
    }
}

