/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.Rule
 *  org.apache.tomcat.util.digester.RuleSet
 */
package org.apache.catalina.startup;

import org.apache.catalina.startup.LifecycleListenerRule;
import org.apache.catalina.startup.RealmRuleSet;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;

public class EngineRuleSet
implements RuleSet {
    protected final String prefix;

    public EngineRuleSet() {
        this("");
    }

    public EngineRuleSet(String prefix) {
        this.prefix = prefix;
    }

    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate(this.prefix + "Engine", "org.apache.catalina.core.StandardEngine", "className");
        digester.addSetProperties(this.prefix + "Engine");
        digester.addRule(this.prefix + "Engine", (Rule)new LifecycleListenerRule("org.apache.catalina.startup.EngineConfig", "engineConfigClass"));
        digester.addSetNext(this.prefix + "Engine", "setContainer", "org.apache.catalina.Engine");
        digester.addObjectCreate(this.prefix + "Engine/Cluster", null, "className");
        digester.addSetProperties(this.prefix + "Engine/Cluster");
        digester.addSetNext(this.prefix + "Engine/Cluster", "setCluster", "org.apache.catalina.Cluster");
        digester.addObjectCreate(this.prefix + "Engine/Listener", null, "className");
        digester.addSetProperties(this.prefix + "Engine/Listener");
        digester.addSetNext(this.prefix + "Engine/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addRuleSet((RuleSet)new RealmRuleSet(this.prefix + "Engine/"));
        digester.addObjectCreate(this.prefix + "Engine/Valve", null, "className");
        digester.addSetProperties(this.prefix + "Engine/Valve");
        digester.addSetNext(this.prefix + "Engine/Valve", "addValve", "org.apache.catalina.Valve");
    }
}

