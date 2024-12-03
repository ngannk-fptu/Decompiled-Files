/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.lang.NonNull
 *  org.springframework.util.Assert
 */
package org.springframework.security.converter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public final class RsaKeyConverters {
    private static final String DASHES = "-----";
    private static final String PKCS8_PEM_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PKCS8_PEM_FOOTER = "-----END PRIVATE KEY-----";
    private static final String X509_PEM_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String X509_PEM_FOOTER = "-----END PUBLIC KEY-----";
    private static final String X509_CERT_HEADER = "-----BEGIN CERTIFICATE-----";
    private static final String X509_CERT_FOOTER = "-----END CERTIFICATE-----";

    private RsaKeyConverters() {
    }

    public static Converter<InputStream, RSAPrivateKey> pkcs8() {
        KeyFactory keyFactory = RsaKeyConverters.rsaFactory();
        return source -> {
            List<String> lines = RsaKeyConverters.readAllLines(source);
            Assert.isTrue((!lines.isEmpty() && lines.get(0).startsWith(PKCS8_PEM_HEADER) ? 1 : 0) != 0, (String)"Key is not in PEM-encoded PKCS#8 format, please check that the header begins with -----BEGIN PRIVATE KEY-----");
            StringBuilder base64Encoded = new StringBuilder();
            for (String line : lines) {
                if (!RsaKeyConverters.isNotPkcs8Wrapper(line)) continue;
                base64Encoded.append(line);
            }
            byte[] pkcs8 = Base64.getDecoder().decode(base64Encoded.toString());
            try {
                return (RSAPrivateKey)keyFactory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8));
            }
            catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        };
    }

    public static Converter<InputStream, RSAPublicKey> x509() {
        X509PemDecoder pemDecoder = new X509PemDecoder(RsaKeyConverters.rsaFactory());
        X509CertificateDecoder certDecoder = new X509CertificateDecoder(RsaKeyConverters.x509CertificateFactory());
        return source -> {
            List<String> lines = RsaKeyConverters.readAllLines(source);
            Assert.notEmpty(lines, (String)"Input stream is empty");
            String encodingHint = lines.get(0);
            X509PemDecoder decoder = encodingHint.startsWith(X509_PEM_HEADER) ? pemDecoder : (encodingHint.startsWith(X509_CERT_HEADER) ? certDecoder : null);
            Assert.notNull((Object)decoder, (String)"Key is not in PEM-encoded X.509 format or a valid X.509 certificate, please check that the header begins with -----BEGIN PUBLIC KEY----- or -----BEGIN CERTIFICATE-----");
            return (RSAPublicKey)decoder.convert(lines);
        };
    }

    private static CertificateFactory x509CertificateFactory() {
        try {
            return CertificateFactory.getInstance("X.509");
        }
        catch (CertificateException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static List<String> readAllLines(InputStream source) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(source));
        return reader.lines().collect(Collectors.toList());
    }

    private static KeyFactory rsaFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static boolean isNotPkcs8Wrapper(String line) {
        return !PKCS8_PEM_HEADER.equals(line) && !PKCS8_PEM_FOOTER.equals(line);
    }

    private static class X509CertificateDecoder
    implements Converter<List<String>, RSAPublicKey> {
        private final CertificateFactory certificateFactory;

        X509CertificateDecoder(CertificateFactory certificateFactory) {
            this.certificateFactory = certificateFactory;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @NonNull
        public RSAPublicKey convert(List<String> lines) {
            StringBuilder base64Encoded = new StringBuilder();
            for (String line : lines) {
                if (!this.isNotX509CertificateWrapper(line)) continue;
                base64Encoded.append(line);
            }
            byte[] x509 = Base64.getDecoder().decode(base64Encoded.toString());
            try (ByteArrayInputStream x509CertStream = new ByteArrayInputStream(x509);){
                X509Certificate certificate = (X509Certificate)this.certificateFactory.generateCertificate(x509CertStream);
                RSAPublicKey rSAPublicKey = (RSAPublicKey)certificate.getPublicKey();
                return rSAPublicKey;
            }
            catch (IOException | CertificateException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        private boolean isNotX509CertificateWrapper(String line) {
            return !RsaKeyConverters.X509_CERT_HEADER.equals(line) && !RsaKeyConverters.X509_CERT_FOOTER.equals(line);
        }
    }

    private static class X509PemDecoder
    implements Converter<List<String>, RSAPublicKey> {
        private final KeyFactory keyFactory;

        X509PemDecoder(KeyFactory keyFactory) {
            this.keyFactory = keyFactory;
        }

        @NonNull
        public RSAPublicKey convert(List<String> lines) {
            StringBuilder base64Encoded = new StringBuilder();
            for (String line : lines) {
                if (!this.isNotX509PemWrapper(line)) continue;
                base64Encoded.append(line);
            }
            byte[] x509 = Base64.getDecoder().decode(base64Encoded.toString());
            try {
                return (RSAPublicKey)this.keyFactory.generatePublic(new X509EncodedKeySpec(x509));
            }
            catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        private boolean isNotX509PemWrapper(String line) {
            return !RsaKeyConverters.X509_PEM_HEADER.equals(line) && !RsaKeyConverters.X509_PEM_FOOTER.equals(line);
        }
    }
}

