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

    BCDHPublicKey(DHPublicKeySpec dHPublicKeySpec) {
        this.y = dHPublicKeySpec.getY();
        this.dhSpec = dHPublicKeySpec instanceof DHExtendedPublicKeySpec ? ((DHExtendedPublicKeySpec)dHPublicKeySpec).getParams() : new DHParameterSpec(dHPublicKeySpec.getP(), dHPublicKeySpec.getG());
        if (this.dhSpec instanceof DHDomainParameterSpec) {
            DHDomainParameterSpec dHDomainParameterSpec = (DHDomainParameterSpec)this.dhSpec;
            this.dhPublicKey = new DHPublicKeyParameters(this.y, dHDomainParameterSpec.getDomainParameters());
        } else {
            this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(dHPublicKeySpec.getP(), dHPublicKeySpec.getG()));
        }
    }

    BCDHPublicKey(DHPublicKey dHPublicKey) {
        this.y = dHPublicKey.getY();
        this.dhSpec = dHPublicKey.getParams();
        if (this.dhSpec instanceof DHDomainParameterSpec) {
            DHDomainParameterSpec dHDomainParameterSpec = (DHDomainParameterSpec)this.dhSpec;
            this.dhPublicKey = new DHPublicKeyParameters(this.y, dHDomainParameterSpec.getDomainParameters());
        } else {
            this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
        }
    }

    BCDHPublicKey(DHPublicKeyParameters dHPublicKeyParameters) {
        this.y = dHPublicKeyParameters.getY();
        this.dhSpec = new DHDomainParameterSpec(dHPublicKeyParameters.getParameters());
        this.dhPublicKey = dHPublicKeyParameters;
    }

    BCDHPublicKey(BigInteger bigInteger, DHParameterSpec dHParameterSpec) {
        this.y = bigInteger;
        this.dhSpec = dHParameterSpec;
        this.dhPublicKey = dHParameterSpec instanceof DHDomainParameterSpec ? new DHPublicKeyParameters(bigInteger, ((DHDomainParameterSpec)dHParameterSpec).getDomainParameters()) : new DHPublicKeyParameters(bigInteger, new DHParameters(dHParameterSpec.getP(), dHParameterSpec.getG()));
    }

    public BCDHPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        ASN1Integer aSN1Integer;
        this.info = subjectPublicKeyInfo;
        try {
            aSN1Integer = (ASN1Integer)subjectPublicKeyInfo.parsePublicKey();
        }
        catch (IOException iOException) {
            throw new IllegalArgumentException("invalid info structure in DH public key");
        }
        this.y = aSN1Integer.getValue();
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
        ASN1ObjectIdentifier aSN1ObjectIdentifier = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.dhKeyAgreement) || this.isPKCSParam(aSN1Sequence)) {
            DHParameter dHParameter = DHParameter.getInstance(aSN1Sequence);
            if (dHParameter.getL() != null) {
                this.dhSpec = new DHParameterSpec(dHParameter.getP(), dHParameter.getG(), dHParameter.getL().intValue());
                this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG(), null, this.dhSpec.getL()));
            } else {
                this.dhSpec = new DHParameterSpec(dHParameter.getP(), dHParameter.getG());
                this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
            }
        } else if (aSN1ObjectIdentifier.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            DomainParameters domainParameters = DomainParameters.getInstance(aSN1Sequence);
            ValidationParams validationParams = domainParameters.getValidationParams();
            this.dhPublicKey = validationParams != null ? new DHPublicKeyParameters(this.y, new DHParameters(domainParameters.getP(), domainParameters.getG(), domainParameters.getQ(), domainParameters.getJ(), new DHValidationParameters(validationParams.getSeed(), validationParams.getPgenCounter().intValue()))) : new DHPublicKeyParameters(this.y, new DHParameters(domainParameters.getP(), domainParameters.getG(), domainParameters.getQ(), domainParameters.getJ(), null));
            this.dhSpec = new DHDomainParameterSpec(this.dhPublicKey.getParameters());
        } else {
            throw new IllegalArgumentException("unknown algorithm type: " + aSN1ObjectIdentifier);
        }
    }

    public String getAlgorithm() {
        return "DH";
    }

    public String getFormat() {
        return "X.509";
    }

    public byte[] getEncoded() {
        if (this.info != null) {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(this.info);
        }
        if (this.dhSpec instanceof DHDomainParameterSpec && ((DHDomainParameterSpec)this.dhSpec).getQ() != null) {
            DHParameters dHParameters = ((DHDomainParameterSpec)this.dhSpec).getDomainParameters();
            DHValidationParameters dHValidationParameters = dHParameters.getValidationParameters();
            ValidationParams validationParams = null;
            if (dHValidationParameters != null) {
                validationParams = new ValidationParams(dHValidationParameters.getSeed(), dHValidationParameters.getCounter());
            }
            return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.dhpublicnumber, new DomainParameters(dHParameters.getP(), dHParameters.getG(), dHParameters.getQ(), dHParameters.getJ(), validationParams).toASN1Primitive()), new ASN1Integer(this.y));
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL()).toASN1Primitive()), new ASN1Integer(this.y));
    }

    public String toString() {
        return DHUtil.publicKeyToString("DH", this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
    }

    public DHParameterSpec getParams() {
        return this.dhSpec;
    }

    public BigInteger getY() {
        return this.y;
    }

    public DHPublicKeyParameters engineGetKeyParameters() {
        return this.dhPublicKey;
    }

    private boolean isPKCSParam(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() == 2) {
            return true;
        }
        if (aSN1Sequence.size() > 3) {
            return false;
        }
        ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(2));
        ASN1Integer aSN1Integer2 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        return aSN1Integer.getValue().compareTo(BigInteger.valueOf(aSN1Integer2.getValue().bitLength())) <= 0;
    }

    public int hashCode() {
        return this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getL();
    }

    public boolean equals(Object object) {
        if (!(object instanceof DHPublicKey)) {
            return false;
        }
        DHPublicKey dHPublicKey = (DHPublicKey)object;
        return this.getY().equals(dHPublicKey.getY()) && this.getParams().getG().equals(dHPublicKey.getParams().getG()) && this.getParams().getP().equals(dHPublicKey.getParams().getP()) && this.getParams().getL() == dHPublicKey.getParams().getL();
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.dhSpec = new DHParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), objectInputStream.readInt());
        this.info = null;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.dhSpec.getP());
        objectOutputStream.writeObject(this.dhSpec.getG());
        objectOutputStream.writeInt(this.dhSpec.getL());
    }
}

