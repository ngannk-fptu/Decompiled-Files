/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.util.io.pem.PemGenerationException
 */
package org.bouncycastle.openssl.jcajce;

import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.io.pem.PemGenerationException;

public class JcaPKCS8Generator
extends PKCS8Generator {
    public JcaPKCS8Generator(PrivateKey key, OutputEncryptor encryptor) throws PemGenerationException {
        super(PrivateKeyInfo.getInstance((Object)key.getEncoded()), encryptor);
    }
}

