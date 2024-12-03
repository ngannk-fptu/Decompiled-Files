/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.WebRuleSet;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class AbsoluteOrderingRule
extends Rule {
    boolean isAbsoluteOrderingSet = false;
    private final boolean fragment;

    AbsoluteOrderingRule(boolean fragment) {
        this.fragment = fragment;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        StringBuilder code;
        if (this.fragment) {
            this.digester.getLogger().warn((Object)WebRuleSet.sm.getString("webRuleSet.absoluteOrdering"));
        }
        if (this.isAbsoluteOrderingSet) {
            throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.absoluteOrderingCount"));
        }
        this.isAbsoluteOrderingSet = true;
        WebXml webXml = (WebXml)this.digester.peek();
        webXml.createAbsoluteOrdering();
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)(webXml.getClass().getName() + ".setAbsoluteOrdering()"));
        }
        if ((code = this.digester.getGeneratedCode()) != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(webXml)).append(".createAbsoluteOrdering();");
            code.append(System.lineSeparator());
        }
    }
}

