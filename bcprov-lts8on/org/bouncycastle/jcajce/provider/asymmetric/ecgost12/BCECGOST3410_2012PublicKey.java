/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;

public class BCECGOST3410_2012PublicKey
implements ECPublicKey,
org.bouncycastle.jce.interfaces.ECPublicKey,
ECPointEncoder {
    static final long serialVersionUID = 7026240464295649314L;
    private String algorithm = "ECGOST3410-2012";
    private boolean withCompression;
    private transient ECPublicKeyParameters ecPublicKey;
    private transient ECParameterSpec ecSpec;
    private transient GOST3410PublicKeyAlgParameters gostParams;

    public BCECGOST3410_2012PublicKey(BCECGOST3410_2012PublicKey key) {
        this.ecPublicKey = key.ecPublicKey;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.gostParams = key.gostParams;
    }

    public BCECGOST3410_2012PublicKey(java.security.spec.ECPublicKeySpec spec) {
        this.ecSpec = spec.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, spec.getW()), EC5Util.getDomainParameters(null, spec.getParams()));
    }

    public BCECGOST3410_2012PublicKey(ECPublicKeySpec spec, ProviderConfiguration configuration) {
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecPublicKey = new ECPublicKeyParameters(spec.getQ(), ECUtil.getDomainParameters(configuration, spec.getParams()));
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
        } else {
            org.bouncycastle.jce.spec.ECParameterSpec s = configuration.getEcImplicitlyCa();
            this.ecPublicKey = new ECPublicKeyParameters(s.getCurve().createPoint(spec.getQ().getAffineXCoord().toBigInteger(), spec.getQ().getAffineYCoord().toBigInteger()), EC5Util.getDomainParameters(configuration, null));
            this.ecSpec = null;
        }
    }

    public BCECGOST3410_2012PublicKey(String algorithm, ECPublicKeyParameters params, ECParameterSpec spec) {
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        if (dp instanceof ECGOST3410Parameters) {
            ECGOST3410Parameters p = (ECGOST3410Parameters)dp;
            this.gostParams = new GOST3410PublicKeyAlgParameters(p.getPublicKeyParamSet(), p.getDigestParamSet(), p.getEncryptionParamSet());
        }
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = this.createSpec(ellipticCurve, dp);
        } else {
            this.ecSpec = spec;
        }
    }

    public BCECGOST3410_2012PublicKey(String algorithm, ECPublicKeyParameters params, org.bouncycastle.jce.spec.ECParameterSpec spec) {
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = this.createSpec(ellipticCurve, dp);
        } else {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec);
        }
    }

    public BCECGOST3410_2012PublicKey(String algorithm, ECPublicKeyParameters params) {
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        this.ecSpec = null;
    }

    private ECParameterSpec createSpec(EllipticCurve ellipticCurve, ECDomainParameters dp) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
    }

    public BCECGOST3410_2012PublicKey(ECPublicKey key) {
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, key.getW()), EC5Util.getDomainParameters(null, key.getParams()));
    }

    BCECGOST3410_2012PublicKey(SubjectPublicKeyInfo info) {
        this.populateFromPubKeyInfo(info);
    }

    private void populateFromPubKeyInfo(SubjectPublicKeyInfo info) {
        ASN1OctetString key;
        ASN1ObjectIdentifier algOid = info.getAlgorithm().getAlgorithm();
        ASN1BitString bits = info.getPublicKeyData();
        this.algorithm = "ECGOST3410-2012";
        try {
            key = (ASN1OctetString)ASN1Primitive.fromByteArray(bits.getBytes());
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("error recovering public key");
        }
        byte[] keyEnc = key.getOctets();
        int fieldSize = 32;
        if (algOid.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512)) {
            fieldSize = 64;
        }
        int keySize = 2 * fieldSize;
        byte[] x9Encoding = new byte[1 + keySize];
        x9Encoding[0] = 4;
        for (int i = 1; i <= fieldSize; ++i) {
            x9Encoding[i] = keyEnc[fieldSize - i];
            x9Encoding[i + fieldSize] = keyEnc[keySize - i];
        }
        this.gostParams = GOST3410PublicKeyAlgParameters.getInstance(info.getAlgorithm().getParameters());
        ECNamedCurveParameterSpec spec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
        ECCurve curve = spec.getCurve();
        EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getSeed());
        this.ecPublicKey = new ECPublicKeyParameters(curve.decodePoint(x9Encoding), ECUtil.getDomainParameters(null, spec));
        this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), ellipticCurve, EC5Util.convertPoint(spec.getG()), spec.getN(), spec.getH());
    }

    @Override
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        SubjectPublicKeyInfo info;
        ASN1ObjectIdentifier algIdentifier;
        int offset;
        int encKeySize;
        BigInteger bX = this.ecPublicKey.getQ().getAffineXCoord().toBigInteger();
        BigInteger bY = this.ecPublicKey.getQ().getAffineYCoord().toBigInteger();
        boolean is512 = bX.bitLength() > 256;
        ASN1Object params = this.getGostParams();
        if (params == null) {
            if (this.ecSpec instanceof ECNamedCurveSpec) {
                params = is512 ? new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512) : new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
            } else {
                ECCurve curve = EC5Util.convertCurve(this.ecSpec.getCurve());
                X9ECParameters ecP = new X9ECParameters(curve, new X9ECPoint(EC5Util.convertPoint(curve, this.ecSpec.getGenerator()), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
                params = new X962Parameters(ecP);
            }
        }
        if (is512) {
            encKeySize = 128;
            offset = 64;
            algIdentifier = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512;
        } else {
            encKeySize = 64;
            offset = 32;
            algIdentifier = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
        }
        byte[] encKey = new byte[encKeySize];
        this.extractBytes(encKey, encKeySize / 2, 0, bX);
        this.extractBytes(encKey, encKeySize / 2, offset, bY);
        try {
            info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(algIdentifier, params), new DEROctetString(encKey));
        }
        catch (IOException e) {
            return null;
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(info);
    }

    private void extractBytes(byte[] encKey, int size, int offSet, BigInteger bI) {
        byte[] val = bI.toByteArray();
        if (val.length < size) {
            byte[] tmp = new byte[size];
            System.arraycopy(val, 0, tmp, tmp.length - val.length, val.length);
            val = tmp;
        }
        for (int i = 0; i != size; ++i) {
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

    @Override
    public ECPoint getW() {
        return EC5Util.convertPoint(this.ecPublicKey.getQ());
    }

    @Override
    public org.bouncycastle.math.ec.ECPoint getQ() {
        if (this.ecSpec == null) {
            return this.ecPublicKey.getQ().getDetachedPoint();
        }
        return this.ecPublicKey.getQ();
    }

    ECPublicKeyParameters engineGetKeyParameters() {
        return this.ecPublicKey;
    }

    org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        if (this.ecSpec != null) {
            return EC5Util.convertSpec(this.ecSpec);
        }
        return BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
    }

    public String toString() {
        return ECUtil.publicKeyToString(this.algorithm, this.ecPublicKey.getQ(), this.engineGetSpec());
    }

    @Override
    public void setPointFormat(String style) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(style);
    }

    public boolean equals(Object o) {
        if (!(o instanceof BCECGOST3410_2012PublicKey)) {
            return false;
        }
        BCECGOST3410_2012PublicKey other = (BCECGOST3410_2012PublicKey)o;
        return this.ecPublicKey.getQ().equals(other.ecPublicKey.getQ()) && this.engineGetSpec().equals(other.engineGetSpec());
    }

    public int hashCode() {
        return this.ecPublicKey.getQ().hashCode() ^ this.engineGetSpec().hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        byte[] enc = (byte[])in.readObject();
        this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }

    public GOST3410PublicKeyAlgParameters getGostParams() {
        if (this.gostParams == null && this.ecSpec instanceof ECNamedCurveSpec) {
            BigInteger bX = this.ecPublicKey.getQ().getAffineXCoord().toBigInteger();
            boolean is512 = bX.bitLength() > 256;
            this.gostParams = is512 ? new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512) : new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
        }
        return this.gostParams;
    }
}

