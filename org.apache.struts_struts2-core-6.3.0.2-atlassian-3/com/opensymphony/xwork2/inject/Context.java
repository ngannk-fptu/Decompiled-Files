/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;
import java.lang.reflect.Member;

public interface Context {
    public Container getContainer();

    public Scope.Strategy getScopeStrategy();

    public Member getMember();

    public Class<?> getType();

    public String getName();
}

