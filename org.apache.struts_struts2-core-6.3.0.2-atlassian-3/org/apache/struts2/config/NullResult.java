/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

public class NullResult
implements Result {
    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        throw new IllegalStateException("Shouldn't be called");
    }
}

