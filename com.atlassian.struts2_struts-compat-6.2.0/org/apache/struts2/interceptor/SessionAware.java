/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.action.SessionAware
 */
package org.apache.struts2.interceptor;

import java.util.Map;

@Deprecated(since="1.0.0", forRemoval=true)
public interface SessionAware
extends org.apache.struts2.action.SessionAware {
    public void setSession(Map<String, Object> var1);

    default public void withSession(Map<String, Object> session) {
        this.setSession(session);
    }
}

