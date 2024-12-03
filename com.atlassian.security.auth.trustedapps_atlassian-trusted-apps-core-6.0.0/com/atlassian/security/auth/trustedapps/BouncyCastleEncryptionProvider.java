/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 *  org.bouncycastle.util.encoders.Base64
 *  org.bouncycastle.util.encoders.DecoderException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.BaseEncryptionProvider;
import com.atlassian.security.auth.trustedapps.Clock;
import com.atlassian.security.auth.trustedapps.DefaultApplicationCertificate;
import com.atlassian.security.auth.trustedapps.DefaultEncryptedCertificate;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.SystemClock;
import com.atlassian.security.auth.trustedapps.SystemException;
import com.atlassian.security.auth.trustedapps.Transcoder;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import com.atlassian.security.auth.trustedapps.UnableToVerifySignatureException;
import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.TimeUnit;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BouncyCastleEncryptionProvider
extends BaseEncryptionProvider {
    private static final Logger log = LoggerFactory.getLogger(BouncyCastleEncryptionProvider.class);
    public static final Provider PROVIDER = new BouncyCastleProvider();
    private static final String STREAM_CIPHER = "RC4";
    private static final String ASYM_CIPHER = "RSA/NONE/NoPadding";
    private static final String ASYM_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String UTF8 = "utf-8";
    private final Clock clock;
    private final SecretKeyFactory secretKeyFactory;
    private final Transcoder transcoder;

    public BouncyCastleEncryptionProvider() {
        this(new ValidatingSecretKeyFactory(new BCKeyFactory(), new TransmissionValidator()), new Transcoder.Base64Transcoder(), new SystemClock());
    }

    private BouncyCastleEncryptionProvider(SecretKeyFactory secretKeyFactory, Transcoder transcoder, Clock clock) {
        Null.not("secretKeyFactory", secretKeyFactory);
        Null.not("transcoder", transcoder);
        Null.not("clock", clock);
        this.secretKeyFactory = secretKeyFactory;
        this.transcoder = transcoder;
        this.clock = clock;
    }

    @VisibleForTesting
    BouncyCastleEncryptionProvider(Clock clock) {
        this(new ValidatingSecretKeyFactory(new BCKeyFactory(), new TransmissionValidator()), new Transcoder.Base64Transcoder(), clock);
    }

    @Override
    public PublicKey toPublicKey(byte[] encodedForm) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encodedForm);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGORITHM, PROVIDER);
        return keyFactory.generatePublic(pubKeySpec);
    }

    @Override
    public PrivateKey toPrivateKey(byte[] encodedForm) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        PKCS8EncodedKeySpec pubKeySpec = new PKCS8EncodedKeySpec(encodedForm);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGORITHM, PROVIDER);
        return keyFactory.generatePrivate(pubKeySpec);
    }

    @Override
    public KeyPair generateNewKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(ASYM_ALGORITHM, PROVIDER);
        return gen.generateKeyPair();
    }

    @Override
    public ApplicationCertificate decodeEncryptedCertificate(EncryptedCertificate encCert, PublicKey publicKey, String appId) throws InvalidCertificateException {
        BufferedReader in;
        try {
            in = TrustedApplicationUtils.Constant.VERSION_TWO.equals(TrustedApplicationUtils.getProtocolVersionInUse()) ? this.getV2CertificateReader(encCert, publicKey, appId) : this.getV3CertificateReader(encCert);
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError((Object)e);
        }
        catch (NoSuchPaddingException e) {
            throw new AssertionError((Object)e);
        }
        catch (NumberFormatException e) {
            throw new SystemException(appId, (Exception)e);
        }
        catch (IllegalBlockSizeException e) {
            throw new SystemException(appId, (Exception)e);
        }
        catch (BadPaddingException e) {
            throw new SystemException(appId, (Exception)e);
        }
        catch (InvalidKeyException e) {
            throw new InvalidCertificateException(new TransportErrorMessage.BadMagicNumber("secret key", appId));
        }
        catch (DecoderException e) {
            throw new InvalidCertificateException(new TransportErrorMessage.BadMagicNumber("secret key", appId));
        }
        catch (SecurityException e) {
            throw new InvalidCertificateException(new TransportErrorMessage.BadMagicNumber("secret key", appId));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            String created = in.readLine();
            String userName = in.readLine();
            TrustedApplicationUtils.validateMagicNumber("secret key", appId, encCert.getProtocolVersion(), in.readLine());
            in.close();
            long timeCreated = Long.parseLong(created);
            return new DefaultApplicationCertificate(appId, userName, timeCreated, encCert.getProtocolVersion());
        }
        catch (NumberFormatException e) {
            throw new SystemException(appId, (Exception)e);
        }
        catch (CharConversionException e) {
            throw new SystemException(appId, (Exception)e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader getV3CertificateReader(EncryptedCertificate encCert) throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(this.transcoder.decode(encCert.getCertificate())), UTF8));
    }

    private BufferedReader getV2CertificateReader(EncryptedCertificate encCert, PublicKey publicKey, String appId) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidCertificateException {
        Cipher asymCipher = Cipher.getInstance(ASYM_CIPHER, PROVIDER);
        asymCipher.init(2, publicKey);
        String encryptedMagicNumber = encCert.getMagicNumber();
        if (encryptedMagicNumber != null) {
            String magicNumber = new String(asymCipher.doFinal(this.transcoder.decode(encryptedMagicNumber)), UTF8);
            TrustedApplicationUtils.validateMagicNumber("public key", appId, encCert.getProtocolVersion(), magicNumber);
        } else if (encCert.getProtocolVersion() != null) {
            throw new InvalidCertificateException(new TransportErrorMessage.BadMagicNumber("public key", appId));
        }
        byte[] secretKeyData = asymCipher.doFinal(this.transcoder.decode(encCert.getSecretKey()));
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyData, STREAM_CIPHER);
        Cipher symCipher = Cipher.getInstance(STREAM_CIPHER, PROVIDER);
        symCipher.init(2, secretKeySpec);
        byte[] decryptedData = symCipher.doFinal(this.transcoder.decode(encCert.getCertificate()));
        return new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(decryptedData), UTF8));
    }

    @Override
    public EncryptedCertificate createEncryptedCertificate(String userName, PrivateKey privateKey, String appId) {
        return this.createEncryptedCertificate(userName, privateKey, appId, null);
    }

    @Override
    public EncryptedCertificate createEncryptedCertificate(String userName, PrivateKey privateKey, String appId, String urlToSign) {
        try {
            if (TrustedApplicationUtils.Constant.VERSION_TWO.equals(TrustedApplicationUtils.getProtocolVersionInUse())) {
                return this.generateV2EncryptedCertificate(privateKey, appId, userName, urlToSign);
            }
            return this.generateV3EncryptedCertificate(privateKey, appId, userName, urlToSign);
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError((Object)e);
        }
        catch (NoSuchPaddingException e) {
            throw new AssertionError((Object)e);
        }
        catch (InvalidKeyException e) {
            throw new IllegalKeyException(e);
        }
        catch (IllegalBlockSizeException e) {
            throw new IllegalKeyException(e);
        }
        catch (BadPaddingException e) {
            throw new IllegalKeyException(e);
        }
        catch (SignatureException e) {
            throw new IllegalKeyException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalKeyException(e);
        }
    }

    private EncryptedCertificate generateV3EncryptedCertificate(PrivateKey privateKey, String appId, String username, String urlToSign) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        SignatureString certificate = this.generateCertificate(username);
        String signature = this.generateSignature(privateKey, TrustedApplicationUtils.generateSignatureBaseString(certificate.getTimeStamp(), urlToSign, username));
        return new DefaultEncryptedCertificate(appId, null, this.transcoder.encode(certificate.getSignature().getBytes(UTF8)), TrustedApplicationUtils.Constant.VERSION_THREE, null, signature);
    }

    private EncryptedCertificate generateV2EncryptedCertificate(PrivateKey privateKey, String appId, String username, String urlToSign) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, SignatureException {
        SecretKey secretKey = this.secretKeyFactory.generateSecretKey();
        Cipher symmetricCipher = Cipher.getInstance(STREAM_CIPHER, PROVIDER);
        symmetricCipher.init(1, secretKey);
        Cipher asymCipher = Cipher.getInstance(ASYM_CIPHER, PROVIDER);
        asymCipher.init(1, privateKey);
        String encryptedKey = this.transcoder.encode(asymCipher.doFinal(secretKey.getEncoded()));
        String encryptedMagic = this.transcoder.encode(asymCipher.doFinal(this.transcoder.getBytes(TrustedApplicationUtils.Constant.MAGIC)));
        SignatureString certificate = this.generateCertificate(username);
        String signature = this.generateSignature(privateKey, TrustedApplicationUtils.generateSignatureBaseString(certificate.getTimeStamp(), urlToSign, username));
        String encryptedCertificate = this.transcoder.encode(symmetricCipher.doFinal(this.transcoder.getBytes(certificate.getSignature())));
        return new DefaultEncryptedCertificate(appId, encryptedKey, encryptedCertificate, TrustedApplicationUtils.Constant.VERSION_TWO, encryptedMagic, signature);
    }

    private SignatureString generateCertificate(String userName) throws IllegalBlockSizeException, BadPaddingException {
        long timeStamp = this.clock.currentTimeMillis() + TimeUnit.SECONDS.toMillis(900L);
        return new SignatureString(userName, timeStamp);
    }

    @Override
    public String generateSignature(PrivateKey privateKey, byte[] signatureBaseString) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        Signature algo = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
        algo.initSign(privateKey);
        algo.update(signatureBaseString);
        String signature = this.transcoder.encode(algo.sign());
        log.debug("Signature for request to '{}' is '{}'", (Object)signatureBaseString, (Object)signature);
        return signature;
    }

    @Override
    public boolean verifySignature(PublicKey publicKey, byte[] signatureBaseString, String signatureToVerify) throws UnableToVerifySignatureException {
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
            sig.initVerify(publicKey);
            sig.update(signatureBaseString);
            return sig.verify(Base64.decode((String)signatureToVerify));
        }
        catch (InvalidKeyException e) {
            throw new UnableToVerifySignatureException(e);
        }
        catch (SignatureException e) {
            throw new UnableToVerifySignatureException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new UnableToVerifySignatureException(e);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new UnableToVerifySignatureException(e);
        }
    }

    static class IllegalKeyException
    extends IllegalArgumentException {
        IllegalKeyException(Exception ex) {
            super(ex.toString());
            this.initCause(ex);
        }
    }

    static class TransmissionValidator
    implements SecretKeyValidator {
        TransmissionValidator() {
        }

        @Override
        public boolean isValid(SecretKey secretKey) {
            byte[] encoded = secretKey.getEncoded();
            if (encoded.length != 16) {
                return false;
            }
            return encoded[0] != 0;
        }
    }

    static interface SecretKeyValidator {
        public boolean isValid(SecretKey var1);
    }

    static class ValidatingSecretKeyFactory
    implements SecretKeyFactory {
        private final SecretKeyFactory delegate;
        private final SecretKeyValidator validator;

        ValidatingSecretKeyFactory(SecretKeyFactory secretKeyFactory, SecretKeyValidator validator) {
            this.delegate = secretKeyFactory;
            this.validator = validator;
        }

        @Override
        public SecretKey generateSecretKey() {
            SecretKey result = this.delegate.generateSecretKey();
            while (!this.validator.isValid(result)) {
                result = this.delegate.generateSecretKey();
            }
            return result;
        }
    }

    static class BCKeyFactory
    implements SecretKeyFactory {
        BCKeyFactory() {
        }

        @Override
        public SecretKey generateSecretKey() {
            try {
                return KeyGenerator.getInstance(BouncyCastleEncryptionProvider.STREAM_CIPHER, PROVIDER).generateKey();
            }
            catch (NoSuchAlgorithmException e) {
                throw new AssertionError((Object)e);
            }
        }
    }

    static interface SecretKeyFactory {
        public SecretKey generateSecretKey();
    }

    private static class SignatureString {
        private final String userName;
        private final long timeStamp;
        private final String signature;

        public SignatureString(String userName, long timeStamp) {
            this.userName = userName;
            this.timeStamp = timeStamp;
            this.signature = this.generateSignature();
        }

        private String generateSignature() {
            StringWriter writer = new StringWriter();
            writer.write(Long.toString(this.timeStamp));
            writer.write(10);
            writer.write(this.userName);
            writer.write(10);
            writer.write(TrustedApplicationUtils.Constant.MAGIC);
            writer.flush();
            return writer.toString();
        }

        public long getTimeStamp() {
            return this.timeStamp;
        }

        public String getUserName() {
            return this.userName;
        }

        public String getSignature() {
            return this.signature;
        }
    }
}

