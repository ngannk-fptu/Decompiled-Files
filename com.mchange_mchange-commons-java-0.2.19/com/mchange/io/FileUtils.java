/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.InputStreamUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public final class FileUtils {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getBytes(File file, int n) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] byArray = InputStreamUtils.getBytes(bufferedInputStream, n);
            return byArray;
        }
        finally {
            InputStreamUtils.attemptClose(bufferedInputStream);
        }
    }

    public static byte[] getBytes(File file) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] byArray = InputStreamUtils.getBytes(bufferedInputStream);
            return byArray;
        }
        finally {
            InputStreamUtils.attemptClose(bufferedInputStream);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getContentsAsString(File file, String string) throws IOException, UnsupportedEncodingException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
            String string2 = InputStreamUtils.getContentsAsString((InputStream)bufferedInputStream, string);
            return string2;
        }
        finally {
            InputStreamUtils.attemptClose(bufferedInputStream);
        }
    }

    public static String getContentsAsString(File file) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
            String string = InputStreamUtils.getContentsAsString(bufferedInputStream);
            return string;
        }
        finally {
            InputStreamUtils.attemptClose(bufferedInputStream);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getContentsAsString(File file, int n, String string) throws IOException, UnsupportedEncodingException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
            String string2 = InputStreamUtils.getContentsAsString(bufferedInputStream, n, string);
            return string2;
        }
        finally {
            InputStreamUtils.attemptClose(bufferedInputStream);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getContentsAsString(File file, int n) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
            String string = InputStreamUtils.getContentsAsString((InputStream)bufferedInputStream, n);
            return string;
        }
        finally {
            InputStreamUtils.attemptClose(bufferedInputStream);
        }
    }

    private FileUtils() {
    }
}

