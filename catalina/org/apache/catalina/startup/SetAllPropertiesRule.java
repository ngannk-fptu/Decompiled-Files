/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.SetPropertiesRule
 */
package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.SetPropertiesRule;

@Deprecated
public class SetAllPropertiesRule
extends SetPropertiesRule {
    public SetAllPropertiesRule() {
    }

    public SetAllPropertiesRule(String[] exclude) {
        super(exclude);
    }
}

