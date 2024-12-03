/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.params.KeyParameter
 */
package org.bouncycastle.cms.bc;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.GenericKey;

class CMSUtils {
    CMSUtils() {
    }

    static CipherParameters getBcKey(GenericKey key) {
        if (key.getRepresentation() instanceof CipherParameters) {
            return (CipherParameters)key.getRepresentation();
        }
        if (key.getRepresentation() instanceof byte[]) {
            return new KeyParameter((byte[])key.getRepresentation());
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
}

