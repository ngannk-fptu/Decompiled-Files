/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.XMLGrammarPool;

public interface XSGrammarPoolContainer {
    public XMLGrammarPool getGrammarPool();

    public boolean isFullyComposed();

    public Boolean getFeature(String var1);
}

