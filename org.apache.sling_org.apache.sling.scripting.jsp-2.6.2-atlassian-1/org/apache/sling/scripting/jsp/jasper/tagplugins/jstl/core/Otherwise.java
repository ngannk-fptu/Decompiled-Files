/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.tagplugins.jstl.core;

import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPlugin;
import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPluginContext;

public final class Otherwise
implements TagPlugin {
    @Override
    public void doTag(TagPluginContext ctxt) {
        ctxt.generateJavaSource("} else {");
        ctxt.generateBody();
    }
}

