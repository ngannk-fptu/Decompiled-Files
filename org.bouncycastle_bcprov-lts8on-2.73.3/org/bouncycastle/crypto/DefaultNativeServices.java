/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import org.bouncycastle.crypto.NativeFeatures;
import org.bouncycastle.crypto.NativeLibIdentity;
import org.bouncycastle.crypto.NativeLoader;
import org.bouncycastle.crypto.NativeServices;
import org.bouncycastle.crypto.VariantSelector;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class DefaultNativeServices
implements NativeServices {
    private static Set<String> nativeFeatures = null;

    DefaultNativeServices() {
    }

    @Override
    public String getStatusMessage() {
        if (NativeLoader.isNativeLibsAvailableForSystem()) {
            if (NativeLoader.isNativeInstalled()) {
                return "READY";
            }
            return NativeLoader.getNativeStatusMessage();
        }
        return "UNSUPPORTED";
    }

    @Override
    public Set<String> getFeatureSet() {
        return DefaultNativeServices.getNativeFeatureSet();
    }

    @Override
    public String getVariant() {
        return NativeLoader.getSelectedVariant();
    }

    @Override
    public String[][] getVariantSelectionMatrix() {
        return VariantSelector.getFeatureMatrix();
    }

    @Override
    public boolean hasService(String feature) {
        if (nativeFeatures == null) {
            nativeFeatures = NativeLoader.isJavaSupportOnly() ? Collections.singleton("NONE") : DefaultNativeServices.getNativeFeatureSet();
        }
        return nativeFeatures.contains(feature);
    }

    @Override
    public String getBuildDate() {
        return NativeLibIdentity.getNativeBuiltTimeStamp();
    }

    @Override
    public String getLibraryIdent() {
        return NativeLibIdentity.getLibraryIdent();
    }

    @Override
    public boolean isEnabled() {
        return NativeLoader.isNativeAvailable();
    }

    @Override
    public boolean isInstalled() {
        return NativeLoader.isNativeInstalled();
    }

    @Override
    public boolean isSupported() {
        return NativeLoader.isNativeLibsAvailableForSystem();
    }

    static Set<String> getNativeFeatureSet() {
        TreeSet<String> set = new TreeSet<String>();
        if (!NativeLoader.isJavaSupportOnly()) {
            if (NativeFeatures.hasHardwareSeed()) {
                set.add("NRBG");
            }
            if (NativeFeatures.hasHardwareRand()) {
                set.add("DRBG");
            }
            if (NativeFeatures.hasAESHardwareSupport()) {
                set.add("AES/ECB");
            }
            if (NativeFeatures.hasGCMHardwareSupport()) {
                set.add("AES/GCM");
            }
            if (NativeFeatures.hasCBCHardwareSupport()) {
                set.add("AES/CBC");
            }
            if (NativeFeatures.hasCFBHardwareSupport()) {
                set.add("AES/CFB");
            }
            if (NativeFeatures.hasCTRHardwareSupport()) {
                set.add("AES/CTR");
            }
            if (NativeFeatures.hasHardwareSHA()) {
                set.add("SHA2");
            }
            if (NativeFeatures.hasCCMHardwareSupport()) {
                set.add("AES/CCM");
            }
            if (NativeFeatures.hasMultiplyAcc()) {
                set.add("MULACC");
            }
        }
        if (set.isEmpty()) {
            set.add("NONE");
        }
        return Collections.unmodifiableSet(set);
    }
}

