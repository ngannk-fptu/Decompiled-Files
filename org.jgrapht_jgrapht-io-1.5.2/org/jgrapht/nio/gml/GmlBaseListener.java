/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ParserRuleContext
 *  org.antlr.v4.runtime.tree.ErrorNode
 *  org.antlr.v4.runtime.tree.TerminalNode
 */
package org.jgrapht.nio.gml;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jgrapht.nio.gml.GmlListener;
import org.jgrapht.nio.gml.GmlParser;

class GmlBaseListener
implements GmlListener {
    GmlBaseListener() {
    }

    @Override
    public void enterGml(GmlParser.GmlContext ctx) {
    }

    @Override
    public void exitGml(GmlParser.GmlContext ctx) {
    }

    @Override
    public void enterStringKeyValue(GmlParser.StringKeyValueContext ctx) {
    }

    @Override
    public void exitStringKeyValue(GmlParser.StringKeyValueContext ctx) {
    }

    @Override
    public void enterNumberKeyValue(GmlParser.NumberKeyValueContext ctx) {
    }

    @Override
    public void exitNumberKeyValue(GmlParser.NumberKeyValueContext ctx) {
    }

    @Override
    public void enterListKeyValue(GmlParser.ListKeyValueContext ctx) {
    }

    @Override
    public void exitListKeyValue(GmlParser.ListKeyValueContext ctx) {
    }

    public void enterEveryRule(ParserRuleContext ctx) {
    }

    public void exitEveryRule(ParserRuleContext ctx) {
    }

    public void visitTerminal(TerminalNode node) {
    }

    public void visitErrorNode(ErrorNode node) {
    }
}

