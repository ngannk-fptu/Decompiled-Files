/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.DSAParameter
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.io.pem.PemGenerationException
 *  org.bouncycastle.util.io.pem.PemHeader
 *  org.bouncycastle.util.io.pem.PemObject
 *  org.bouncycastle.util.io.pem.PemObjectGenerator
 */
package org.bouncycastle.openssl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.X509TrustedCertificateBlock;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

public class MiscPEMGenerator
implements PemObjectGenerator {
    private static final ASN1ObjectIdentifier[] dsaOids = new ASN1ObjectIdentifier[]{X9ObjectIdentifiers.id_dsa, OIWObjectIdentifiers.dsaWithSHA1};
    private static final byte[] hexEncodingTable = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    private final Object obj;
    private final PEMEncryptor encryptor;

    public MiscPEMGenerator(Object o) {
        this.obj = o;
        this.encryptor = null;
    }

    public MiscPEMGenerator(Object o, PEMEncryptor encryptor) {
        this.obj = o;
        this.encryptor = encryptor;
    }

    private PemObject createPemObject(Object o) throws IOException {
        byte[] encoding;
        String type;
        if (o instanceof PemObject) {
            return (PemObject)o;
        }
        if (o instanceof PemObjectGenerator) {
            return ((PemObjectGenerator)o).generate();
        }
        if (o instanceof X509CertificateHolder) {
            type = "CERTIFICATE";
            encoding = ((X509CertificateHolder)o).getEncoded();
        } else if (o instanceof X509CRLHolder) {
            type = "X509 CRL";
            encoding = ((X509CRLHolder)o).getEncoded();
        } else if (o instanceof X509TrustedCertificateBlock) {
            type = "TRUSTED CERTIFICATE";
            encoding = ((X509TrustedCertificateBlock)o).getEncoded();
        } else if (o instanceof PrivateKeyInfo) {
            PrivateKeyInfo info = (PrivateKeyInfo)o;
            ASN1ObjectIdentifier algOID = info.getPrivateKeyAlgorithm().getAlgorithm();
            if (algOID.equals((ASN1Primitive)PKCSObjectIdentifiers.rsaEncryption)) {
                type = "RSA PRIVATE KEY";
                encoding = info.parsePrivateKey().toASN1Primitive().getEncoded();
            } else if (algOID.equals((ASN1Primitive)dsaOids[0]) || algOID.equals((ASN1Primitive)dsaOids[1])) {
                type = "DSA PRIVATE KEY";
                DSAParameter p = DSAParameter.getInstance((Object)info.getPrivateKeyAlgorithm().getParameters());
                ASN1EncodableVector v = new ASN1EncodableVector();
                v.add((ASN1Encodable)new ASN1Integer(0L));
                v.add((ASN1Encodable)new ASN1Integer(p.getP()));
                v.add((ASN1Encodable)new ASN1Integer(p.getQ()));
                v.add((ASN1Encodable)new ASN1Integer(p.getG()));
                BigInteger x = ASN1Integer.getInstance((Object)info.parsePrivateKey()).getValue();
                BigInteger y = p.getG().modPow(x, p.getP());
                v.add((ASN1Encodable)new ASN1Integer(y));
                v.add((ASN1Encodable)new ASN1Integer(x));
                encoding = new DERSequence(v).getEncoded();
            } else if (algOID.equals((ASN1Primitive)X9ObjectIdentifiers.id_ecPublicKey)) {
                type = "EC PRIVATE KEY";
                encoding = info.parsePrivateKey().toASN1Primitive().getEncoded();
            } else {
                type = "PRIVATE KEY";
                encoding = info.getEncoded();
            }
        } else if (o instanceof SubjectPublicKeyInfo) {
            type = "PUBLIC KEY";
            encoding = ((SubjectPublicKeyInfo)o).getEncoded();
        } else if (o instanceof X509AttributeCertificateHolder) {
            type = "ATTRIBUTE CERTIFICATE";
            encoding = ((X509AttributeCertificateHolder)o).getEncoded();
        } else if (o instanceof PKCS10CertificationRequest) {
            type = "CERTIFICATE REQUEST";
            encoding = ((PKCS10CertificationRequest)o).getEncoded();
        } else if (o instanceof PKCS8EncryptedPrivateKeyInfo) {
            type = "ENCRYPTED PRIVATE KEY";
            encoding = ((PKCS8EncryptedPrivateKeyInfo)o).getEncoded();
        } else if (o instanceof ContentInfo) {
            type = "PKCS7";
            encoding = ((ContentInfo)o).getEncoded();
        } else {
            throw new PemGenerationException("unknown object passed - can't encode.");
        }
        if (this.encryptor != null) {
            String dekAlgName = Strings.toUpperCase((String)this.encryptor.getAlgorithm());
            if (dekAlgName.equals("DESEDE")) {
                dekAlgName = "DES-EDE3-CBC";
            }
            byte[] iv = this.encryptor.getIV();
            byte[] encData = this.encryptor.encrypt(encoding);
            ArrayList<PemHeader> headers = new ArrayList<PemHeader>(2);
            headers.add(new PemHeader("Proc-Type", "4,ENCRYPTED"));
            headers.add(new PemHeader("DEK-Info", dekAlgName + "," + this.getHexEncoded(iv)));
            return new PemObject(type, headers, encData);
        }
        return new PemObject(type, encoding);
    }

    private String getHexEncoded(byte[] bytes) throws IOException {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i != bytes.length; ++i) {
            int v = bytes[i] & 0xFF;
            chars[2 * i] = (char)hexEncodingTable[v >>> 4];
            chars[2 * i + 1] = (char)hexEncodingTable[v & 0xF];
        }
        return new String(chars);
    }

    public PemObject generate() throws PemGenerationException {
        try {
            return this.createPemObject(this.obj);
        }
        catch (IOException e) {
            throw new PemGenerationException("encoding exception: " + e.getMessage(), (Throwable)e);
        }
    }
}

