/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.gost.GOSTUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.GOST3410Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.GOST3410Params;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PrivateKeySpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class BCGOST3410PrivateKey
implements GOST3410PrivateKey,
PKCS12BagAttributeCarrier {
    static final long serialVersionUID = 8581661527592305464L;
    private BigInteger x;
    private transient GOST3410Params gost3410Spec;
    private transient PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected BCGOST3410PrivateKey() {
    }

    BCGOST3410PrivateKey(GOST3410PrivateKey key) {
        this.x = key.getX();
        this.gost3410Spec = key.getParameters();
    }

    BCGOST3410PrivateKey(GOST3410PrivateKeySpec spec) {
        this.x = spec.getX();
        this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(spec.getP(), spec.getQ(), spec.getA()));
    }

    BCGOST3410PrivateKey(PrivateKeyInfo info) throws IOException {
        GOST3410PublicKeyAlgParameters params = GOST3410PublicKeyAlgParameters.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        ASN1Encodable privKey = info.parsePrivateKey();
        if (privKey instanceof ASN1Integer) {
            this.x = ASN1Integer.getInstance(privKey).getPositiveValue();
        } else {
            ASN1OctetString derX = ASN1OctetString.getInstance(info.parsePrivateKey());
            byte[] keyEnc = derX.getOctets();
            byte[] keyBytes = new byte[keyEnc.length];
            for (int i = 0; i != keyEnc.length; ++i) {
                keyBytes[i] = keyEnc[keyEnc.length - 1 - i];
            }
            this.x = new BigInteger(1, keyBytes);
        }
        this.gost3410Spec = GOST3410ParameterSpec.fromPublicKeyAlg(params);
    }

    BCGOST3410PrivateKey(GOST3410PrivateKeyParameters params, GOST3410ParameterSpec spec) {
        this.x = params.getX();
        this.gost3410Spec = spec;
        if (spec == null) {
            throw new IllegalArgumentException("spec is null");
        }
    }

    @Override
    public String getAlgorithm() {
        return "GOST3410";
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        byte[] keyEnc = this.getX().toByteArray();
        byte[] keyBytes = keyEnc[0] == 0 ? new byte[keyEnc.length - 1] : new byte[keyEnc.length];
        for (int i = 0; i != keyBytes.length; ++i) {
            keyBytes[i] = keyEnc[keyEnc.length - 1 - i];
        }
        try {
            PrivateKeyInfo info = this.gost3410Spec instanceof GOST3410ParameterSpec ? new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94, new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID()))), new DEROctetString(keyBytes)) : new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94), new DEROctetString(keyBytes));
            return info.getEncoded("DER");
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public GOST3410Params getParameters() {
        return this.gost3410Spec;
    }

    @Override
    public BigInteger getX() {
        return this.x;
    }

    public boolean equals(Object o) {
        if (!(o instanceof GOST3410PrivateKey)) {
            return false;
        }
        GOST3410PrivateKey other = (GOST3410PrivateKey)o;
        return this.getX().equals(other.getX()) && this.getParameters().getPublicKeyParameters().equals(other.getParameters().getPublicKeyParameters()) && this.getParameters().getDigestParamSetOID().equals(other.getParameters().getDigestParamSetOID()) && this.compareObj(this.getParameters().getEncryptionParamSetOID(), other.getParameters().getEncryptionParamSetOID());
    }

    private boolean compareObj(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    public int hashCode() {
        return this.getX().hashCode() ^ this.gost3410Spec.hashCode();
    }

    public String toString() {
        try {
            return GOSTUtil.privateKeyToString("GOST3410", this.x, ((GOST3410PrivateKeyParameters)GOST3410Util.generatePrivateKeyParameter(this)).getParameters());
        }
        catch (InvalidKeyException e) {
            throw new IllegalStateException(e.getMessage());
        }
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
        String publicKeyParamSetOID = (String)in.readObject();
        if (publicKeyParamSetOID != null) {
            this.gost3410Spec = new GOST3410ParameterSpec(publicKeyParamSetOID, (String)in.readObject(), (String)in.readObject());
        } else {
            this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec((BigInteger)in.readObject(), (BigInteger)in.readObject(), (BigInteger)in.readObject()));
            in.readObject();
            in.readObject();
        }
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.gost3410Spec.getPublicKeyParamSetOID() != null) {
            out.writeObject(this.gost3410Spec.getPublicKeyParamSetOID());
            out.writeObject(this.gost3410Spec.getDigestParamSetOID());
            out.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
        } else {
            out.writeObject(null);
            out.writeObject(this.gost3410Spec.getPublicKeyParameters().getP());
            out.writeObject(this.gost3410Spec.getPublicKeyParameters().getQ());
            out.writeObject(this.gost3410Spec.getPublicKeyParameters().getA());
            out.writeObject(this.gost3410Spec.getDigestParamSetOID());
            out.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
        }
    }
}

