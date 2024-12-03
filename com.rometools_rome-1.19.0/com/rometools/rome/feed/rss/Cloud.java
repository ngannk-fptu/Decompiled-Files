/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.rss;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import java.io.Serializable;
import java.util.Collections;

public class Cloud
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 1L;
    private String domain;
    private int port;
    private String path;
    private String registerProcedure;
    private String protocol;

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

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegisterProcedure() {
        return this.registerProcedure;
    }

    public void setRegisterProcedure(String registerProcedure) {
        this.registerProcedure = registerProcedure;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}

