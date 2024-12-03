/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class SetOverrideRule
extends Rule {
    SetOverrideRule() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        StringBuilder code;
        ContextEnvironment envEntry = (ContextEnvironment)this.digester.peek();
        envEntry.setOverride(false);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)(envEntry.getClass().getName() + ".setOverride(false)"));
        }
        if ((code = this.digester.getGeneratedCode()) != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(envEntry)).append(".setOverride(false);");
            code.append(System.lineSeparator());
        }
    }
}

