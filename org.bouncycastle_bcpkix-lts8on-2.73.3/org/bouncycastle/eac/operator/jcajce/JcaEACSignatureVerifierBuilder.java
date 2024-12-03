/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.eac.EACObjectIdentifiers
 */
package org.bouncycastle.eac.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.eac.operator.EACSignatureVerifier;
import org.bouncycastle.eac.operator.jcajce.DefaultEACHelper;
import org.bouncycastle.eac.operator.jcajce.EACHelper;
import org.bouncycastle.eac.operator.jcajce.NamedEACHelper;
import org.bouncycastle.eac.operator.jcajce.ProviderEACHelper;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;

public class JcaEACSignatureVerifierBuilder {
    private EACHelper helper = new DefaultEACHelper();

    public JcaEACSignatureVerifierBuilder setProvider(String providerName) {
        this.helper = new NamedEACHelper(providerName);
        return this;
    }

    public JcaEACSignatureVerifierBuilder setProvider(Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }

    public EACSignatureVerifier build(final ASN1ObjectIdentifier usageOid, PublicKey pubKey) throws OperatorCreationException {
        Signature sig;
        try {
            sig = this.helper.getSignature(usageOid);
            sig.initVerify(pubKey);
        }
        catch (NoSuchAlgorithmException e) {
            throw new OperatorCreationException("unable to find algorithm: " + e.getMessage(), e);
        }
        catch (NoSuchProviderException e) {
            throw new OperatorCreationException("unable to find provider: " + e.getMessage(), e);
        }
        catch (InvalidKeyException e) {
            throw new OperatorCreationException("invalid key: " + e.getMessage(), e);
        }
        final SignatureOutputStream sigStream = new SignatureOutputStream(sig);
        return new EACSignatureVerifier(){

            @Override
            public ASN1ObjectIdentifier getUsageIdentifier() {
                return usageOid;
            }

            @Override
            public OutputStream getOutputStream() {
                return sigStream;
            }

            @Override
            public boolean verify(byte[] expected) {
                try {
                    if (usageOid.on(EACObjectIdentifiers.id_TA_ECDSA)) {
                        try {
                            byte[] reencoded = JcaEACSignatureVerifierBuilder.derEncode(expected);
                            return sigStream.verify(reencoded);
                        }
                        catch (Exception e) {
                            return false;
                        }
                    }
                    return sigStream.verify(expected);
                }
                catch (SignatureException e) {
                    throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                }
            }
        };
    }

    private static byte[] derEncode(byte[] rawSign) throws IOException {
        int len = rawSign.length / 2;
        byte[] r = new byte[len];
        byte[] s = new byte[len];
        System.arraycopy(rawSign, 0, r, 0, len);
        System.arraycopy(rawSign, len, s, 0, len);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)new ASN1Integer(new BigInteger(1, r)));
        v.add((ASN1Encodable)new ASN1Integer(new BigInteger(1, s)));
        DERSequence seq = new DERSequence(v);
        return seq.getEncoded();
    }

    private static class SignatureOutputStream
    extends OutputStream {
        private Signature sig;

        SignatureOutputStream(Signature sig) {
            this.sig = sig;
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            try {
                this.sig.update(bytes, off, len);
            }
            catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            try {
                this.sig.update(bytes);
            }
            catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        @Override
        public void write(int b) throws IOException {
            try {
                this.sig.update((byte)b);
            }
            catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        boolean verify(byte[] expected) throws SignatureException {
            return this.sig.verify(expected);
        }
    }
}

