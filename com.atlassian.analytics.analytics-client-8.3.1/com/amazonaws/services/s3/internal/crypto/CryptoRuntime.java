/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal.crypto;

import com.amazonaws.services.s3.internal.crypto.ContentCryptoScheme;
import java.security.Provider;
import java.security.Security;
import javax.crypto.Cipher;
import org.apache.commons.logging.LogFactory;

public class CryptoRuntime {
    public static final String BOUNCY_CASTLE_PROVIDER = "BC";
    private static final String BC_PROVIDER_FQCN = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    public static synchronized boolean isBouncyCastleAvailable() {
        return Security.getProvider(BOUNCY_CASTLE_PROVIDER) != null;
    }

    public static synchronized void enableBouncyCastle() {
        if (CryptoRuntime.isBouncyCastleAvailable()) {
            return;
        }
        try {
            Class<?> c = Class.forName(BC_PROVIDER_FQCN);
            Provider provider = (Provider)c.newInstance();
            Security.addProvider(provider);
        }
        catch (Exception e) {
            LogFactory.getLog(CryptoRuntime.class).debug((Object)"Bouncy Castle not available", (Throwable)e);
        }
    }

    public static void recheck() {
        CryptoRuntime.recheckAesGcmAvailablility();
        CryptoRuntime.recheckRsaKeyWrapAvailablility();
    }

    public static boolean isAesGcmAvailable() {
        return AesGcm.isAvailable;
    }

    public static void recheckAesGcmAvailablility() {
        AesGcm.recheck();
    }

    public static boolean isRsaKeyWrapAvailable() {
        return RsaEcbOaepWithSHA256AndMGF1Padding.isAvailable;
    }

    private static void recheckRsaKeyWrapAvailablility() {
        RsaEcbOaepWithSHA256AndMGF1Padding.recheck();
    }

    private static final class RsaEcbOaepWithSHA256AndMGF1Padding {
        static volatile boolean isAvailable = RsaEcbOaepWithSHA256AndMGF1Padding.check();

        private RsaEcbOaepWithSHA256AndMGF1Padding() {
        }

        static boolean recheck() {
            isAvailable = RsaEcbOaepWithSHA256AndMGF1Padding.check();
            return isAvailable;
        }

        private static boolean check() {
            try {
                Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", CryptoRuntime.BOUNCY_CASTLE_PROVIDER);
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
    }

    private static final class AesGcm {
        static volatile boolean isAvailable = AesGcm.check();

        private AesGcm() {
        }

        static boolean recheck() {
            isAvailable = AesGcm.check();
            return isAvailable;
        }

        private static boolean check() {
            try {
                Cipher.getInstance(ContentCryptoScheme.AES_GCM.getCipherAlgorithm(), CryptoRuntime.BOUNCY_CASTLE_PROVIDER);
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
    }
}

