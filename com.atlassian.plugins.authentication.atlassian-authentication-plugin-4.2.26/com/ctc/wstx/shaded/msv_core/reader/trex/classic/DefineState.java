/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.classic;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import org.xml.sax.Locator;

public class DefineState
extends com.ctc.wstx.shaded.msv_core.reader.trex.DefineState {
    protected Expression doCombine(ReferenceExp baseExp, Expression newExp, String combine) {
        TREXGrammarReader reader = (TREXGrammarReader)this.reader;
        if (baseExp.exp == null) {
            if (combine != null) {
                reader.reportWarning("TREXGrammarReader.Warning.CombineIgnored", baseExp.name);
            }
            return newExp;
        }
        if (reader.getDeclaredLocationOf(baseExp).getSystemId().equals(reader.getLocator().getSystemId())) {
            reader.reportError("TREXGrammarReader.DuplicateDefinition", (Object)baseExp.name);
            return baseExp.exp;
        }
        if (combine == null) {
            reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(baseExp)}, "TREXGrammarReader.CombineMissing", new Object[]{baseExp.name});
            return baseExp.exp;
        }
        if (combine.equals("group")) {
            return reader.pool.createSequence(baseExp.exp, newExp);
        }
        if (combine.equals("choice")) {
            return reader.pool.createChoice(baseExp.exp, newExp);
        }
        if (combine.equals("replace")) {
            return this.exp;
        }
        if (combine.equals("interleave")) {
            return reader.pool.createInterleave(baseExp.exp, newExp);
        }
        if (combine.equals("concur")) {
            return reader.pool.createConcur(baseExp.exp, newExp);
        }
        return null;
    }
}

