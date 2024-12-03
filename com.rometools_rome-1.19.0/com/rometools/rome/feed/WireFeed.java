/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 *  org.jdom2.Element
 */
package com.rometools.rome.feed;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.Extendable;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.utils.Lists;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.jdom2.Element;

public abstract class WireFeed
implements Cloneable,
Serializable,
Extendable {
    private static final long serialVersionUID = 1L;
    private String feedType;
    private String encoding;
    private String styleSheet;
    private List<Module> modules;
    private List<Element> foreignMarkup;

    protected WireFeed() {
    }

    protected WireFeed(String type) {
        this();
        this.feedType = type;
    }

    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof WireFeed)) {
            return false;
        }
        List<Element> fm = this.getForeignMarkup();
        this.setForeignMarkup(((WireFeed)other).getForeignMarkup());
        boolean ret = EqualsBean.beanEquals(this.getClass(), this, other);
        this.setForeignMarkup(fm);
        return ret;
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.getClass(), this);
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedType() {
        return this.feedType;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
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

    public List<Element> getForeignMarkup() {
        this.foreignMarkup = Lists.createWhenNull(this.foreignMarkup);
        return this.foreignMarkup;
    }

    public void setForeignMarkup(List<Element> foreignMarkup) {
        this.foreignMarkup = foreignMarkup;
    }

    public String getStyleSheet() {
        return this.styleSheet;
    }

    public void setStyleSheet(String styleSheet) {
        this.styleSheet = styleSheet;
    }
}

