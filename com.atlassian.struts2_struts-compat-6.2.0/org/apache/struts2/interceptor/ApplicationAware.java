/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.action.ApplicationAware
 */
package org.apache.struts2.interceptor;

import java.util.Map;

@Deprecated(since="1.0.0", forRemoval=true)
public interface ApplicationAware
extends org.apache.struts2.action.ApplicationAware {
    public void setApplication(Map<String, Object> var1);

    default public void withApplication(Map<String, Object> application) {
        this.setApplication(application);
    }
}

