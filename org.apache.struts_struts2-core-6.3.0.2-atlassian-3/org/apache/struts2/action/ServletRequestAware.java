/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.struts2.action;

import javax.servlet.http.HttpServletRequest;

public interface ServletRequestAware {
    public void withServletRequest(HttpServletRequest var1);
}

