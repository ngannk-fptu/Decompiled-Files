/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.Rule
 *  org.apache.tomcat.util.digester.RuleSet
 */
package org.apache.catalina.startup;

import org.apache.catalina.startup.SetNextNamingRule;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;

public class NamingRuleSet
implements RuleSet {
    protected final String prefix;

    public NamingRuleSet() {
        this("");
    }

    public NamingRuleSet(String prefix) {
        this.prefix = prefix;
    }

    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate(this.prefix + "Ejb", "org.apache.tomcat.util.descriptor.web.ContextEjb");
        digester.addSetProperties(this.prefix + "Ejb");
        digester.addRule(this.prefix + "Ejb", (Rule)new SetNextNamingRule("addEjb", "org.apache.tomcat.util.descriptor.web.ContextEjb"));
        digester.addObjectCreate(this.prefix + "Environment", "org.apache.tomcat.util.descriptor.web.ContextEnvironment");
        digester.addSetProperties(this.prefix + "Environment");
        digester.addRule(this.prefix + "Environment", (Rule)new SetNextNamingRule("addEnvironment", "org.apache.tomcat.util.descriptor.web.ContextEnvironment"));
        digester.addObjectCreate(this.prefix + "LocalEjb", "org.apache.tomcat.util.descriptor.web.ContextLocalEjb");
        digester.addSetProperties(this.prefix + "LocalEjb");
        digester.addRule(this.prefix + "LocalEjb", (Rule)new SetNextNamingRule("addLocalEjb", "org.apache.tomcat.util.descriptor.web.ContextLocalEjb"));
        digester.addObjectCreate(this.prefix + "Resource", "org.apache.tomcat.util.descriptor.web.ContextResource");
        digester.addSetProperties(this.prefix + "Resource");
        digester.addRule(this.prefix + "Resource", (Rule)new SetNextNamingRule("addResource", "org.apache.tomcat.util.descriptor.web.ContextResource"));
        digester.addObjectCreate(this.prefix + "ResourceEnvRef", "org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef");
        digester.addSetProperties(this.prefix + "ResourceEnvRef");
        digester.addRule(this.prefix + "ResourceEnvRef", (Rule)new SetNextNamingRule("addResourceEnvRef", "org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef"));
        digester.addObjectCreate(this.prefix + "ServiceRef", "org.apache.tomcat.util.descriptor.web.ContextService");
        digester.addSetProperties(this.prefix + "ServiceRef");
        digester.addRule(this.prefix + "ServiceRef", (Rule)new SetNextNamingRule("addService", "org.apache.tomcat.util.descriptor.web.ContextService"));
        digester.addObjectCreate(this.prefix + "Transaction", "org.apache.tomcat.util.descriptor.web.ContextTransaction");
        digester.addSetProperties(this.prefix + "Transaction");
        digester.addRule(this.prefix + "Transaction", (Rule)new SetNextNamingRule("setTransaction", "org.apache.tomcat.util.descriptor.web.ContextTransaction"));
    }
}

