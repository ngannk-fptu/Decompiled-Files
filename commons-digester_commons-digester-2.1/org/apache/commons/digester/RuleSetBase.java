/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;

public abstract class RuleSetBase
implements RuleSet {
    protected String namespaceURI = null;

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public abstract void addRuleInstances(Digester var1);
}

