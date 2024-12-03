/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.ext.dom.AtAtKey;
import freemarker.ext.dom.DomStringUtil;
import freemarker.ext.dom.NodeListModel;
import freemarker.ext.dom.NodeModel;
import freemarker.ext.dom.NodeOutputter;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.util.Collections;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ElementModel
extends NodeModel
implements TemplateScalarModel {
    public ElementModel(Element element) {
        super(element);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        if (key.equals("*")) {
            NodeListModel ns = new NodeListModel(this);
            TemplateSequenceModel children = this.getChildNodes();
            int size = children.size();
            for (int i = 0; i < size; ++i) {
                NodeModel child = (NodeModel)children.get(i);
                if (child.node.getNodeType() != 1) continue;
                ns.add(child);
            }
            return ns;
        }
        if (key.equals("**")) {
            return new NodeListModel(((Element)this.node).getElementsByTagName("*"), (NodeModel)this);
        }
        if (key.startsWith("@")) {
            if (key.startsWith("@@")) {
                if (key.equals(AtAtKey.ATTRIBUTES.getKey())) {
                    return new NodeListModel(this.node.getAttributes(), (NodeModel)this);
                }
                if (key.equals(AtAtKey.START_TAG.getKey())) {
                    NodeOutputter nodeOutputter = new NodeOutputter(this.node);
                    return new SimpleScalar(nodeOutputter.getOpeningTag((Element)this.node));
                }
                if (key.equals(AtAtKey.END_TAG.getKey())) {
                    NodeOutputter nodeOutputter = new NodeOutputter(this.node);
                    return new SimpleScalar(nodeOutputter.getClosingTag((Element)this.node));
                }
                if (key.equals(AtAtKey.ATTRIBUTES_MARKUP.getKey())) {
                    StringBuilder buf = new StringBuilder();
                    NodeOutputter nu = new NodeOutputter(this.node);
                    nu.outputContent(this.node.getAttributes(), buf);
                    return new SimpleScalar(buf.toString().trim());
                }
                if (key.equals(AtAtKey.PREVIOUS_SIBLING_ELEMENT.getKey())) {
                    Node previousSibling;
                    for (previousSibling = this.node.getPreviousSibling(); previousSibling != null && !this.isSignificantNode(previousSibling); previousSibling = previousSibling.getPreviousSibling()) {
                    }
                    return previousSibling != null && previousSibling.getNodeType() == 1 ? ElementModel.wrap(previousSibling) : new NodeListModel(Collections.emptyList(), null);
                }
                if (key.equals(AtAtKey.NEXT_SIBLING_ELEMENT.getKey())) {
                    Node nextSibling;
                    for (nextSibling = this.node.getNextSibling(); nextSibling != null && !this.isSignificantNode(nextSibling); nextSibling = nextSibling.getNextSibling()) {
                    }
                    return nextSibling != null && nextSibling.getNodeType() == 1 ? ElementModel.wrap(nextSibling) : new NodeListModel(Collections.emptyList(), null);
                }
                return super.get(key);
            }
            if (DomStringUtil.isXMLNameLike(key, 1)) {
                Attr att = this.getAttribute(key.substring(1));
                if (att == null) {
                    return new NodeListModel(this);
                }
                return ElementModel.wrap(att);
            }
            if (key.equals("@*")) {
                return new NodeListModel(this.node.getAttributes(), (NodeModel)this);
            }
            return super.get(key);
        }
        if (DomStringUtil.isXMLNameLike(key)) {
            NodeListModel result = ((NodeListModel)this.getChildNodes()).filterByName(key);
            return result.size() != 1 ? result : result.get(0);
        }
        return super.get(key);
    }

    @Override
    public String getAsString() throws TemplateModelException {
        NodeList nl = this.node.getChildNodes();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            short nodeType = child.getNodeType();
            if (nodeType == 1) {
                String msg = "Only elements with no child elements can be processed as text.\nThis element with name \"" + this.node.getNodeName() + "\" has a child element named: " + child.getNodeName();
                throw new TemplateModelException(msg);
            }
            if (nodeType != 3 && nodeType != 4) continue;
            result.append(child.getNodeValue());
        }
        return result.toString();
    }

    @Override
    public String getNodeName() {
        String result = this.node.getLocalName();
        if (result == null || result.equals("")) {
            result = this.node.getNodeName();
        }
        return result;
    }

    @Override
    String getQualifiedName() {
        String nodeName = this.getNodeName();
        String nsURI = this.getNodeNamespace();
        if (nsURI == null || nsURI.length() == 0) {
            return nodeName;
        }
        Environment env = Environment.getCurrentEnvironment();
        String defaultNS = env.getDefaultNS();
        String prefix = defaultNS != null && defaultNS.equals(nsURI) ? "" : env.getPrefixForNamespace(nsURI);
        if (prefix == null) {
            return null;
        }
        if (prefix.length() > 0) {
            prefix = prefix + ":";
        }
        return prefix + nodeName;
    }

    private Attr getAttribute(String qname) {
        Element element = (Element)this.node;
        Attr result = element.getAttributeNode(qname);
        if (result != null) {
            return result;
        }
        int colonIndex = qname.indexOf(58);
        if (colonIndex > 0) {
            String prefix = qname.substring(0, colonIndex);
            String uri = prefix.equals("D") ? Environment.getCurrentEnvironment().getDefaultNS() : Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
            String localName = qname.substring(1 + colonIndex);
            if (uri != null) {
                result = element.getAttributeNodeNS(uri, localName);
            }
        }
        return result;
    }

    private boolean isSignificantNode(Node node) throws TemplateModelException {
        return node.getNodeType() == 3 || node.getNodeType() == 4 ? !this.isBlankXMLText(node.getTextContent()) : node.getNodeType() != 7 && node.getNodeType() != 8;
    }

    private boolean isBlankXMLText(String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (this.isXMLWhiteSpace(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private boolean isXMLWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' | c == '\r';
    }

    boolean matchesName(String name, Environment env) {
        return DomStringUtil.matchesName(name, this.getNodeName(), this.getNodeNamespace(), env);
    }
}

