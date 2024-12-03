/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ElementToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.Token;

public final class AnyElementToken
extends ElementToken {
    public static final Token theInstance = new AnyElementToken();

    private AnyElementToken() {
        super(null);
    }

    public boolean match(ElementExp exp) {
        return true;
    }
}

