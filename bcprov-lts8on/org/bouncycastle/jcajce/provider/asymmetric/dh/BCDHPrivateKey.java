/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.ValidationParams;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.DHUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jcajce.spec.DHExtendedPrivateKeySpec;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class BCDHPrivateKey
implements DHPrivateKey,
PKCS12BagAttributeCarrier {
    static final long serialVersionUID = 311058815616901812L;
    private BigInteger x;
    private transient DHParameterSpec dhSpec;
    private transient PrivateKeyInfo info;
    private transient DHPrivateKeyParameters dhPrivateKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected BCDHPrivateKey() {
    }

    BCDHPrivateKey(DHPrivateKey key) {
        this.x = key.getX();
        this.dhSpec = key.getParams();
    }

    BCDHPrivateKey(DHPrivateKeySpec spec) {
        this.x = spec.getX();
        this.dhSpec = spec instanceof DHExtendedPrivateKeySpec ? ((DHExtendedPrivateKeySpec)spec).getParams() : new DHParameterSpec(spec.getP(), spec.getG());
    }

    public BCDHPrivateKey(PrivateKeyInfo info) throws IOException {
        ASN1Sequence seq = ASN1Sequence.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        ASN1Integer derX = (ASN1Integer)info.parsePrivateKey();
        ASN1ObjectIdentifier id = info.getPrivateKeyAlgorithm().getAlgorithm();
        this.info = info;
        this.x = derX.getValue();
        if (id.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            DHParameter params = DHParameter.getInstance(seq);
            if (params.getL() != null) {
                this.dhSpec = new DHParameterSpec(params.getP(), params.getG(), params.getL().intValue());
                this.dhPrivateKey = new DHPrivateKeyParameters(this.x, new DHParameters(params.getP(), params.getG(), null, params.getL().intValue()));
            } else {
                this.dhSpec = new DHParameterSpec(params.getP(), params.getG());
                this.dhPrivateKey = new DHPrivateKeyParameters(this.x, new DHParameters(params.getP(), params.getG()));
            }
        } else if (id.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            DomainParameters params = DomainParameters.getInstance(seq);
            this.dhSpec = new DHDomainParameterSpec(params.getP(), params.getQ(), params.getG(), params.getJ(), 0);
            this.dhPrivateKey = new DHPrivateKeyParameters(this.x, new DHParameters(params.getP(), params.getG(), params.getQ(), params.getJ(), null));
        } else {
            throw new IllegalArgumentException("unknown algorithm type: " + id);
        }
    }

    BCDHPrivateKey(DHPrivateKeyParameters params) {
        this.x = params.getX();
        this.dhSpec = new DHDomainParameterSpec(params.getParameters());
    }

    @Override
    public String getAlgorithm() {
        return "DH";
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        try {
            PrivateKeyInfo info;
            if (this.info != null) {
                return this.info.getEncoded("DER");
            }
            if (this.dhSpec instanceof DHDomainParameterSpec && ((DHDomainParameterSpec)this.dhSpec).getQ() != null) {
                DHParameters params = ((DHDomainParameterSpec)this.dhSpec).getDomainParameters();
                DHValidationParameters validationParameters = params.getValidationParameters();
                ValidationParams vParams = null;
                if (validationParameters != null) {
                    vParams = new ValidationParams(validationParameters.getSeed(), validationParameters.getCounter());
                }
                info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.dhpublicnumber, new DomainParameters(params.getP(), params.getG(), params.getQ(), params.getJ(), vParams).toASN1Primitive()), new ASN1Integer(this.getX()));
            } else {
                info = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL()).toASN1Primitive()), new ASN1Integer(this.getX()));
            }
            return info.getEncoded("DER");
        }
        catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        return DHUtil.privateKeyToString("DH", this.x, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
    }

    @Override
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }

    @Override
    public BigInteger getX() {
        return this.x;
    }

    DHPrivateKeyParameters engineGetKeyParameters() {
        if (this.dhPrivateKey != null) {
            return this.dhPrivateKey;
        }
        if (this.dhSpec instanceof DHDomainParameterSpec) {
            return new DHPrivateKeyParameters(this.x, ((DHDomainParameterSpec)this.dhSpec).getDomainParameters());
        }
        return new DHPrivateKeyParameters(this.x, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG(), null, this.dhSpec.getL()));
    }

    public boolean equals(Object o) {
        if (!(o instanceof DHPrivateKey)) {
            return false;
        }
        DHPrivateKey other = (DHPrivateKey)o;
        return this.getX().equals(other.getX()) && this.getParams().getG().equals(other.getParams().getG()) && this.getParams().getP().equals(other.getParams().getP()) && this.getParams().getL() == other.getParams().getL();
    }

    public int hashCode() {
        return this.getX().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getL();
    }

    @Override
    public void setBagAttribute(ASN1ObjectIdentifier oid, ASN1Encodable attribute) {
        this.attrCarrier.setBagAttribute(oid, attribute);
    }

    @Override
    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier oid) {
        return this.attrCarrier.getBagAttribute(oid);
    }

    @Override
    public Enumeration getBagAttributeKeys() {
        return this.attrCarrier.getBagAttributeKeys();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.dhSpec = new DHParameterSpec((BigInteger)in.readObject(), (BigInteger)in.readObject(), in.readInt());
        this.info = null;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.dhSpec.getP());
        out.writeObject(this.dhSpec.getG());
        out.writeInt(this.dhSpec.getL());
    }
}

