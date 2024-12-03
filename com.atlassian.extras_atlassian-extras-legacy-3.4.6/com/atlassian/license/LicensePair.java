/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.decoder.v2.Version2LicenseDecoder
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.license;

import com.atlassian.extras.decoder.v1.Version1LicenseDecoder;
import com.atlassian.extras.decoder.v2.Version2LicenseDecoder;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicenseUtils;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;

@Deprecated
public class LicensePair {
    public static final byte[] NEW_LICENSE_PREFIX = new byte[]{13, 14, 12, 10, 15};
    private final byte[] license;
    private final byte[] hash;
    private final String originalLicenseString;
    private final boolean isNG;

    public LicensePair(byte[] license, byte[] hash) throws LicenseException {
        this.license = license;
        this.hash = hash;
        this.isNG = this.startsWith(this.license, NEW_LICENSE_PREFIX);
        this.originalLicenseString = this.isNG ? Version2LicenseDecoder.packLicense((byte[])this.license, (byte[])this.hash) : this.packV1License(this.license, this.hash);
    }

    public LicensePair(byte[] text, byte[] hash, String originalString) {
        this.license = text;
        this.hash = hash;
        this.isNG = this.startsWith(this.license, NEW_LICENSE_PREFIX);
        this.originalLicenseString = originalString;
    }

    private boolean startsWith(byte[] target, byte[] prefix) {
        if (prefix.length > target.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; ++i) {
            if (target[i] == prefix[i]) continue;
            return false;
        }
        return true;
    }

    public LicensePair(String license, String hash) throws LicenseException {
        if (license == null || hash == null) {
            throw new LicenseException("License string or hash are null.");
        }
        try {
            this.license = LicenseUtils.getBytes(license);
            this.hash = LicenseUtils.getBytes(hash);
            this.isNG = this.startsWith(this.license, NEW_LICENSE_PREFIX);
            this.originalLicenseString = this.isNG ? Version2LicenseDecoder.packLicense((byte[])this.license, (byte[])this.hash) : this.packV1License(this.license, this.hash);
        }
        catch (Exception e) {
            throw new LicenseException("Exception generating license: " + e);
        }
    }

    public LicensePair(String concatLicense) throws LicenseException {
        if (concatLicense == null) {
            throw new LicenseException("contactLicense was null");
        }
        LicensePair pair = this.splitVersion2License(concatLicense);
        if (pair == null) {
            pair = Version1LicenseDecoder.splitLicense(concatLicense);
        }
        this.originalLicenseString = pair.originalLicenseString;
        this.hash = pair.hash;
        this.license = pair.license;
        this.isNG = pair.isNG;
    }

    private String packV1License(byte[] license, byte[] hash) {
        StringBuffer sb = new StringBuffer();
        String hashString = LicenseUtils.getString(hash);
        int lineLength = hashString.length() / 2;
        while (hashString.length() > lineLength) {
            sb.append(hashString.substring(0, lineLength));
            sb.append("\n");
            hashString = hashString.substring(lineLength);
        }
        sb.append(hashString);
        sb.append("\n");
        String licenseStr = LicenseUtils.getString(license);
        while (licenseStr.length() > lineLength) {
            sb.append(licenseStr.substring(0, lineLength));
            sb.append("\n");
            licenseStr = licenseStr.substring(lineLength);
        }
        sb.append(licenseStr);
        sb.append("\n");
        return sb.toString();
    }

    private LicensePair splitVersion2License(String encodedLicense) throws LicenseException {
        if (!new Version2LicenseDecoder().canDecode(encodedLicense)) {
            return null;
        }
        int pos = encodedLicense.lastIndexOf(88);
        try {
            String licenseContent = encodedLicense.substring(0, pos);
            byte[] decodedBytes = Base64.decodeBase64((byte[])licenseContent.getBytes(StandardCharsets.UTF_8));
            ByteArrayInputStream in = new ByteArrayInputStream(decodedBytes);
            DataInputStream dIn = new DataInputStream(in);
            int textLength = dIn.readInt();
            byte[] licenseText = new byte[textLength];
            dIn.read(licenseText);
            byte[] hash = new byte[dIn.available()];
            dIn.read(hash);
            return new LicensePair(licenseText, hash, encodedLicense);
        }
        catch (IOException e) {
            throw new LicenseException(e);
        }
    }

    public boolean isNG() {
        return this.isNG;
    }

    public byte[] getLicense() {
        return this.license;
    }

    public String getLicenseString() {
        return LicenseUtils.getString(this.license);
    }

    public byte[] getHash() {
        return this.hash;
    }

    public String getHashString() {
        return LicenseUtils.getString(this.hash);
    }

    public String getOriginalLicenseString() {
        return this.originalLicenseString;
    }

    public String toString() {
        return this.originalLicenseString;
    }
}

