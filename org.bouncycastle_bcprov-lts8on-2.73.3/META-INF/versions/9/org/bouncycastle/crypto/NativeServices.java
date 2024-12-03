/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.util.Set;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface NativeServices {
    public static final String NRBG = "NRBG";
    public static final String DRBG = "DRBG";
    public static final String AES_ECB = "AES/ECB";
    public static final String AES_GCM = "AES/GCM";
    public static final String AES_CBC = "AES/CBC";
    public static final String AES_CFB = "AES/CFB";
    public static final String AES_CTR = "AES/CTR";
    public static final String AES_CCM = "AES/CCM";
    public static final String SHA2 = "SHA2";
    public static final String MULACC = "MULACC";
    public static final String NONE = "NONE";

    public String getStatusMessage();

    public Set<String> getFeatureSet();

    public String getVariant();

    public String[][] getVariantSelectionMatrix();

    public boolean hasService(String var1);

    public String getBuildDate();

    public String getLibraryIdent();

    public boolean isEnabled();

    public boolean isInstalled();

    public boolean isSupported();
}

