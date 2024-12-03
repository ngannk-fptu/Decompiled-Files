/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ParserRuleContext
 *  org.antlr.v4.runtime.tree.ErrorNode
 *  org.antlr.v4.runtime.tree.TerminalNode
 */
package org.jgrapht.io;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jgrapht.io.JsonListener;
import org.jgrapht.io.JsonParser;

class JsonBaseListener
implements JsonListener {
    JsonBaseListener() {
    }

    @Override
    public void enterJson(JsonParser.JsonContext ctx) {
    }

    @Override
    public void exitJson(JsonParser.JsonContext ctx) {
    }

    @Override
    public void enterObj(JsonParser.ObjContext ctx) {
    }

    @Override
    public void exitObj(JsonParser.ObjContext ctx) {
    }

    @Override
    public void enterPair(JsonParser.PairContext ctx) {
    }

    @Override
    public void exitPair(JsonParser.PairContext ctx) {
    }

    @Override
    public void enterArray(JsonParser.ArrayContext ctx) {
    }

    @Override
    public void exitArray(JsonParser.ArrayContext ctx) {
    }

    @Override
    public void enterValue(JsonParser.ValueContext ctx) {
    }

    @Override
    public void exitValue(JsonParser.ValueContext ctx) {
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

