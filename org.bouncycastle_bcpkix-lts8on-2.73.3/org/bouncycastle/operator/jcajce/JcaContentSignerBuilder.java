/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.misc.MiscObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.pkcs.RSASSAPSSparams
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.CompositePrivateKey
 *  org.bouncycastle.jcajce.io.OutputStreamFactory
 *  org.bouncycastle.jcajce.spec.CompositeAlgorithmSpec
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.io.TeeOutputStream
 */
package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.spec.CompositeAlgorithmSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.operator.jcajce.OperatorHelper;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.TeeOutputStream;

public class JcaContentSignerBuilder {
    private static final Set isAlgIdFromPrivate = new HashSet();
    private final String signatureAlgorithm;
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    private SecureRandom random;
    private AlgorithmIdentifier sigAlgId;
    private AlgorithmParameterSpec sigAlgSpec;

    public JcaContentSignerBuilder(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public JcaContentSignerBuilder(String signatureAlgorithm, AlgorithmParameterSpec sigParamSpec) {
        this.signatureAlgorithm = signatureAlgorithm;
        if (sigParamSpec instanceof PSSParameterSpec) {
            PSSParameterSpec pssSpec = (PSSParameterSpec)sigParamSpec;
            this.sigAlgSpec = pssSpec;
            this.sigAlgId = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, (ASN1Encodable)JcaContentSignerBuilder.createPSSParams(pssSpec));
        } else if (sigParamSpec instanceof CompositeAlgorithmSpec) {
            CompositeAlgorithmSpec compSpec = (CompositeAlgorithmSpec)sigParamSpec;
            this.sigAlgSpec = compSpec;
            this.sigAlgId = new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite, (ASN1Encodable)JcaContentSignerBuilder.createCompParams(compSpec));
        } else {
            throw new IllegalArgumentException("unknown sigParamSpec: " + (sigParamSpec == null ? "null" : sigParamSpec.getClass().getName()));
        }
    }

    public JcaContentSignerBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaContentSignerBuilder setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public JcaContentSignerBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public ContentSigner build(PrivateKey privateKey) throws OperatorCreationException {
        if (privateKey instanceof CompositePrivateKey) {
            return this.buildComposite((CompositePrivateKey)privateKey);
        }
        try {
            if (this.sigAlgSpec == null) {
                if (isAlgIdFromPrivate.contains(Strings.toUpperCase((String)this.signatureAlgorithm))) {
                    this.sigAlgId = PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()).getPrivateKeyAlgorithm();
                    this.sigAlgSpec = null;
                } else {
                    this.sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(this.signatureAlgorithm);
                    this.sigAlgSpec = null;
                }
            }
            final AlgorithmIdentifier signatureAlgId = this.sigAlgId;
            final Signature sig = this.helper.createSignature(this.sigAlgId);
            if (this.random != null) {
                sig.initSign(privateKey, this.random);
            } else {
                sig.initSign(privateKey);
            }
            return new ContentSigner(){
                private OutputStream stream;
                {
                    this.stream = OutputStreamFactory.createStream((Signature)sig);
                }

                @Override
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return signatureAlgId;
                }

                @Override
                public OutputStream getOutputStream() {
                    return this.stream;
                }

                @Override
                public byte[] getSignature() {
                    try {
                        return sig.sign();
                    }
                    catch (SignatureException e) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                    }
                }
            };
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create signer: " + e.getMessage(), e);
        }
    }

    private ContentSigner buildComposite(CompositePrivateKey privateKey) throws OperatorCreationException {
        try {
            List privateKeys = privateKey.getPrivateKeys();
            ASN1Sequence sigAlgIds = ASN1Sequence.getInstance((Object)this.sigAlgId.getParameters());
            final Signature[] sigs = new Signature[sigAlgIds.size()];
            for (int i = 0; i != sigAlgIds.size(); ++i) {
                sigs[i] = this.helper.createSignature(AlgorithmIdentifier.getInstance((Object)sigAlgIds.getObjectAt(i)));
                if (this.random != null) {
                    sigs[i].initSign((PrivateKey)privateKeys.get(i), this.random);
                    continue;
                }
                sigs[i].initSign((PrivateKey)privateKeys.get(i));
            }
            OutputStream sStream = OutputStreamFactory.createStream((Signature)sigs[0]);
            for (int i = 1; i != sigs.length; ++i) {
                sStream = new TeeOutputStream(sStream, OutputStreamFactory.createStream((Signature)sigs[i]));
            }
            final OutputStream sigStream = sStream;
            return new ContentSigner(){
                OutputStream stream;
                {
                    this.stream = sigStream;
                }

                @Override
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return JcaContentSignerBuilder.this.sigAlgId;
                }

                @Override
                public OutputStream getOutputStream() {
                    return this.stream;
                }

                @Override
                public byte[] getSignature() {
                    try {
                        ASN1EncodableVector sigV = new ASN1EncodableVector();
                        for (int i = 0; i != sigs.length; ++i) {
                            sigV.add((ASN1Encodable)new DERBitString(sigs[i].sign()));
                        }
                        return new DERSequence(sigV).getEncoded("DER");
                    }
                    catch (IOException e) {
                        throw new RuntimeOperatorException("exception encoding signature: " + e.getMessage(), e);
                    }
                    catch (SignatureException e) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                    }
                }
            };
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create signer: " + e.getMessage(), e);
        }
    }

    private static RSASSAPSSparams createPSSParams(PSSParameterSpec pssSpec) {
        AlgorithmIdentifier mgfDig;
        DefaultDigestAlgorithmIdentifierFinder digFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier digId = digFinder.find(pssSpec.getDigestAlgorithm());
        if (digId.getParameters() == null) {
            digId = new AlgorithmIdentifier(digId.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE);
        }
        if ((mgfDig = digFinder.find(((MGF1ParameterSpec)pssSpec.getMGFParameters()).getDigestAlgorithm())).getParameters() == null) {
            mgfDig = new AlgorithmIdentifier(mgfDig.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE);
        }
        return new RSASSAPSSparams(digId, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)mgfDig), new ASN1Integer((long)pssSpec.getSaltLength()), new ASN1Integer((long)pssSpec.getTrailerField()));
    }

    private static ASN1Sequence createCompParams(CompositeAlgorithmSpec compSpec) {
        DefaultSignatureAlgorithmIdentifierFinder algFinder = new DefaultSignatureAlgorithmIdentifierFinder();
        ASN1EncodableVector v = new ASN1EncodableVector();
        List algorithmNames = compSpec.getAlgorithmNames();
        List algorithmSpecs = compSpec.getParameterSpecs();
        for (int i = 0; i != algorithmNames.size(); ++i) {
            AlgorithmParameterSpec sigSpec = (AlgorithmParameterSpec)algorithmSpecs.get(i);
            if (sigSpec == null) {
                v.add((ASN1Encodable)algFinder.find((String)algorithmNames.get(i)));
                continue;
            }
            if (sigSpec instanceof PSSParameterSpec) {
                v.add((ASN1Encodable)JcaContentSignerBuilder.createPSSParams((PSSParameterSpec)sigSpec));
                continue;
            }
            throw new IllegalArgumentException("unrecognized parameterSpec");
        }
        return new DERSequence(v);
    }

    static {
        isAlgIdFromPrivate.add("DILITHIUM");
        isAlgIdFromPrivate.add("SPHINCS+");
        isAlgIdFromPrivate.add("SPHINCSPlus");
    }
}

