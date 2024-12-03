/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.DocumentationBean
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class DocLinkFunction
implements SoyServerFunction<String>,
SoyClientFunction {
    static final Pattern STRING_ARG = Pattern.compile("^'(.*)'$");
    private DocumentationBeanFactory documentationBeanFactory;

    public DocLinkFunction(DocumentationBeanFactory documentationBeanFactory) {
        this.documentationBeanFactory = documentationBeanFactory;
    }

    public String getName() {
        return "docLink";
    }

    public String apply(Object ... args) {
        return this.getDocumentationBean().getLink((String)args[0]);
    }

    public Set<Integer> validArgSizes() {
        return Collections.singleton(1);
    }

    public JsExpression generate(JsExpression ... args) {
        JsExpression keyExpr = args[0];
        Matcher m = STRING_ARG.matcher(keyExpr.getText());
        if (!m.matches()) {
            throw new IllegalArgumentException("Argument to docLink() is not a literal string: " + keyExpr.getText());
        }
        String key = m.group(1);
        String link = this.getDocumentationBean().getLink(key);
        return new JsExpression("\"" + link + "\"");
    }

    private DocumentationBean getDocumentationBean() {
        return this.documentationBeanFactory.getDocumentationBean();
    }
}

