/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ies;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.jce.spec.IESParameterSpec;

public class AlgorithmParametersSpi
extends java.security.AlgorithmParametersSpi {
    IESParameterSpec currentSpec;

    protected boolean isASN1FormatString(String format) {
        return format == null || format.equals("ASN.1");
    }

    protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
        }
        return this.localEngineGetParameterSpec(paramSpec);
    }

    @Override
    protected byte[] engineGetEncoded() {
        try {
            ASN1EncodableVector v = new ASN1EncodableVector();
            if (this.currentSpec.getDerivationV() != null) {
                v.add(new DERTaggedObject(false, 0, (ASN1Encodable)new DEROctetString(this.currentSpec.getDerivationV())));
            }
            if (this.currentSpec.getEncodingV() != null) {
                v.add(new DERTaggedObject(false, 1, (ASN1Encodable)new DEROctetString(this.currentSpec.getEncodingV())));
            }
            v.add(new ASN1Integer(this.currentSpec.getMacKeySize()));
            if (this.currentSpec.getNonce() != null) {
                ASN1EncodableVector cV = new ASN1EncodableVector();
                cV.add(new ASN1Integer(this.currentSpec.getCipherKeySize()));
                cV.add(new DEROctetString(this.currentSpec.getNonce()));
                v.add(new DERSequence(cV));
            }
            v.add(this.currentSpec.getPointCompression() ? ASN1Boolean.TRUE : ASN1Boolean.FALSE);
            return new DERSequence(v).getEncoded("DER");
        }
        catch (IOException e) {
            throw new RuntimeException("Error encoding IESParameters");
        }
    }

    @Override
    protected byte[] engineGetEncoded(String format) {
        if (this.isASN1FormatString(format) || format.equalsIgnoreCase("X.509")) {
            return this.engineGetEncoded();
        }
        return null;
    }

    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == IESParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to ElGamal parameters object.");
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof IESParameterSpec)) {
            throw new InvalidParameterSpecException("IESParameterSpec required to initialise a IES algorithm parameters object");
        }
        this.currentSpec = (IESParameterSpec)paramSpec;
    }

    @Override
    protected void engineInit(byte[] params) throws IOException {
        try {
            ASN1Sequence s = (ASN1Sequence)ASN1Primitive.fromByteArray(params);
            if (s.size() > 5) {
                throw new IOException("sequence too big");
            }
            byte[] derivationV = null;
            byte[] encodingV = null;
            BigInteger macKeySize = null;
            BigInteger keySize = null;
            byte[] nonce = null;
            boolean pointCompression = false;
            Enumeration en = s.getObjects();
            while (en.hasMoreElements()) {
                Object o = en.nextElement();
                if (o instanceof ASN1TaggedObject) {
                    ASN1TaggedObject t = ASN1TaggedObject.getInstance(o);
                    if (t.getTagNo() == 0) {
                        derivationV = ASN1OctetString.getInstance(t, false).getOctets();
                        continue;
                    }
                    if (t.getTagNo() != 1) continue;
                    encodingV = ASN1OctetString.getInstance(t, false).getOctets();
                    continue;
                }
                if (o instanceof ASN1Integer) {
                    macKeySize = ASN1Integer.getInstance(o).getValue();
                    continue;
                }
                if (o instanceof ASN1Sequence) {
                    ASN1Sequence seq = ASN1Sequence.getInstance(o);
                    keySize = ASN1Integer.getInstance(seq.getObjectAt(0)).getValue();
                    nonce = ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets();
                    continue;
                }
                if (!(o instanceof ASN1Boolean)) continue;
                pointCompression = ASN1Boolean.getInstance(o).isTrue();
            }
            this.currentSpec = keySize != null ? new IESParameterSpec(derivationV, encodingV, macKeySize.intValue(), keySize.intValue(), nonce, pointCompression) : new IESParameterSpec(derivationV, encodingV, macKeySize.intValue(), -1, null, pointCompression);
        }
        catch (ClassCastException e) {
            throw new IOException("Not a valid IES Parameter encoding.");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Not a valid IES Parameter encoding.");
        }
    }

    @Override
    protected void engineInit(byte[] params, String format) throws IOException {
        if (!this.isASN1FormatString(format) && !format.equalsIgnoreCase("X.509")) {
            throw new IOException("Unknown parameter format " + format);
        }
        this.engineInit(params);
    }

    @Override
    protected String engineToString() {
        return "IES Parameters";
    }
}

