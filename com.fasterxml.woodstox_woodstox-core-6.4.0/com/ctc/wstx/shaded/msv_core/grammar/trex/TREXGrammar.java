/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabularyMap;

public class TREXGrammar
extends ReferenceExp
implements Grammar {
    public final RefContainer namedPatterns = new RefContainer();
    public final ExpressionPool pool;
    protected final TREXGrammar parentGrammar;
    public final DataTypeVocabularyMap dataTypes = new DataTypeVocabularyMap();
    private static final long serialVersionUID = 1L;

    public Expression getTopLevel() {
        return this.exp;
    }

    public ExpressionPool getPool() {
        return this.pool;
    }

    public final TREXGrammar getParentGrammar() {
        return this.parentGrammar;
    }

    public TREXGrammar(ExpressionPool pool, TREXGrammar parentGrammar) {
        super(null);
        this.pool = pool;
        this.parentGrammar = parentGrammar;
    }

    public TREXGrammar(ExpressionPool pool) {
        this(pool, null);
    }

    public TREXGrammar() {
        this(new ExpressionPool(), null);
    }

    public static final class RefContainer
    extends ReferenceContainer {
        public ReferenceExp getOrCreate(String name) {
            return super._getOrCreate(name);
        }

        protected ReferenceExp createReference(String name) {
            return new ReferenceExp(name);
        }
    }
}

