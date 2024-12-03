/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public interface RuleNode
extends ParseTree {
    public RuleContext getRuleContext();
}

