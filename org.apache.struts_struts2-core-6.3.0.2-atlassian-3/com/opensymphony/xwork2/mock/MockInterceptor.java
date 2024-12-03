/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Assert
 */
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.junit.Assert;

public class MockInterceptor
extends AbstractInterceptor {
    private static final long serialVersionUID = 2692551676567227756L;
    public static final String DEFAULT_FOO_VALUE = "fooDefault";
    private String expectedFoo = "fooDefault";
    private String foo = "fooDefault";
    private boolean executed = false;

    public boolean isExecuted() {
        return this.executed;
    }

    public void setExpectedFoo(String expectedFoo) {
        this.expectedFoo = expectedFoo;
    }

    public String getExpectedFoo() {
        return this.expectedFoo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return this.foo;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MockInterceptor)) {
            return false;
        }
        MockInterceptor testInterceptor = (MockInterceptor)o;
        if (this.executed != testInterceptor.executed) {
            return false;
        }
        if (this.expectedFoo != null ? !this.expectedFoo.equals(testInterceptor.expectedFoo) : testInterceptor.expectedFoo != null) {
            return false;
        }
        return !(this.foo != null ? !this.foo.equals(testInterceptor.foo) : testInterceptor.foo != null);
    }

    public int hashCode() {
        int result = this.expectedFoo != null ? this.expectedFoo.hashCode() : 0;
        result = 29 * result + (this.foo != null ? this.foo.hashCode() : 0);
        result = 29 * result + (this.executed ? 1 : 0);
        return result;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        this.executed = true;
        Assert.assertNotSame((Object)DEFAULT_FOO_VALUE, (Object)this.foo);
        Assert.assertEquals((Object)this.expectedFoo, (Object)this.foo);
        return invocation.invoke();
    }
}

