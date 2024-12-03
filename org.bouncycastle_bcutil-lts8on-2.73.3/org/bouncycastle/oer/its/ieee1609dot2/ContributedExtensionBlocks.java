/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.ContributedExtensionBlock;

public class ContributedExtensionBlocks
extends ASN1Object {
    private final List<ContributedExtensionBlock> contributedExtensionBlocks;

    public ContributedExtensionBlocks(List<ContributedExtensionBlock> extensionBlocks) {
        this.contributedExtensionBlocks = Collections.unmodifiableList(extensionBlocks);
    }

    private ContributedExtensionBlocks(ASN1Sequence sequence) {
        ArrayList<ContributedExtensionBlock> blocks = new ArrayList<ContributedExtensionBlock>();
        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            blocks.add(ContributedExtensionBlock.getInstance(it.next()));
        }
        this.contributedExtensionBlocks = Collections.unmodifiableList(blocks);
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<ContributedExtensionBlock> getContributedExtensionBlocks() {
        return this.contributedExtensionBlocks;
    }

    public int size() {
        return this.contributedExtensionBlocks.size();
    }

    public static ContributedExtensionBlocks getInstance(Object o) {
        if (o instanceof ContributedExtensionBlocks) {
            return (ContributedExtensionBlocks)((Object)o);
        }
        if (o != null) {
            return new ContributedExtensionBlocks(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[])this.contributedExtensionBlocks.toArray(new ContributedExtensionBlock[0]));
    }

    public static class Builder {
        private final List<ContributedExtensionBlock> extensionBlocks = new ArrayList<ContributedExtensionBlock>();

        public Builder add(ContributedExtensionBlock ... blocks) {
            this.extensionBlocks.addAll(Arrays.asList(blocks));
            return this;
        }

        public ContributedExtensionBlocks build() {
            return new ContributedExtensionBlocks(this.extensionBlocks);
        }
    }
}

