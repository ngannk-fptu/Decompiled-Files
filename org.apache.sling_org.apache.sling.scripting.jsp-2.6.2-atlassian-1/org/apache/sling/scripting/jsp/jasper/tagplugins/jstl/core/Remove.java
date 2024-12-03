/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.core;

import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPlugin;
import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPluginContext;
import org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.Util;

public class Remove
implements TagPlugin {
    @Override
    public void doTag(TagPluginContext ctxt) {
        boolean hasScope = ctxt.isAttributeSpecified("scope");
        String strVar = ctxt.getConstantAttribute("var");
        if (hasScope) {
            int iScope = Util.getScope(ctxt.getConstantAttribute("scope"));
            ctxt.generateJavaSource("pageContext.removeAttribute(\"" + strVar + "\"," + iScope + ");");
        } else {
            ctxt.generateJavaSource("pageContext.removeAttribute(\"" + strVar + "\");");
        }
    }
}

