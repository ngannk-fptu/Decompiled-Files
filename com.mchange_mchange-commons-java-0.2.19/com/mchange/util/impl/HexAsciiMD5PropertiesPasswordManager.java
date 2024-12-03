/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.lang.ByteUtils;
import com.mchange.util.PasswordManager;
import com.mchange.util.impl.SyncedProperties;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HexAsciiMD5PropertiesPasswordManager
implements PasswordManager {
    private static final String DIGEST_ALGORITHM = "MD5";
    private static final String PASSWORD_ENCODING = "8859_1";
    private static final String DEF_PASSWORD_PROP_PFX = "password";
    private static final String DEF_HEADER = "com.mchange.util.impl.HexAsciiMD5PropertiesPasswordManager data";
    private static final boolean DEBUG = true;
    SyncedProperties props;
    String pfx;
    MessageDigest md;

    public HexAsciiMD5PropertiesPasswordManager(File file, String string, String[] stringArray) throws IOException {
        this(new SyncedProperties(file, stringArray), string);
    }

    public HexAsciiMD5PropertiesPasswordManager(File file, String string, String string2) throws IOException {
        this(new SyncedProperties(file, string2), string);
    }

    public HexAsciiMD5PropertiesPasswordManager(File file) throws IOException {
        this(file, DEF_PASSWORD_PROP_PFX, DEF_HEADER);
    }

    private HexAsciiMD5PropertiesPasswordManager(SyncedProperties syncedProperties, String string) throws IOException {
        try {
            this.props = syncedProperties;
            this.pfx = string;
            this.md = MessageDigest.getInstance(DIGEST_ALGORITHM);
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new InternalError("MD5 is not supported???");
        }
    }

    @Override
    public synchronized boolean validate(String string, String string2) throws IOException {
        try {
            String string3 = this.props.getProperty(this.pfx != null ? this.pfx + '.' + string : string);
            byte[] byArray = ByteUtils.fromHexAscii(string3);
            byte[] byArray2 = this.md.digest(string2.getBytes(PASSWORD_ENCODING));
            return Arrays.equals(byArray, byArray2);
        }
        catch (NumberFormatException numberFormatException) {
            throw new IOException("Password file corrupted! [contains invalid hex ascii string]");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
            throw new InternalError("8859_1is an unsupported encoding???");
        }
    }

    @Override
    public synchronized boolean updatePassword(String string, String string2, String string3) throws IOException {
        if (!this.validate(string, string2)) {
            return false;
        }
        this.props.put(this.pfx + '.' + string, ByteUtils.toHexAscii(this.md.digest(string3.getBytes(PASSWORD_ENCODING))));
        return true;
    }
}

