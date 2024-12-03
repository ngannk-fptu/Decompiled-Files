/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.core;

import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPlugin;
import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPluginContext;

public class ForTokens
implements TagPlugin {
    @Override
    public void doTag(TagPluginContext ctxt) {
        boolean hasVar = ctxt.isAttributeSpecified("var");
        boolean hasVarStatus = ctxt.isAttributeSpecified("varStatus");
        boolean hasBegin = ctxt.isAttributeSpecified("begin");
        boolean hasEnd = ctxt.isAttributeSpecified("end");
        boolean hasStep = ctxt.isAttributeSpecified("step");
        if (hasVarStatus) {
            ctxt.dontUseTagPlugin();
            return;
        }
        String itemsName = ctxt.getTemporaryVariableName();
        String delimsName = ctxt.getTemporaryVariableName();
        String stName = ctxt.getTemporaryVariableName();
        String beginName = ctxt.getTemporaryVariableName();
        String endName = ctxt.getTemporaryVariableName();
        String stepName = ctxt.getTemporaryVariableName();
        String index = ctxt.getTemporaryVariableName();
        String temp = ctxt.getTemporaryVariableName();
        String tokensCountName = ctxt.getTemporaryVariableName();
        ctxt.generateJavaSource("String " + itemsName + " = (String)");
        ctxt.generateAttribute("items");
        ctxt.generateJavaSource(";");
        ctxt.generateJavaSource("String " + delimsName + " = (String)");
        ctxt.generateAttribute("delims");
        ctxt.generateJavaSource(";");
        ctxt.generateJavaSource("java.util.StringTokenizer " + stName + " = new java.util.StringTokenizer(" + itemsName + ", " + delimsName + ");");
        ctxt.generateJavaSource("int " + tokensCountName + " = " + stName + ".countTokens();");
        if (hasBegin) {
            ctxt.generateJavaSource("int " + beginName + " = ");
            ctxt.generateAttribute("begin");
            ctxt.generateJavaSource(";");
            ctxt.generateJavaSource("for(int " + index + " = 0; " + index + " < " + beginName + " && " + stName + ".hasMoreTokens(); " + index + "++, " + stName + ".nextToken()){}");
        } else {
            ctxt.generateJavaSource("int " + beginName + " = 0;");
        }
        if (hasEnd) {
            ctxt.generateJavaSource("int " + endName + " = 0;");
            ctxt.generateJavaSource("if((" + tokensCountName + " - 1) < ");
            ctxt.generateAttribute("end");
            ctxt.generateJavaSource("){");
            ctxt.generateJavaSource("    " + endName + " = " + tokensCountName + " - 1;");
            ctxt.generateJavaSource("}else{");
            ctxt.generateJavaSource("    " + endName + " = ");
            ctxt.generateAttribute("end");
            ctxt.generateJavaSource(";}");
        } else {
            ctxt.generateJavaSource("int " + endName + " = " + tokensCountName + " - 1;");
        }
        if (hasStep) {
            ctxt.generateJavaSource("int " + stepName + " = ");
            ctxt.generateAttribute("step");
            ctxt.generateJavaSource(";");
        } else {
            ctxt.generateJavaSource("int " + stepName + " = 1;");
        }
        ctxt.generateJavaSource("for(int " + index + " = " + beginName + "; " + index + " <= " + endName + "; " + index + "++){");
        ctxt.generateJavaSource("    String " + temp + " = " + stName + ".nextToken();");
        ctxt.generateJavaSource("    if(((" + index + " - " + beginName + ") % " + stepName + ") == 0){");
        if (hasVar) {
            String strVar = ctxt.getConstantAttribute("var");
            ctxt.generateJavaSource("        pageContext.setAttribute(\"" + strVar + "\", " + temp + ");");
        }
        ctxt.generateBody();
        ctxt.generateJavaSource("    }");
        ctxt.generateJavaSource("}");
    }
}

