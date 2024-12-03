/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jaxen.BaseXPath
 *  org.jaxen.JaxenException
 *  org.jaxen.NamespaceContext
 *  org.jaxen.Navigator
 *  org.jaxen.SimpleVariableContext
 *  org.jaxen.VariableContext
 *  org.jaxen.XPath
 */
package org.jdom2.xpath.jaxen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.Navigator;
import org.jaxen.SimpleVariableContext;
import org.jaxen.VariableContext;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.xpath.XPath;
import org.jdom2.xpath.jaxen.JDOMNavigator;
import org.jdom2.xpath.jaxen.NamespaceContainer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class JDOMXPath
extends XPath {
    private static final long serialVersionUID = 200L;
    private transient org.jaxen.XPath xPath;
    private final JDOMNavigator navigator = new JDOMNavigator();

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
            ret.add(JDOMXPath.unWrapNS(it.next()));
        }
        return ret;
    }

    public JDOMXPath(String expr) throws JDOMException {
        this.setXPath(expr);
    }

    @Override
    public List<?> selectNodes(Object context) throws JDOMException {
        try {
            this.navigator.setContext(context);
            List<Object> list = JDOMXPath.unWrap(this.xPath.selectNodes(context));
            return list;
        }
        catch (JaxenException ex1) {
            throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), ex1);
        }
        finally {
            this.navigator.reset();
        }
    }

    @Override
    public Object selectSingleNode(Object context) throws JDOMException {
        try {
            this.navigator.setContext(context);
            Object object = JDOMXPath.unWrapNS(this.xPath.selectSingleNode(context));
            return object;
        }
        catch (JaxenException ex1) {
            throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), ex1);
        }
        finally {
            this.navigator.reset();
        }
    }

    @Override
    public String valueOf(Object context) throws JDOMException {
        try {
            this.navigator.setContext(context);
            String string = this.xPath.stringValueOf(context);
            return string;
        }
        catch (JaxenException ex1) {
            throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), ex1);
        }
        finally {
            this.navigator.reset();
        }
    }

    @Override
    public Number numberValueOf(Object context) throws JDOMException {
        try {
            this.navigator.setContext(context);
            Number number = this.xPath.numberValueOf(context);
            return number;
        }
        catch (JaxenException ex1) {
            throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), ex1);
        }
        finally {
            this.navigator.reset();
        }
    }

    @Override
    public void setVariable(String name, Object value) throws IllegalArgumentException {
        VariableContext o = this.xPath.getVariableContext();
        if (o instanceof SimpleVariableContext) {
            ((SimpleVariableContext)o).setVariableValue(null, name, value);
        }
    }

    @Override
    public void addNamespace(Namespace namespace) {
        this.navigator.includeNamespace(namespace);
    }

    @Override
    public String getXPath() {
        return this.xPath.toString();
    }

    private void setXPath(String expr) throws JDOMException {
        try {
            this.xPath = new BaseXPath(expr, (Navigator)this.navigator);
            this.xPath.setNamespaceContext((NamespaceContext)this.navigator);
        }
        catch (Exception ex1) {
            throw new JDOMException("Invalid XPath expression: \"" + expr + "\"", ex1);
        }
    }

    public String toString() {
        return String.format("[XPath: %s]", this.xPath.toString());
    }
}

