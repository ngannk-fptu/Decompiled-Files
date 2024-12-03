/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Attribute
 *  org.dom4j.Branch
 *  org.dom4j.Document
 *  org.dom4j.DocumentType
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.ProcessingInstruction
 *  org.dom4j.tree.DefaultAttribute
 *  org.jaxen.Context
 *  org.jaxen.NamespaceContext
 *  org.jaxen.dom4j.Dom4jXPath
 */
package freemarker.ext.xml;

import freemarker.ext.xml.Navigator;
import freemarker.template.TemplateModelException;
import java.io.StringWriter;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.tree.DefaultAttribute;
import org.jaxen.Context;
import org.jaxen.NamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

public class _Dom4jNavigator
extends Navigator {
    @Override
    void getAsString(Object node, StringWriter sw) {
        sw.getBuffer().append(((Node)node).asXML());
    }

    @Override
    void getChildren(Object node, String localName, String namespaceUri, List result) {
        if (node instanceof Element) {
            Element e = (Element)node;
            if (localName == null) {
                result.addAll(e.elements());
            } else {
                result.addAll(e.elements(e.getQName().getDocumentFactory().createQName(localName, "", namespaceUri)));
            }
        } else if (node instanceof Document) {
            Element root = ((Document)node).getRootElement();
            if (localName == null || this.equal(root.getName(), localName) && this.equal(root.getNamespaceURI(), namespaceUri)) {
                result.add(root);
            }
        }
    }

    @Override
    void getAttributes(Object node, String localName, String namespaceUri, List result) {
        if (node instanceof Element) {
            Element e = (Element)node;
            if (localName == null) {
                result.addAll(e.attributes());
            } else {
                Attribute attr = e.attribute(e.getQName().getDocumentFactory().createQName(localName, "", namespaceUri));
                if (attr != null) {
                    result.add(attr);
                }
            }
        } else if (node instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction)node;
            if ("target".equals(localName)) {
                result.add(new DefaultAttribute("target", pi.getTarget()));
            } else if ("data".equals(localName)) {
                result.add(new DefaultAttribute("data", pi.getText()));
            } else {
                result.add(new DefaultAttribute(localName, pi.getValue(localName)));
            }
        } else if (node instanceof DocumentType) {
            DocumentType doctype = (DocumentType)node;
            if ("publicId".equals(localName)) {
                result.add(new DefaultAttribute("publicId", doctype.getPublicID()));
            } else if ("systemId".equals(localName)) {
                result.add(new DefaultAttribute("systemId", doctype.getSystemID()));
            } else if ("elementName".equals(localName)) {
                result.add(new DefaultAttribute("elementName", doctype.getElementName()));
            }
        }
    }

    @Override
    void getDescendants(Object node, List result) {
        if (node instanceof Branch) {
            this.getDescendants((Branch)node, result);
        }
    }

    private void getDescendants(Branch node, List result) {
        List content = node.content();
        for (Node subnode : content) {
            if (!(subnode instanceof Element)) continue;
            result.add(subnode);
            this.getDescendants(subnode, result);
        }
    }

    @Override
    Object getParent(Object node) {
        return ((Node)node).getParent();
    }

    @Override
    Object getDocument(Object node) {
        return ((Node)node).getDocument();
    }

    @Override
    Object getDocumentType(Object node) {
        return node instanceof Document ? ((Document)node).getDocType() : null;
    }

    @Override
    void getContent(Object node, List result) {
        if (node instanceof Branch) {
            result.addAll(((Branch)node).content());
        }
    }

    @Override
    String getText(Object node) {
        return ((Node)node).getText();
    }

    @Override
    String getLocalName(Object node) {
        return ((Node)node).getName();
    }

    @Override
    String getNamespacePrefix(Object node) {
        if (node instanceof Element) {
            return ((Element)node).getNamespacePrefix();
        }
        if (node instanceof Attribute) {
            return ((Attribute)node).getNamespacePrefix();
        }
        return null;
    }

    @Override
    String getNamespaceUri(Object node) {
        if (node instanceof Element) {
            return ((Element)node).getNamespaceURI();
        }
        if (node instanceof Attribute) {
            return ((Attribute)node).getNamespaceURI();
        }
        return null;
    }

    @Override
    String getType(Object node) {
        switch (((Node)node).getNodeType()) {
            case 2: {
                return "attribute";
            }
            case 4: {
                return "cdata";
            }
            case 8: {
                return "comment";
            }
            case 9: {
                return "document";
            }
            case 10: {
                return "documentType";
            }
            case 1: {
                return "element";
            }
            case 5: {
                return "entityReference";
            }
            case 13: {
                return "namespace";
            }
            case 7: {
                return "processingInstruction";
            }
            case 3: {
                return "text";
            }
        }
        return "unknown";
    }

    @Override
    Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
        try {
            return new Dom4jXPathEx(xpathString);
        }
        catch (Exception e) {
            throw new TemplateModelException(e);
        }
    }

    private static final class Dom4jXPathEx
    extends Dom4jXPath
    implements Navigator.XPathEx {
        Dom4jXPathEx(String path) throws Exception {
            super(path);
        }

        @Override
        public List selectNodes(Object object, NamespaceContext namespaces) throws TemplateModelException {
            Context context = this.getContext(object);
            context.getContextSupport().setNamespaceContext(namespaces);
            try {
                return this.selectNodesForContext(context);
            }
            catch (Exception e) {
                throw new TemplateModelException(e);
            }
        }
    }
}

