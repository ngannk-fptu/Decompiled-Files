/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

final class ReadOnlyGrammarPool
implements XMLGrammarPool {
    private final XMLGrammarPool core;

    public ReadOnlyGrammarPool(XMLGrammarPool xMLGrammarPool) {
        this.core = xMLGrammarPool;
    }

    @Override
    public void cacheGrammars(String string, Grammar[] grammarArray) {
    }

    @Override
    public void clear() {
    }

    @Override
    public void lockPool() {
    }

    @Override
    public Grammar retrieveGrammar(XMLGrammarDescription xMLGrammarDescription) {
        return this.core.retrieveGrammar(xMLGrammarDescription);
    }

    @Override
    public Grammar[] retrieveInitialGrammarSet(String string) {
        return this.core.retrieveInitialGrammarSet(string);
    }

    @Override
    public void unlockPool() {
    }
}

