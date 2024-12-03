/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.Attribute;
import org.jdom2.Namespace;
import org.jdom2.filter.AbstractFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AttributeFilter
extends AbstractFilter<Attribute> {
    private static final long serialVersionUID = 200L;
    private final String name;
    private final Namespace namespace;

    public AttributeFilter() {
        this(null, null);
    }

    public AttributeFilter(String name) {
        this(name, null);
    }

    public AttributeFilter(Namespace namespace) {
        this(null, namespace);
    }

    public AttributeFilter(String name, Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    @Override
    public Attribute filter(Object content) {
        if (content instanceof Attribute) {
            Attribute att = (Attribute)content;
            if (this.name == null) {
                if (this.namespace == null) {
                    return att;
                }
                return this.namespace.equals(att.getNamespace()) ? att : null;
            }
            if (!this.name.equals(att.getName())) {
                return null;
            }
            if (this.namespace == null) {
                return att;
            }
            return this.namespace.equals(att.getNamespace()) ? att : null;
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AttributeFilter)) {
            return false;
        }
        AttributeFilter filter = (AttributeFilter)obj;
        if (this.name != null ? !this.name.equals(filter.name) : filter.name != null) {
            return false;
        }
        return !(this.namespace != null ? !this.namespace.equals(filter.namespace) : filter.namespace != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 29 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
        return result;
    }
}

