/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.tree.ParseTreeListener
 */
package org.jgrapht.nio.csv;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.jgrapht.nio.csv.CSVParser;

interface CSVListener
extends ParseTreeListener {
    public void enterFile(CSVParser.FileContext var1);

    public void exitFile(CSVParser.FileContext var1);

    public void enterHeader(CSVParser.HeaderContext var1);

    public void exitHeader(CSVParser.HeaderContext var1);

    public void enterRecord(CSVParser.RecordContext var1);

    public void exitRecord(CSVParser.RecordContext var1);

    public void enterTextField(CSVParser.TextFieldContext var1);

    public void exitTextField(CSVParser.TextFieldContext var1);

    public void enterStringField(CSVParser.StringFieldContext var1);

    public void exitStringField(CSVParser.StringFieldContext var1);

    public void enterEmptyField(CSVParser.EmptyFieldContext var1);

    public void exitEmptyField(CSVParser.EmptyFieldContext var1);
}

