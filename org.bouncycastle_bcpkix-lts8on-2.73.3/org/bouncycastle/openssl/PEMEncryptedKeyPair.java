/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import java.io.IOException;
import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMKeyPairParser;
import org.bouncycastle.operator.OperatorCreationException;

public class PEMEncryptedKeyPair {
    private final String dekAlgName;
    private final byte[] iv;
    private final byte[] keyBytes;
    private final PEMKeyPairParser parser;

    PEMEncryptedKeyPair(String dekAlgName, byte[] iv, byte[] keyBytes, PEMKeyPairParser parser) {
        this.dekAlgName = dekAlgName;
        this.iv = iv;
        this.keyBytes = keyBytes;
        this.parser = parser;
    }

    public String getDekAlgName() {
        return this.dekAlgName;
    }

    public PEMKeyPair decryptKeyPair(PEMDecryptorProvider keyDecryptorProvider) throws IOException {
        try {
            PEMDecryptor keyDecryptor = keyDecryptorProvider.get(this.dekAlgName);
            return this.parser.parse(keyDecryptor.decrypt(this.keyBytes, this.iv));
        }
        catch (IOException e) {
            throw e;
        }
        catch (OperatorCreationException e) {
            throw new PEMException("cannot create extraction operator: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new PEMException("exception processing key pair: " + e.getMessage(), e);
        }
    }
}

