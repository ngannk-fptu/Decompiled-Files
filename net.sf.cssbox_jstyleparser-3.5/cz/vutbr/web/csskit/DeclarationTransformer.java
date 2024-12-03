/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.Term;
import java.util.Map;

public interface DeclarationTransformer {
    public boolean parseDeclaration(Declaration var1, Map<String, CSSProperty> var2, Map<String, Term<?>> var3);
}

