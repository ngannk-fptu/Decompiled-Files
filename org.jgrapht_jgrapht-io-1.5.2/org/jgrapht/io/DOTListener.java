/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.tree.ParseTreeListener
 */
package org.jgrapht.io;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.jgrapht.io.DOTParser;

interface DOTListener
extends ParseTreeListener {
    public void enterGraph(DOTParser.GraphContext var1);

    public void exitGraph(DOTParser.GraphContext var1);

    public void enterCompoundStatement(DOTParser.CompoundStatementContext var1);

    public void exitCompoundStatement(DOTParser.CompoundStatementContext var1);

    public void enterGraphHeader(DOTParser.GraphHeaderContext var1);

    public void exitGraphHeader(DOTParser.GraphHeaderContext var1);

    public void enterGraphIdentifier(DOTParser.GraphIdentifierContext var1);

    public void exitGraphIdentifier(DOTParser.GraphIdentifierContext var1);

    public void enterStatement(DOTParser.StatementContext var1);

    public void exitStatement(DOTParser.StatementContext var1);

    public void enterIdentifierPairStatement(DOTParser.IdentifierPairStatementContext var1);

    public void exitIdentifierPairStatement(DOTParser.IdentifierPairStatementContext var1);

    public void enterAttributeStatement(DOTParser.AttributeStatementContext var1);

    public void exitAttributeStatement(DOTParser.AttributeStatementContext var1);

    public void enterAttributesList(DOTParser.AttributesListContext var1);

    public void exitAttributesList(DOTParser.AttributesListContext var1);

    public void enterAList(DOTParser.AListContext var1);

    public void exitAList(DOTParser.AListContext var1);

    public void enterEdgeStatement(DOTParser.EdgeStatementContext var1);

    public void exitEdgeStatement(DOTParser.EdgeStatementContext var1);

    public void enterNodeStatement(DOTParser.NodeStatementContext var1);

    public void exitNodeStatement(DOTParser.NodeStatementContext var1);

    public void enterNodeStatementNoAttributes(DOTParser.NodeStatementNoAttributesContext var1);

    public void exitNodeStatementNoAttributes(DOTParser.NodeStatementNoAttributesContext var1);

    public void enterNodeIdentifier(DOTParser.NodeIdentifierContext var1);

    public void exitNodeIdentifier(DOTParser.NodeIdentifierContext var1);

    public void enterPort(DOTParser.PortContext var1);

    public void exitPort(DOTParser.PortContext var1);

    public void enterSubgraphStatement(DOTParser.SubgraphStatementContext var1);

    public void exitSubgraphStatement(DOTParser.SubgraphStatementContext var1);

    public void enterIdentifierPair(DOTParser.IdentifierPairContext var1);

    public void exitIdentifierPair(DOTParser.IdentifierPairContext var1);

    public void enterIdentifier(DOTParser.IdentifierContext var1);

    public void exitIdentifier(DOTParser.IdentifierContext var1);
}

