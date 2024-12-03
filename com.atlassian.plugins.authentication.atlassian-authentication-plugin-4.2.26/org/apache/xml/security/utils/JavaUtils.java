/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.security.SecurityPermission;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JavaUtils.class);
    private static final SecurityPermission REGISTER_PERMISSION = new SecurityPermission("org.apache.xml.security.register");

    private JavaUtils() {
    }

    public static byte[] getBytesFromFile(String fileName) throws FileNotFoundException, IOException {
        byte[] refBytes = null;
        try (InputStream inputStream = Files.newInputStream(Paths.get(fileName, new String[0]), new OpenOption[0]);
             UnsyncByteArrayOutputStream baos = new UnsyncByteArrayOutputStream();){
            int len;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            refBytes = baos.toByteArray();
        }
        return refBytes;
    }

    public static void writeBytesToFilename(String filename, byte[] bytes) {
        if (filename != null && bytes != null) {
            try (OutputStream outputStream = Files.newOutputStream(Paths.get(filename, new String[0]), new OpenOption[0]);){
                outputStream.write(bytes);
            }
            catch (IOException ex) {
                LOG.debug(ex.getMessage(), (Throwable)ex);
            }
        } else {
            LOG.debug("writeBytesToFilename got null byte[] pointed");
        }
    }

    public static byte[] getBytesFromStream(InputStream inputStream) throws IOException {
        try (UnsyncByteArrayOutputStream baos = new UnsyncByteArrayOutputStream();){
            int len;
            byte[] buf = new byte[4096];
            while ((len = inputStream.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
    }

    public static byte[] convertDsaASN1toXMLDSIG(byte[] asn1Bytes, int size) throws IOException {
        int sLength;
        int j;
        int rLength;
        int i;
        if (asn1Bytes[0] != 48 || asn1Bytes[1] != asn1Bytes.length - 2 || asn1Bytes[2] != 2) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        }
        for (i = rLength = asn1Bytes[3]; i > 0 && asn1Bytes[4 + rLength - i] == 0; --i) {
        }
        for (j = sLength = asn1Bytes[5 + rLength]; j > 0 && asn1Bytes[6 + rLength + sLength - j] == 0; --j) {
        }
        if (i > size || asn1Bytes[4 + rLength] != 2 || j > size) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        }
        byte[] xmldsigBytes = new byte[size * 2];
        System.arraycopy(asn1Bytes, 4 + rLength - i, xmldsigBytes, size - i, i);
        System.arraycopy(asn1Bytes, 6 + rLength + sLength - j, xmldsigBytes, size * 2 - j, j);
        return xmldsigBytes;
    }

    public static byte[] convertDsaXMLDSIGtoASN1(byte[] xmldsigBytes, int size) throws IOException {
        int k;
        int i;
        int totalSize = size * 2;
        if (xmldsigBytes.length != totalSize) {
            throw new IOException("Invalid XMLDSIG format of DSA signature");
        }
        for (i = size; i > 0 && xmldsigBytes[size - i] == 0; --i) {
        }
        int j = i;
        if (xmldsigBytes[size - i] < 0) {
            ++j;
        }
        for (k = size; k > 0 && xmldsigBytes[totalSize - k] == 0; --k) {
        }
        int l = k;
        if (xmldsigBytes[totalSize - k] < 0) {
            ++l;
        }
        byte[] asn1Bytes = new byte[6 + j + l];
        asn1Bytes[0] = 48;
        asn1Bytes[1] = (byte)(4 + j + l);
        asn1Bytes[2] = 2;
        asn1Bytes[3] = (byte)j;
        System.arraycopy(xmldsigBytes, size - i, asn1Bytes, 4 + j - i, i);
        asn1Bytes[4 + j] = 2;
        asn1Bytes[5 + j] = (byte)l;
        System.arraycopy(xmldsigBytes, totalSize - k, asn1Bytes, 6 + j + l - k, k);
        return asn1Bytes;
    }

    public static void checkRegisterPermission() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(REGISTER_PERMISSION);
        }
    }

    public static <T> T newInstanceWithEmptyConstructor(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        try {
            return clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (NoSuchMethodException | InvocationTargetException e) {
            throw (InstantiationException)new InstantiationException(clazz.getName()).initCause(e);
        }
    }
}

