/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.Rule
 *  org.apache.tomcat.util.digester.RuleSet
 */
package org.apache.catalina.realm;

import org.apache.catalina.realm.MemoryUserRule;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;

public class MemoryRuleSet
implements RuleSet {
    protected final String prefix;

    public MemoryRuleSet() {
        this("tomcat-users/");
    }

    public MemoryRuleSet(String prefix) {
        this.prefix = prefix;
    }

    public void addRuleInstances(Digester digester) {
        digester.addRule(this.prefix + "user", (Rule)new MemoryUserRule());
    }
}

