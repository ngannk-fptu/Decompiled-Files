/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.internal.ReflectionConstructor;
import org.jdom2.internal.SystemProperty;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.jaxen.JaxenXPathFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class XPathFactory {
    private static final Namespace[] EMPTYNS = new Namespace[0];
    private static final AtomicReference<XPathFactory> defaultreference = new AtomicReference();
    private static final String DEFAULTFACTORY = SystemProperty.get("org.jdom2.xpath.XPathFactory", null);

    public static final XPathFactory instance() {
        XPathFactory fac;
        XPathFactory ret = defaultreference.get();
        if (ret != null) {
            return ret;
        }
        XPathFactory xPathFactory = fac = DEFAULTFACTORY == null ? new JaxenXPathFactory() : XPathFactory.newInstance(DEFAULTFACTORY);
        if (defaultreference.compareAndSet(null, fac)) {
            return fac;
        }
        return defaultreference.get();
    }

    public static final XPathFactory newInstance(String factoryclass) {
        return ReflectionConstructor.construct(factoryclass, XPathFactory.class);
    }

    public abstract <T> XPathExpression<T> compile(String var1, Filter<T> var2, Map<String, Object> var3, Namespace ... var4);

    public <T> XPathExpression<T> compile(String expression, Filter<T> filter, Map<String, Object> variables, Collection<Namespace> namespaces) {
        return this.compile(expression, filter, variables, namespaces.toArray(EMPTYNS));
    }

    public <T> XPathExpression<T> compile(String expression, Filter<T> filter) {
        return this.compile(expression, filter, null, EMPTYNS);
    }

    public XPathExpression<Object> compile(String expression) {
        return this.compile(expression, Filters.fpassthrough(), null, EMPTYNS);
    }
}

