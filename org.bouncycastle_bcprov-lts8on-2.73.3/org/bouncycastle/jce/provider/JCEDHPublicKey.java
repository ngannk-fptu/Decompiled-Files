/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;

public class JCEDHPublicKey
implements DHPublicKey {
    static final long serialVersionUID = -216691575254424324L;
    private BigInteger y;
    private DHParameterSpec dhSpec;
    private SubjectPublicKeyInfo info;

    JCEDHPublicKey(DHPublicKeySpec spec) {
        this.y = spec.getY();
        this.dhSpec = new DHParameterSpec(spec.getP(), spec.getG());
    }

    JCEDHPublicKey(DHPublicKey key) {
        this.y = key.getY();
        this.dhSpec = key.getParams();
    }

    JCEDHPublicKey(DHPublicKeyParameters params) {
        this.y = params.getY();
        this.dhSpec = new DHParameterSpec(params.getParameters().getP(), params.getParameters().getG(), params.getParameters().getL());
    }

    JCEDHPublicKey(BigInteger y, DHParameterSpec dhSpec) {
        this.y = y;
        this.dhSpec = dhSpec;
    }

    JCEDHPublicKey(SubjectPublicKeyInfo info) {
        ASN1Integer derY;
        this.info = info;
        try {
            derY = (ASN1Integer)info.parsePublicKey();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in DH public key");
        }
        this.y = derY.getValue();
        ASN1Sequence seq = ASN1Sequence.getInstance(info.getAlgorithm().getParameters());
        ASN1ObjectIdentifier id = info.getAlgorithm().getAlgorithm();
        if (id.equals(PKCSObjectIdentifiers.dhKeyAgreement) || this.isPKCSParam(seq)) {
            DHParameter params = DHParameter.getInstance(seq);
            this.dhSpec = params.getL() != null ? new DHParameterSpec(params.getP(), params.getG(), params.getL().intValue()) : new DHParameterSpec(params.getP(), params.getG());
        } else if (id.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            DomainParameters params = DomainParameters.getInstance(seq);
            this.dhSpec = new DHParameterSpec(params.getP(), params.getG());
        } else {
            throw new IllegalArgumentException("unknown algorithm type: " + id);
        }
    }

    @Override
    public String getAlgorithm() {
        return "DH";
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        if (this.info != null) {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(this.info);
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL())), new ASN1Integer(this.y));
    }

    @Override
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }

    @Override
    public BigInteger getY() {
        return this.y;
    }

    private boolean isPKCSParam(ASN1Sequence seq) {
        if (seq.size() == 2) {
            return true;
        }
        if (seq.size() > 3) {
            return false;
        }
        ASN1Integer l = ASN1Integer.getInstance(seq.getObjectAt(2));
        ASN1Integer p = ASN1Integer.getInstance(seq.getObjectAt(0));
        return l.getValue().compareTo(BigInteger.valueOf(p.getValue().bitLength())) <= 0;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.y = (BigInteger)in.readObject();
        this.dhSpec = new DHParameterSpec((BigInteger)in.readObject(), (BigInteger)in.readObject(), in.readInt());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.getY());
        out.writeObject(this.dhSpec.getP());
        out.writeObject(this.dhSpec.getG());
        out.writeInt(this.dhSpec.getL());
    }
}

