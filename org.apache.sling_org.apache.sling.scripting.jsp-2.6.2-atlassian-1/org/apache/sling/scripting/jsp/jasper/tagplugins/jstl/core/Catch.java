/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.core;

import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPlugin;
import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPluginContext;

public class Catch
implements TagPlugin {
    @Override
    public void doTag(TagPluginContext ctxt) {
        String strVar;
        boolean hasVar = ctxt.isAttributeSpecified("var");
        String exceptionName = ctxt.getTemporaryVariableName();
        String caughtName = ctxt.getTemporaryVariableName();
        ctxt.generateJavaSource("boolean " + caughtName + " = false;");
        ctxt.generateJavaSource("try{");
        ctxt.generateBody();
        ctxt.generateJavaSource("}");
        ctxt.generateJavaSource("catch(Throwable " + exceptionName + "){");
        if (hasVar) {
            strVar = ctxt.getConstantAttribute("var");
            ctxt.generateJavaSource("    pageContext.setAttribute(\"" + strVar + "\", " + exceptionName + ", PageContext.PAGE_SCOPE);");
        }
        ctxt.generateJavaSource("    " + caughtName + " = true;");
        ctxt.generateJavaSource("}");
        ctxt.generateJavaSource("finally{");
        if (hasVar) {
            strVar = ctxt.getConstantAttribute("var");
            ctxt.generateJavaSource("    if(!" + caughtName + "){");
            ctxt.generateJavaSource("        pageContext.removeAttribute(\"" + strVar + "\", PageContext.PAGE_SCOPE);");
            ctxt.generateJavaSource("    }");
        }
        ctxt.generateJavaSource("}");
    }
}

