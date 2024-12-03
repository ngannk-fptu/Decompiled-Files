/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import java.util.HashSet;
import java.util.Set;

class StringRecoveryToken
extends StringToken {
    final Set failedExps;

    StringRecoveryToken(StringToken base) {
        this(base, new HashSet());
    }

    StringRecoveryToken(StringToken base, Set failedExps) {
        super(base.resCalc, base.literal, base.context, null);
        this.failedExps = failedExps;
    }

    public boolean match(DataExp exp) {
        if (super.match(exp)) {
            return true;
        }
        this.failedExps.add(exp);
        return true;
    }

    public boolean match(ValueExp exp) {
        if (super.match(exp)) {
            return true;
        }
        this.failedExps.add(exp);
        return true;
    }

    public boolean match(ListExp exp) {
        super.match(exp);
        return true;
    }

    protected StringToken createChildStringToken(String literal, DatatypeRef dtRef) {
        return new StringRecoveryToken(new StringToken(this.resCalc, literal, this.context, dtRef));
    }
}

