/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.action.PrincipalAware
 *  org.apache.struts2.interceptor.PrincipalProxy
 */
package org.apache.struts2.interceptor;

import org.apache.struts2.interceptor.PrincipalProxy;

@Deprecated(since="1.0.0", forRemoval=true)
public interface PrincipalAware
extends org.apache.struts2.action.PrincipalAware {
    public void setPrincipalProxy(PrincipalProxy var1);

    default public void withPrincipalProxy(PrincipalProxy principalProxy) {
        this.setPrincipalProxy(principalProxy);
    }
}

