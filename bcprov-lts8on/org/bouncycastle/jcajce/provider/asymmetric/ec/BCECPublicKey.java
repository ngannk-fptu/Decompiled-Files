/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCECPublicKey
implements ECPublicKey,
org.bouncycastle.jce.interfaces.ECPublicKey,
ECPointEncoder {
    static final long serialVersionUID = 2422789860422731812L;
    private String algorithm = "EC";
    private boolean withCompression;
    private transient ECPublicKeyParameters ecPublicKey;
    private transient ECParameterSpec ecSpec;
    private transient ProviderConfiguration configuration;
    private transient byte[] encoding;
    private transient boolean oldPcSet;

    public BCECPublicKey(String algorithm, BCECPublicKey key) {
        this.algorithm = algorithm;
        this.ecPublicKey = key.ecPublicKey;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.configuration = key.configuration;
    }

    public BCECPublicKey(String algorithm, java.security.spec.ECPublicKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.ecSpec = spec.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, spec.getW()), EC5Util.getDomainParameters(configuration, spec.getParams()));
        this.configuration = configuration;
    }

    public BCECPublicKey(String algorithm, ECPublicKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
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
        this.configuration = configuration;
    }

    public BCECPublicKey(String algorithm, ECPublicKeyParameters params, ECParameterSpec spec, ProviderConfiguration configuration) {
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = this.createSpec(ellipticCurve, dp);
        } else {
            this.ecSpec = spec;
        }
        this.configuration = configuration;
    }

    public BCECPublicKey(String algorithm, ECPublicKeyParameters params, org.bouncycastle.jce.spec.ECParameterSpec spec, ProviderConfiguration configuration) {
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = this.createSpec(ellipticCurve, dp);
        } else {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec);
        }
        this.ecPublicKey = params;
        this.configuration = configuration;
    }

    public BCECPublicKey(String algorithm, ECPublicKeyParameters params, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        this.ecSpec = null;
        this.configuration = configuration;
    }

    public BCECPublicKey(ECPublicKey key, ProviderConfiguration configuration) {
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, key.getW()), EC5Util.getDomainParameters(configuration, key.getParams()));
        this.configuration = configuration;
    }

    BCECPublicKey(String algorithm, SubjectPublicKeyInfo info, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.configuration = configuration;
        this.populateFromPubKeyInfo(info);
    }

    private ECParameterSpec createSpec(EllipticCurve ellipticCurve, ECDomainParameters dp) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
    }

    private void populateFromPubKeyInfo(SubjectPublicKeyInfo info) {
        int qLength;
        X962Parameters params = X962Parameters.getInstance(info.getAlgorithm().getParameters());
        ECCurve curve = EC5Util.getCurve(this.configuration, params);
        this.ecSpec = EC5Util.convertToSpec(params, curve);
        ASN1BitString bits = info.getPublicKeyData();
        byte[] data = bits.getBytes();
        ASN1OctetString key = new DEROctetString(data);
        if (data[0] == 4 && data[1] == data.length - 2 && (data[2] == 2 || data[2] == 3) && (qLength = new X9IntegerConverter().getByteLength(curve)) >= data.length - 3) {
            try {
                key = (ASN1OctetString)ASN1Primitive.fromByteArray(data);
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("error recovering public key");
            }
        }
        X9ECPoint derQ = new X9ECPoint(curve, key);
        this.ecPublicKey = new ECPublicKeyParameters(derQ.getPoint(), ECUtil.getDomainParameters(this.configuration, params));
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
        boolean pcSet = Properties.isOverrideSet("org.bouncycastle.ec.enable_pc");
        if (this.encoding == null || this.oldPcSet != pcSet) {
            boolean compress = this.withCompression || pcSet;
            AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, ECUtils.getDomainParametersFromName(this.ecSpec, compress));
            byte[] pubKeyOctets = this.ecPublicKey.getQ().getEncoded(compress);
            this.encoding = KeyUtil.getEncodedSubjectPublicKeyInfo(algId, pubKeyOctets);
            this.oldPcSet = pcSet;
        }
        return Arrays.clone(this.encoding);
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
        org.bouncycastle.math.ec.ECPoint q = this.ecPublicKey.getQ();
        if (this.ecSpec == null) {
            return q.getDetachedPoint();
        }
        return q;
    }

    ECPublicKeyParameters engineGetKeyParameters() {
        return this.ecPublicKey;
    }

    org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        if (this.ecSpec != null) {
            return EC5Util.convertSpec(this.ecSpec);
        }
        return this.configuration.getEcImplicitlyCa();
    }

    public String toString() {
        return ECUtil.publicKeyToString("EC", this.ecPublicKey.getQ(), this.engineGetSpec());
    }

    @Override
    public void setPointFormat(String style) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(style);
        this.encoding = null;
    }

    public boolean equals(Object o) {
        if (o instanceof BCECPublicKey) {
            BCECPublicKey other = (BCECPublicKey)o;
            return this.ecPublicKey.getQ().equals(other.ecPublicKey.getQ()) && this.engineGetSpec().equals(other.engineGetSpec());
        }
        if (o instanceof ECPublicKey) {
            ECPublicKey other = (ECPublicKey)o;
            return Arrays.areEqual(this.getEncoded(), other.getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return this.ecPublicKey.getQ().hashCode() ^ this.engineGetSpec().hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        byte[] enc = (byte[])in.readObject();
        this.configuration = BouncyCastleProvider.CONFIGURATION;
        this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }
}

