/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ClauseState;
import org.xml.sax.Locator;

public class AttPoolState
extends ClauseState {
    protected void endSelf() {
        super.endSelf();
        String role = this.startTag.getAttribute("role");
        if (role == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"attPool", (Object)"role");
            return;
        }
        if (this.startTag.getAttribute("combine") == null) {
            AttPoolClause c = this.getReader().module.attPools.getOrCreate(role);
            if (c.exp != null) {
                this.reader.reportError(new Locator[]{this.getReader().getDeclaredLocationOf(c), this.location}, "RELAXReader.MultipleAttPoolDeclarations", new Object[]{role});
            }
            c.exp = this.exp;
            this.getReader().setDeclaredLocationOf(c);
        } else {
            ReferenceExp e = this.getReader().combinedAttPools._getOrCreate(role);
            if (e.exp == null) {
                e.exp = Expression.epsilon;
            }
            e.exp = this.reader.pool.createSequence(this.exp, e.exp);
            this.reader.setDeclaredLocationOf(e);
        }
    }
}

