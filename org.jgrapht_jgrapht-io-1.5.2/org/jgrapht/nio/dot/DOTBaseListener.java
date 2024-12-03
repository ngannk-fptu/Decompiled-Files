/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ParserRuleContext
 *  org.antlr.v4.runtime.tree.ErrorNode
 *  org.antlr.v4.runtime.tree.TerminalNode
 */
package org.jgrapht.nio.dot;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jgrapht.nio.dot.DOTListener;
import org.jgrapht.nio.dot.DOTParser;

class DOTBaseListener
implements DOTListener {
    DOTBaseListener() {
    }

    @Override
    public void enterGraph(DOTParser.GraphContext ctx) {
    }

    @Override
    public void exitGraph(DOTParser.GraphContext ctx) {
    }

    @Override
    public void enterCompoundStatement(DOTParser.CompoundStatementContext ctx) {
    }

    @Override
    public void exitCompoundStatement(DOTParser.CompoundStatementContext ctx) {
    }

    @Override
    public void enterGraphHeader(DOTParser.GraphHeaderContext ctx) {
    }

    @Override
    public void exitGraphHeader(DOTParser.GraphHeaderContext ctx) {
    }

    @Override
    public void enterGraphIdentifier(DOTParser.GraphIdentifierContext ctx) {
    }

    @Override
    public void exitGraphIdentifier(DOTParser.GraphIdentifierContext ctx) {
    }

    @Override
    public void enterStatement(DOTParser.StatementContext ctx) {
    }

    @Override
    public void exitStatement(DOTParser.StatementContext ctx) {
    }

    @Override
    public void enterIdentifierPairStatement(DOTParser.IdentifierPairStatementContext ctx) {
    }

    @Override
    public void exitIdentifierPairStatement(DOTParser.IdentifierPairStatementContext ctx) {
    }

    @Override
    public void enterAttributeStatement(DOTParser.AttributeStatementContext ctx) {
    }

    @Override
    public void exitAttributeStatement(DOTParser.AttributeStatementContext ctx) {
    }

    @Override
    public void enterAttributesList(DOTParser.AttributesListContext ctx) {
    }

    @Override
    public void exitAttributesList(DOTParser.AttributesListContext ctx) {
    }

    @Override
    public void enterAList(DOTParser.AListContext ctx) {
    }

    @Override
    public void exitAList(DOTParser.AListContext ctx) {
    }

    @Override
    public void enterEdgeStatement(DOTParser.EdgeStatementContext ctx) {
    }

    @Override
    public void exitEdgeStatement(DOTParser.EdgeStatementContext ctx) {
    }

    @Override
    public void enterNodeStatement(DOTParser.NodeStatementContext ctx) {
    }

    @Override
    public void exitNodeStatement(DOTParser.NodeStatementContext ctx) {
    }

    @Override
    public void enterNodeStatementNoAttributes(DOTParser.NodeStatementNoAttributesContext ctx) {
    }

    @Override
    public void exitNodeStatementNoAttributes(DOTParser.NodeStatementNoAttributesContext ctx) {
    }

    @Override
    public void enterNodeIdentifier(DOTParser.NodeIdentifierContext ctx) {
    }

    @Override
    public void exitNodeIdentifier(DOTParser.NodeIdentifierContext ctx) {
    }

    @Override
    public void enterPort(DOTParser.PortContext ctx) {
    }

    @Override
    public void exitPort(DOTParser.PortContext ctx) {
    }

    @Override
    public void enterSubgraphStatement(DOTParser.SubgraphStatementContext ctx) {
    }

    @Override
    public void exitSubgraphStatement(DOTParser.SubgraphStatementContext ctx) {
    }

    @Override
    public void enterIdentifierPair(DOTParser.IdentifierPairContext ctx) {
    }

    @Override
    public void exitIdentifierPair(DOTParser.IdentifierPairContext ctx) {
    }

    @Override
    public void enterIdentifier(DOTParser.IdentifierContext ctx) {
    }

    @Override
    public void exitIdentifier(DOTParser.IdentifierContext ctx) {
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

