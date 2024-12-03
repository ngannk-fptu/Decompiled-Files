/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.openssl.PEMDecryptorProvider
 *  org.bouncycastle.openssl.PEMEncryptedKeyPair
 *  org.bouncycastle.openssl.PEMKeyPair
 *  org.bouncycastle.openssl.PEMParser
 *  org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
 *  org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder
 *  org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder
 *  org.bouncycastle.operator.InputDecryptorProvider
 *  org.bouncycastle.operator.OperatorCreationException
 *  org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
 *  org.bouncycastle.pkcs.PKCSException
 */
package io.netty.handler.ssl;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

final class BouncyCastlePemReader {
    private static final String BC_PROVIDER = "org.bouncycastle.jce.provider.BouncyCastleProvider";
    private static final String BC_PEMPARSER = "org.bouncycastle.openssl.PEMParser";
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(BouncyCastlePemReader.class);
    private static volatile Throwable unavailabilityCause;
    private static volatile Provider bcProvider;
    private static volatile boolean attemptedLoading;

    public static boolean hasAttemptedLoading() {
        return attemptedLoading;
    }

    public static boolean isAvailable() {
        if (!BouncyCastlePemReader.hasAttemptedLoading()) {
            BouncyCastlePemReader.tryLoading();
        }
        return unavailabilityCause == null;
    }

    public static Throwable unavailabilityCause() {
        return unavailabilityCause;
    }

    private static void tryLoading() {
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                try {
                    ClassLoader classLoader = this.getClass().getClassLoader();
                    Class<?> bcProviderClass = Class.forName(BouncyCastlePemReader.BC_PROVIDER, true, classLoader);
                    Class.forName(BouncyCastlePemReader.BC_PEMPARSER, true, classLoader);
                    bcProvider = (Provider)bcProviderClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                    logger.debug("Bouncy Castle provider available");
                    attemptedLoading = true;
                }
                catch (Throwable e) {
                    logger.debug("Cannot load Bouncy Castle provider", e);
                    unavailabilityCause = e;
                    attemptedLoading = true;
                }
                return null;
            }
        });
    }

    public static PrivateKey getPrivateKey(InputStream keyInputStream, String keyPassword) {
        if (!BouncyCastlePemReader.isAvailable()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Bouncy castle provider is unavailable.", BouncyCastlePemReader.unavailabilityCause());
            }
            return null;
        }
        try {
            PEMParser parser = BouncyCastlePemReader.newParser(keyInputStream);
            return BouncyCastlePemReader.getPrivateKey(parser, keyPassword);
        }
        catch (Exception e) {
            logger.debug("Unable to extract private key", e);
            return null;
        }
    }

    public static PrivateKey getPrivateKey(File keyFile, String keyPassword) {
        if (!BouncyCastlePemReader.isAvailable()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Bouncy castle provider is unavailable.", BouncyCastlePemReader.unavailabilityCause());
            }
            return null;
        }
        try {
            PEMParser parser = BouncyCastlePemReader.newParser(keyFile);
            return BouncyCastlePemReader.getPrivateKey(parser, keyPassword);
        }
        catch (Exception e) {
            logger.debug("Unable to extract private key", e);
            return null;
        }
    }

    private static JcaPEMKeyConverter newConverter() {
        return new JcaPEMKeyConverter().setProvider(bcProvider);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static PrivateKey getPrivateKey(PEMParser pemParser, String keyPassword) throws IOException, PKCSException, OperatorCreationException {
        try {
            JcaPEMKeyConverter converter = BouncyCastlePemReader.newConverter();
            PrivateKey pk = null;
            Object object = pemParser.readObject();
            while (object != null && pk == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Parsed PEM object of type {} and assume key is {}encrypted", (Object)object.getClass().getName(), (Object)(keyPassword == null ? "not " : ""));
                }
                if (keyPassword == null) {
                    if (object instanceof PrivateKeyInfo) {
                        pk = converter.getPrivateKey((PrivateKeyInfo)object);
                    } else if (object instanceof PEMKeyPair) {
                        pk = converter.getKeyPair((PEMKeyPair)object).getPrivate();
                    } else {
                        logger.debug("Unable to handle PEM object of type {} as a non encrypted key", (Object)object.getClass());
                    }
                } else if (object instanceof PEMEncryptedKeyPair) {
                    PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().setProvider(bcProvider).build(keyPassword.toCharArray());
                    pk = converter.getKeyPair(((PEMEncryptedKeyPair)object).decryptKeyPair(decProv)).getPrivate();
                } else if (object instanceof PKCS8EncryptedPrivateKeyInfo) {
                    InputDecryptorProvider pkcs8InputDecryptorProvider = new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider(bcProvider).build(keyPassword.toCharArray());
                    pk = converter.getPrivateKey(((PKCS8EncryptedPrivateKeyInfo)object).decryptPrivateKeyInfo(pkcs8InputDecryptorProvider));
                } else {
                    logger.debug("Unable to handle PEM object of type {} as a encrypted key", (Object)object.getClass());
                }
                if (pk != null) continue;
                object = pemParser.readObject();
            }
            if (pk == null && logger.isDebugEnabled()) {
                logger.debug("No key found");
            }
            PrivateKey privateKey = pk;
            return privateKey;
        }
        finally {
            if (pemParser != null) {
                try {
                    pemParser.close();
                }
                catch (Exception exception) {
                    logger.debug("Failed closing pem parser", exception);
                }
            }
        }
    }

    private static PEMParser newParser(File keyFile) throws FileNotFoundException {
        return new PEMParser((Reader)new FileReader(keyFile));
    }

    private static PEMParser newParser(InputStream keyInputStream) {
        return new PEMParser((Reader)new InputStreamReader(keyInputStream, CharsetUtil.US_ASCII));
    }

    private BouncyCastlePemReader() {
    }
}

