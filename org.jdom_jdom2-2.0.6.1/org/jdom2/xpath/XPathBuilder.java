/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XPathBuilder<T> {
    private final Filter<T> filter;
    private final String expression;
    private Map<String, Object> variables;
    private Map<String, Namespace> namespaces;

    public XPathBuilder(String expression, Filter<T> filter) {
        if (expression == null) {
            throw new NullPointerException("Null expression");
        }
        if (filter == null) {
            throw new NullPointerException("Null filter");
        }
        this.filter = filter;
        this.expression = expression;
    }

    public boolean setVariable(String qname, Object value) {
        if (qname == null) {
            throw new NullPointerException("Null variable name");
        }
        if (this.variables == null) {
            this.variables = new HashMap<String, Object>();
        }
        return this.variables.put(qname, value) == null;
    }

    public boolean setNamespace(String prefix, String uri) {
        if (prefix == null) {
            throw new NullPointerException("Null prefix");
        }
        if (uri == null) {
            throw new NullPointerException("Null URI");
        }
        return this.setNamespace(Namespace.getNamespace(prefix, uri));
    }

    public boolean setNamespace(Namespace namespace) {
        if (namespace == null) {
            throw new NullPointerException("Null Namespace");
        }
        if ("".equals(namespace.getPrefix())) {
            if (Namespace.NO_NAMESPACE != namespace) {
                throw new IllegalArgumentException("Cannot set a Namespace URI in XPath for the \"\" prefix.");
            }
            return false;
        }
        if (this.namespaces == null) {
            this.namespaces = new HashMap<String, Namespace>();
        }
        return this.namespaces.put(namespace.getPrefix(), namespace) == null;
    }

    public boolean setNamespaces(Collection<Namespace> namespaces) {
        if (namespaces == null) {
            throw new NullPointerException("Null namespaces Collection");
        }
        boolean ret = false;
        for (Namespace ns : namespaces) {
            if (!this.setNamespace(ns)) continue;
            ret = true;
        }
        return ret;
    }

    public Object getVariable(String qname) {
        if (qname == null) {
            throw new NullPointerException("Null qname");
        }
        if (this.variables == null) {
            return null;
        }
        return this.variables.get(qname);
    }

    public Namespace getNamespace(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("Null prefix");
        }
        if ("".equals(prefix)) {
            return Namespace.NO_NAMESPACE;
        }
        if (this.namespaces == null) {
            return null;
        }
        return this.namespaces.get(prefix);
    }

    public Filter<T> getFilter() {
        return this.filter;
    }

    public String getExpression() {
        return this.expression;
    }

    public XPathExpression<T> compileWith(XPathFactory factory) {
        if (this.namespaces == null) {
            return factory.compile(this.expression, this.filter, this.variables, new Namespace[0]);
        }
        return factory.compile(this.expression, this.filter, this.variables, this.namespaces.values().toArray(new Namespace[this.namespaces.size()]));
    }
}

