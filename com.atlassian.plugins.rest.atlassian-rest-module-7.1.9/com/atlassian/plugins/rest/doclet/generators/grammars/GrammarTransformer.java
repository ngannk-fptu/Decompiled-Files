/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.doclet.generators.grammars;

import com.atlassian.plugins.rest.doclet.generators.grammars.Doc;
import com.atlassian.plugins.rest.doclet.generators.grammars.Grammars;
import com.atlassian.plugins.rest.doclet.generators.grammars.Include;
import java.util.List;

public class GrammarTransformer {
    public static com.sun.research.ws.wadl.Grammars transform(Grammars grm) {
        com.sun.research.ws.wadl.Grammars result = new com.sun.research.ws.wadl.Grammars();
        GrammarTransformer.transformDocument(grm.getDoc(), result.getDoc());
        GrammarTransformer.transformInclude(grm.getInclude(), result.getInclude());
        result.getAny().addAll(grm.getAny());
        return result;
    }

    private static void transformDocument(List<Doc> src, List<com.sun.research.ws.wadl.Doc> dst) {
        for (Doc doc : src) {
            dst.add(GrammarTransformer.transform(doc));
        }
    }

    private static void transformInclude(List<Include> src, List<com.sun.research.ws.wadl.Include> dst) {
        for (Include inc : src) {
            dst.add(GrammarTransformer.transform(inc));
        }
    }

    private static com.sun.research.ws.wadl.Doc transform(Doc doc) {
        com.sun.research.ws.wadl.Doc result = new com.sun.research.ws.wadl.Doc();
        result.setLang(doc.getLang());
        result.setTitle(doc.getTitle());
        result.getOtherAttributes().putAll(doc.getOtherAttributes());
        result.getContent().addAll(doc.getContent());
        return result;
    }

    private static com.sun.research.ws.wadl.Include transform(Include inc) {
        com.sun.research.ws.wadl.Include result = new com.sun.research.ws.wadl.Include();
        GrammarTransformer.transformDocument(inc.getDoc(), result.getDoc());
        result.setHref(inc.getHref());
        result.getOtherAttributes().putAll(inc.getOtherAttributes());
        return result;
    }
}

