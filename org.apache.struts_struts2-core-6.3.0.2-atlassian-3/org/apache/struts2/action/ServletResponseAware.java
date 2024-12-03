/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.action;

import javax.servlet.http.HttpServletResponse;

public interface ServletResponseAware {
    public void withServletResponse(HttpServletResponse var1);
}

