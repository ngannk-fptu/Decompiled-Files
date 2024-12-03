/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import org.apache.commons.jrcs.diff.AddDelta;
import org.apache.commons.jrcs.diff.ChangeDelta;
import org.apache.commons.jrcs.diff.DeleteDelta;
import org.apache.commons.jrcs.diff.Revision;

public interface RevisionVisitor {
    public void visit(Revision var1);

    public void visit(DeleteDelta var1);

    public void visit(ChangeDelta var1);

    public void visit(AddDelta var1);
}

