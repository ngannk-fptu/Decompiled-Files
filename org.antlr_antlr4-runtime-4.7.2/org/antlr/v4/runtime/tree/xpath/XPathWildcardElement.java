/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.runtime.tree.xpath.XPathElement;

public class XPathWildcardElement
extends XPathElement {
    public XPathWildcardElement() {
        super("*");
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        if (this.invert) {
            return new ArrayList<ParseTree>();
        }
        ArrayList<ParseTree> kids = new ArrayList<ParseTree>();
        for (Tree c : Trees.getChildren(t)) {
            kids.add((ParseTree)c);
        }
        return kids;
    }
}

