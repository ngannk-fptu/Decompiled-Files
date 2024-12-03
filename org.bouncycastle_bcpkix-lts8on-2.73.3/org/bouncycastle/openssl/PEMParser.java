/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.pkcs.RSAPrivateKey
 *  org.bouncycastle.asn1.pkcs.RSAPublicKey
 *  org.bouncycastle.asn1.sec.ECPrivateKey
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.DSAParameter
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x9.X9ECParameters
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.util.encoders.Hex
 *  org.bouncycastle.util.io.pem.PemHeader
 *  org.bouncycastle.util.io.pem.PemObject
 *  org.bouncycastle.util.io.pem.PemObjectParser
 *  org.bouncycastle.util.io.pem.PemReader
 */
package org.bouncycastle.openssl;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMKeyPairParser;
import org.bouncycastle.openssl.X509TrustedCertificateBlock;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectParser;
import org.bouncycastle.util.io.pem.PemReader;

public class PEMParser
extends PemReader {
    public static final String TYPE_CERTIFICATE_REQUEST = "CERTIFICATE REQUEST";
    public static final String TYPE_NEW_CERTIFICATE_REQUEST = "NEW CERTIFICATE REQUEST";
    public static final String TYPE_CERTIFICATE = "CERTIFICATE";
    public static final String TYPE_TRUSTED_CERTIFICATE = "TRUSTED CERTIFICATE";
    public static final String TYPE_X509_CERTIFICATE = "X509 CERTIFICATE";
    public static final String TYPE_X509_CRL = "X509 CRL";
    public static final String TYPE_PKCS7 = "PKCS7";
    public static final String TYPE_CMS = "CMS";
    public static final String TYPE_ATTRIBUTE_CERTIFICATE = "ATTRIBUTE CERTIFICATE";
    public static final String TYPE_EC_PARAMETERS = "EC PARAMETERS";
    public static final String TYPE_PUBLIC_KEY = "PUBLIC KEY";
    public static final String TYPE_RSA_PUBLIC_KEY = "RSA PUBLIC KEY";
    public static final String TYPE_RSA_PRIVATE_KEY = "RSA PRIVATE KEY";
    public static final String TYPE_DSA_PRIVATE_KEY = "DSA PRIVATE KEY";
    public static final String TYPE_EC_PRIVATE_KEY = "EC PRIVATE KEY";
    public static final String TYPE_ENCRYPTED_PRIVATE_KEY = "ENCRYPTED PRIVATE KEY";
    public static final String TYPE_PRIVATE_KEY = "PRIVATE KEY";
    protected final Map parsers = new HashMap();

    public PEMParser(Reader reader) {
        super(reader);
        this.parsers.put(TYPE_CERTIFICATE_REQUEST, new PKCS10CertificationRequestParser());
        this.parsers.put(TYPE_NEW_CERTIFICATE_REQUEST, new PKCS10CertificationRequestParser());
        this.parsers.put(TYPE_CERTIFICATE, new X509CertificateParser());
        this.parsers.put(TYPE_TRUSTED_CERTIFICATE, new X509TrustedCertificateParser());
        this.parsers.put(TYPE_X509_CERTIFICATE, new X509CertificateParser());
        this.parsers.put(TYPE_X509_CRL, new X509CRLParser());
        this.parsers.put(TYPE_PKCS7, new PKCS7Parser());
        this.parsers.put(TYPE_CMS, new PKCS7Parser());
        this.parsers.put(TYPE_ATTRIBUTE_CERTIFICATE, new X509AttributeCertificateParser());
        this.parsers.put(TYPE_EC_PARAMETERS, new ECCurveParamsParser());
        this.parsers.put(TYPE_PUBLIC_KEY, new PublicKeyParser());
        this.parsers.put(TYPE_RSA_PUBLIC_KEY, new RSAPublicKeyParser());
        this.parsers.put(TYPE_RSA_PRIVATE_KEY, new KeyPairParser(new RSAKeyPairParser()));
        this.parsers.put(TYPE_DSA_PRIVATE_KEY, new KeyPairParser(new DSAKeyPairParser()));
        this.parsers.put(TYPE_EC_PRIVATE_KEY, new KeyPairParser(new ECDSAKeyPairParser()));
        this.parsers.put(TYPE_ENCRYPTED_PRIVATE_KEY, new EncryptedPrivateKeyParser());
        this.parsers.put(TYPE_PRIVATE_KEY, new PrivateKeyParser());
    }

    public Object readObject() throws IOException {
        PemObject obj = this.readPemObject();
        if (obj != null) {
            String type = obj.getType();
            Object pemObjectParser = this.parsers.get(type);
            if (pemObjectParser != null) {
                return ((PemObjectParser)pemObjectParser).parseObject(obj);
            }
            throw new IOException("unrecognised object: " + type);
        }
        return null;
    }

    public Set<String> getSupportedTypes() {
        return Collections.unmodifiableSet(this.parsers.keySet());
    }

    private static class DSAKeyPairParser
    implements PEMKeyPairParser {
        private DSAKeyPairParser() {
        }

        @Override
        public PEMKeyPair parse(byte[] encoding) throws IOException {
            try {
                ASN1Sequence seq = ASN1Sequence.getInstance((Object)encoding);
                if (seq.size() != 6) {
                    throw new PEMException("malformed sequence in DSA private key");
                }
                ASN1Integer p = ASN1Integer.getInstance((Object)seq.getObjectAt(1));
                ASN1Integer q = ASN1Integer.getInstance((Object)seq.getObjectAt(2));
                ASN1Integer g = ASN1Integer.getInstance((Object)seq.getObjectAt(3));
                ASN1Integer y = ASN1Integer.getInstance((Object)seq.getObjectAt(4));
                ASN1Integer x = ASN1Integer.getInstance((Object)seq.getObjectAt(5));
                return new PEMKeyPair(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(p.getValue(), q.getValue(), g.getValue())), (ASN1Encodable)y), new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(p.getValue(), q.getValue(), g.getValue())), (ASN1Encodable)x));
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new PEMException("problem creating DSA private key: " + e.toString(), e);
            }
        }
    }

    private static class ECCurveParamsParser
    implements PemObjectParser {
        private ECCurveParamsParser() {
        }

        public Object parseObject(PemObject obj) throws IOException {
            try {
                ASN1Primitive param = ASN1Primitive.fromByteArray((byte[])obj.getContent());
                if (param instanceof ASN1ObjectIdentifier) {
                    return ASN1Primitive.fromByteArray((byte[])obj.getContent());
                }
                if (param instanceof ASN1Sequence) {
                    return X9ECParameters.getInstance((Object)param);
                }
                return null;
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new PEMException("exception extracting EC named curve: " + e.toString());
            }
        }
    }

    private static class ECDSAKeyPairParser
    implements PEMKeyPairParser {
        private ECDSAKeyPairParser() {
        }

        @Override
        public PEMKeyPair parse(byte[] encoding) throws IOException {
            try {
                ASN1Sequence seq = ASN1Sequence.getInstance((Object)encoding);
                ECPrivateKey pKey = ECPrivateKey.getInstance((Object)seq);
                AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)pKey.getParametersObject());
                PrivateKeyInfo privInfo = new PrivateKeyInfo(algId, (ASN1Encodable)pKey);
                if (pKey.getPublicKey() != null) {
                    SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(algId, pKey.getPublicKey().getBytes());
                    return new PEMKeyPair(pubInfo, privInfo);
                }
                return new PEMKeyPair(null, privInfo);
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new PEMException("problem creating EC private key: " + e.toString(), e);
            }
        }
    }

    private static class EncryptedPrivateKeyParser
    implements PemObjectParser {
        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance((Object)obj.getContent()));
            }
            catch (Exception e) {
                throw new PEMException("problem parsing ENCRYPTED PRIVATE KEY: " + e.toString(), e);
            }
        }
    }

    private static class KeyPairParser
    implements PemObjectParser {
        private final PEMKeyPairParser pemKeyPairParser;

        public KeyPairParser(PEMKeyPairParser pemKeyPairParser) {
            this.pemKeyPairParser = pemKeyPairParser;
        }

        public Object parseObject(PemObject obj) throws IOException {
            boolean isEncrypted = false;
            String dekInfo = null;
            List headers = obj.getHeaders();
            for (PemHeader hdr : headers) {
                if (hdr.getName().equals("Proc-Type") && hdr.getValue().equals("4,ENCRYPTED")) {
                    isEncrypted = true;
                    continue;
                }
                if (!hdr.getName().equals("DEK-Info")) continue;
                dekInfo = hdr.getValue();
            }
            byte[] keyBytes = obj.getContent();
            try {
                if (isEncrypted) {
                    StringTokenizer tknz = new StringTokenizer(dekInfo, ",");
                    String dekAlgName = tknz.nextToken();
                    byte[] iv = Hex.decode((String)tknz.nextToken());
                    return new PEMEncryptedKeyPair(dekAlgName, iv, keyBytes, this.pemKeyPairParser);
                }
                return this.pemKeyPairParser.parse(keyBytes);
            }
            catch (IOException e) {
                if (isEncrypted) {
                    throw new PEMException("exception decoding - please check password and data.", e);
                }
                throw new PEMException(e.getMessage(), e);
            }
            catch (IllegalArgumentException e) {
                if (isEncrypted) {
                    throw new PEMException("exception decoding - please check password and data.", e);
                }
                throw new PEMException(e.getMessage(), e);
            }
        }
    }

    private static class PKCS10CertificationRequestParser
    implements PemObjectParser {
        private PKCS10CertificationRequestParser() {
        }

        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new PKCS10CertificationRequest(obj.getContent());
            }
            catch (Exception e) {
                throw new PEMException("problem parsing certrequest: " + e.toString(), e);
            }
        }
    }

    private static class PKCS7Parser
    implements PemObjectParser {
        private PKCS7Parser() {
        }

        public Object parseObject(PemObject obj) throws IOException {
            try {
                ASN1InputStream aIn = new ASN1InputStream(obj.getContent());
                return ContentInfo.getInstance((Object)aIn.readObject());
            }
            catch (Exception e) {
                throw new PEMException("problem parsing PKCS7 object: " + e.toString(), e);
            }
        }
    }

    private static class PrivateKeyParser
    implements PemObjectParser {
        public Object parseObject(PemObject obj) throws IOException {
            try {
                return PrivateKeyInfo.getInstance((Object)obj.getContent());
            }
            catch (Exception e) {
                throw new PEMException("problem parsing PRIVATE KEY: " + e.toString(), e);
            }
        }
    }

    private static class PublicKeyParser
    implements PemObjectParser {
        public Object parseObject(PemObject obj) throws IOException {
            return SubjectPublicKeyInfo.getInstance((Object)obj.getContent());
        }
    }

    private static class RSAKeyPairParser
    implements PEMKeyPairParser {
        private RSAKeyPairParser() {
        }

        @Override
        public PEMKeyPair parse(byte[] encoding) throws IOException {
            try {
                ASN1Sequence seq = ASN1Sequence.getInstance((Object)encoding);
                if (seq.size() != 9) {
                    throw new PEMException("malformed sequence in RSA private key");
                }
                RSAPrivateKey keyStruct = RSAPrivateKey.getInstance((Object)seq);
                RSAPublicKey pubSpec = new RSAPublicKey(keyStruct.getModulus(), keyStruct.getPublicExponent());
                AlgorithmIdentifier algId = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE);
                return new PEMKeyPair(new SubjectPublicKeyInfo(algId, (ASN1Encodable)pubSpec), new PrivateKeyInfo(algId, (ASN1Encodable)keyStruct));
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new PEMException("problem creating RSA private key: " + e.toString(), e);
            }
        }
    }

    private static class RSAPublicKeyParser
    implements PemObjectParser {
        public Object parseObject(PemObject obj) throws IOException {
            try {
                RSAPublicKey rsaPubStructure = RSAPublicKey.getInstance((Object)obj.getContent());
                return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE), (ASN1Encodable)rsaPubStructure);
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new PEMException("problem extracting key: " + e.toString(), e);
            }
        }
    }

    private static class X509AttributeCertificateParser
    implements PemObjectParser {
        private X509AttributeCertificateParser() {
        }

        public Object parseObject(PemObject obj) throws IOException {
            return new X509AttributeCertificateHolder(obj.getContent());
        }
    }

    private static class X509CRLParser
    implements PemObjectParser {
        private X509CRLParser() {
        }

        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new X509CRLHolder(obj.getContent());
            }
            catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    private static class X509CertificateParser
    implements PemObjectParser {
        private X509CertificateParser() {
        }

        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new X509CertificateHolder(obj.getContent());
            }
            catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    private static class X509TrustedCertificateParser
    implements PemObjectParser {
        private X509TrustedCertificateParser() {
        }

        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new X509TrustedCertificateBlock(obj.getContent());
            }
            catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }
}

