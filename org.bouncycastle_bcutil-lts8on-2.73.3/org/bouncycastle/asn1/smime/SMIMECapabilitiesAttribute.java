/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERSet
 */
package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.smime.SMIMEAttributes;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;

public class SMIMECapabilitiesAttribute
extends Attribute {
    public SMIMECapabilitiesAttribute(SMIMECapabilityVector capabilities) {
        super(SMIMEAttributes.smimeCapabilities, (ASN1Set)new DERSet((ASN1Encodable)new DERSequence(capabilities.toASN1EncodableVector())));
    }
}

