/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

abstract class ElementRuleBaseState
extends SimpleState {
    protected TagClause clause;

    ElementRuleBaseState() {
    }

    protected RELAXCoreReader getReader() {
        return (RELAXCoreReader)this.reader;
    }

    protected abstract Expression getContentModel();

    protected void onEndInlineClause(TagClause inlineTag) {
        if (this.clause != null) {
            this.reader.reportError("RELAXReader.MoreThanOneInlineTag");
        }
        this.clause = inlineTag;
    }

    protected void endSelf() {
        String role = this.startTag.getAttribute("role");
        String label = this.startTag.getAttribute("label");
        if (role == null && label == null) {
            this.reader.reportError("GrammarReader.MissingAttribute.2", "elementRule", "role", "label");
            label = "<undefined>";
        }
        if (label == null) {
            label = role;
        }
        if (this.clause == null) {
            if (role == null) {
                this.reader.reportError("GrammarReader.MissingAttribute", (Object)"elementRule", (Object)"role");
                this.clause = new TagClause();
                this.clause.nameClass = NameClass.ALL;
                this.clause.exp = Expression.nullSet;
            } else {
                this.clause = this.getReader().module.tags.getOrCreate(role);
            }
        }
        ElementRules er = this.getReader().module.elementRules.getOrCreate(label);
        this.getReader().setDeclaredLocationOf(er);
        er.addElementRule(this.reader.pool, new ElementRule(this.reader.pool, this.clause, this.getContentModel()));
        super.endSelf();
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("tag")) {
            return this.getReader().getStateFactory().tagInline(this, tag);
        }
        return null;
    }
}

