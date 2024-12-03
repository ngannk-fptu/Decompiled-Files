/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine;

import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface CSSNavigableDocumentListener {
    public void nodeInserted(Node var1);

    public void nodeToBeRemoved(Node var1);

    public void subtreeModified(Node var1);

    public void characterDataModified(Node var1);

    public void attrModified(Element var1, Attr var2, short var3, String var4, String var5);

    public void overrideStyleTextChanged(CSSStylableElement var1, String var2);

    public void overrideStylePropertyRemoved(CSSStylableElement var1, String var2);

    public void overrideStylePropertyChanged(CSSStylableElement var1, String var2, String var3, String var4);
}

