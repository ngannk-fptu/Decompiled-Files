/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dh;

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
import org.bouncycastle.asn1.x9.ValidationParams;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.DHUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jcajce.spec.DHExtendedPublicKeySpec;

public class BCDHPublicKey
implements DHPublicKey {
    static final long serialVersionUID = -216691575254424324L;
    private BigInteger y;
    private transient DHPublicKeyParameters dhPublicKey;
    private transient DHParameterSpec dhSpec;
    private transient SubjectPublicKeyInfo info;

    BCDHPublicKey(DHPublicKeySpec spec) {
        this.y = spec.getY();
        this.dhSpec = spec instanceof DHExtendedPublicKeySpec ? ((DHExtendedPublicKeySpec)spec).getParams() : new DHParameterSpec(spec.getP(), spec.getG());
        if (this.dhSpec instanceof DHDomainParameterSpec) {
            DHDomainParameterSpec dhSp = (DHDomainParameterSpec)this.dhSpec;
            this.dhPublicKey = new DHPublicKeyParameters(this.y, dhSp.getDomainParameters());
        } else {
            this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(spec.getP(), spec.getG()));
        }
    }

    BCDHPublicKey(DHPublicKey key) {
        this.y = key.getY();
        this.dhSpec = key.getParams();
        if (this.dhSpec instanceof DHDomainParameterSpec) {
            DHDomainParameterSpec dhSp = (DHDomainParameterSpec)this.dhSpec;
            this.dhPublicKey = new DHPublicKeyParameters(this.y, dhSp.getDomainParameters());
        } else {
            this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
        }
    }

    BCDHPublicKey(DHPublicKeyParameters params) {
        this.y = params.getY();
        this.dhSpec = new DHDomainParameterSpec(params.getParameters());
        this.dhPublicKey = params;
    }

    BCDHPublicKey(BigInteger y, DHParameterSpec dhSpec) {
        this.y = y;
        this.dhSpec = dhSpec;
        this.dhPublicKey = dhSpec instanceof DHDomainParameterSpec ? new DHPublicKeyParameters(y, ((DHDomainParameterSpec)dhSpec).getDomainParameters()) : new DHPublicKeyParameters(y, new DHParameters(dhSpec.getP(), dhSpec.getG()));
    }

    public BCDHPublicKey(SubjectPublicKeyInfo info) {
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
            if (params.getL() != null) {
                this.dhSpec = new DHParameterSpec(params.getP(), params.getG(), params.getL().intValue());
                this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG(), null, this.dhSpec.getL()));
            } else {
                this.dhSpec = new DHParameterSpec(params.getP(), params.getG());
                this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
            }
        } else if (id.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            DomainParameters params = DomainParameters.getInstance(seq);
            ValidationParams validationParams = params.getValidationParams();
            this.dhPublicKey = validationParams != null ? new DHPublicKeyParameters(this.y, new DHParameters(params.getP(), params.getG(), params.getQ(), params.getJ(), new DHValidationParameters(validationParams.getSeed(), validationParams.getPgenCounter().intValue()))) : new DHPublicKeyParameters(this.y, new DHParameters(params.getP(), params.getG(), params.getQ(), params.getJ(), null));
            this.dhSpec = new DHDomainParameterSpec(this.dhPublicKey.getParameters());
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
        if (this.dhSpec instanceof DHDomainParameterSpec && ((DHDomainParameterSpec)this.dhSpec).getQ() != null) {
            DHParameters params = ((DHDomainParameterSpec)this.dhSpec).getDomainParameters();
            DHValidationParameters validationParameters = params.getValidationParameters();
            ValidationParams vParams = null;
            if (validationParameters != null) {
                vParams = new ValidationParams(validationParameters.getSeed(), validationParameters.getCounter());
            }
            return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.dhpublicnumber, new DomainParameters(params.getP(), params.getG(), params.getQ(), params.getJ(), vParams).toASN1Primitive()), new ASN1Integer(this.y));
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL()).toASN1Primitive()), new ASN1Integer(this.y));
    }

    public String toString() {
        return DHUtil.publicKeyToString("DH", this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
    }

    @Override
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }

    @Override
    public BigInteger getY() {
        return this.y;
    }

    public DHPublicKeyParameters engineGetKeyParameters() {
        return this.dhPublicKey;
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

    public int hashCode() {
        return this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getL();
    }

    public boolean equals(Object o) {
        if (!(o instanceof DHPublicKey)) {
            return false;
        }
        DHPublicKey other = (DHPublicKey)o;
        return this.getY().equals(other.getY()) && this.getParams().getG().equals(other.getParams().getG()) && this.getParams().getP().equals(other.getParams().getP()) && this.getParams().getL() == other.getParams().getL();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.dhSpec = new DHParameterSpec((BigInteger)in.readObject(), (BigInteger)in.readObject(), in.readInt());
        this.info = null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.dhSpec.getP());
        out.writeObject(this.dhSpec.getG());
        out.writeInt(this.dhSpec.getL());
    }
}

