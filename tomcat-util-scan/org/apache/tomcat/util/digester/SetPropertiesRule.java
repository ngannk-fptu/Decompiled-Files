/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils
 */
package org.apache.tomcat.util.digester;

import java.util.HashMap;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

public class SetPropertiesRule
extends Rule {
    protected final HashMap<String, String> excludes;

    public SetPropertiesRule() {
        this.excludes = null;
    }

    public SetPropertiesRule(String[] exclude) {
        this.excludes = new HashMap();
        for (String s : exclude) {
            if (s == null) continue;
            this.excludes.put(s, s);
        }
    }

    @Override
    public void begin(String namespace, String theName, Attributes attributes) throws Exception {
        Object top = this.digester.peek();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " properties"));
        }
        StringBuilder code = this.digester.getGeneratedCode();
        String variableName = null;
        if (code != null) {
            variableName = this.digester.toVariableName(top);
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            String name = attributes.getLocalName(i);
            if (name.isEmpty()) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Setting property '" + name + "' to '" + value + "'"));
            }
            if (this.digester.isFakeAttribute(top, name) || this.excludes != null && this.excludes.containsKey(name)) continue;
            StringBuilder actualMethod = null;
            if (code != null) {
                actualMethod = new StringBuilder();
            }
            if (!IntrospectionUtils.setProperty((Object)top, (String)name, (String)value, (boolean)true, (StringBuilder)actualMethod)) {
                if (!this.digester.getRulesValidation() || "optional".equals(name)) continue;
                this.digester.log.warn((Object)sm.getString("rule.noProperty", new Object[]{this.digester.match, name, value}));
                continue;
            }
            if (code == null) continue;
            code.append(variableName).append('.').append((CharSequence)actualMethod).append(';');
            code.append(System.lineSeparator());
        }
        if (top instanceof Listener) {
            ((Listener)top).endSetPropertiesRule();
            if (code != null) {
                code.append("((org.apache.tomcat.util.digester.SetPropertiesRule.Listener) ");
                code.append(variableName).append(").endSetPropertiesRule();");
                code.append(System.lineSeparator());
            }
        }
    }

    public String toString() {
        return "SetPropertiesRule[]";
    }

    public static interface Listener {
        public void endSetPropertiesRule();
    }
}

