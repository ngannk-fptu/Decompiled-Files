/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jaxen.JaxenException
 *  org.jaxen.NamespaceContext
 *  org.jaxen.SimpleNamespaceContext
 *  org.jaxen.SimpleVariableContext
 *  org.jaxen.VariableContext
 *  org.jaxen.jdom.JDOMXPath
 */
package org.jdom.xpath;

import java.util.List;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.VariableContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

class JaxenXPath
extends XPath {
    private static final String CVS_ID = "@(#) $RCSfile: JaxenXPath.java,v $ $Revision: 1.20 $ $Date: 2007/11/10 05:29:02 $ $Name:  $";
    private transient JDOMXPath xPath;
    private Object currentContext;

    public JaxenXPath(String expr) throws JDOMException {
        this.setXPath(expr);
    }

    @Override
    public List selectNodes(Object context) throws JDOMException {
        try {
            this.currentContext = context;
            List list = this.xPath.selectNodes(context);
            return list;
        }
        catch (JaxenException ex1) {
            throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), ex1);
        }
        finally {
            this.currentContext = null;
        }
    }

    @Override
    public Object selectSingleNode(Object context) throws JDOMException {
        try {
            this.currentContext = context;
            Object object = this.xPath.selectSingleNode(context);
            return object;
        }
        catch (JaxenException ex1) {
            throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), ex1);
        }
        finally {
            this.currentContext = null;
        }
    }

    @Override
    public String valueOf(Object context) throws JDOMException {
        try {
            this.currentContext = context;
            String string = this.xPath.stringValueOf(context);
            return string;
        }
        catch (JaxenException ex1) {
            throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), ex1);
        }
        finally {
            this.currentContext = null;
        }
    }

    @Override
    public Number numberValueOf(Object context) throws JDOMException {
        try {
            this.currentContext = context;
            Number number = this.xPath.numberValueOf(context);
            return number;
        }
        catch (JaxenException ex1) {
            throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), ex1);
        }
        finally {
            this.currentContext = null;
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
        try {
            this.xPath.addNamespace(namespace.getPrefix(), namespace.getURI());
        }
        catch (JaxenException jaxenException) {
            // empty catch block
        }
    }

    @Override
    public String getXPath() {
        return this.xPath.toString();
    }

    private void setXPath(String expr) throws JDOMException {
        try {
            this.xPath = new JDOMXPath(expr);
            this.xPath.setNamespaceContext((NamespaceContext)new NSContext());
        }
        catch (Exception ex1) {
            throw new JDOMException("Invalid XPath expression: \"" + expr + "\"", ex1);
        }
    }

    public String toString() {
        return this.xPath.toString();
    }

    public boolean equals(Object o) {
        if (o instanceof JaxenXPath) {
            JaxenXPath x = (JaxenXPath)o;
            return super.equals(o) && this.xPath.toString().equals(x.xPath.toString());
        }
        return false;
    }

    public int hashCode() {
        return this.xPath.hashCode();
    }

    private class NSContext
    extends SimpleNamespaceContext {
        public String translateNamespacePrefixToUri(String prefix) {
            Object ctx;
            if (prefix == null || prefix.length() == 0) {
                return null;
            }
            String uri = super.translateNamespacePrefixToUri(prefix);
            if (uri == null && (ctx = JaxenXPath.this.currentContext) != null) {
                Namespace ns;
                Element elt = null;
                if (ctx instanceof Element) {
                    elt = (Element)ctx;
                } else if (ctx instanceof Attribute) {
                    elt = ((Attribute)ctx).getParent();
                } else if (ctx instanceof Content) {
                    elt = ((Content)ctx).getParentElement();
                } else if (ctx instanceof Document) {
                    elt = ((Document)ctx).getRootElement();
                }
                if (elt != null && (ns = elt.getNamespace(prefix)) != null) {
                    uri = ns.getURI();
                }
            }
            return uri;
        }
    }
}

