/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.output;

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.adapters.DOMAdapter;
import org.jdom.output.NamespaceStack;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.EntityReference;

public class DOMOutputter {
    private static final String CVS_ID = "@(#) $RCSfile: DOMOutputter.java,v $ $Revision: 1.43 $ $Date: 2007/11/10 05:29:01 $ $Name:  $";
    private static final String DEFAULT_ADAPTER_CLASS = "org.jdom.adapters.XercesDOMAdapter";
    private String adapterClass;
    private boolean forceNamespaceAware;

    public DOMOutputter() {
    }

    public DOMOutputter(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public void setForceNamespaceAware(boolean flag) {
        this.forceNamespaceAware = flag;
    }

    public boolean getForceNamespaceAware() {
        return this.forceNamespaceAware;
    }

    public org.w3c.dom.Document output(Document document) throws JDOMException {
        NamespaceStack namespaces = new NamespaceStack();
        org.w3c.dom.Document domDoc = null;
        try {
            DocType dt = document.getDocType();
            domDoc = this.createDOMDocument(dt);
            org.w3c.dom.Element autoroot = domDoc.getDocumentElement();
            if (autoroot != null) {
                domDoc.removeChild(autoroot);
            }
            for (Object node : document.getContent()) {
                if (node instanceof Element) {
                    org.w3c.dom.Element domElement = this.output((Element)node, domDoc, namespaces);
                    domDoc.appendChild(domElement);
                    continue;
                }
                if (node instanceof Comment) {
                    Comment comment = (Comment)node;
                    org.w3c.dom.Comment domComment = domDoc.createComment(comment.getText());
                    domDoc.appendChild(domComment);
                    continue;
                }
                if (node instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction)node;
                    org.w3c.dom.ProcessingInstruction domPI = domDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
                    domDoc.appendChild(domPI);
                    continue;
                }
                if (node instanceof DocType) continue;
                throw new JDOMException("Document contained top-level content with type:" + node.getClass().getName());
            }
        }
        catch (Throwable e) {
            throw new JDOMException("Exception outputting Document", e);
        }
        return domDoc;
    }

    private org.w3c.dom.Document createDOMDocument(DocType dt) throws JDOMException {
        if (this.adapterClass != null) {
            try {
                DOMAdapter adapter = (DOMAdapter)Class.forName(this.adapterClass).newInstance();
                return adapter.createDocument(dt);
            }
            catch (ClassNotFoundException adapter) {
            }
            catch (IllegalAccessException adapter) {
            }
            catch (InstantiationException adapter) {}
        } else {
            try {
                DOMAdapter adapter = (DOMAdapter)Class.forName("org.jdom.adapters.JAXPDOMAdapter").newInstance();
                return adapter.createDocument(dt);
            }
            catch (ClassNotFoundException adapter) {
            }
            catch (IllegalAccessException adapter) {
            }
            catch (InstantiationException adapter) {
                // empty catch block
            }
        }
        try {
            DOMAdapter adapter = (DOMAdapter)Class.forName(DEFAULT_ADAPTER_CLASS).newInstance();
            return adapter.createDocument(dt);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InstantiationException instantiationException) {
            // empty catch block
        }
        throw new JDOMException("No JAXP or default parser available");
    }

    private org.w3c.dom.Element output(Element element, org.w3c.dom.Document domDoc, NamespaceStack namespaces) throws JDOMException {
        try {
            int previouslyDeclaredNamespaces = namespaces.size();
            org.w3c.dom.Element domElement = null;
            domElement = element.getNamespace() == Namespace.NO_NAMESPACE ? (this.forceNamespaceAware ? domDoc.createElementNS(null, element.getQualifiedName()) : domDoc.createElement(element.getQualifiedName())) : domDoc.createElementNS(element.getNamespaceURI(), element.getQualifiedName());
            Namespace ns = element.getNamespace();
            if (ns != Namespace.XML_NAMESPACE && (ns != Namespace.NO_NAMESPACE || namespaces.getURI("") != null)) {
                String prefix = ns.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (!ns.getURI().equals(uri)) {
                    namespaces.push(ns);
                    String attrName = DOMOutputter.getXmlnsTagFor(ns);
                    domElement.setAttribute(attrName, ns.getURI());
                }
            }
            for (Namespace additional : element.getAdditionalNamespaces()) {
                String prefix = additional.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (additional.getURI().equals(uri)) continue;
                String attrName = DOMOutputter.getXmlnsTagFor(additional);
                domElement.setAttribute(attrName, additional.getURI());
                namespaces.push(additional);
            }
            for (Attribute attribute : element.getAttributes()) {
                domElement.setAttributeNode(this.output(attribute, domDoc));
                Namespace ns1 = attribute.getNamespace();
                if (ns1 != Namespace.NO_NAMESPACE && ns1 != Namespace.XML_NAMESPACE) {
                    String prefix = ns1.getPrefix();
                    String uri = namespaces.getURI(prefix);
                    if (!ns1.getURI().equals(uri)) {
                        String attrName = DOMOutputter.getXmlnsTagFor(ns1);
                        domElement.setAttribute(attrName, ns1.getURI());
                        namespaces.push(ns1);
                    }
                }
                if (attribute.getNamespace() == Namespace.NO_NAMESPACE) {
                    if (this.forceNamespaceAware) {
                        domElement.setAttributeNS(null, attribute.getQualifiedName(), attribute.getValue());
                        continue;
                    }
                    domElement.setAttribute(attribute.getQualifiedName(), attribute.getValue());
                    continue;
                }
                domElement.setAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName(), attribute.getValue());
            }
            for (Object node : element.getContent()) {
                org.w3c.dom.Text domText;
                if (node instanceof Element) {
                    Element e = (Element)node;
                    org.w3c.dom.Element domElt = this.output(e, domDoc, namespaces);
                    domElement.appendChild(domElt);
                    continue;
                }
                if (node instanceof String) {
                    String str = (String)node;
                    domText = domDoc.createTextNode(str);
                    domElement.appendChild(domText);
                    continue;
                }
                if (node instanceof CDATA) {
                    CDATA cdata = (CDATA)node;
                    CDATASection domCdata = domDoc.createCDATASection(cdata.getText());
                    domElement.appendChild(domCdata);
                    continue;
                }
                if (node instanceof Text) {
                    Text text = (Text)node;
                    domText = domDoc.createTextNode(text.getText());
                    domElement.appendChild(domText);
                    continue;
                }
                if (node instanceof Comment) {
                    Comment comment = (Comment)node;
                    org.w3c.dom.Comment domComment = domDoc.createComment(comment.getText());
                    domElement.appendChild(domComment);
                    continue;
                }
                if (node instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction)node;
                    org.w3c.dom.ProcessingInstruction domPI = domDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
                    domElement.appendChild(domPI);
                    continue;
                }
                if (node instanceof EntityRef) {
                    EntityRef entity = (EntityRef)node;
                    EntityReference domEntity = domDoc.createEntityReference(entity.getName());
                    domElement.appendChild(domEntity);
                    continue;
                }
                throw new JDOMException("Element contained content with type:" + node.getClass().getName());
            }
            while (namespaces.size() > previouslyDeclaredNamespaces) {
                namespaces.pop();
            }
            return domElement;
        }
        catch (Exception e) {
            throw new JDOMException("Exception outputting Element " + element.getQualifiedName(), e);
        }
    }

    private Attr output(Attribute attribute, org.w3c.dom.Document domDoc) throws JDOMException {
        Attr domAttr = null;
        try {
            domAttr = attribute.getNamespace() == Namespace.NO_NAMESPACE ? (this.forceNamespaceAware ? domDoc.createAttributeNS(null, attribute.getQualifiedName()) : domDoc.createAttribute(attribute.getQualifiedName())) : domDoc.createAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName());
            domAttr.setValue(attribute.getValue());
        }
        catch (Exception e) {
            throw new JDOMException("Exception outputting Attribute " + attribute.getQualifiedName(), e);
        }
        return domAttr;
    }

    private static String getXmlnsTagFor(Namespace ns) {
        String attrName = "xmlns";
        if (!ns.getPrefix().equals("")) {
            attrName = attrName + ":";
            attrName = attrName + ns.getPrefix();
        }
        return attrName;
    }
}

