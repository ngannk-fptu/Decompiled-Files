/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Alternatives
 *  com.rometools.utils.Lists
 */
package com.rometools.rome.feed.atom;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.Extendable;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.utils.Alternatives;
import com.rometools.utils.Lists;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Person
implements SyndPerson,
Cloneable,
Serializable,
Extendable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String uri;
    private String uriResolved;
    private String email;
    private List<Module> modules;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        return EqualsBean.beanEquals(this.getClass(), this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.getClass(), this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.uri;
    }

    public void setUrl(String url) {
        this.uri = url;
    }

    public void setUriResolved(String uriResolved) {
        this.uriResolved = uriResolved;
    }

    public String getUriResolved(String resolveURI) {
        return (String)Alternatives.firstNotNull((Object[])new String[]{this.uriResolved, this.uri});
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public List<Module> getModules() {
        this.modules = Lists.createWhenNull(this.modules);
        return this.modules;
    }

    @Override
    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public Module getModule(String uri) {
        return ModuleUtils.getModule(this.modules, uri);
    }
}

