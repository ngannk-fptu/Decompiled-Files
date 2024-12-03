/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath.jaxen;

import java.util.Map;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.xpath.jaxen.JaxenCompiled;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JaxenXPathFactory
extends XPathFactory {
    @Override
    public <T> XPathExpression<T> compile(String expression, Filter<T> filter, Map<String, Object> variables, Namespace ... namespaces) {
        return new JaxenCompiled<T>(expression, filter, variables, namespaces);
    }
}

