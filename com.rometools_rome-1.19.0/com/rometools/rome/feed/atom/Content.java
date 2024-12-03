/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Strings
 */
package com.rometools.rome.feed.atom;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.utils.Strings;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Content
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private String value;
    private String src;
    public static final String TEXT = "text";
    public static final String HTML = "html";
    public static final String XHTML = "xhtml";
    public static final String XML = "xml";
    public static final String BASE64 = "base64";
    public static final String ESCAPED = "escaped";
    private String mode;
    private static final Set<String> MODES = new HashSet<String>();

    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (!(other instanceof Content)) {
            return false;
        }
        return EqualsBean.beanEquals(this.getClass(), this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.getClass(), this);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = Strings.toLowerCase((String)mode);
        if (mode == null || !MODES.contains(mode)) {
            throw new IllegalArgumentException("Invalid mode [" + mode + "]");
        }
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSrc() {
        return this.src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    static {
        MODES.add(XML);
        MODES.add(BASE64);
        MODES.add(ESCAPED);
    }
}

