/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.grammars;

import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;

public interface XMLGrammarPool {
    public Grammar[] retrieveInitialGrammarSet(String var1);

    public void cacheGrammars(String var1, Grammar[] var2);

    public Grammar retrieveGrammar(XMLGrammarDescription var1);

    public void lockPool();

    public void unlockPool();

    public void clear();
}

