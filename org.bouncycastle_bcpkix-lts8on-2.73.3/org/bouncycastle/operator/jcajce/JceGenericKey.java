/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.operator.jcajce;

import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;

public class JceGenericKey
extends GenericKey {
    private static Object getRepresentation(Key key) {
        byte[] keyBytes = key.getEncoded();
        if (keyBytes != null) {
            return keyBytes;
        }
        return key;
    }

    public JceGenericKey(AlgorithmIdentifier algorithmIdentifier, Key representation) {
        super(algorithmIdentifier, JceGenericKey.getRepresentation(representation));
    }
}

