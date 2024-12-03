/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.Token;

public class ElementToken
extends Token {
    final ElementExp[] acceptedPatterns;

    public ElementToken(ElementExp[] acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    public boolean match(ElementExp exp) {
        for (int i = 0; i < this.acceptedPatterns.length; ++i) {
            if (this.acceptedPatterns[i] != exp) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        String s = "ElementToken";
        for (int i = 0; i < this.acceptedPatterns.length; ++i) {
            s = s + "/" + this.acceptedPatterns[i].getNameClass().toString();
        }
        return s;
    }
}

