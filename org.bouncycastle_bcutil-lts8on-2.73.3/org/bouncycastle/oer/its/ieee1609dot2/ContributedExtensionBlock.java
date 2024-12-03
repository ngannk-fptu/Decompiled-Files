/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiOriginatingHeaderInfoExtension;
import org.bouncycastle.oer.its.ieee1609dot2.HeaderInfoContributorId;

public class ContributedExtensionBlock
extends ASN1Object {
    private final HeaderInfoContributorId contributorId;
    private final List<EtsiOriginatingHeaderInfoExtension> extns;

    public ContributedExtensionBlock(HeaderInfoContributorId contributorId, List<EtsiOriginatingHeaderInfoExtension> extns) {
        this.contributorId = contributorId;
        this.extns = extns;
    }

    private ContributedExtensionBlock(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.contributorId = HeaderInfoContributorId.getInstance(sequence.getObjectAt(0));
        Iterator items = ASN1Sequence.getInstance((Object)sequence.getObjectAt(1)).iterator();
        ArrayList<EtsiOriginatingHeaderInfoExtension> extns = new ArrayList<EtsiOriginatingHeaderInfoExtension>();
        while (items.hasNext()) {
            extns.add(EtsiOriginatingHeaderInfoExtension.getInstance(items.next()));
        }
        this.extns = Collections.unmodifiableList(extns);
    }

    public static ContributedExtensionBlock getInstance(Object src) {
        if (src instanceof ContributedExtensionBlock) {
            return (ContributedExtensionBlock)((Object)src);
        }
        if (src != null) {
            return new ContributedExtensionBlock(ASN1Sequence.getInstance((Object)src));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.contributorId, ItsUtils.toSequence(this.extns)});
    }

    public HeaderInfoContributorId getContributorId() {
        return this.contributorId;
    }

    public List<EtsiOriginatingHeaderInfoExtension> getExtns() {
        return this.extns;
    }
}

