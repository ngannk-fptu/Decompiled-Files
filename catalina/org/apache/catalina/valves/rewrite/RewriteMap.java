/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.valves.rewrite;

import org.apache.tomcat.util.res.StringManager;

public interface RewriteMap {
    public String setParameters(String var1);

    default public void setParameters(String ... params) {
        if (params == null) {
            return;
        }
        if (params.length > 1) {
            throw new IllegalArgumentException(StringManager.getManager(RewriteMap.class).getString("rewriteMap.tooManyParameters"));
        }
        this.setParameters(params[0]);
    }

    public String lookup(String var1);
}

