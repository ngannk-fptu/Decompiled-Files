/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class SetDenyUncoveredHttpMethodsRule
extends Rule {
    SetDenyUncoveredHttpMethodsRule() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        StringBuilder code;
        WebXml webXml = (WebXml)this.digester.peek();
        webXml.setDenyUncoveredHttpMethods(true);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)(webXml.getClass().getName() + ".setDenyUncoveredHttpMethods(true)"));
        }
        if ((code = this.digester.getGeneratedCode()) != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(webXml)).append(".setDenyUncoveredHttpMethods(true);");
            code.append(System.lineSeparator());
        }
    }
}

