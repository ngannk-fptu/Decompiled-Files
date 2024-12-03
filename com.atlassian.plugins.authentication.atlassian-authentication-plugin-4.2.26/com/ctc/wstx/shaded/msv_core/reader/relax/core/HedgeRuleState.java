/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;

public class HedgeRuleState
extends HedgeRuleBaseState {
    protected void endSelf(Expression contentModel) {
        String label = this.startTag.getAttribute("label");
        if (label == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"hedgeRule", (Object)"label");
            return;
        }
        RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        HedgeRules hr = reader.module.hedgeRules.getOrCreate(label);
        reader.setDeclaredLocationOf(hr);
        hr.addHedge(contentModel, reader.pool);
    }
}

