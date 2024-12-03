/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.tagplugins.jstl.core;

import org.apache.jasper.compiler.tagplugin.TagPlugin;
import org.apache.jasper.compiler.tagplugin.TagPluginContext;

public final class When
implements TagPlugin {
    @Override
    public void doTag(TagPluginContext ctxt) {
        TagPluginContext parentContext = ctxt.getParentContext();
        if (parentContext == null) {
            ctxt.dontUseTagPlugin();
            return;
        }
        if ("true".equals(parentContext.getPluginAttribute("hasBeenHere"))) {
            ctxt.generateJavaSource("} else if(");
        } else {
            ctxt.generateJavaSource("if(");
            parentContext.setPluginAttribute("hasBeenHere", "true");
        }
        ctxt.generateAttribute("test");
        ctxt.generateJavaSource("){");
        ctxt.generateBody();
    }
}

