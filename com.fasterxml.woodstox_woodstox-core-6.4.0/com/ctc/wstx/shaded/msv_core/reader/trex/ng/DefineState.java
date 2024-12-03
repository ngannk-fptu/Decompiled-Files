/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import org.xml.sax.Locator;

public class DefineState
extends com.ctc.wstx.shaded.msv_core.reader.trex.DefineState {
    private RELAXNGReader.RefExpParseInfo prevNamedPattern;
    private boolean previousDirectRefernce;

    protected void startSelf() {
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        super.startSelf();
        this.prevNamedPattern = reader.currentNamedPattern;
        this.previousDirectRefernce = reader.directRefernce;
        reader.directRefernce = true;
        ReferenceExp exp = this.getReference();
        if (exp == null) {
            reader.currentNamedPattern = null;
        } else {
            reader.currentNamedPattern = reader.getRefExpParseInfo(exp);
            if (reader.currentNamedPattern.redefinition != RELAXNGReader.RefExpParseInfo.notBeingRedefined) {
                reader.currentNamedPattern = null;
            }
        }
    }

    protected void endSelf() {
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        reader.currentNamedPattern = this.prevNamedPattern;
        reader.directRefernce = this.previousDirectRefernce;
        super.endSelf();
    }

    protected Expression doCombine(ReferenceExp baseExp, Expression newExp, String combine) {
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(baseExp);
        if (combine == null) {
            if (info.haveHead) {
                reader.reportError("TREXGrammarReader.CombineMissing", (Object)baseExp.name);
                return baseExp.exp;
            }
            info.haveHead = true;
        } else if (info.combineMethod == null) {
            info.combineMethod = combine.trim();
            if (!info.combineMethod.equals("choice") && !info.combineMethod.equals("interleave")) {
                reader.reportError("TREXGrammarReader.BadCombine", (Object)info.combineMethod);
            }
        } else if (!info.combineMethod.equals(combine)) {
            reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(baseExp)}, "RELAXNGReader.InconsistentCombine", new Object[]{baseExp.name});
            info.combineMethod = null;
            return baseExp.exp;
        }
        if (baseExp.exp == null) {
            return newExp;
        }
        if (info.redefinition != RELAXNGReader.RefExpParseInfo.notBeingRedefined) {
            info.redefinition = RELAXNGReader.RefExpParseInfo.originalFound;
            return baseExp.exp;
        }
        if (info.combineMethod.equals("choice")) {
            return reader.pool.createChoice(baseExp.exp, newExp);
        }
        if (info.combineMethod.equals("interleave")) {
            return reader.pool.createInterleave(baseExp.exp, newExp);
        }
        return null;
    }
}

