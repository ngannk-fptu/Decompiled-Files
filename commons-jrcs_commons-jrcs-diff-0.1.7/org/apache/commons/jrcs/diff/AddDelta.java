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

public class AddDelta
extends Delta {
    AddDelta() {
    }

    public AddDelta(int origpos, Chunk rev) {
        this.init(new Chunk(origpos, 0), rev);
    }

    public void verify(List target) throws PatchFailedException {
        if (this.original.first() > target.size()) {
            throw new PatchFailedException("original.first() > target.size()");
        }
    }

    public void applyTo(List target) {
        this.revised.applyAdd(this.original.first(), target);
    }

    public void toString(StringBuffer s) {
        s.append(this.original.anchor());
        s.append("a");
        s.append(this.revised.rangeString());
        s.append(Diff.NL);
        this.revised.toString(s, "> ", Diff.NL);
    }

    public void toRCSString(StringBuffer s, String EOL) {
        s.append("a");
        s.append(this.original.anchor());
        s.append(" ");
        s.append(this.revised.size());
        s.append(EOL);
        this.revised.toString(s, "", EOL);
    }

    public void Accept(RevisionVisitor visitor) {
        visitor.visit(this);
    }

    public void accept(RevisionVisitor visitor) {
        visitor.visit(this);
    }
}

