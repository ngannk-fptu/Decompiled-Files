/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp;

import com.ctc.wstx.shaded.msv_core.grammar.relaxng.RELAXNGGrammar;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.RELAXNGCompReader;
import org.xml.sax.Locator;

abstract class CompatibilityChecker {
    protected final RELAXNGCompReader reader;
    protected final RELAXNGGrammar grammar;

    protected CompatibilityChecker(RELAXNGCompReader _reader) {
        this.reader = _reader;
        this.grammar = (RELAXNGGrammar)_reader.getGrammar();
    }

    protected abstract void setCompatibility(boolean var1);

    protected void reportCompError(Locator[] locs, String propertyName) {
        this.reportCompError(locs, propertyName, null);
    }

    protected void reportCompError(Locator[] locs, String propertyName, Object[] args) {
        this.setCompatibility(false);
        this.reader.reportWarning(propertyName, args, locs);
    }
}

