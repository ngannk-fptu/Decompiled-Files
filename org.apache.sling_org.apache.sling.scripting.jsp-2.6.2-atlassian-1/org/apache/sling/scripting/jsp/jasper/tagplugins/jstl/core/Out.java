/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.core;

import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPlugin;
import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPluginContext;

public final class Out
implements TagPlugin {
    @Override
    public void doTag(TagPluginContext ctxt) {
        boolean hasDefault = false;
        boolean hasEscapeXml = false;
        hasDefault = ctxt.isAttributeSpecified("default");
        hasEscapeXml = ctxt.isAttributeSpecified("escapeXml");
        String strValName = ctxt.getTemporaryVariableName();
        String strDefName = ctxt.getTemporaryVariableName();
        String strEscapeXmlName = ctxt.getTemporaryVariableName();
        ctxt.generateJavaSource("String " + strValName + " = null;");
        ctxt.generateJavaSource("if(");
        ctxt.generateAttribute("value");
        ctxt.generateJavaSource("!=null){");
        ctxt.generateJavaSource("    " + strValName + " = (");
        ctxt.generateAttribute("value");
        ctxt.generateJavaSource(").toString();");
        ctxt.generateJavaSource("}");
        ctxt.generateJavaSource("String " + strDefName + " = null;\n");
        if (hasDefault) {
            ctxt.generateJavaSource("if(");
            ctxt.generateAttribute("default");
            ctxt.generateJavaSource(" != null){");
            ctxt.generateJavaSource(strDefName + " = (");
            ctxt.generateAttribute("default");
            ctxt.generateJavaSource(").toString();");
            ctxt.generateJavaSource("}");
        }
        ctxt.generateJavaSource("boolean " + strEscapeXmlName + " = true;");
        if (hasEscapeXml) {
            ctxt.generateJavaSource(strEscapeXmlName + " = Boolean.parseBoolean((");
            ctxt.generateAttribute("default");
            ctxt.generateJavaSource(").toString());");
        }
        ctxt.generateJavaSource("if(null != " + strValName + "){");
        ctxt.generateJavaSource("    if(" + strEscapeXmlName + "){");
        ctxt.generateJavaSource("        " + strValName + " = org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.Util.escapeXml(" + strValName + ");");
        ctxt.generateJavaSource("    }");
        ctxt.generateJavaSource("    out.write(" + strValName + ");");
        ctxt.generateJavaSource("}else{");
        ctxt.generateJavaSource("    if(null != " + strDefName + "){");
        ctxt.generateJavaSource("        if(" + strEscapeXmlName + "){");
        ctxt.generateJavaSource("            " + strDefName + " = org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.Util.escapeXml(" + strDefName + ");");
        ctxt.generateJavaSource("        }");
        ctxt.generateJavaSource("        out.write(" + strDefName + ");");
        ctxt.generateJavaSource("    }else{");
        ctxt.generateBody();
        ctxt.generateJavaSource("    }");
        ctxt.generateJavaSource("}");
    }
}

