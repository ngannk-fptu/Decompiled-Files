/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.Tree;

public interface SyntaxTree
extends Tree {
    public Interval getSourceInterval();
}

