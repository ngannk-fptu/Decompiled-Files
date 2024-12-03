/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.outerj.daisy.diff.tag.Atom;

public interface IAtomSplitter
extends IRangeComparator {
    public Atom getAtom(int var1);

    public String substring(int var1, int var2);

    public String substring(int var1);
}

