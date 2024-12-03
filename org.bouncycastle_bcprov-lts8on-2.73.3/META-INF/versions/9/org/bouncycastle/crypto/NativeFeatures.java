/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class NativeFeatures {
    private static final Logger LOG = Logger.getLogger(NativeFeatures.class.getName());

    NativeFeatures() {
    }

    static boolean hasCTRHardwareSupport() {
        try {
            return NativeFeatures.nativeCTR();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native ctr exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeCTR();

    static boolean hasCFBHardwareSupport() {
        try {
            return NativeFeatures.nativeCFB();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native cfb exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeCFB();

    static boolean hasCBCHardwareSupport() {
        try {
            return NativeFeatures.nativeCBC();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native cbc exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeCBC();

    static boolean hasAESHardwareSupport() {
        try {
            return NativeFeatures.nativeAES();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native aes exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeAES();

    static boolean hasGCMHardwareSupport() {
        try {
            return NativeFeatures.nativeGCM();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native gcm exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeGCM();

    static boolean hasCCMHardwareSupport() {
        try {
            return NativeFeatures.nativeCCM();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native ccm exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeCCM();

    static boolean hasHardwareRand() {
        try {
            return NativeFeatures.nativeRand();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native rand exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeRand();

    static boolean hasHardwareSeed() {
        try {
            return NativeFeatures.nativeSeed();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native seed exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeSeed();

    static boolean hasHardwareSHA() {
        try {
            return NativeFeatures.nativeSHA2();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native sha exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    static boolean hasMultiplyAcc() {
        try {
            return NativeFeatures.nativeMulAcc();
        }
        catch (UnsatisfiedLinkError ule) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "native multiply and accumulate exception: " + ule.getMessage(), ule);
            }
            return false;
        }
    }

    private static native boolean nativeSHA2();

    private static native boolean nativeMulAcc();

    private static native boolean nativeRSA();
}

