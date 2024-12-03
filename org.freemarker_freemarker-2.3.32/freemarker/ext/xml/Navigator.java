/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jaxen.NamespaceContext
 */
package freemarker.ext.xml;

import freemarker.ext.xml.NodeOperator;
import freemarker.template.TemplateModelException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jaxen.NamespaceContext;

abstract class Navigator {
    private final Map xpathCache = new WeakHashMap();
    private final Map operators = this.createOperatorMap();
    private final NodeOperator attributeOperator = this.getOperator("_attributes");
    private final NodeOperator childrenOperator = this.getOperator("_children");

    Navigator() {
    }

    NodeOperator getOperator(String key) {
        return (NodeOperator)this.operators.get(key);
    }

    NodeOperator getAttributeOperator() {
        return this.attributeOperator;
    }

    NodeOperator getChildrenOperator() {
        return this.childrenOperator;
    }

    abstract void getAsString(Object var1, StringWriter var2) throws TemplateModelException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    List applyXPath(List nodes, String xpathString, Object namespaces) throws TemplateModelException {
        XPathEx xpath = null;
        try {
            Map map = this.xpathCache;
            synchronized (map) {
                xpath = (XPathEx)this.xpathCache.get(xpathString);
                if (xpath == null) {
                    xpath = this.createXPathEx(xpathString);
                    this.xpathCache.put(xpathString, xpath);
                }
            }
            return xpath.selectNodes(nodes, (NamespaceContext)namespaces);
        }
        catch (Exception e) {
            throw new TemplateModelException("Could not evaulate XPath expression " + xpathString, e);
        }
    }

    abstract XPathEx createXPathEx(String var1) throws TemplateModelException;

    abstract void getChildren(Object var1, String var2, String var3, List var4);

    abstract void getAttributes(Object var1, String var2, String var3, List var4);

    abstract void getDescendants(Object var1, List var2);

    abstract Object getParent(Object var1);

    abstract Object getDocument(Object var1);

    abstract Object getDocumentType(Object var1);

    private void getAncestors(Object node, List result) {
        Object parent;
        while ((parent = this.getParent(node)) != null) {
            result.add(parent);
            node = parent;
        }
    }

    abstract void getContent(Object var1, List var2);

    abstract String getText(Object var1);

    abstract String getLocalName(Object var1);

    abstract String getNamespacePrefix(Object var1);

    String getQualifiedName(Object node) {
        String lname = this.getLocalName(node);
        if (lname == null) {
            return null;
        }
        String nsprefix = this.getNamespacePrefix(node);
        if (nsprefix == null || nsprefix.length() == 0) {
            return lname;
        }
        return nsprefix + ":" + lname;
    }

    abstract String getType(Object var1);

    abstract String getNamespaceUri(Object var1);

    boolean equal(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    private Map createOperatorMap() {
        HashMap<String, NodeOperator> map = new HashMap<String, NodeOperator>();
        map.put("_attributes", new AttributesOp());
        map.put("@*", (NodeOperator)map.get("_attributes"));
        map.put("_children", new ChildrenOp());
        map.put("*", (NodeOperator)map.get("_children"));
        map.put("_descendantOrSelf", new DescendantOrSelfOp());
        map.put("_descendant", new DescendantOp());
        map.put("_document", new DocumentOp());
        map.put("_doctype", new DocumentTypeOp());
        map.put("_ancestor", new AncestorOp());
        map.put("_ancestorOrSelf", new AncestorOrSelfOp());
        map.put("_content", new ContentOp());
        map.put("_name", new LocalNameOp());
        map.put("_nsprefix", new NamespacePrefixOp());
        map.put("_nsuri", new NamespaceUriOp());
        map.put("_parent", new ParentOp());
        map.put("_qname", new QualifiedNameOp());
        map.put("_text", new TextOp());
        map.put("_type", new TypeOp());
        return map;
    }

    private class TypeOp
    implements NodeOperator {
        private TypeOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            result.add(Navigator.this.getType(node));
        }
    }

    private class NamespaceUriOp
    implements NodeOperator {
        private NamespaceUriOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            String text = Navigator.this.getNamespaceUri(node);
            if (text != null) {
                result.add(text);
            }
        }
    }

    private class NamespacePrefixOp
    implements NodeOperator {
        private NamespacePrefixOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            String text = Navigator.this.getNamespacePrefix(node);
            if (text != null) {
                result.add(text);
            }
        }
    }

    private class QualifiedNameOp
    implements NodeOperator {
        private QualifiedNameOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            String qname = Navigator.this.getQualifiedName(node);
            if (qname != null) {
                result.add(qname);
            }
        }
    }

    private class LocalNameOp
    implements NodeOperator {
        private LocalNameOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            String text = Navigator.this.getLocalName(node);
            if (text != null) {
                result.add(text);
            }
        }
    }

    private class TextOp
    implements NodeOperator {
        private TextOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            String text = Navigator.this.getText(node);
            if (text != null) {
                result.add(text);
            }
        }
    }

    private class ContentOp
    implements NodeOperator {
        private ContentOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getContent(node, result);
        }
    }

    private class DocumentTypeOp
    implements NodeOperator {
        private DocumentTypeOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            Object documentType = Navigator.this.getDocumentType(node);
            if (documentType != null) {
                result.add(documentType);
            }
        }
    }

    private class DocumentOp
    implements NodeOperator {
        private DocumentOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            Object document = Navigator.this.getDocument(node);
            if (document != null) {
                result.add(document);
            }
        }
    }

    private class ParentOp
    implements NodeOperator {
        private ParentOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            Object parent = Navigator.this.getParent(node);
            if (parent != null) {
                result.add(parent);
            }
        }
    }

    private class AncestorOp
    implements NodeOperator {
        private AncestorOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getAncestors(node, result);
        }
    }

    private class AncestorOrSelfOp
    implements NodeOperator {
        private AncestorOrSelfOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            result.add(node);
            Navigator.this.getAncestors(node, result);
        }
    }

    private class DescendantOp
    implements NodeOperator {
        private DescendantOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getDescendants(node, result);
        }
    }

    private class DescendantOrSelfOp
    implements NodeOperator {
        private DescendantOrSelfOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            result.add(node);
            Navigator.this.getDescendants(node, result);
        }
    }

    private class AttributesOp
    implements NodeOperator {
        private AttributesOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getAttributes(node, localName, namespaceUri, result);
        }
    }

    private class ChildrenOp
    implements NodeOperator {
        private ChildrenOp() {
        }

        @Override
        public void process(Object node, String localName, String namespaceUri, List result) {
            Navigator.this.getChildren(node, localName, namespaceUri, result);
        }
    }

    static interface XPathEx {
        public List selectNodes(Object var1, NamespaceContext var2) throws TemplateModelException;
    }
}

