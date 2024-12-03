/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jaxen.BaseXPath
 *  org.jaxen.JaxenException
 *  org.jaxen.NamespaceContext
 *  org.jaxen.Navigator
 *  org.jaxen.UnresolvableException
 *  org.jaxen.VariableContext
 *  org.jaxen.XPath
 */
package org.jdom2.xpath.jaxen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.Navigator;
import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;
import org.jaxen.XPath;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.jaxen.JDOM2Navigator;
import org.jdom2.xpath.jaxen.NamespaceContainer;
import org.jdom2.xpath.util.AbstractXPathCompiled;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class JaxenCompiled<T>
extends AbstractXPathCompiled<T>
implements NamespaceContext,
VariableContext {
    private final XPath xPath;
    private final JDOM2Navigator navigator = new JDOM2Navigator();

    private static final Object unWrapNS(Object o) {
        if (o instanceof NamespaceContainer) {
            return ((NamespaceContainer)o).getNamespace();
        }
        return o;
    }

    private static final List<Object> unWrap(List<?> results) {
        ArrayList<Object> ret = new ArrayList<Object>(results.size());
        Iterator<?> it = results.iterator();
        while (it.hasNext()) {
            ret.add(JaxenCompiled.unWrapNS(it.next()));
        }
        return ret;
    }

    public JaxenCompiled(String expression, Filter<T> filter, Map<String, Object> variables, Namespace[] namespaces) {
        super(expression, filter, variables, namespaces);
        try {
            this.xPath = new BaseXPath(expression, (Navigator)this.navigator);
        }
        catch (JaxenException e) {
            throw new IllegalArgumentException("Unable to compile '" + expression + "'. See Cause.", e);
        }
        this.xPath.setNamespaceContext((NamespaceContext)this);
        this.xPath.setVariableContext((VariableContext)this);
    }

    private JaxenCompiled(JaxenCompiled<T> toclone) {
        this(toclone.getExpression(), toclone.getFilter(), toclone.getVariables(), toclone.getNamespaces());
    }

    public String translateNamespacePrefixToUri(String prefix) {
        return this.getNamespace(prefix).getURI();
    }

    public Object getVariableValue(String namespaceURI, String prefix, String localName) throws UnresolvableException {
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (prefix == null) {
            prefix = "";
        }
        try {
            if ("".equals(namespaceURI)) {
                namespaceURI = this.getNamespace(prefix).getURI();
            }
            return this.getVariable(localName, Namespace.getNamespace(namespaceURI));
        }
        catch (IllegalArgumentException e) {
            throw new UnresolvableException("Unable to resolve variable " + localName + " in namespace '" + namespaceURI + "' to a value.");
        }
    }

    @Override
    protected List<?> evaluateRawAll(Object context) {
        try {
            return JaxenCompiled.unWrap(this.xPath.selectNodes(context));
        }
        catch (JaxenException e) {
            throw new IllegalStateException("Unable to evaluate expression. See cause", e);
        }
    }

    @Override
    protected Object evaluateRawFirst(Object context) {
        try {
            return JaxenCompiled.unWrapNS(this.xPath.selectSingleNode(context));
        }
        catch (JaxenException e) {
            throw new IllegalStateException("Unable to evaluate expression. See cause", e);
        }
    }

    @Override
    public JaxenCompiled<T> clone() {
        return new JaxenCompiled<T>(this);
    }
}

