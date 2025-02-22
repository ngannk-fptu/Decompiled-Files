/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.core;

import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPlugin;
import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPluginContext;

public final class If
implements TagPlugin {
    @Override
    public void doTag(TagPluginContext ctxt) {
        String condV = ctxt.getTemporaryVariableName();
        ctxt.generateJavaSource("boolean " + condV + "=");
        ctxt.generateAttribute("test");
        ctxt.generateJavaSource(";");
        if (ctxt.isAttributeSpecified("var")) {
            String scope = "PageContext.PAGE_SCOPE";
            if (ctxt.isAttributeSpecified("scope")) {
                String scopeStr = ctxt.getConstantAttribute("scope");
                if ("request".equals(scopeStr)) {
                    scope = "PageContext.REQUEST_SCOPE";
                } else if ("session".equals(scopeStr)) {
                    scope = "PageContext.SESSION_SCOPE";
                } else if ("application".equals(scopeStr)) {
                    scope = "PageContext.APPLICATION_SCOPE";
                }
            }
            ctxt.generateJavaSource("_jspx_page_context.setAttribute(");
            ctxt.generateAttribute("var");
            ctxt.generateJavaSource(", new Boolean(" + condV + ")," + scope + ");");
        }
        ctxt.generateJavaSource("if (" + condV + "){");
        ctxt.generateBody();
        ctxt.generateJavaSource("}");
    }
}

