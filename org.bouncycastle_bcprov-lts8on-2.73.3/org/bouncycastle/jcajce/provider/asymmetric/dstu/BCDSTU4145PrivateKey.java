/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dstu;

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
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ua.DSTU4145BinaryField;
import org.bouncycastle.asn1.ua.DSTU4145ECBinary;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.BCDSTU4145PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

public class BCDSTU4145PrivateKey
implements java.security.interfaces.ECPrivateKey,
ECPrivateKey,
PKCS12BagAttributeCarrier,
ECPointEncoder {
    static final long serialVersionUID = 7245981689601667138L;
    private String algorithm = "DSTU4145";
    private boolean withCompression;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient ASN1BitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected BCDSTU4145PrivateKey() {
    }

    public BCDSTU4145PrivateKey(java.security.interfaces.ECPrivateKey key) {
        this.d = key.getS();
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
    }

    public BCDSTU4145PrivateKey(org.bouncycastle.jce.spec.ECPrivateKeySpec spec) {
        this.d = spec.getD();
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
        } else {
            this.ecSpec = null;
        }
    }

    public BCDSTU4145PrivateKey(ECPrivateKeySpec spec) {
        this.d = spec.getS();
        this.ecSpec = spec.getParams();
    }

    public BCDSTU4145PrivateKey(BCDSTU4145PrivateKey key) {
        this.d = key.d;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.attrCarrier = key.attrCarrier;
        this.publicKey = key.publicKey;
    }

    public BCDSTU4145PrivateKey(String algorithm, ECPrivateKeyParameters params, BCDSTU4145PublicKey pubKey, ECParameterSpec spec) {
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.d = params.getD();
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
        } else {
            this.ecSpec = spec;
        }
        this.publicKey = this.getPublicKeyDetails(pubKey);
    }

    public BCDSTU4145PrivateKey(String algorithm, ECPrivateKeyParameters params, BCDSTU4145PublicKey pubKey, org.bouncycastle.jce.spec.ECParameterSpec spec) {
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.d = params.getD();
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
        } else {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(spec.getG()), spec.getN(), spec.getH().intValue());
        }
        this.publicKey = this.getPublicKeyDetails(pubKey);
    }

    public BCDSTU4145PrivateKey(String algorithm, ECPrivateKeyParameters params) {
        this.algorithm = algorithm;
        this.d = params.getD();
        this.ecSpec = null;
    }

    BCDSTU4145PrivateKey(PrivateKeyInfo info) throws IOException {
        this.populateFromPrivKeyInfo(info);
    }

    private void populateFromPrivKeyInfo(PrivateKeyInfo info) throws IOException {
        X9ECParameters ecP;
        X962Parameters params = X962Parameters.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        if (params.isNamedCurve()) {
            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
            ecP = ECUtil.getNamedCurveByOid(oid);
            if (ecP == null) {
                ECDomainParameters gParam = DSTU4145NamedCurves.getByOID(oid);
                EllipticCurve ellipticCurve = EC5Util.convertCurve(gParam.getCurve(), gParam.getSeed());
                this.ecSpec = new ECNamedCurveSpec(oid.getId(), ellipticCurve, EC5Util.convertPoint(gParam.getG()), gParam.getN(), gParam.getH());
            } else {
                EllipticCurve ellipticCurve = EC5Util.convertCurve(ecP.getCurve(), ecP.getSeed());
                this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(oid), ellipticCurve, EC5Util.convertPoint(ecP.getG()), ecP.getN(), ecP.getH());
            }
        } else if (params.isImplicitlyCA()) {
            this.ecSpec = null;
        } else {
            ASN1Sequence seq = ASN1Sequence.getInstance(params.getParameters());
            if (seq.getObjectAt(0) instanceof ASN1Integer) {
                ecP = X9ECParameters.getInstance(params.getParameters());
                EllipticCurve ellipticCurve = EC5Util.convertCurve(ecP.getCurve(), ecP.getSeed());
                this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(ecP.getG()), ecP.getN(), ecP.getH().intValue());
            } else {
                org.bouncycastle.jce.spec.ECParameterSpec spec;
                DSTU4145Params dstuParams = DSTU4145Params.getInstance(seq);
                if (dstuParams.isNamedCurve()) {
                    ASN1ObjectIdentifier curveOid = dstuParams.getNamedCurve();
                    ECDomainParameters ecP2 = DSTU4145NamedCurves.getByOID(curveOid);
                    spec = new ECNamedCurveParameterSpec(curveOid.getId(), ecP2.getCurve(), ecP2.getG(), ecP2.getN(), ecP2.getH(), ecP2.getSeed());
                } else {
                    DSTU4145ECBinary binary = dstuParams.getECBinary();
                    byte[] b_bytes = binary.getB();
                    if (info.getPrivateKeyAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                        this.reverseBytes(b_bytes);
                    }
                    DSTU4145BinaryField field = binary.getField();
                    ECCurve.F2m curve = new ECCurve.F2m(field.getM(), field.getK1(), field.getK2(), field.getK3(), binary.getA(), new BigInteger(1, b_bytes), null, null);
                    byte[] g_bytes = binary.getG();
                    if (info.getPrivateKeyAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                        this.reverseBytes(g_bytes);
                    }
                    spec = new org.bouncycastle.jce.spec.ECParameterSpec(curve, DSTU4145PointEncoder.decodePoint(curve, g_bytes), binary.getN());
                }
                EllipticCurve ellipticCurve = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
                this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(spec.getG()), spec.getN(), spec.getH().intValue());
            }
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

    private void reverseBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; ++i) {
            byte tmp = bytes[i];
            bytes[i] = bytes[bytes.length - 1 - i];
            bytes[bytes.length - 1 - i] = tmp;
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
            PrivateKeyInfo info = this.algorithm.equals("DSTU4145") ? new PrivateKeyInfo(new AlgorithmIdentifier(UAObjectIdentifiers.dstu4145be, params.toASN1Primitive()), keyStructure.toASN1Primitive()) : new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params.toASN1Primitive()), keyStructure.toASN1Primitive());
            return info.getEncoded("DER");
        }
        catch (IOException e) {
            return null;
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
        if (!(o instanceof BCDSTU4145PrivateKey)) {
            return false;
        }
        BCDSTU4145PrivateKey other = (BCDSTU4145PrivateKey)o;
        return this.getD().equals(other.getD()) && this.engineGetSpec().equals(other.engineGetSpec());
    }

    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }

    public String toString() {
        return ECUtil.privateKeyToString(this.algorithm, this.d, this.engineGetSpec());
    }

    private ASN1BitString getPublicKeyDetails(BCDSTU4145PublicKey pub) {
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

