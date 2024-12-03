/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.filter.AndFilter;
import org.bedework.caldav.util.filter.OrFilter;
import org.bedework.util.misc.ToString;
import org.bedework.webdav.servlet.shared.WebdavException;

public class FilterBase
implements Serializable {
    protected String name;
    protected String description;
    protected boolean not;
    protected boolean cache = true;
    protected FilterBase parent;
    protected List<FilterBase> children;

    public FilterBase(String name) {
        this.setName(name);
    }

    public void setName(String val) {
        this.name = val;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String val) {
        this.description = val;
    }

    public String getDescription() {
        return this.description;
    }

    public void setNot(boolean val) {
        this.not = val;
    }

    public boolean getNot() {
        return this.not;
    }

    public void setCache(boolean val) {
        this.cache = val;
    }

    public boolean getCache() {
        return this.cache;
    }

    public void setParent(FilterBase val) {
        this.parent = val;
    }

    public FilterBase getParent() {
        return this.parent;
    }

    public void setChildren(List<FilterBase> val) {
        this.children = val;
    }

    public List<FilterBase> getChildren() {
        return this.children;
    }

    public int getNumChildren() {
        List<FilterBase> c = this.getChildren();
        if (c == null) {
            return 0;
        }
        return c.size();
    }

    public void addChild(FilterBase val) {
        if (val == null) {
            return;
        }
        List<FilterBase> c = this.getChildren();
        if (c == null) {
            c = new ArrayList<FilterBase>();
            this.setChildren(c);
        }
        c.add(val);
        val.setParent(this);
    }

    public boolean match(Object o, String userHref) throws WebdavException {
        return false;
    }

    public static FilterBase addOrChild(FilterBase filter, FilterBase child) {
        OrFilter orf;
        if (child == null) {
            return filter;
        }
        if (filter == null) {
            return child;
        }
        if (filter instanceof OrFilter) {
            orf = (OrFilter)filter;
        } else {
            orf = new OrFilter();
            orf.addChild(filter);
        }
        orf.addChild(child);
        return orf;
    }

    public static FilterBase addAndChild(FilterBase filter, FilterBase child) {
        AndFilter andf;
        if (child == null) {
            return filter;
        }
        if (filter == null) {
            return child;
        }
        if (filter instanceof AndFilter) {
            andf = (AndFilter)filter;
        } else {
            andf = new AndFilter();
            andf.addChild(filter);
        }
        andf.addChild(child);
        return andf;
    }

    protected void stringOper(StringBuilder sb) {
        if (this.getNot()) {
            sb.append(" != ");
        } else {
            sb.append(" = ");
        }
    }

    protected void toStringSegment(ToString ts) {
        ts.append("description", this.description);
        if (this.parent != null) {
            ts.append("parent", this.parent.getName());
        }
    }
}

