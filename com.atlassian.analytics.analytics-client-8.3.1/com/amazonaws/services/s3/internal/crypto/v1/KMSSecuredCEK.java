/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.v1;

import com.amazonaws.services.s3.internal.crypto.v1.SecuredCEK;
import java.util.Map;

final class KMSSecuredCEK
extends SecuredCEK {
    static final String KEY_PROTECTION_MECHANISM_V1 = "kms";
    static final String KEY_PROTECTION_MECHANISM_V2 = "kms+context";

    KMSSecuredCEK(byte[] encryptedKeyBlob, Map<String, String> matdesc) {
        super(encryptedKeyBlob, KEY_PROTECTION_MECHANISM_V1, matdesc);
    }

    public static boolean isKMSKeyWrapped(String keyWrapAlgo) {
        return KMSSecuredCEK.isKMSV1KeyWrapped(keyWrapAlgo) || KMSSecuredCEK.isKMSV2KeyWrapped(keyWrapAlgo);
    }

    public static boolean isKMSV1KeyWrapped(String keyWrapAlgo) {
        return KEY_PROTECTION_MECHANISM_V1.equals(keyWrapAlgo);
    }

    public static boolean isKMSV2KeyWrapped(String keyWrapAlgo) {
        return KEY_PROTECTION_MECHANISM_V2.equals(keyWrapAlgo);
    }
}

