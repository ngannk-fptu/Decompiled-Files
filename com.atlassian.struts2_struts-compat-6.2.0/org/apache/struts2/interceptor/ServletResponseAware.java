/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.action.ServletResponseAware
 */
package org.apache.struts2.interceptor;

import javax.servlet.http.HttpServletResponse;

@Deprecated(since="1.0.0", forRemoval=true)
public interface ServletResponseAware
extends org.apache.struts2.action.ServletResponseAware {
    public void setServletResponse(HttpServletResponse var1);

    default public void withServletResponse(HttpServletResponse response) {
        this.setServletResponse(response);
    }
}

