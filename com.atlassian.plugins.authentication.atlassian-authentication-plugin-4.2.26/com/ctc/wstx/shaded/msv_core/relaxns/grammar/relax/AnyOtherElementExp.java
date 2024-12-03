/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.Localizer;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;

public class AnyOtherElementExp
extends ElementExp
implements ElementDecl {
    public NameClass nameClass;
    public transient Locator source;
    public final String includeNamespace;
    public final String excludeNamespace;

    public NameClass getNameClass() {
        return this.nameClass;
    }

    public String getName() {
        return "anyOtherElement:" + this.nameClass;
    }

    public AnyOtherElementExp(Locator loc, String includeNamespace, String excludeNamespace) {
        super(Expression.nullSet, true);
        this.source = loc;
        this.includeNamespace = includeNamespace;
        this.excludeNamespace = excludeNamespace;
        if (includeNamespace == null && excludeNamespace == null) {
            throw new IllegalArgumentException();
        }
        if (includeNamespace != null && excludeNamespace != null) {
            throw new IllegalArgumentException();
        }
    }

    protected void wrapUp(Grammar owner, Expression pseudoContentModel, SchemaProvider provider, ErrorHandler errorHandler) throws SAXException {
        NamespaceNameClass nsnc;
        StringTokenizer st = this.includeNamespace != null ? new StringTokenizer(this.includeNamespace) : new StringTokenizer(this.excludeNamespace);
        NameClass nc = null;
        while (st.hasMoreTokens()) {
            String uri = st.nextToken();
            if (uri.equals("##local")) {
                uri = "";
            }
            if (provider.getSchemaByNamespace(uri) != null) {
                errorHandler.warning(new SAXParseException(Localizer.localize("AnyOtherElementExp.Warning.AnyOtherNamespaceIgnored", uri), this.source));
                continue;
            }
            nsnc = new NamespaceNameClass(uri);
            if (nc == null) {
                nc = nsnc;
                continue;
            }
            nc = new ChoiceNameClass(nc, nsnc);
        }
        if (this.excludeNamespace != null) {
            Iterator itr = provider.iterateNamespace();
            while (itr.hasNext()) {
                nsnc = new NamespaceNameClass((String)itr.next());
                if (nc == null) {
                    nc = nsnc;
                    continue;
                }
                nc = new ChoiceNameClass(nc, nsnc);
            }
            nc = new NotNameClass(nc);
        }
        this.nameClass = nc;
        this.contentModel = owner.getPool().createMixed(owner.getPool().createZeroOrMore(owner.getPool().createChoice(this, pseudoContentModel)));
    }

    public boolean getFeature(String feature) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(feature);
    }

    public Object getProperty(String property) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(property);
    }
}

