/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.pkcs.Attribute
 *  org.bouncycastle.asn1.pkcs.CertificationRequest
 *  org.bouncycastle.asn1.pkcs.CertificationRequestInfo
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.util.Exceptions
 */
package org.bouncycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.PKCSIOException;
import org.bouncycastle.util.Exceptions;

public class PKCS10CertificationRequest {
    private static Attribute[] EMPTY_ARRAY = new Attribute[0];
    private CertificationRequest certificationRequest;

    private static CertificationRequest parseBytes(byte[] encoding) throws IOException {
        try {
            CertificationRequest rv = CertificationRequest.getInstance((Object)ASN1Primitive.fromByteArray((byte[])encoding));
            if (rv == null) {
                throw new PKCSIOException("empty data passed to constructor");
            }
            return rv;
        }
        catch (ClassCastException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public PKCS10CertificationRequest(CertificationRequest certificationRequest) {
        if (certificationRequest == null) {
            throw new NullPointerException("certificationRequest cannot be null");
        }
        this.certificationRequest = certificationRequest;
    }

    public PKCS10CertificationRequest(byte[] encoded) throws IOException {
        this(PKCS10CertificationRequest.parseBytes(encoded));
    }

    public CertificationRequest toASN1Structure() {
        return this.certificationRequest;
    }

    public X500Name getSubject() {
        return X500Name.getInstance((Object)this.certificationRequest.getCertificationRequestInfo().getSubject());
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.certificationRequest.getSignatureAlgorithm();
    }

    public byte[] getSignature() {
        return this.certificationRequest.getSignature().getOctets();
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.certificationRequest.getCertificationRequestInfo().getSubjectPublicKeyInfo();
    }

    public Attribute[] getAttributes() {
        ASN1Set attrSet = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (attrSet == null) {
            return EMPTY_ARRAY;
        }
        Attribute[] attrs = new Attribute[attrSet.size()];
        for (int i = 0; i != attrSet.size(); ++i) {
            attrs[i] = Attribute.getInstance((Object)attrSet.getObjectAt(i));
        }
        return attrs;
    }

    public Attribute[] getAttributes(ASN1ObjectIdentifier type) {
        ASN1Set attrSet = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (attrSet == null) {
            return EMPTY_ARRAY;
        }
        ArrayList<Attribute> list = new ArrayList<Attribute>();
        for (int i = 0; i != attrSet.size(); ++i) {
            Attribute attr = Attribute.getInstance((Object)attrSet.getObjectAt(i));
            if (!attr.getAttrType().equals((ASN1Primitive)type)) continue;
            list.add(attr);
        }
        if (list.size() == 0) {
            return EMPTY_ARRAY;
        }
        return list.toArray(new Attribute[list.size()]);
    }

    public byte[] getEncoded() throws IOException {
        return this.certificationRequest.getEncoded();
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws PKCSException {
        ContentVerifier verifier;
        CertificationRequestInfo requestInfo = this.certificationRequest.getCertificationRequestInfo();
        try {
            verifier = verifierProvider.get(this.certificationRequest.getSignatureAlgorithm());
            OutputStream sOut = verifier.getOutputStream();
            sOut.write(requestInfo.getEncoded("DER"));
            sOut.close();
        }
        catch (Exception e) {
            throw new PKCSException("unable to process signature: " + e.getMessage(), e);
        }
        return verifier.verify(this.getSignature());
    }

    public Extensions getRequestedExtensions() {
        Attribute[] attributes = this.getAttributes();
        for (int i = 0; i != attributes.length; ++i) {
            Attribute encodable = attributes[i];
            if (!PKCSObjectIdentifiers.pkcs_9_at_extensionRequest.equals((ASN1Primitive)encodable.getAttrType())) continue;
            ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
            ASN1Set attrValues = encodable.getAttrValues();
            if (attrValues == null || attrValues.size() == 0) {
                throw new IllegalStateException("pkcs_9_at_extensionRequest present but has no value");
            }
            ASN1Sequence extensionSequence = ASN1Sequence.getInstance((Object)attrValues.getObjectAt(0));
            try {
                Enumeration en = extensionSequence.getObjects();
                while (en.hasMoreElements()) {
                    boolean critical;
                    ASN1Sequence itemSeq = ASN1Sequence.getInstance(en.nextElement());
                    boolean bl = critical = itemSeq.size() == 3 && ASN1Boolean.getInstance((Object)itemSeq.getObjectAt(1)).isTrue();
                    if (itemSeq.size() == 2) {
                        extensionsGenerator.addExtension(ASN1ObjectIdentifier.getInstance((Object)itemSeq.getObjectAt(0)), false, ASN1OctetString.getInstance((Object)itemSeq.getObjectAt(1)).getOctets());
                        continue;
                    }
                    if (itemSeq.size() == 3) {
                        extensionsGenerator.addExtension(ASN1ObjectIdentifier.getInstance((Object)itemSeq.getObjectAt(0)), critical, ASN1OctetString.getInstance((Object)itemSeq.getObjectAt(2)).getOctets());
                        continue;
                    }
                    throw new IllegalStateException("incorrect sequence size of Extension get " + itemSeq.size() + " expected 2 or three");
                }
            }
            catch (IllegalArgumentException e) {
                throw Exceptions.illegalStateException((String)("asn1 processing issue: " + e.getMessage()), (Throwable)e);
            }
            return extensionsGenerator.generate();
        }
        return null;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PKCS10CertificationRequest)) {
            return false;
        }
        PKCS10CertificationRequest other = (PKCS10CertificationRequest)o;
        return this.toASN1Structure().equals((Object)other.toASN1Structure());
    }

    public int hashCode() {
        return this.toASN1Structure().hashCode();
    }
}

