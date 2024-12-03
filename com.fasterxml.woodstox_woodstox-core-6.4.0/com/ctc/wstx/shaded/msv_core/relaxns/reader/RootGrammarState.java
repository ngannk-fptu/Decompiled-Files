/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.GrammarState;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RELAXNSReader;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.IslandSchemaImpl;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.SchemaProviderImpl;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class RootGrammarState
extends SimpleState
implements ExpressionOwner {
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("grammar")) {
            return new GrammarState();
        }
        return null;
    }

    protected void endSelf() {
        RELAXNSReader reader = (RELAXNSReader)this.reader;
        SchemaProviderImpl schemaProvider = new SchemaProviderImpl(reader.grammar);
        reader.schemaProvider = schemaProvider;
        if (!reader.controller.hadError()) {
            if (!schemaProvider.bind(reader.controller)) {
                reader.controller.setErrorFlag();
            }
            if (reader.grammar.topLevel != null) {
                reader.grammar.topLevel = reader.grammar.topLevel.visit(new IslandSchemaImpl.Binder(schemaProvider, reader.controller, reader.pool));
            }
        }
    }

    public final void onEndChild(Expression exp) {
    }
}

