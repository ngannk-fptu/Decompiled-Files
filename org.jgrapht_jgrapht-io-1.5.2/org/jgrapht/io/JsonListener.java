/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.tree.ParseTreeListener
 */
package org.jgrapht.io;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.jgrapht.io.JsonParser;

interface JsonListener
extends ParseTreeListener {
    public void enterJson(JsonParser.JsonContext var1);

    public void exitJson(JsonParser.JsonContext var1);

    public void enterObj(JsonParser.ObjContext var1);

    public void exitObj(JsonParser.ObjContext var1);

    public void enterPair(JsonParser.PairContext var1);

    public void exitPair(JsonParser.PairContext var1);

    public void enterArray(JsonParser.ArrayContext var1);

    public void exitArray(JsonParser.ArrayContext var1);

    public void enterValue(JsonParser.ValueContext var1);

    public void exitValue(JsonParser.ValueContext var1);
}

