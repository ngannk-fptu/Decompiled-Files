/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.action.ServletRequestAware
 */
package org.apache.struts2.interceptor;

import javax.servlet.http.HttpServletRequest;

@Deprecated(since="1.0.0", forRemoval=true)
public interface ServletRequestAware
extends org.apache.struts2.action.ServletRequestAware {
    public void setServletRequest(HttpServletRequest var1);

    default public void withServletRequest(HttpServletRequest request) {
        this.setServletRequest(request);
    }
}

