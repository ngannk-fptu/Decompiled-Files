/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.AnyOtherElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.AnyOtherElementVerifier;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.ErrorHandlerAdaptor;
import com.ctc.wstx.shaded.msv_core.relaxns.verifier.RulesAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.Verifier;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ComplexAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.SimpleAcceptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class TREXIslandVerifier
extends Verifier
implements IslandVerifier {
    protected Dispatcher dispatcher;
    protected final Map rule2exp = new HashMap();
    private String lastNamaespaceUri;
    private String lastLocalName;
    private String lastQName;
    private Set unparsedEntities;

    public void setDispatcher(Dispatcher disp) {
        this.dispatcher = disp;
        this.errorHandler = new ErrorHandlerAdaptor(disp);
    }

    TREXIslandVerifier(RulesAcceptor initialAcceptor) {
        super(null, null);
        this.current = initialAcceptor;
    }

    public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(namespaceUri, localName, qName, atts);
        if (this.current instanceof SimpleAcceptor) {
            SimpleAcceptor sa = (SimpleAcceptor)this.current;
            if (sa.owner instanceof ExternalElementExp) {
                this.switchToChildIsland(new ExternalElementExp[]{(ExternalElementExp)sa.owner}, namespaceUri, localName, qName, atts);
                return;
            }
            if (sa.owner instanceof AnyOtherElementExp) {
                this.switchToAnyOtherElement(new AnyOtherElementExp[]{(AnyOtherElementExp)sa.owner}, namespaceUri, localName, qName, atts);
                return;
            }
            return;
        }
        if (this.current instanceof ComplexAcceptor) {
            int i;
            ComplexAcceptor ca = (ComplexAcceptor)this.current;
            Vector<ElementExp> vec = null;
            for (i = 0; i < ca.owners.length; ++i) {
                if (!(ca.owners[i] instanceof ExternalElementExp)) continue;
                if (vec == null) {
                    vec = new Vector<ElementExp>();
                }
                vec.add(ca.owners[i]);
            }
            if (vec != null) {
                ExternalElementExp[] exps = new ExternalElementExp[vec.size()];
                vec.toArray(exps);
                this.switchToChildIsland(exps, namespaceUri, localName, qName, atts);
                return;
            }
            for (i = 0; i < ca.owners.length; ++i) {
                if (!(ca.owners[i] instanceof AnyOtherElementExp)) continue;
                if (vec == null) {
                    vec = new Vector();
                }
                vec.add(ca.owners[i]);
            }
            if (vec != null) {
                AnyOtherElementExp[] exps = new AnyOtherElementExp[vec.size()];
                vec.toArray(exps);
                this.switchToAnyOtherElement(exps, namespaceUri, localName, qName, atts);
                return;
            }
            return;
        }
        throw new Error();
    }

    protected void switchToChildIsland(ExternalElementExp[] exps, String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        this.lastNamaespaceUri = namespaceUri;
        this.lastLocalName = localName;
        this.lastQName = qName;
        ElementDecl[] rules = new ElementDecl[exps.length];
        for (int i = 0; i < exps.length; ++i) {
            rules[i] = exps[i].rule;
            this.rule2exp.put(rules[i], exps[i]);
        }
        if (this.rule2exp.size() != rules.length) {
            throw new Error();
        }
        IslandSchema is = this.dispatcher.getSchemaProvider().getSchemaByNamespace(namespaceUri);
        IslandVerifier iv = is.createNewVerifier(namespaceUri, rules);
        this.dispatcher.switchVerifier(iv);
        iv.startElement(namespaceUri, localName, qName, atts);
    }

    protected void switchToAnyOtherElement(AnyOtherElementExp[] exps, String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        for (int i = 0; i < exps.length; ++i) {
            this.rule2exp.put(exps[i], exps[i]);
        }
        AnyOtherElementVerifier iv = new AnyOtherElementVerifier(exps);
        this.dispatcher.switchVerifier(iv);
        this.lastNamaespaceUri = namespaceUri;
        this.lastLocalName = localName;
        this.lastQName = qName;
        iv.startElement(namespaceUri, localName, qName, atts);
    }

    public void endChildIsland(String childURI, ElementDecl[] ruleSet) throws SAXException {
        ElementExp[] exps = new ElementExp[ruleSet.length];
        for (int i = 0; i < ruleSet.length; ++i) {
            exps[i] = (ElementExp)this.rule2exp.get(ruleSet[i]);
            if (exps[i] != null) continue;
            throw new Error();
        }
        Expression[] epsilons = new Expression[exps.length];
        for (int i = 0; i < epsilons.length; ++i) {
            epsilons[i] = Expression.epsilon;
        }
        this.current = new ComplexAcceptor((REDocumentDeclaration)this.docDecl, ruleSet.length == 0 ? Expression.nullSet : Expression.epsilon, epsilons, exps);
        super.endElement(this.lastNamaespaceUri, this.lastLocalName, this.lastQName);
    }

    public ElementDecl[] endIsland() {
        return ((RulesAcceptor)this.current).getSatisfiedElementDecls();
    }

    public boolean isUnparsedEntity(String entityName) {
        if (this.unparsedEntities == null) {
            this.unparsedEntities = new HashSet();
            int len = this.dispatcher.countUnparsedEntityDecls();
            for (int i = 0; i < len; ++i) {
                this.unparsedEntities.add(this.dispatcher.getUnparsedEntityDecl((int)i).name);
            }
        }
        return this.unparsedEntities.contains(entityName);
    }
}

