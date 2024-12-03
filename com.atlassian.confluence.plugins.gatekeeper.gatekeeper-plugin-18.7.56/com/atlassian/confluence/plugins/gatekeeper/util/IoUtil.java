/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoUtil {
    private static final int BUFFER_SIZE = 8192;
    private static final byte[] MAGIC_GIF = new byte[]{71, 73, 70, 56};
    private static final byte[] MAGIC_JPG = new byte[]{-1, -40, -1};
    private static final byte[] MAGIC_PNG = new byte[]{-119, 80, 78, 71};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String loadFile(File file) throws IOException {
        String string;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            int len;
            ByteArrayOutputStream baos = new ByteArrayOutputStream((int)file.length());
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis, 8192);
            byte[] b = new byte[8192];
            while ((len = bis.read(b)) > 0) {
                baos.write(b, 0, len);
            }
            string = baos.toString("UTF-8");
        }
        catch (Throwable throwable) {
            IoUtil.closeStream(bis);
            throw throwable;
        }
        IoUtil.closeStream(bis);
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void saveFile(File file, String content) throws IOException {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos, 8192);
            byte[] b = content.getBytes("UTF-8");
            bos.write(b, 0, b.length);
        }
        catch (Throwable throwable) {
            IoUtil.closeStream(bos);
            throw throwable;
        }
        IoUtil.closeStream(bos);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copyStreamToStream(InputStream is, OutputStream os) throws IOException {
        try {
            int len;
            byte[] b = new byte[8192];
            while ((len = is.read(b)) > 0) {
                os.write(b, 0, len);
            }
        }
        finally {
            IoUtil.closeStream(is);
            IoUtil.closeStream(os);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void copyStreamToFile(InputStream is, File destinationFile) throws IOException {
        FileOutputStream os = null;
        try {
            int len;
            os = new FileOutputStream(destinationFile);
            byte[] b = new byte[8192];
            while ((len = is.read(b)) > 0) {
                os.write(b, 0, len);
            }
        }
        finally {
            IoUtil.closeStream(is);
            IoUtil.closeStream(os);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String copyImageStreamToFile(InputStream is, File destinationFile) throws FileNotFoundException, IOException {
        String imageType = null;
        FileOutputStream os = null;
        try {
            int len;
            os = new FileOutputStream(destinationFile);
            byte[] b = new byte[8192];
            boolean isFirstChunk = true;
            while ((len = is.read(b)) > 0) {
                os.write(b, 0, len);
                if (!isFirstChunk) continue;
                imageType = IoUtil.detectImageType(b);
                isFirstChunk = false;
            }
        }
        finally {
            IoUtil.closeStream(is);
            IoUtil.closeStream(os);
        }
        return imageType;
    }

    private static String detectImageType(byte[] buffer) {
        if (IoUtil.compareBytes(MAGIC_PNG, buffer)) {
            return "png";
        }
        if (IoUtil.compareBytes(MAGIC_JPG, buffer)) {
            return "jpg";
        }
        if (IoUtil.compareBytes(MAGIC_GIF, buffer)) {
            return "gif";
        }
        return null;
    }

    private static boolean compareBytes(byte[] magicNumbers, byte[] buffer) {
        for (int i = 0; i < magicNumbers.length; ++i) {
            if (magicNumbers[i] == buffer[i]) continue;
            return false;
        }
        return true;
    }

    public static void closeStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public static void closeStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}

