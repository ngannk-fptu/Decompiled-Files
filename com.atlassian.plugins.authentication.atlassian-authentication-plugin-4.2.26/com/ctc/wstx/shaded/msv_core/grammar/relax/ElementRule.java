/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;

public class ElementRule
extends ElementExp {
    public final TagClause clause;
    public final Expression attributeFreeContentModel;
    protected ElementRules parent;
    private static final long serialVersionUID = 1L;

    public ElementRules getParent() {
        return this.parent;
    }

    public final NameClass getNameClass() {
        return this.clause.nameClass;
    }

    public ElementRule(ExpressionPool pool, TagClause clause, Expression contentModel) {
        super(pool.createSequence(clause, contentModel), true);
        this.clause = clause;
        this.attributeFreeContentModel = contentModel;
    }
}

