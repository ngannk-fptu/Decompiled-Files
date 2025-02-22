/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.cert.X509CertificateHolder
 *  org.bouncycastle.openssl.PEMParser
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.X509CertUtils;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.minidev.json.JSONArray;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;

public class X509CertChainUtils {
    public static List<Base64> toBase64List(JSONArray jsonArray) throws ParseException {
        if (jsonArray == null) {
            return null;
        }
        LinkedList<Base64> chain = new LinkedList<Base64>();
        for (int i = 0; i < jsonArray.size(); ++i) {
            Object item = jsonArray.get(i);
            if (item == null) {
                throw new ParseException("The X.509 certificate at position " + i + " must not be null", 0);
            }
            if (!(item instanceof String)) {
                throw new ParseException("The X.509 certificate at position " + i + " must be encoded as a Base64 string", 0);
            }
            chain.add(new Base64((String)item));
        }
        return chain;
    }

    public static List<X509Certificate> parse(List<Base64> b64List) throws ParseException {
        if (b64List == null) {
            return null;
        }
        LinkedList<X509Certificate> out = new LinkedList<X509Certificate>();
        for (int i = 0; i < b64List.size(); ++i) {
            if (b64List.get(i) == null) continue;
            X509Certificate cert = X509CertUtils.parse(b64List.get(i).decode());
            if (cert == null) {
                throw new ParseException("Invalid X.509 certificate at position " + i, 0);
            }
            out.add(cert);
        }
        return out;
    }

    public static List<X509Certificate> parse(File pemFile) throws IOException, CertificateException {
        String pemString = new String(Files.readAllBytes(pemFile.toPath()), StandardCharsets.UTF_8);
        return X509CertChainUtils.parse(pemString);
    }

    public static List<X509Certificate> parse(String pemString) throws IOException, CertificateException {
        Object pemObject;
        StringReader pemReader = new StringReader(pemString);
        PEMParser parser = new PEMParser((Reader)pemReader);
        LinkedList<X509Certificate> certChain = new LinkedList<X509Certificate>();
        do {
            if (!((pemObject = parser.readObject()) instanceof X509CertificateHolder)) continue;
            X509CertificateHolder certHolder = (X509CertificateHolder)pemObject;
            byte[] derEncodedCert = certHolder.getEncoded();
            certChain.add(X509CertUtils.parseWithException(derEncodedCert));
        } while (pemObject != null);
        return certChain;
    }

    public static List<UUID> store(KeyStore trustStore, List<X509Certificate> certChain) throws KeyStoreException {
        LinkedList<UUID> aliases = new LinkedList<UUID>();
        for (X509Certificate cert : certChain) {
            UUID alias = UUID.randomUUID();
            trustStore.setCertificateEntry(alias.toString(), cert);
            aliases.add(alias);
        }
        return aliases;
    }

    private X509CertChainUtils() {
    }
}

