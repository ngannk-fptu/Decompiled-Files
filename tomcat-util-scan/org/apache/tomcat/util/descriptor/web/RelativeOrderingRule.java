/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.WebRuleSet;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class RelativeOrderingRule
extends Rule {
    boolean isRelativeOrderingSet = false;
    private final boolean fragment;

    RelativeOrderingRule(boolean fragment) {
        this.fragment = fragment;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (!this.fragment) {
            this.digester.getLogger().warn((Object)WebRuleSet.sm.getString("webRuleSet.relativeOrdering"));
        }
        if (this.isRelativeOrderingSet) {
            throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.relativeOrderingCount"));
        }
        this.isRelativeOrderingSet = true;
    }
}

