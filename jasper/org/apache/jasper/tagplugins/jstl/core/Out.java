/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspWriter
 */
package org.apache.jasper.tagplugins.jstl.core;

import java.io.IOException;
import java.io.Reader;
import javax.servlet.jsp.JspWriter;
import org.apache.jasper.compiler.tagplugin.TagPlugin;
import org.apache.jasper.compiler.tagplugin.TagPluginContext;
import org.apache.jasper.tagplugins.jstl.Util;

public final class Out
implements TagPlugin {
    @Override
    public void doTag(TagPluginContext ctxt) {
        boolean hasDefault = false;
        boolean hasEscapeXml = false;
        hasDefault = ctxt.isAttributeSpecified("default");
        hasEscapeXml = ctxt.isAttributeSpecified("escapeXml");
        String strObjectName = ctxt.getTemporaryVariableName();
        String strValName = ctxt.getTemporaryVariableName();
        String strDefName = ctxt.getTemporaryVariableName();
        String strEscapeXmlName = ctxt.getTemporaryVariableName();
        String strSkipBodyName = ctxt.getTemporaryVariableName();
        ctxt.generateImport("java.io.Reader");
        ctxt.generateJavaSource("Object " + strObjectName + "=");
        ctxt.generateAttribute("value");
        ctxt.generateJavaSource(";");
        ctxt.generateJavaSource("String " + strValName + "=null;");
        ctxt.generateJavaSource("if(!(" + strObjectName + " instanceof Reader) && " + strObjectName + " != null){");
        ctxt.generateJavaSource(strValName + " = " + strObjectName + ".toString();");
        ctxt.generateJavaSource("}");
        ctxt.generateJavaSource("String " + strDefName + " = null;");
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
            ctxt.generateJavaSource(strEscapeXmlName + " = ");
            ctxt.generateAttribute("escapeXml");
            ctxt.generateJavaSource(";");
        }
        ctxt.generateJavaSource("boolean " + strSkipBodyName + " = org.apache.jasper.tagplugins.jstl.core.Out.output(out, " + strObjectName + ", " + strValName + ", " + strDefName + ", " + strEscapeXmlName + ");");
        ctxt.generateJavaSource("if(!" + strSkipBodyName + ") {");
        ctxt.generateBody();
        ctxt.generateJavaSource("}");
    }

    public static boolean output(JspWriter out, Object input, String value, String defaultValue, boolean escapeXml) throws IOException {
        String v;
        if (input instanceof Reader) {
            char[] buffer = new char[8096];
            int read = 0;
            while (read != -1) {
                read = ((Reader)input).read(buffer);
                if (read == -1) continue;
                if (escapeXml) {
                    String escaped = Util.escapeXml(buffer, read);
                    if (escaped == null) {
                        out.write(buffer, 0, read);
                        continue;
                    }
                    out.print(escaped);
                    continue;
                }
                out.write(buffer, 0, read);
            }
            return true;
        }
        String string = v = value != null ? value : defaultValue;
        if (v != null) {
            if (escapeXml) {
                v = Util.escapeXml(v);
            }
            out.write(v);
            return true;
        }
        return false;
    }
}

