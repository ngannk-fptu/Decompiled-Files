/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.common.LicenseException
 *  com.atlassian.extras.common.org.springframework.util.DefaultPropertiesPersister
 *  com.atlassian.extras.decoder.api.AbstractLicenseDecoder
 *  com.atlassian.extras.decoder.api.LicenseVerificationException
 *  com.atlassian.extras.decoder.api.LicenseVerificationException$VerificationFailureReason
 *  com.atlassian.extras.keymanager.KeyManager
 *  com.atlassian.extras.keymanager.SortedProperties
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.extras.decoder.v2;

import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.common.org.springframework.util.DefaultPropertiesPersister;
import com.atlassian.extras.decoder.api.AbstractLicenseDecoder;
import com.atlassian.extras.decoder.api.LicenseVerificationException;
import com.atlassian.extras.keymanager.KeyManager;
import com.atlassian.extras.keymanager.SortedProperties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.codec.binary.Base64;

public class Version2LicenseDecoder
extends AbstractLicenseDecoder {
    public static final int VERSION_NUMBER_1 = 1;
    public static final int VERSION_NUMBER_2 = 2;
    public static final int VERSION_LENGTH = 3;
    public static final int ENCODED_LICENSE_LENGTH_BASE = 31;
    public static final byte[] LICENSE_PREFIX = new byte[]{13, 14, 12, 10, 15};
    public static final char SEPARATOR = 'X';
    private static final int ENCODED_LICENSE_LINE_LENGTH = 76;
    private static final Date LICENSE_HASH_CUTOFF_DATE = new Date(120, 12, 1);
    private volatile boolean verifyLicenseHash = false;
    private volatile boolean skipVerificationBeforeCutoffDate = false;

    public Version2LicenseDecoder() {
    }

    public Version2LicenseDecoder(boolean verifyLicenseHash, boolean skipVerificationBeforeCutoffDate) {
        this.verifyLicenseHash = verifyLicenseHash;
        this.skipVerificationBeforeCutoffDate = skipVerificationBeforeCutoffDate;
    }

    public boolean canDecode(String licenseString) {
        int pos = (licenseString = Version2LicenseDecoder.removeWhiteSpaces(licenseString)).lastIndexOf(88);
        if (pos == -1 || pos + 3 >= licenseString.length()) {
            return false;
        }
        try {
            int version = Integer.parseInt(licenseString.substring(pos + 1, pos + 3));
            if (version != 1 && version != 2) {
                return false;
            }
            String lengthStr = licenseString.substring(pos + 3);
            int encodedLicenseLength = Integer.valueOf(lengthStr, 31);
            return pos == encodedLicenseLength;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public Properties doDecode(String licenseString) throws LicenseVerificationException {
        String encodedLicenseTextAndHash = this.getLicenseContent(Version2LicenseDecoder.removeWhiteSpaces(licenseString));
        byte[] zippedLicenseBytes = this.checkAndGetLicenseText(encodedLicenseTextAndHash);
        Reader licenseText = this.unzipText(zippedLicenseBytes);
        Properties properties = this.loadLicenseConfiguration(licenseText);
        if (this.verifyLicenseHash) {
            this.verifyLicenseHash(properties);
        }
        return properties;
    }

    private void verifyLicenseHash(Properties properties) throws LicenseVerificationException {
        boolean verified;
        if (this.skipVerificationBeforeCutoffDate) {
            String creationDate = properties.getProperty("CreationDate");
            if (creationDate == null) {
                throw new LicenseVerificationException(LicenseVerificationException.VerificationFailureReason.MISSING_PROPERTY, "CreationDate", properties);
            }
            try {
                Date created = new SimpleDateFormat("yyyy-MM-dd").parse(creationDate);
                if (created.before(LICENSE_HASH_CUTOFF_DATE)) {
                    return;
                }
            }
            catch (Exception e) {
                throw new LicenseVerificationException(LicenseVerificationException.VerificationFailureReason.ERROR_DURING_VERIFICATION, properties, (Throwable)e);
            }
        }
        SortedProperties clonedProps = new SortedProperties();
        clonedProps.putAll((Map)properties);
        String licenseHash = (String)clonedProps.remove((Object)"licenseHash");
        if (licenseHash == null) {
            throw new LicenseVerificationException(LicenseVerificationException.VerificationFailureReason.MISSING_PROPERTY, "licenseHash", properties);
        }
        String keyVersion = clonedProps.getProperty("keyVersion");
        if (keyVersion == null) {
            throw new LicenseVerificationException(LicenseVerificationException.VerificationFailureReason.MISSING_PROPERTY, "keyVersion", properties);
        }
        try {
            StringWriter out = new StringWriter();
            new DefaultPropertiesPersister().store((Properties)clonedProps, (Writer)out, null, true);
            String encodedProps = new String(Base64.encodeBase64((byte[])out.toString().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            verified = KeyManager.getInstance().verify(encodedProps, licenseHash, keyVersion);
        }
        catch (Exception e) {
            throw new LicenseVerificationException(LicenseVerificationException.VerificationFailureReason.ERROR_DURING_VERIFICATION, properties, (Throwable)e);
        }
        if (!verified) {
            throw new LicenseVerificationException(LicenseVerificationException.VerificationFailureReason.VERIFICATION_FAILED, properties);
        }
    }

    protected int getLicenseVersion() {
        return 2;
    }

    private Reader unzipText(byte[] licenseText) {
        ByteArrayInputStream in = new ByteArrayInputStream(licenseText);
        in.skip(LICENSE_PREFIX.length);
        InflaterInputStream zipIn = new InflaterInputStream(in, new Inflater());
        return new InputStreamReader((InputStream)zipIn, StandardCharsets.UTF_8);
    }

    private String getLicenseContent(String licenseString) {
        String lengthStr = licenseString.substring(licenseString.lastIndexOf(88) + 3);
        try {
            int encodedLicenseLength = Integer.valueOf(lengthStr, 31);
            return licenseString.substring(0, encodedLicenseLength);
        }
        catch (NumberFormatException e) {
            throw new LicenseException("Could NOT decode license length <" + lengthStr + ">", (Throwable)e);
        }
    }

    private byte[] checkAndGetLicenseText(String licenseContent) {
        byte[] licenseText;
        try {
            byte[] decodedBytes = Base64.decodeBase64((byte[])licenseContent.getBytes(StandardCharsets.UTF_8));
            ByteArrayInputStream in = new ByteArrayInputStream(decodedBytes);
            DataInputStream dIn = new DataInputStream(in);
            int textLength = dIn.readInt();
            licenseText = new byte[textLength];
            dIn.read(licenseText);
            byte[] hash = new byte[dIn.available()];
            dIn.read(hash);
            String encodedLicenseText = new String(Base64.encodeBase64((byte[])licenseText), StandardCharsets.UTF_8);
            String encodedHash = new String(Base64.encodeBase64((byte[])hash), StandardCharsets.UTF_8);
            if (!KeyManager.getInstance().verify(encodedLicenseText, encodedHash, "LICENSE_STRING_KEY_V2")) {
                throw new LicenseException("Failed to verify the license.");
            }
        }
        catch (Exception e) {
            throw new LicenseException((Throwable)e);
        }
        return licenseText;
    }

    private Properties loadLicenseConfiguration(Reader text) {
        try {
            Properties props = new Properties();
            new DefaultPropertiesPersister().load(props, text);
            return props;
        }
        catch (IOException e) {
            throw new LicenseException("Could NOT load properties from reader", (Throwable)e);
        }
    }

    private static String removeWhiteSpaces(String licenseData) {
        if (licenseData == null || licenseData.length() == 0) {
            return licenseData;
        }
        char[] chars = licenseData.toCharArray();
        StringBuffer buf = new StringBuffer(chars.length);
        for (int i = 0; i < chars.length; ++i) {
            if (Character.isWhitespace(chars[i])) continue;
            buf.append(chars[i]);
        }
        return buf.toString();
    }

    public static String packLicense(byte[] text, byte[] hash) throws LicenseException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dOut = new DataOutputStream(out);
            dOut.writeInt(text.length);
            dOut.write(text);
            dOut.write(hash);
            byte[] allData = out.toByteArray();
            String result = new String(Base64.encodeBase64((byte[])allData), StandardCharsets.UTF_8).trim();
            result = result + 'X' + "0" + 2 + Integer.toString(result.length(), 31);
            result = Version2LicenseDecoder.split(result);
            return result;
        }
        catch (IOException e) {
            throw new LicenseException((Throwable)e);
        }
    }

    private static String split(String licenseData) {
        if (licenseData == null || licenseData.length() == 0) {
            return licenseData;
        }
        char[] chars = licenseData.toCharArray();
        StringBuffer buf = new StringBuffer(chars.length + chars.length / 76);
        for (int i = 0; i < chars.length; ++i) {
            buf.append(chars[i]);
            if (i <= 0 || i % 76 != 0) continue;
            buf.append('\n');
        }
        return buf.toString();
    }

    public void setVerifyLicenseHash(boolean verifyLicenseHash) {
        this.verifyLicenseHash = verifyLicenseHash;
    }

    public void setSkipVerificationBeforeCutoffDate(boolean skipVerificationBeforeCutoffDate) {
        this.skipVerificationBeforeCutoffDate = skipVerificationBeforeCutoffDate;
    }
}

