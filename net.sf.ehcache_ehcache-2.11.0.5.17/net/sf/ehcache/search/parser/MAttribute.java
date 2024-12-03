/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.parser.ModelElement;

public class MAttribute
implements ModelElement<Attribute<?>> {
    private final String name;
    private final boolean isKey;
    private final boolean isValue;
    private final boolean isStar;
    public static MAttribute KEY = new MAttribute("key", true, false, false);
    public static MAttribute VALUE = new MAttribute("value", false, true, false);
    public static MAttribute STAR = new MAttribute("star", false, false, true);

    private MAttribute(String name, boolean k, boolean v, boolean isStar) {
        this.name = name;
        this.isKey = k;
        this.isValue = v;
        this.isStar = isStar;
    }

    public MAttribute(String name) {
        this(name, false, false, false);
    }

    public String getName() {
        return this.name;
    }

    public boolean isKey() {
        return this.isKey;
    }

    public boolean isValue() {
        return this.isValue;
    }

    public boolean isStar() {
        return this.isStar;
    }

    public String asEhcacheAttributeString() {
        if (this.isKey() || this.isStar()) {
            return Query.KEY.getAttributeName();
        }
        if (this.isValue()) {
            return Query.VALUE.getAttributeName();
        }
        return this.name;
    }

    @Override
    public Attribute<?> asEhcacheObject(ClassLoader loader) {
        if (this.isKey() || this.isStar()) {
            return Query.KEY;
        }
        if (this.isValue()) {
            return Query.VALUE;
        }
        return new Attribute(this.name);
    }

    public String toString() {
        if (this.isKey()) {
            return this.name;
        }
        if (this.isValue()) {
            return this.name;
        }
        return "'" + this.name + "'";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.isKey ? 1231 : 1237);
        result = 31 * result + (this.isValue ? 1231 : 1237);
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MAttribute other = (MAttribute)obj;
        if (this.isKey != other.isKey) {
            return false;
        }
        if (this.isValue != other.isValue) {
            return false;
        }
        return !(this.name == null ? other.name != null : !this.name.equals(other.name));
    }
}

