/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.DeclImpl;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.AnyOtherElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.ExportedAttPoolGenerator;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.IslandSchemaImpl;
import java.util.Iterator;
import java.util.Set;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class RELAXIslandSchema
extends IslandSchemaImpl {
    protected final RELAXModule module;
    protected Set pendingAnyOtherElements;

    public RELAXIslandSchema(RELAXModule module, Set pendingAnyOtherElements) {
        int i;
        this.module = module;
        this.pendingAnyOtherElements = pendingAnyOtherElements;
        ReferenceExp[] refs = module.elementRules.getAll();
        for (i = 0; i < refs.length; ++i) {
            if (!((ElementRules)refs[i]).exported) continue;
            this.elementDecls.put(refs[i].name, new DeclImpl(refs[i]));
        }
        refs = module.hedgeRules.getAll();
        for (i = 0; i < refs.length; ++i) {
            if (!((HedgeRules)refs[i]).exported) continue;
            this.elementDecls.put(refs[i].name, new DeclImpl(refs[i]));
        }
        ExportedAttPoolGenerator expGen = new ExportedAttPoolGenerator(module.pool);
        refs = module.attPools.getAll();
        for (int i2 = 0; i2 < refs.length; ++i2) {
            if (!((AttPoolClause)refs[i2]).exported) continue;
            this.attributesDecls.put(refs[i2].name, new DeclImpl(refs[i2].name, expGen.create(module, refs[i2].exp)));
        }
    }

    protected Grammar getGrammar() {
        return this.module;
    }

    public void bind(SchemaProvider provider, ErrorHandler handler) throws SAXException {
        Expression pseudoContentModel = this.createChoiceOfAllExportedRules(provider);
        Iterator itr = this.pendingAnyOtherElements.iterator();
        while (itr.hasNext()) {
            ((AnyOtherElementExp)itr.next()).wrapUp(this.module, pseudoContentModel, provider, handler);
        }
        this.pendingAnyOtherElements = null;
        IslandSchemaImpl.Binder binder = new IslandSchemaImpl.Binder(provider, handler, this.module.pool);
        this.bind(this.module.elementRules, binder);
        this.bind(this.module.hedgeRules, binder);
        this.bind(this.module.attPools, binder);
        this.bind(this.module.tags, binder);
    }

    private Expression createChoiceOfAllExportedRules(SchemaProvider provider) {
        Expression exp = Expression.nullSet;
        Iterator itr = provider.iterateNamespace();
        while (itr.hasNext()) {
            String namespace = (String)itr.next();
            IslandSchema is = provider.getSchemaByNamespace(namespace);
            ElementDecl[] rules = is.getElementDecls();
            for (int j = 0; j < rules.length; ++j) {
                exp = this.module.pool.createChoice(exp, new ExternalElementExp(this.module.pool, namespace, rules[j].getName(), null));
            }
        }
        return exp;
    }
}

