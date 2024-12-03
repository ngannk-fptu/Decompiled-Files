/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import java.util.List;
import org.apache.commons.jrcs.diff.Chunk;
import org.apache.commons.jrcs.diff.Delta;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.PatchFailedException;
import org.apache.commons.jrcs.diff.RevisionVisitor;

public class DeleteDelta
extends Delta {
    DeleteDelta() {
    }

    public DeleteDelta(Chunk orig) {
        this.init(orig, null);
    }

    public void verify(List target) throws PatchFailedException {
        if (!this.original.verify(target)) {
            throw new PatchFailedException();
        }
    }

    public void applyTo(List target) {
        this.original.applyDelete(target);
    }

    public void toString(StringBuffer s) {
        s.append(this.original.rangeString());
        s.append("d");
        s.append(this.revised.rcsto());
        s.append(Diff.NL);
        this.original.toString(s, "< ", Diff.NL);
    }

    public void toRCSString(StringBuffer s, String EOL) {
        s.append("d");
        s.append(this.original.rcsfrom());
        s.append(" ");
        s.append(this.original.size());
        s.append(EOL);
    }

    public void accept(RevisionVisitor visitor) {
        visitor.visit(this);
    }
}

