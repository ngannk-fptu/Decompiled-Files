/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.params.KeyParameter
 */
package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;

class AESUtil {
    AESUtil() {
    }

    static AlgorithmIdentifier determineKeyEncAlg(KeyParameter key) {
        ASN1ObjectIdentifier wrapOid;
        int length = key.getKey().length * 8;
        if (length == 128) {
            wrapOid = NISTObjectIdentifiers.id_aes128_wrap;
        } else if (length == 192) {
            wrapOid = NISTObjectIdentifiers.id_aes192_wrap;
        } else if (length == 256) {
            wrapOid = NISTObjectIdentifiers.id_aes256_wrap;
        } else {
            throw new IllegalArgumentException("illegal keysize in AES");
        }
        return new AlgorithmIdentifier(wrapOid);
    }
}

