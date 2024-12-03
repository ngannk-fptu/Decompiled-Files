/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ClauseState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleBaseState;

public class InlineTagState
extends ClauseState {
    protected void endSelf() {
        super.endSelf();
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            name = this.parentState.getStartTag().getAttribute("label");
            if (name == null) {
                name = this.parentState.getStartTag().getAttribute("role");
            }
            if (name == null) {
                name = "<undefined>";
            }
        }
        if (!(this.parentState instanceof ElementRuleBaseState)) {
            throw new Error();
        }
        TagClause c = new TagClause();
        c.nameClass = new SimpleNameClass(this.getReader().module.targetNamespace, name);
        c.exp = this.exp;
        ((ElementRuleBaseState)this.parentState).onEndInlineClause(c);
    }
}

