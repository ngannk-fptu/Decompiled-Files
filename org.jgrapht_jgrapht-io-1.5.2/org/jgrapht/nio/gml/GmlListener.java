/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.tree.ParseTreeListener
 */
package org.jgrapht.nio.gml;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.jgrapht.nio.gml.GmlParser;

interface GmlListener
extends ParseTreeListener {
    public void enterGml(GmlParser.GmlContext var1);

    public void exitGml(GmlParser.GmlContext var1);

    public void enterStringKeyValue(GmlParser.StringKeyValueContext var1);

    public void exitStringKeyValue(GmlParser.StringKeyValueContext var1);

    public void enterNumberKeyValue(GmlParser.NumberKeyValueContext var1);

    public void exitNumberKeyValue(GmlParser.NumberKeyValueContext var1);

    public void enterListKeyValue(GmlParser.ListKeyValueContext var1);

    public void exitListKeyValue(GmlParser.ListKeyValueContext var1);
}

