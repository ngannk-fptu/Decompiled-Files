/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.AuthorityKeyIdentifier
 *  org.bouncycastle.asn1.x509.SubjectKeyIdentifier
 *  org.bouncycastle.crypto.digests.SHA1Digest
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory
 */
package org.bouncycastle.cert.bc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.operator.DigestCalculator;

public class BcX509ExtensionUtils
extends X509ExtensionUtils {
    public BcX509ExtensionUtils() {
        super(new SHA1DigestCalculator());
    }

    public BcX509ExtensionUtils(DigestCalculator calculator) {
        super(calculator);
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(AsymmetricKeyParameter publicKey) throws IOException {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo((AsymmetricKeyParameter)publicKey));
    }

    public SubjectKeyIdentifier createSubjectKeyIdentifier(AsymmetricKeyParameter publicKey) throws IOException {
        return super.createSubjectKeyIdentifier(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo((AsymmetricKeyParameter)publicKey));
    }

    private static class SHA1DigestCalculator
    implements DigestCalculator {
        private ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        private SHA1DigestCalculator() {
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }

        @Override
        public OutputStream getOutputStream() {
            return this.bOut;
        }

        @Override
        public byte[] getDigest() {
            byte[] bytes = this.bOut.toByteArray();
            this.bOut.reset();
            SHA1Digest sha1 = new SHA1Digest();
            sha1.update(bytes, 0, bytes.length);
            byte[] digest = new byte[sha1.getDigestSize()];
            sha1.doFinal(digest, 0);
            return digest;
        }
    }
}

