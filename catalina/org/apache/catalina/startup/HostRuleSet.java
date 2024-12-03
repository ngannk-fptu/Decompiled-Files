/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.Rule
 *  org.apache.tomcat.util.digester.RuleSet
 */
package org.apache.catalina.startup;

import org.apache.catalina.startup.CopyParentClassLoaderRule;
import org.apache.catalina.startup.LifecycleListenerRule;
import org.apache.catalina.startup.RealmRuleSet;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;

public class HostRuleSet
implements RuleSet {
    protected final String prefix;

    public HostRuleSet() {
        this("");
    }

    public HostRuleSet(String prefix) {
        this.prefix = prefix;
    }

    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate(this.prefix + "Host", "org.apache.catalina.core.StandardHost", "className");
        digester.addSetProperties(this.prefix + "Host");
        digester.addRule(this.prefix + "Host", (Rule)new CopyParentClassLoaderRule());
        digester.addRule(this.prefix + "Host", (Rule)new LifecycleListenerRule("org.apache.catalina.startup.HostConfig", "hostConfigClass"));
        digester.addSetNext(this.prefix + "Host", "addChild", "org.apache.catalina.Container");
        digester.addCallMethod(this.prefix + "Host/Alias", "addAlias", 0);
        digester.addObjectCreate(this.prefix + "Host/Cluster", null, "className");
        digester.addSetProperties(this.prefix + "Host/Cluster");
        digester.addSetNext(this.prefix + "Host/Cluster", "setCluster", "org.apache.catalina.Cluster");
        digester.addObjectCreate(this.prefix + "Host/Listener", null, "className");
        digester.addSetProperties(this.prefix + "Host/Listener");
        digester.addSetNext(this.prefix + "Host/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addRuleSet((RuleSet)new RealmRuleSet(this.prefix + "Host/"));
        digester.addObjectCreate(this.prefix + "Host/Valve", null, "className");
        digester.addSetProperties(this.prefix + "Host/Valve");
        digester.addSetNext(this.prefix + "Host/Valve", "addValve", "org.apache.catalina.Valve");
    }
}

