/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XMLSchemaGrammar
implements Grammar {
    protected final ExpressionPool pool;
    public Expression topLevel;
    protected final Map schemata = new HashMap();
    private static final long serialVersionUID = 1L;

    public XMLSchemaGrammar() {
        this(new ExpressionPool());
    }

    public XMLSchemaGrammar(ExpressionPool pool) {
        this.pool = pool;
    }

    public final ExpressionPool getPool() {
        return this.pool;
    }

    public final Expression getTopLevel() {
        return this.topLevel;
    }

    public XMLSchemaSchema getByNamespace(String targetNamesapce) {
        return (XMLSchemaSchema)this.schemata.get(targetNamesapce);
    }

    public Iterator iterateSchemas() {
        return this.schemata.values().iterator();
    }
}

