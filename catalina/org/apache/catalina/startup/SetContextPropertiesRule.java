/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.SetPropertiesRule
 */
package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.SetPropertiesRule;

@Deprecated
public class SetContextPropertiesRule
extends SetPropertiesRule {
    public SetContextPropertiesRule() {
        super(new String[]{"path", "docBase"});
    }
}

