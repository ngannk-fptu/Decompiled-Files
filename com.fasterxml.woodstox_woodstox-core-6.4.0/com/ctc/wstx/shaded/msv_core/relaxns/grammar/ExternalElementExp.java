/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.grammar;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import org.xml.sax.Locator;

public class ExternalElementExp
extends ElementExp {
    private final NamespaceNameClass nameClass;
    public final String namespaceURI;
    public final String ruleName;
    public transient Locator source;
    public ElementDecl rule;

    public NameClass getNameClass() {
        return this.nameClass;
    }

    public ExternalElementExp(ExpressionPool pool, String namespaceURI, String ruleName, Locator loc) {
        super(Expression.nullSet, false);
        this.ruleName = ruleName;
        this.namespaceURI = namespaceURI;
        this.nameClass = new NamespaceNameClass(namespaceURI);
        this.source = loc;
        this.contentModel = pool.createZeroOrMore(pool.createMixed(pool.createChoice(pool.createAttribute(this.nameClass), this)));
    }
}

