/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

public class MockResult
implements Result {
    public static final String DEFAULT_PARAM = "foo";
    private ActionInvocation invocation;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof MockResult;
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        this.invocation = invocation;
    }

    public int hashCode() {
        return 10;
    }

    public void setFoo(String foo) {
    }

    public ActionInvocation getInvocation() {
        return this.invocation;
    }
}

