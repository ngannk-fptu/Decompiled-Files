/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Attribute;
import com.sun.xml.txw2.NamespaceDecl;

interface ContentVisitor {
    public void onStartDocument();

    public void onEndDocument();

    public void onEndTag();

    public void onPcdata(StringBuilder var1);

    public void onCdata(StringBuilder var1);

    public void onStartTag(String var1, String var2, Attribute var3, NamespaceDecl var4);

    public void onComment(StringBuilder var1);
}

