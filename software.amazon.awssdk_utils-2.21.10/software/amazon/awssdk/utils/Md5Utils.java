/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkProtectedApi
public final class Md5Utils {
    private static final int SIXTEEN_K = 16384;

    private Md5Utils() {
    }

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
                LoggerFactory.getLogger(Md5Utils.class).debug("Unable to close input stream of hash candidate: {}", (Throwable)e);
            }
        }
    }

    public static String md5AsBase64(InputStream is) throws IOException {
        return BinaryUtils.toBase64(Md5Utils.computeMD5Hash(is));
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
        return BinaryUtils.toBase64(Md5Utils.computeMD5Hash(input));
    }

    public static byte[] computeMD5Hash(File file) throws FileNotFoundException, IOException {
        return Md5Utils.computeMD5Hash(new FileInputStream(file));
    }

    public static String md5AsBase64(File file) throws FileNotFoundException, IOException {
        return BinaryUtils.toBase64(Md5Utils.computeMD5Hash(file));
    }
}

