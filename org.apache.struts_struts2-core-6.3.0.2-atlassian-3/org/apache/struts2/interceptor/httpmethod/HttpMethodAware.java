/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor.httpmethod;

import org.apache.struts2.interceptor.httpmethod.HttpMethod;

public interface HttpMethodAware {
    public void setMethod(HttpMethod var1);

    public String getBadRequestResultName();
}

