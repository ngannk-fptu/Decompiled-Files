/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class KeystoreUtil {
    private static final CertificateFactory CERTIFICATE_FACTORY;
    private static final KeyFactory KEY_FACTORY;

    KeystoreUtil() {
    }

    static KeyStore createKeyStore(String keyAlias, KeySpec privateKeySpec, X509Certificate ... certificates) throws GeneralSecurityException, IOException {
        PrivateKey privateKey = KEY_FACTORY.generatePrivate(privateKeySpec);
        KeyStore keyStore = KeystoreUtil.createKeyStore();
        ArrayList certChain = new ArrayList();
        Collections.addAll(certChain, certificates);
        keyStore.setKeyEntry(keyAlias, privateKey, new char[0], certChain.toArray(new Certificate[certChain.size()]));
        return keyStore;
    }

    static KeyStore createKeyStore(X509Certificate ... certificates) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeystoreUtil.createKeyStore();
        int counter = 0;
        for (X509Certificate certificate : certificates) {
            keyStore.setCertificateEntry(String.format("cert_%d", counter++), certificate);
        }
        return keyStore;
    }

    static X509Certificate getCertificate(byte[] source) throws CertificateException {
        List<X509Certificate> certificates = KeystoreUtil.getCertificates(CERTIFICATE_FACTORY, source);
        return (X509Certificate)certificates.stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No X509Certificate found"));
    }

    private static KeyStore createKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, new char[0]);
        return keyStore;
    }

    private static List<X509Certificate> getCertificates(CertificateFactory cf, byte[] source) throws CertificateException {
        ArrayList<X509Certificate> x509Certificates = new ArrayList<X509Certificate>();
        ByteArrayInputStream bis = new ByteArrayInputStream(source);
        while (bis.available() > 0) {
            Certificate cert = cf.generateCertificate(bis);
            if (!(cert instanceof X509Certificate)) continue;
            x509Certificates.add((X509Certificate)cert);
        }
        return x509Certificates;
    }

    static RSAPublicKeySpec getRSAPublicKeySpec(byte[] keyBytes) throws IOException, IllegalStateException {
        DerParser parser = new DerParser(keyBytes);
        Asn1Object sequence = parser.read();
        if (sequence.getType() != 16) {
            throw new IllegalStateException("Invalid DER: not a sequence");
        }
        parser = sequence.getParser();
        Asn1Object object = parser.read();
        if (object.type == 16) {
            Asn1Object read = object.getParser().read();
            if (!"1.2.840.113549.1.1.1".equalsIgnoreCase(read.getString())) {
                throw new IllegalStateException("Unsupported Public Key Algorithm. Expected RSA (1.2.840.113549.1.1.1), but was: " + read.getString());
            }
            Asn1Object bitString = parser.read();
            if (bitString.getType() != 3) {
                throw new IllegalStateException("Invalid DER: not a bit string");
            }
            parser = new DerParser(bitString.getValue());
            sequence = parser.read();
            if (sequence.getType() != 16) {
                throw new IllegalStateException("Invalid DER: not a sequence");
            }
            parser = sequence.getParser();
        }
        BigInteger modulus = parser.read().getInteger();
        BigInteger publicExp = parser.read().getInteger();
        return new RSAPublicKeySpec(modulus, publicExp);
    }

    static RSAPrivateCrtKeySpec getRSAPrivateKeySpec(byte[] keyBytes) throws IOException {
        DerParser parser = new DerParser(keyBytes);
        Asn1Object sequence = parser.read();
        if (sequence.getType() != 16) {
            throw new IllegalStateException("Invalid DER: not a sequence");
        }
        parser = sequence.getParser();
        parser.read();
        BigInteger modulus = parser.read().getInteger();
        BigInteger publicExp = parser.read().getInteger();
        BigInteger privateExp = parser.read().getInteger();
        BigInteger prime1 = parser.read().getInteger();
        BigInteger prime2 = parser.read().getInteger();
        BigInteger exp1 = parser.read().getInteger();
        BigInteger exp2 = parser.read().getInteger();
        BigInteger crtCoef = parser.read().getInteger();
        return new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
    }

    static {
        try {
            CERTIFICATE_FACTORY = CertificateFactory.getInstance("X.509");
        }
        catch (CertificateException e) {
            throw new IllegalStateException("No X.509 Certificate available", e);
        }
        try {
            KEY_FACTORY = KeyFactory.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No RSA KeyFactory available", e);
        }
    }

    static class Asn1Object {
        private static final long LONG_LIMIT = 0xFFFFFFFFFFFF80L;
        private final int type;
        private final int length;
        private final byte[] value;
        private final int tag;

        Asn1Object(int tag, int length, byte[] value) {
            this.tag = tag;
            this.type = tag & 0x1F;
            this.length = length;
            this.value = value;
        }

        int getType() {
            return this.type;
        }

        int getLength() {
            return this.length;
        }

        byte[] getValue() {
            return this.value;
        }

        boolean isConstructed() {
            return (this.tag & 0x20) == 32;
        }

        DerParser getParser() throws IOException {
            if (!this.isConstructed()) {
                throw new IllegalStateException("Invalid DER: can't parse primitive entity");
            }
            return new DerParser(this.value);
        }

        BigInteger getInteger() {
            if (this.type != 2) {
                throw new IllegalStateException(String.format("Invalid DER: object (%d) is not integer.", this.type));
            }
            return new BigInteger(this.value);
        }

        String getString() throws IOException {
            String encoding;
            switch (this.type) {
                case 18: 
                case 19: 
                case 21: 
                case 22: 
                case 25: 
                case 26: 
                case 27: {
                    encoding = "ISO-8859-1";
                    break;
                }
                case 30: {
                    encoding = "UTF-16BE";
                    break;
                }
                case 12: {
                    encoding = "UTF-8";
                    break;
                }
                case 28: {
                    throw new IOException("Invalid DER: can't handle UCS-4 string");
                }
                case 6: {
                    return Asn1Object.getObjectIdentifier(this.value);
                }
                default: {
                    throw new IOException(String.format("Invalid DER: object (%d) is not a string", this.type));
                }
            }
            return new String(this.value, encoding);
        }

        private static String getObjectIdentifier(byte[] bytes) {
            StringBuffer objId = new StringBuffer();
            long value = 0L;
            BigInteger bigValue = null;
            boolean first = true;
            for (int i = 0; i != bytes.length; ++i) {
                int b = bytes[i] & 0xFF;
                if (value <= 0xFFFFFFFFFFFF80L) {
                    value += (long)(b & 0x7F);
                    if ((b & 0x80) == 0) {
                        if (first) {
                            if (value < 40L) {
                                objId.append('0');
                            } else if (value < 80L) {
                                objId.append('1');
                                value -= 40L;
                            } else {
                                objId.append('2');
                                value -= 80L;
                            }
                            first = false;
                        }
                        objId.append('.');
                        objId.append(value);
                        value = 0L;
                        continue;
                    }
                    value <<= 7;
                    continue;
                }
                if (bigValue == null) {
                    bigValue = BigInteger.valueOf(value);
                }
                bigValue = bigValue.or(BigInteger.valueOf(b & 0x7F));
                if ((b & 0x80) == 0) {
                    if (first) {
                        objId.append('2');
                        bigValue = bigValue.subtract(BigInteger.valueOf(80L));
                        first = false;
                    }
                    objId.append('.');
                    objId.append(bigValue);
                    bigValue = null;
                    value = 0L;
                    continue;
                }
                bigValue = bigValue.shiftLeft(7);
            }
            return objId.toString();
        }
    }

    private static class DerParser {
        static final int UNIVERSAL = 0;
        static final int APPLICATION = 64;
        static final int CONTEXT = 128;
        static final int PRIVATE = 192;
        static final int CONSTRUCTED = 32;
        static final int ANY = 0;
        static final int BOOLEAN = 1;
        static final int INTEGER = 2;
        static final int BIT_STRING = 3;
        static final int OCTET_STRING = 4;
        static final int NULL = 5;
        static final int OID = 6;
        static final int REAL = 9;
        static final int ENUMERATED = 10;
        static final int SEQUENCE = 16;
        static final int SET = 17;
        static final int NUMERIC_STRING = 18;
        static final int PRINTABLE_STRING = 19;
        static final int VIDEOTEX_STRING = 21;
        static final int IA5_STRING = 22;
        static final int GRAPHIC_STRING = 25;
        static final int ISO646_STRING = 26;
        static final int GENERAL_STRING = 27;
        static final int UTF8_STRING = 12;
        static final int UNIVERSAL_STRING = 28;
        static final int BMP_STRING = 30;
        static final int UTC_TIME = 23;
        protected InputStream in;

        DerParser(InputStream in) {
            this.in = in;
        }

        DerParser(byte[] bytes) {
            this(new ByteArrayInputStream(bytes));
        }

        public Asn1Object read() throws IOException {
            byte[] value;
            int n;
            int tag = this.in.read();
            if (tag == -1) {
                throw new IllegalStateException("Invalid DER: stream too short, missing tag");
            }
            int length = this.getLength();
            if (tag == 3) {
                int padBits = this.in.read();
                --length;
            }
            if ((n = this.in.read(value = new byte[length])) < length) {
                throw new IllegalStateException("Invalid DER: stream too short, missing value");
            }
            return new Asn1Object(tag, length, value);
        }

        private int getLength() throws IOException {
            int i = this.in.read();
            if (i == -1) {
                throw new IllegalStateException("Invalid DER: length missing");
            }
            if ((i & 0xFFFFFF80) == 0) {
                return i;
            }
            int num = i & 0x7F;
            if (i >= 255 || num > 4) {
                throw new IllegalStateException("Invalid DER: length field too big (" + i + ")");
            }
            byte[] bytes = new byte[num];
            int n = this.in.read(bytes);
            if (n < num) {
                throw new IllegalStateException("Invalid DER: length too short");
            }
            return new BigInteger(1, bytes).intValue();
        }
    }

    private static class ObjectIdentifiers {
        static final String RSA = "1.2.840.113549.1.1.1";

        private ObjectIdentifiers() {
        }
    }
}

