/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.openssl.PEMDecryptorProvider
 *  org.bouncycastle.openssl.PEMEncryptedKeyPair
 *  org.bouncycastle.openssl.PEMKeyPair
 *  org.bouncycastle.openssl.PEMParser
 *  org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
 *  org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerBouncyCastleLoader;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

final class SQLServerCertificateUtils {
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerCertificateUtils");
    private static final String logContext = Thread.currentThread().getStackTrace()[1].getClassName() + ": ";
    private static final String PKCS12_ALG = "PKCS12";
    private static final String SUN_X_509 = "SunX509";
    private static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";
    private static final String JAVA_KEY_STORE = "JKS";
    private static final String CLIENT_CERT = "client-cert";
    private static final String CLIENT_KEY = "client-key";
    private static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
    private static final long PVK_MAGIC = 2964713758L;
    private static final byte[] RSA2_MAGIC = new byte[]{82, 83, 65, 50};
    private static final String RC4_ALG = "RC4";
    private static final String RSA_ALG = "RSA";

    private SQLServerCertificateUtils() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static KeyManager[] getKeyManagerFromFile(String certPath, String keyPath, String keyPassword) throws IOException, GeneralSecurityException, SQLServerException {
        if (keyPath != null && keyPath.length() > 0) {
            return SQLServerCertificateUtils.readPKCS8Certificate(certPath, keyPath, keyPassword);
        }
        return SQLServerCertificateUtils.readPKCS12Certificate(certPath, keyPassword);
    }

    static String parseCommonName(String distinguishedName) {
        int index = distinguishedName.indexOf("cn=");
        if (index == -1) {
            return null;
        }
        distinguishedName = distinguishedName.substring(index + 3);
        for (index = 0; index < distinguishedName.length() && distinguishedName.charAt(index) != ','; ++index) {
        }
        String commonName = distinguishedName.substring(0, index);
        if (commonName.length() > 1 && '\"' == commonName.charAt(0)) {
            commonName = '\"' == commonName.charAt(commonName.length() - 1) ? commonName.substring(1, commonName.length() - 1) : null;
        }
        return commonName;
    }

    static boolean validateServerName(String nameInCert, String hostName) {
        if (null == nameInCert) {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(logContext + " Failed to parse the name from the certificate or name is empty.");
            }
            return false;
        }
        if (!nameInCert.startsWith("xn--") && nameInCert.contains("*")) {
            int hostIndex = 0;
            int certIndex = 0;
            int match = 0;
            int startIndex = -1;
            int periodCount = 0;
            while (hostIndex < hostName.length()) {
                if ('.' == hostName.charAt(hostIndex)) {
                    ++periodCount;
                }
                if (certIndex < nameInCert.length() && hostName.charAt(hostIndex) == nameInCert.charAt(certIndex)) {
                    ++hostIndex;
                    ++certIndex;
                    continue;
                }
                if (certIndex < nameInCert.length() && '*' == nameInCert.charAt(certIndex)) {
                    startIndex = certIndex++;
                    match = hostIndex;
                    continue;
                }
                if (startIndex != -1 && 0 == periodCount) {
                    certIndex = startIndex + 1;
                    hostIndex = ++match;
                    continue;
                }
                SQLServerCertificateUtils.logFailMessage(nameInCert, hostName);
                return false;
            }
            if (nameInCert.length() == certIndex && periodCount > 1) {
                SQLServerCertificateUtils.logSuccessMessage(nameInCert, hostName);
                return true;
            }
            SQLServerCertificateUtils.logFailMessage(nameInCert, hostName);
            return false;
        }
        if (!nameInCert.equals(hostName)) {
            SQLServerCertificateUtils.logFailMessage(nameInCert, hostName);
            return false;
        }
        SQLServerCertificateUtils.logSuccessMessage(nameInCert, hostName);
        return true;
    }

    static void validateServerNameInCertificate(X509Certificate cert, String hostName) throws CertificateException {
        Collection<List<?>> sanCollection;
        String nameInCertDN = cert.getSubjectX500Principal().getName("canonical");
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(logContext + " Validating the server name:" + hostName);
            logger.finer(logContext + " The DN name in certificate:" + nameInCertDN);
        }
        String dnsNameInSANCert = "";
        String subjectCN = SQLServerCertificateUtils.parseCommonName(nameInCertDN);
        boolean isServerNameValidated = SQLServerCertificateUtils.validateServerName(subjectCN, hostName);
        if (!isServerNameValidated && (sanCollection = cert.getSubjectAlternativeNames()) != null) {
            for (List<?> sanEntry : sanCollection) {
                if (sanEntry != null && sanEntry.size() >= 2) {
                    Object key = sanEntry.get(0);
                    Object value = sanEntry.get(1);
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(logContext + "Key: " + key + "; KeyClass:" + (key != null ? key.getClass() : null) + ";value: " + value + "; valueClass:" + (value != null ? value.getClass() : null));
                    }
                    if (key == null || !(key instanceof Integer) || (Integer)key != 2) continue;
                    if (value != null && value instanceof String) {
                        dnsNameInSANCert = (String)value;
                        isServerNameValidated = SQLServerCertificateUtils.validateServerName(dnsNameInSANCert = dnsNameInSANCert.toLowerCase(Locale.ENGLISH), hostName);
                        if (isServerNameValidated) {
                            if (!logger.isLoggable(Level.FINER)) break;
                            logger.finer(logContext + " found a valid name in certificate: " + dnsNameInSANCert);
                            break;
                        }
                    }
                    if (!logger.isLoggable(Level.FINER)) continue;
                    logger.finer(logContext + " the following name in certificate does not match the serverName: " + value);
                    logger.finer(logContext + " certificate:\n" + cert.toString());
                    continue;
                }
                if (!logger.isLoggable(Level.FINER)) continue;
                logger.finer(logContext + " found an invalid san entry: " + sanEntry);
                logger.finer(logContext + " certificate:\n" + cert.toString());
            }
        }
        if (!isServerNameValidated) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_certNameFailed"));
            Object[] msgArgs = new Object[]{hostName, dnsNameInSANCert};
            throw new CertificateException(form.format(msgArgs));
        }
    }

    static void validateServerCerticate(X509Certificate cert, String certFile) throws CertificateException {
        try (InputStream is = SQLServerCertificateUtils.fileToStream(certFile);){
            if (!CertificateFactory.getInstance("X509").generateCertificate(is).equals(cert)) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_serverCertError"));
                Object[] msgArgs = new Object[]{certFile};
                throw new CertificateException(form.format(msgArgs));
            }
        }
        catch (Exception e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_serverCertError"));
            Object[] msgArgs = new Object[]{e.getMessage(), certFile, cert.toString()};
            throw new CertificateException(form.format(msgArgs));
        }
    }

    private static void logFailMessage(String nameInCert, String hostName) {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(logContext + " The name in certificate " + nameInCert + " does not match with the server name " + hostName + ".");
        }
    }

    private static void logSuccessMessage(String nameInCert, String hostName) {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(logContext + " The name in certificate:" + nameInCert + " validated against server name " + hostName + ".");
        }
    }

    static KeyStore loadPKCS12KeyStore(String certPath, String keyPassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, SQLServerException {
        KeyStore keyStore = KeyStore.getInstance(PKCS12_ALG);
        try (FileInputStream certStream = new FileInputStream(certPath);){
            keyStore.load(certStream, keyPassword != null ? keyPassword.toCharArray() : null);
        }
        catch (FileNotFoundException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_readCertError"), null, 0, null);
        }
        return keyStore;
    }

    static KeyManager[] readPKCS8Certificate(String certPath, String keyPath, String keyPassword) throws IOException, GeneralSecurityException, SQLServerException {
        Certificate clientCertificate = SQLServerCertificateUtils.loadCertificate(certPath);
        ((X509Certificate)clientCertificate).checkValidity();
        PrivateKey privateKey = SQLServerCertificateUtils.loadPrivateKey(keyPath, keyPassword);
        KeyStore keyStore = KeyStore.getInstance(JAVA_KEY_STORE);
        keyStore.load(null, null);
        keyStore.setCertificateEntry(CLIENT_CERT, clientCertificate);
        keyStore.setKeyEntry(CLIENT_KEY, privateKey, keyPassword.toCharArray(), new Certificate[]{clientCertificate});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyPassword.toCharArray());
        return kmf.getKeyManagers();
    }

    private static KeyManager[] readPKCS12Certificate(String certPath, String keyPassword) throws NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyStoreException, SQLServerException {
        KeyStore keyStore = SQLServerCertificateUtils.loadPKCS12KeyStore(certPath, keyPassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(SUN_X_509);
        keyManagerFactory.init(keyStore, keyPassword != null ? keyPassword.toCharArray() : null);
        return keyManagerFactory.getKeyManagers();
    }

    private static PrivateKey loadPrivateKeyFromPKCS8(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        StringBuilder sb = new StringBuilder(key);
        SQLServerCertificateUtils.deleteFirst(sb, PEM_PRIVATE_START);
        SQLServerCertificateUtils.deleteFirst(sb, PEM_PRIVATE_END);
        byte[] formattedKey = Base64.getDecoder().decode(sb.toString().replaceAll("\\s", ""));
        KeyFactory factory = KeyFactory.getInstance(RSA_ALG);
        return factory.generatePrivate(new PKCS8EncodedKeySpec(formattedKey));
    }

    private static void deleteFirst(StringBuilder sb, String str) {
        int i = sb.indexOf(str);
        if (i != -1) {
            sb.delete(i, i + str.length());
        }
    }

    private static PrivateKey loadPrivateKeyFromPKCS1(String key, String keyPass) throws IOException {
        SQLServerBouncyCastleLoader.loadBouncyCastle();
        try (PEMParser pemParser = new PEMParser((Reader)new StringReader(key));){
            KeyPair kp;
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            if (object instanceof PEMEncryptedKeyPair && keyPass != null) {
                PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(keyPass.toCharArray());
                kp = converter.getKeyPair(((PEMEncryptedKeyPair)object).decryptKeyPair(decProv));
            } else {
                kp = converter.getKeyPair((PEMKeyPair)object);
            }
            PrivateKey privateKey = kp.getPrivate();
            return privateKey;
        }
    }

    private static PrivateKey loadPrivateKeyFromPVK(String keyPath, String keyPass) throws IOException, GeneralSecurityException, SQLServerException {
        File f = new File(keyPath);
        ByteBuffer buffer = ByteBuffer.allocate((int)f.length());
        try (FileInputStream in = new FileInputStream(f);){
            PrivateKey privateKey;
            block14: {
                FileChannel channel = in.getChannel();
                try {
                    ((Buffer)buffer.order(ByteOrder.LITTLE_ENDIAN)).rewind();
                    long magic = (long)buffer.getInt() & 0xFFFFFFFFL;
                    if (2964713758L != magic) {
                        SQLServerException.makeFromDriverError(null, magic, SQLServerResource.getResource("R_pvkHeaderError"), "", false);
                    }
                    ((Buffer)buffer).position(buffer.position() + 8);
                    boolean encrypted = buffer.getInt() != 0;
                    int saltLength = buffer.getInt();
                    int keyLength = buffer.getInt();
                    byte[] salt = new byte[saltLength];
                    buffer.get(salt);
                    ((Buffer)buffer).position(buffer.position() + 8);
                    byte[] key = new byte[keyLength - 8];
                    buffer.get(key);
                    if (encrypted) {
                        MessageDigest digest = MessageDigest.getInstance("SHA1");
                        digest.update(salt);
                        if (null != keyPass) {
                            digest.update(keyPass.getBytes());
                        }
                        byte[] hash = digest.digest();
                        key = SQLServerCertificateUtils.getSecretKeyFromHash(key, hash);
                    }
                    ByteBuffer buff = ByteBuffer.wrap(key).order(ByteOrder.LITTLE_ENDIAN);
                    ((Buffer)buff).position(RSA2_MAGIC.length);
                    int byteLength = buff.getInt() / 8;
                    BigInteger publicExponent = BigInteger.valueOf(buff.getInt());
                    BigInteger modulus = SQLServerCertificateUtils.getBigInteger(buff, byteLength);
                    BigInteger prime1 = SQLServerCertificateUtils.getBigInteger(buff, byteLength / 2);
                    BigInteger prime2 = SQLServerCertificateUtils.getBigInteger(buff, byteLength / 2);
                    BigInteger primeExponent1 = SQLServerCertificateUtils.getBigInteger(buff, byteLength / 2);
                    BigInteger primeExponent2 = SQLServerCertificateUtils.getBigInteger(buff, byteLength / 2);
                    BigInteger crtCoefficient = SQLServerCertificateUtils.getBigInteger(buff, byteLength / 2);
                    BigInteger privateExponent = SQLServerCertificateUtils.getBigInteger(buff, byteLength);
                    RSAPrivateCrtKeySpec spec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, prime1, prime2, primeExponent1, primeExponent2, crtCoefficient);
                    KeyFactory factory = KeyFactory.getInstance(RSA_ALG);
                    privateKey = factory.generatePrivate(spec);
                    if (channel == null) break block14;
                }
                catch (Throwable throwable) {
                    if (channel != null) {
                        try {
                            channel.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                channel.close();
            }
            return privateKey;
        }
    }

    static Certificate loadCertificate(String certificatePem) throws IOException, GeneralSecurityException, SQLServerException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        try (InputStream certStream = SQLServerCertificateUtils.fileToStream(certificatePem);){
            Certificate certificate = certificateFactory.generateCertificate(certStream);
            return certificate;
        }
    }

    static PrivateKey loadPrivateKey(String privateKeyPemPath, String privateKeyPassword) throws GeneralSecurityException, IOException, SQLServerException {
        String privateKeyPem = SQLServerCertificateUtils.getStringFromFile(privateKeyPemPath);
        if (privateKeyPem.contains(PEM_PRIVATE_START)) {
            return SQLServerCertificateUtils.loadPrivateKeyFromPKCS8(privateKeyPem);
        }
        if (privateKeyPem.contains(PEM_RSA_PRIVATE_START)) {
            return SQLServerCertificateUtils.loadPrivateKeyFromPKCS1(privateKeyPem, privateKeyPassword);
        }
        return SQLServerCertificateUtils.loadPrivateKeyFromPVK(privateKeyPemPath, privateKeyPassword);
    }

    private static boolean startsWithMagic(byte[] b) {
        for (int i = 0; i < RSA2_MAGIC.length; ++i) {
            if (b[i] == RSA2_MAGIC[i]) continue;
            return false;
        }
        return true;
    }

    private static byte[] getSecretKeyFromHash(byte[] originalKey, byte[] keyHash) throws GeneralSecurityException, SQLServerException {
        SecretKeySpec key = new SecretKeySpec(keyHash, 0, 16, RC4_ALG);
        byte[] decrypted = SQLServerCertificateUtils.decryptSecretKey(key, originalKey);
        if (SQLServerCertificateUtils.startsWithMagic(decrypted)) {
            return decrypted;
        }
        Arrays.fill(keyHash, 5, keyHash.length, (byte)0);
        key = new SecretKeySpec(keyHash, 0, 16, RC4_ALG);
        decrypted = SQLServerCertificateUtils.decryptSecretKey(key, originalKey);
        if (SQLServerCertificateUtils.startsWithMagic(decrypted)) {
            return decrypted;
        }
        SQLServerException.makeFromDriverError(null, originalKey, SQLServerResource.getResource("R_pvkParseError"), "", false);
        return null;
    }

    private static byte[] decryptSecretKey(SecretKey key, byte[] encoded) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(2, key);
        return cipher.doFinal(encoded);
    }

    private static BigInteger getBigInteger(ByteBuffer buffer, int length) {
        byte[] array = new byte[length + 1];
        for (int i = 0; i < length; ++i) {
            array[array.length - 1 - i] = buffer.get();
        }
        return new BigInteger(array);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static InputStream fileToStream(String fname) throws IOException, SQLServerException {
        try (FileInputStream fis = new FileInputStream(fname);){
            ByteArrayInputStream byteArrayInputStream;
            try (DataInputStream dis = new DataInputStream(fis);){
                byte[] bytes = new byte[dis.available()];
                dis.readFully(bytes);
                byteArrayInputStream = new ByteArrayInputStream(bytes);
            }
            return byteArrayInputStream;
        }
        catch (FileNotFoundException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_readCertError"), null, 0, null);
        }
    }

    private static String getStringFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath, new String[0])));
    }
}

