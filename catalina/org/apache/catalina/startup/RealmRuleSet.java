/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.RuleSet
 */
package org.apache.catalina.startup;

import org.apache.catalina.startup.CredentialHandlerRuleSet;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;

public class RealmRuleSet
implements RuleSet {
    private static final int MAX_NESTED_REALM_LEVELS = Integer.getInteger("org.apache.catalina.startup.RealmRuleSet.MAX_NESTED_REALM_LEVELS", 3);
    protected final String prefix;

    public RealmRuleSet() {
        this("");
    }

    public RealmRuleSet(String prefix) {
        this.prefix = prefix;
    }

    public void addRuleInstances(Digester digester) {
        StringBuilder pattern = new StringBuilder(this.prefix);
        for (int i = 0; i < MAX_NESTED_REALM_LEVELS; ++i) {
            if (i > 0) {
                pattern.append('/');
            }
            pattern.append("Realm");
            this.addRuleInstances(digester, pattern.toString(), i == 0 ? "setRealm" : "addRealm");
        }
    }

    private void addRuleInstances(Digester digester, String pattern, String methodName) {
        digester.addObjectCreate(pattern, null, "className");
        digester.addSetProperties(pattern);
        digester.addSetNext(pattern, methodName, "org.apache.catalina.Realm");
        digester.addRuleSet((RuleSet)new CredentialHandlerRuleSet(pattern + "/"));
    }
}

