/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CipherKeyGenerator
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.util.AlgorithmIdentifierFactory
 *  org.bouncycastle.crypto.util.CipherFactory
 *  org.bouncycastle.crypto.util.CipherKeyGeneratorFactory
 */
package org.bouncycastle.cert.crmf.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.AlgorithmIdentifierFactory;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.crypto.util.CipherKeyGeneratorFactory;

class CRMFHelper {
    CRMFHelper() {
    }

    CipherKeyGenerator createKeyGenerator(ASN1ObjectIdentifier algorithm, SecureRandom random) throws CRMFException {
        try {
            return CipherKeyGeneratorFactory.createKeyGenerator((ASN1ObjectIdentifier)algorithm, (SecureRandom)random);
        }
        catch (IllegalArgumentException e) {
            throw new CRMFException(e.getMessage(), e);
        }
    }

    static Object createContentCipher(boolean forEncryption, CipherParameters encKey, AlgorithmIdentifier encryptionAlgID) throws CRMFException {
        try {
            return CipherFactory.createContentCipher((boolean)forEncryption, (CipherParameters)encKey, (AlgorithmIdentifier)encryptionAlgID);
        }
        catch (IllegalArgumentException e) {
            throw new CRMFException(e.getMessage(), e);
        }
    }

    AlgorithmIdentifier generateEncryptionAlgID(ASN1ObjectIdentifier encryptionOID, KeyParameter encKey, SecureRandom random) throws CRMFException {
        try {
            return AlgorithmIdentifierFactory.generateEncryptionAlgID((ASN1ObjectIdentifier)encryptionOID, (int)(encKey.getKey().length * 8), (SecureRandom)random);
        }
        catch (IllegalArgumentException e) {
            throw new CRMFException(e.getMessage(), e);
        }
    }
}

