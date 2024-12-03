/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.runtime.tree.xpath.XPathElement;

public class XPathRuleElement
extends XPathElement {
    protected int ruleIndex;

    public XPathRuleElement(String ruleName, int ruleIndex) {
        super(ruleName);
        this.ruleIndex = ruleIndex;
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        ArrayList<ParseTree> nodes = new ArrayList<ParseTree>();
        for (Tree c : Trees.getChildren(t)) {
            ParserRuleContext ctx;
            if (!(c instanceof ParserRuleContext) || ((ctx = (ParserRuleContext)c).getRuleIndex() != this.ruleIndex || this.invert) && (ctx.getRuleIndex() == this.ruleIndex || !this.invert)) continue;
            nodes.add(ctx);
        }
        return nodes;
    }
}

