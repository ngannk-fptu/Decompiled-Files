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
package org.bouncycastle.oer.its.etsi102941;

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
import org.bouncycastle.oer.its.etsi102941.CtlCommand;

public class SequenceOfCtlCommand
extends ASN1Object {
    private final List<CtlCommand> ctlCommands;

    public SequenceOfCtlCommand(List<CtlCommand> hashedId8s) {
        this.ctlCommands = Collections.unmodifiableList(hashedId8s);
    }

    private SequenceOfCtlCommand(ASN1Sequence sequence) {
        ArrayList<CtlCommand> items = new ArrayList<CtlCommand>();
        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            items.add(CtlCommand.getInstance(it.next()));
        }
        this.ctlCommands = Collections.unmodifiableList(items);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfCtlCommand getInstance(Object o) {
        if (o instanceof SequenceOfCtlCommand) {
            return (SequenceOfCtlCommand)((Object)o);
        }
        if (o != null) {
            return new SequenceOfCtlCommand(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<CtlCommand> getCtlCommands() {
        return this.ctlCommands;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.ctlCommands.toArray(new ASN1Encodable[0]));
    }

    public static class Builder {
        private final List<CtlCommand> items = new ArrayList<CtlCommand>();

        public Builder addHashId8(CtlCommand ... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public SequenceOfCtlCommand build() {
            return new SequenceOfCtlCommand(this.items);
        }
    }
}

