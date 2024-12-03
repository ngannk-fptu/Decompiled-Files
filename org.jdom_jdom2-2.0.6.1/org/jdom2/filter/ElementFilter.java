/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.AbstractFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ElementFilter
extends AbstractFilter<Element> {
    private static final long serialVersionUID = 200L;
    private String name;
    private Namespace namespace;

    public ElementFilter() {
    }

    public ElementFilter(String name) {
        this.name = name;
    }

    public ElementFilter(Namespace namespace) {
        this.namespace = namespace;
    }

    public ElementFilter(String name, Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    @Override
    public Element filter(Object content) {
        if (content instanceof Element) {
            Element el = (Element)content;
            if (this.name == null) {
                if (this.namespace == null) {
                    return el;
                }
                return this.namespace.equals(el.getNamespace()) ? el : null;
            }
            if (!this.name.equals(el.getName())) {
                return null;
            }
            if (this.namespace == null) {
                return el;
            }
            return this.namespace.equals(el.getNamespace()) ? el : null;
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ElementFilter)) {
            return false;
        }
        ElementFilter filter = (ElementFilter)obj;
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

    public String toString() {
        return "[ElementFilter: Name " + (this.name == null ? "*any*" : this.name) + " with Namespace " + this.namespace + "]";
    }
}

