/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.util;

import com.amazonaws.util.Base64;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.logging.LogFactory;

public class Md5Utils {
    private static final int SIXTEEN_K = 16384;

    public static byte[] computeMD5Hash(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            int bytesRead;
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[16384];
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            }
            byte[] byArray = messageDigest.digest();
            return byArray;
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        finally {
            try {
                bis.close();
            }
            catch (Exception e) {
                LogFactory.getLog(Md5Utils.class).debug((Object)("Unable to close input stream of hash candidate: " + e));
            }
        }
    }

    public static String md5AsBase64(InputStream is) throws IOException {
        return Base64.encodeAsString(Md5Utils.computeMD5Hash(is));
    }

    public static byte[] computeMD5Hash(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(input);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String md5AsBase64(byte[] input) {
        return Base64.encodeAsString(Md5Utils.computeMD5Hash(input));
    }

    public static byte[] computeMD5Hash(File file) throws FileNotFoundException, IOException {
        return Md5Utils.computeMD5Hash(new FileInputStream(file));
    }

    public static String md5AsBase64(File file) throws FileNotFoundException, IOException {
        return Base64.encodeAsString(Md5Utils.computeMD5Hash(file));
    }
}

