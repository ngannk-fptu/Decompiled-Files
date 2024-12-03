/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ParserRuleContext
 *  org.antlr.v4.runtime.tree.ErrorNode
 *  org.antlr.v4.runtime.tree.TerminalNode
 */
package org.jgrapht.nio.csv;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jgrapht.nio.csv.CSVListener;
import org.jgrapht.nio.csv.CSVParser;

class CSVBaseListener
implements CSVListener {
    CSVBaseListener() {
    }

    @Override
    public void enterFile(CSVParser.FileContext ctx) {
    }

    @Override
    public void exitFile(CSVParser.FileContext ctx) {
    }

    @Override
    public void enterHeader(CSVParser.HeaderContext ctx) {
    }

    @Override
    public void exitHeader(CSVParser.HeaderContext ctx) {
    }

    @Override
    public void enterRecord(CSVParser.RecordContext ctx) {
    }

    @Override
    public void exitRecord(CSVParser.RecordContext ctx) {
    }

    @Override
    public void enterTextField(CSVParser.TextFieldContext ctx) {
    }

    @Override
    public void exitTextField(CSVParser.TextFieldContext ctx) {
    }

    @Override
    public void enterStringField(CSVParser.StringFieldContext ctx) {
    }

    @Override
    public void exitStringField(CSVParser.StringFieldContext ctx) {
    }

    @Override
    public void enterEmptyField(CSVParser.EmptyFieldContext ctx) {
    }

    @Override
    public void exitEmptyField(CSVParser.EmptyFieldContext ctx) {
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

