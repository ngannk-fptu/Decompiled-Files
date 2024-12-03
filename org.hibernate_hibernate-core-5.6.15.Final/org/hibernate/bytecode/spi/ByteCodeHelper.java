/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.spi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ByteCodeHelper {
    private ByteCodeHelper() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] readByteCode(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("null input stream");
        }
        byte[] buffer = new byte[409600];
        byte[] classBytes = new byte[]{};
        try {
            byte[] temp;
            int r = inputStream.read(buffer);
            while (r >= buffer.length) {
                temp = new byte[classBytes.length + buffer.length];
                System.arraycopy(classBytes, 0, temp, 0, classBytes.length);
                System.arraycopy(buffer, 0, temp, classBytes.length, buffer.length);
                classBytes = temp;
                r = inputStream.read(buffer);
            }
            if (r != -1) {
                temp = new byte[classBytes.length + r];
                System.arraycopy(classBytes, 0, temp, 0, classBytes.length);
                System.arraycopy(buffer, 0, temp, classBytes.length, r);
                classBytes = temp;
            }
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException iOException) {}
        }
        return classBytes;
    }

    public static byte[] readByteCode(File file) throws IOException {
        return ByteCodeHelper.readByteCode(new FileInputStream(file));
    }

    public static byte[] readByteCode(ZipInputStream zip) throws IOException {
        int b;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(zip);
        while ((b = ((InputStream)in).read()) != -1) {
            bout.write(b);
        }
        return bout.toByteArray();
    }
}

