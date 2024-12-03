/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.EmptyStringType;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.NoneType;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import java.util.HashMap;
import java.util.Map;

public class RELAXModule
implements Grammar {
    public final ElementRulesContainer elementRules = new ElementRulesContainer();
    public final HedgeRulesContainer hedgeRules = new HedgeRulesContainer();
    public final TagContainer tags = new TagContainer();
    public final AttPoolContainer attPools = new AttPoolContainer();
    public final String targetNamespace;
    public final DatatypeContainer datatypes = new DatatypeContainer();
    public Expression topLevel;
    public final ExpressionPool pool;
    private static final long serialVersionUID = 1L;

    public Expression getTopLevel() {
        return this.topLevel;
    }

    public ExpressionPool getPool() {
        return this.pool;
    }

    public RELAXModule(ExpressionPool pool, String targetNamespace) {
        if (targetNamespace == null) {
            throw new NullPointerException();
        }
        this.pool = pool;
        this.targetNamespace = targetNamespace;
        this.datatypes.add(EmptyStringType.theInstance);
        this.datatypes.add(NoneType.theInstance);
    }

    public class DatatypeContainer {
        private final Map m = new HashMap();

        public XSDatatype get(String name) {
            return (XSDatatype)this.m.get(name);
        }

        public void add(XSDatatype dt) {
            if (dt.getName() == null) {
                throw new IllegalArgumentException();
            }
            this.m.put(dt.getName(), dt);
        }
    }

    public final class AttPoolContainer
    extends ReferenceContainer {
        public AttPoolClause getOrCreate(String name) {
            return (AttPoolClause)super._getOrCreate(name);
        }

        public AttPoolClause get(String name) {
            return (AttPoolClause)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new AttPoolClause(name);
        }
    }

    public final class TagContainer
    extends ReferenceContainer {
        public TagClause getOrCreate(String name) {
            return (TagClause)super._getOrCreate(name);
        }

        public TagClause get(String name) {
            return (TagClause)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new TagClause(name);
        }
    }

    public final class HedgeRulesContainer
    extends ReferenceContainer {
        public HedgeRules getOrCreate(String name) {
            return (HedgeRules)super._getOrCreate(name);
        }

        public HedgeRules get(String name) {
            return (HedgeRules)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new HedgeRules(name, RELAXModule.this);
        }
    }

    public final class ElementRulesContainer
    extends ReferenceContainer {
        public ElementRules getOrCreate(String name) {
            return (ElementRules)super._getOrCreate(name);
        }

        public ElementRules get(String name) {
            return (ElementRules)super._get(name);
        }

        protected ReferenceExp createReference(String name) {
            return new ElementRules(name, RELAXModule.this);
        }
    }
}

