/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.util.encoders.Hex;

class X509SignatureUtil {
    private static final Map<ASN1ObjectIdentifier, String> algNames = new HashMap<ASN1ObjectIdentifier, String>();
    private static final ASN1Null derNull;

    X509SignatureUtil() {
    }

    static boolean isCompositeAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
        return MiscObjectIdentifiers.id_alg_composite.equals(algorithmIdentifier.getAlgorithm());
    }

    static void setSignatureParameters(Signature signature, ASN1Encodable params) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (params != null && !derNull.equals(params)) {
            AlgorithmParameters sigParams = AlgorithmParameters.getInstance(signature.getAlgorithm(), signature.getProvider());
            try {
                sigParams.init(params.toASN1Primitive().getEncoded());
            }
            catch (IOException e) {
                throw new SignatureException("IOException decoding parameters: " + e.getMessage());
            }
            if (signature.getAlgorithm().endsWith("MGF1")) {
                try {
                    signature.setParameter(sigParams.getParameterSpec(PSSParameterSpec.class));
                }
                catch (GeneralSecurityException e) {
                    throw new SignatureException("Exception extracting parameters: " + e.getMessage());
                }
            }
        }
    }

    static String getSignatureName(AlgorithmIdentifier sigAlgId) {
        String algName;
        ASN1Encodable params = sigAlgId.getParameters();
        if (params != null && !derNull.equals(params)) {
            if (sigAlgId.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                RSASSAPSSparams rsaParams = RSASSAPSSparams.getInstance(params);
                return X509SignatureUtil.getDigestAlgName(rsaParams.getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
            }
            if (sigAlgId.getAlgorithm().equals(X9ObjectIdentifiers.ecdsa_with_SHA2)) {
                ASN1Sequence ecDsaParams = ASN1Sequence.getInstance(params);
                return X509SignatureUtil.getDigestAlgName((ASN1ObjectIdentifier)ecDsaParams.getObjectAt(0)) + "withECDSA";
            }
        }
        if ((algName = algNames.get(sigAlgId.getAlgorithm())) != null) {
            return algName;
        }
        return X509SignatureUtil.findAlgName(sigAlgId.getAlgorithm());
    }

    private static String getDigestAlgName(ASN1ObjectIdentifier digestAlgOID) {
        String name = MessageDigestUtils.getDigestName(digestAlgOID);
        int dIndex = name.indexOf(45);
        if (dIndex > 0 && !name.startsWith("SHA3")) {
            return name.substring(0, dIndex) + name.substring(dIndex + 1);
        }
        return name;
    }

    private static String findAlgName(ASN1ObjectIdentifier algOid) {
        String algName;
        Provider prov = Security.getProvider("BC");
        if (prov != null && (algName = X509SignatureUtil.lookupAlg(prov, algOid)) != null) {
            return algName;
        }
        Provider[] provs = Security.getProviders();
        for (int i = 0; i != provs.length; ++i) {
            String algName2;
            if (prov == provs[i] || (algName2 = X509SignatureUtil.lookupAlg(provs[i], algOid)) == null) continue;
            return algName2;
        }
        return algOid.getId();
    }

    private static String lookupAlg(Provider prov, ASN1ObjectIdentifier algOid) {
        String algName = prov.getProperty("Alg.Alias.Signature." + algOid);
        if (algName != null) {
            return algName;
        }
        algName = prov.getProperty("Alg.Alias.Signature.OID." + algOid);
        if (algName != null) {
            return algName;
        }
        return null;
    }

    static void prettyPrintSignature(byte[] sig, StringBuffer buf, String nl) {
        if (sig.length > 20) {
            buf.append("            Signature: ").append(Hex.toHexString(sig, 0, 20)).append(nl);
            for (int i = 20; i < sig.length; i += 20) {
                if (i < sig.length - 20) {
                    buf.append("                       ").append(Hex.toHexString(sig, i, 20)).append(nl);
                    continue;
                }
                buf.append("                       ").append(Hex.toHexString(sig, i, sig.length - i)).append(nl);
            }
        } else {
            buf.append("            Signature: ").append(Hex.toHexString(sig)).append(nl);
        }
    }

    static {
        algNames.put(EdECObjectIdentifiers.id_Ed25519, "Ed25519");
        algNames.put(EdECObjectIdentifiers.id_Ed448, "Ed448");
        algNames.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1withDSA");
        algNames.put(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1withDSA");
        derNull = DERNull.INSTANCE;
    }
}

