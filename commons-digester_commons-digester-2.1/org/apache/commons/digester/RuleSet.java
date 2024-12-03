/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.digester.Digester;

public interface RuleSet {
    public String getNamespaceURI();

    public void addRuleInstances(Digester var1);
}

