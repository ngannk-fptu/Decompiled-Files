/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.util;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface AsymmetricKeyInfoConverter {
    public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException;

    public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException;
}

