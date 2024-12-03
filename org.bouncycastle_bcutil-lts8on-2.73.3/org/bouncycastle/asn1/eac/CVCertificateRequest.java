/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1ParsingException
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.CertificateBody;
import org.bouncycastle.asn1.eac.EACTagged;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.util.Arrays;

public class CVCertificateRequest
extends ASN1Object {
    private final ASN1TaggedObject original;
    private CertificateBody certificateBody;
    private byte[] innerSignature = null;
    private byte[] outerSignature = null;
    private static final int bodyValid = 1;
    private static final int signValid = 2;

    private CVCertificateRequest(ASN1TaggedObject request) throws IOException {
        this.original = request;
        if (request.hasTag(64, 7)) {
            ASN1Sequence seq = ASN1Sequence.getInstance((Object)request.getBaseUniversal(false, 16));
            this.initCertBody(ASN1TaggedObject.getInstance((Object)seq.getObjectAt(0), (int)64));
            this.outerSignature = ASN1OctetString.getInstance((Object)ASN1TaggedObject.getInstance((Object)seq.getObjectAt(seq.size() - 1)).getBaseUniversal(false, 4)).getOctets();
        } else {
            this.initCertBody(request);
        }
    }

    private void initCertBody(ASN1TaggedObject request) throws IOException {
        if (request.hasTag(64, 33)) {
            int valid = 0;
            ASN1Sequence seq = ASN1Sequence.getInstance((Object)request.getBaseUniversal(false, 16));
            Enumeration en = seq.getObjects();
            block4: while (en.hasMoreElements()) {
                ASN1TaggedObject tObj = ASN1TaggedObject.getInstance(en.nextElement(), (int)64);
                switch (tObj.getTagNo()) {
                    case 78: {
                        this.certificateBody = CertificateBody.getInstance(tObj);
                        valid |= 1;
                        continue block4;
                    }
                    case 55: {
                        this.innerSignature = ASN1OctetString.getInstance((Object)tObj.getBaseUniversal(false, 4)).getOctets();
                        valid |= 2;
                        continue block4;
                    }
                }
                throw new IOException("Invalid tag, not an CV Certificate Request element:" + tObj.getTagNo());
            }
            if ((valid & 3) == 0) {
                throw new IOException("Invalid CARDHOLDER_CERTIFICATE in request:" + request.getTagNo());
            }
        } else {
            throw new IOException("not a CARDHOLDER_CERTIFICATE in request:" + request.getTagNo());
        }
    }

    public static CVCertificateRequest getInstance(Object obj) {
        if (obj instanceof CVCertificateRequest) {
            return (CVCertificateRequest)((Object)obj);
        }
        if (obj != null) {
            try {
                return new CVCertificateRequest(ASN1TaggedObject.getInstance((Object)obj, (int)64));
            }
            catch (IOException e) {
                throw new ASN1ParsingException("unable to parse data: " + e.getMessage(), (Throwable)e);
            }
        }
        return null;
    }

    public CertificateBody getCertificateBody() {
        return this.certificateBody;
    }

    public PublicKeyDataObject getPublicKey() {
        return this.certificateBody.getPublicKey();
    }

    public byte[] getInnerSignature() {
        return Arrays.clone((byte[])this.innerSignature);
    }

    public byte[] getOuterSignature() {
        return Arrays.clone((byte[])this.outerSignature);
    }

    public boolean hasOuterSignature() {
        return this.outerSignature != null;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.original != null) {
            return this.original;
        }
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.certificateBody);
        v.add((ASN1Encodable)EACTagged.create(55, this.innerSignature));
        return EACTagged.create(33, (ASN1Sequence)new DERSequence(v));
    }
}

