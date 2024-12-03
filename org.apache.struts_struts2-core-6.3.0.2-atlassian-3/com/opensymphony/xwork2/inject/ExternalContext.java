/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerImpl;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Key;
import com.opensymphony.xwork2.inject.Scope;
import java.lang.reflect.Member;
import java.util.LinkedHashMap;

class ExternalContext<T>
implements Context {
    final Member member;
    final Key<T> key;
    final ContainerImpl container;

    public ExternalContext(Member member, Key<T> key, ContainerImpl container) {
        this.member = member;
        this.key = key;
        this.container = container;
    }

    public Class<T> getType() {
        return this.key.getType();
    }

    @Override
    public Scope.Strategy getScopeStrategy() {
        return (Scope.Strategy)this.container.localScopeStrategy.get();
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public Member getMember() {
        return this.member;
    }

    @Override
    public String getName() {
        return this.key.getName();
    }

    public String toString() {
        return "Context" + new LinkedHashMap<String, Object>(){
            {
                this.put("member", ExternalContext.this.member);
                this.put("type", ExternalContext.this.getType());
                this.put("name", ExternalContext.this.getName());
                this.put("container", ExternalContext.this.container);
            }
        }.toString();
    }

    static <T> ExternalContext<T> newInstance(Member member, Key<T> key, ContainerImpl container) {
        return new ExternalContext<T>(member, key, container);
    }
}

