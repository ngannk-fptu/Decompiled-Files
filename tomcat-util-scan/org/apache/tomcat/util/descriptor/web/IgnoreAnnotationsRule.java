/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class IgnoreAnnotationsRule
extends Rule {
    IgnoreAnnotationsRule() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        WebXml webXml = (WebXml)this.digester.peek(this.digester.getCount() - 1);
        String value = attributes.getValue("metadata-complete");
        if ("true".equals(value)) {
            webXml.setMetadataComplete(true);
        } else if ("false".equals(value)) {
            webXml.setMetadataComplete(false);
        } else {
            value = null;
        }
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)(webXml.getClass().getName() + ".setMetadataComplete( " + webXml.isMetadataComplete() + ")"));
        }
        StringBuilder code = this.digester.getGeneratedCode();
        if (value != null && code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(webXml)).append(".setMetadataComplete(");
            code.append(value).append(");");
            code.append(System.lineSeparator());
        }
    }
}

