/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost.BCECGOST3410PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

public class BCECGOST3410PrivateKey
implements java.security.interfaces.ECPrivateKey,
ECPrivateKey,
PKCS12BagAttributeCarrier,
ECPointEncoder {
    static final long serialVersionUID = 7245981689601667138L;
    private String algorithm = "ECGOST3410";
    private boolean withCompression;
    private transient ASN1Encodable gostParams;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient ASN1BitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected BCECGOST3410PrivateKey() {
    }

    public BCECGOST3410PrivateKey(java.security.interfaces.ECPrivateKey key) {
        this.d = key.getS();
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
    }

    public BCECGOST3410PrivateKey(org.bouncycastle.jce.spec.ECPrivateKeySpec spec) {
        this.d = spec.getD();
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
        } else {
            this.ecSpec = null;
        }
    }

    public BCECGOST3410PrivateKey(ECPrivateKeySpec spec) {
        this.d = spec.getS();
        this.ecSpec = spec.getParams();
    }

    public BCECGOST3410PrivateKey(BCECGOST3410PrivateKey key) {
        this.d = key.d;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.attrCarrier = key.attrCarrier;
        this.publicKey = key.publicKey;
        this.gostParams = key.gostParams;
    }

    public BCECGOST3410PrivateKey(String algorithm, ECPrivateKeyParameters params, BCECGOST3410PublicKey pubKey, ECParameterSpec spec) {
        this.algorithm = algorithm;
        this.d = params.getD();
        if (spec == null) {
            ECDomainParameters dp = params.getParameters();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
        } else {
            this.ecSpec = spec;
        }
        this.gostParams = pubKey.getGostParams();
        this.publicKey = this.getPublicKeyDetails(pubKey);
    }

    public BCECGOST3410PrivateKey(String algorithm, ECPrivateKeyParameters params, BCECGOST3410PublicKey pubKey, org.bouncycastle.jce.spec.ECParameterSpec spec) {
        this.algorithm = algorithm;
        this.d = params.getD();
        if (spec == null) {
            ECDomainParameters dp = params.getParameters();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
        } else {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(spec.getG()), spec.getN(), spec.getH().intValue());
        }
        this.gostParams = pubKey.getGostParams();
        this.publicKey = this.getPublicKeyDetails(pubKey);
    }

    public BCECGOST3410PrivateKey(String algorithm, ECPrivateKeyParameters params) {
        this.algorithm = algorithm;
        this.d = params.getD();
        this.ecSpec = null;
    }

    BCECGOST3410PrivateKey(PrivateKeyInfo info) throws IOException {
        this.populateFromPrivKeyInfo(info);
    }

    private void populateFromPrivKeyInfo(PrivateKeyInfo info) throws IOException {
        AlgorithmIdentifier pkAlg = info.getPrivateKeyAlgorithm();
        ASN1Encodable pkParams = pkAlg.getParameters();
        ASN1Primitive p = pkParams.toASN1Primitive();
        if (p instanceof ASN1Sequence && (ASN1Sequence.getInstance(p).size() == 2 || ASN1Sequence.getInstance(p).size() == 3)) {
            GOST3410PublicKeyAlgParameters gParams = GOST3410PublicKeyAlgParameters.getInstance(pkParams);
            this.gostParams = gParams;
            ECNamedCurveParameterSpec spec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(gParams.getPublicKeyParamSet()));
            ECCurve curve = spec.getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getSeed());
            this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(gParams.getPublicKeyParamSet()), ellipticCurve, EC5Util.convertPoint(spec.getG()), spec.getN(), spec.getH());
            ASN1Encodable privKey = info.parsePrivateKey();
            if (privKey instanceof ASN1Integer) {
                this.d = ASN1Integer.getInstance(privKey).getPositiveValue();
            } else {
                byte[] encVal = ASN1OctetString.getInstance(privKey).getOctets();
                byte[] dVal = new byte[encVal.length];
                for (int i = 0; i != encVal.length; ++i) {
                    dVal[i] = encVal[encVal.length - 1 - i];
                }
                this.d = new BigInteger(1, dVal);
            }
        } else {
            X962Parameters params = X962Parameters.getInstance(pkParams);
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
                X9ECParameters ecP = ECUtil.getNamedCurveByOid(oid);
                if (ecP == null) {
                    throw new IllegalStateException();
                }
                String curveName = ECUtil.getCurveName(oid);
                EllipticCurve ellipticCurve = EC5Util.convertCurve(ecP.getCurve(), ecP.getSeed());
                this.ecSpec = new ECNamedCurveSpec(curveName, ellipticCurve, EC5Util.convertPoint(ecP.getG()), ecP.getN(), ecP.getH());
            } else if (params.isImplicitlyCA()) {
                this.ecSpec = null;
            } else {
                X9ECParameters ecP = X9ECParameters.getInstance(params.getParameters());
                EllipticCurve ellipticCurve = EC5Util.convertCurve(ecP.getCurve(), ecP.getSeed());
                this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(ecP.getG()), ecP.getN(), ecP.getH().intValue());
            }
            ASN1Encodable privKey = info.parsePrivateKey();
            if (privKey instanceof ASN1Integer) {
                ASN1Integer derD = ASN1Integer.getInstance(privKey);
                this.d = derD.getValue();
            } else {
                org.bouncycastle.asn1.sec.ECPrivateKey ec = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(privKey);
                this.d = ec.getKey();
                this.publicKey = ec.getPublicKey();
            }
        }
    }

    @Override
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        int orderBitLength;
        X962Parameters params;
        if (this.gostParams != null) {
            byte[] encKey = new byte[32];
            this.extractBytes(encKey, 0, this.getS());
            try {
                PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, this.gostParams), new DEROctetString(encKey));
                return info.getEncoded("DER");
            }
            catch (IOException e) {
                return null;
            }
        }
        if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
            if (curveOid == null) {
                curveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName());
            }
            params = new X962Parameters(curveOid);
            orderBitLength = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
        } else if (this.ecSpec == null) {
            params = new X962Parameters(DERNull.INSTANCE);
            orderBitLength = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, null, this.getS());
        } else {
            ECCurve curve = EC5Util.convertCurve(this.ecSpec.getCurve());
            X9ECParameters ecP = new X9ECParameters(curve, new X9ECPoint(EC5Util.convertPoint(curve, this.ecSpec.getGenerator()), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
            params = new X962Parameters(ecP);
            orderBitLength = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
        }
        org.bouncycastle.asn1.sec.ECPrivateKey keyStructure = this.publicKey != null ? new org.bouncycastle.asn1.sec.ECPrivateKey(orderBitLength, this.getS(), this.publicKey, params) : new org.bouncycastle.asn1.sec.ECPrivateKey(orderBitLength, this.getS(), params);
        try {
            PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, params.toASN1Primitive()), keyStructure.toASN1Primitive());
            return info.getEncoded("DER");
        }
        catch (IOException e) {
            return null;
        }
    }

    private void extractBytes(byte[] encKey, int offSet, BigInteger bI) {
        byte[] val = bI.toByteArray();
        if (val.length < 32) {
            byte[] tmp = new byte[32];
            System.arraycopy(val, 0, tmp, tmp.length - val.length, val.length);
            val = tmp;
        }
        for (int i = 0; i != 32; ++i) {
            encKey[offSet + i] = val[val.length - 1 - i];
        }
    }

    @Override
    public ECParameterSpec getParams() {
        return this.ecSpec;
    }

    @Override
    public org.bouncycastle.jce.spec.ECParameterSpec getParameters() {
        if (this.ecSpec == null) {
            return null;
        }
        return EC5Util.convertSpec(this.ecSpec);
    }

    org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        if (this.ecSpec != null) {
            return EC5Util.convertSpec(this.ecSpec);
        }
        return BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
    }

    @Override
    public BigInteger getS() {
        return this.d;
    }

    @Override
    public BigInteger getD() {
        return this.d;
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

    @Override
    public void setPointFormat(String style) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(style);
    }

    public boolean equals(Object o) {
        if (!(o instanceof BCECGOST3410PrivateKey)) {
            return false;
        }
        BCECGOST3410PrivateKey other = (BCECGOST3410PrivateKey)o;
        return this.getD().equals(other.getD()) && this.engineGetSpec().equals(other.engineGetSpec());
    }

    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }

    public String toString() {
        return ECUtil.privateKeyToString(this.algorithm, this.d, this.engineGetSpec());
    }

    private ASN1BitString getPublicKeyDetails(BCECGOST3410PublicKey pub) {
        try {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(pub.getEncoded()));
            return info.getPublicKeyData();
        }
        catch (IOException e) {
            return null;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        byte[] enc = (byte[])in.readObject();
        this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }
}

