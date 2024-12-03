/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl.bc;

import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PasswordException;
import org.bouncycastle.openssl.bc.PEMUtilities;

public class BcPEMDecryptorProvider
implements PEMDecryptorProvider {
    private final char[] password;

    public BcPEMDecryptorProvider(char[] password) {
        this.password = password;
    }

    @Override
    public PEMDecryptor get(final String dekAlgName) {
        return new PEMDecryptor(){

            @Override
            public byte[] decrypt(byte[] keyBytes, byte[] iv) throws PEMException {
                if (BcPEMDecryptorProvider.this.password == null) {
                    throw new PasswordException("Password is null, but a password is required");
                }
                return PEMUtilities.crypt(false, keyBytes, BcPEMDecryptorProvider.this.password, dekAlgName, iv);
            }
        };
    }
}

