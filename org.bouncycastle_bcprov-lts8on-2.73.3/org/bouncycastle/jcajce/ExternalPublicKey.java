/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.PublicKey;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bc.ExternalValue;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.util.Arrays;

public class ExternalPublicKey
implements PublicKey {
    private final GeneralName location;
    private final AlgorithmIdentifier digestAlg;
    private final byte[] digest;

    public ExternalPublicKey(GeneralName location, AlgorithmIdentifier digestAlg, byte[] digest) {
        this.location = location;
        this.digestAlg = digestAlg;
        this.digest = Arrays.clone(digest);
    }

    public ExternalPublicKey(PublicKey key, GeneralName location, MessageDigest digest) {
        this(location, MessageDigestUtils.getDigestAlgID(digest.getAlgorithm()), digest.digest(key.getEncoded()));
    }

    public ExternalPublicKey(ExternalValue extKey) {
        this(extKey.getLocation(), extKey.getHashAlg(), extKey.getHashVal().getBytes());
    }

    @Override
    public String getAlgorithm() {
        return "ExternalKey";
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(BCObjectIdentifiers.external_value), new ExternalValue(this.location, this.digestAlg, this.digest)).getEncoded("DER");
        }
        catch (IOException e) {
            throw new IllegalStateException("unable to encode composite key: " + e.getMessage());
        }
    }
}

