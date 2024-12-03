/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jaxen.BaseXPath
 *  org.jaxen.DefaultNavigator
 *  org.jaxen.FunctionCallException
 *  org.jaxen.JaxenConstants
 *  org.jaxen.Navigator
 *  org.jaxen.UnsupportedAxisException
 *  org.jaxen.XPath
 *  org.jaxen.saxpath.SAXPathException
 *  org.jaxen.util.SingleObjectIterator
 */
package org.jdom2.xpath.jaxen;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import org.jaxen.BaseXPath;
import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenConstants;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;
import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.jaxen.NamespaceContainer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class JDOMCoreNavigator
extends DefaultNavigator {
    private static final long serialVersionUID = 200L;
    private transient IdentityHashMap<Element, NamespaceContainer[]> emtnsmap = new IdentityHashMap();

    JDOMCoreNavigator() {
    }

    void reset() {
        this.emtnsmap.clear();
    }

    public final XPath parseXPath(String path) throws SAXPathException {
        return new BaseXPath(path, (Navigator)this);
    }

    public final Object getDocument(String url) throws FunctionCallException {
        SAXBuilder sb = new SAXBuilder();
        try {
            return sb.build(url);
        }
        catch (JDOMException e) {
            throw new FunctionCallException("Failed to parse " + url, (Exception)e);
        }
        catch (IOException e) {
            throw new FunctionCallException("Failed to access " + url, (Exception)e);
        }
    }

    public final boolean isText(Object isit) {
        return isit instanceof Text;
    }

    public final boolean isProcessingInstruction(Object isit) {
        return isit instanceof ProcessingInstruction;
    }

    public final boolean isNamespace(Object isit) {
        return isit instanceof NamespaceContainer;
    }

    public final boolean isElement(Object isit) {
        return isit instanceof Element;
    }

    public final boolean isDocument(Object isit) {
        return isit instanceof Document;
    }

    public final boolean isComment(Object isit) {
        return isit instanceof Comment;
    }

    public final boolean isAttribute(Object isit) {
        return isit instanceof Attribute;
    }

    public final String getTextStringValue(Object text) {
        return ((Text)text).getText();
    }

    public final String getNamespaceStringValue(Object namespace) {
        return ((NamespaceContainer)namespace).getNamespace().getURI();
    }

    public final String getNamespacePrefix(Object namespace) {
        return ((NamespaceContainer)namespace).getNamespace().getPrefix();
    }

    private final void recurseElementText(Element element, StringBuilder sb) {
        for (Content c : element.getContent()) {
            if (c instanceof Element) {
                this.recurseElementText((Element)c, sb);
                continue;
            }
            if (!(c instanceof Text)) continue;
            sb.append(((Text)c).getText());
        }
    }

    public final String getElementStringValue(Object element) {
        StringBuilder sb = new StringBuilder();
        this.recurseElementText((Element)element, sb);
        return sb.toString();
    }

    public final String getElementQName(Object element) {
        Element e = (Element)element;
        if (e.getNamespace().getPrefix().length() == 0) {
            return e.getName();
        }
        return e.getNamespacePrefix() + ":" + e.getName();
    }

    public final String getElementNamespaceUri(Object element) {
        return ((Element)element).getNamespaceURI();
    }

    public final String getElementName(Object element) {
        return ((Element)element).getName();
    }

    public final String getCommentStringValue(Object comment) {
        return ((Comment)comment).getValue();
    }

    public final String getAttributeStringValue(Object attribute) {
        return ((Attribute)attribute).getValue();
    }

    public final String getAttributeQName(Object att) {
        Attribute attribute = (Attribute)att;
        if (attribute.getNamespacePrefix().length() == 0) {
            return attribute.getName();
        }
        return attribute.getNamespacePrefix() + ":" + attribute.getName();
    }

    public final String getAttributeNamespaceUri(Object attribute) {
        return ((Attribute)attribute).getNamespaceURI();
    }

    public final String getAttributeName(Object attribute) {
        return ((Attribute)attribute).getName();
    }

    public final String getProcessingInstructionTarget(Object pi) {
        return ((ProcessingInstruction)pi).getTarget();
    }

    public final String getProcessingInstructionData(Object pi) {
        return ((ProcessingInstruction)pi).getData();
    }

    public final Object getDocumentNode(Object contextNode) {
        if (contextNode instanceof Document) {
            return contextNode;
        }
        if (contextNode instanceof NamespaceContainer) {
            return ((NamespaceContainer)contextNode).getParentElement().getDocument();
        }
        if (contextNode instanceof Attribute) {
            return ((Attribute)contextNode).getDocument();
        }
        return ((Content)contextNode).getDocument();
    }

    public final Object getParentNode(Object contextNode) throws UnsupportedAxisException {
        if (contextNode instanceof Document) {
            return null;
        }
        if (contextNode instanceof NamespaceContainer) {
            return ((NamespaceContainer)contextNode).getParentElement();
        }
        if (contextNode instanceof Content) {
            return ((Content)contextNode).getParent();
        }
        if (contextNode instanceof Attribute) {
            return ((Attribute)contextNode).getParent();
        }
        return null;
    }

    public final Iterator<?> getAttributeAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (this.isElement(contextNode) && ((Element)contextNode).hasAttributes()) {
            return ((Element)contextNode).getAttributes().iterator();
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    public final Iterator<?> getChildAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (contextNode instanceof Parent) {
            return ((Parent)contextNode).getContent().iterator();
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    public final Iterator<?> getNamespaceAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (!this.isElement(contextNode)) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        NamespaceContainer[] ret = this.emtnsmap.get(contextNode);
        if (ret == null) {
            List<Namespace> nsl = ((Element)contextNode).getNamespacesInScope();
            ret = new NamespaceContainer[nsl.size()];
            int i = 0;
            for (Namespace ns : nsl) {
                ret[i++] = new NamespaceContainer(ns, (Element)contextNode);
            }
            this.emtnsmap.put((Element)contextNode, ret);
        }
        return Arrays.asList(ret).iterator();
    }

    public final Iterator<?> getParentAxisIterator(Object contextNode) throws UnsupportedAxisException {
        Parent p = null;
        if (contextNode instanceof Content) {
            p = ((Content)contextNode).getParent();
        } else if (contextNode instanceof NamespaceContainer) {
            p = ((NamespaceContainer)contextNode).getParentElement();
        } else if (contextNode instanceof Attribute) {
            p = ((Attribute)contextNode).getParent();
        }
        if (p != null) {
            return new SingleObjectIterator((Object)p);
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.emtnsmap = new IdentityHashMap();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
}

