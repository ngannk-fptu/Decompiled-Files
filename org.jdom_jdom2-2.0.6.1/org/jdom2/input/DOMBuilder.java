/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input;

import java.util.HashMap;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMFactory;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMBuilder {
    private JDOMFactory factory = new DefaultJDOMFactory();

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

    public CDATA build(CDATASection cdata) {
        return this.factory.cdata(cdata.getNodeValue());
    }

    public Text build(org.w3c.dom.Text text) {
        return this.factory.text(text.getNodeValue());
    }

    public Comment build(org.w3c.dom.Comment comment) {
        return this.factory.comment(comment.getNodeValue());
    }

    public ProcessingInstruction build(org.w3c.dom.ProcessingInstruction pi) {
        return this.factory.processingInstruction(pi.getTarget(), pi.getData());
    }

    public EntityRef build(EntityReference er) {
        return this.factory.entityRef(er.getNodeName());
    }

    public DocType build(DocumentType doctype) {
        String publicID = doctype.getPublicId();
        String systemID = doctype.getSystemId();
        String internalDTD = doctype.getInternalSubset();
        DocType docType = this.factory.docType(doctype.getName());
        docType.setPublicID(publicID);
        docType.setSystemID(systemID);
        docType.setInternalSubset(internalDTD);
        return docType;
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
                    this.factory.setRoot(doc, element);
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
                    if (attPrefix.isEmpty() && (attURI == null || "".equals(attURI))) {
                        attNS = Namespace.NO_NAMESPACE;
                    } else if (attPrefix.length() > 0) {
                        attNS = attURI == null ? element.getNamespace(attPrefix) : Namespace.getNamespace(attPrefix, attURI);
                    } else {
                        HashMap<String, Namespace> tmpmap = new HashMap<String, Namespace>();
                        for (Namespace nss : element.getNamespacesInScope()) {
                            if (nss.getPrefix().length() > 0 && nss.getURI().equals(attURI)) {
                                attNS = nss;
                                break;
                            }
                            tmpmap.put(nss.getPrefix(), nss);
                        }
                        if (attNS == null) {
                            int cnt = 0;
                            String base = "attns";
                            String pfx = base + cnt;
                            while (tmpmap.containsKey(pfx)) {
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
                this.factory.addContent(current, this.build((org.w3c.dom.Text)node));
                break;
            }
            case 4: {
                this.factory.addContent(current, this.build((CDATASection)node));
                break;
            }
            case 7: {
                if (atRoot) {
                    this.factory.addContent(doc, this.build((org.w3c.dom.ProcessingInstruction)node));
                    break;
                }
                this.factory.addContent(current, this.build((org.w3c.dom.ProcessingInstruction)node));
                break;
            }
            case 8: {
                if (atRoot) {
                    this.factory.addContent(doc, this.build((org.w3c.dom.Comment)node));
                    break;
                }
                this.factory.addContent(current, this.build((org.w3c.dom.Comment)node));
                break;
            }
            case 5: {
                this.factory.addContent(current, this.build((EntityReference)node));
                break;
            }
            case 6: {
                break;
            }
            case 10: {
                this.factory.addContent(doc, this.build((DocumentType)node));
            }
        }
    }
}

