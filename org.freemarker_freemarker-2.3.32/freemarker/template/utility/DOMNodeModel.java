/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

@Deprecated
public class DOMNodeModel
implements TemplateHashModel {
    private static HashMap equivalenceTable = new HashMap();
    private Node node;
    private HashMap cache = new HashMap();

    public DOMNodeModel(Node node) {
        this.node = node;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        TemplateModel result = null;
        if (equivalenceTable.containsKey(key)) {
            key = (String)equivalenceTable.get(key);
        }
        if (this.cache.containsKey(key)) {
            result = (TemplateModel)this.cache.get(key);
        }
        if (result != null) return result;
        if ("attributes".equals(key)) {
            NamedNodeMap attributes = this.node.getAttributes();
            if (attributes != null) {
                SimpleHash hash = new SimpleHash(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                for (int i = 0; i < attributes.getLength(); ++i) {
                    Attr att = (Attr)attributes.item(i);
                    hash.put(att.getName(), att.getValue());
                }
                result = hash;
            }
        } else if (key.charAt(0) == '@') {
            if (!(this.node instanceof Element)) throw new TemplateModelException("Trying to get an attribute value for a non-element node");
            String attValue = ((Element)this.node).getAttribute(key.substring(1));
            result = new SimpleScalar(attValue);
        } else if ("is_element".equals(key)) {
            result = this.node instanceof Element ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        } else if ("is_text".equals(key)) {
            result = this.node instanceof Text ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        } else if ("name".equals(key)) {
            result = new SimpleScalar(this.node.getNodeName());
        } else if ("children".equals(key)) {
            result = new NodeListTM(this.node.getChildNodes());
        } else if ("parent".equals(key)) {
            Node parent = this.node.getParentNode();
            result = parent == null ? null : new DOMNodeModel(parent);
        } else if ("ancestorByName".equals(key)) {
            result = new AncestorByName();
        } else if ("nextSibling".equals(key)) {
            Node next = this.node.getNextSibling();
            result = next == null ? null : new DOMNodeModel(next);
        } else if ("previousSibling".equals(key)) {
            Node previous = this.node.getPreviousSibling();
            result = previous == null ? null : new DOMNodeModel(previous);
        } else if ("nextSiblingElement".equals(key)) {
            Element next = DOMNodeModel.nextSiblingElement(this.node);
            result = next == null ? null : new DOMNodeModel(next);
        } else if ("previousSiblingElement".equals(key)) {
            Element previous = DOMNodeModel.previousSiblingElement(this.node);
            result = previous == null ? null : new DOMNodeModel(previous);
        } else if ("nextElement".equals(key)) {
            Element next = DOMNodeModel.nextElement(this.node);
            result = next == null ? null : new DOMNodeModel(next);
        } else if ("previousElement".equals(key)) {
            Element previous = DOMNodeModel.previousElement(this.node);
            result = previous == null ? null : new DOMNodeModel(previous);
        } else if ("text".equals(key)) {
            result = new SimpleScalar(DOMNodeModel.getText(this.node));
        }
        this.cache.put(key, result);
        return result;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private static String getText(Node node) {
        String result = "";
        if (node instanceof Text) {
            result = ((Text)node).getData();
        } else if (node instanceof Element) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                result = result + DOMNodeModel.getText(children.item(i));
            }
        }
        return result;
    }

    private static Element nextSiblingElement(Node node) {
        Node next = node;
        while (next != null) {
            if (!((next = next.getNextSibling()) instanceof Element)) continue;
            return (Element)next;
        }
        return null;
    }

    private static Element previousSiblingElement(Node node) {
        Node previous = node;
        while (previous != null) {
            if (!((previous = previous.getPreviousSibling()) instanceof Element)) continue;
            return (Element)previous;
        }
        return null;
    }

    private static Element nextElement(Node node) {
        Element nextSiblingElement;
        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (!(child instanceof Element)) continue;
                return (Element)child;
            }
        }
        if ((nextSiblingElement = DOMNodeModel.nextSiblingElement(node)) != null) {
            return nextSiblingElement;
        }
        Node parent = node.getParentNode();
        while (parent instanceof Element) {
            Element next = DOMNodeModel.nextSiblingElement(parent);
            if (next != null) {
                return next;
            }
            parent = parent.getParentNode();
        }
        return null;
    }

    private static Element previousElement(Node node) {
        Element result = DOMNodeModel.previousSiblingElement(node);
        if (result != null) {
            return result;
        }
        Node parent = node.getParentNode();
        if (parent instanceof Element) {
            return (Element)parent;
        }
        return null;
    }

    void setParent(DOMNodeModel parent) {
        if (parent != null) {
            this.cache.put("parent", parent);
        }
    }

    String getNodeName() {
        return this.node.getNodeName();
    }

    static {
        equivalenceTable.put("*", "children");
        equivalenceTable.put("@*", "attributes");
    }

    class NodeListTM
    implements TemplateSequenceModel,
    TemplateMethodModel {
        private NodeList nodeList;
        private TemplateModel[] nodes;

        NodeListTM(NodeList nodeList) {
            this.nodeList = nodeList;
            this.nodes = new TemplateModel[nodeList.getLength()];
        }

        @Override
        public TemplateModel get(int index) {
            DOMNodeModel result = (DOMNodeModel)this.nodes[index];
            if (result == null) {
                result = new DOMNodeModel(this.nodeList.item(index));
                this.nodes[index] = result;
                result.setParent(DOMNodeModel.this);
            }
            return result;
        }

        @Override
        public int size() {
            return this.nodes.length;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() != 1) {
                throw new TemplateModelException("Expecting exactly one string argument here");
            }
            if (!(DOMNodeModel.this.node instanceof Element)) {
                throw new TemplateModelException("Expecting element here.");
            }
            Element elem = (Element)DOMNodeModel.this.node;
            return new NodeListTM(elem.getElementsByTagName((String)arguments.get(0)));
        }
    }

    class AncestorByName
    implements TemplateMethodModel {
        AncestorByName() {
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() != 1) {
                throw new TemplateModelException("Expecting exactly one string argument here");
            }
            String nodeName = (String)arguments.get(0);
            for (DOMNodeModel ancestor = (DOMNodeModel)DOMNodeModel.this.get("parent"); ancestor != null; ancestor = (DOMNodeModel)ancestor.get("parent")) {
                if (!nodeName.equals(ancestor.getNodeName())) continue;
                return ancestor;
            }
            return null;
        }
    }
}

