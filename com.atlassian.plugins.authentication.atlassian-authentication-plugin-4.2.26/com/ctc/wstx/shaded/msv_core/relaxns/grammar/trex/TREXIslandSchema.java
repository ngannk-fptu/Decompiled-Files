/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.grammar.trex;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.DeclImpl;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.IslandSchemaImpl;
import org.xml.sax.ErrorHandler;

public class TREXIslandSchema
extends IslandSchemaImpl {
    protected final TREXGrammar grammar;

    public TREXIslandSchema(TREXGrammar grammar) {
        this.grammar = grammar;
        ReferenceExp[] refs = grammar.namedPatterns.getAll();
        for (int i = 0; i < refs.length; ++i) {
            this.elementDecls.put(refs[i].name, new DeclImpl(refs[i]));
        }
    }

    protected Grammar getGrammar() {
        return this.grammar;
    }

    public void bind(SchemaProvider provider, ErrorHandler handler) {
        IslandSchemaImpl.Binder binder = new IslandSchemaImpl.Binder(provider, handler, this.grammar.pool);
        this.bind(this.grammar.namedPatterns, binder);
    }
}

