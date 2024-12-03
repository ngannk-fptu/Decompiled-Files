/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Util;

public class WebdavProperty
implements Serializable {
    private final QName tag;
    private String pval;
    private List<Attribute> attrs;

    public WebdavProperty(QName tag, String pval) {
        this.tag = tag;
        this.pval = pval;
    }

    public QName getTag() {
        return this.tag;
    }

    public void setPval(String val) {
        this.pval = val;
    }

    public String getPval() {
        return this.pval;
    }

    public List<Attribute> getAttrs() {
        if (this.attrs == null) {
            this.attrs = new ArrayList<Attribute>();
        }
        return this.attrs;
    }

    public boolean hasAttrs() {
        return !Util.isEmpty(this.attrs);
    }

    public String getAttr(String name) {
        if (!this.hasAttrs()) {
            return null;
        }
        for (Attribute attr : this.attrs) {
            if (!attr.name.equals(name)) continue;
            return attr.value;
        }
        return null;
    }

    public void addAttr(String name, String val) {
        this.getAttrs().add(new Attribute(name, val));
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("tag", this.getTag());
        ts.append("pval", this.getPval());
        ts.append("attrs", this.attrs);
        return ts.toString();
    }

    public static class Attribute
    implements Serializable {
        public String name;
        public String value;

        Attribute(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}

