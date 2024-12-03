/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class VersionRule
extends Rule {
    VersionRule() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        StringBuilder code;
        WebXml webXml = (WebXml)this.digester.peek(this.digester.getCount() - 1);
        webXml.setVersion(attributes.getValue("version"));
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)(webXml.getClass().getName() + ".setVersion( " + webXml.getVersion() + ")"));
        }
        if ((code = this.digester.getGeneratedCode()) != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(webXml)).append(".setVersion(\"");
            code.append(attributes.getValue("version")).append("\");");
            code.append(System.lineSeparator());
        }
    }
}

