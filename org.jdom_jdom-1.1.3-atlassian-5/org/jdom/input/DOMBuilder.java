/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.input;

import java.util.HashSet;
import org.jdom.Attribute;
import org.jdom.DefaultJDOMFactory;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMFactory;
import org.jdom.Namespace;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMBuilder {
    private static final String CVS_ID = "@(#) $RCSfile: DOMBuilder.java,v $ $Revision: 1.60 $ $Date: 2007/11/10 05:29:00 $ $Name:  $";
    private String adapterClass;
    private JDOMFactory factory = new DefaultJDOMFactory();

    public DOMBuilder() {
    }

    public DOMBuilder(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public Document build(org.w3c.dom.Document domDocument) {
        Document doc = this.factory.document(null);
        this.buildTree(domDocument, doc, null, true);
        return doc;
    }

    public Element build(org.w3c.dom.Element domElement) {
        Document doc = this.factory.document(null);
        this.buildTree(domElement, doc, null, true);
        return doc.getRootElement();
    }

    private void buildTree(Node node, Document doc, Element current, boolean atRoot) {
        switch (node.getNodeType()) {
            case 9: {
                NodeList nodes = node.getChildNodes();
                int size = nodes.getLength();
                for (int i = 0; i < size; ++i) {
                    this.buildTree(nodes.item(i), doc, current, true);
                }
                break;
            }
            case 1: {
                String attPrefix;
                String attname;
                Attr att;
                int i;
                String nodeName = node.getNodeName();
                String prefix = "";
                String localName = nodeName;
                int colon = nodeName.indexOf(58);
                if (colon >= 0) {
                    prefix = nodeName.substring(0, colon);
                    localName = nodeName.substring(colon + 1);
                }
                Namespace ns = null;
                String uri = node.getNamespaceURI();
                ns = uri == null ? (current == null ? Namespace.NO_NAMESPACE : current.getNamespace(prefix)) : Namespace.getNamespace(prefix, uri);
                Element element = this.factory.element(localName, ns);
                if (atRoot) {
                    doc.setRootElement(element);
                } else {
                    this.factory.addContent(current, element);
                }
                NamedNodeMap attributeList = node.getAttributes();
                int attsize = attributeList.getLength();
                for (i = 0; i < attsize; ++i) {
                    att = (Attr)attributeList.item(i);
                    attname = att.getName();
                    if (!attname.startsWith("xmlns")) continue;
                    attPrefix = "";
                    colon = attname.indexOf(58);
                    if (colon >= 0) {
                        attPrefix = attname.substring(colon + 1);
                    }
                    String attvalue = att.getValue();
                    Namespace declaredNS = Namespace.getNamespace(attPrefix, attvalue);
                    if (prefix.equals(attPrefix)) {
                        element.setNamespace(declaredNS);
                        continue;
                    }
                    this.factory.addNamespaceDeclaration(element, declaredNS);
                }
                for (i = 0; i < attsize; ++i) {
                    att = (Attr)attributeList.item(i);
                    attname = att.getName();
                    if (attname.startsWith("xmlns")) continue;
                    attPrefix = "";
                    String attLocalName = attname;
                    colon = attname.indexOf(58);
                    if (colon >= 0) {
                        attPrefix = attname.substring(0, colon);
                        attLocalName = attname.substring(colon + 1);
                    }
                    String attvalue = att.getValue();
                    Namespace attNS = null;
                    String attURI = att.getNamespaceURI();
                    if (attURI == null || "".equals(attURI)) {
                        attNS = Namespace.NO_NAMESPACE;
                    } else if (attPrefix.length() > 0) {
                        attNS = Namespace.getNamespace(attPrefix, attURI);
                    } else {
                        HashSet<String> overrides = new HashSet<String>();
                        Element p = element;
                        block14: do {
                            if (p.getNamespace().getURI().equals(attURI) && !overrides.contains(p.getNamespacePrefix()) && !"".equals(element.getNamespace().getPrefix())) {
                                attNS = p.getNamespace();
                                break;
                            }
                            overrides.add(p.getNamespacePrefix());
                            for (Namespace tns : p.getAdditionalNamespaces()) {
                                if (!overrides.contains(tns.getPrefix()) && attURI.equals(tns.getURI())) {
                                    attNS = tns;
                                    break block14;
                                }
                                overrides.add(tns.getPrefix());
                            }
                        } while ((p = p.getParentElement()) != null);
                        if (attNS == null) {
                            int cnt = 0;
                            String base = "attns";
                            String pfx = base + cnt;
                            while (overrides.contains(pfx)) {
                                pfx = base + ++cnt;
                            }
                            attNS = Namespace.getNamespace(pfx, attURI);
                        }
                    }
                    Attribute attribute = this.factory.attribute(attLocalName, attvalue, attNS);
                    this.factory.setAttribute(element, attribute);
                }
                NodeList children = node.getChildNodes();
                if (children == null) break;
                int size = children.getLength();
                for (int i2 = 0; i2 < size; ++i2) {
                    Node item = children.item(i2);
                    if (item == null) continue;
                    this.buildTree(item, doc, element, false);
                }
                break;
            }
            case 3: {
                String data = node.getNodeValue();
                this.factory.addContent(current, this.factory.text(data));
                break;
            }
            case 4: {
                String cdata = node.getNodeValue();
                this.factory.addContent(current, this.factory.cdata(cdata));
                break;
            }
            case 7: {
                if (atRoot) {
                    this.factory.addContent(doc, this.factory.processingInstruction(node.getNodeName(), node.getNodeValue()));
                    break;
                }
                this.factory.addContent(current, this.factory.processingInstruction(node.getNodeName(), node.getNodeValue()));
                break;
            }
            case 8: {
                if (atRoot) {
                    this.factory.addContent(doc, this.factory.comment(node.getNodeValue()));
                    break;
                }
                this.factory.addContent(current, this.factory.comment(node.getNodeValue()));
                break;
            }
            case 5: {
                EntityRef entity = this.factory.entityRef(node.getNodeName());
                this.factory.addContent(current, entity);
                break;
            }
            case 6: {
                break;
            }
            case 10: {
                DocumentType domDocType = (DocumentType)node;
                String publicID = domDocType.getPublicId();
                String systemID = domDocType.getSystemId();
                String internalDTD = domDocType.getInternalSubset();
                DocType docType = this.factory.docType(domDocType.getName());
                docType.setPublicID(publicID);
                docType.setSystemID(systemID);
                docType.setInternalSubset(internalDTD);
                this.factory.addContent(doc, docType);
            }
        }
    }
}

