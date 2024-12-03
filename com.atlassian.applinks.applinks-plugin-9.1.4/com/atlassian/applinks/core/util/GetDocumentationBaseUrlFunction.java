/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.applinks.core.util;

import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class GetDocumentationBaseUrlFunction
implements SoyServerFunction<String>,
SoyClientFunction {
    private final DocumentationLinker documentationLinker;

    public GetDocumentationBaseUrlFunction(DocumentationLinker documentationLinker) {
        this.documentationLinker = documentationLinker;
    }

    public String getName() {
        return "getDocumentationBaseUrl";
    }

    public JsExpression generate(JsExpression ... jsExpressions) {
        return new JsExpression("'" + this.documentationLinker.getDocumentationBaseUrl().toString() + "'");
    }

    public String apply(Object ... objects) {
        return this.documentationLinker.getDocumentationBaseUrl().toString();
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }
}

