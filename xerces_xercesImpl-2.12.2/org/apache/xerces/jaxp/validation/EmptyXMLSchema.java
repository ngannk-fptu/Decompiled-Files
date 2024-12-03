/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import org.apache.xerces.jaxp.validation.AbstractXMLSchema;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

final class EmptyXMLSchema
extends AbstractXMLSchema
implements XMLGrammarPool {
    private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];

    @Override
    public Grammar[] retrieveInitialGrammarSet(String string) {
        return ZERO_LENGTH_GRAMMAR_ARRAY;
    }

    @Override
    public void cacheGrammars(String string, Grammar[] grammarArray) {
    }

    @Override
    public Grammar retrieveGrammar(XMLGrammarDescription xMLGrammarDescription) {
        return null;
    }

    @Override
    public void lockPool() {
    }

    @Override
    public void unlockPool() {
    }

    @Override
    public void clear() {
    }

    @Override
    public XMLGrammarPool getGrammarPool() {
        return this;
    }

    @Override
    public boolean isFullyComposed() {
        return true;
    }
}

