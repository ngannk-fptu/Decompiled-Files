/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.WebRuleSet;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class NameRule
extends Rule {
    boolean isNameSet = false;

    NameRule() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.isNameSet) {
            throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.nameCount"));
        }
        this.isNameSet = true;
    }

    @Override
    public void body(String namespace, String name, String text) throws Exception {
        ((WebXml)this.digester.peek()).setName(text);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(this.digester.peek())).append(".setName(\"");
            code.append(text).append("\");");
            code.append(System.lineSeparator());
        }
    }
}

