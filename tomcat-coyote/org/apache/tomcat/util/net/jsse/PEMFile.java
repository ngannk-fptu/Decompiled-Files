/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.Asn1Parser
 *  org.apache.tomcat.util.buf.Asn1Writer
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.codec.binary.Base64
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net.jsse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.buf.Asn1Parser;
import org.apache.tomcat.util.buf.Asn1Writer;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.res.StringManager;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public class PEMFile {
    private static final StringManager sm = StringManager.getManager(PEMFile.class);
    private static final byte[] OID_EC_PUBLIC_KEY = new byte[]{6, 7, 42, -122, 72, -50, 61, 2, 1};
    private static final byte[] OID_PBES2 = new byte[]{42, -122, 72, -122, -9, 13, 1, 5, 13};
    private static final byte[] OID_PBKDF2 = new byte[]{42, -122, 72, -122, -9, 13, 1, 5, 12};
    private static final Map<String, String> OID_TO_PRF = new HashMap<String, String>();
    private static final Map<String, Algorithm> OID_TO_ALGORITHM;
    private List<X509Certificate> certificates = new ArrayList<X509Certificate>();
    private PrivateKey privateKey;

    public static String toPEM(X509Certificate certificate) throws CertificateEncodingException {
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN CERTIFICATE-----");
        result.append(System.lineSeparator());
        Base64 b64 = new Base64(64);
        result.append(b64.encodeAsString(certificate.getEncoded()));
        result.append("-----END CERTIFICATE-----");
        return result.toString();
    }

    public List<X509Certificate> getCertificates() {
        return this.certificates;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PEMFile(String filename) throws IOException, GeneralSecurityException {
        this(filename, null);
    }

    public PEMFile(String filename, String password) throws IOException, GeneralSecurityException {
        this(filename, password, null);
    }

    public PEMFile(String filename, String password, String keyAlgorithm) throws IOException, GeneralSecurityException {
        this(filename, ConfigFileLoader.getSource().getResource(filename).getInputStream(), password, keyAlgorithm);
    }

    public PEMFile(String filename, String password, String passwordFilename, String keyAlgorithm) throws IOException, GeneralSecurityException {
        this(filename, ConfigFileLoader.getSource().getResource(filename).getInputStream(), password, passwordFilename, passwordFilename != null ? ConfigFileLoader.getSource().getResource(passwordFilename).getInputStream() : null, keyAlgorithm);
    }

    public PEMFile(String filename, InputStream fileStream, String password, String keyAlgorithm) throws IOException, GeneralSecurityException {
        this(filename, fileStream, password, null, null, keyAlgorithm);
    }

    public PEMFile(String filename, InputStream fileStream, String password, String passwordFilename, InputStream passwordFileStream, String keyAlgorithm) throws IOException, GeneralSecurityException {
        ArrayList<Part> parts = new ArrayList<Part>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.US_ASCII));){
            String line;
            Part part = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-----BEGIN ")) {
                    part = new Part();
                    part.type = line.substring("-----BEGIN ".length(), line.length() - "-----".length()).trim();
                    continue;
                }
                if (line.startsWith("-----END ")) {
                    parts.add(part);
                    part = null;
                    continue;
                }
                if (part != null && !line.contains(":") && !line.startsWith(" ")) {
                    part.content = part.content + line;
                    continue;
                }
                if (part == null || !line.contains(":") || line.startsWith(" ") || !line.startsWith("DEK-Info: ")) continue;
                String[] pieces = line.split(" ");
                if ((pieces = pieces[1].split(",")).length != 2) continue;
                part.algorithm = pieces[0];
                part.ivHex = pieces[1];
            }
        }
        String passwordToUse = null;
        if (passwordFileStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(passwordFileStream, StandardCharsets.UTF_8));){
                passwordToUse = reader.readLine();
            }
        } else {
            passwordToUse = password;
        }
        for (Part part : parts) {
            switch (part.type) {
                case "PRIVATE KEY": {
                    this.privateKey = part.toPrivateKey(keyAlgorithm, Format.PKCS8, filename);
                    break;
                }
                case "EC PRIVATE KEY": {
                    this.privateKey = part.toPrivateKey("EC", Format.RFC5915, filename);
                    break;
                }
                case "ENCRYPTED PRIVATE KEY": {
                    this.privateKey = part.toPrivateKey(passwordToUse, keyAlgorithm, Format.PKCS8, filename);
                    break;
                }
                case "RSA PRIVATE KEY": {
                    if (part.algorithm == null) {
                        this.privateKey = part.toPrivateKey(keyAlgorithm, Format.PKCS1, filename);
                        break;
                    }
                    this.privateKey = part.toPrivateKey(passwordToUse, keyAlgorithm, Format.PKCS1, filename);
                    break;
                }
                case "CERTIFICATE": 
                case "X509 CERTIFICATE": {
                    this.certificates.add(part.toCertificate());
                }
            }
        }
    }

    static {
        OID_TO_PRF.put("2a864886f70d0207", "HmacSHA1");
        OID_TO_PRF.put("2a864886f70d0208", "HmacSHA224");
        OID_TO_PRF.put("2a864886f70d0209", "HmacSHA256");
        OID_TO_PRF.put("2a864886f70d020a", "HmacSHA384");
        OID_TO_PRF.put("2a864886f70d020b", "HmacSHA512");
        OID_TO_PRF.put("2a864886f70d020c", "HmacSHA512/224");
        OID_TO_PRF.put("2a864886f70d020d", "HmacSHA512/256");
        OID_TO_ALGORITHM = new HashMap<String, Algorithm>();
        OID_TO_ALGORITHM.put("2a864886f70d0307", Algorithm.DES_EDE3_CBC);
        OID_TO_ALGORITHM.put("608648016503040102", Algorithm.AES128_CBC_PAD);
        OID_TO_ALGORITHM.put("60864801650304012a", Algorithm.AES256_CBC_PAD);
    }

    private static class Part {
        public static final String BEGIN_BOUNDARY = "-----BEGIN ";
        public static final String END_BOUNDARY = "-----END ";
        public static final String FINISH_BOUNDARY = "-----";
        public static final String PRIVATE_KEY = "PRIVATE KEY";
        public static final String EC_PRIVATE_KEY = "EC PRIVATE KEY";
        public static final String ENCRYPTED_PRIVATE_KEY = "ENCRYPTED PRIVATE KEY";
        public static final String RSA_PRIVATE_KEY = "RSA PRIVATE KEY";
        public static final String CERTIFICATE = "CERTIFICATE";
        public static final String X509_CERTIFICATE = "X509 CERTIFICATE";
        public String type;
        public String content = "";
        public String algorithm = null;
        public String ivHex = null;

        private Part() {
        }

        private byte[] decode() {
            return Base64.decodeBase64((String)this.content);
        }

        public X509Certificate toCertificate() throws CertificateException {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(this.decode()));
        }

        public PrivateKey toPrivateKey(String keyAlgorithm, Format format, String filename) throws GeneralSecurityException {
            return this.toPrivateKey(keyAlgorithm, format, filename, this.decode());
        }

        public PrivateKey toPrivateKey(String password, String keyAlgorithm, Format format, String filename) throws GeneralSecurityException, IOException {
            switch (format) {
                case PKCS1: {
                    int keyLength;
                    String cipherTransformation;
                    String secretKeyAlgorithm;
                    switch (this.algorithm) {
                        case "DES-CBC": {
                            secretKeyAlgorithm = "DES";
                            cipherTransformation = "DES/CBC/PKCS5Padding";
                            keyLength = 8;
                            break;
                        }
                        case "DES-EDE3-CBC": {
                            secretKeyAlgorithm = "DESede";
                            cipherTransformation = "DESede/CBC/PKCS5Padding";
                            keyLength = 24;
                            break;
                        }
                        case "AES-256-CBC": {
                            secretKeyAlgorithm = "AES";
                            cipherTransformation = "AES/CBC/PKCS5Padding";
                            keyLength = 32;
                            break;
                        }
                        default: {
                            secretKeyAlgorithm = this.algorithm;
                            cipherTransformation = this.algorithm;
                            keyLength = 8;
                        }
                    }
                    byte[] iv = this.fromHex(this.ivHex);
                    byte[] key = this.deriveKeyPBKDF1(keyLength, password, iv);
                    SecretKeySpec secretKey = new SecretKeySpec(key, secretKeyAlgorithm);
                    Cipher cipher = Cipher.getInstance(cipherTransformation);
                    cipher.init(2, (Key)secretKey, new IvParameterSpec(iv));
                    byte[] pkcs1 = cipher.doFinal(this.decode());
                    return this.toPrivateKey(keyAlgorithm, format, filename, pkcs1);
                }
                case PKCS8: {
                    Asn1Parser p = new Asn1Parser(this.decode());
                    p.parseTagSequence();
                    p.parseFullLength();
                    p.parseTagSequence();
                    p.parseLength();
                    byte[] oidEncryptionAlgorithm = p.parseOIDAsBytes();
                    if (!Arrays.equals(oidEncryptionAlgorithm, OID_PBES2)) {
                        throw new NoSuchAlgorithmException(sm.getString("pemFile.unknownPkcs8Algorithm", new Object[]{this.toDottedOidString(oidEncryptionAlgorithm)}));
                    }
                    p.parseTagSequence();
                    p.parseLength();
                    p.parseTagSequence();
                    p.parseLength();
                    byte[] oidKDF = p.parseOIDAsBytes();
                    if (!Arrays.equals(oidKDF, OID_PBKDF2)) {
                        throw new NoSuchAlgorithmException(sm.getString("pemFile.notPbkdf2", new Object[]{this.toDottedOidString(oidKDF)}));
                    }
                    p.parseTagSequence();
                    p.parseLength();
                    byte[] salt = p.parseOctetString();
                    int iterationCount = p.parseInt().intValue();
                    if (p.peekTag() == 2) {
                        int n = p.parseInt().intValue();
                    }
                    p.parseTagSequence();
                    p.parseLength();
                    byte[] oidPRF = p.parseOIDAsBytes();
                    String prf = (String)OID_TO_PRF.get(HexUtils.toHexString((byte[])oidPRF));
                    if (prf == null) {
                        throw new NoSuchAlgorithmException(sm.getString("pemFile.unknownPrfAlgorithm", new Object[]{this.toDottedOidString(oidPRF)}));
                    }
                    p.parseNull();
                    p.parseTagSequence();
                    p.parseLength();
                    byte[] oidCipher = p.parseOIDAsBytes();
                    Algorithm algorithm = (Algorithm)((Object)OID_TO_ALGORITHM.get(HexUtils.toHexString((byte[])oidCipher)));
                    if (algorithm == null) {
                        throw new NoSuchAlgorithmException(sm.getString("pemFile.unknownEncryptionAlgorithm", new Object[]{this.toDottedOidString(oidCipher)}));
                    }
                    byte[] iv = p.parseOctetString();
                    byte[] encryptedData = p.parseOctetString();
                    byte[] key = this.deriveKeyPBKDF2("PBKDF2With" + prf, password, salt, iterationCount, algorithm.getKeyLength());
                    SecretKeySpec secretKey = new SecretKeySpec(key, algorithm.getSecretKeyAlgorithm());
                    Cipher cipher = Cipher.getInstance(algorithm.getTransformation());
                    cipher.init(2, (Key)secretKey, new IvParameterSpec(iv));
                    byte[] decryptedData = cipher.doFinal(encryptedData);
                    return this.toPrivateKey(keyAlgorithm, format, filename, decryptedData);
                }
            }
            throw new NoSuchAlgorithmException(sm.getString("pemFile.unknownEncryptedFormat", new Object[]{format}));
        }

        private PrivateKey toPrivateKey(String keyAlgorithm, Format format, String filename, byte[] source) throws GeneralSecurityException {
            KeySpec keySpec = null;
            switch (format) {
                case PKCS1: {
                    keySpec = this.parsePKCS1(source);
                    break;
                }
                case PKCS8: {
                    keySpec = new PKCS8EncodedKeySpec(source);
                    break;
                }
                case RFC5915: {
                    keySpec = new PKCS8EncodedKeySpec(this.rfc5915ToPkcs8(source));
                }
            }
            InvalidKeyException exception = new InvalidKeyException(sm.getString("pemFile.parseError", new Object[]{filename}));
            if (keyAlgorithm == null) {
                for (String algorithm : new String[]{"RSA", "DSA", "EC"}) {
                    try {
                        return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
                    }
                    catch (InvalidKeySpecException e) {
                        exception.addSuppressed(e);
                    }
                }
            } else {
                try {
                    return KeyFactory.getInstance(keyAlgorithm).generatePrivate(keySpec);
                }
                catch (InvalidKeySpecException e) {
                    exception.addSuppressed(e);
                }
            }
            throw exception;
        }

        private byte[] deriveKeyPBKDF1(int keyLength, String password, byte[] salt) throws NoSuchAlgorithmException {
            byte[] round;
            byte[] key = new byte[keyLength];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] pw = password.getBytes(StandardCharsets.UTF_8);
            for (int insertPosition = 0; insertPosition < keyLength; insertPosition += round.length) {
                digest.update(pw);
                digest.update(salt, 0, 8);
                round = digest.digest();
                digest.update(round);
                System.arraycopy(round, 0, key, insertPosition, Math.min(keyLength - insertPosition, round.length));
            }
            return key;
        }

        private byte[] deriveKeyPBKDF2(String algorithm, String password, byte[] salt, int iterations, int keyLength) throws GeneralSecurityException {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            return secretKey.getEncoded();
        }

        private byte[] rfc5915ToPkcs8(byte[] source) {
            Asn1Parser p = new Asn1Parser(source);
            p.parseTag(48);
            p.parseFullLength();
            BigInteger version = p.parseInt();
            if (version.intValue() != 1) {
                throw new IllegalArgumentException(sm.getString("pemFile.notValidRFC5915"));
            }
            p.parseTag(4);
            int privateKeyLen = p.parseLength();
            byte[] privateKey = new byte[privateKeyLen];
            p.parseBytes(privateKey);
            p.parseTag(160);
            int oidLen = p.parseLength();
            byte[] oid = new byte[oidLen];
            p.parseBytes(oid);
            if (oid[0] != 6) {
                throw new IllegalArgumentException(sm.getString("pemFile.notValidRFC5915"));
            }
            p.parseTag(161);
            int publicKeyLen = p.parseLength();
            byte[] publicKey = new byte[publicKeyLen];
            p.parseBytes(publicKey);
            if (publicKey[0] != 3) {
                throw new IllegalArgumentException(sm.getString("pemFile.notValidRFC5915"));
            }
            return Asn1Writer.writeSequence((byte[][])new byte[][]{Asn1Writer.writeInteger((int)0), Asn1Writer.writeSequence((byte[][])new byte[][]{OID_EC_PUBLIC_KEY, oid}), Asn1Writer.writeOctetString((byte[])Asn1Writer.writeSequence((byte[][])new byte[][]{Asn1Writer.writeInteger((int)1), Asn1Writer.writeOctetString((byte[])privateKey), Asn1Writer.writeTag((byte)-95, (byte[])publicKey)}))});
        }

        private RSAPrivateCrtKeySpec parsePKCS1(byte[] source) {
            Asn1Parser p = new Asn1Parser(source);
            p.parseTag(48);
            p.parseFullLength();
            BigInteger version = p.parseInt();
            if (version.intValue() == 1) {
                throw new IllegalArgumentException(sm.getString("pemFile.noMultiPrimes"));
            }
            return new RSAPrivateCrtKeySpec(p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt());
        }

        private byte[] fromHex(String hexString) {
            byte[] bytes = new byte[hexString.length() / 2];
            for (int i = 0; i < hexString.length(); i += 2) {
                bytes[i / 2] = (byte)((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
            }
            return bytes;
        }

        private String toDottedOidString(byte[] oidBytes) {
            try {
                Oid oid = new Oid(oidBytes);
                return oid.toString();
            }
            catch (GSSException e) {
                return HexUtils.toHexString((byte[])oidBytes);
            }
        }
    }

    private static enum Format {
        PKCS1,
        PKCS8,
        RFC5915;

    }

    private static enum Algorithm {
        AES128_CBC_PAD("AES/CBC/PKCS5PADDING", "AES", 128),
        AES256_CBC_PAD("AES/CBC/PKCS5PADDING", "AES", 256),
        DES_EDE3_CBC("DESede/CBC/PKCS5Padding", "DESede", 192);

        private final String transformation;
        private final String secretKeyAlgorithm;
        private final int keyLength;

        private Algorithm(String transformation, String secretKeyAlgorithm, int keyLength) {
            this.transformation = transformation;
            this.secretKeyAlgorithm = secretKeyAlgorithm;
            this.keyLength = keyLength;
        }

        public String getTransformation() {
            return this.transformation;
        }

        public String getSecretKeyAlgorithm() {
            return this.secretKeyAlgorithm;
        }

        public int getKeyLength() {
            return this.keyLength;
        }
    }
}

