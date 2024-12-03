/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.AttributeCertificate
 *  org.bouncycastle.asn1.x509.AttributeCertificateInfo
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.CertificateList
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.asn1.x509.TBSCertList
 *  org.bouncycastle.asn1.x509.TBSCertificate
 *  org.bouncycastle.util.Properties
 */
package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Properties;

class CertUtils {
    private static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
    private static List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());

    CertUtils() {
    }

    static ASN1Primitive parseNonEmptyASN1(byte[] encoding) throws IOException {
        ASN1Primitive p = ASN1Primitive.fromByteArray((byte[])encoding);
        if (p == null) {
            throw new IOException("no content found");
        }
        return p;
    }

    static X509CertificateHolder generateFullCert(ContentSigner signer, TBSCertificate tbsCert) {
        try {
            return new X509CertificateHolder(CertUtils.generateStructure(tbsCert, signer.getAlgorithmIdentifier(), CertUtils.generateSig(signer, (ASN1Object)tbsCert)));
        }
        catch (IOException e) {
            throw new IllegalStateException("cannot produce certificate signature");
        }
    }

    static X509AttributeCertificateHolder generateFullAttrCert(ContentSigner signer, AttributeCertificateInfo attrInfo) {
        try {
            return new X509AttributeCertificateHolder(CertUtils.generateAttrStructure(attrInfo, signer.getAlgorithmIdentifier(), CertUtils.generateSig(signer, (ASN1Object)attrInfo)));
        }
        catch (IOException e) {
            throw new IllegalStateException("cannot produce attribute certificate signature");
        }
    }

    private static Certificate generateStructure(TBSCertificate tbsCert, AlgorithmIdentifier sigAlgId, byte[] signature) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)tbsCert);
        v.add((ASN1Encodable)sigAlgId);
        v.add((ASN1Encodable)new DERBitString(signature));
        return Certificate.getInstance((Object)new DERSequence(v));
    }

    private static AttributeCertificate generateAttrStructure(AttributeCertificateInfo attrInfo, AlgorithmIdentifier sigAlgId, byte[] signature) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)attrInfo);
        v.add((ASN1Encodable)sigAlgId);
        v.add((ASN1Encodable)new DERBitString(signature));
        return AttributeCertificate.getInstance((Object)new DERSequence(v));
    }

    private static CertificateList generateCRLStructure(TBSCertList tbsCertList, AlgorithmIdentifier sigAlgId, byte[] signature) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)tbsCertList);
        v.add((ASN1Encodable)sigAlgId);
        v.add((ASN1Encodable)new DERBitString(signature));
        return CertificateList.getInstance((Object)new DERSequence(v));
    }

    static Set getCriticalExtensionOIDs(Extensions extensions) {
        if (extensions == null) {
            return EMPTY_SET;
        }
        return Collections.unmodifiableSet(new HashSet<ASN1ObjectIdentifier>(Arrays.asList(extensions.getCriticalExtensionOIDs())));
    }

    static Set getNonCriticalExtensionOIDs(Extensions extensions) {
        if (extensions == null) {
            return EMPTY_SET;
        }
        return Collections.unmodifiableSet(new HashSet<ASN1ObjectIdentifier>(Arrays.asList(extensions.getNonCriticalExtensionOIDs())));
    }

    static List getExtensionOIDs(Extensions extensions) {
        if (extensions == null) {
            return EMPTY_LIST;
        }
        return Collections.unmodifiableList(Arrays.asList(extensions.getExtensionOIDs()));
    }

    static void addExtension(ExtensionsGenerator extGenerator, ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        try {
            extGenerator.addExtension(oid, isCritical, value);
        }
        catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
    }

    static DERBitString booleanToBitString(boolean[] id) {
        byte[] bytes = new byte[(id.length + 7) / 8];
        for (int i = 0; i != id.length; ++i) {
            int n = i / 8;
            bytes[n] = (byte)(bytes[n] | (id[i] ? 1 << 7 - i % 8 : 0));
        }
        int pad = id.length % 8;
        if (pad == 0) {
            return new DERBitString(bytes);
        }
        return new DERBitString(bytes, 8 - pad);
    }

    static boolean[] bitStringToBoolean(ASN1BitString bitString) {
        if (bitString != null) {
            byte[] bytes = bitString.getBytes();
            boolean[] boolId = new boolean[bytes.length * 8 - bitString.getPadBits()];
            for (int i = 0; i != boolId.length; ++i) {
                boolId[i] = (bytes[i / 8] & 128 >>> i % 8) != 0;
            }
            return boolId;
        }
        return null;
    }

    static Date recoverDate(ASN1GeneralizedTime time) {
        try {
            return time.getDate();
        }
        catch (ParseException e) {
            throw new IllegalStateException("unable to recover date: " + e.getMessage());
        }
    }

    static boolean isAlgIdEqual(AlgorithmIdentifier id1, AlgorithmIdentifier id2) {
        if (!id1.getAlgorithm().equals((ASN1Primitive)id2.getAlgorithm())) {
            return false;
        }
        if (Properties.isOverrideSet((String)"org.bouncycastle.x509.allow_absent_equiv_NULL")) {
            if (id1.getParameters() == null) {
                return id2.getParameters() == null || id2.getParameters().equals(DERNull.INSTANCE);
            }
            if (id2.getParameters() == null) {
                return id1.getParameters() == null || id1.getParameters().equals(DERNull.INSTANCE);
            }
        }
        if (id1.getParameters() != null) {
            return id1.getParameters().equals(id2.getParameters());
        }
        if (id2.getParameters() != null) {
            return id2.getParameters().equals(id1.getParameters());
        }
        return true;
    }

    static ExtensionsGenerator doReplaceExtension(ExtensionsGenerator extGenerator, Extension ext) {
        boolean isReplaced = false;
        Extensions exts = extGenerator.generate();
        extGenerator = new ExtensionsGenerator();
        Enumeration en = exts.oids();
        while (en.hasMoreElements()) {
            ASN1ObjectIdentifier extOid = (ASN1ObjectIdentifier)en.nextElement();
            if (extOid.equals((ASN1Primitive)ext.getExtnId())) {
                isReplaced = true;
                extGenerator.addExtension(ext);
                continue;
            }
            extGenerator.addExtension(exts.getExtension(extOid));
        }
        if (!isReplaced) {
            throw new IllegalArgumentException("replace - original extension (OID = " + ext.getExtnId() + ") not found");
        }
        return extGenerator;
    }

    static ExtensionsGenerator doRemoveExtension(ExtensionsGenerator extGenerator, ASN1ObjectIdentifier oid) {
        boolean isRemoved = false;
        Extensions exts = extGenerator.generate();
        extGenerator = new ExtensionsGenerator();
        Enumeration en = exts.oids();
        while (en.hasMoreElements()) {
            ASN1ObjectIdentifier extOid = (ASN1ObjectIdentifier)en.nextElement();
            if (extOid.equals((ASN1Primitive)oid)) {
                isRemoved = true;
                continue;
            }
            extGenerator.addExtension(exts.getExtension(extOid));
        }
        if (!isRemoved) {
            throw new IllegalArgumentException("remove - extension (OID = " + oid + ") not found");
        }
        return extGenerator;
    }

    private static byte[] generateSig(ContentSigner signer, ASN1Object tbsObj) throws IOException {
        OutputStream sOut = signer.getOutputStream();
        tbsObj.encodeTo(sOut, "DER");
        sOut.close();
        return signer.getSignature();
    }

    static ASN1TaggedObject trimExtensions(int tagNo, Extensions exts) {
        ASN1Sequence extSeq = ASN1Sequence.getInstance((Object)exts.toASN1Primitive());
        ASN1EncodableVector extV = new ASN1EncodableVector();
        for (int i = 0; i != extSeq.size(); ++i) {
            ASN1Sequence ext = ASN1Sequence.getInstance((Object)extSeq.getObjectAt(i));
            if (Extension.altSignatureValue.equals(ext.getObjectAt(0))) continue;
            extV.add((ASN1Encodable)ext);
        }
        return new DERTaggedObject(true, tagNo, (ASN1Encodable)new DERSequence(extV));
    }
}

