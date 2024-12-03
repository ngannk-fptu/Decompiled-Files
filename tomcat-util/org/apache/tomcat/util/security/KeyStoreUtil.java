/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreUtil {
    private KeyStoreUtil() {
    }

    public static void load(KeyStore keystore, InputStream is, char[] storePass) throws NoSuchAlgorithmException, CertificateException, IOException {
        if (keystore.getType().equals("PKCS12")) {
            int numRead;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            while ((numRead = is.read(buf)) >= 0) {
                baos.write(buf, 0, numRead);
            }
            baos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            keystore.load(bais, storePass);
        } else {
            keystore.load(is, storePass);
        }
    }
}

