/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jaxen.Context
 *  org.jaxen.NamespaceContext
 *  org.jaxen.jdom.JDOMXPath
 *  org.jdom.Attribute
 *  org.jdom.CDATA
 *  org.jdom.Comment
 *  org.jdom.DocType
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.EntityRef
 *  org.jdom.Namespace
 *  org.jdom.ProcessingInstruction
 *  org.jdom.Text
 *  org.jdom.output.XMLOutputter
 */
package freemarker.ext.xml;

import freemarker.ext.xml.Navigator;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.NamespaceContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.output.XMLOutputter;

public class _JdomNavigator
extends Navigator {
    private static final XMLOutputter OUTPUT = new XMLOutputter();

    @Override
    void getAsString(Object node, StringWriter sw) throws TemplateModelException {
        block11: {
            try {
                if (node instanceof Element) {
                    OUTPUT.output((Element)node, (Writer)sw);
                    break block11;
                }
                if (node instanceof Attribute) {
                    Attribute attribute = (Attribute)node;
                    sw.write(" ");
                    sw.write(attribute.getQualifiedName());
                    sw.write("=\"");
                    sw.write(OUTPUT.escapeAttributeEntities(attribute.getValue()));
                    sw.write("\"");
                    break block11;
                }
                if (node instanceof Text) {
                    OUTPUT.output((Text)node, (Writer)sw);
                    break block11;
                }
                if (node instanceof Document) {
                    OUTPUT.output((Document)node, (Writer)sw);
                    break block11;
                }
                if (node instanceof ProcessingInstruction) {
                    OUTPUT.output((ProcessingInstruction)node, (Writer)sw);
                    break block11;
                }
                if (node instanceof Comment) {
                    OUTPUT.output((Comment)node, (Writer)sw);
                    break block11;
                }
                if (node instanceof CDATA) {
                    OUTPUT.output((CDATA)node, (Writer)sw);
                    break block11;
                }
                if (node instanceof DocType) {
                    OUTPUT.output((DocType)node, (Writer)sw);
                    break block11;
                }
                if (node instanceof EntityRef) {
                    OUTPUT.output((EntityRef)node, (Writer)sw);
                    break block11;
                }
                throw new TemplateModelException(node.getClass().getName() + " is not a core JDOM class");
            }
            catch (IOException e) {
                throw new TemplateModelException(e);
            }
        }
    }

    @Override
    void getChildren(Object node, String localName, String namespaceUri, List result) {
        if (node instanceof Element) {
            Element e = (Element)node;
            if (localName == null) {
                result.addAll(e.getChildren());
            } else {
                result.addAll(e.getChildren(localName, Namespace.getNamespace((String)"", (String)namespaceUri)));
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
                result.addAll(e.getAttributes());
            } else {
                Attribute attr = e.getAttribute(localName, Namespace.getNamespace((String)"", (String)namespaceUri));
                if (attr != null) {
                    result.add(attr);
                }
            }
        } else if (node instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction)node;
            if ("target".equals(localName)) {
                result.add(new Attribute("target", pi.getTarget()));
            } else if ("data".equals(localName)) {
                result.add(new Attribute("data", pi.getData()));
            } else {
                result.add(new Attribute(localName, pi.getValue(localName)));
            }
        } else if (node instanceof DocType) {
            DocType doctype = (DocType)node;
            if ("publicId".equals(localName)) {
                result.add(new Attribute("publicId", doctype.getPublicID()));
            } else if ("systemId".equals(localName)) {
                result.add(new Attribute("systemId", doctype.getSystemID()));
            } else if ("elementName".equals(localName)) {
                result.add(new Attribute("elementName", doctype.getElementName()));
            }
        }
    }

    @Override
    void getDescendants(Object node, List result) {
        if (node instanceof Document) {
            Element root = ((Document)node).getRootElement();
            result.add(root);
            this.getDescendants(root, result);
        } else if (node instanceof Element) {
            this.getDescendants((Element)node, result);
        }
    }

    private void getDescendants(Element node, List result) {
        for (Element subnode : node.getChildren()) {
            result.add(subnode);
            this.getDescendants(subnode, result);
        }
    }

    @Override
    Object getParent(Object node) {
        if (node instanceof Element) {
            return ((Element)node).getParent();
        }
        if (node instanceof Attribute) {
            return ((Attribute)node).getParent();
        }
        if (node instanceof Text) {
            return ((Text)node).getParent();
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction)node).getParent();
        }
        if (node instanceof Comment) {
            return ((Comment)node).getParent();
        }
        if (node instanceof EntityRef) {
            return ((EntityRef)node).getParent();
        }
        return null;
    }

    @Override
    Object getDocument(Object node) {
        if (node instanceof Element) {
            return ((Element)node).getDocument();
        }
        if (node instanceof Attribute) {
            Element parent = ((Attribute)node).getParent();
            return parent == null ? null : parent.getDocument();
        }
        if (node instanceof Text) {
            Element parent = ((Text)node).getParent();
            return parent == null ? null : parent.getDocument();
        }
        if (node instanceof Document) {
            return node;
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction)node).getDocument();
        }
        if (node instanceof EntityRef) {
            return ((EntityRef)node).getDocument();
        }
        if (node instanceof Comment) {
            return ((Comment)node).getDocument();
        }
        return null;
    }

    @Override
    Object getDocumentType(Object node) {
        return node instanceof Document ? ((Document)node).getDocType() : null;
    }

    @Override
    void getContent(Object node, List result) {
        if (node instanceof Element) {
            result.addAll(((Element)node).getContent());
        } else if (node instanceof Document) {
            result.addAll(((Document)node).getContent());
        }
    }

    @Override
    String getText(Object node) {
        if (node instanceof Element) {
            return ((Element)node).getTextTrim();
        }
        if (node instanceof Attribute) {
            return ((Attribute)node).getValue();
        }
        if (node instanceof CDATA) {
            return ((CDATA)node).getText();
        }
        if (node instanceof Comment) {
            return ((Comment)node).getText();
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction)node).getData();
        }
        return null;
    }

    @Override
    String getLocalName(Object node) {
        if (node instanceof Element) {
            return ((Element)node).getName();
        }
        if (node instanceof Attribute) {
            return ((Attribute)node).getName();
        }
        if (node instanceof EntityRef) {
            return ((EntityRef)node).getName();
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction)node).getTarget();
        }
        if (node instanceof DocType) {
            return ((DocType)node).getElementName();
        }
        return null;
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
        if (node instanceof Attribute) {
            return "attribute";
        }
        if (node instanceof CDATA) {
            return "cdata";
        }
        if (node instanceof Comment) {
            return "comment";
        }
        if (node instanceof Document) {
            return "document";
        }
        if (node instanceof DocType) {
            return "documentType";
        }
        if (node instanceof Element) {
            return "element";
        }
        if (node instanceof EntityRef) {
            return "entityReference";
        }
        if (node instanceof Namespace) {
            return "namespace";
        }
        if (node instanceof ProcessingInstruction) {
            return "processingInstruction";
        }
        if (node instanceof Text) {
            return "text";
        }
        return "unknown";
    }

    @Override
    Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
        try {
            return new JDOMXPathEx(xpathString);
        }
        catch (Exception e) {
            throw new TemplateModelException(e);
        }
    }

    private static final class JDOMXPathEx
    extends JDOMXPath
    implements Navigator.XPathEx {
        JDOMXPathEx(String path) throws Exception {
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

