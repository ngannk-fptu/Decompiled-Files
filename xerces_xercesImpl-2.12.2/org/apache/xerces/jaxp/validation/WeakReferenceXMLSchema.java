/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.lang.ref.WeakReference;
import org.apache.xerces.jaxp.validation.AbstractXMLSchema;
import org.apache.xerces.jaxp.validation.SoftReferenceGrammarPool;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

final class WeakReferenceXMLSchema
extends AbstractXMLSchema {
    private WeakReference fGrammarPool = new WeakReference<Object>(null);

    @Override
    public synchronized XMLGrammarPool getGrammarPool() {
        XMLGrammarPool xMLGrammarPool = (XMLGrammarPool)this.fGrammarPool.get();
        if (xMLGrammarPool == null) {
            xMLGrammarPool = new SoftReferenceGrammarPool();
            this.fGrammarPool = new WeakReference<XMLGrammarPool>(xMLGrammarPool);
        }
        return xMLGrammarPool;
    }

    @Override
    public boolean isFullyComposed() {
        return false;
    }
}

