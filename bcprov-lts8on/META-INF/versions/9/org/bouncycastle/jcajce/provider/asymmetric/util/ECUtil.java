/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Enumeration;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ECUtil {
    static int[] convertMidTerms(int[] k) {
        int[] res = new int[3];
        if (k.length == 1) {
            res[0] = k[0];
        } else {
            if (k.length != 3) {
                throw new IllegalArgumentException("Only Trinomials and pentanomials supported");
            }
            if (k[0] < k[1] && k[0] < k[2]) {
                res[0] = k[0];
                if (k[1] < k[2]) {
                    res[1] = k[1];
                    res[2] = k[2];
                } else {
                    res[1] = k[2];
                    res[2] = k[1];
                }
            } else if (k[1] < k[2]) {
                res[0] = k[1];
                if (k[0] < k[2]) {
                    res[1] = k[0];
                    res[2] = k[2];
                } else {
                    res[1] = k[2];
                    res[2] = k[0];
                }
            } else {
                res[0] = k[2];
                if (k[0] < k[1]) {
                    res[1] = k[0];
                    res[2] = k[1];
                } else {
                    res[1] = k[1];
                    res[2] = k[0];
                }
            }
        }
        return res;
    }

    public static ECDomainParameters getDomainParameters(ProviderConfiguration configuration, ECParameterSpec params) {
        ECDomainParameters domainParameters;
        if (params instanceof ECNamedCurveParameterSpec) {
            ECNamedCurveParameterSpec nParams = (ECNamedCurveParameterSpec)params;
            ASN1ObjectIdentifier nameOid = ECUtil.getNamedCurveOid(nParams.getName());
            domainParameters = new ECNamedDomainParameters(nameOid, nParams.getCurve(), nParams.getG(), nParams.getN(), nParams.getH(), nParams.getSeed());
        } else if (params == null) {
            ECParameterSpec iSpec = configuration.getEcImplicitlyCa();
            domainParameters = new ECDomainParameters(iSpec.getCurve(), iSpec.getG(), iSpec.getN(), iSpec.getH(), iSpec.getSeed());
        } else {
            domainParameters = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());
        }
        return domainParameters;
    }

    public static ECDomainParameters getDomainParameters(ProviderConfiguration configuration, X962Parameters params) {
        ECDomainParameters domainParameters;
        if (params.isNamedCurve()) {
            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
            X9ECParameters ecP = ECUtil.getNamedCurveByOid(oid);
            if (ecP == null) {
                Map extraCurves = configuration.getAdditionalECParameters();
                ecP = (X9ECParameters)extraCurves.get(oid);
            }
            domainParameters = new ECNamedDomainParameters(oid, ecP);
        } else if (params.isImplicitlyCA()) {
            ECParameterSpec iSpec = configuration.getEcImplicitlyCa();
            domainParameters = new ECDomainParameters(iSpec.getCurve(), iSpec.getG(), iSpec.getN(), iSpec.getH(), iSpec.getSeed());
        } else {
            X9ECParameters ecP = X9ECParameters.getInstance(params.getParameters());
            domainParameters = new ECDomainParameters(ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
        }
        return domainParameters;
    }

    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        if (key instanceof org.bouncycastle.jce.interfaces.ECPublicKey) {
            org.bouncycastle.jce.interfaces.ECPublicKey k = (org.bouncycastle.jce.interfaces.ECPublicKey)key;
            ECParameterSpec s = k.getParameters();
            return new ECPublicKeyParameters(k.getQ(), new ECDomainParameters(s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
        }
        if (key instanceof ECPublicKey) {
            ECPublicKey pubKey = (ECPublicKey)key;
            ECParameterSpec s = EC5Util.convertSpec(pubKey.getParams());
            return new ECPublicKeyParameters(EC5Util.convertPoint(pubKey.getParams(), pubKey.getW()), new ECDomainParameters(s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
        }
        try {
            byte[] bytes = key.getEncoded();
            if (bytes == null) {
                throw new InvalidKeyException("no encoding for EC public key");
            }
            PublicKey publicKey = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(bytes));
            if (publicKey instanceof ECPublicKey) {
                return ECUtil.generatePublicKeyParameter(publicKey);
            }
        }
        catch (Exception e) {
            throw new InvalidKeyException("cannot identify EC public key: " + e.toString());
        }
        throw new InvalidKeyException("cannot identify EC public key.");
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        if (key instanceof ECPrivateKey) {
            ECPrivateKey k = (ECPrivateKey)key;
            ECParameterSpec s = k.getParameters();
            if (s == null) {
                s = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            }
            if (k.getParameters() instanceof ECNamedCurveParameterSpec) {
                String name = ((ECNamedCurveParameterSpec)k.getParameters()).getName();
                return new ECPrivateKeyParameters(k.getD(), (ECDomainParameters)new ECNamedDomainParameters(ECNamedCurveTable.getOID(name), s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
            }
            return new ECPrivateKeyParameters(k.getD(), new ECDomainParameters(s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
        }
        if (key instanceof java.security.interfaces.ECPrivateKey) {
            java.security.interfaces.ECPrivateKey privKey = (java.security.interfaces.ECPrivateKey)key;
            ECParameterSpec s = EC5Util.convertSpec(privKey.getParams());
            return new ECPrivateKeyParameters(privKey.getS(), new ECDomainParameters(s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
        }
        try {
            byte[] bytes = key.getEncoded();
            if (bytes == null) {
                throw new InvalidKeyException("no encoding for EC private key");
            }
            PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(bytes));
            if (privateKey instanceof java.security.interfaces.ECPrivateKey) {
                return ECUtil.generatePrivateKeyParameter(privateKey);
            }
        }
        catch (Exception e) {
            throw new InvalidKeyException("cannot identify EC private key: " + e.toString());
        }
        throw new InvalidKeyException("can't identify EC private key.");
    }

    public static int getOrderBitLength(ProviderConfiguration configuration, BigInteger order, BigInteger privateValue) {
        if (order == null) {
            if (configuration == null) {
                return privateValue.bitLength();
            }
            ECParameterSpec implicitCA = configuration.getEcImplicitlyCa();
            if (implicitCA == null) {
                return privateValue.bitLength();
            }
            return implicitCA.getN().bitLength();
        }
        return order.bitLength();
    }

    public static ASN1ObjectIdentifier getNamedCurveOid(String curveName) {
        ASN1ObjectIdentifier oid;
        if (null == curveName || curveName.length() < 1) {
            return null;
        }
        int spacePos = curveName.indexOf(32);
        if (spacePos > 0) {
            curveName = curveName.substring(spacePos + 1);
        }
        if (null != (oid = ECUtil.getOID(curveName))) {
            return oid;
        }
        return ECNamedCurveTable.getOID(curveName);
    }

    public static ASN1ObjectIdentifier getNamedCurveOid(ECParameterSpec ecParameterSpec) {
        Enumeration names = ECNamedCurveTable.getNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            X9ECParameters params = ECNamedCurveTable.getByName(name);
            if (!params.getN().equals(ecParameterSpec.getN()) || !params.getH().equals(ecParameterSpec.getH()) || !params.getCurve().equals(ecParameterSpec.getCurve()) || !params.getG().equals(ecParameterSpec.getG())) continue;
            return ECNamedCurveTable.getOID(name);
        }
        return null;
    }

    public static X9ECParameters getNamedCurveByOid(ASN1ObjectIdentifier oid) {
        X9ECParameters params = CustomNamedCurves.getByOID(oid);
        if (params == null) {
            params = ECNamedCurveTable.getByOID(oid);
        }
        return params;
    }

    public static X9ECParameters getNamedCurveByName(String curveName) {
        X9ECParameters params = CustomNamedCurves.getByName(curveName);
        if (params == null) {
            params = ECNamedCurveTable.getByName(curveName);
        }
        return params;
    }

    public static String getCurveName(ASN1ObjectIdentifier oid) {
        return ECNamedCurveTable.getName(oid);
    }

    public static String privateKeyToString(String algorithm, BigInteger d, ECParameterSpec spec) {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        ECPoint q = new FixedPointCombMultiplier().multiply(spec.getG(), d).normalize();
        buf.append(algorithm);
        buf.append(" Private Key [").append(ECUtil.generateKeyFingerprint(q, spec)).append("]").append(nl);
        buf.append("            X: ").append(q.getAffineXCoord().toBigInteger().toString(16)).append(nl);
        buf.append("            Y: ").append(q.getAffineYCoord().toBigInteger().toString(16)).append(nl);
        return buf.toString();
    }

    public static String publicKeyToString(String algorithm, ECPoint q, ECParameterSpec spec) {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append(algorithm);
        buf.append(" Public Key [").append(ECUtil.generateKeyFingerprint(q, spec)).append("]").append(nl);
        buf.append("            X: ").append(q.getAffineXCoord().toBigInteger().toString(16)).append(nl);
        buf.append("            Y: ").append(q.getAffineYCoord().toBigInteger().toString(16)).append(nl);
        return buf.toString();
    }

    public static String generateKeyFingerprint(ECPoint publicPoint, ECParameterSpec spec) {
        ECCurve curve = spec.getCurve();
        ECPoint g = spec.getG();
        if (curve != null) {
            return new Fingerprint(Arrays.concatenate(publicPoint.getEncoded(false), curve.getA().getEncoded(), curve.getB().getEncoded(), g.getEncoded(false))).toString();
        }
        return new Fingerprint(publicPoint.getEncoded(false)).toString();
    }

    public static String getNameFrom(final AlgorithmParameterSpec paramSpec) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    Method m = paramSpec.getClass().getMethod("getName", new Class[0]);
                    return m.invoke((Object)paramSpec, new Object[0]);
                }
                catch (Exception exception) {
                    return null;
                }
            }
        });
    }

    private static ASN1ObjectIdentifier getOID(String curveName) {
        char firstChar = curveName.charAt(0);
        if (firstChar >= '0' && firstChar <= '2') {
            try {
                return new ASN1ObjectIdentifier(curveName);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }
}

