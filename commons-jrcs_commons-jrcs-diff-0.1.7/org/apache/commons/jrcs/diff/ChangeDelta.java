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

public class ChangeDelta
extends Delta {
    ChangeDelta() {
    }

    public ChangeDelta(Chunk orig, Chunk rev) {
        this.init(orig, rev);
    }

    public void verify(List target) throws PatchFailedException {
        if (!this.original.verify(target)) {
            throw new PatchFailedException();
        }
        if (this.original.first() > target.size()) {
            throw new PatchFailedException("original.first() > target.size()");
        }
    }

    public void applyTo(List target) {
        this.original.applyDelete(target);
        this.revised.applyAdd(this.original.first(), target);
    }

    public void toString(StringBuffer s) {
        this.original.rangeString(s);
        s.append("c");
        this.revised.rangeString(s);
        s.append(Diff.NL);
        this.original.toString(s, "< ", "\n");
        s.append("---");
        s.append(Diff.NL);
        this.revised.toString(s, "> ", "\n");
    }

    public void toRCSString(StringBuffer s, String EOL) {
        s.append("d");
        s.append(this.original.rcsfrom());
        s.append(" ");
        s.append(this.original.size());
        s.append(EOL);
        s.append("a");
        s.append(this.original.rcsto());
        s.append(" ");
        s.append(this.revised.size());
        s.append(EOL);
        this.revised.toString(s, "", EOL);
    }

    public void accept(RevisionVisitor visitor) {
        visitor.visit(this);
    }
}

