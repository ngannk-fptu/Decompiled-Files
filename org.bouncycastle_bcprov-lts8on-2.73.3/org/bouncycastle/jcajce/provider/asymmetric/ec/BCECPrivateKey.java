/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

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
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;

public class BCECPrivateKey
implements java.security.interfaces.ECPrivateKey,
ECPrivateKey,
PKCS12BagAttributeCarrier,
ECPointEncoder {
    static final long serialVersionUID = 994553197664784084L;
    private String algorithm = "EC";
    private boolean withCompression;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient ProviderConfiguration configuration;
    private transient ASN1BitString publicKey;
    private transient PrivateKeyInfo privateKeyInfo;
    private transient byte[] encoding;
    private transient ECPrivateKeyParameters baseKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected BCECPrivateKey() {
    }

    public BCECPrivateKey(java.security.interfaces.ECPrivateKey key, ProviderConfiguration configuration) {
        this.d = key.getS();
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
        this.configuration = configuration;
        this.baseKey = BCECPrivateKey.convertToBaseKey(this);
    }

    public BCECPrivateKey(String algorithm, org.bouncycastle.jce.spec.ECPrivateKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.d = spec.getD();
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
        } else {
            this.ecSpec = null;
        }
        this.configuration = configuration;
        this.baseKey = BCECPrivateKey.convertToBaseKey(this);
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.d = spec.getS();
        this.ecSpec = spec.getParams();
        this.configuration = configuration;
        this.baseKey = BCECPrivateKey.convertToBaseKey(this);
    }

    public BCECPrivateKey(String algorithm, BCECPrivateKey key) {
        this.algorithm = algorithm;
        this.d = key.d;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.attrCarrier = key.attrCarrier;
        this.publicKey = key.publicKey;
        this.configuration = key.configuration;
        this.baseKey = key.baseKey;
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, BCECPublicKey pubKey, ECParameterSpec spec, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.d = params.getD();
        this.configuration = configuration;
        this.baseKey = params;
        if (spec == null) {
            ECDomainParameters dp = params.getParameters();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
        } else {
            this.ecSpec = spec;
        }
        this.publicKey = this.getPublicKeyDetails(pubKey);
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, BCECPublicKey pubKey, org.bouncycastle.jce.spec.ECParameterSpec spec, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.d = params.getD();
        this.configuration = configuration;
        this.baseKey = params;
        if (spec == null) {
            ECDomainParameters dp = params.getParameters();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
        } else {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec);
        }
        try {
            this.publicKey = this.getPublicKeyDetails(pubKey);
        }
        catch (Exception e) {
            this.publicKey = null;
        }
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.d = params.getD();
        this.ecSpec = null;
        this.configuration = configuration;
        this.baseKey = params;
    }

    BCECPrivateKey(String algorithm, PrivateKeyInfo info, ProviderConfiguration configuration) throws IOException {
        this.algorithm = algorithm;
        this.configuration = configuration;
        this.populateFromPrivKeyInfo(info);
    }

    private void populateFromPrivKeyInfo(PrivateKeyInfo info) throws IOException {
        X962Parameters params = X962Parameters.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        ECCurve curve = EC5Util.getCurve(this.configuration, params);
        this.ecSpec = EC5Util.convertToSpec(params, curve);
        ASN1Encodable privKey = info.parsePrivateKey();
        if (privKey instanceof ASN1Integer) {
            ASN1Integer derD = ASN1Integer.getInstance(privKey);
            this.d = derD.getValue();
        } else {
            org.bouncycastle.asn1.sec.ECPrivateKey ec = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(privKey);
            this.d = ec.getKey();
            this.publicKey = ec.getPublicKey();
        }
        this.baseKey = BCECPrivateKey.convertToBaseKey(this);
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
        if (this.encoding == null) {
            PrivateKeyInfo info = this.getPrivateKeyInfo();
            if (info == null) {
                return null;
            }
            try {
                this.encoding = info.getEncoded("DER");
            }
            catch (IOException e) {
                return null;
            }
        }
        return Arrays.clone(this.encoding);
    }

    private PrivateKeyInfo getPrivateKeyInfo() {
        if (this.privateKeyInfo == null) {
            X962Parameters params = ECUtils.getDomainParametersFromName(this.ecSpec, this.withCompression);
            int orderBitLength = this.ecSpec == null ? ECUtil.getOrderBitLength(this.configuration, null, this.getS()) : ECUtil.getOrderBitLength(this.configuration, this.ecSpec.getOrder(), this.getS());
            org.bouncycastle.asn1.sec.ECPrivateKey keyStructure = this.publicKey != null ? new org.bouncycastle.asn1.sec.ECPrivateKey(orderBitLength, this.getS(), this.publicKey, params) : new org.bouncycastle.asn1.sec.ECPrivateKey(orderBitLength, this.getS(), params);
            try {
                this.privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), keyStructure);
            }
            catch (IOException e) {
                return null;
            }
        }
        return this.privateKeyInfo;
    }

    public ECPrivateKeyParameters engineGetKeyParameters() {
        return this.baseKey;
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
        return this.configuration.getEcImplicitlyCa();
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
        if (o instanceof java.security.interfaces.ECPrivateKey) {
            PrivateKeyInfo otherInfo;
            java.security.interfaces.ECPrivateKey other = (java.security.interfaces.ECPrivateKey)o;
            PrivateKeyInfo info = this.getPrivateKeyInfo();
            PrivateKeyInfo privateKeyInfo = otherInfo = other instanceof BCECPrivateKey ? ((BCECPrivateKey)other).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(other.getEncoded());
            if (info == null || otherInfo == null) {
                return false;
            }
            try {
                boolean algEquals = Arrays.constantTimeAreEqual(info.getPrivateKeyAlgorithm().getEncoded(), otherInfo.getPrivateKeyAlgorithm().getEncoded());
                boolean keyEquals = Arrays.constantTimeAreEqual(this.getS().toByteArray(), other.getS().toByteArray());
                return algEquals & keyEquals;
            }
            catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }

    public String toString() {
        return ECUtil.privateKeyToString("EC", this.d, this.engineGetSpec());
    }

    private ASN1BitString getPublicKeyDetails(BCECPublicKey pub) {
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
        this.configuration = BouncyCastleProvider.CONFIGURATION;
        this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }

    private static ECPrivateKeyParameters convertToBaseKey(BCECPrivateKey key) {
        String name;
        BCECPrivateKey k = key;
        org.bouncycastle.jce.spec.ECParameterSpec s = k.getParameters();
        if (s == null) {
            s = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
        }
        if (k.getParameters() instanceof ECNamedCurveParameterSpec && (name = ((ECNamedCurveParameterSpec)k.getParameters()).getName()) != null) {
            return new ECPrivateKeyParameters(k.getD(), (ECDomainParameters)new ECNamedDomainParameters(ECNamedCurveTable.getOID(name), s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
        }
        return new ECPrivateKeyParameters(k.getD(), new ECDomainParameters(s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
    }
}

