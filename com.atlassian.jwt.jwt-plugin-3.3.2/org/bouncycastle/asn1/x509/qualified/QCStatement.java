/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.qualified.ETSIQCObjectIdentifiers;
import org.bouncycastle.asn1.x509.qualified.RFC3739QCObjectIdentifiers;

public class QCStatement
extends ASN1Object
implements ETSIQCObjectIdentifiers,
RFC3739QCObjectIdentifiers {
    ASN1ObjectIdentifier qcStatementId;
    ASN1Encodable qcStatementInfo;

    public static QCStatement getInstance(Object object) {
        if (object instanceof QCStatement) {
            return (QCStatement)object;
        }
        if (object != null) {
            return new QCStatement(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private QCStatement(ASN1Sequence aSN1Sequence) {
        Enumeration enumeration = aSN1Sequence.getObjects();
        this.qcStatementId = ASN1ObjectIdentifier.getInstance(enumeration.nextElement());
        if (enumeration.hasMoreElements()) {
            this.qcStatementInfo = (ASN1Encodable)enumeration.nextElement();
        }
    }

    public QCStatement(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.qcStatementId = aSN1ObjectIdentifier;
        this.qcStatementInfo = null;
    }

    public QCStatement(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.qcStatementId = aSN1ObjectIdentifier;
        this.qcStatementInfo = aSN1Encodable;
    }

    public ASN1ObjectIdentifier getStatementId() {
        return this.qcStatementId;
    }

    public ASN1Encodable getStatementInfo() {
        return this.qcStatementInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.qcStatementId);
        if (this.qcStatementInfo != null) {
            aSN1EncodableVector.add(this.qcStatementInfo);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

