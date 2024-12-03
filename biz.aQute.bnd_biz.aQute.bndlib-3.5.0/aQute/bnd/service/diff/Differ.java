/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.diff;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Jar;
import aQute.bnd.service.diff.Tree;

public interface Differ {
    public Tree tree(Analyzer var1) throws Exception;

    public Tree tree(Jar var1) throws Exception;

    public Tree deserialize(Tree.Data var1) throws Exception;
}

